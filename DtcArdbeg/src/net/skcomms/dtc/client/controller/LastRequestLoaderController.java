package net.skcomms.dtc.client.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.PersistenceManager;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameterModel;

import com.google.gwt.core.client.GWT;

public class LastRequestLoaderController {

  private static final char FORM_VALUE_DELIMETER = 0x0b;

  private static final char FORM_FIELD_DELIMETER = 0x0C;

  private static final String PREFIX = "LastRequestLoaderController.";

  private static String findKey(String string) {
    return LastRequestLoaderController.PREFIX + string;
  }

  private static Map<String, String> getLastParameters(String key) {
    String data = PersistenceManager.getInstance().getItem(key);

    GWT.log("data: " + data);
    Map<String, String> map = new HashMap<String, String>();

    if (data == null) {
      return map;
    }

    String[] formFields = data.split(Character
        .toString(LastRequestLoaderController.FORM_FIELD_DELIMETER));
    if (formFields == null) {
      return map;
    }

    for (String element : formFields) {
      String[] pair = element.split(Character
          .toString(LastRequestLoaderController.FORM_VALUE_DELIMETER));
      if (pair.length == 2) {
        map.put(pair[0], pair[1]);
      }
    }

    return map;
  }

  String requestKey;

  StringBuilder requestData;

  Map<String, String> storedParams;

  public LastRequestLoaderController() {
    this.requestKey = null;
    this.requestData = new StringBuilder();
  }

  public void createLastRequest(String requestKey, Map<String, String> param) {

    this.requestKey = null;
    this.requestData.setLength(0);
    this.requestKey = LastRequestLoaderController.findKey(requestKey);

    // FIXME entrySet()에 대해 for each 구문을 사용하자.
    Iterator<String> keyItor = param.keySet().iterator();
    for (int i = 0; i < param.keySet().size(); i++) {
      String key = keyItor.next();
      String value = param.get(key);
      this.requestData.append(key);
      this.requestData.append(LastRequestLoaderController.FORM_VALUE_DELIMETER);
      this.requestData.append(value);
      this.requestData.append(LastRequestLoaderController.FORM_FIELD_DELIMETER);
    }
  }

  public void loadLastRequest(DtcRequestMeta requestInfo) {
    // set value
    List<DtcRequestParameterModel> requestParamList = requestInfo.getParams();
    DtcRequestParameterModel requestParam = null;

    String storedValue = null;

    // FIXME for each 방식을 사용할 것.
    for (int i = 0; i < requestParamList.size(); i++) {
      requestParam = requestParamList.get(i);
      storedValue = this.storedParams.get(requestParam.getKey());
      if (storedValue == null) {
        storedValue = "";
      }
      requestParam.setValue(storedValue);
    }

    // set ip
    String storedIp = this.storedParams.get("IP");
    requestInfo.getIpInfo().setIpText(storedIp);
  }

  /**
   * @param dtcArdbeg
   */

  public void persist() {
    PersistenceManager.getInstance().setItem(this.requestKey, this.requestData.toString());
  }

  public boolean recall(String lastRequestKey) {
    String key = LastRequestLoaderController.findKey(lastRequestKey);
    this.storedParams = LastRequestLoaderController.getLastParameters(key);
    return this.storedParams.size() > 0;
  }
}
