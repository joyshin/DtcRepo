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
public class DtcRequestInfo implements Serializable {

  private List<DtcRequestParameter> params;

  private IpInfo ipInfo;

  /**
   * @return
   */
  public IpInfo getIpInfo() {
    return this.ipInfo;
  }

  public List<DtcRequestParameter> getParams() {
    return this.params;
  }

  /**
   * @param ipInfo
   */
  public void setIpInfo(IpInfo ipInfo) {
    this.ipInfo = ipInfo;
  }

  public void setParams(List<DtcRequestParameter> params) {
    this.params = params;
  }

  @Override
  public String toString() {
    return this.ipInfo.toString() + ", " + this.params.toString();
  }

}
