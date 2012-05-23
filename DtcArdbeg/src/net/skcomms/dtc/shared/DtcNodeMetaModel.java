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
public class DtcNodeMetaModel implements Serializable {

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

  public boolean isDirectoryPageNode() {
    int idx = this.path.indexOf("/", 1);

    if (0 < idx && idx < (this.path.length() - 1)) {
      return true;
    }
    return false;
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
