package net.skcomms.dtc.client.controller;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
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

  private SearchHistoryController lastRequestLoader;

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

  private Map<String, List<String>> initialRequestParameters = null;

  private void adjustRequestInfo(DtcRequestInfoModel requestInfo) {
    if (this.initialRequestParameters != null) {
      this.adjustRequestInfoByInitialParameters(requestInfo);
      this.initialRequestParameters = null;
    }
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

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView,
      SearchHistoryController lastRequestLoader) {
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

    // TODO : return type이 String이 아닌, Response 정보(웅답시간, 성공여부 등)를 담고있는 클래스 객체로
    // 바꾸자.
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
            DtcTestPageController.this.lastRequestLoader.persist(
                DtcTestPageController.this.currentPath, requestParam);
          }
        });
  }
}
