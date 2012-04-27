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
    //transformer.setOutputProperty(OutputKeys.ENCODING, "euc-kr");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    String xml = sw.toString();

    return xml;
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
      StringBuffer rawHtml = null;
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
   final String RESULT_HEADER = "ResultHeader";
   final String RESULT_LIST = "ResultList";
   final String DOCUMENT = "Document";
   final String CDATA_TOKEN = "<![CDATA[";
   
   private StringBuffer parseBuffer;
   private StringBuffer rawHtml;
   private String previousTag;
    
   public DtcXmlParser() {
      super();
      parseBuffer = new StringBuffer();
      rawHtml = new StringBuffer();
      previousTag = null;
    }

    public void characters(char ch[], int start, int length) throws SAXException {

      //System.out.print("START :" + Integer.toString(start));

      for (int i = 0; i < start + length; i++) {
          //System.out.print(ch[i]);
          parseBuffer.append(ch[i]);
      }
      //System.out.println("\"");
    }

    public void startDocument() throws SAXException {

      String dtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			       +"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.1//EN\" "
                   +"\"http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd\">\n"
      		       +"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";

      rawHtml.append(dtd);
      rawHtml.append("<body>");
    }

    public void endDocument() throws SAXException {
      //System.out.println(rawHtml);
      rawHtml.append("</body>");
      rawHtml.append("</html>");
    }

    public void startElement(String uri, String name, String qName, Attributes atts)
        throws SAXException {
      //System.out.println("qName Start: " + qName + "..." + atts.getLength());
      StringBuffer divForm = new StringBuffer(); 
      StringBuffer attrBuffer = new StringBuffer();
      String elementName = qName;
      String className = null;
      
      if ( (elementName == RESULTS) ||
		   (elementName == RESULT_SET) || 
           (elementName == RESULTS) ) {        
        className = elementName;
      	divForm.append("<div id=\"");  
      	
      } else if ( (elementName == RESPONSE_INFO) ||
                  (elementName == RESULT_HEADER) ||
                  (elementName == RESULT_LIST) ||
                  (elementName == DOCUMENT))
      {
        className = elementName;
      	divForm.append("<ul id=\"");        
      } else {
        className = elementName;
      	divForm.append("<li id=\"");      
      }
      
      if (atts.getLength() > 0) {
        String[] attrName = new String[atts.getLength()];
        String[] attrValue = new String[atts.getLength()];
                
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

    public void endElement(String uri, String name, String qName) throws SAXException {
      //System.out.println("qName End: " + qName);
      StringBuffer divForm = new StringBuffer();
      String elementName = qName;
      String temp;
      
      if ( (elementName == RESULTS ) || 
           (elementName == RESULT_SET) || 
    	   (elementName == RESULTS) ) {
      	divForm.append("</div>\n");     	
      } else if ( (elementName == RESPONSE_INFO) ||
                  (elementName == RESULT_HEADER) ||
                  (elementName == RESULT_LIST) ||
                  (elementName == DOCUMENT))
      {
      	divForm.append("</ul>\n");
      	divForm.append("<hr />");
      } else {
      	divForm.append("</li>\n");      
      }
      
      if(parseBuffer.length() > 0 && parseBuffer.lastIndexOf(">") > 0 ) { // Text Node 영역에 tag가 들어올 경우
        temp = parseBuffer.substring(parseBuffer.lastIndexOf(">") + 1);
        parseBuffer.setLength(0);
        parseBuffer.append(CDATA_TOKEN);
        parseBuffer.append(temp);
        parseBuffer.append("]]>");
        parseBuffer.append("\n");

      } else if (parseBuffer.length() > 0){
        parseBuffer.insert(0, CDATA_TOKEN);
        parseBuffer.append("]]>");
        parseBuffer.append("\n");
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
