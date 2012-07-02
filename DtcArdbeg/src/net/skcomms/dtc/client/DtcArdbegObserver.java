package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcRequestInfoModel;

public interface DtcArdbegObserver {

  void onDtcDirectoryLoaded(String path);

  void onDtcHomeLoaded();

  void onDtcResponseFrameLoaded(boolean success);

  void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo);

}
