<%@page language="java" contentType="text/html" pageEncoding="windows-949"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@page import="javax.xml.parsers.*"%>
<%@page import="javax.xml.transform.dom.*"%>
<%@page import="javax.xml.transform.stream.*"%>
<%@page import="javax.xml.transform.*"%>
<%@page import="org.w3c.dom.*"%>
<%@page import="org.xml.sax.helpers.DefaultHandler" %>
<%@page import="org.xml.sax.*" %>
<%!final String DTC_URL = "http://10.141.6.198/";%>
<%!public String getForwardedUrl(HttpServletRequest request) {
    int index = request.getRequestURL().toString().indexOf("/_dtcproxy_/");
    String baseUrl = request.getRequestURL().toString().substring(0, index + 12);

    String forwardedUrl = request.getRequestURL().toString();
    if (request.getQueryString() != null) {
      forwardedUrl += "?" + request.getQueryString();
    }
    forwardedUrl = forwardedUrl.replace(baseUrl, DTC_URL);
    forwardedUrl = forwardedUrl.replace("./", DTC_URL);

    return forwardedUrl;
  }%>

<%!public static String transformXmlToHtml(Document xmlDoc) throws TransformerException,
      TransformerConfigurationException {
    DOMSource domSource = new DOMSource(xmlDoc);

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.ENCODING, "euc-kr");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    String xml = sw.toString();

    return xml;
  }%>
<%! public static Document decorateXml(Document xmlDoc) {
  Document htmlDoc;
  Element root = xmlDoc.getDocumentElement();
  
  Node responseInfoRoot = root.getElementsByTagName("ResponseInfo").item(0);
  System.out.println(responseInfoRoot.getNodeName());
  NodeList responseInfoChildNodes = responseInfoRoot.getChildNodes();
  
  try {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    htmlDoc = builder.newDocument();
    Node responseInfoNode;
    for (int i = 0; i < responseInfoChildNodes.getLength(); i+=2) {
      responseInfoNode = responseInfoChildNodes.item(i);
      System.out.println(responseInfoNode.getNodeName());
      responseInfoRoot.insertBefore(xmlDoc.createElement("div"), responseInfoNode);
    }
  } catch (ParserConfigurationException e) {
  }
  
  htmlDoc = xmlDoc;
  
  return htmlDoc;
  }%>
<%!public static String guessCharacterEncoding(byte[] bytes) throws IOException {
    String string;
    if (bytes.length > 1024) {
      string = new String(bytes, 0, 1024);
    }
    else {
      string = new String(bytes);
    }
    if (string.contains("charset=utf-8") || string.contains("encoding=\"utf-8\"")) {
      return "utf-8";
    }
    return "windows-949";
  }%>
<%!public static byte[] readAllBytes(InputStream is) throws IOException {
    DataInputStream dis = new DataInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream(40960);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = dis.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    return bos.toByteArray();
  }%>

