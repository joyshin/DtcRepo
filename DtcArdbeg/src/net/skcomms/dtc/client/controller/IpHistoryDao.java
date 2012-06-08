package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.PersistenceManager;
import net.skcomms.dtc.client.model.IpOptionModel;

public class IpHistoryDao extends DefaultDtcArdbegObserver {

  private String ipText;

  private String combineKeyPrefix(String path) {

    String[] dtcParams = path.split("#");
    String param = dtcParams[0];
    String keyPrefix = param + "IpHistoryManager";
    return keyPrefix;
  }

  public List<IpOptionModel> getIpHistories(String path) {
    String keyPrefix = this.combineKeyPrefix(path);
    List<IpOptionModel> ipHistories = new ArrayList<IpOptionModel>();
    List<String> keys = new ArrayList<String>(PersistenceManager.getInstance().getItemKeys());
    for (String key : keys) {
      if (key.startsWith(keyPrefix)) {
        String ip = key.substring(keyPrefix.length());
        String value = PersistenceManager.getInstance().getItem(key);
        Date date = new Date(Long.parseLong(value));
        ipHistories.add(new IpOptionModel(ip, date, ""));
      }
    }
    return ipHistories;
  }

  public void setIpHistory(String path, String ip, Date date) {
    String keyPrefix = this.combineKeyPrefix(path);
    PersistenceManager.getInstance().setItem(keyPrefix + ip, Long.toString(date.getTime()));
  }

}
