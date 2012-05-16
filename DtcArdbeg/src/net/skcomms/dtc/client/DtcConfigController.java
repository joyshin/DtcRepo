package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.UserConfigModel;
import net.skcomms.dtc.shared.UserConfigModel.UserConfigView;

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
    this.observers.add(observer);
  }

  public UserConfigView getUserConfigView() {
    return this.userConfig.getView();
  }

  private void notifyObservers() {
    for (DtcUserConfigObserver observer : this.observers) {
      observer.onChangeUserConfig();
    }
  }

  public void removeUserConfigObserver(DtcUserConfigObserver observer) {
    this.observers.remove(observer);
  }

  public void setUsername(String userId) {
    if (userId == null || userId.isEmpty()) {
      System.out.println("Empty ID");
      this.userConfig = UserConfigModel.EMPTY_CONFIG;
    } else {
      System.out.println("IDIDIDIDIDIDIDID");
      DtcUserConfigService.Util.getInstance().getUserConfig(userId,
          new AsyncCallback<UserConfigModel>() {
            @Override
            public void onFailure(Throwable caught) {
              System.out.println(caught.toString());
              DtcConfigController.this.userConfig = UserConfigModel.EMPTY_CONFIG;
              DtcConfigController.this.notifyObservers();
            }

            @Override
            public void onSuccess(UserConfigModel result) {
              System.out.println("UserConfig:" + result);
              DtcConfigController.this.userConfig = result;
              DtcConfigController.this.notifyObservers();
            }
          });
    }
  }

}
