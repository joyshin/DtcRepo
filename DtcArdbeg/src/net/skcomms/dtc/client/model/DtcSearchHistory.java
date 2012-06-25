package net.skcomms.dtc.client.model;

import java.util.Map;

public class DtcSearchHistory {

  public static DtcSearchHistory create(String path, Map<String, String> param) {
    return new DtcSearchHistory(path, param);
  }

  public DtcSearchHistory(String path, Map<String, String> param) {
  }

}
