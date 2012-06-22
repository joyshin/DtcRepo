package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.List;

public class DtcAtpRecord {

  private List<String> fields = new ArrayList<String>();

  public void addField(String value) {
    this.fields.add(value);
  }

  @Override
  public String toString() {
    return this.fields.toString();
  }
}
