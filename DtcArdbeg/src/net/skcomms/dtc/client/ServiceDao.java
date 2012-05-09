/**
 * 
 */
package net.skcomms.dtc.client;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

/**
 * @author jujang@sk.com
 * 
 */
public class ServiceDao {
  /**
   * @param serviceName
   */
  public void addVisitCount(String serviceName) {
    this.setVisitCount(serviceName, this.getVisitCount(serviceName) + 1);
  }

  public int getVisitCount(String serviceName) {
    if (Cookies.getCookie(serviceName) == null) {
      return 0;
    } else {
      return Integer.parseInt(Cookies.getCookie(serviceName));
    }
  }

  public void removeServiceFromCookie(String serviceName) {
    Cookies.removeCookie(serviceName);
  }

  public void setVisitCount(String serviceName, int visitCount) {
    Date expireDate = new Date();
    expireDate.setTime(expireDate.getTime() + 1000L * 60 * 60 * 24 * 365);
    Cookies.setCookie(serviceName, Integer.toString(visitCount), expireDate);
  }
}
