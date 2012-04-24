package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.skcomms.dtc.client.IpOption.Origin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Cookies;

public class IpHistoryManager {

  private final List<IpOption> options = new ArrayList<IpOption>();
  private final DtcRequestFormAccessor requestParameter;
  private String ipText;
  private OptGroupElement dtcOptGroup;
  private OptGroupElement cookieOptGroup;

  public IpHistoryManager(DtcRequestFormAccessor dtcRequestFormAccesser) {
    requestParameter = dtcRequestFormAccesser;
  }

  private String combineKeyPrefix(Document requestDoc) {
    String url = requestDoc.getURL();
    String queryString = "";

    if (url.indexOf("?c=") != -1) {
      queryString = url.substring(url.indexOf("?c=") + 3);
    }

    String[] dtcParams = queryString.split("#");
    String param = dtcParams[0];
    String keyPrefix = param + "IpHistoryManager";
    return keyPrefix;
  }

  private IpOption getOrCreateIpOptionBy(String ip) {
    for (IpOption option : options) {
      if (ip.equals(option.getIp())) {
        return option;
      }
    }
    IpOption option = new IpOption(ip, ip, Origin.COOKIE);
    options.add(option);

    return option;
  }

  private SelectElement getOrCreateIpSelectElement(Document requestDoc) {
    Element select = requestDoc.getElementById("ip_select");

    if (select == null) {

      String innerHtml = "<div><input type=\"text\" name=\"ip_text\" id=\"ip_text\" value=\""
          + ipText + "\" style=\"width:226px;display:none;\" disabled>";
      innerHtml += "<select name=\"ip_select\" id=\"ip_select\" style=\"width:226px; font-size:10pt\">";
      innerHtml += "</select></div>";
      innerHtml += "<div style=\"float:right;\"><input type=\"button\" value=\"¡ê\" onclick=\"javascript:fnCHANGE_IP();\"></div>";

      Element spanIp = requestDoc.getElementById("span_ip");
      spanIp.setInnerHTML(innerHtml);
      select = requestDoc.getElementById("ip_select");
    }
    return SelectElement.as(select);
  }

  private void joinAdditionalInfo(Document requestDoc) {
    String keyPrefix = combineKeyPrefix(requestDoc);

    ArrayList<String> cookieNames = new ArrayList<String>(Cookies.getCookieNames());
    for (String key : cookieNames) {
      if (key.startsWith(keyPrefix)) {
        String ip = key.substring(keyPrefix.length());

        String value = Cookies.getCookie(key);
        Date date = new Date(Long.parseLong(value));

        IpOption option = getOrCreateIpOptionBy(ip);
        option.setLastSuccessTime(date);
      }
    }
  }

  public void redrawIpOptions(Document dtcFrameDoc) {

    if (options.size() < 2) {
      return;
    }

    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();
    SelectElement select = getOrCreateIpSelectElement(requestDoc);
    int current = select.getSelectedIndex();

    select.clear();

    for (IpOption option : options) {
      OptionElement optionElement = requestDoc.createOptionElement();
      optionElement.setValue(option.getIp());
      optionElement.setInnerText(option.getText() + option.getDecoratedText());

      // select.add(optionElement, null);
      if (option.getOrigin().equals(Origin.DTC)) {
        dtcOptGroup.appendChild(optionElement);
      } else {
        cookieOptGroup.appendChild(optionElement);
      }
    }
    select.appendChild(dtcOptGroup);
    select.appendChild(cookieOptGroup);

    select.setSelectedIndex(current);
    // Window.alert("" + select.getOptions().getLength());
  }

  public void retrieveInfo(Document dtcFrameDoc) {
    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();

    retrieveNativeIpInfos(requestDoc);
    joinAdditionalInfo(requestDoc);

    dtcOptGroup = requestDoc.createOptGroupElement();
    dtcOptGroup.setLabel("DTC");
    cookieOptGroup = requestDoc.createOptGroupElement();
    cookieOptGroup.setLabel("Cookie");

  }

  private void retrieveNativeIpInfos(Document requestDoc) {
    options.clear();

    NodeList<Element> elementsByTagName = requestDoc.getElementsByTagName("option");
    for (int i = 0; i < elementsByTagName.getLength(); i++) {
      String ip = elementsByTagName.getItem(i).getAttribute("value");
      String text = elementsByTagName.getItem(i).getInnerText();
      GWT.log(ip + ":" + text);
      options.add(new IpOption(ip, text, IpOption.Origin.DTC));
    }
    Element ipTextElement = requestDoc.getElementById("ip_text");
    ipText = InputElement.as(ipTextElement).getValue();
  }

  public void updateIpHistory(Document dtcFrameDoc) {
    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();

    String keyPrefix = combineKeyPrefix(requestDoc);
    String ip = requestParameter.getDtcRequestParameter("IP");

    String key = keyPrefix + ip;
    IpOption option = getOrCreateIpOptionBy(ip);

    Date now = new Date();
    option.setLastSuccessTime(now);

    Date expireTime = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 7));
    Cookies.setCookie(key, Long.toString(now.getTime()), expireTime);

  }

}
