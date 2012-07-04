package net.skcomms.dtc.client;


public interface DtcArdbegObserver {

  void onDtcDirectoryLoaded(String path);

  void onDtcHomeLoaded();

  void onDtcResponseFrameLoaded(boolean success);

}
