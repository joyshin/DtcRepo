package net.skcomms.dtc.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DtcSearchHistory {

  public static DtcSearchHistory create(String path, Date time, Map<String, String> params) {
    return new DtcSearchHistory(path, time, params);
  }

  private final String path;
  private final Date time;
  private final Map<String, String> params;

  public static final String FORM_FIELD_DELIMETER = Character.toString((char) 0x0C);

  public static final String FORM_VALUE_DELIMETER = Character.toString((char) 0x0b);

  public static DtcSearchHistory create(String path, Map<String, String> params) {
    return DtcSearchHistory.create(path, new Date(), params);
  }

  public static DtcSearchHistory deserialize(String source) {
    if (source == null) {
      throw new IllegalArgumentException("Error: Source should not be null!");
    }
    String[] elements = source.split(DtcSearchHistory.FORM_FIELD_DELIMETER);
    if (elements.length == 0) {
      throw new IllegalArgumentException("Error: Invalid argument: " + source);
    }
    String path = elements[0];
    Date time = new Date(Long.parseLong(elements[1]));
    Map<String, String> params = new HashMap<String, String>();

    for (int i = 2; i < elements.length; i++) {
      String[] pair = elements[i].split(DtcSearchHistory.FORM_VALUE_DELIMETER);
      if (pair.length != 2) {
        throw new IllegalArgumentException("Error: Invalid format near: " + elements[i]);
      }
      params.put(pair[0], pair[1]);
    }

    return DtcSearchHistory.create(path, time, params);
  }

  public DtcSearchHistory(String path, Date time, Map<String, String> params) {
    this.time = time;
    this.path = path;
    this.params = params;
  }

  public String getPath() {
    return this.path;
  }

  public Date getSearchTime() {
    return this.time;
  }

  public String serialize() {
    StringBuilder result = new StringBuilder();

    result.append(this.path);
    result.append(DtcSearchHistory.FORM_FIELD_DELIMETER);

    result.append(this.time.getTime());
    result.append(DtcSearchHistory.FORM_FIELD_DELIMETER);

    for (Entry<String, String> entry : this.params.entrySet()) {
      result.append(entry.getKey());
      result.append(DtcSearchHistory.FORM_VALUE_DELIMETER);
      result.append(entry.getValue());
      result.append(DtcSearchHistory.FORM_FIELD_DELIMETER);
    }

    return result.toString();
  }
}
