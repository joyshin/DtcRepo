package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;

public interface DtcArdbegObserver {
  void onLoadDtcDirectory();

  void onLoadDtcHome();

  void onLoadDtcResponseFrame(Document dtcFrameDoc, boolean success);

  void onLoadDtcTestPage(Document dtcFrameDoc);
}
