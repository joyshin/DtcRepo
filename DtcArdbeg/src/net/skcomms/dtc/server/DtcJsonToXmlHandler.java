package net.skcomms.dtc.server;

import java.io.IOException;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

public class DtcJsonToXmlHandler implements ContentHandler {

  private final Stack<String> stack = new Stack<String>();
  private StringBuilder xmlSb;
  private String encoding;

  public DtcJsonToXmlHandler() {
  }

  @Override
  public boolean endArray() throws ParseException, IOException {
    // System.out.println("]");
    return true;
  }

  @Override
  public void endJSON() throws ParseException, IOException {
    xmlSb.append("</Results>");
    // System.out.println("<?xml version=\"1.0\" encoding=\"" + encoding +
    // "\"?>");
    // System.out.println(xmlSb.toString());
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

  private String escape(String string) {
    return string;
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

  @Override
  public String toString() {
    return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + xmlSb.toString();
  }
}