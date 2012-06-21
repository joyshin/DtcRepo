package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.controller.DtcNodeController;
import net.skcomms.dtc.client.controller.DtcTestPageController;
import net.skcomms.dtc.client.controller.DtcUrlCopyController;
import net.skcomms.dtc.client.controller.IpHistoryController;
import net.skcomms.dtc.client.controller.LastRequestLoaderController;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcNavigationBarView;
import net.skcomms.dtc.client.view.DtcNodeView;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;
import net.skcomms.dtc.client.view.DtcUserSignInView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

  public enum DtcPageType {
    HOME, DIRECTORY, TEST;
  }

  public static class Pair<K, V> {

    private final K key;

    private final V value;

    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return this.key;
    }

    /**
     * @return
     */
    public V getValue() {
      return this.value;
    }
  }

  /**
   * 선택된 아이템의 DtcPageType을 가져온다.
   * 
   * @param path
   *          이동할 페이지 경로
   * @param isLeaf
   *          True: Test, False: 나머지
   * @return DtcPageType
   */
  public static DtcPageType getTypeOfSelected(String path, boolean isLeaf) {
    if (isLeaf == true) {
      return DtcPageType.TEST;
    } else {
      if (path.equals("/")) {
        return DtcPageType.HOME;
      } else {
        return DtcPageType.DIRECTORY;
      }
    }
  }

  private final String BASE_URL = this.calculateBaseUrl();

  private final String DTC_PROXY_URL = this.BASE_URL + "_dtcproxy_/";

  private final DtcTestPageView dtcTestPageView = new DtcTestPageView();

  private final DtcUserSignInView usernameSubmissionManager = new DtcUserSignInView();

  private final List<DtcArdbegObserver> dtcArdbegObservers = new ArrayList<DtcArdbegObserver>();

  private String currentPath;

  private final DtcNodeModel dtcArdbegNodeModel = DtcNodeModel.getInstance();

  private DtcNodeView dtcNodeView;

  private DtcNodeView dtcFavoriteNodeView;

  public void addDtcArdbegObserver(DtcArdbegObserver observer) {
    this.dtcArdbegObservers.add(observer);
  }

  protected String calculateBaseUrl() {
    int queryStringStart = Document.get().getURL().indexOf('?');
    int index;
    if (queryStringStart == -1) {
      index = Document.get().getURL().lastIndexOf('/');
    }
    else {
      index = Document.get().getURL().lastIndexOf('/', queryStringStart);
    }
    return Document.get().getURL().substring(0, index + 1);
  }

  void displayDirectoryPage() {
    this.dtcFavoriteNodeView.setVisible(false);
    this.dtcNodeView.setVisible(true);
    RootPanel.get("dtcContainer").setVisible(false);
  }

  void displayHomePage() {
    this.dtcFavoriteNodeView.setVisible(true);
    this.dtcNodeView.setVisible(true);
    RootPanel.get("dtcContainer").setVisible(false);
  }

  private void displayTestPage() {
    this.dtcFavoriteNodeView.setVisible(false);
    this.dtcNodeView.setVisible(false);
    RootPanel.get("dtcContainer").setVisible(true);

  }

  public void fireDtcHomePageLoaded() {
    this.currentPath = "/";
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcHomeLoaded();
    }

    this.hideSplash();
    this.displayHomePage();
  }

  public void fireDtcServiceDirectoryPageLoaded(String path) {
    this.currentPath = path;
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcDirectoryLoaded(path);
    }

    this.hideSplash();
    this.displayDirectoryPage();

    String[] nodes = path.split("/");
    if (nodes.length == 2) {
      String serviceName = nodes[1];
      PersistenceManager.getInstance().addVisitCount(serviceName);
    }
  }

  public void fireDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.currentPath = requestInfo.getPath();
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcTestPageLoaded(requestInfo);
    }

    this.hideSplash();
    this.displayTestPage();
  }

  public String getCurrentPath() {
    return this.currentPath;
  }

  public String getDtcProxyUrl() {
    return this.DTC_PROXY_URL;
  }

  /**
   * @return
   */
  public String getHref() {
    return Window.Location.getHref();
  }

  public Map<String, List<String>> getRequestParameters() {
    return Window.Location.getParameterMap();
  }

  public void hideSplash() {
    RootPanel.get("loading").setVisible(false);
  }

  protected void initializeComponents() {
    this.initializeDtcNodeView();
    this.initializePath();

    this.initializeIpHistory();
    this.initializeNavigationBar();
    this.initializeTestPage();

    this.initializeUrlCopy();
    this.usernameSubmissionManager.initialize();
  }

  private void initializeDtcNodeView() {
    this.dtcNodeView = new DtcNodeView();
    this.dtcFavoriteNodeView = new DtcNodeView();
    this.dtcNodeView.initialize("Services", "nodeContainer");
    this.dtcFavoriteNodeView.initialize("Favorites", "favoriteNodeContainer");
    new DtcNodeController().initialize(this.dtcNodeView, this.dtcFavoriteNodeView);
    DtcNodeModel.getInstance().initialize(this);
  }

  private void initializeIpHistory() {
    new IpHistoryController().initialize(this);
  }

  private void initializeNavigationBar() {
    new DtcNavigationBarView().initialize(this);
  }

  private void initializePath() {
    String path;
    if (Window.Location.getParameter("path") != null) {
      path = "/" + Window.Location.getParameter("path");
    } else {
      path = "/";
    }
    this.setPath(path);
  }

  private void initializeTestPage() {
    LastRequestLoaderController lastRequestLoaderController = new LastRequestLoaderController();
    new DtcTestPageController().initialize(this, this.dtcTestPageView, lastRequestLoaderController);
  }

  private void initializeUrlCopy() {
    DtcUrlCopyButtonView button = new DtcUrlCopyButtonView();
    DtcUrlCopyDialogBoxView dialogBox = new DtcUrlCopyDialogBoxView();
    DtcUrlCopyController controller = new DtcUrlCopyController();
    controller.initialize(this, this.dtcTestPageView, button, dialogBox);
  }

  public void onLoadDtcResponseFrame(boolean success) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcResponseFrameLoaded(success);
    }
  }

  @Override
  public void onModuleLoad() {
    this.initializeComponents();
  }

  public void onSubmitRequestForm() {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onSubmitRequestForm();
    }
  }

  public void removeDtcArdbegObserver(DtcArdbegObserver observer) {
    this.dtcArdbegObservers.remove(observer);
  }

  /**
   * @param path
   */
  public void setPath(String path) {
    this.showSplash();

    DtcPageType type = DtcArdbeg.getTypeOfSelected(path, !path.endsWith("/"));

    if (type == DtcPageType.HOME) {
      this.dtcArdbegNodeModel.refreshDtcHomePageNode();
    } else if (type == DtcPageType.DIRECTORY) {
      this.dtcArdbegNodeModel.refreshDtcDirectoryPageNode(path);
    } else {
      this.dtcArdbegNodeModel.refreshDtcTestPage(path);
    }
  }

  public void showSplash() {
    RootPanel.get("loading").setVisible(true);
  }
}
