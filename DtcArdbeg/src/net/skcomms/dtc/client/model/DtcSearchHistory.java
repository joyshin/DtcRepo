package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.skcomms.dtc.shared.DtcRequestParameter;

public class DtcSearchHistory {

  private static DtcSearchHistory create(String path, Date time, List<DtcRequestParameter> params,
      long responseTime) {
    return new DtcSearchHistory(path, time, params, responseTime);
  }

  private final String path;

  private final Date time;

  private final List<DtcRequestParameter> params;

  private final long responseTime;

  public static final String FORM_FIELD_DELIMETER = Character.toString((char) 0x0b);

  public static final String FORM_VALUE_DELIMETER = Character.toString((char) 0x0c);

  public static DtcSearchHistory create(String path, List<DtcRequestParameter> params,
      long responseTime) {
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
    List<DtcRequestParameter> params = new ArrayList<DtcRequestParameter>();

    for (int i = 3; i < elements.length; i++) {
      String[] pair = elements[i].split(DtcSearchHistory.FORM_VALUE_DELIMETER);
      if (pair.length == 0) {
        throw new IllegalArgumentException("Error: Invalid format near: " + elements[i]);
      }
      DtcRequestParameter param = new DtcRequestParameter(pair[0], null,
          ((pair.length == 2) ? pair[1] : ""));
      params.add(param);
    }

    return DtcSearchHistory.create(path, time, params, responseTime);
  }

  private DtcSearchHistory(String path, Date time, List<DtcRequestParameter> params,
      long responseTime) {
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
      sb.append(this.getRequestParameter(field));
    }
    return sb.toString();
  }

  public String getPath() {
    return this.path;
  }

  private String getRequestParameter(String key) {
    int index = this.params.indexOf(new DtcRequestParameter(key, null, null));
    return (index == -1 ? null : this.params.get(index).getValue());
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

    for (DtcRequestParameter param : this.params) {
      result.append(param.getKey());
      result.append(DtcSearchHistory.FORM_VALUE_DELIMETER);
      result.append(param.getValue() == null ? "" : param.getValue());
      result.append(DtcSearchHistory.FORM_FIELD_DELIMETER);
    }

    return result.toString();
  }
}
