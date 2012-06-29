package net.skcomms.dtc.client.controller;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcActivityIndicatorObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.DtcRequestParameterModel;
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

  private Map<String, List<String>> initialRequestParameters = null;

  private DtcActivityIndicatorObserver dtcActivityIndicatorObserver;

  private void adjustRequestInfo(DtcRequestInfoModel requestInfo) {
    if (this.initialRequestParameters != null && this.initialRequestParameters.size() > 0) {
      this.adjustRequestInfoByInitialParameters(requestInfo);
    } else {
      this.adjustRequestInfoByLastRequest(requestInfo);
    }
    this.initialRequestParameters = null;
  }

  private void adjustRequestInfoByInitialParameters(DtcRequestInfoModel requestInfo) {
    // IP
    if (this.initialRequestParameters.containsKey("IP")) {
      requestInfo.getIpInfo().setIpText(this.initialRequestParameters.get("IP").get(0));
    }

    // other
    for (DtcRequestParameterModel param : requestInfo.getParams()) {
      if (this.initialRequestParameters.containsKey(param.getKey())) {
        param.setValue(this.initialRequestParameters.get(param.getKey()).get(0));
      }
    }
  }

  private void adjustRequestInfoByLastRequest(DtcRequestInfoModel requestInfo) {
    final String lastRequestLoaderKey = "c" + "=" + requestInfo.getPath();
    GWT.log("requestLoader key: " + lastRequestLoaderKey);

    boolean lastRequestExists =
        DtcTestPageController.this.lastRequestLoader.recall(lastRequestLoaderKey);

    GWT.log("requestExists: " + lastRequestExists);

    if (lastRequestExists) {
      DtcTestPageController.this.lastRequestLoader.loadLastRequest(requestInfo);
    }
  }

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView,
      LastRequestLoaderController lastRequestLoader) {
    dtcArdbeg.addDtcArdbegObserver(this);
    this.dtcProxyUrl = dtcArdbeg.getDtcProxyUrl();
    this.dtcTestPageView = dtcTestPageView;
    this.lastRequestLoader = lastRequestLoader;
    this.initialRequestParameters = dtcArdbeg.getRequestParameters();

    dtcTestPageView.setOnReadyRequestDataObserver(new DtcTestPageViewObserver() {

      @Override
      public void onReadyRequestData() {
        dtcArdbeg.onSubmitRequestForm();
      }
    });
  }

  public void loadDtcTestPageView(DtcRequestInfoModel requestInfo) {
    this.adjustRequestInfo(requestInfo);
    this.currentPath = requestInfo.getPath();

    DtcTestPageController.this.dtcTestPageView.setRequestInfo(requestInfo);
    DtcTestPageController.this.dtcTestPageView.draw();
    DtcTestPageController.this.encoding = requestInfo.getEncoding();
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.loadDtcTestPageView(requestInfo);
  }

  @Override
  public void onSubmitRequestForm() {
    this.dtcActivityIndicatorObserver.onShow();
    this.sendRequest();
  }

  protected void sendRequest() {
    StringBuilder requestData = new StringBuilder();
    final String testURL = "c" + "=" + URL.encode(this.currentPath);
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

    // FIXME HttpRequestInfoModel이 DtcRequestParams로 더 추상화되거나 GWT RPC를 쓰지 않고 일반
    // http 요청을 하자. 어느 쪽이든지 아래 비동기 요청은 Model의 역할이니 옮겨주자.

    DtcService.Util.getInstance().getDtcTestPageResponse(httpRequestInfo,
        new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            DtcTestPageController.this.dtcTestPageView.chronoStop();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());

            DtcTestPageController.this.dtcActivityIndicatorObserver.onHide();
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
            // FIXME 키 생성방법을 은닉시킬 것.
            String lastRequestKey = "c" + "=" + DtcTestPageController.this.currentPath;
            // FIXME 한 개 메써드로 합치자.
            DtcTestPageController.this.lastRequestLoader.createLastRequest(lastRequestKey,
                requestParam);
            DtcTestPageController.this.lastRequestLoader.persist();

            DtcTestPageController.this.dtcActivityIndicatorObserver.onHide();
          }
        });
  }

  public void setOnIndicatorActivityObserver(
      DtcActivityIndicatorObserver cb) {
    this.dtcActivityIndicatorObserver = cb;
  }
}
