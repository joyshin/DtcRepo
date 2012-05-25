package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcRequestFormAccessor;
import net.skcomms.dtc.client.PersistenceManager;
import net.skcomms.dtc.client.model.IpOptionModel;
import net.skcomms.dtc.client.model.IpOptionModel.Origin;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.DtcRequestParameterModel;

import com.google.gwt.dom.client.Document;

public class IpHistoryController extends DefaultDtcArdbegObserver {

  private final List<IpOptionModel> options = new ArrayList<IpOptionModel>();

  private String ipText;

  public IpHistoryController(DtcRequestFormAccessor dtcRequestFormAccesser) {
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

  private IpOptionModel getOrCreateIpOptionBy(String ip) {
    for (IpOptionModel option : this.options) {
      if (ip.equals(option.getIp())) {
        return option;
      }
    }
    IpOptionModel option = new IpOptionModel(ip, ip, Origin.COOKIE);
    this.options.add(option);

    return option;
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
  }

  private void joinAdditionalInfo(Document requestDoc) {
    String keyPrefix = this.combineKeyPrefix(requestDoc);

    ArrayList<String> keys = new ArrayList<String>(PersistenceManager.getInstance().getItemKeys());
    for (String key : keys) {
      if (key.startsWith(keyPrefix)) {
        String ip = key.substring(keyPrefix.length());

        String value = PersistenceManager.getInstance().getItem(key);
        Date date = new Date(Long.parseLong(value));

        IpOptionModel option = this.getOrCreateIpOptionBy(ip);
        option.setLastSuccessTime(date);
      }
    }
  }

  @Override
  public void onDtcResponseFrameLoaded(boolean success) {
    this.updateIpHistory();
    this.redrawIpOptions();
    if (success) {

    }
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.retrieveInfo(requestInfo);
  }

  public void redrawIpOptions() {
    if (this.options.size() < 2) {
      return;
    }

    /*
     * SelectElement select = this.getOrCreateIpSelectElement(dtcFrameDoc); int
     * current = select.getSelectedIndex(); select.clear(); Document requestDoc
     * = FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
     * .getContentDocument(); for (IpOptionModel option : this.options) {
     * OptionElement optionElement = requestDoc.createOptionElement();
     * optionElement.setValue(option.getIp());
     * optionElement.setInnerText(option.getText() + option.getDecoratedText());
     * if (option.getOrigin().equals(Origin.DTC)) {
     * this.dtcOptGroup.appendChild(optionElement); } else {
     * this.storedOptGroup.appendChild(optionElement); } }
     * select.appendChild(this.dtcOptGroup);
     * select.appendChild(this.storedOptGroup);
     * select.setSelectedIndex(current);
     */
  }

  public void retrieveInfo(DtcRequestInfoModel requestInfo) {
    this.retrieveNativeIpInfos(requestInfo);
  }

  private void retrieveNativeIpInfos(DtcRequestInfoModel requestInfo) {
    this.options.clear();

    requestInfo.getIpInfo().getOptions();
    for (DtcRequestParameterModel option : requestInfo.getIpInfo().getOptions()) {
      this.options.add(new IpOptionModel(option.getKey(), option.getValue(),
          IpOptionModel.Origin.DTC));
    }
    this.ipText = requestInfo.getIpInfo().getIpText();
  }

  public void updateIpHistory() {
    throw new UnsupportedOperationException("Not implemented yet!");
    /*
     * Document requestDoc =
     * FrameElement.as(dtcFrameDoc.getElementsByTagName("frame").getItem(0))
     * .getContentDocument(); String keyPrefix =
     * this.combineKeyPrefix(requestDoc); String ip =
     * this.requestParameter.getDtcRequestParameter("IP"); String key =
     * keyPrefix + ip; IpOptionModel option = this.getOrCreateIpOptionBy(ip);
     * Date now = new Date(); option.setLastSuccessTime(now);
     * PersistenceManager.getInstance().setItem(key,
     * Long.toString(now.getTime()));
     */
  }
}
