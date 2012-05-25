package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcNodeView;

public class DtcNodeController implements DtcNodeObserver {
  private final static DtcNodeController instance = new DtcNodeController();

  public static DtcNodeController getInstance() {
    return DtcNodeController.instance;
  }

  private DtcNodeModel dtcNodeModel = null;
  private DtcNodeView dtcNodeView = null;
  private DtcNodeView dtcFavoriteNodeView = null;

  private DtcNodeController() {
  }

  public void initialize(DtcNodeView nodeView, DtcNodeView favoriteNodeView) {
    dtcNodeView = nodeView;
    dtcFavoriteNodeView = favoriteNodeView;
    dtcNodeModel = DtcNodeModel.getInstance();
    dtcNodeModel.addObserver(this);
  }

  @Override
  public void onFavoriteNodeListChanged() {
    dtcFavoriteNodeView.setDtcNodeWidget(dtcNodeModel.getFavoriteNodeList());
  }

  @Override
  public void onNodeListChanged() {
    dtcNodeView.setDtcNodeWidget(dtcNodeModel.getNodeList());
  }
}
