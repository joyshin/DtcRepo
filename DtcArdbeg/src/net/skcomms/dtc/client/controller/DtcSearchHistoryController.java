package net.skcomms.dtc.client.controller;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DtcTestPageModelObserver;
import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.client.model.DtcSearchHistory;
import net.skcomms.dtc.client.model.DtcSearchHistoryDao;
import net.skcomms.dtc.client.model.DtcTestPageModel;

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
  public void onTestPageResponseReceived(DtcResponse response) {
    this.persist(response);
    this.redrawSearchHistoryView(response.getRequest().getPath());
  }

  private void persist(DtcResponse response) {
    GWT.log("DtcSearchHistoryController.persist() called");
    String path = response.getRequest().getPath();
    Map<String, String> params = response.getRequest().getRequestParameters();
    DtcSearchHistory searchHistory = DtcSearchHistory.create(path, params,
        response.getResponseTime());
    this.searchHistoryDao.persist(searchHistory);
  }

  private void redrawSearchHistoryView(String path) {
    List<DtcSearchHistory> searchHistories = this.searchHistoryDao.getSearchHistroies(path);

    // TODO view 를 다시 그린다.

    for (DtcSearchHistory history : searchHistories) {
      System.out.println(history.getFormattedString("Query", "IP"));
    }
  }
}
