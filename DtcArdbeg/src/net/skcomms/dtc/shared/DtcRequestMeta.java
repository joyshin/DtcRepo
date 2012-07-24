/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author jujang@sk.com
 */
@SuppressWarnings("serial")
public class DtcRequestMeta implements Serializable {

  private List<DtcRequestParameter> params;

  private IpInfoModel ipInfo;

  private String encoding;

  private String path;

  private String appName;

  private String apiNumber;

  /**
   * @return
   */
  public String getEncoding() {
    return this.encoding;
  }

  /**
   * @return
   */
  public IpInfoModel getIpInfo() {
    return this.ipInfo;
  }

  public List<DtcRequestParameter> getParams() {
    return this.params;
  }

  /**
   * @return
   */
  public String getPath() {
    return this.path;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setIpInfo(IpInfoModel ipInfo) {
    this.ipInfo = ipInfo;
  }

  public void setParams(List<DtcRequestParameter> params) {
    this.params = params;
  }

  /**
   * @param path
   */
  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return this.encoding + ", " + this.ipInfo.toString() + ", " + this.params.toString();
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getApiNumber() {
    return apiNumber;
  }

  public void setApiNumber(String apiNumber) {
    this.apiNumber = apiNumber;
  }

}
