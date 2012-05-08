package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.skcomms.dtc.client.IpOption.Origin;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Cookies;

public class IpHistoryManager extends DefaultDtcArdbegObserver {

  native static void addDtcIpSelectClickEventHandler(Document dtcFrameDoc,
      IpHistoryManager ipHistoryManager) /*-{
    var select = dtcFrameDoc.getElementsByTagName("frame")[0].contentDocument
        .getElementById("ip_select");
    if (select != null) {
      select.onclick = function() {
        ipHistoryManager.@net.skcomms.dtc.client.IpHistoryManager::redrawIpOptions(Lcom/google/gwt/dom/client/Document;)(dtcFrameDoc);
      }
    }
  }-*/;

  native static void changeIpType() /*-{
    $doc.getElementsByTagName("iframe")[1].contentDocument.getElementsByTagName("frame")[0].contentWindow.sIP_TYPE = "2";
  }-*/;

  private final List<IpOption> options = new ArrayList<IpOption>();
  private final DtcRequestFormAccessor requestParameter;
  private String ipText;
  private OptGroupElement dtcOptGroup;

  private OptGroupElement cookieOptGroup;

  public IpHistoryManager(DtcRequestFormAccessor dtcRequestFormAccesser) {
    this.requestParameter = dtcRequestFormAccesser;
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
    for (IpOption option : this.options) {
      if (ip.equals(option.getIp())) {
        return option;
      }
    }
    IpOption option = new IpOption(ip, ip, Origin.COOKIE);
    this.options.add(option);

    return option;
  }

  private SelectElement getOrCreateIpSelectElement(Document dtcFrameDoc) {
    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();
    Element select = requestDoc.getElementById("ip_select");

    if (select == null) {

      String innerHtml = "<div><input type=\"text\" name=\"ip_text\" id=\"ip_text\" value=\""
          + this.ipText + "\" style=\"width:226px;display:none;\" disabled>";
      innerHtml += "<select name=\"ip_select\" id=\"ip_select\" style=\"width:226px; font-size:10pt\">";
      innerHtml += "</select></div>";
      innerHtml += "<div style=\"float:right;\"><input type=\"button\" value=\"↔\" onclick=\"javascript:fnCHANGE_IP();\"></div>";
      innerHtml += "<script type='text/javascript'>";
      innerHtml += " sIP_TYPE=\"2\"; </script> ";

      Element spanIp = requestDoc.getElementById("span_ip");
      spanIp.setInnerHTML(innerHtml);
      select = requestDoc.getElementById("ip_select");

      IpHistoryManager.changeIpType();
      IpHistoryManager.addDtcIpSelectClickEventHandler(dtcFrameDoc, this);
    }
    return SelectElement.as(select);
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
  }

  private void joinAdditionalInfo(Document requestDoc) {
    String keyPrefix = this.combineKeyPrefix(requestDoc);

    ArrayList<String> cookieNames = new ArrayList<String>(Cookies.getCookieNames());
    for (String key : cookieNames) {
      if (key.startsWith(keyPrefix)) {
        String ip = key.substring(keyPrefix.length());

        String value = Cookies.getCookie(key);
        Date date = new Date(Long.parseLong(value));

        IpOption option = this.getOrCreateIpOptionBy(ip);
        option.setLastSuccessTime(date);
      }
    }
  }

  @Override
  public void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success) {
    this.updateIpHistory(dtcFrameDoc);
    this.redrawIpOptions(dtcFrameDoc);
    if (success) {

    }
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    this.retrieveInfo(dtcFrameDoc);
    this.redrawIpOptions(dtcFrameDoc);
    IpHistoryManager.addDtcIpSelectClickEventHandler(dtcFrameDoc, this);
  }

  public void redrawIpOptions(Document dtcFrameDoc) {
    if (this.options.size() < 2) {
      return;
    }

    SelectElement select = this.getOrCreateIpSelectElement(dtcFrameDoc);
    int current = select.getSelectedIndex();
    select.clear();

    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();
    for (IpOption option : this.options) {
      OptionElement optionElement = requestDoc.createOptionElement();
      optionElement.setValue(option.getIp());
      optionElement.setInnerText(option.getText() + option.getDecoratedText());

      if (option.getOrigin().equals(Origin.DTC)) {
        this.dtcOptGroup.appendChild(optionElement);
      } else {
        this.cookieOptGroup.appendChild(optionElement);
      }
    }
    select.appendChild(this.dtcOptGroup);
    select.appendChild(this.cookieOptGroup);

    select.setSelectedIndex(current);
  }

  public void retrieveInfo(Document dtcFrameDoc) {
    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();

    this.retrieveNativeIpInfos(requestDoc);
    this.joinAdditionalInfo(requestDoc);

    this.dtcOptGroup = requestDoc.createOptGroupElement();
    this.dtcOptGroup.setLabel("DTC");
    this.cookieOptGroup = requestDoc.createOptGroupElement();
    this.cookieOptGroup.setLabel("Cookie");

  }

  private void retrieveNativeIpInfos(Document requestDoc) {
    this.options.clear();

    NodeList<Element> elementsByTagName = requestDoc.getElementsByTagName("option");
    for (int i = 0; i < elementsByTagName.getLength(); i++) {
      String ip = elementsByTagName.getItem(i).getAttribute("value");
      String text = elementsByTagName.getItem(i).getInnerText();
      this.options.add(new IpOption(ip, text, IpOption.Origin.DTC));
    }
    Element ipTextElement = requestDoc.getElementById("ip_text");
    this.ipText = InputElement.as(ipTextElement).getValue();
  }

  public void updateIpHistory(Document dtcFrameDoc) {
    Document requestDoc = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
        .getContentDocument();

    String keyPrefix = this.combineKeyPrefix(requestDoc);
    String ip = this.requestParameter.getDtcRequestParameter("IP");

    String key = keyPrefix + ip;
    IpOption option = this.getOrCreateIpOptionBy(ip);

    Date now = new Date();
    option.setLastSuccessTime(now);

    Date expireTime = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 7));
    Cookies.setCookie(key, Long.toString(now.getTime()), expireTime);
  }
}
