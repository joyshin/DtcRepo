<%@page language="java" contentType="text/html; charset=windows-949"  pageEncoding="windows-949"%>
<%@page import="java.net.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="java.lang.*" %>
<%
request.setCharacterEncoding("EUC-KR");

final String DTC_URL = "http://dtc.skcomms.net/";
final String PROXY_URL = "http://127.0.0.1:8888/dtcproxy/";

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
    sb.append(URLEncoder.encode(request.getParameter(name)));
  }
  while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sb.append('&');
    sb.append(name);
    sb.append('=');
    sb.append(URLEncoder.encode(request.getParameter(name)));
  }
  String params = sb.toString();

  URLConnection conn = new URL(requestUrl).openConnection();
  ((HttpURLConnection) conn).setRequestMethod("POST");
  conn.setDoOutput(true);
  
  OutputStream os = new DataOutputStream(conn.getOutputStream());
  os.write(params.getBytes("UTF-8"));
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