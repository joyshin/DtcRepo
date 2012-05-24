/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author jujang@sk.com
 * 
 */
@SuppressWarnings("serial")
public class DtcRequestInfoModel implements Serializable {

  private List<DtcRequestParameterModel> params;

  private IpInfoModel ipInfo;

  private String encoding;

  private String path;

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

  public List<DtcRequestParameterModel> getParams() {
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

  public void setParams(List<DtcRequestParameterModel> params) {
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

}
