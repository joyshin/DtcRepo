/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;

/**
 * @author jujang@sk.com
 * 
 */
public class Item implements Serializable {

  private String name;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private String date;
  private String description;

  public Item() {
  }

  /**
   * @param data
   */
  public Item(char[] data) {
    this.name = new String(data);
  }

}
