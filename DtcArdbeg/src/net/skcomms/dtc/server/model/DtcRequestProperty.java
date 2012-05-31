package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.List;

public class DtcRequestProperty {

  private String key;

  private String value;

  private String type;

  private ArrayList<String> attrs;

  private String comment;

  public DtcRequestProperty(String key, String type, String value, String comment,
      List<String> attrs) {
    this.key = key;
    this.type = type;
    this.value = value;
    this.comment = comment;
    this.attrs = new ArrayList<String>(attrs);
  }

  public String getComment() {
    return this.comment;
  }

  public String getKey() {
    return this.key;
  }

  public String getType() {
    return this.type;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return "Type:" + this.type + ", Key: " + this.key + ", Val:" + this.value + ", Comment:"
        + this.comment + ", Attrs:" + this.attrs.toString();
  }
}
