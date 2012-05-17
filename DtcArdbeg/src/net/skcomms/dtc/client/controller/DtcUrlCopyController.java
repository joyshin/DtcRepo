package net.skcomms.dtc.client.controller;

import java.util.Map;
import java.util.Map.Entry;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class DtcUrlCopyController {

  private DtcArdbeg module;

  private DtcUrlCopyDialogBoxView dialogBox;

  private DtcUrlCopyButtonView button;

  private String combineUrl() {
    StringBuilder sb = new StringBuilder();

    String href = this.module.getHref();
    if (href.indexOf('?') == -1) {
      sb.append(href);
    } else {
      sb.append(href.substring(0, href.indexOf('?')));
    }

    if (this.module.getDtcFrameSrc().indexOf('?') != -1) {
      sb.append(this.module.getDtcFrameSrc().substring(
          this.module.getDtcFrameSrc().indexOf('?')));
    }

    Map<String, String> params = DtcUrlCopyController.this.module.getDtcRequestParameters();
    for (Entry<String, String> entry : params.entrySet()) {
      sb.append('&');
      sb.append(entry.getKey());
      sb.append('=');
      sb.append(entry.getValue());
    }
    return sb.toString();
  }

  public void initialize(DtcArdbeg dtcArdbeg, DtcUrlCopyButtonView aButton,
      DtcUrlCopyDialogBoxView aDialogBox) {
    this.module = dtcArdbeg;
    this.button = aButton;
    this.dialogBox = aDialogBox;

    this.button.addUrlCopyButtonClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        DtcUrlCopyController.this.onClickUrlCopyButton();
      }
    });
  }

  private void onClickUrlCopyButton() {
    String url = this.combineUrl();
    this.dialogBox.showUrlText(url);
  }
}