package net.skcomms.dtc.client.model;

import java.io.Serializable;

import net.skcomms.dtc.shared.DtcRequest;

@SuppressWarnings("serial")
public class DtcResponse implements Serializable {

  private String result;

  private DtcRequest request;

  private long responseTime;

  public DtcRequest getRequest() {
    return this.request;
  }

  public long getResponseTime() {
    return this.responseTime;
  }

  public String getResult() {
    return this.result;
  }

  public void setRequest(DtcRequest aRequest) {
    this.request = aRequest;
  }

  public void setResponseTime(long time) {
    this.responseTime = time;
  }

  public void setResult(String result) {
    this.result = result;
  }

}
