package net.skcomms.dtc.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DtcSearchHistory {

  private static DtcSearchHistory create(String path, Date time, Map<String, String> param,
      long responseTime) {
    return new DtcSearchHistory(path, time, param, responseTime);
  }

  private final String path;

  private final Date time;

  private final Map<String, String> params;

  private long responseTime;

  public static final String FORM_FIELD_DELIMETER = Character.toString((char) 0x0b);

  public static final String FORM_VALUE_DELIMETER = Character.toString((char) 0x0c);

  public static DtcSearchHistory create(String path, Map<String, String> params, long responseTime) {
    return DtcSearchHistory.create(path, new Date(), params, responseTime);
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
    long responseTime = Long.parseLong(elements[2]);
    Map<String, String> params = new HashMap<String, String>();

    for (int i = 3; i < elements.length; i++) {
      String[] pair = elements[i].split(DtcSearchHistory.FORM_VALUE_DELIMETER);
      if (pair.length == 0) {
        throw new IllegalArgumentException("Error: Invalid format near: " + elements[i]);
      }
      params.put(pair[0], ((pair.length == 2) ? pair[1] : ""));
    }

    return DtcSearchHistory.create(path, time, params, responseTime);
  }

  private DtcSearchHistory(String path, Date time, Map<String, String> params, long responseTime) {
    this.path = path;
    this.time = time;
    this.responseTime = responseTime;
    this.params = params;
  }

  public String getFormattedString(String... fields) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.time);
    for (String field : fields) {
      sb.append(", ");
      sb.append(this.params.get(field));
    }
    return sb.toString();
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

    result.append(this.responseTime);
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
