/**
 * 
 */
package net.skcomms.dtc.server;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcXmlToHtmlHandler extends DefaultHandler {

  final String RESULTS = "Results";
  final String RESULT_SET = "ResultSet";
  final String RESPONSE_INFO = "ResponseInfo";
  final String RESULT_HEADER = "ResultHeader";
  final String RESULT_LIST = "ResultList";
  final String DOCUMENT = "Document";
  final String CDATA_TOKEN = "//<![CDATA[";

  private final StringBuilder parseBuffer;
  private final StringBuilder rawHtml;

  public DtcXmlToHtmlHandler() {
    super();
    this.parseBuffer = new StringBuilder();
    this.rawHtml = new StringBuilder();
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    this.parseBuffer.append(ch, start, length);
  }

  @Override
  public void endDocument() throws SAXException {
    this.rawHtml.append("</body>");
    this.rawHtml.append("</html>");
  }

  @Override
  public void endElement(String uri, String name, String qName) throws SAXException {
    StringBuilder divForm = new StringBuilder();
    String elementName = qName;
    String temp;

    if ((elementName == this.RESULTS) ||
        (elementName == this.RESULT_SET) ||
        (elementName == this.RESULTS)) {
      divForm.append("</div>\n");
    } else if ((elementName == this.RESPONSE_INFO) ||
        (elementName == this.RESULT_HEADER) ||
        (elementName == this.RESULT_LIST) ||
        (elementName == this.DOCUMENT))
    {
      divForm.append("</ul>\n");
      divForm.append("<hr />");
    } else {
      divForm.append("</li>\n");
    }

    if (this.parseBuffer.length() > 0 && this.parseBuffer.lastIndexOf(">") > 0) {
      // Text Node 영역에 tag가 들어올 경우
      temp = this.parseBuffer.substring(this.parseBuffer.lastIndexOf(">") + 1);
      this.parseBuffer.setLength(0);
      this.parseBuffer.append(this.CDATA_TOKEN);
      this.parseBuffer.append(temp);
      this.parseBuffer.append("//]]>");
      this.parseBuffer.append("\n");

    } else if (this.parseBuffer.length() > 0) {
      this.parseBuffer.insert(0, this.CDATA_TOKEN);
      this.parseBuffer.append("//]]>");
      this.parseBuffer.append("\n");
    }

    this.rawHtml.append(this.parseBuffer);
    this.rawHtml.append(divForm);
    this.parseBuffer.setLength(0);
  }

  public StringBuilder getHtml() {
    return this.rawHtml;
  }

  @Override
  public void startDocument() throws SAXException {
    String dtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.1//EN\" "
        + "\"http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd\">\n"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    this.rawHtml.append(dtd);
    this.rawHtml.append("<body>");
  }

  @Override
  public void startElement(String uri, String name, String qName, Attributes atts)
      throws SAXException {
    StringBuilder divForm = new StringBuilder();
    StringBuilder attrBuffer = new StringBuilder();
    String elementName = qName;
    String className = null;

    if ((elementName == this.RESULTS) ||
        (elementName == this.RESULT_SET) ||
        (elementName == this.RESULTS)) {
      className = elementName;
      divForm.append("<div id=\"");
    } else if ((elementName == this.RESPONSE_INFO) ||
        (elementName == this.RESULT_HEADER) ||
        (elementName == this.RESULT_LIST) ||
        (elementName == this.DOCUMENT)) {
      className = elementName;
      divForm.append("<ul id=\"");
    } else {
      className = elementName;
      divForm.append("<li id=\"");
    }

    if (atts.getLength() > 0) {
      for (int i = 0; i < atts.getLength(); i++) {
        attrBuffer.append(" ");
        attrBuffer.append(atts.getQName(i));
        attrBuffer.append("=\"");
        attrBuffer.append(atts.getValue(i));
        attrBuffer.append("\"");
      }
    }

    divForm.append(elementName);
    divForm.append("\"");

    if (className != null) {
      divForm.append(" class=\"");
      divForm.append(className);
      divForm.append("\"");
    }

    divForm.append(attrBuffer);
    divForm.append(">\n");
    this.rawHtml.append(divForm);
  }

  @Override
  public void warning(SAXParseException e) {
    System.out.println("Warning: " + e.getMessage());
  }
}