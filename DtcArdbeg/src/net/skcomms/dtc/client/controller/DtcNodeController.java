package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcNodeView;

public class DtcNodeController implements DtcNodeObserver {
  private final static DtcNodeController instance = new DtcNodeController();

  public static DtcNodeController getInstance() {
    return DtcNodeController.instance;
  }

  private final DtcNodeModel dtcNodeModel = DtcNodeModel.getInstance();
  private final DtcNodeView dtcNodeView = DtcNodeView.getInstace();

  private DtcNodeController() {
    initialize();
  }

  private void initialize() {
    dtcNodeModel.addObserver(this);
  }

  @Override
  public void onFavoriteNodeListChanged() {
    dtcNodeView.setDtcFavoriteNodeWidget(dtcNodeModel.getFavoriteNodeList());
  }

  @Override
  public void onNodeListChanged() {
    dtcNodeView.setDtcNodeWidget(dtcNodeModel.getNodeList());
  }
}
