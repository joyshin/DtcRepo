package net.skcomms.dtc.client.view;

import java.util.List;

import net.skcomms.dtc.shared.DtcNodeMetaModel;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DtcNodeView {
  private FlowPanel dtcNodePanel = null;

  private static CellList<DtcNodeMetaModel> dtcNodeCellList = null;

  private static Label dtcNodePanelLabel = null;

  private String containerName = "";

  public DtcNodeView() {
  }

  public Widget getDtcNodePanel() {
    return dtcNodePanel;
  }

  public Widget getDtcNodePanelLabel() {
    return dtcNodePanelLabel;
  }

  public void initialize(String label, String containerName) {
    dtcNodeCellList = new CellList<DtcNodeMetaModel>(DtcNodeMetaCellView.getInstance());

    dtcNodePanel = new FlowPanel();
    dtcNodePanel.add(dtcNodeCellList);

    dtcNodePanelLabel = new Label(label);
    RootPanel.get(containerName).add(dtcNodePanelLabel);
    RootPanel.get(containerName).add(dtcNodePanel);
    this.containerName = containerName;
  }

  public void setDtcNodeWidget(List<DtcNodeMetaModel> list) {
    dtcNodeCellList.setRowData(list);
    dtcNodeCellList.setRowCount(list.size(), true);
    System.out.println(containerName + " : " + dtcNodeCellList.getVisibleItems().get(0).getName());
  }

  public void setVisible(boolean visible) {
    System.out.println(visible + " : " + containerName + " : "
        + dtcNodeCellList.getVisibleItems().isEmpty());
    RootPanel.get(containerName).setVisible(visible);
  }
}
