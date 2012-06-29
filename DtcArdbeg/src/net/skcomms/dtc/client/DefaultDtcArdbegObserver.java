/**
 * 
 */
package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author jujang@sk.com
 */
public class DefaultDtcArdbegObserver implements DtcArdbegObserver {

  @Override
  public void onDtcDirectoryLoaded(String path) {
  }

  @Override
  public void onDtcHomeLoaded() {
  }

  @Override
  public void onDtcResponseFrameLoaded(boolean success) {
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
  }

  @Override
  public void onHide() {
    RootPanel.get("loading").setVisible(false);
  }

  @Override
  public void onShow() {
    RootPanel.get("loading").setVisible(true);
  }

  @Override
  public void onSubmitRequestForm() {

  }

}
