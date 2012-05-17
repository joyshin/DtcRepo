/**
 * 
 */
package net.skcomms.dtc.client.view;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author jujang@sk.com
 */
public class DtcUrlCopyButtonView {

  private Button linkCopyButton = new Button();

  public DtcUrlCopyButtonView() {
    this.initializeLinkCopyButton();
  }

  public void addUrlCopyButtonClickHandler(ClickHandler handler) {
    this.linkCopyButton.addClickHandler(handler);
  }

  private void initializeLinkCopyButton() {
    RootPanel.get("linkCopyContainer").add(this.linkCopyButton);
    this.linkCopyButton.setText("Copy link");
  }

}