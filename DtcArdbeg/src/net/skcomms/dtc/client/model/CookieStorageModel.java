package net.skcomms.dtc.client.model;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.Cookies;

public class CookieStorageModel implements ClientStorageModel {

  @Override
  public String getItem(String key) {
    return Cookies.getCookie(key);
  }

  @Override
  public List<String> getItemKeys() {
    return (List<String>) Cookies.getCookieNames();
  }

  @Override
  public void removeItem(String key) {
    Cookies.removeCookie(key);
  }

  @Override
  public void setItem(String key, String data) {
    Date expireDate = new Date();
    expireDate.setTime(expireDate.getTime() + 1000L * 60 * 60 * 24 * 365);
    Cookies.setCookie(key, data, expireDate);
  }
}
