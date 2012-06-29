package net.skcomms.dtc.client.controller;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.model.DtcTestPageModel;
import net.skcomms.dtc.client.model.DtcTestPageResponse;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.DtcRequestParameterModel;
import net.skcomms.dtc.shared.HttpRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class DtcTestPageController implements DtcNodeObserver, DtcTestPageModelObserver {

  private DtcTestPageView testPageView;

  private DtcTestPageModel testPageModel;

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

  private HttpRequestInfoModel createHttpRequestInfo() {
    StringBuilder requestData = new StringBuilder();
    final String testURL = "c" + "=" + URL.encode(DtcTestPageController.this.currentPath);
    String process = "process=1";
    String targetUrl = URL.encode(DtcTestPageController.this.dtcProxyUrl + "response.html");

    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);

    requestData.append(DtcTestPageController.this.testPageView.createRequestData());
    GWT.log("ProxyURL: " + DtcTestPageController.this.dtcProxyUrl);
    // GWT.log("TargetURL: " + targetUrl);

    HttpRequestInfoModel httpRequestInfo = new HttpRequestInfoModel();
    httpRequestInfo.setRequestParameter(this.testPageView.getRequestParameter());
    httpRequestInfo.setPath(DtcTestPageController.this.currentPath);
    httpRequestInfo.setHttpMethod("POST");
    httpRequestInfo.setUrl(targetUrl);
    httpRequestInfo.setRequestData(requestData.toString());
    httpRequestInfo.setEncoding(DtcTestPageController.this.encoding);
    return httpRequestInfo;
  }

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView,
      DtcSearchHistoryController lastRequestLoader, DtcNodeModel nodeModel,
      DtcTestPageModel testPageModel) {
    this.dtcProxyUrl = dtcArdbeg.getDtcProxyUrl();
    this.testPageView = dtcTestPageView;
    this.initialRequestParameters = dtcArdbeg.getRequestParameters();
    this.testPageModel = testPageModel;
    testPageModel.addObserver(this);
    nodeModel.addObserver(this);

    dtcTestPageView.setOnReadyRequestDataObserver(new DtcTestPageViewObserver() {

      @Override
      public void onReadyRequestData() {
        HttpRequestInfoModel httpRequestInfo = DtcTestPageController.this.createHttpRequestInfo();
        DtcTestPageController.this.testPageModel.sendRequest(httpRequestInfo);
        DtcTestPageController.this.testPageView.chronoStart();
      }
    });
  }

  public void loadDtcTestPageView(DtcRequestInfoModel requestInfo) {
    this.adjustRequestInfo(requestInfo);
    this.currentPath = requestInfo.getPath();

    DtcTestPageController.this.testPageView.setRequestInfo(requestInfo);
    DtcTestPageController.this.testPageView.draw();
    DtcTestPageController.this.encoding = requestInfo.getEncoding();
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.loadDtcTestPageView(requestInfo);
  }

  @Override
  public void onFavoriteNodeListChanged() {
  }

  @Override
  public void onNodeListChanged() {
  }

  @Override
  public void onRequestFailed(Throwable caught) {
    DtcTestPageController.this.testPageView.chronoStop();
    // DtcTestPageController.this.testPageView.setHTMLData(caught.getMessage());
  }

  @Override
  public void onTestPageResponseReceived(DtcTestPageResponse response) {
    GWT.log("Success: " + response.getResult());
    this.redrawTestPageView(response.getResult());
  }

  private void redrawTestPageView(String result) {
    String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").
        replaceAll("\\]\\]>", "");
    GWT.log(convertedHTML);
    DtcTestPageController.this.testPageView.setHTMLData(convertedHTML);
    DtcTestPageController.this.testPageView.chronoStop();
  }

}
