package net.skcomms.dtc.client.view;

import java.util.List;

import net.skcomms.dtc.shared.DtcNodeMetaModel;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DtcNodeView {
  private final static DtcNodeView instance = new DtcNodeView();
  private FlowPanel dtcNodePanel = null;
  private FlowPanel dtcFavoriteNodePanel = null;

  private static CellList<DtcNodeMetaModel> dtcNodeCellList = null;
  private static CellList<DtcNodeMetaModel> dtcFavoriteNodeCellList = null;

  private static Label dtcNodePanelLabel = null;
  private static Label dtcFavoriteNodePanelLabel = null;

  private final static String NODE_PANEL_LABEL = "Services";
  private final static String FAVORITE_NODE_PANEL_LABEL = "Favorites";

  public static DtcNodeView getInstace() {
    return instance;
  }

  private DtcNodeView() {
  }

  public Widget getDtcFavoriteNodePanel() {
    return dtcFavoriteNodePanel;
  }

  public Widget getDtcFavoriteNodePanelLabel() {
    return dtcFavoriteNodePanelLabel;
  }

  public Widget getDtcNodePanel() {
    return dtcNodePanel;
  }

  public Widget getDtcNodePanelLabel() {
    return dtcNodePanelLabel;
  }

  public void initialize() {
    dtcNodeCellList = new CellList<DtcNodeMetaModel>(DtcNodeMetaCellView.getInstance());
    dtcFavoriteNodeCellList = new CellList<DtcNodeMetaModel>(DtcNodeMetaCellView.getInstance());

    dtcNodePanel = new FlowPanel();
    dtcNodePanel.add(dtcNodeCellList);
    dtcFavoriteNodePanel = new FlowPanel();
    dtcFavoriteNodePanel.add(dtcFavoriteNodeCellList);

    dtcNodePanelLabel = new Label();
    dtcNodePanelLabel.setText(NODE_PANEL_LABEL);
    dtcFavoriteNodePanelLabel = new Label();
    dtcFavoriteNodePanelLabel.setText(FAVORITE_NODE_PANEL_LABEL);
  }

  public void setDtcFavoriteNodeWidget(List<DtcNodeMetaModel> list) {
    dtcFavoriteNodeCellList.setRowData(list);
    dtcFavoriteNodeCellList.setRowCount(list.size(), true);
  }

  public void setDtcNodeWidget(List<DtcNodeMetaModel> list) {
    dtcNodeCellList.setRowData(list);
    dtcNodeCellList.setRowCount(list.size(), true);
  }
}
