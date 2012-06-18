package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.controller.DtcTestPageController;
import net.skcomms.dtc.client.controller.DtcUrlCopyController;
import net.skcomms.dtc.client.controller.IpHistoryController;
import net.skcomms.dtc.client.controller.LastRequestLoaderController;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcNavigationBarView;
import net.skcomms.dtc.client.view.DtcTestPageView;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;
import net.skcomms.dtc.client.view.DtcUserSignInView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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

  private final String BASE_URL = calculateBaseUrl();

  private final String DTC_PROXY_URL = BASE_URL + "_dtcproxy_/";

  private final DtcTestPageController dtcTestPageConroller = new DtcTestPageController();

  private final DtcTestPageView dtcTestPageView = new DtcTestPageView();

  private final LastRequestLoaderController requestRecaller = new LastRequestLoaderController();

  private final FlowPanel dtcNodePanel = createFlowPanel();

  private final FlowPanel dtcfavoriteNodePanel = createFlowPanel();

  private DtcNavigationBarView navigationBar;

  private final DtcUserSignInView usernameSubmissionManager = new DtcUserSignInView();

  private final IpHistoryController ipHistoryManager = new IpHistoryController();

  private final List<DtcArdbegObserver> dtcArdbegObservers = new ArrayList<DtcArdbegObserver>();

  private String currentPath;

  public void addDtcArdbegObserver(DtcArdbegObserver observer) {
    dtcArdbegObservers.add(observer);
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

  protected FlowPanel createFlowPanel() {
    return new FlowPanel();
  }

  void displayDirectoryPage() {
    RootPanel.get("favoriteNodeContainer").setVisible(false);
    RootPanel.get("nodeContainer").setVisible(true);
    RootPanel.get("dtcContainer").setVisible(false);
  }

  void displayHomePage() {
    RootPanel.get("favoriteNodeContainer").setVisible(true);
    RootPanel.get("nodeContainer").setVisible(true);
    RootPanel.get("dtcContainer").setVisible(false);
  }

  private void displayTestPage() {
    RootPanel.get("nodeContainer").setVisible(false);
    RootPanel.get("favoriteNodeContainer").setVisible(false);

    RootPanel.get("dtcContainer").setVisible(true);

  }

  public void fireDtcHomePageLoaded() {
    currentPath = "/";
    for (DtcArdbegObserver observer : dtcArdbegObservers) {
      observer.onDtcHomeLoaded();
    }

    hideSplash();
    displayHomePage();
  }

  public void fireDtcServiceDirectoryPageLoaded(String path) {
    currentPath = path;
    for (DtcArdbegObserver observer : dtcArdbegObservers) {
      observer.onDtcDirectoryLoaded(path);
    }

    hideSplash();
    displayDirectoryPage();

    String[] nodes = path.split("/");
    if (nodes.length == 2) {
      String serviceName = nodes[1];
      PersistenceManager.getInstance().addVisitCount(serviceName);
    }
  }

  public void fireDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    currentPath = requestInfo.getPath();
    for (DtcArdbegObserver observer : dtcArdbegObservers) {
      observer.onDtcTestPageLoaded(requestInfo);
    }

    hideSplash();
    displayTestPage();
  }

  public String getBaseUrl() {
    return BASE_URL;
  }

  public String getCurrentPath() {
    return currentPath;
  }

  public String getDtcProxyUrl() {
    return DTC_PROXY_URL;
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
    initializeDtcFrame();
    initializeDtcNodeContainer();

    ipHistoryManager.initialize(this);
    initializeNavigationBar();
    initializeTestPage();

    initializeUrlCopy();
    usernameSubmissionManager.initialize();
    requestRecaller.initialize(this);
  }

  private void initializeDtcFrame() {
    // displayHomePage();

    setDtcFramePath("/");
  }

  private void initializeDtcNodeContainer() {

    DtcNodeModel.getInstance().initialize(this);

    dtcNodePanel.add(DtcNodeModel.getInstance().getDtcNodeCellList());
    dtcfavoriteNodePanel.add(DtcNodeModel.getInstance().getDtcFavoriteNodeCellList());

    Label dtcNodePanelLabel = new Label();
    dtcNodePanelLabel.setText("Services");

    Label dtcFavoriteNodePanelLabel = new Label();
    dtcFavoriteNodePanelLabel.setText("Favorites");

    RootPanel.get("nodeContainer").add(dtcNodePanelLabel);
    RootPanel.get("nodeContainer").add(dtcNodePanel);

    RootPanel.get("favoriteNodeContainer").add(dtcFavoriteNodePanelLabel);
    RootPanel.get("favoriteNodeContainer").add(dtcfavoriteNodePanel);
  }

  private void initializeNavigationBar() {
    navigationBar = new DtcNavigationBarView();
    navigationBar.initialize(this);
  }

  private void initializeTestPage() {
    dtcTestPageConroller.initialize(this, dtcTestPageView);
  }

  private void initializeUrlCopy() {
    DtcUrlCopyButtonView button = new DtcUrlCopyButtonView();
    DtcUrlCopyDialogBoxView dialogBox = new DtcUrlCopyDialogBoxView();
    DtcUrlCopyController controller = new DtcUrlCopyController();

    controller.initialize(this, dtcTestPageView, button, dialogBox);
  }

  public void onLoadDtcResponseFrame(boolean success) {
    for (DtcArdbegObserver observer : dtcArdbegObservers) {
      observer.onDtcResponseFrameLoaded(success);
    }
  }

  @Override
  public void onModuleLoad() {
    initializeComponents();
  }

  public void onSubmitRequestForm() {
    for (DtcArdbegObserver observer : dtcArdbegObservers) {
      observer.onSubmitRequestForm();
    }
  }

  public void removeDtcArdbegObserver(DtcArdbegObserver observer) {
    dtcArdbegObservers.remove(observer);
  }

  /**
   * @param path
   */
  public void setDtcFramePath(String path) {
    showSplash();

    DtcPageType type = DtcArdbeg.getTypeOfSelected(path, !path.endsWith("/"));

    if (type == DtcPageType.HOME) {
      DtcNodeModel.getInstance().refreshDtcHomePageNode();
    }
    else if (type == DtcPageType.DIRECTORY) {
      DtcNodeModel.getInstance().refreshDtcDirectoryPageNode(path);
    }
    else if (type == DtcPageType.TEST) {
      DtcNodeModel.getInstance().refreshDtcTestPage(path);
    }
  }

  public void showSplash() {
    RootPanel.get("loading").setVisible(true);
  }
}
