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
public class DtcNodeInfo implements Serializable {

  private String description;

  private String name;

  private String path;

  private String updateTime;

  public String getDescription() {
    return this.description;
  }

  public String getName() {
    return this.name;
  }

  /**
   * @return
   */
  public String getPath() {
    return this.path;
  }

  public String getUpdateTime() {
    return this.updateTime;
  }

  public boolean isLeaf() {
    return !this.path.endsWith("/");
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setUpdateTime(String date) {
    this.updateTime = date;
  }

}
