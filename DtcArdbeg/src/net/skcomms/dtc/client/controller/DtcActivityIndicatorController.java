package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DtcActivityIndicatorObserver;
import net.skcomms.dtc.client.DtcArdbeg;

import com.google.gwt.user.client.ui.RootPanel;

public class DtcActivityIndicatorController {

  DtcActivityIndicatorObserver indicator = new DtcActivityIndicatorObserver() {

    @Override
    public void onHide() {
      RootPanel.get("loading").setVisible(false);
    }

    @Override
    public void onShow() {
      RootPanel.get("loading").setVisible(true);
    }
  };

  public void initialize(DtcArdbeg dtcArdbeg, DtcTestPageController dtcTestPageController) {
    dtcTestPageController.setOnIndicatorActivityObserver(this.indicator);
    dtcArdbeg.setOnIndicatorActivityObserver(this.indicator);
  }
}
