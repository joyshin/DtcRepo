package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DtcResponseProperty {

  private String fieldName;

  private String type;

  private String comment;

  private List<String> attrs;

  public DtcResponseProperty(String fieldName, String type, String comment, List<String> attrs) {
    this.fieldName = fieldName;
    this.type = type;
    this.comment = comment;
    this.attrs = new ArrayList<String>(attrs);
  }

  public List<String> getAttrs() {
    return Collections.unmodifiableList(this.attrs);
  }

  public String getComment() {
    return this.comment;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public String getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return "Type:" + this.type + ", FieldName: " + this.fieldName + ", Comment:" + this.comment
        + ", Attrs:" + this.attrs.toString();
  }
}
