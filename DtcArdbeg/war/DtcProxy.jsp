<%@page language="java" contentType="text/html; charset=EUC-KR"  pageEncoding="EUC-KR"%>
<%@page import="java.net.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="java.lang.*" %>
<%
final String CHARSET = "EUC-KR";
request.setCharacterEncoding(CHARSET);
response.setCharacterEncoding(CHARSET);

int index = request.getRequestURL().toString().indexOf("/_dtcproxy_/");
String baseUrl = request.getRequestURL().toString().substring(0, index+12);

final String DTC_URL = "http://10.141.6.198/";
final String PROXY_URL = baseUrl;

if (request.getMethod().equals("GET")) {
	
	String requestUrl = request.getRequestURL().toString();
	requestUrl = requestUrl.replace(PROXY_URL, DTC_URL);
	requestUrl = requestUrl.replace("./", DTC_URL);
	
	String b = request.getParameter("b");
	if (b != null && b != "") {
	  requestUrl += "?b=" + b;
	}
	String c = request.getParameter("c");
	if (c != null && c != "") {
	  requestUrl += "?c=" + c;
	}
	String u = request.getParameter("u");
	if (u != null && u != "") {
	  requestUrl += "?u=" + URLEncoder.encode(u);
	  response.setContentType("text/xml");
    }
  
	URL url = new URL(requestUrl);
	URLConnection con = url.openConnection();
	
	BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-949"));
	String line;
	while ((line = reader.readLine()) != null) {
	  line = line.replace("/newwindow.png", DTC_URL + "newwindow.png");
	  line = line.replace("/?c=", "?c=");
	  if (!line.equals("")) {
	   out.println(line);
	  }
	}
	reader.close();
}
else if (request.getMethod().equals("POST")) {  // POST

  String requestUrl = request.getRequestURL().toString();
  requestUrl = requestUrl.replace(PROXY_URL, DTC_URL);
  
  StringBuilder sb = new StringBuilder();
  Enumeration names = request.getParameterNames();
  if (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sb.append(name);
    sb.append('=');
    sb.append(URLEncoder.encode(request.getParameter(name), CHARSET));
  }
  while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sb.append('&');
    sb.append(name);
    sb.append('=');
    sb.append(URLEncoder.encode(request.getParameter(name), CHARSET));
  }
  String params = sb.toString();

  URLConnection conn = new URL(requestUrl).openConnection();
  ((HttpURLConnection) conn).setRequestMethod("POST");
  conn.setDoOutput(true);
  
  OutputStream os = new DataOutputStream(conn.getOutputStream());
  os.write(params.getBytes());
  os.flush();
  os.close();
  
  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-949"));
  String line;
  while ((line = reader.readLine()) != null) {
    line = line.replace("/?c=", "?c=");
    out.println(line);
  }
  reader.close();
}
%>