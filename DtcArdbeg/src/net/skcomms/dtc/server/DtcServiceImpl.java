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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcLog;
import net.skcomms.dtc.server.util.DtcHelper;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;
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

  /**
   * @param dirPath
   *          파일이 존재하는 디렉토리 경로.
   * @param node
   * @return
   * @throws IOException
   */
  public static DtcNodeMeta createDtcNodeMeta(String dirPath, File node) throws IOException {
    DtcNodeMeta nodeMeta = new DtcNodeMeta();
    nodeMeta.setName(node.getName());
    if (node.isDirectory()) {
      nodeMeta.setDescription("디렉토리");
      nodeMeta.setPath(dirPath + node.getName() + "/");
    } else {
      DtcIni ini = new DtcIniFactory().createFrom(node.getPath());
      nodeMeta.setDescription(ini.getBaseProp("DESCRIPTION").getValue());
      nodeMeta.setPath(dirPath + node.getName());
    }
    String updateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(node
        .lastModified()));
    nodeMeta.setUpdateTime(updateTime);
    return nodeMeta;
  }

  // TODO 복잡도 해소 예제
  static String createDtcResponse(DtcRequest dtcRequest) throws IOException {
    InputStream is = DtcServiceImpl.sendHttpRequest(dtcRequest);
    String response = DtcServiceImpl.readHttpResponse(is, dtcRequest.getCharset());

    return response;
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

    URL conUrl = new URL(DtcHelper.combinUrl(dtcRequest));
    HttpURLConnection httpCon = (HttpURLConnection) conUrl.openConnection();
    httpCon.setConnectTimeout(TIMEOUT);
    httpCon.setReadTimeout(TIMEOUT);
    httpCon.setDoInput(true);
    httpCon.setRequestProperty("Content-Type", "text/html; charset="
        + dtcRequest.getCharset());

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

  private File[] getChildNodes(File file) {
    File[] files = file.listFiles(new DtcNodeFilter());
    Arrays.sort(files, DtcServiceImpl.NODE_COMPARATOR);
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
      DtcNodeMeta node = DtcServiceImpl.createDtcNodeMeta(relativePath, child);
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
    String filePath = DtcHelper.getRootPath() + path.substring(1);
    DtcIni ini = new DtcIniFactory().createFrom(filePath);
    // FIXME DtcRequestInfoModel을 DtcIni로 대체하는 것을 검토하자.
    DtcRequestMeta requestInfo = ini.createRequestInfo();
    requestInfo.setPath(path);

    return requestInfo;
  }

  @Override
  public DtcResponse getDtcResponse(DtcRequest request)
      throws IllegalArgumentException {

    String result = null;
    // change encoding
    try {
      String filePath = DtcHelper.getRootPath() + request.getPath().substring(1);
      DtcIni ini = new DtcIniFactory().createFrom(filePath);

      // replace URL
      Date startTime = new Date();
      System.out.println("INI Protocol:" + ini.getProtocol());
      if (ini.getProtocol().equals("ATP")) {
        result = this.createAtpResponse(request, ini);
        System.out.println("ATP HTML:" + result);
      } else if (ini.getProtocol().equals("XML")) {
        String xml = DtcServiceImpl.createDtcResponse(request);
        result = DtcHelper.getHtmlFromXml(xml, request.getCharset());
      } else {
        result = DtcServiceImpl.createDtcResponse(request);
      }

      Date endTime = new Date();
      DtcResponse response = new DtcResponse();
      response.setResponseTime(endTime.getTime() - startTime.getTime());
      response.setResult(result);

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
    return new Socket(ip, Integer.parseInt(port));
  }

  @Autowired
  void setEntityManagerFactory(EntityManagerFactory emf) {
    System.out.println("setEntityManagerFactory() called.");
    this.emf = emf;
  }

}
