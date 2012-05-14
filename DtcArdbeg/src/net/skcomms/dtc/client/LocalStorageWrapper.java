package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.storage.client.Storage;

public class LocalStorageWrapper implements ClientStorage {

  public static boolean isSupported() {
    return Storage.isLocalStorageSupported();
  }

  @Override
  public String getItem(String key) {
    return Storage.getLocalStorageIfSupported().getItem(key);
  }

  @Override
  public List<String> getItemKeys() {
    List<String> itemKeys = new ArrayList<String>();
    for (int i = 0; i < Storage.getLocalStorageIfSupported().getLength(); i++) {
      itemKeys.add(Storage.getLocalStorageIfSupported().key(i));
    }
    return itemKeys;
  }

  @Override
  public void removeItem(String key) {
    Storage.getLocalStorageIfSupported().removeItem(key);
  }

  @Override
  public void setItem(String key, String data) {
    Storage.getLocalStorageIfSupported().setItem(key, data);
  }
}
