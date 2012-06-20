/**
 * 
 */
package net.skcomms.dtc.client;

import java.util.List;

import net.skcomms.dtc.client.model.ClientStorageModel;
import net.skcomms.dtc.client.model.CookieStorageModel;
import net.skcomms.dtc.client.model.LocalStorageModel;

import com.google.gwt.core.client.GWT;

/**
 * @author jujang@sk.com
 * 
 */
public class PersistenceManager {

  private static final PersistenceManager INSTANCE = new PersistenceManager();

  public static PersistenceManager getInstance() {
    return PersistenceManager.INSTANCE;
  }

  private final ClientStorageModel clientStorage;

  private PersistenceManager() {

    if (LocalStorageModel.isSupported()) {
      System.out.println("use Local Storage");
      this.clientStorage = new LocalStorageModel();
    }
    else {
      this.clientStorage = new CookieStorageModel();
    }
  }

  public void addVisitCount(String serviceName) {
    this.setVisitCount(serviceName, this.getVisitCount(serviceName) + 1);
  }

  public String getItem(String key) {
    GWT.log("getItem Key: " + key);
    GWT.log("getItem Data :" + this.clientStorage.getItem(key));
    return this.clientStorage.getItem(key);
  }

  public List<String> getItemKeys() {
    return this.clientStorage.getItemKeys();
  }

  public int getVisitCount(String serviceName) {
    if (this.clientStorage.getItem(serviceName) == null) {
      return 0;
    } else {
      return Integer.parseInt(this.clientStorage.getItem(serviceName));
    }
  }

  /**
   * @param name
   */
  public void removeItem(String key) {
    this.clientStorage.removeItem(key);
  }

  public void setItem(String key, String data) {
    GWT.log("setItem Key: " + key);
    GWT.log("setItem Data :" + data);
    this.clientStorage.setItem(key, data);
  }

  public void setVisitCount(String serviceName, int visitCount) {
    this.clientStorage.setItem(serviceName, Integer.toString(visitCount));
  }
}
