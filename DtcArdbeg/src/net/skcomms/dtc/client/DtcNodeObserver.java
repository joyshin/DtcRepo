package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcRequestInfoModel;

public interface DtcNodeObserver {
  void onFavoriteNodeListChanged();

  void onNodeListChanged();

  void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo);
}
