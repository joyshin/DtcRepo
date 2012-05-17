/**
 * 
 */
package net.skcomms.dtc.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author jujang@sk.com
 */
public class DtcUrlCopyDialogBoxView {

  private DialogBox dialogBox = new DialogBox();

  private TextArea urlTextArea = new TextArea();

  public DtcUrlCopyDialogBoxView() {
    this.initializeDialogBox();
  }

  public void initializeDialogBox() {
    this.urlTextArea.addStyleName("dtc-line-wrap");
    this.urlTextArea.setHeight("200px");
    this.urlTextArea.setWidth("550px");

    this.dialogBox.setText("Copy to Clipboard");
    this.dialogBox.setAnimationEnabled(true);

    final Button closeButton = new Button("Close");
    closeButton.getElement().setId("closeButton");

    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(this.urlTextArea);
    dialogVPanel.add(closeButton);
    dialogVPanel.addStyleName("dialogVPanel");

    this.dialogBox.setWidget(dialogVPanel);

    closeButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        DtcUrlCopyDialogBoxView.this.dialogBox.hide();
      }
    });
  }

  public void showUrlText(String url) {
    this.urlTextArea.setText(URL.encode(url));
    this.dialogBox.center();
    this.urlTextArea.selectAll();
  }

}