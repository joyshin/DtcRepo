package net.skcomms.dtc.client.model;

import com.smartgwt.client.widgets.tree.TreeNode;

public class DtcServiceTreeNode extends TreeNode {

  public DtcServiceTreeNode(int serviceId, int parentServiceId, String name, String path,
      String description, String updateTime) {
    this.setServiceId(serviceId);
    this.setParentId(parentServiceId);
    this.setName(name);
    this.setPath(path);
    this.setDescription(description);
    this.setUpdateTime(updateTime);
  }

  private void setDescription(String description) {
    this.setAttribute("Description", description);
  }

  @Override
  public void setName(String name) {
    this.setAttribute("Name", name);
  }

  public void setParentId(int value) {
    this.setAttribute("ParentServiceId", value);
  }

  private void setPath(String path) {
    this.setAttribute("Path", path);
  }

  public void setServiceId(int value) {
    this.setAttribute("ServiceId", value);
  }

  private void setUpdateTime(String updateTime) {
    this.setAttribute("UpdateTime", updateTime);
  }
}
