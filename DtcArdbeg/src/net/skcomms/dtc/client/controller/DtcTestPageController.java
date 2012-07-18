package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.DtcTestPageControllerObserver;
import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.client.model.DtcTestPageModel;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameter;

import com.google.gwt.core.client.GWT;

public class DtcTestPageController implements DtcNodeObserver, DtcTestPageModelObserver,
    DtcTestPageViewObserver {

  private DtcTestPageView testPageView;

  private DtcTestPageModel testPageModel;

  private Map<String, List<String>> initialRequestParameters = null;

  private final List<DtcTestPageControllerObserver> dtcTestPageControllerObservers = new ArrayList<DtcTestPageControllerObserver>();

  private DtcRequestMeta requestMeta;

  public void addObserver(DtcTestPageControllerObserver observer) {
    this.dtcTestPageControllerObservers.add(observer);
  }

  private void adjustRequestInfo(DtcRequestMeta requestInfo) {
    if (this.initialRequestParameters != null && this.initialRequestParameters.size() > 0) {
      this.adjustRequestInfoByInitialParameters(requestInfo);
      this.initialRequestParameters = null;
    }
  }

  private void adjustRequestInfoByInitialParameters(DtcRequestMeta requestInfo) {
    // IP
    if (this.initialRequestParameters.containsKey("IP")) {
      requestInfo.getIpInfo().setIpText(this.initialRequestParameters.get("IP").get(0));
    }

    // other
    for (DtcRequestParameter param : requestInfo.getParams()) {
      if (this.initialRequestParameters.containsKey(param.getKey())) {
        param.setValue(this.initialRequestParameters.get(param.getKey()).get(0));
      }
    }
  }

  private DtcRequest createDtcRequest() {
    DtcRequest request = new DtcRequest();
    request.setPath(this.requestMeta.getPath());
    request.setEncoding(this.requestMeta.getEncoding());

    request.setAppName(this.requestMeta.getAppName());
    request.setApiNumber(this.requestMeta.getApiNumber());
    request.setCndFieldName(this.requestMeta.getCndFieldName());
    request.setQueryFieldName(this.requestMeta.getQueryFieldName());
    request.setRequestParameters(this.testPageView.getRequestParameters());

    return request;
  }

  public void initialize(final DtcArdbeg dtcArdbeg, DtcTestPageView dtcTestPageView,
      DtcNodeModel nodeModel, DtcTestPageModel testPageModel) {
    this.testPageView = dtcTestPageView;
    this.initialRequestParameters = dtcArdbeg.getRequestParameters();
    this.testPageModel = testPageModel;

    testPageModel.addObserver(this);
    nodeModel.addObserver(this);
    this.testPageView.addObserver(this);
  }

  public void loadDtcTestPageView(DtcRequestMeta requestMeta) {
    this.adjustRequestInfo(requestMeta);
    this.requestMeta = requestMeta;

    this.testPageView.setRequestMeta(requestMeta);
    this.testPageView.draw();
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestMeta requestMeta) {
    this.loadDtcTestPageView(requestMeta);
  }

  @Override
  public void onFavoriteNodeListChanged() {
  }

  @Override
  public void onNodeListChanged() {
  }

  @Override
  public void onReadyRequestData() {
    this.onSearchStart();
    DtcRequest request = DtcTestPageController.this.createDtcRequest();
    DtcTestPageController.this.testPageModel.sendRequest(request);
    DtcTestPageController.this.testPageView.chronoStart();
  }

  @Override
  public void onRequestFailed(Throwable caught) {
    DtcTestPageController.this.testPageView.chronoStop();
    DtcTestPageController.this.testPageView.setHTMLData(caught.getMessage());
    this.onSearchStop();
  }

  private void onSearchStart() {
    for (DtcTestPageControllerObserver observer : this.dtcTestPageControllerObservers) {
      observer.onSearchStart();
    }
  }

  private void onSearchStop() {
    for (DtcTestPageControllerObserver observer : this.dtcTestPageControllerObservers) {
      observer.onSearchStop();
    }
  }

  @Override
  public void onTestPageResponseReceived(DtcResponse response) {
    GWT.log("Success: " + response.getResult());
    this.onSearchStop();
    this.testPageView.chronoStop();
    this.redrawTestPageView(response.getResult());
  }

  private void redrawTestPageView(String result) {
    String convertedHTML = result.replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", "");
    GWT.log(convertedHTML);
    this.testPageView.setHTMLData(convertedHTML);
  }

}
