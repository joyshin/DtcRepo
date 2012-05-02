package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;

public interface DtcArdbegObserver {

  void onDtcDirectoryLoaded(Document dtcFrameDoc);

  void onDtcHomeLoaded(Document dtcFrameDoc);

  void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success);

  void onDtcTestPageLoaded(Document dtcFrameDoc);

  void onSubmittingDtcRequest();
}
