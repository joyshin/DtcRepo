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
public class IpInfo implements Serializable {

  private List<Pair<String, String>> options = new ArrayList<Pair<String, String>>();

  /**
   * @param value
   * @param text
   */
  public void addOption(String value, String text) {
    this.options.add(new Pair<String, String>(value, text));
  }

  @Override
  public String toString() {
    return this.options.toString();
  }

}
