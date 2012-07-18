/**
 * 
 */
package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameter;
import net.skcomms.dtc.shared.IpInfoModel;

/**
 * @author jujang@sk.com
 */
public class DtcIni {

  private static final Pattern IP_PATTERN = Pattern
      .compile("^([.0-9]+)$|(\\s*\\{\\s*(\")?([.0-9]+)(\")?\\s*((\")?([^\"}]*)(\")?)?\\s*\\})");

  private String charset = "euckr";

  private final List<DtcBaseProperty> baseProps = new ArrayList<DtcBaseProperty>();

  private final List<DtcRequestProperty> requestProps = new ArrayList<DtcRequestProperty>();

  private final List<DtcResponseProperty> responseProps = new ArrayList<DtcResponseProperty>();

  private final List<String> listAttrs = new ArrayList<String>();

  private final List<String> errors = new ArrayList<String>();

  private final Map<String, String> ips = new HashMap<String, String>();

  public void addErrorMessage(String message) {
    this.errors.add(message);
  }

  public DtcRequestMeta createRequestMeta() {
    DtcRequestMeta requestMeta = new DtcRequestMeta();

    this.setupParams(requestMeta);
    requestMeta.setEncoding(this.getCharacterSet());
    requestMeta.setAppName(this.getBaseProp("APP_NAME").getValue());
    requestMeta.setApiNumber(this.getBaseProp("API_NUM").getValue());
    requestMeta.setCndQueryFieldName(this.getCndFieldName());
    requestMeta.setQueryFieldName(this.getQueryFieldName());
    this.setupIpInfo(requestMeta);

    return requestMeta;
  }

  private void detectIps(DtcBaseProperty prop) {
    Matcher matcher = DtcIni.IP_PATTERN.matcher(prop.getValue());
    if (!matcher.find()) {
      this.addErrorMessage("Error: invalid IP pattern:\"" + prop.getValue() + "\"");
      return;
    }

    do {
      if (matcher.group(1) != null) {
        this.ips.put(matcher.group(1), "");
      }
      if (matcher.group(4) != null) {
        this.ips.put(matcher.group(4), (matcher.group(8) == null ? "" : matcher.group(8)));
      }
    } while (matcher.find());
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

  public String getCndFieldName() {
    for (DtcRequestProperty prop : this.requestProps) {
      if (prop.getAttrs().contains("CNDQUERY")) {
        return prop.getKey();
      }
    }
    return null;
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

  public String getProtocol() {
    DtcBaseProperty protocol = this.getBaseProp("PROTOCOL");
    return (protocol == null) ? "ATP" : protocol.getValue();
  }

  public String getQueryFieldName() {
    for (DtcRequestProperty prop : this.requestProps) {
      if (prop.getAttrs().contains("QUERY")) {
        return prop.getKey();
      }
    }
    return null;
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

  public List<DtcResponseProperty> getResponseProps() {
    return this.responseProps;
  }

  public List<DtcResponseProperty> getResultHeaderProps() {
    List<DtcResponseProperty> headerProps = new ArrayList<DtcResponseProperty>();
    for (DtcResponseProperty prop : this.responseProps) {
      if (!prop.getAttrs().contains("LIST_FIELD")) {
        headerProps.add(prop);
      }
    }
    return headerProps;
  }

  public List<DtcResponseProperty> getResultListProps() {
    List<DtcResponseProperty> headerProps = new ArrayList<DtcResponseProperty>();
    for (DtcResponseProperty prop : this.responseProps) {
      if (prop.getAttrs().contains("LIST_FIELD")) {
        headerProps.add(prop);
      }
    }
    return headerProps;
  }

  /**
   * @param dtcProperty
   */
  public void setBaseProp(DtcBaseProperty prop) {
    this.baseProps.add(prop);
    if (prop.getKey().equals("IP")) {
      this.detectIps(prop);
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

  private void setupIpInfo(DtcRequestMeta requestInfo) {
    IpInfoModel ipInfo = new IpInfoModel();
    for (Entry<String, String> entry : this.getIps().entrySet()) {
      ipInfo.addOption(entry.getKey(), entry.getKey() + " - " + entry.getValue());
    }
    requestInfo.setIpInfo(ipInfo);
  }

  private void setupParams(DtcRequestMeta requestInfo) {
    ArrayList<DtcRequestParameter> params = new ArrayList<DtcRequestParameter>();
    int index = 0;
    for (DtcRequestProperty prop : this.getRequestProps()) {
      index++;
      params.add(new DtcRequestParameter(prop.getKey(), "REQUEST" + index, prop.getValue()));
    }
    params.add(new DtcRequestParameter("Port", "port", this.getBaseProp("PORT").getValue()));
    requestInfo.setParams(params);
  }

}
