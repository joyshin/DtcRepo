<%@page language="java" contentType="text/html" pageEncoding="windows-949"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
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

    DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
    writer.write(readAllBytes(request.getInputStream()));
    writer.flush();
    writer.close();
  }

  byte[] content = readAllBytes(conn.getInputStream());

  String encoding = guessCharacterEncoding(content);
  response.setCharacterEncoding(encoding);
  BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
      content), encoding));

  String line;
  while ((line = reader.readLine()) != null) {
    line = line.replace("/newwindow.png", DTC_URL + "newwindow.png");
    line = line.replace("/?c=", "?c=");
    out.println(line);
  }
  reader.close();
%>
