package net.skcomms.dtc.client;

import net.skcomms.dtc.client.model.DtcResponse;

public interface DtcTestPageModelObserver {

  void onRequestFailed(Throwable caught);

  void onTestPageResponseReceived(DtcResponse response);

}
