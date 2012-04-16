package net.skcomms.dtc.client;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DtcUrlCopyHelper {

  private DtcArdbeg module;

  private final DialogBox dialogBox = new DialogBox();
  private final TextArea textUrl = new TextArea();

  public Button linkCopyButton = new Button();

  public void initialize(DtcArdbeg dtcArdbeg) {
    this.module = dtcArdbeg;
    this.initializeDialogBox();
    this.initializeLinkCopyButton();
    RootPanel.get("linkCopyContainer").add(this.linkCopyButton);
  }

  private void initializeDialogBox() {
    this.textUrl.addStyleName("dtc-line-wrap");
    this.textUrl.setHeight("200px");
    this.textUrl.setWidth("550px");

    this.dialogBox.setText("Copy to Clipboard");
    this.dialogBox.setAnimationEnabled(true);

    final Button closeButton = new Button("Close");
    closeButton.getElement().setId("closeButton");

    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(this.textUrl);
    dialogVPanel.add(closeButton);
    dialogVPanel.addStyleName("dialogVPanel");

    this.dialogBox.setWidget(dialogVPanel);

    closeButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        DtcUrlCopyHelper.this.dialogBox.hide();
      }
    });
  }

  private void initializeLinkCopyButton() {
    this.linkCopyButton.setText("Copy link");
    this.linkCopyButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        StringBuilder sb = new StringBuilder();

        if (Window.Location.getHref().indexOf('?') == -1) {
          sb.append(Window.Location.getHref());
        } else {
          sb.append(Window.Location.getHref().substring(0, Window.Location.getHref().indexOf('?')));
        }

        if (DtcUrlCopyHelper.this.module.getDtcFrameSrc().indexOf('?') != -1) {
          sb.append(DtcUrlCopyHelper.this.module.getDtcFrameSrc().substring(
              DtcUrlCopyHelper.this.module.getDtcFrameSrc().indexOf('?')));
        }

        Map<String, String> params = DtcUrlCopyHelper.this.module.getDtcRequestParameters();
        for (Entry<String, String> entry : params.entrySet()) {
          sb.append('&');
          sb.append(entry.getKey());
          sb.append('=');
          sb.append(entry.getValue());
        }

        DtcUrlCopyHelper.this.textUrl.setText(URL.encode(sb.toString()));
        DtcUrlCopyHelper.this.dialogBox.center();
        DtcUrlCopyHelper.this.textUrl.selectAll();
      }
    });
  }
}