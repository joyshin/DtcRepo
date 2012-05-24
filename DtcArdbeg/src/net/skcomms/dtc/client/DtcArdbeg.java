package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.controller.DtcTestPageViewController;
import net.skcomms.dtc.client.controller.DtcUrlCopyController;
import net.skcomms.dtc.client.controller.IpHistoryController;
import net.skcomms.dtc.client.controller.LastRequestLoaderController;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.client.view.DtcChronoView;
import net.skcomms.dtc.client.view.DtcNavigationBarView;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;
import net.skcomms.dtc.client.view.DtcUserSignInView;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

  public enum DtcPageType {
    NONE, HOME, DIRECTORY, TEST;
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

  private final String BASE_URL = this.calculateBaseUrl();

  private final String DTC_PROXY_URL = this.BASE_URL + "_dtcproxy_/";

  private static DtcChronoView dtcChrono = new DtcChronoView();

  public native static void addDtcFormEventHandler(DtcArdbeg module, Document dtcDoc) /*-{
    var inputForm = dtcDoc.getElementsByTagName("frame")[0].contentWindow.document
        .getElementsByTagName("form")[0];
    for (i = 0; i < inputForm.elements.length; i++) {
      var inputElement = inputForm.elements[i];
      if (inputElement.type == "text") {
        inputElement.onkeydown = function(event) {
          if (event.keyCode == 13) {
            module.@net.skcomms.dtc.client.DtcArdbeg::onSubmitRequestForm()();
            this.form.submit();
            this.select();
          }
        }
      }
      ;
    }
  }-*/;

  private static native void addDtcFrameScrollEventHandler(DtcArdbeg ardbeg) /*-{
    if ($doc.cssInserted == null) {
      $doc.cssInserted = true;
      $doc.styleSheets[0]
          .insertRule("div#dtcContainer iframe { background-position: 0px 0px; }", 0);
    }

    dtc = $doc.getElementsByTagName("iframe")[1];
    $doc.styleSheets[0].cssRules[0].style.backgroundPositionY = "-100px";
    dtc.contentWindow.onscroll = function() {
      $doc.styleSheets[0].cssRules[0].style.backgroundPositionY = "-"
          + parseInt((dtc.contentWindow.pageYOffset * 0.02 + 100)) + "px";
      ardbeg.@net.skcomms.dtc.client.DtcArdbeg::onScrollDtcFrame()();
    };
  }-*/;

  public native static void addDtcResponseFrameLoadEventHandler(DtcArdbeg module, Document dtcDoc) /*-{
    var responseFrame = dtcDoc.getElementsByTagName("frame")[1];
    responseFrame.onload = function() {
      var resultFrame = responseFrame.contentDocument.getElementById("xmlresult");
      var successfulSearch = false;

      if (resultFrame != null) {
        var codeElements = resultFrame.contentDocument.getElementsByTagName("Code");

        if (codeElements.length > 0 && codeElements[0].textContent == "100") {
          successfulSearch = true;
        }
      } else {
        form = responseFrame.contentDocument.forms[0];
        var patt = /status: 100/gi;

        if (form.innerText.substring(0, 100).match(patt) != null) {
          successfulSearch = true;
        }
      }
      module.@net.skcomms.dtc.client.DtcArdbeg::onLoadDtcResponseFrame(Z)(successfulSearch);
    }
  }-*/;

  public native static void addDtcSearchButtonEventHandler(DtcArdbeg module, Document dtcDoc) /*-{
    var searchButton = dtcDoc.getElementsByTagName("frame")[0].contentWindow.document
        .getElementById("div_search");
    searchButton.onclick = function() {
      module.@net.skcomms.dtc.client.DtcArdbeg::onSubmitRequestForm()();
    };
  }-*/;

  /**
   * 선택된 아이템의 DtcPageType을 가져온다.
   * 
   * @param path
   *          이동할 페이지 경로
   * 
   * @param isLeaf
   *          True: Test, False: 나머지
   * 
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

  private static void sortServicesByVisitCount(List<Pair<Integer, Node>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, Node>>() {
      @Override
      public int compare(Pair<Integer, Node> arg0, Pair<Integer, Node> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final DtcTestPageViewController dtcTestPageViewConroller = this.createTestPageView();

  private final LastRequestLoaderController requestRecaller = new LastRequestLoaderController();

  private final FlowPanel dtcNodePanel = this.createFlowPanel();

  private final FlowPanel dtcfavoriteNodePanel = this.createFlowPanel();

  private DtcNavigationBarView navigationBar;

  private DtcRequestFormAccessor dtcRequestFormAccessor;

  private final DtcUserSignInView usernameSubmissionManager = new DtcUserSignInView();

  private final IpHistoryController ipHistoryManager = new IpHistoryController(
      this.dtcRequestFormAccessor);

  private final List<DtcArdbegObserver> dtcArdbegObservers = new ArrayList<DtcArdbegObserver>();

  private String currentPath;

  private void addCssLinkIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", this.BASE_URL + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  public void addDtcArdbegObserver(DtcArdbegObserver observer) {
    this.dtcArdbegObservers.add(observer);
  }

  private void applyStylesToDtcDirectoryNodes(List<Pair<Integer, Node>> pairs) {
    for (Pair<Integer, Node> pair : pairs) {
      if (pair.getKey() == 0) {
        Element.as(pair.getValue()).setAttribute("style", "color:gray; ");
      }
    }
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

  private String calculateInitialDtcUrl() {
    if (Window.Location.getParameter("b") != null) {
      return this.DTC_PROXY_URL + "?b=" + Window.Location.getParameter("b");
    } else if (Window.Location.getParameter("c") != null) {
      return this.DTC_PROXY_URL + "?c=" + Window.Location.getParameter("c");
    } else {
      return this.DTC_PROXY_URL;
    }
  }

  protected FlowPanel createFlowPanel() {
    return new FlowPanel();
  }

  protected DtcTestPageViewController createTestPageView() {
    return new DtcTestPageViewController();
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

  public String getBaseUrl() {
    return this.BASE_URL;
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

  public void hideSplash() {
    RootPanel.get("loading").setVisible(false);
  }

  protected void initializeComponents() {
    this.initializeDtcFrame();
    this.initializeDtcNodeContainer();

    // this.ipHistoryManager.initialize(this);
    this.initializeNavigationBar();
    this.initializeRequestFormAccessor();
    this.dtcTestPageViewConroller.initialize(this);

    this.initializeUrlCopy();
    this.usernameSubmissionManager.initialize();
    this.requestRecaller.initialize(this);
    new DtcChronoView().initialize(this);
  }

  private void initializeDtcFrame() {
    this.displayHomePage();

    this.setDtcFramePath("/");
  }

  private void initializeDtcNodeContainer() {

    DtcNodeModel.getInstance().initialize(this);

    this.dtcNodePanel.add(DtcNodeModel.getInstance().getDtcNodeCellList());
    this.dtcfavoriteNodePanel.add(DtcNodeModel.getInstance().getDtcFavoriteNodeCellList());

    Label dtcNodePanelLabel = new Label();
    dtcNodePanelLabel.setText("Services");

    Label dtcFavoriteNodePanelLabel = new Label();
    dtcFavoriteNodePanelLabel.setText("Favorites");

    RootPanel.get("nodeContainer").add(dtcNodePanelLabel);
    RootPanel.get("nodeContainer").add(this.dtcNodePanel);

    RootPanel.get("favoriteNodeContainer").add(dtcFavoriteNodePanelLabel);
    RootPanel.get("favoriteNodeContainer").add(this.dtcfavoriteNodePanel);
  }

  /**
     * 
     */
  private void initializeNavigationBar() {
    this.navigationBar = new DtcNavigationBarView();
    this.navigationBar.initialize(this);
  }

  /**
   * 
   */
  private void initializeRequestFormAccessor() {
    this.dtcRequestFormAccessor = new DtcRequestFormAccessor();
    this.dtcRequestFormAccessor.initialize(this);
  }

  private void initializeUrlCopy() {
    DtcUrlCopyButtonView button = new DtcUrlCopyButtonView();
    DtcUrlCopyDialogBoxView dialogBox = new DtcUrlCopyDialogBoxView();
    DtcUrlCopyController controller = new DtcUrlCopyController();

    controller.initialize(this, button, dialogBox);
  }

  private void onLoadDtcResponseFrame(boolean success) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcResponseFrameLoaded(success);
    }
  }

  @Override
  public void onModuleLoad() {
    this.initializeComponents();
  }

  private void onScrollDtcFrame() {
  }

  private void onSubmitRequestForm() {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onSubmittingDtcRequest();
    }
  }

  private void removeComaparePageAnchor(Document doc) {
    Node anchor = doc.getBody().getChild(0);
    doc.getBody().removeChild(anchor);

    Node br = doc.getBody().getChild(0);
    doc.getBody().removeChild(br);

    Node currentDirectoryMessage = doc.getBody().getChild(0);
    doc.getBody().removeChild(currentDirectoryMessage);

    br = doc.getBody().getChild(0);
    doc.getBody().removeChild(br);
  }

  public void removeDtcArdbegObserver(DtcArdbegObserver observer) {
    this.dtcArdbegObservers.remove(observer);
  }

  /**
   * @param path
   */
  public void setDtcFramePath(String path) {
    this.showSplash();

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
