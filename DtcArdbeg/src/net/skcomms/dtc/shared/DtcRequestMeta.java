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

  private String cndFieldName;

  private String queryFieldName;

  public String getApiNumber() {
    return this.apiNumber;
  }

  public String getAppName() {
    return this.appName;
  }

  public String getCndFieldName() {
    return this.cndFieldName;
  }

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

  public String getQueryFieldName() {
    return this.queryFieldName;
  }

  public void setApiNumber(String apiNumber) {
    this.apiNumber = apiNumber;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setCndQueryFieldName(String cndFieldName) {
    this.cndFieldName = cndFieldName;
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

  public void setQueryFieldName(String queryFieldName) {
    this.queryFieldName = queryFieldName;
  }

  @Override
  public String toString() {
    return this.encoding + ", " + this.ipInfo.toString() + ", " + this.params.toString();
  }

}
