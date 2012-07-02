package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DefaultDtcTestPageViewObserver;
import net.skcomms.dtc.client.DtcArdbegObserver;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.user.client.ui.RootPanel;

public class DtcActivityIndicatorController extends DefaultDtcTestPageViewObserver implements
    DtcArdbegObserver {

  @Override
  public void onDtcDirectoryLoaded(String path) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDtcHomeLoaded() {
    RootPanel.get("loading").setVisible(false);
  }

  @Override
  public void onDtcResponseFrameLoaded(boolean success) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSearchStart() {
    RootPanel.get("loading").setVisible(true);
  }

  @Override
  public void onSearchStop() {
    RootPanel.get("loading").setVisible(false);
  }
}
