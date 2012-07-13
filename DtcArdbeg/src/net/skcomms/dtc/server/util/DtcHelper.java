package net.skcomms.dtc.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skcomms.dtc.server.DtcIniFactory;
import net.skcomms.dtc.server.DtcXmlToHtmlHandler;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestParameter;

import org.xml.sax.SAXException;

public class DtcHelper {

  private static void appendOrigin(DtcRequest dtcRequest, StringBuilder url) {
    url.append("http://");
    url.append(dtcRequest.getRequestParameter("IP"));
    url.append(":");
    url.append(dtcRequest.getRequestParameter("Port"));
    url.append("/");
    url.append(dtcRequest.getAppName());
    url.append("/");
    url.append(dtcRequest.getApiNumber());
  }

  private static void appendQueryString(DtcRequest dtcRequest, StringBuilder url)
      throws UnsupportedEncodingException {
    url.append("?");
    url.append("Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1");

    for (DtcRequestParameter param : dtcRequest.getRequestParameters()) {
      if (param.getKey().equals("IP") || param.getKey().equals("Port")) {
        continue;
      }
      url.append("&");
      url.append(param.getKey());
      url.append("=");
      url.append(param.getValue() == null ? "" : URLEncoder.encode(param.getValue(),
          dtcRequest.getCharset()));
    }
  }

  public static String combineUrl(DtcRequest dtcRequest) throws UnsupportedEncodingException {
    StringBuilder url = new StringBuilder();
    DtcHelper.appendOrigin(dtcRequest, url);
    DtcHelper.appendQueryString(dtcRequest, url);
    System.out.println("url : [" + url.toString() + "]");
    return url.toString();
  }

  public static String getHtmlFromXml(String xml, String encoding) {
    try {
      DtcXmlToHtmlHandler dp = new DtcXmlToHtmlHandler();
      ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(xml.getBytes(encoding));
      SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
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

  public static String getRelativePath(String path) throws IOException {
    return path.substring(DtcHelper.getRootPath().length() - 1);
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

  public static byte[] readAllBytes(InputStream is) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(40960);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = is.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    return bos.toByteArray();
  }

  public static final Comparator<File> NODE_COMPARATOR = new Comparator<File>() {
  
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

}
