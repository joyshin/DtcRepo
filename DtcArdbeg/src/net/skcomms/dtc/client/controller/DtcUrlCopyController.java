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

    String href = module.getHref();
    if (href.indexOf('?') == -1) {
      sb.append(href);
    } else {
      sb.append(href.substring(0, href.indexOf('?')));
    }

    if (module.getCurrentPath().equals("/")) {
    }
    else if (module.getCurrentPath().endsWith("/")) {
      sb.append("?path=");
      sb.append(URL.encode(module.getCurrentPath().substring(1)));
    } else {
      sb.append("?path=");
      sb.append(URL.encode(module.getCurrentPath().substring(1)));
      Map<String, String> params = dtcTestPageView.getRequestParameter();
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
    module = dtcArdbeg;
    this.dtcTestPageView = dtcTestPageView;
    button = aButton;
    dialogBox = aDialogBox;

    button.addUrlCopyButtonClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        DtcUrlCopyController.this.onClickUrlCopyButton();
      }
    });
  }

  private void onClickUrlCopyButton() {
    String url = combineUrl();
    dialogBox.showUrlText(url);
  }
}