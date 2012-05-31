package net.skcomms.dtc.client.controller;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class DtcTestPageController extends DefaultDtcArdbegObserver {

  private DtcTestPageView dtcTestPageView;

  private String currentPath;

  private String dtcProxyUrl;

  private String encoding;

  protected void getResponse(Response response) {

    GWT.log(response.getText());
    String rawUrl = response.getText();
    RegExp regExp = RegExp.compile("src=\"([^\"]*)");
    MatchResult match = regExp.exec(rawUrl);
    String responseUrl = match.getGroup(0);
    GWT.log("responseUrl: " + responseUrl);

    if (responseUrl == null) {
      DtcTestPageController.this.dtcTestPageView.setHTMLData(response.getText());
    }
    else {
      RequestBuilder resultRequest = new RequestBuilder(RequestBuilder.GET,
          this.dtcProxyUrl + responseUrl.split("/")[1]);
      resultRequest.setHeader("Content-Type", "text/html; charset="
          + DtcTestPageController.this.encoding);
      resultRequest.setCallback(new RequestCallback() {

        @Override
        public void onError(Request request, Throwable exception) {
          GWT.log(exception.getMessage());
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
          String result = response.getText();
          GWT.log(result);
          String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").
              replaceAll("\\]\\]>", "");
          GWT.log(convertedHTML);

          DtcTestPageController.this.dtcTestPageView.setHTMLData(convertedHTML);
        }
      });

      try {
        resultRequest.send();
      } catch (RequestException e) {
        e.printStackTrace();
      }
    }
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
    RequestBuilder request = new RequestBuilder(RequestBuilder.POST, targetURL);

    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
    request.setRequestData(requestData.toString());
    request.setCallback(new RequestCallback() {

      @Override
      public void onError(Request request, Throwable exception) {
        GWT.log(exception.getMessage());
      }

      @Override
      public void onResponseReceived(Request request, Response response) {

        DtcTestPageController.this.getResponse(response);
      }
    });

    try {
      request.send();
    } catch (RequestException e) {
      e.printStackTrace();
    }
  }

}
