package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.shared.HttpRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcTestPageModel {

  List<DtcTestPageModelObserver> observers = new ArrayList<DtcTestPageModelObserver>();

  public void addObserver(DtcTestPageModelObserver observer) {
    this.observers.add(observer);
  }

  public void sendRequest(final HttpRequestInfoModel httpRequestInfo) {
    // TODO : return type이 String이 아닌, Response 정보(웅답시간, 성공여부 등)를 담고있는 클래스 객체로
    // 바꾸자.
    DtcService.Util.getInstance().getDtcTestPageResponse(httpRequestInfo,
        new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            GWT.log("getDtcTestPageResponse Failed: " + caught.getMessage());
            for (DtcTestPageModelObserver observer : DtcTestPageModel.this.observers) {
              observer.onRequestFailed(caught);
            }
          }

          @Override
          public void onSuccess(String result) {
            DtcTestPageResponse response = new DtcTestPageResponse();
            response.setRequest(httpRequestInfo);
            response.setResult(result);
            for (DtcTestPageModelObserver observer : DtcTestPageModel.this.observers) {
              observer.onTestPageResponseReceived(response);
            }
          }
        });
  }
}
