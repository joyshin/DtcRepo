package net.skcomms.dtc.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.skcomms.dtc.client.PersistenceManager;

import com.google.gwt.core.client.GWT;

public class DtcSearchHistoryDao {

  public static final char FORM_VALUE_DELIMETER = 0x0b;

  public static final char FORM_FIELD_DELIMETER = 0x0C;

  public static final String PREFIX = "LastRequestLoaderController.";

  public static String findKey(String string) {
    return DtcSearchHistoryDao.PREFIX + string;
  }

  public static Map<String, String> getLastParameters(String key) {
    String data = PersistenceManager.getInstance().getItem(key);

    GWT.log("data: " + data);
    Map<String, String> map = new HashMap<String, String>();

    if (data == null) {
      return map;
    }

    String[] formFields = data.split(Character
        .toString(DtcSearchHistoryDao.FORM_FIELD_DELIMETER));
    if (formFields == null) {
      return map;
    }

    for (String element : formFields) {
      String[] pair = element.split(Character
          .toString(DtcSearchHistoryDao.FORM_VALUE_DELIMETER));
      if (pair.length == 2) {
        map.put(pair[0], pair[1]);
      }
    }

    return map;
  }

  public void persist(DtcSearchHistory searchHistory) {

  }

  public void persist(String path, Map<String, String> param) {

    StringBuilder requestData = new StringBuilder();
    String requestKey = DtcSearchHistoryDao.findKey("c" + "=" + path);

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

    for (Entry<String, String> entry : param.entrySet()) {
      requestData.append(entry.getKey());
      requestData.append(DtcSearchHistoryDao.FORM_VALUE_DELIMETER);
      requestData.append(entry.getValue());
      requestData.append(DtcSearchHistoryDao.FORM_FIELD_DELIMETER);
    }
    PersistenceManager.getInstance().setItem(requestKey, requestData.toString());

  }
}
