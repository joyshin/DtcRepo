package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcTestPageControllerObserver;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class DtcActivityIndicatorController extends DefaultDtcArdbegObserver implements
    DtcTestPageControllerObserver {

  // ardbeg
  @Override
  public void onDtcHomeLoaded() {
    RootPanel.get("loading").setVisible(false);
  }

  // this
  @Override
  public void onSearchStart() {
    GWT.log(this.getClass() + ": onSearchStart() called.");
    RootPanel.get("loading").setVisible(true);
  }

  @Override
  public void onSearchStop() {
    GWT.log(this.getClass() + ": onSearchStop() called.");
    RootPanel.get("loading").setVisible(false);
  }
}
