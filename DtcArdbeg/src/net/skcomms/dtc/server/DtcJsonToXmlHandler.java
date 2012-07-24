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
    this.xmlSb.append("</Results>");
    // System.out.println("<?xml version=\"1.0\" encoding=\"" + encoding +
    // "\"?>");
    // System.out.println(xmlSb.toString());
  }

  @Override
  public boolean endObject() throws ParseException, IOException {
    if (this.isCurrentPath("ResultSet/Results/ResultList/Document/")) {
      String documentKey = this.stack.pop();
      this.xmlSb.append(this.getIndentString() + "</" + documentKey + ">");
    }
    return true;
  }

  @Override
  public boolean endObjectEntry() throws ParseException, IOException {
    String key = this.stack.pop();
    if (!(this.isCurrentPath("") && key.equals("Encoding"))) {
      this.xmlSb.append(this.getIndentString() + "</" + key + ">");
    }
    return true;
  }

  private String getCDataString(String value) {
    return "<![CDATA[" + value + "]]>";
  }

  private String getIndentString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= this.stack.size(); i++) {
      // sb.append("  ");
      sb.append("");
    }
    return sb.toString();
  }

  private boolean isCurrentPath(String path) {
    StringBuilder sb = new StringBuilder();

    for (String item : this.stack.toArray(new String[0])) {
      sb.append(item + "/");
    }

    return path.equals(sb.toString());
  }

  @Override
  public boolean primitive(Object value) throws ParseException, IOException {
    if (this.isCurrentPath("ResultSet/ResponseInfo/Code/")
        || this.isCurrentPath("ResultSet/ResponseInfo/Message/")) {
      this.xmlSb.append(this.getIndentString() + value);
    }
    else if (this.isCurrentPath("Encoding/")) {
      this.encoding = value.toString();
    }
    else {
      this.xmlSb.append(this.getIndentString() + this.getCDataString(value.toString()));
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
    this.xmlSb = new StringBuilder();
    this.xmlSb.append("<Results version=\"1.0\">");
  }

  @Override
  public boolean startObject() throws ParseException, IOException {
    if (this.isCurrentPath("ResultSet/Results/ResultList/")) {
      this.xmlSb.append(this.getIndentString() + "<Document>");
      this.stack.push("Document");
    }
    return true;
  }

  @Override
  public boolean startObjectEntry(String key) throws ParseException, IOException {
    if (!(this.isCurrentPath("") && key.equals("Encoding"))) {
      this.xmlSb.append(this.getIndentString() + "<" + key + ">");
    }
    this.stack.push(key);
    return true;
  }

  @Override
  public String toString() {
    return "<?xml version=\"1.0\" encoding=\"" + this.encoding + "\"?>" + this.xmlSb.toString();
  }
}