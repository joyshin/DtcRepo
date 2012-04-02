<%@ page language="java" contentType="text/html; charset=windows-949"
    pageEncoding="windows-949"%>
<%@page import="java.net.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="java.lang.*" %>
<%
if (request.getMethod().equals("GET")) {
	final String DTC_URL = "http://dtc.skcomms.net/"; 
	
	String requestUrl = request.getRequestURL().toString();
	requestUrl = requestUrl.replaceFirst("http://127.0.0.1:8888/dtcproxy/", DTC_URL);
	
	String b = request.getParameter("b");
	if (b != null && b != "") {
	  requestUrl += "?b=" + b;
	}
	String c = request.getParameter("c");
	if (c != null && c != "") {
	  requestUrl += "?c=" + c;
	}
	
	//out.println(requestUrl);
	//out.println("<p />");
	
	URL url = new URL(requestUrl);
	URLConnection con = url.openConnection();
	
	BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-949"));
	String line;
	while ((line = reader.readLine()) != null) {
	  line = line.replaceAll("/[?]c=", "?c=");
	  out.println(line);
	}
	reader.close();
}
else if (request.getMethod().equals("POST")) {  // POST
  final String DTC_URL = "http://dtc.skcomms.net/"; 
  
  String requestUrl = request.getRequestURL().toString();
  requestUrl = requestUrl.replaceFirst("http://127.0.0.1:8888/dtcproxy/", DTC_URL);
  
  out.println("<!--" + requestUrl + "-->");
  
  out.println(requestUrl);
  //out.println("<p />");

  String data = "";
  Enumeration names = request.getParameterNames();
  while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    //data += "&" + URLEncoder.encode(name, "UTF-8") 
    //    + "=" + URLEncoder.encode(request.getParameter(name), "UTF-8");
    data += "&" + name + "=" + request.getParameter(name);
  }
  data = data.substring(1);

  out.println("<!--" + data + "-->");
  
  URLConnection conn = new URL(requestUrl).openConnection();
  ((HttpURLConnection) conn).setRequestMethod("POST");
  conn.setDoOutput(true);
  
  OutputStream os = new DataOutputStream(conn.getOutputStream());
  os.write(data.getBytes("UTF-8"));
  os.flush();
  os.close();
  
  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-949"));
  String line;
  while ((line = reader.readLine()) != null) {
    line = line.replaceAll("/[?]c=", "?c=");
    line = line.replaceAll("[.]/", "http://dtc.skcomms.net/");
    out.println(line);
  }
  reader.close();
 
}
%>
