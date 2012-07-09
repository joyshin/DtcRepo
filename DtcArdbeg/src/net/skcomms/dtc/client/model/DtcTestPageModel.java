package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.shared.DtcRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcTestPageModel {

  List<DtcTestPageModelObserver> observers = new ArrayList<DtcTestPageModelObserver>();

  public void addObserver(DtcTestPageModelObserver observer) {
    this.observers.add(observer);
  }

  private void fireRequestFailed(Throwable caught) {
    for (DtcTestPageModelObserver observer : DtcTestPageModel.this.observers) {
      observer.onRequestFailed(caught);
    }
  }

  private void fireResponseReceived(DtcResponse response) {
    for (DtcTestPageModelObserver observer : DtcTestPageModel.this.observers) {
      try {
        observer.onTestPageResponseReceived(response);
      } catch (Exception e) {
        Window.alert(e.getMessage());
      }
    }
  }

  public void sendRequest(final DtcRequest request) {
    DtcService.Util.getInstance().getDtcResponse(request,
        new AsyncCallback<DtcResponse>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());
            DtcTestPageModel.this.fireRequestFailed(caught);
          }

          @Override
          public void onSuccess(DtcResponse response) {
            response.setRequest(request);
            DtcTestPageModel.this.fireResponseReceived(response);
          }

        });
  }
}
