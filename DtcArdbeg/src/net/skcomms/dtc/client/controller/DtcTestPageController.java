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

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

  private Map<String, List<String>> initialRequestParameters = null;

  private void adjustRequestInfo(DtcRequestInfoModel requestInfo) {
    if (initialRequestParameters != null) {
      // requestInfo;

      // IP
      if (initialRequestParameters.containsKey("IP")) {
        requestInfo.getIpInfo().setIpText(initialRequestParameters.get("IP").get(0));
      }

      // other
      for (DtcRequestParameterModel param : requestInfo.getParams()) {
        if (initialRequestParameters.containsKey(param.getKey())) {
          param.setValue(initialRequestParameters.get(param.getKey()).get(0));
        }
      }

      initialRequestParameters = null;
    }

  }

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView) {
    dtcArdbeg.addDtcArdbegObserver(this);
    dtcProxyUrl = dtcArdbeg.getDtcProxyUrl();
    this.dtcTestPageView = dtcTestPageView;
    initialRequestParameters = dtcArdbeg.getRequestParameters();

    dtcTestPageView.setOnReadyRequestDataObserver(new DtcTestPageViewObserver() {

      @Override
      public void onReadyRequestData() {
        dtcArdbeg.onSubmitRequestForm();
      }
    });
  }

  public void loadDtcTestPageView(DtcRequestInfoModel requestInfo) {
    adjustRequestInfo(requestInfo);
    currentPath = requestInfo.getPath();
    DtcTestPageController.this.dtcTestPageView.setRequestInfo(requestInfo);
    // GWT.log(requestInfo.toString());
    DtcTestPageController.this.dtcTestPageView.draw();
    DtcTestPageController.this.encoding = requestInfo.getEncoding();
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    loadDtcTestPageView(requestInfo);
  }

  @Override
  public void onSubmitRequestForm() {
    sendRequest();

  }

  protected void sendRequest() {
    StringBuffer requestData = new StringBuffer();
    String testURL = "c" + "=" + URL.encode(currentPath);
    String process = "process=1";
    String targetUrl = URL.encode(dtcProxyUrl + "response.html");

    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);

    requestData.append(dtcTestPageView.createRequestData());
    GWT.log("ProxyURL: " + dtcProxyUrl);
    // GWT.log("TargetURL: " + targetUrl);

    HttpRequestInfoModel httpRequestInfo = new HttpRequestInfoModel();
    httpRequestInfo.setHttpMethod("POST");
    httpRequestInfo.setUrl(targetUrl);
    httpRequestInfo.setRequestData(requestData.toString());
    httpRequestInfo.setEncoding(encoding);
    DtcTestPageController.this.dtcTestPageView.chronoStart();

    DtcService.Util.getInstance().getDtcTestPageResponse(httpRequestInfo,
        new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            dtcTestPageView.chronoStop();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            GWT.log("Success: " + result);
            String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").
                replaceAll("\\]\\]>", "");
            GWT.log(convertedHTML);
            dtcTestPageView.chronoStop();
            dtcTestPageView.setHTMLData(convertedHTML);
          }
        });
  }
}
