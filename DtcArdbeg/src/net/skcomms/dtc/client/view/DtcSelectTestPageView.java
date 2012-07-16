package net.skcomms.dtc.client.view;

import java.util.List;

import net.skcomms.dtc.client.model.DtcServiceTreeNode;

import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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

    this.setWidth(800);
    this.setHeight(600);
    this.setTitle("Select Test Page Window");
    this.setShowMinimizeButton(false);
    this.setIsModal(true);
    this.setShowModalMask(true);
    this.centerInPage();
    this.setDismissOnOutsideClick(true);

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

  }

  public void setTreeNodeData(List<TreeNode> nodes) {

    DtcServiceTreeNode[] serviceList = new DtcServiceTreeNode[nodes.size()];
    nodes.toArray(serviceList);

    this.treeGrid = new TreeGrid();
    this.treeGrid.setWidth100();
    this.treeGrid.setHeight(400);

    // this.treeGrid.setFolderIcon(this.treeGrid.getNodeIcon());

    // GWT.log(this.treeGrid.getNodeIcon());
    this.treeGrid.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

      }

    });

    this.field = new TreeGridField("Name");
    this.field.setCanSort(false);
    this.treeGrid.setFields(this.field);

    this.tree.setModelType(TreeModelType.PARENT);
    this.tree.setNameProperty("Name");
    this.tree.setIdField("ServiceId");
    this.tree.setParentIdField("ParentServiceId");
    this.tree.setShowRoot(false);

    this.tree.setData(serviceList);
    this.treeGrid.setData(this.tree);

    this.treeGrid.addDrawHandler(new DrawHandler() {
      @Override
      public void onDraw(DrawEvent event) {
        // DtcSelectTestPageView.this.tree.openAll();
        DtcSelectTestPageView.this.tree.openFolder(DtcSelectTestPageView.this.tree
            .find("Root/kadcpts"));
        DtcSelectTestPageView.this.tree.getParents(DtcSelectTestPageView.this.tree
            .find("Root/kadcpts"));

        DtcSelectTestPageView.this.tree.openFolders(DtcSelectTestPageView.this.tree
            .getParents(DtcSelectTestPageView.this.tree
                .find("Root/kadcpts")));

      }
    });

    this.serviceListLayout.addMember(this.treeGrid);
    this.treeGrid.draw();
  }
}
