package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;

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

    if (this.module.getCurrentPath().equals("/")) {
    }
    else if (this.module.getCurrentPath().endsWith("/")) {
      sb.append("?b=");
    } else {
      sb.append("?c=");
    }
    sb.append(URL.encode(this.module.getCurrentPath().substring(1)));

    // TODO 현재 경로가 test page인 경우 request parameter를 추가한다.
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