<%
  final String forwardedUrl = getForwardedUrl(request);

  URL url = new URL(forwardedUrl);
  URLConnection conn = url.openConnection();
  DocumentBuilder htmlBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  BufferedReader reader;
  /*
  if (forwardedUrl.contains("/response_xml.html?")) {
    response.setContentType("text/xml");
  }
  else if (forwardedUrl.contains("/response_json.html?")) {
    response.setContentType("text/json");
  }
   */
  if (request.getMethod().equals("POST")) { // POST
    ((HttpURLConnection) conn).setRequestMethod("POST");
    conn.setDoOutput(true);

    DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
    writer.write(readAllBytes(request.getInputStream()));
    writer.flush();
    writer.close();
  }

  byte[] content = readAllBytes(conn.getInputStream());

  String encoding = guessCharacterEncoding(content);
  response.setCharacterEncoding(encoding);
  //  reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
  //      content), encoding));

  if (forwardedUrl.contains("/response_xml.html?")) {
    response.setContentType("text/xml");
    try {
      DtcXmlParser dp = new DtcXmlParser();

      ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(content);
      SAXParserFactory sf = SAXParserFactory.newInstance();
      SAXParser sp = sf.newSAXParser();
      sp.parse(bufferInputStream, dp);

      //String tmpXML = sw.toString().replaceAll("[<]", "&lt");
      //String xml = tmpXML.replaceAll("[>]", "&gt");
      out.println(dp.getHtml());

    } catch (org.xml.sax.SAXException se) {
      System.out.println("SAXException: " + se.getMessage());
      return;
    } catch (java.io.IOException ie) {
      System.out.println("IOException: " + ie.getMessage());
      return;
    } catch (ParserConfigurationException pce) {
      System.out.println("ParserConfigurationException: " + pce.getMessage());
      return;
    } catch (java.lang.IllegalArgumentException ae) {
      System.out.println("IllegalArgumentException: " + ae.getMessage());
      return;
    }
  }
  else if (forwardedUrl.contains("/response_json.html?")) {
    response.setContentType("text/json");
    reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        content), encoding));
  }
  else {
    reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        content), encoding));
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.replace("/newwindow.png", DTC_URL + "newwindow.png");
      line = line.replace("/?c=", "?c=");
      out.println(line);
    }
    reader.close();
  }
%>

<%!public class DtcXmlParser extends DefaultHandler {

   final String RESULTS = "Results";
   final String RESULT_SET = "ResultSet";
   final String RESPONSE_INFO = "ResponseInfo";
   final String RESULT_HEAD = "ResultHeader";
   final String RESULt_LIST = "ResultList";
   final String DOCUMENT = "Document";
   private StringBuffer parseBuffer;
   private StringBuffer rawHtml;
    
   public DtcXmlParser() {
      super();
      parseBuffer = new StringBuffer();
      rawHtml = new StringBuffer();
    }

    public void characters(char ch[], int start, int length) throws SAXException {

      //System.out.print("\"");

      for (int i = 0; i < start + length; i++) {
          //System.out.print(ch[i]);
          parseBuffer.append(ch[i]);
      }
      //System.out.println("\"");
      parseBuffer.append("\n");
    }

    public void startDocument() throws SAXException {

    }

    public void endDocument() throws SAXException {
      System.out.println(rawHtml);
    }

    public void startElement(String uri, String name, String qName, Attributes atts)
        throws SAXException {
      //System.out.println("qName Start: " + qName + "..." + atts.getLength());
      StringBuffer divForm = new StringBuffer("<div id=\"");
      StringBuffer attrBuffer = new StringBuffer();
      String elementName = qName;
      
      if (atts.getLength() > 0) {
        String[] attrName = new String[atts.getLength()];
        String[] attrValue = new String[atts.getLength()];
        if (elementName == RESULTS) elementName = "ResultRoot";
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
      divForm.append(attrBuffer);
      divForm.append(">\n");
      rawHtml.append(divForm);
    }

    public void endElement(String uri, String name, String qName) throws SAXException {
      //System.out.println("qName End: " + qName);
      StringBuffer divForm = new StringBuffer("</div>\n");
      String temp;
      
      if(parseBuffer.length() > 0 && parseBuffer.lastIndexOf(">") > 0 ) { // Text Node 영역에 tag가 들어올 경우
        temp = parseBuffer.substring(parseBuffer.lastIndexOf(">") + 1);
        parseBuffer.setLength(0);
        parseBuffer.append(temp);
      }
      rawHtml.append(parseBuffer);
      rawHtml.append(divForm);
      parseBuffer.setLength(0);
    }

    public void error(SAXParseException e) {
      System.out.println("ERR: " + e.getMessage());
    }

    public void warning(SAXParseException e) {
      System.out.println("Warning: " + e.getMessage());
    }
    
    public StringBuffer getHtml() {
      return rawHtml;
    }
  }%>
