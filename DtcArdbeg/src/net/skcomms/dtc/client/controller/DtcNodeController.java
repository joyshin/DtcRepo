package net.skcomms.dtc.client.controller;

import java.util.List;

import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcNodeMetaCellView;
import net.skcomms.dtc.shared.DtcNodeMetaModel;

import com.google.gwt.user.cellview.client.CellList;

public class DtcNodeController implements DtcNodeObserver {
  private final static DtcNodeController instance = new DtcNodeController();

  public static final CellList<DtcNodeMetaModel> dtcNodeCellList = new CellList<DtcNodeMetaModel>(
      DtcNodeMetaCellView.getInstance());

  public static final CellList<DtcNodeMetaModel> dtcFavoriteNodeCellList = new CellList<DtcNodeMetaModel>(
      DtcNodeMetaCellView.getInstance());

  public static DtcNodeController getInstance() {
    return DtcNodeController.instance;
  }

  private final DtcNodeModel dtcNodeModel = DtcNodeModel.getInstance();

  private DtcNodeController() {
    initialize();
  }

  public CellList<DtcNodeMetaModel> getDtcFavoriteNodeCellList() {
    return DtcNodeController.dtcFavoriteNodeCellList;
  }

  public CellList<DtcNodeMetaModel> getDtcNodeCellList() {
    return DtcNodeController.dtcNodeCellList;
  }

  private void initialize() {
    dtcNodeModel.addObserver(this);
  }

  @Override
  public void onFavoriteNodeListChanged() {
    setDtcFavoriteNodeCellList(dtcNodeModel.getFavoriteNodeList());
  }

  @Override
  public void onNodeListChanged() {
    setDtcNodeCellList(dtcNodeModel.getNodeList());
  }

  private void setDtcFavoriteNodeCellList(List<DtcNodeMetaModel> list) {
    dtcFavoriteNodeCellList.setRowData(list);
    dtcFavoriteNodeCellList.setRowCount(list.size(), true);
  }

  private void setDtcNodeCellList(List<DtcNodeMetaModel> list) {
    dtcNodeCellList.setRowData(list);
    dtcNodeCellList.setRowCount(list.size(), true);
  }
}
