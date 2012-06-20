package net.skcomms.dtc.client.controller;

import java.util.Map;

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
  private LastRequestLoaderController lastRequestLoader;

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

  public void initialize(final DtcArdbeg dtcArdbeg,
      DtcTestPageView dtcTestPageView,
      LastRequestLoaderController lastRequestLoader) {

    dtcArdbeg.addDtcArdbegObserver(this);
    this.dtcProxyUrl = dtcArdbeg.getDtcProxyUrl();
    this.dtcTestPageView = dtcTestPageView;
    this.lastRequestLoader = lastRequestLoader;

    dtcTestPageView.setOnReadyRequestDataObserver(new DtcTestPageViewObserver() {

      @Override
      public void onReadyRequestData() {
        dtcArdbeg.onSubmitRequestForm();
      }
    });
  }

  public void loadDtcTestPageView(DtcRequestInfoModel requestInfo) {

    this.currentPath = requestInfo.getPath();
    final String lastRequestLoaderKey = "c" + "=" + this.currentPath;
    GWT.log("requestLoader key: " + lastRequestLoaderKey);

    boolean lastRequestExists =
        DtcTestPageController.this.lastRequestLoader.recall(lastRequestLoaderKey);

    GWT.log("requestExists: " + lastRequestExists);

    if (lastRequestExists) {
      DtcTestPageController.this.lastRequestLoader.loadLastRequest(requestInfo);
    }
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
    final String testURL = "c" + "=" + URL.encode(this.currentPath);
    final String lastRequestKey = "c" + "=" + this.currentPath;
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
    DtcTestPageController.this.dtcTestPageView.chronoStart();

    DtcService.Util.getInstance().getDtcTestPageResponse(httpRequestInfo,
        new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            DtcTestPageController.this.dtcTestPageView.chronoStop();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            GWT.log("Success: " + result);
            String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").
                replaceAll("\\]\\]>", "");
            GWT.log(convertedHTML);
            DtcTestPageController.this.dtcTestPageView.chronoStop();
            DtcTestPageController.this.dtcTestPageView.setHTMLData(convertedHTML);

            Map<String, String> requestParam =
                DtcTestPageController.this.dtcTestPageView.getRequestParameter();
            DtcTestPageController.this.lastRequestLoader.createLastRequest(lastRequestKey,
                requestParam);
            DtcTestPageController.this.lastRequestLoader.persist();
          }
        });
  }
}
