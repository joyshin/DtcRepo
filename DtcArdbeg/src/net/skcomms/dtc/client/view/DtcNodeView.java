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

  private CellList<DtcNodeMetaModel> dtcNodeCellList = null;

  private String containerName = "";

  public DtcNodeView() {
  }

  public Widget getDtcNodePanel() {
    return this.dtcNodePanel;
  }

  public void initialize(String label, String containerName) {
    this.dtcNodeCellList = new CellList<DtcNodeMetaModel>(DtcNodeMetaCellView.getInstance());

    this.dtcNodePanel = new FlowPanel();
    this.dtcNodePanel.add(this.dtcNodeCellList);

    Label dtcNodePanelLabel = new Label(label);
    RootPanel.get(containerName).add(dtcNodePanelLabel);
    RootPanel.get(containerName).add(this.dtcNodePanel);

    this.containerName = containerName;
  }

  public void setDtcNodeWidget(List<DtcNodeMetaModel> list) {
    this.dtcNodeCellList.setRowData(list);
    this.dtcNodeCellList.setRowCount(list.size(), true);
  }

  public void setVisible(boolean visible) {
    RootPanel.get(this.containerName).setVisible(visible);
  }
}
