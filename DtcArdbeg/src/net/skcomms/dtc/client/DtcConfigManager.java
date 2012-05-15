package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.UserConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcConfigManager {
  private static final DtcConfigManager INSTANCE = new DtcConfigManager();

  public static DtcConfigManager getInstance() {
    return INSTANCE;
  }

  private UserConfig userConfig;

  private DtcConfigManager() {
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
            }

            @Override
            public void onSuccess(UserConfig result) {
              System.out.println("okokokokokokokokok");
              userConfig = result;
            }
          });
    }
  }
}
