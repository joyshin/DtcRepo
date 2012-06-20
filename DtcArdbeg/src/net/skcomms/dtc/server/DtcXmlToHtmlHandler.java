/**
 * 
 */
package net.skcomms.dtc.server;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcXmlToHtmlHandler extends DefaultHandler {

  public class ResultNode {
    String nodeName;
    String attribute;
    int depth;
    boolean isRoot;

    public ResultNode() {
      this.nodeName = new String();
      this.attribute = new String();
      this.isRoot = false;
    }

    String getAttribute() {
      return this.attribute;
    }

    int getDepth() {
      return this.depth;
    }

    String getNodeName() {
      return this.nodeName;
    }

    boolean isRoot() {
      return this.isRoot;
    }

    void setAttribute(String attr) {
      this.attribute = attr;
    }

    void setDepth(int depth) {
      this.depth = depth;
    }

    void setNodeName(String name) {
      this.nodeName = name;
    }

    void setRoot(boolean root) {
      this.isRoot = root;
    }
  }

  final String CDATA_TOKEN = "<![CDATA[";

  private final StringBuilder parseBuffer;
  private final StringBuilder rawHtml;
  private final Stack<ResultNode> divFormStack;
  private final Stack<String> tagNameStack;

  public DtcXmlToHtmlHandler() {
    super();
    this.parseBuffer = new StringBuilder();
    this.rawHtml = new StringBuilder();

    this.divFormStack = new Stack<ResultNode>();
    this.tagNameStack = new Stack<String>();
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
    String elementName = qName;
    String temp;
    StringBuilder divForm = new StringBuilder();
    StringBuilder nodeItem = new StringBuilder();
    ResultNode node = null;

    // terminal node
    // System.out.println("tagNameStack: " + this.tagNameStack.peek() + ":" +
    // elementName);

    if (this.parseBuffer.length() > 0 && this.parseBuffer.lastIndexOf(">") > 0) {
      // Text Node 영역에 tag가 들어올 경우
      temp = this.parseBuffer.substring(this.parseBuffer.lastIndexOf(">") + 1);
      this.parseBuffer.setLength(0);
      this.parseBuffer.append(CDATA_TOKEN);
      this.parseBuffer.append(temp);
      this.parseBuffer.append("]]>");
      this.parseBuffer.append("\n");

    } else if (this.parseBuffer.length() > 0) {
      this.parseBuffer.insert(0, CDATA_TOKEN);
      this.parseBuffer.append("]]>");
      this.parseBuffer.append("\n");
    }

    if (this.tagNameStack.peek().equals(elementName)) {

      if (this.divFormStack.isEmpty()) {
        nodeItem.append("</div>\n");
        divForm.append(nodeItem.toString());
      }
      else {
        node = this.divFormStack.pop();
        nodeItem.append("<div class=\"table_row ");
        nodeItem.append(node.getNodeName());
        nodeItem.append("\"");
        nodeItem.append(">");
        nodeItem.append("<div class=key_");
        nodeItem.append(node.getNodeName());
        nodeItem.append(" name=");
        nodeItem.append(node.getNodeName());
        nodeItem.append(node.getAttribute());
        nodeItem.append(">\n");
        nodeItem.append(node.getNodeName());
        nodeItem.append("</div>\n");
        nodeItem.append("<div class=value_");
        nodeItem.append(node.getNodeName());
        nodeItem.append(">\n");
        nodeItem.append(this.parseBuffer);
        nodeItem.append("</div>\n");
        nodeItem.append("</div>\n");

        divForm.append(nodeItem.toString());
        nodeItem.setLength(0);

        // pop all node
        int stackSize = this.divFormStack.size();
        for (int i = 0; i < stackSize; i++) {
          node = this.divFormStack.pop();

          if (node.isRoot()) {
            nodeItem.append("<div id=");
            nodeItem.append("\"");
          }
          else {
            nodeItem.append("<div class=");
            nodeItem.append("\"");
            nodeItem.append("depth_");
            nodeItem.append(node.getDepth());
            nodeItem.append(" ");
          }
          nodeItem.append(node.getNodeName());
          nodeItem.append("\"");
          nodeItem.append(" name=");
          nodeItem.append(node.getNodeName());
          nodeItem.append(node.getAttribute());
          nodeItem.append(">");
          nodeItem.append(node.getNodeName());
          divForm.insert(0, nodeItem.toString());
          nodeItem.setLength(0);

        }
      }
      // System.out.println("div: " + divForm.toString());

    } else {
      nodeItem.append("</div>\n");
      divForm.append(nodeItem.toString());

    }

    this.tagNameStack.pop();
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

    String elementName = qName;
    StringBuilder attrBuffer = new StringBuilder();
    ResultNode node = new ResultNode();

    if (this.tagNameStack.isEmpty()) {
      if (this.divFormStack.isEmpty()) {
        node.setRoot(true);
      }
      this.tagNameStack.push(elementName);
    } else if (!this.tagNameStack.peek().equals(elementName)) {
      this.tagNameStack.push(elementName);
    }

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

    node.setDepth(this.tagNameStack.size());
    node.setNodeName(elementName);
    node.setAttribute(attrBuffer.toString());

    this.divFormStack.push(node);
  }

  @Override
  public void warning(SAXParseException e) {
    System.out.println("Warning: " + e.getMessage());
  }
}
