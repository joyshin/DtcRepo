package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.PersistenceManager;

public class DtcSearchHistoryDao {

  public static final String PREFIX = "DtcSearchHistoryDao.";

  private static final int MAX_HISTORY_SIZE = 5;

  public static String combineKey(DtcSearchHistory searchHistory) {
    return DtcSearchHistoryDao.combineKeyPrefix(searchHistory.getPath())
        + searchHistory.getSearchTime().getTime();
  }

  public static String combineKeyPrefix(String path) {
    return DtcSearchHistoryDao.PREFIX + path + ".";
  }

  private List<DtcSearchHistory> getAllSearchHistories(String path) {
    List<DtcSearchHistory> searchHistories = new ArrayList<DtcSearchHistory>();
    for (String key : PersistenceManager.getInstance().getItemKeys()) {
      if (key.startsWith(DtcSearchHistoryDao.combineKeyPrefix(path))) {
        DtcSearchHistory history = DtcSearchHistory.deserialize(PersistenceManager.getInstance()
            .getItem(key));
        searchHistories.add(history);
      }
    }
    return searchHistories;
  }

  public List<DtcSearchHistory> getSearchHistroies(String path) {
    List<DtcSearchHistory> searchHistories = this.getAllSearchHistories(path);
    this.sortHistoriesOrderByTimeDesc(searchHistories);
    this.removeOldHistories(searchHistories);
    return searchHistories;
  }

  public void persist(DtcSearchHistory searchHistory) {
    PersistenceManager.getInstance().setItem(
        DtcSearchHistoryDao.combineKey(searchHistory), searchHistory.serialize());
  }

  private void removeOldHistories(List<DtcSearchHistory> searchHistories) {
    for (int i = DtcSearchHistoryDao.MAX_HISTORY_SIZE; i < searchHistories.size(); i++) {
      PersistenceManager.getInstance().removeItem(
          DtcSearchHistoryDao.combineKey(searchHistories.get(i)));
    }
    if (searchHistories.size() > DtcSearchHistoryDao.MAX_HISTORY_SIZE) {
      searchHistories.subList(DtcSearchHistoryDao.MAX_HISTORY_SIZE, searchHistories.size()).clear();
    }
  }

  private void sortHistoriesOrderByTimeDesc(List<DtcSearchHistory> searchHistories) {
    Collections.sort(searchHistories, new Comparator<DtcSearchHistory>() {

      @Override
      public int compare(DtcSearchHistory o1, DtcSearchHistory o2) {
        return (int) (-(o1.getSearchTime().getTime() - o2.getSearchTime().getTime()));
      }
    });
  }

}
