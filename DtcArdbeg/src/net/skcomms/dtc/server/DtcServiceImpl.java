/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.skcomms.dtc.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcLog;
import net.skcomms.dtc.server.model.DtcRequestProperty;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameterModel;
import net.skcomms.dtc.shared.DtcServiceVerifier;
import net.skcomms.dtc.shared.IpInfoModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcServiceImpl extends RemoteServiceServlet implements DtcService {

  public static class DtcNodeFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
      return file.isDirectory() || (file.isFile() && file.getName().endsWith(".ini"));
    }

  }

  private static final String DTC_URL = "http://10.141.6.198/";

  static final Comparator<File> NODE_COMPARATOR = new Comparator<File>() {

    @Override
    public int compare(File arg0, File arg1) {
      if (arg0.isDirectory() != arg1.isDirectory()) {
        if (arg0.isDirectory()) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return arg0.getName().compareTo(arg1.getName());
      }
    }

  };

  public static String combineQueryString(Map<String, String> params, String encoding) {
    System.out.println("charset:" + encoding);
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      sb.append(entry.getKey());
      sb.append("=");

      if (entry.getValue() != null) {
        try {
          sb.append(URLEncoder.encode(entry.getValue(), encoding));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      else {
        sb.append("");
      }
      sb.append("&");
    }
    return sb.toString().substring(0, sb.length() - 1);
  }

  // TODO 복잡도 해소 예제
  static String createDtcResponse(DtcRequest dtcRequest) throws IOException {
    InputStream is = sendHttpRequest(dtcRequest);
    String response = readHttpResponse(is, dtcRequest.getEncoding());
    
    Matcher matcher = HTTP_REF_PATTERN.matcher(response);
    boolean found = matcher.find();

    if (!found) {
      return response;
    }
    else {
      String responseUrl = URLDecoder.decode(matcher.group(0).split("/")[1], "utf-8");
      String targetUrl = getTargetUrl(responseUrl);
      DtcRequest request = createRequest(targetUrl, dtcRequest.getEncoding());
      response = DtcServiceImpl.createDtcResponse(request);
      System.out.println("Response: " + response);

      if (responseUrl.contains("response_json.html")) {
        return response;
      } else if (responseUrl.contains("response_xml.html")) {
        return DtcServiceImpl.getHtmlFromXml(response, dtcRequest.getEncoding());
      } else {
        return null;
      }
    }

  }

  private static String readHttpResponse(InputStream is, String encoding)
      throws IOException, UnsupportedEncodingException {
    StringBuilder responseBuffer = new StringBuilder();
    byte[] content = DtcServiceImpl.readAllBytes(is);
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        content), encoding));
    String line;
    while ((line = reader.readLine()) != null) {
      responseBuffer.append(line);
    }
    reader.close();
    return responseBuffer.toString();
  }

  private static String getTargetUrl(String responseUrl) throws UnsupportedEncodingException {
    String targetUrl;
    System.out.println("Response URL: " + responseUrl);

    String url = responseUrl.split("\\?u=")[0];
    String requestUrl = responseUrl.split("\\?u=")[1].split("\\?")[0];
    String query = responseUrl.split("\\?u=")[1].split("\\?")[1];

    query = requestUrl + "?" + query;

    targetUrl = DtcServiceImpl.DTC_URL + url + "?u=" + URLEncoder.encode(query, "utf-8");
    System.out.println("Target URL: " + targetUrl);
    return targetUrl;
  }

  private static DtcRequest createRequest(String url, String encoding) {
    DtcRequest request = new DtcRequest();
    request.setEncoding(encoding);
    request.setHttpMethod("GET");
    request.setUrl(url);
    return request;
  }

  protected static InputStream sendHttpRequest(DtcRequest dtcRequest)
      throws MalformedURLException, IOException, ProtocolException {
    URL conUrl;
    int TIMEOUT = 5000;
    conUrl = new URL(dtcRequest.getUrl());
    HttpURLConnection httpCon = (HttpURLConnection) conUrl.openConnection();
    httpCon.setConnectTimeout(TIMEOUT);
    httpCon.setReadTimeout(TIMEOUT);
    httpCon.setDoInput(true);
    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    if (dtcRequest.getEncoding() != "") {
      httpCon.setRequestProperty("Content-Type", "text/html; charset="
          + dtcRequest.getEncoding());
    }

    httpCon.setRequestMethod(dtcRequest.getHttpMethod().toUpperCase());

    if (dtcRequest.getHttpMethod().toUpperCase().equals("POST")) {
      httpCon.setDoOutput(true);
      OutputStream postStream = httpCon.getOutputStream();
      postStream.write(dtcRequest.getRequestData().getBytes());
      postStream.flush();
      postStream.close();
    }
    return httpCon.getInputStream();
  }

  public static String escapeForXml(String src) {
    return src.replaceAll("\\\\u000B", "&#11;").replaceAll("\\\\f", "&#12;");
  }

  private static String getEncodedQuery(String query, String encoding) throws IOException {
    // decode UTF-8 query
    StringBuilder paramList = new StringBuilder();
    // FIXME block 내부에서만 사용하는 변수는 block 내부에 선언하자.
    String decodedQuery = null;
    String encodedQuery = null;

    if (encoding.toLowerCase().equals("utf-8")) {
      return query;
    }

    String[] params = query.split("&");

    for (String param : params) {
      String[] pair = param.split("=");
      if (pair.length == 2) {
        decodedQuery = URLDecoder.decode(pair[1], "utf-8");
        encodedQuery = URLEncoder.encode(decodedQuery, encoding);
        // System.out.println("decodedQuery: " + decodedQuery);
        // System.out.println("encodedQuery: " + encodedQuery);
        paramList.append(pair[0]);
        paramList.append("=");
        paramList.append(encodedQuery);
        paramList.append("&");
      } else {
        paramList.append(pair[0]);
        paramList.append("=");
        paramList.append("");
        paramList.append("&");
      }
    }

    return paramList.toString().substring(0, paramList.length() - 1);
  }

  /**
   * @param url
   * @return
   * @throws IOException
   */
  static byte[] getHtmlContents(String href) throws IOException {
    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());

    return contents;
  }

  public static String getHtmlFromXml(String xml, String encoding) {
    try {
      DtcXmlToHtmlHandler dp = new DtcXmlToHtmlHandler();
      ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(xml.getBytes(encoding));
      SAXParserFactory sf = SAXParserFactory.newInstance();
      SAXParser sp;
      sp = sf.newSAXParser();
      sp.parse(bufferInputStream, dp);

      return dp.getHtml().toString();
    } catch (SAXException e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid XML:" + xml);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  public static String getRootPath() throws IOException {
    if (new File("/home/search/dtc").isDirectory()) {
      return "/home/search/dtc/";
    } else if (new File("sample/dtc").isDirectory()) {
      return "sample/dtc/";
    } else {
      return "../sample/dtc/";
    }

  }

  static String guessCharacterEncoding(byte[] bytes) throws IOException {
    String string;
    if (bytes.length > 1024) {
      string = new String(bytes, 0, 1024);
    }
    else {
      string = new String(bytes);
    }
    if (string.contains("charset=utf-8") || string.contains("encoding=\"utf-8\"")) {
      return "utf-8";
    }
    return "windows-949";
  }

  static byte[] readAllBytes(InputStream is) throws IOException {
    DataInputStream dis = new DataInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream(40960);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = dis.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    return bos.toByteArray();
  }

  private EntityManagerFactory emf;

  private static final Pattern HTTP_REF_PATTERN = Pattern.compile("src=\"([^\"]*)");

  private DtcRequestMeta createRequestInfo(DtcIni ini) {
    DtcRequestMeta requestInfo = new DtcRequestMeta();

    ArrayList<DtcRequestParameterModel> params = new ArrayList<DtcRequestParameterModel>();
    int index = 0;
    for (DtcRequestProperty prop : ini.getRequestProps()) {
      index++;
      params.add(new DtcRequestParameterModel(prop.getKey(), "REQUEST" + index, prop.getValue()));
    }
    params.add(new DtcRequestParameterModel("Port", "port", ini.getBaseProp("PORT").getValue()));
    requestInfo.setParams(params);

    requestInfo.setEncoding(ini.getCharacterSet());

    IpInfoModel ipInfo = new IpInfoModel();
    for (Entry<String, String> entry : ini.getIps().entrySet()) {
      ipInfo.addOption(entry.getKey(), entry.getKey() + " - " + entry.getValue());
    }
    requestInfo.setIpInfo(ipInfo);

    return requestInfo;
  }

  @Override
  public List<DtcNodeMeta> getDir(String path) {
    if (!DtcServiceVerifier.isValidDirectoryPath(path)) {
      throw new IllegalArgumentException("Invalid directory:" + path);
    }

    EntityManager manager = this.emf.createEntityManager();
    manager.getTransaction().begin();
    manager.persist(new DtcLog("\"" + path + "\" requested."));
    manager.getTransaction().commit();
    manager.close();

    try {
      return this.getDirImpl(path);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  List<DtcNodeMeta> getDirImpl(String path) throws IOException {
    String root = DtcServiceImpl.getRootPath();
    String parentPath = root + path.substring(1);
    System.out.println("Absolute path:" + parentPath);
    File file = new File(parentPath);
    List<DtcNodeMeta> nodes = new ArrayList<DtcNodeMeta>();
    File[] files = file.listFiles(new DtcNodeFilter());
    Arrays.sort(files, DtcServiceImpl.NODE_COMPARATOR);
    for (File child : files) {
      DtcNodeMeta node = new DtcNodeMeta();
      node.setName(child.getName());
      if (child.isDirectory()) {
        node.setDescription("디렉토리");
        node.setPath(parentPath.substring(root.length() - 1) + child.getName() + "/");
      } else {
        DtcIni ini = new DtcIniFactory().createFrom(child.getPath());
        node.setDescription(ini.getBaseProp("DESCRIPTION").getValue());
        node.setPath(parentPath.substring(root.length() - 1) + child.getName());
      }
      String updateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(child
          .lastModified()));
      node.setUpdateTime(updateTime);
      nodes.add(node);
    }
    return nodes;
  }

  DtcRequestMeta getDtcRequestInfoImpl(String path) throws IOException {
    String filePath = DtcServiceImpl.getRootPath() + path.substring(1);
    DtcIni ini = new DtcIniFactory().createFrom(filePath);
    // FIXME DtcRequestInfoModel을 DtcIni로 대체하는 것을 검토하자.
    DtcRequestMeta requestInfo = this.createRequestInfo(ini);
    requestInfo.setPath(path);
    return requestInfo;
  }

  @Override
  public DtcRequestMeta getDtcRequestPageInfo(String path) {
    if (!DtcServiceVerifier.isValidTestPage(path)) {
      throw new IllegalArgumentException("Invalid test page:" + path);
    }
    try {
      return this.getDtcRequestInfoImpl(path);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  @Override
  public DtcResponse getDtcResponse(DtcRequest httpRequestInfo)
      throws IllegalArgumentException {

    String rawHtml = null;
    if (!DtcServiceVerifier.isValidMethod(httpRequestInfo.getHttpMethod())) {
      throw new IllegalArgumentException("Invalid HTTP Method: " + httpRequestInfo.getHttpMethod());
    }

    // change encoding
    try {
      String encodedData = DtcServiceImpl.getEncodedQuery(httpRequestInfo.getRequestData(),
          httpRequestInfo.getEncoding());
      httpRequestInfo.setRequestData(encodedData);

      // replace URL
      String targetUrl = DtcServiceImpl.DTC_URL + "response.html";
      httpRequestInfo.setUrl(targetUrl);
      Date startTime = new Date();
      rawHtml = DtcServiceImpl.createDtcResponse(httpRequestInfo);
      Date endTime = new Date();
      DtcResponse response = new DtcResponse();
      response.setResponseTime(endTime.getTime() - startTime.getTime());
      response.setResult(rawHtml);
      return response;
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    System.out.println("init() called.");
    super.init(config);
    WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext())
        .getAutowireCapableBeanFactory().autowireBean(this);
  }

  @Autowired
  void setEntityManagerFactory(EntityManagerFactory emf) {
    System.out.println("setEntityManagerFactory() called.");
    this.emf = emf;
  }

}
