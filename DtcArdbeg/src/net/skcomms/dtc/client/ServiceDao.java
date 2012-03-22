/**
 * 
 */
package net.skcomms.dtc.client;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

/**
 * @author jujang@sk.com
 * 
 */
public class ServiceDao {
	public int getVisitCount(String serviceName) {
		if (Cookies.getCookie(serviceName) == null) {
			return 0;
		} else {
			return Integer.parseInt(Cookies.getCookie(serviceName));
		}
	}

	public void setVisitCount(String serviceName, int visitCount) {
		Cookies.setCookie(serviceName, Integer.toString(visitCount));
	}

	/**
	 * @param serviceName
	 */
	public void addVisitCount(String serviceName) {
		this.setVisitCount(serviceName, this.getVisitCount(serviceName) + 1);
		Window.alert(serviceName + ": " + this.getVisitCount(serviceName));
	}
}
