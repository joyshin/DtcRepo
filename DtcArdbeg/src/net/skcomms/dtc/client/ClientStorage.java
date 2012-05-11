package net.skcomms.dtc.client;

import java.util.List;

public interface ClientStorage {

  String getItem(String key);

  List<String> getItemKeys();

  void setItem(String key, String data);

}
