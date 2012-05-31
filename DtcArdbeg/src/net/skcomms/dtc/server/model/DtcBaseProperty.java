/**
 * 
 */
package net.skcomms.dtc.server.model;

/**
 * @author jujang@sk.com
 */
public class DtcBaseProperty {

  private String key;

  private String val;

  private String comment;

  /**
   * @param key
   * @param val
   */
  public DtcBaseProperty(String key, String val) {
    this.key = key;
    this.val = val;
  }

  /**
   * @param key
   * @param val
   * @param comment
   */
  public DtcBaseProperty(String key, String val, String comment) {
    this.key = key;
    this.val = val;
    this.comment = comment;
  }

  public String getComment() {
    return this.comment;
  }

  /**
   * @return
   */
  public String getKey() {
    return this.key;
  }

  /**
   * @return
   */
  public String getValue() {
    return this.val;
  }

  @Override
  public String toString() {
    return "Key: " + this.key + ", Val:" + this.val + ", Comment:" + this.comment;
  }
}
