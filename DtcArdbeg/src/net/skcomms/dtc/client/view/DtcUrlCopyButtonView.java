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

  protected Button linkCopyButton = this.createButton();

  public DtcUrlCopyButtonView() {
    this.initializeLinkCopyButton();
  }

  public void addUrlCopyButtonClickHandler(ClickHandler handler) {
    this.linkCopyButton.addClickHandler(handler);
  }

  protected Button createButton() {
    return new Button();
  }

  protected void initializeLinkCopyButton() {
    RootPanel.get("linkCopyContainer").add(this.linkCopyButton);
    this.linkCopyButton.setHTML("<img src='Copy-icon.png' width='30' height='30'>");
    this.linkCopyButton.addStyleDependentName("copyLink");
  }

}