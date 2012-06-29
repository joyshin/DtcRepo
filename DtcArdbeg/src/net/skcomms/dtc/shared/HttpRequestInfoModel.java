package net.skcomms.dtc.shared;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class HttpRequestInfoModel implements Serializable {

  private String httpMethod;
  private String url;
  private String requestData;
  private String encoding;
  private Map<String, String> requestParameter;
  private String path;

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
    return this.encoding;
  }

  public String getHttpMethod() {
    return this.httpMethod;
  }

  public String getPath() {
    return this.path;
  }

  public String getRequestData() {
    return this.requestData;
  }

  public Map<String, String> getRequestParameter() {
    return this.requestParameter;
  }

  public String getUrl() {
    return this.url;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public void setPath(String currentPath) {
    this.path = currentPath;
  }

  public void setRequestData(String requestData) {
    this.requestData = requestData;
  }

  public void setRequestParameter(Map<String, String> requestParameter) {
    this.requestParameter = requestParameter;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}