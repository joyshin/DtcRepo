package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.UserConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DtcUserConfigServiceAsync {

  void getUserConfig(String userId, AsyncCallback<UserConfig> callback);

  void setUserConfig(String userId, UserConfig userConfig, AsyncCallback<Void> callback);

}
