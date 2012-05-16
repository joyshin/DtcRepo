package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.UserConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcConfigManager {
  private static final DtcConfigManager INSTANCE = new DtcConfigManager();

  public static DtcConfigManager getInstance() {
    return DtcConfigManager.INSTANCE;
  }

  private UserConfig userConfig;
  private final List<DtcUserConfigObserver> observers = new ArrayList<DtcUserConfigObserver>();

  private DtcConfigManager() {
  }

  public void addUserConfigObserver(DtcUserConfigObserver observer) {
    observers.add(observer);
  }

  private void notifyObservers() {
    for (DtcUserConfigObserver observer : observers) {
      observer.onChangeUserConfig();
    }
  }

  public void removeUserConfigObserver(DtcUserConfigObserver observer) {
    observers.remove(observer);
  }

  public void setUsername(String userId) {
    if (userId == null || userId.isEmpty()) {
      System.out.println("Empty ID");
      userConfig = UserConfig.EMPTY_CONFIG;
    } else {
      System.out.println("IDIDIDIDIDIDIDID");
      DtcUserConfigService.Util.getInstance().getUserConfig(userId,
          new AsyncCallback<UserConfig>() {
            @Override
            public void onFailure(Throwable caught) {
              System.out.println(caught.toString());
              userConfig = UserConfig.EMPTY_CONFIG;
              notifyObservers();
            }

            @Override
            public void onSuccess(UserConfig result) {
              System.out.println("UserConfig:" + result);
              userConfig = result;
              notifyObservers();
            }
          });
    }
  }
}
