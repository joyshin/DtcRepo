package net.skcomms.dtc.server;

import java.io.IOException;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class DtcProxyTest {

  class TestHandler implements ContentHandler {

    private final Stack<String> stack = new Stack<String>();
    private StringBuilder xmlSb;
    private String encoding;

    @Override
    public boolean endArray() throws ParseException, IOException {
      // System.out.println("]");
      return true;
    }

    @Override
    public void endJSON() throws ParseException, IOException {
      xmlSb.append("</Results>");
      System.out.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
      System.out.println(xmlSb.toString());
    }

    @Override
    public boolean endObject() throws ParseException, IOException {
      if (isCurrentPath("ResultSet/Results/ResultList/Document/")) {
        String documentKey = stack.pop();
        xmlSb.append(getIndentString() + "</" + documentKey + ">");
      }
      return true;
    }

    @Override
    public boolean endObjectEntry() throws ParseException, IOException {
      String key = stack.pop();
      if (!(isCurrentPath("") && key.equals("Encoding"))) {
        xmlSb.append(getIndentString() + "</" + key + ">");
      }
      return true;
    }

    private String getCDataString(String value) {
      return "<![CDATA[" + value + "]]>";
    }

    private String getIndentString() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i <= stack.size(); i++) {
        // sb.append("  ");
        sb.append("");
      }
      return sb.toString();
    }

    private boolean isCurrentPath(String path) {
      StringBuilder sb = new StringBuilder();

      for (String item : stack.toArray(new String[0])) {
        sb.append(item + "/");
      }

      return path.equals(sb.toString());
    }

    @Override
    public boolean primitive(Object value) throws ParseException, IOException {
      if (isCurrentPath("ResultSet/ResponseInfo/Code/")
          || isCurrentPath("ResultSet/ResponseInfo/Message/")) {
        xmlSb.append(getIndentString() + value);
      }
      else if (isCurrentPath("Encoding/")) {
        encoding = value.toString();
      }
      else {
        xmlSb.append(getIndentString() + getCDataString(value.toString()));
      }
      return true;
    }

    @Override
    public boolean startArray() throws ParseException, IOException {
      // System.out.print(getIndentString() + "[");
      return true;
    }

    @Override
    public void startJSON() throws ParseException, IOException {
      xmlSb = new StringBuilder();
      xmlSb.append("<Results version=\"1.0\">");
    }

    @Override
    public boolean startObject() throws ParseException, IOException {
      if (isCurrentPath("ResultSet/Results/ResultList/")) {
        xmlSb.append(getIndentString() + "<Document>");
        stack.push("Document");
      }
      return true;
    }

    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
      if (!(isCurrentPath("") && key.equals("Encoding"))) {
        xmlSb.append(getIndentString() + "<" + key + ">");
      }
      stack.push(key);
      return true;
    }

  }

  TestHandler test = new TestHandler();
  JSONParser parser = new JSONParser();

  @Test
  public void test() throws ParseException, IOException {
    byte[] htmlContents = DtcServiceImpl
        .getHtmlContents("http://10.141.1.138:7002/KBOOK2SD/100?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1&Version=102&ClientCode=EBB&ClientURI=&Query=%C7%E2%BC%F6&SearchDomain=bk_information&GroupStartPos=1&GroupCount=1&ResultStartPos=1&ResultCount=2&MaxSummaryLength=640&ID=");
    // System.out.println(new String(htmlContents));
    parser.parse(new String(htmlContents), test);
  }
}
