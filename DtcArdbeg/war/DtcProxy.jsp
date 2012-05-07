<%@page language="java" contentType="text/html" pageEncoding="windows-949"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="javax.xml.parsers.*"%>
<%@page import="org.json.simple.parser.JSONParser"%>
<%@page import="org.xml.sax.InputSource"%>
<%@page import="net.skcomms.dtc.server.*"%>
<%!final String DTC_URL = "http://10.141.6.198/";%>
<%!public String escapeForXml(String src) {
    return src.replaceAll("\\\\u000B", "&#11;").replaceAll("\\\\f", "&#12;");
  }%>
<%!public String getForwardedUrl(HttpServletRequest request) {
    int index = request.getRequestURL().toString().indexOf("/_dtcproxy_/");
    String baseUrl = request.getRequestURL().toString().substring(0, index + 12);

    String forwardedUrl = request.getRequestURL().toString();
    System.out.println(forwardedUrl);
    if (forwardedUrl.contains("/response_json.html")) {
      System.out.println(request.getParameter("u"));
      return request.getParameter("u");
    }
    if (request.getQueryString() != null) {
      forwardedUrl += "?" + request.getQueryString();
    }
    forwardedUrl = forwardedUrl.replace(baseUrl, DTC_URL);
    forwardedUrl = forwardedUrl.replace("./", DTC_URL);

    return forwardedUrl;
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
<%!public String getHtmlFromXml(byte[] content) throws Exception {
    DtcXmlToHtmlHandler dp = new DtcXmlToHtmlHandler();
    ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(content);
    SAXParserFactory sf = SAXParserFactory.newInstance();
    SAXParser sp = sf.newSAXParser();
    sp.parse(bufferInputStream, dp);
    //sp.parse(new String(content, "windows-949"), dp);
    return dp.getHtml().toString();
  }%>

<%
  final String forwardedUrl = getForwardedUrl(request);

  URL url = new URL(forwardedUrl);
  URLConnection conn = url.openConnection();
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
  
  System.out.println("getContentType : " + ((HttpURLConnection) conn).getContentType());
  System.out.println("content : " + new String(content, encoding));
  System.out.println("index:" + new String(content, encoding).indexOf(0x0b));
  
  if (forwardedUrl.contains("/response_xml.html?")) {
    response.setContentType("text/xml");
    out.print(getHtmlFromXml(content));
  }
  else if (((HttpURLConnection) conn).getContentType().startsWith("Application/json")) {
    response.setContentType("text/xml");
    
    System.out.println("json:" + new String(content, "euc-kr"));
    
    JSONParser parser = new JSONParser();
    DtcJsonToXmlHandler jsonHandler = new DtcJsonToXmlHandler();
    parser.parse(escapeForXml(new String(content, encoding)), jsonHandler);
    String xmlString = jsonHandler.toString();
    System.out.println("xml:" + xmlString.substring(0, 1130));
    System.out.println("xml_index:" + xmlString.indexOf(0x0b));
    String xml = getHtmlFromXml(xmlString.getBytes());
    out.println(xml);
  }
  else {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        content), encoding));
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.replace("/newwindow.png", "http://dtc.skcomms.net/newwindow.png");
      line = line.replace("/?c=", "?c=");
      out.println(line);
    }
    reader.close();
  }
%>
