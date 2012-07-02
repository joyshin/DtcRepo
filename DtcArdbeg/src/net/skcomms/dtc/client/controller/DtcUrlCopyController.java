package net.skcomms.dtc.client.controller;

import java.util.Map;
import java.util.Map.Entry;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;

public class DtcUrlCopyController {

  private DtcArdbeg module;

  private DtcUrlCopyDialogBoxView dialogBox;

  private DtcUrlCopyButtonView button;

  private DtcTestPageView dtcTestPageView;

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
      sb.append("?path=");
      sb.append(URL.encode(this.module.getCurrentPath()));
    } else {
      sb.append("?path=");
      sb.append(URL.encode(this.module.getCurrentPath()));
      Map<String, String> params = this.dtcTestPageView.getRequestParameters();
      for (Entry<String, String> entry : params.entrySet()) {
        sb.append('&');
        sb.append(entry.getKey());
        sb.append('=');
        sb.append(URL.encode(entry.getValue()));
      }
    }

    System.out.println(sb.toString());
    return sb.toString();
  }

  public void initialize(DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView,
      DtcUrlCopyButtonView aButton, DtcUrlCopyDialogBoxView aDialogBox) {
    this.module = dtcArdbeg;
    this.dtcTestPageView = dtcTestPageView;
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