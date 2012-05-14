/**
 * 
 */
package net.skcomms.dtc.client;

import java.util.List;

/**
 * @author jujang@sk.com
 * 
 */
public class PersistenceManager {

  private static final PersistenceManager INSTANCE = new PersistenceManager();

  public static PersistenceManager getInstance() {
    return PersistenceManager.INSTANCE;
  }

  private final ClientStorage clientStorage;

  private PersistenceManager() {
    if (LocalStorageWrapper.isSupported()) {
      System.out.println("use Local Storage");
      this.clientStorage = new LocalStorageWrapper();
    }
    else {
      this.clientStorage = new CookieWrapper();
    }
  }

  public void addVisitCount(String serviceName) {
    this.setVisitCount(serviceName, this.getVisitCount(serviceName) + 1);
  }

  public String getItem(String key) {
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
    this.clientStorage.setItem(key, data);
  }

  public void setVisitCount(String serviceName, int visitCount) {
    this.clientStorage.setItem(serviceName, Integer.toString(visitCount));
  }
}
