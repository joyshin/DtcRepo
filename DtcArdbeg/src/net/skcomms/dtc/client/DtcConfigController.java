package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.UserConfigModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcConfigController {
  private static final DtcConfigController INSTANCE = new DtcConfigController();

  public static DtcConfigController getInstance() {
    return DtcConfigController.INSTANCE;
  }

  private UserConfigModel userConfig;
  
  private final List<DtcUserConfigObserver> observers = new ArrayList<DtcUserConfigObserver>();

  private DtcConfigController() {
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
      userConfig = UserConfigModel.EMPTY_CONFIG;
    } else {
      System.out.println("IDIDIDIDIDIDIDID");
      DtcUserConfigService.Util.getInstance().getUserConfig(userId,
          new AsyncCallback<UserConfigModel>() {
            @Override
            public void onFailure(Throwable caught) {
              System.out.println(caught.toString());
              userConfig = UserConfigModel.EMPTY_CONFIG;
              notifyObservers();
            }

            @Override
            public void onSuccess(UserConfigModel result) {
              System.out.println("UserConfig:" + result);
              userConfig = result;
              notifyObservers();
            }
          });
    }
  }
  

}
