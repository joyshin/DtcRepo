/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;

/**
 * @author jujang@sk.com
 * 
 */
@SuppressWarnings("serial")
public class DtcRequestInfo implements Serializable {

  private IpInfo ipInfo;

  /**
   * @return
   */
  public IpInfo getIpInfo() {
    return this.ipInfo;
  }

  /**
   * @param ipInfo
   */
  public void setIpInfo(IpInfo ipInfo) {
    this.ipInfo = ipInfo;
  }

}
