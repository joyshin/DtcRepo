package net.skcomms.dtc.client;

import java.util.List;

public interface ClientStorageModel {

  String getItem(String key);

  List<String> getItemKeys();

  void removeItem(String key);

  void setItem(String key, String data);

}
