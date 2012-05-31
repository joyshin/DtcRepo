/**
 * 
 */
package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jujang@sk.com
 */
public class DtcIni {

  private String charset = "euckr";

  private List<DtcBaseProperty> baseProps = new ArrayList<DtcBaseProperty>();

  private List<DtcRequestProperty> requestProps = new ArrayList<DtcRequestProperty>();

  private List<DtcResponseProperty> responseProps = new ArrayList<DtcResponseProperty>();

  private List<String> listAttrs = new ArrayList<String>();

  private List<String> errors = new ArrayList<String>();

  public void addErrorMessage(String message) {
    this.errors.add(message);
  }

  /**
   * @param port
   * @return
   */
  public DtcBaseProperty getBaseProp(String key) {
    for (DtcBaseProperty prop : this.baseProps) {
      if (prop.getKey().equals(key)) {
        return prop;
      }
    }
    return null;
  }

  public String getCharacterSet() {
    return this.getCharset();
  }

  public String getCharset() {
    return this.charset;
  }

  public List<String> getErrors() {
    return Collections.unmodifiableList(this.errors);
  }

  public List<String> getListAttrs() {
    return Collections.unmodifiableList(this.listAttrs);
  }

  public DtcRequestProperty getRequestProp(String key) {
    for (DtcRequestProperty prop : this.requestProps) {
      if (prop.getKey().equals(key)) {
        return prop;
      }
    }
    return null;
  }

  public DtcResponseProperty getResponseProp(String key) {
    for (DtcResponseProperty prop : this.responseProps) {
      if (prop.getFieldName().equals(key)) {
        return prop;
      }
    }
    return null;
  }

  /**
   * @param dtcProperty
   */
  public void setBaseProp(DtcBaseProperty prop) {
    System.out.println(prop);
    this.baseProps.add(prop);
  }

  public void setCharset(String charset) {
    this.charset = charset.toLowerCase();
  }

  public void setListAttr(String attr) {
    System.out.println("List Attr:" + attr);
    this.listAttrs.add(attr);
  }

  public void setRequestProp(DtcRequestProperty prop) {
    System.out.println(prop);
    this.requestProps.add(prop);
  }

  public void setResponseProp(DtcResponseProperty prop) {
    System.out.println(prop);
    this.responseProps.add(prop);
  }

}
