package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.PersistenceManager;

import com.google.gwt.core.client.GWT;

public class DtcSearchHistoryDao {

  public static final String PREFIX = "DtcSearchHistoryDao.";

  private static final int MAX_HISTORY_SIZE = 20;

  public static String combineKey(DtcSearchHistory searchHistory) {
    return DtcSearchHistoryDao.combineKeyPrefix(searchHistory.getPath())
        + searchHistory.getSearchTime().getTime();
  }

  public static String combineKeyPrefix(String path) {
    return DtcSearchHistoryDao.PREFIX + path + ".";
  }

  public static Map<String, String> getLastParameters(String key) {
    String data = PersistenceManager.getInstance().getItem(key);

    GWT.log("data: " + data);
    Map<String, String> map = new HashMap<String, String>();

    if (data == null) {
      return map;
    }

    String[] formFields = data.split(DtcSearchHistory.FORM_FIELD_DELIMETER);
    if (formFields == null) {
      return map;
    }

    for (String element : formFields) {
      String[] pair = element.split(DtcSearchHistory.FORM_VALUE_DELIMETER);
      if (pair.length == 2) {
        map.put(pair[0], pair[1]);
      }
    }

    return map;
  }

  public List<DtcSearchHistory> getSearchHistroies(String path) {
    List<DtcSearchHistory> searchHistories = new ArrayList<DtcSearchHistory>();
    for (String key : PersistenceManager.getInstance().getItemKeys()) {
      if (key.startsWith(DtcSearchHistoryDao.combineKeyPrefix(path))) {
        DtcSearchHistory history = DtcSearchHistory.deserialize(PersistenceManager.getInstance()
            .getItem(key));

        searchHistories.add(history);
      }
    }
    for (int i = 0; i < searchHistories.size() - DtcSearchHistoryDao.MAX_HISTORY_SIZE; i++) {
      PersistenceManager.getInstance().removeItem(
          DtcSearchHistoryDao.combineKey(searchHistories.get(i)));
    }
    return searchHistories.subList(searchHistories.size() - DtcSearchHistoryDao.MAX_HISTORY_SIZE,
        searchHistories.size());
  }

  public void persist(DtcSearchHistory searchHistory) {
    PersistenceManager.getInstance().setItem(
        DtcSearchHistoryDao.combineKey(searchHistory), searchHistory.serialize());
  }

  public void persist(String path, Map<String, String> params) {
    // FIXME entrySet()에 대해 for each 구문을 사용하자.
    // Iterator<String> keyItor = param.keySet().iterator();
    // for (int i = 0; i < param.keySet().size(); i++) {
    // String key = keyItor.next();
    // String value = param.get(key);
    // requestData.append(key);
    // requestData.append(DtcSearchHistoryDao.FORM_VALUE_DELIMETER);
    // requestData.append(value);
    // requestData.append(DtcSearchHistoryDao.FORM_FIELD_DELIMETER);
    // }
    this.persist(DtcSearchHistory.create(path, params));
  }
}
