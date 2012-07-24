<%@page language="java" contentType="text/html" pageEncoding="windows-949"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.xml.parsers.*"%>
<%@page import="org.json.simple.parser.JSONParser"%>
<%@page import="org.xml.sax.InputSource"%>
<%@page import="net.skcomms.dtc.server.*"%>
<%!final String DTC_URL = "http://10.141.6.198/";%>
<%!public String escapeForXml(String src) {
    return src.replaceAll("\\\\u000B", "&#11;").replaceAll("\\\\f", "&#12;");
  }%>
<%!public Map<String, String> parseQueryString(String queryString) 
    throws UnsupportedEncodingException{
    Map<String, String> map = new HashMap<String, String>();
    String[] params = queryString.split("&");
    for (String param : params) {
      String[] pair = param.split("=");
      if (pair.length == 2) {
        map.put(pair[0], URLDecoder.decode(pair[1], "utf-8"));
      } else {
        map.put(pair[0], "");
      }
    }
    return map;
}%>
<%!public String combineQueryString(Map<String, String> params, String encoding) 
    throws UnsupportedEncodingException{
  System.out.println("charset:" + encoding);
  StringBuilder sb = new StringBuilder();
  for (Map.Entry<String, String> entry : params.entrySet()) {
    sb.append(entry.getKey());
    sb.append("=");
    sb.append(URLEncoder.encode(entry.getValue(), encoding));
    sb.append("&");
  }
  return sb.toString().substring(0, sb.length()-1);
}
%>
<%!public String getForwardedUrl(HttpServletRequest request) throws UnsupportedEncodingException {
    int index = request.getRequestURL().toString().indexOf("/_dtcproxy_/");
    String baseUrl = request.getRequestURL().toString().substring(0, index + 12);

    String forwardedUrl = request.getRequestURL().toString();
    if (forwardedUrl.contains("/response_json.html")) {
      return request.getParameter("u");
    }
    
    if (forwardedUrl.contains("/response_xml.html")) {
      String u = request.getParameter("u");
      int uIndex= u.indexOf('?');
      String queryString = u.substring(uIndex+1);
      Map<String, String> params = parseQueryString(queryString);
      u = u.substring(0, uIndex) + "?" + combineQueryString(params, request.getCharacterEncoding());
      System.out.println("u:" + u);
      forwardedUrl = forwardedUrl + "?u=" + URLEncoder.encode(u, "utf-8") ;
    }
    else if (request.getQueryString() != null) {
      forwardedUrl += "?" + request.getQueryString();
    }
    forwardedUrl = forwardedUrl.replace(baseUrl, DTC_URL);
    forwardedUrl = forwardedUrl.replace("./", DTC_URL);

    System.out.println("ForwardedURL: " + forwardedUrl);
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
    byte[] tempData = readAllBytes(request.getInputStream());
    System.out.println(new String(tempData));
    System.out.println(request.getCharacterEncoding());
    //System.out.println(URLDecoder.decode(new String(tempData), request.getCharacterEncoding()));
    writer.write(tempData);
    writer.flush();
    writer.close();
  }

  byte[] content = readAllBytes(conn.getInputStream());

  String encoding = guessCharacterEncoding(content);
  
  if (forwardedUrl.contains("/response_xml.html?")) {
    response.setContentType("text/xml");
    out.print(getHtmlFromXml(content));
    out.flush();
    out.close();
  }
  else if (((HttpURLConnection) conn).getContentType().startsWith("Application/json")) {
    response.setContentType("text/xml");
    
    JSONParser parser = new JSONParser();
    DtcJsonToXmlHandler jsonHandler = new DtcJsonToXmlHandler();
    parser.parse(escapeForXml(new String(content, encoding)), jsonHandler);
    String xmlString = jsonHandler.toString();
    String xml = getHtmlFromXml(xmlString.getBytes(encoding));
    out.println(xml);
    out.flush();
    out.close();
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
