package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.HttpRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcTestPageController extends DefaultDtcArdbegObserver {

  private DtcTestPageView dtcTestPageView;

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

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
    String targetUrl = URL.encode(this.dtcProxyUrl + "response.html");

    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);

    requestData.append(this.dtcTestPageView.createRequestData());
    GWT.log("ProxyURL: " + this.dtcProxyUrl);
    // GWT.log("TargetURL: " + targetUrl);

    HttpRequestInfoModel httpRequestInfo = new HttpRequestInfoModel();
    httpRequestInfo.setHttpMethod("POST");
    httpRequestInfo.setUrl(targetUrl);
    httpRequestInfo.setRequestData(requestData.toString());
    httpRequestInfo.setEncoding(this.encoding);
    DtcService.Util.getInstance().getDtcTestPageResponse(httpRequestInfo,
        new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            GWT.log("Success: " + result);
            String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").
                replaceAll("\\]\\]>", "");
            GWT.log(convertedHTML);

            DtcTestPageController.this.dtcTestPageView.setHTMLData(convertedHTML);
          }
        });
  }

}
