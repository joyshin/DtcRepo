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
  final String CDATA_TOKEN = "<![CDATA[";

  private final StringBuilder parseBuffer;
  private final StringBuilder rawHtml;

  public DtcXmlToHtmlHandler() {
    super();
    parseBuffer = new StringBuilder();
    rawHtml = new StringBuilder();
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    parseBuffer.append(ch, start, length);
  }

  @Override
  public void endDocument() throws SAXException {
    rawHtml.append("</body>");
    rawHtml.append("</html>");
  }

  @Override
  public void endElement(String uri, String name, String qName) throws SAXException {
    StringBuilder divForm = new StringBuilder();
    String elementName = qName;
    String temp;

    if ((elementName == RESULTS) ||
        (elementName == RESULT_SET) ||
        (elementName == RESULTS)) {
      divForm.append("</div>\n");
    } else if ((elementName == RESPONSE_INFO) ||
        (elementName == RESULT_HEADER) ||
        (elementName == RESULT_LIST) ||
        (elementName == DOCUMENT))
    {
      divForm.append("</ul>\n");
      divForm.append("<hr />");
    } else {
      divForm.append("</li>\n");
    }

    if (parseBuffer.length() > 0 && parseBuffer.lastIndexOf(">") > 0) {
      // Text Node 영역에 tag가 들어올 경우
      temp = parseBuffer.substring(parseBuffer.lastIndexOf(">") + 1);
      parseBuffer.setLength(0);
      parseBuffer.append(CDATA_TOKEN);
      parseBuffer.append(temp);
      parseBuffer.append("]]>");
      parseBuffer.append("\n");

    } else if (parseBuffer.length() > 0) {
      parseBuffer.insert(0, CDATA_TOKEN);
      parseBuffer.append("]]>");
      parseBuffer.append("\n");
    }

    rawHtml.append(parseBuffer);
    rawHtml.append(divForm);
    parseBuffer.setLength(0);
  }

  public StringBuilder getHtml() {
    return rawHtml;
  }

  @Override
  public void startDocument() throws SAXException {
    String dtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.1//EN\" "
        + "\"http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd\">\n"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    rawHtml.append(dtd);
    rawHtml.append("<body>");
  }

  @Override
  public void startElement(String uri, String name, String qName, Attributes atts)
      throws SAXException {
    StringBuilder divForm = new StringBuilder();
    StringBuilder attrBuffer = new StringBuilder();
    String elementName = qName;
    String className = null;

    if ((elementName == RESULTS) ||
        (elementName == RESULT_SET) ||
        (elementName == RESULTS)) {
      className = elementName;
      divForm.append("<div id=\"");
    } else if ((elementName == RESPONSE_INFO) ||
        (elementName == RESULT_HEADER) ||
        (elementName == RESULT_LIST) ||
        (elementName == DOCUMENT)) {
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
    rawHtml.append(divForm);
  }

  @Override
  public void warning(SAXParseException e) {
    System.out.println("Warning: " + e.getMessage());
  }
}