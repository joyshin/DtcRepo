package net.skcomms.dtc.client.model;

import net.skcomms.dtc.shared.HttpRequestInfoModel;

public class DtcTestPageResponse {

  private String result;

  private HttpRequestInfoModel request;

  public HttpRequestInfoModel getRequest() {
    return this.request;
  }

  public String getResult() {
    return this.result;
  }

  public void setRequest(HttpRequestInfoModel aRequest) {
    this.request = aRequest;
  }

  public void setResult(String result) {
    this.result = result;
  }

}
