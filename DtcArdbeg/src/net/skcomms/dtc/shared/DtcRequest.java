package net.skcomms.dtc.shared;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DtcRequest implements Serializable {

  private String encoding;
  private List<DtcRequestParameter> requestParameter;
  private String path;
  private String apiNumber;
  private String appName;

  public String getApiNumber() {
    return this.apiNumber;
  }

  public String getAppName() {
    return this.appName;
  }

  public String getCharset() {
    return this.encoding;
  }

  public String getPath() {
    return this.path;
  }

  public String getRequestParameter(String key) {
    int index = this.requestParameter.indexOf(new DtcRequestParameter(key, null, null));
    return (index == -1 ? null : this.requestParameter.get(index).getValue());
  }

  public List<DtcRequestParameter> getRequestParameters() {
    return this.requestParameter;
  }

  public void setApiNumber(String apiNumber) {
    this.apiNumber = apiNumber;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setPath(String currentPath) {
    this.path = currentPath;
  }

  public void setRequestParameters(List<DtcRequestParameter> params) {
    this.requestParameter = params;
  }
}