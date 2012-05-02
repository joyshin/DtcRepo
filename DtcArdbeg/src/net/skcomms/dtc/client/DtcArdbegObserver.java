package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;

public interface DtcArdbegObserver {

  void onLoadDtcDirectory(Document dtcFrameDoc);

  void onLoadDtcHome(Document dtcFrameDoc);

  void onLoadDtcResponseFrame(Document dtcFrameDoc, boolean success);

  void onLoadDtcTestPage(Document dtcFrameDoc);
}
