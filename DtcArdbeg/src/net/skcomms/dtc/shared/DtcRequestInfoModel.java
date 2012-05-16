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
   * @param ipInfo
   */
  public void setIpInfo(IpInfoModel ipInfo) {
    this.ipInfo = ipInfo;
  }

  public void setParams(List<DtcRequestParameterModel> params) {
    this.params = params;
  }

  @Override
  public String toString() {
    return this.ipInfo.toString() + ", " + this.params.toString();
  }

}
