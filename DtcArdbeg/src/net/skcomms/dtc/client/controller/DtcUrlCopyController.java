package net.skcomms.dtc.client.controller;

import java.util.List;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;
import net.skcomms.dtc.shared.DtcRequestParameter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;

public class DtcUrlCopyController {

  private DtcArdbeg module;

  private DtcUrlCopyDialogBoxView dialogBox;

  private DtcUrlCopyButtonView button;

  private DtcTestPageView dtcTestPageView;

  private void appendQueryString(StringBuilder sb) {
    String path = this.module.getCurrentPath();
    if (path.equals("/")) {
      return;
    }

    sb.append("?path=");
    sb.append(URL.encode(path));

    if (!path.endsWith("/")) {
      this.appendRequestParameters(sb);
    }
  }

  private void appendRequestParameters(StringBuilder sb) {
    List<DtcRequestParameter> params = this.dtcTestPageView.getRequestParameters();
    for (DtcRequestParameter param : params) {
      sb.append('&');
      sb.append(param.getKey());
      sb.append('=');
      sb.append(param.getValue() == null ? "" : URL.encode(param.getValue()));
    }
  }

  private String combineUrl() {
    StringBuilder sb = new StringBuilder();

    String href = this.module.getHref();
    if (href.indexOf('?') == -1) {
      sb.append(href);
    } else {
      sb.append(href.substring(0, href.indexOf('?')));
    }
    this.appendQueryString(sb);
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
