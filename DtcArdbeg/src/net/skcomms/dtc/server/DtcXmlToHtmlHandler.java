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

    divForm.append("</div>\n");

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
    if ((elementName == this.RESULTS) ||
        (elementName == this.RESULT_SET) ||
        (elementName == this.RESPONSE_INFO) ||
        (elementName == this.RESULT_HEADER) ||
        (elementName == this.RESULT_LIST) ||
        (elementName == this.DOCUMENT))
    {
    } else {
      rawHtml.append("</div>\n");

    }
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
    String className = qName;

    // get attribute
    if (atts.getLength() > 0) {
      for (int i = 0; i < atts.getLength(); i++) {
        attrBuffer.append(" ");
        attrBuffer.append(atts.getQName(i));
        attrBuffer.append("=\"");
        attrBuffer.append(atts.getValue(i));
        attrBuffer.append("\"");
      }
    }

    if (className == RESULTS) {
      // root node
      if (atts.getLength() > 0) {
        divForm.append("<div id=");
        divForm.append(className);
        divForm.append(attrBuffer);
        divForm.append(">");
        divForm.append(className);

      } else {
        // child Results node
        divForm.append("<div class=");
        divForm.append(className);
        divForm.append(">");
        divForm.append(className);
      }
    } else if ((className == RESPONSE_INFO) ||
        (className == RESULT_SET) ||
        (className == RESULT_HEADER) ||
        (className == RESULT_LIST) ||
        (className == DOCUMENT)) {

      divForm.append("<div class=");
      divForm.append(className);
      divForm.append(">");
      divForm.append(className);

    } else {
      // data node
      divForm.append("<div class=table_row> <div class=key_");
      divForm.append(className);
      divForm.append(attrBuffer);
      divForm.append(">");

      divForm.append(className);
      divForm.append("</div>\n");
      divForm.append("<div class=value_");
      divForm.append(className);
      divForm.append("\">\n");
    }

    rawHtml.append(divForm);
  }

  @Override
  public void warning(SAXParseException e) {
    System.out.println("Warning: " + e.getMessage());
  }
}
