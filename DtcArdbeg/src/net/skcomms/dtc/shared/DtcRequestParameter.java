/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DtcRequestParameter implements Serializable {

  private String key;

  private String name;

  private String value;

  public DtcRequestParameter() {
  }

  public DtcRequestParameter(String aKey, String aName, String aValue) {
    this.key = aKey;
    this.name = aName;
    this.value = aValue;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof DtcRequestParameter)) {
      return false;
    }

    DtcRequestParameter o = (DtcRequestParameter) other;

    return this.key.equals(o.key);
  }

  public String getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.key + ":" + this.name + ":" + this.value;
  }
}