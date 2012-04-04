<%@page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%
  final String CHARSET = "EUC-KR";

  int index = request.getRequestURL().toString().indexOf("/_dtcproxy_/");
  String baseUrl = request.getRequestURL().toString().substring(0, index + 12);

  final String DTC_URL = "http://10.141.6.198/";
  final String PROXY_URL = baseUrl;

  String forwardedUrl = request.getRequestURL().toString();
  if (request.getQueryString() != null) {
    forwardedUrl += "?" + request.getQueryString();
  }
  forwardedUrl = forwardedUrl.replace(PROXY_URL, DTC_URL);
  forwardedUrl = forwardedUrl.replace("./", DTC_URL);

  if (forwardedUrl.contains("/response_xml.html?")) {
    response.setContentType("text/xml");
  }
  else if (forwardedUrl.contains("/response_json.html?")) {
    response.setContentType("text/json");
  }

  URL url = new URL(forwardedUrl);
  URLConnection conn = url.openConnection();
  
  if (request.getMethod().equals("POST")) { // POST
    ((HttpURLConnection) conn).setRequestMethod("POST");
    conn.setDoOutput(true);

    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), CHARSET));
    BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), CHARSET));
    String line;
    while ((line = reader.readLine()) != null) {
      writer.write(line);
    }
    writer.flush();
    writer.close();
  }
  
  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
  String line;
  while ((line = reader.readLine()) != null) {
    line = line.replace("/newwindow.png", DTC_URL + "newwindow.png");
    line = line.replace("/?c=", "?c=");
    out.println(line);
  }
  reader.close();
%>