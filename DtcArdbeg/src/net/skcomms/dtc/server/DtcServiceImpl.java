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
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcLog;
import net.skcomms.dtc.server.model.DtcRequestProperty;
import net.skcomms.dtc.server.util.DtcHelper;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameter;
import net.skcomms.dtc.shared.DtcServiceVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcServiceImpl extends RemoteServiceServlet implements DtcService {

  public static class DtcNodeFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
      return file.isDirectory() || (file.isFile() && file.getName().endsWith(".ini"));
    }
  }

  static String createDtcResponse(DtcRequest dtcRequest) throws IOException {
    InputStream is = DtcServiceImpl.sendHttpRequest(dtcRequest);
    return DtcServiceImpl.readHttpResponse(is, dtcRequest.getCharset());
  }

  private static String readHttpResponse(InputStream is, String encoding)
      throws IOException, UnsupportedEncodingException {
    StringBuilder responseBuffer = new StringBuilder();
    byte[] content = DtcHelper.readAllBytes(is);
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        content), encoding));
    String line;
    while ((line = reader.readLine()) != null) {
      responseBuffer.append(line);
    }
    reader.close();
    return responseBuffer.toString();
  }

  protected static InputStream sendHttpRequest(DtcRequest dtcRequest)
      throws MalformedURLException, IOException, ProtocolException {
    int TIMEOUT = 5000;

    URL conUrl = new URL(DtcHelper.combineUrl(dtcRequest));
    HttpURLConnection httpCon = (HttpURLConnection) conUrl.openConnection();
    httpCon.setConnectTimeout(TIMEOUT);
    httpCon.setReadTimeout(TIMEOUT);
    httpCon.setDoInput(true);
    httpCon.setRequestProperty("Content-Type", "text/html; charset="
        + dtcRequest.getCharset());
    httpCon.connect();
    return httpCon.getInputStream();
  }

  private EntityManagerFactory emf;

  private String createAtpResponse(DtcRequest request, DtcIni ini) throws IOException {
    Socket socket = this.openSocket(request);
    try {
      DtcAtp atpRequest = DtcAtpFactory.createFrom(request, ini);
      System.out.println("ATP REQUEST:" + atpRequest);
      socket.getOutputStream().write(atpRequest.getBytes(request.getCharset()));
      socket.getOutputStream().flush();

      DtcAtp atpResponse = DtcAtpFactory.createFrom(socket.getInputStream(), request.getCharset());
      return atpResponse.toHtmlString(ini);
    } finally {
      socket.close();
    }
  }

  private DtcRequest createDtcRequestFromHttpRequest(HttpServletRequest req) throws IOException,
      FileNotFoundException {
    DtcRequest request = new DtcRequest();

    request.setPath(req.getParameter("path"));
    request.setEncoding(req.getParameter("charset"));
    request.setAppName(req.getParameter("appName"));
    request.setApiNumber(req.getParameter("apiNumber"));

    List<DtcRequestParameter> daemonParams = new ArrayList<DtcRequestParameter>();
    setUpParameters(req, daemonParams);
    daemonParams.add(new DtcRequestParameter("IP", null, req.getParameter("IP")));
    daemonParams.add(new DtcRequestParameter("Port", null, req.getParameter("Port")));
    request.setRequestParameters(daemonParams);
    return request;
  }

  private void setUpParameters(HttpServletRequest req, List<DtcRequestParameter> daemonParams)
      throws IOException, FileNotFoundException {
    DtcIni ini = this.getIni(req.getParameter("path"));
    for (DtcRequestProperty prop : ini.getRequestProps()) {
      String value = req.getParameter(prop.getKey());
      daemonParams.add(new DtcRequestParameter(prop.getKey(), null, value));
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    DtcRequest request = this.createDtcRequestFromHttpRequest(req);

    DtcResponse response = this.getDtcResponse(request);
    this.writeHtmlResponse(resp, response, request.getCharset());
  }

  private File[] getChildNodes(File file) {
    File[] files = file.listFiles(new DtcNodeFilter());
    Arrays.sort(files, DtcHelper.NODE_COMPARATOR);
    return files;
  }

  @Override
  public List<DtcNodeMeta> getDir(String path) {
    if (!DtcServiceVerifier.isValidDirectoryPath(path)) {
      throw new IllegalArgumentException("Invalid directory format:" + path);
    }

    this.logPath(path);
    try {
      return this.getDirImpl(path);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  List<DtcNodeMeta> getDirImpl(String path) throws IOException {
    String parentPath = DtcHelper.getRootPath() + path.substring(1);
    String relativePath = DtcHelper.getRelativePath(parentPath);
    File file = new File(parentPath);
    List<DtcNodeMeta> nodes = new ArrayList<DtcNodeMeta>();

    File[] files = this.getChildNodes(file);
    for (File child : files) {
      DtcNodeMeta node = DtcHelper.createDtcNodeMeta(relativePath, child);
      nodes.add(node);
    }
    return nodes;
  }

  @Override
  public DtcRequestMeta getDtcRequestMeta(String path) {
    if (!DtcServiceVerifier.isValidTestPage(path)) {
      throw new IllegalArgumentException("Invalid test page:" + path);
    }
    try {
      return this.getDtcRequestMetaImpl(path);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  DtcRequestMeta getDtcRequestMetaImpl(String path) throws IOException {
    DtcIni ini = this.getIni(path);
    // FIXME DtcRequestInfoModel을 DtcIni로 대체하는 것을 검토하자.
    DtcRequestMeta requestInfo = ini.createRequestInfo();
    requestInfo.setPath(path);

    return requestInfo;
  }

  @Override
  public DtcResponse getDtcResponse(DtcRequest request) throws IllegalArgumentException {
    try {
      return this.getDtcResponseImpl(request);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
  }

  private DtcResponse getDtcResponseImpl(DtcRequest request) throws IOException,
      FileNotFoundException {
    DtcResponse response = new DtcResponse();

    DtcIni ini = this.getIni(request.getPath());
    Date startTime = new Date();
    String result = this.getResponseAndConvertToHtml(request, ini);
    response.setResponseTime(new Date().getTime() - startTime.getTime());
    response.setResult(result);

    return response;
  }

  private DtcIni getIni(String nodePath) throws IOException, FileNotFoundException {
    String filePath = DtcHelper.getRootPath() + nodePath.substring(1);
    DtcIni ini = new DtcIniFactory().createFrom(filePath);
    return ini;
  }

  private String getResponseAndConvertToHtml(DtcRequest request, DtcIni ini) throws IOException {
    String result;
    if (ini.getProtocol().equals("ATP")) {
      result = this.createAtpResponse(request, ini);
    } else if (ini.getProtocol().equals("XML")) {
      String xml = DtcServiceImpl.createDtcResponse(request);
      result = DtcHelper.getHtmlFromXml(xml, request.getCharset());
    } else {
      result = DtcServiceImpl.createDtcResponse(request);
    }
    return result;
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    System.out.println("init() called.");
    super.init(config);
    WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext())
        .getAutowireCapableBeanFactory().autowireBean(this);
  }

  private void logPath(String path) {
    EntityManager manager = this.emf.createEntityManager();
    manager.getTransaction().begin();
    manager.persist(new DtcLog("\"" + path + "\" requested."));
    manager.getTransaction().commit();
    manager.close();
  }

  private Socket openSocket(DtcRequest request) throws UnknownHostException, IOException {
    String ip = request.getRequestParameter("IP");
    String port = request.getRequestParameter("Port");
    Socket socket = new Socket(ip, Integer.parseInt(port));
    socket.setReuseAddress(true);
    socket.setSoLinger(true, 0);
    return socket;
  }

  @Autowired
  void setEntityManagerFactory(EntityManagerFactory emf) {
    System.out.println("setEntityManagerFactory() called.");
    this.emf = emf;
  }

  private void writeHtmlResponse(HttpServletResponse resp, DtcResponse response, String charset)
      throws IOException {
    resp.setCharacterEncoding(charset);
    resp.setContentType("text/html");
    resp.getWriter().write("<!DOCTYPE html><html><head>");
    resp.getWriter().write(
        "<meta http-equiv=\"content-type\" content=\"text/html; charset=" + charset + "\">");
    resp.getWriter().write("<link type=\"text/css\" rel=\"stylesheet\" href=\"../DtcArdbeg.css\">");
    resp.getWriter().write("</head><body>");
    resp.getWriter().write(response.getResult());
    resp.getWriter().write("</body></html>");
    resp.getWriter().close();
  }

}
