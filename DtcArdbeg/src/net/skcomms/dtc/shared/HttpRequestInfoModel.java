package net.skcomms.dtc.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HttpRequestInfoModel implements Serializable {

  private String httpMethod;
  private String url;
  private String requestData;
  private String encoding;

  public HttpRequestInfoModel() {
    this.httpMethod = "";
    this.url = "";
    this.requestData = "";
    this.encoding = "";
  }

  public HttpRequestInfoModel(String httpMethod, String url, String encoding, String requestData) {
    this.httpMethod = httpMethod;
    this.url = url;
    this.requestData = requestData;
    this.encoding = encoding;
  }

  public String getEncoding() {
    return encoding;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getRequestData() {
    return requestData;
  }

  public String getUrl() {
    return url;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public void setRequestData(String requestData) {
    this.requestData = requestData;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}