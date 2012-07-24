package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcRequestMeta;

public interface DtcNodeObserver {
  void onFavoriteNodeListChanged();

  void onNodeListChanged();

  void onDtcTestPageLoaded(DtcRequestMeta requestInfo);
}
