package net.skcomms.dtc.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import net.skcomms.dtc.server.DtcXmlToHtmlHandler;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestParameter;

public class DtcHelper {

  public static String combinUrl(DtcRequest dtcRequest) throws UnsupportedEncodingException {
    StringBuilder url = new StringBuilder();

    url.append("http://");
    url.append(dtcRequest.getRequestParameter("IP"));
    url.append(":");
    url.append(dtcRequest.getRequestParameter("Port"));
    url.append("/");
    url.append(dtcRequest.getAppName());
    url.append("/");
    url.append(dtcRequest.getApiNumber());
    url.append("?");
    url.append("Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1");

    for (DtcRequestParameter param : dtcRequest.getRequestParameters()) {
      if (param.getKey().equals("IP") || param.getKey().equals("Port")) {
        continue;
      }
      System.out.println("key:" + param.getKey() + "  value:" + param.getValue());

      url.append("&");
      url.append(param.getKey());
      url.append("=");
      url.append(param.getValue() == null ? "" : param.getValue());
    }

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

}
