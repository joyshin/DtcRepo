package net.skcomms.dtc.client.view;

import java.util.List;

import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DtcSelectTestPageView extends Window {

  private VLayout backgroundLayout;
  private HLayout serviceLayout;
  private VLayout serviceListLayout;

  private TreeGrid treeGrid;
  private TreeGridField field;
  private Tree tree = new Tree();

  public DtcSelectTestPageView() {
    this.backgroundLayout = new VLayout();
    this.backgroundLayout.setHeight100();
    this.backgroundLayout.setWidth100();
    // this.backgroundLayout.setShowEdges(true);

    this.serviceLayout = new HLayout();
    this.serviceLayout.setWidth100();
    this.serviceLayout.setHeight(470);
    // this.serviceLayout.setShowEdges(true);

    this.serviceListLayout = new VLayout();
    this.serviceListLayout.setHeight100();
    this.serviceListLayout.setWidth(400);
    // this.serviceListLayout.setShowEdges(true);

    this.serviceLayout.addMember(this.serviceListLayout);
    this.backgroundLayout.addMember(this.serviceLayout);
    this.addItem(this.backgroundLayout);

    // DtcServiceTreeNode root = new DtcServiceTreeNode("4", "1",
    // "Charles Madigen", "");
    // DtcServiceTreeNode node2 = new DtcServiceTreeNode("188", "4",
    // "Rogine Leger", "");
    // DtcServiceTreeNode node3 = new DtcServiceTreeNode("189", "4",
    // "Gene Porter", "");
    // DtcServiceTreeNode node4 = new DtcServiceTreeNode("265", "189",
    // "Olivier Doucet", "");
    // DtcServiceTreeNode node5 = new DtcServiceTreeNode("264", "189",
    // "Cheryl Pearson", "");
    // this.tree.setData(new TreeNode[] { root, node2, node3, node4, node5 });

  }

  public void setTreeNodeData(List<TreeNode> nodes) {
    System.out.println("aaaaaa");

    // System.out.println(serviceList.length);

    TreeNode[] serviceList = new TreeNode[nodes.size()];
    nodes.toArray(serviceList);

    this.treeGrid = new TreeGrid();
    this.treeGrid.setWidth100();
    this.treeGrid.setHeight(400);

    this.field = new TreeGridField();
    this.field.setCanSort(false);
    this.treeGrid.setFields(this.field);

    this.tree.setModelType(TreeModelType.PARENT);
    this.tree.setNameProperty("Name");
    this.tree.setIdField("ServiceId");
    this.tree.setParentIdField("ParentServiceId");
    this.tree.setShowRoot(true);

    this.tree.setData(serviceList);

    this.treeGrid.addDrawHandler(new DrawHandler() {
      @Override
      public void onDraw(DrawEvent event) {
        DtcSelectTestPageView.this.tree.openAll();
      }
    });

    this.treeGrid.setData(this.tree);
    this.serviceListLayout.addMember(this.treeGrid);
    this.treeGrid.draw();
  }

}
