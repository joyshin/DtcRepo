/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jujang@sk.com
 * 
 */
@SuppressWarnings("serial")
public class IpInfoModel implements Serializable {

  private List<DtcRequestParameter> options = new ArrayList<DtcRequestParameter>();
  private String ipText = "";

  /**
   * @param value
   * @param text
   */
  public void addOption(String value, String text) {
    this.options.add(new DtcRequestParameter(value, "", text));
  }

  public String getIpText() {
    return this.ipText;
  }

  public List<DtcRequestParameter> getOptions() {
    return this.options;
  }

  public void setIpText(String anIpText) {
    this.ipText = anIpText;
  }

  public void setOptions(List<DtcRequestParameter> options) {
    this.options = options;
  }

  @Override
  public String toString() {
    return "ipText:" + this.ipText + ", " + this.options.toString();
  }

}
