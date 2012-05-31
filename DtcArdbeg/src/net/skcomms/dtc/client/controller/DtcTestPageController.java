package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class DtcTestPageController extends DefaultDtcArdbegObserver {

  private DtcTestPageView dtcTestPageView;

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

  protected void getResponse(Response response) {

  }

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView) {
    dtcArdbeg.addDtcArdbegObserver(this);
    this.dtcProxyUrl = dtcArdbeg.getDtcProxyUrl();
    this.dtcTestPageView = dtcTestPageView;

    dtcTestPageView.setOnReadyRequestDataObserver(new DtcTestPageViewObserver() {

      @Override
      public void onReadyRequestData() {
        dtcArdbeg.onSubmitRequestForm();
      }
    });
  }

  public void loadDtcTestPageView(DtcRequestInfoModel requestInfo) {
    this.currentPath = requestInfo.getPath();
    DtcTestPageController.this.dtcTestPageView.setRequestInfo(requestInfo);
    // GWT.log(requestInfo.toString());
    DtcTestPageController.this.dtcTestPageView.draw();
    DtcTestPageController.this.encoding = requestInfo.getEncoding();
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.loadDtcTestPageView(requestInfo);
  }

  @Override
  public void onSubmitRequestForm() {
    this.sendRequest();

  }

  protected void sendRequest() {
    StringBuffer requestData = new StringBuffer();
    String testURL = "c" + "=" + URL.encode(this.currentPath);
    String process = "process=1";

    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);

    requestData.append(this.dtcTestPageView.createRequestData());

    String targetURL = URL.encode(this.dtcProxyUrl + "response.html");

  }

}
