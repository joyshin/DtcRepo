package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.UserConfigModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DtcUserConfigServiceAsync {

  void getUserConfig(String userId, AsyncCallback<UserConfigModel> callback);

  void setUserConfig(String userId, UserConfigModel userConfig, AsyncCallback<Void> callback);

}
