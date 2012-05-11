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
    return INSTANCE;
  }

  private final ClientStorage clientStorage;

  private PersistenceManager() {
    if (LocalStorageWrapper.isSupported()) {
      System.out.println("use Local Storage");
      clientStorage = new LocalStorageWrapper();
    }
    else {
      clientStorage = new CookieWrapper();
    }
  }

  public void addVisitCount(String serviceName) {
    setVisitCount(serviceName, getVisitCount(serviceName) + 1);
  }

  public String getItem(String key) {
    return clientStorage.getItem(key);
  }

  public List<String> getItemKeys() {
    return clientStorage.getItemKeys();
  }

  public int getVisitCount(String serviceName) {
    if (clientStorage.getItem(serviceName) == null) {
      return 0;
    } else {
      return Integer.parseInt(clientStorage.getItem(serviceName));
    }
  }

  public void setItem(String key, String data) {
    clientStorage.setItem(key, data);
  }

  public void setVisitCount(String serviceName, int visitCount) {
    clientStorage.setItem(serviceName, Integer.toString(visitCount));
  }
}
