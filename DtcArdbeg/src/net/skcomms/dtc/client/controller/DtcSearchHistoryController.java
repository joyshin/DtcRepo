package net.skcomms.dtc.client.controller;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.model.DtcSearchHistory;
import net.skcomms.dtc.client.model.DtcSearchHistoryDao;
import net.skcomms.dtc.client.model.DtcTestPageModel;
import net.skcomms.dtc.client.model.DtcTestPageResponse;

import com.google.gwt.core.client.GWT;

public class DtcSearchHistoryController implements DtcTestPageModelObserver {

  private DtcSearchHistoryDao searchHistoryDao;

  public void initialize(DtcSearchHistoryDao searchHistoryDao, DtcTestPageModel testPageModel) {
    this.searchHistoryDao = searchHistoryDao;
    testPageModel.addObserver(this);
  }

  @Override
  public void onRequestFailed(Throwable caught) {
  }

  @Override
  public void onTestPageResponseReceived(DtcTestPageResponse response) {
    Map<String, String> params = response.getRequest().getRequestParameter();
    String path = response.getRequest().getPath();
    this.persist(path, params);
    this.redrawSearchHistoryView(path);
  }

  public void persist(String path, Map<String, String> param) {
    GWT.log("DtcSearchHistoryController.persist() called");
    DtcSearchHistory searchHistory = DtcSearchHistory.create(path, param);
    this.searchHistoryDao.persist(searchHistory);
    // TODO SearchHistoryView에 searchHistory 객제를 추가한다.
  }

  private void redrawSearchHistoryView(String path) {
    List<DtcSearchHistory> searchHistories = this.searchHistoryDao.getSearchHistroies(path);

    // TODO view 를 다시 그린다.

    for (DtcSearchHistory history : searchHistories) {
      System.out.println(history.getFormattedString("Query", "IP"));
    }
  }
}
