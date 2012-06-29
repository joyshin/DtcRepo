package net.skcomms.dtc.client;

import net.skcomms.dtc.client.model.DtcTestPageResponse;

public interface DtcTestPageModelObserver {

  void onRequestFailed(Throwable caught);

  void onTestPageResponseReceived(DtcTestPageResponse response);

}
