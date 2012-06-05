/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.skcomms.dtc.server.DtcServiceImpl;

/**
 * @author jujang@sk.com
 */
@SuppressWarnings("serial")
public class DtcNodeMetaModel implements Serializable {

  private String description;

  private String name;

  private String path;

  private String updateTime;

  public DtcNodeMetaModel() {
  }

  public DtcNodeMetaModel(File file) throws IOException {
    this.name = file.getName();
    this.path = file.getPath().substring(DtcServiceImpl.getRootPath().length() - 1);
    if (file.isDirectory()) {
      this.description = "디렉토리";
    } else {
      this.description = "INI";
    }
    this.updateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(file
        .lastModified()));
  }

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

  @Override
  public String toString() {
    return "{" + this.name + ", " + this.description + ", " + this.updateTime + ", " + this.path
        + "}";
  }

}
