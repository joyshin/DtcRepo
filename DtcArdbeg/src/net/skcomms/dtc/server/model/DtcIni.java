/**
 * 
 */
package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jujang@sk.com
 */
public class DtcIni {

  private static final Pattern IP_PATTERN = Pattern
      .compile("^([.0-9]+)$|(\\s*\\{\\s*(\")?([.0-9]+)(\")?\\s*((\")?([^\"}]*)(\")?)?\\s*\\})");

  private String charset = "euckr";

  private List<DtcBaseProperty> baseProps = new ArrayList<DtcBaseProperty>();

  private List<DtcRequestProperty> requestProps = new ArrayList<DtcRequestProperty>();

  private List<DtcResponseProperty> responseProps = new ArrayList<DtcResponseProperty>();

  private List<String> listAttrs = new ArrayList<String>();

  private List<String> errors = new ArrayList<String>();

  private Map<String, String> ips = new HashMap<String, String>();

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

  public Map<String, String> getIps() {
    return this.ips;
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

  public List<DtcRequestProperty> getRequestProps() {
    return Collections.unmodifiableList(this.requestProps);
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
    this.baseProps.add(prop);
    if (prop.getKey().equals("IP")) {
      Matcher matcher = DtcIni.IP_PATTERN.matcher(prop.getValue());
      while (matcher.find()) {
        if (matcher.group(1) != null) {
          this.ips.put(matcher.group(1), "");
        }
        if (matcher.group(4) != null) {
          this.ips.put(matcher.group(4), (matcher.group(8) == null ? "" : matcher.group(8)));
        }
      }
      matcher.reset();
      if (!matcher.find()) {
        this.addErrorMessage("Error: invalid IP pattern:\"" + prop.getValue() + "\"");
      }
    }
  }

  public void setCharset(String charset) {
    this.charset = charset.toLowerCase();
  }

  public void setListAttr(String attr) {
    this.listAttrs.add(attr);
  }

  public void setRequestProp(DtcRequestProperty prop) {
    this.requestProps.add(prop);
  }

  public void setResponseProp(DtcResponseProperty prop) {
    this.responseProps.add(prop);
  }

}
