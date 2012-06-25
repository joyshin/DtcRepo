package net.skcomms.dtc.client.controller;

import java.util.Map;

import net.skcomms.dtc.client.model.DtcSearchHistory;
import net.skcomms.dtc.client.model.DtcSearchHistoryDao;

public class SearchHistoryController {

  private DtcSearchHistoryDao searchHistoryDao;

  public void initialize(DtcSearchHistoryDao searchHistoryDao) {
    this.searchHistoryDao = searchHistoryDao;
  }

  public void persist(String path, Map<String, String> param) {
    DtcSearchHistory searchHistory = DtcSearchHistory.create(path, param);
    this.searchHistoryDao.persist(searchHistory);
    // TODO : SearchHistoryView에 searchHistory 객제를 추가한다.
  }
}
