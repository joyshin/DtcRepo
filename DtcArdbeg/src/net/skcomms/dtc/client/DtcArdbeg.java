package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

  public class DtcFrameSrcProvider {
    public String get() {
      return DtcArdbeg.this.getDtcFrameSrc();
    }
  }

  public enum DtcPageType {
    NONE, HOME, DIRECTORY, TEST;
  }

  private static class Pair<K, V> {
    private K key;
    private V value;

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

  private final static String BASE_URL = DtcArdbeg.calculateBaseUrl();

  private final static String DTC_PROXY_URL = DtcArdbeg.BASE_URL + "_dtcproxy_/";

  private static ServiceDao serviceDao = new ServiceDao();

  private static DtcChrono dtcChrono = new DtcChrono();

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

  private static String calculateBaseUrl() {
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

  public static String getBaseUrl() {
    return DtcArdbeg.BASE_URL;
  }

  public static String getDtcProxyUrl() {
    return DtcArdbeg.DTC_PROXY_URL;
  }

  private static void sortServicesByVisitCount(List<Pair<Integer, Node>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, Node>>() {
      @Override
      public int compare(Pair<Integer, Node> arg0, Pair<Integer, Node> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final CookieHandler cookieHandler = new CookieHandler();

  private final FlowPanel dtcNodePanel = new FlowPanel();

  private final FlowPanel dtcfavoriteNodePanel = new FlowPanel();

  private final Frame dtcFrame = new Frame();

  private final DtcNavigationBar navigationBar = new DtcNavigationBar(DtcArdbeg.DTC_PROXY_URL);

  final DtcRequestFormAccessor dtcRequestFormAccessor = new DtcRequestFormAccessor();

  private final DtcUrlCopyManager urlCopyManager = new DtcUrlCopyManager();

  private final IpHistoryManager ipHistoryManager = new IpHistoryManager(
      this.dtcRequestFormAccessor);

  private final List<DtcArdbegObserver> dtcArdbegObservers = new ArrayList<DtcArdbegObserver>();

  private final DtcNodeData dtcArdbegNodeData = DtcNodeData.getInstance();

  private void addCssLinkIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", DtcArdbeg.BASE_URL + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  void addDtcArdbegObserver(DtcArdbegObserver observer) {
    this.dtcArdbegObservers.add(observer);
  }

  private void applyStylesToDtcDirectoryNodes(List<Pair<Integer, Node>> pairs) {
    for (Pair<Integer, Node> pair : pairs) {
      if (pair.getKey() == 0) {
        Element.as(pair.getValue()).setAttribute("style", "color:gray; ");
      }
    }
  }

  private String calculateInitialDtcUrl() {
    if (Window.Location.getParameter("b") != null) {
      return DtcArdbeg.DTC_PROXY_URL + "?b=" + Window.Location.getParameter("b");
    } else if (Window.Location.getParameter("c") != null) {
      return DtcArdbeg.DTC_PROXY_URL + "?c=" + Window.Location.getParameter("c");
    } else {
      return DtcArdbeg.DTC_PROXY_URL;
    }
  }

  private LoadHandler createDtcFrameLoadHandler() {
    return new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        Document doc = DtcArdbeg.this.getDtcFrameDoc();

        if (doc == null) {
          return;
        }

        DtcArdbeg.addDtcFrameScrollEventHandler(DtcArdbeg.this);

        DtcPageType type = DtcArdbeg.this.getPageType(doc.getURL());

        switch (type) {
        case HOME:
          DtcArdbeg.this.onLoadDtcHomePage(doc);
          break;
        case DIRECTORY:
          DtcArdbeg.this.onLoadDtcServiceDirectoryPage(doc);
          break;
        case TEST:
          DtcArdbeg.this.onLoadDtcTestPage(doc);
          break;
        default:
          GWT.log("Invalid Page Type: " + doc.getURL());
          break;
        }
      }
    };
  }

  private ResizeHandler createDtcFrameResizeHandler() {
    return new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        DtcArdbeg.this.dtcFrame.setPixelSize(Window.getClientWidth() - 30,
            Window.getClientHeight() - 200);
      }
    };
  }

  private Element createSortedTableBody(Document doc, List<Pair<Integer, Node>> rows) {
    Element newTableBody = doc.createElement("tbody");
    Element oldTableBody = doc.getElementsByTagName("tbody").getItem(0);

    if (this.hasVisitedService(rows)) {
      newTableBody.appendChild(oldTableBody.getFirstChild().cloneNode(true));
    }

    int prevScore = 1;
    for (Pair<Integer, Node> pair : rows) {
      if (prevScore != 0 && pair.getKey().equals(0)) {
        newTableBody.appendChild(oldTableBody.getFirstChild());
      }
      newTableBody.appendChild(pair.getValue());
      prevScore = pair.getKey();
    }
    return newTableBody;
  }

  private List<Pair<Integer, Node>> createTableRows(List<DtcNodeInfo> nodeInfos) {
    Document doc = IFrameElement.as(this.dtcFrame.getElement()).getContentDocument();
    List<Pair<Integer, Node>> rows = new ArrayList<Pair<Integer, Node>>();

    for (DtcNodeInfo nodeInfo : nodeInfos) {
      TableRowElement row = doc.createTRElement();
      TableCellElement cell = doc.createTDElement();
      if (nodeInfo.isLeaf()) {
        String href = "?c=" + nodeInfo.getPath().substring(1);
        AnchorElement a = doc.createAnchorElement();
        a.setHref(href);
        a.setInnerText(nodeInfo.getName());
        cell.appendChild(a);

        cell.appendChild(doc.createTextNode(" "));

        a = doc.createAnchorElement();
        a.setHref(href);
        a.setTarget("_blank");
        ImageElement image = doc.createImageElement();
        image.setSrc("http://dtc.skcomms.net/newwindow.png");
        image.setAttribute("border", "0");
        image.setTitle("새창열기");
        a.appendChild(image);
        cell.appendChild(a);
      } else {
        AnchorElement a = doc.createAnchorElement();
        a.setHref("?b=" + nodeInfo.getPath().substring(1));
        a.setInnerText(nodeInfo.getName());
        cell.appendChild(a);
      }
      row.appendChild(cell);

      cell = doc.createTDElement();
      cell.setInnerText(nodeInfo.getDescription());
      row.appendChild(cell);

      cell = doc.createTDElement();
      cell.setInnerText(nodeInfo.getUpdateTime());
      row.appendChild(cell);

      Integer score = DtcArdbeg.serviceDao.getVisitCount(nodeInfo.getName());
      rows.add(new Pair<Integer, Node>(score, row));
    }
    return rows;
  }

  private void displayDirecotyPage() {
    RootPanel.get("dtcContainer").setVisible(false);
    RootPanel.get("favoriteNodeContainer").setVisible(false);
    RootPanel.get("nodeContainer").setVisible(true);
  }

  private void displayHomePage() {
    RootPanel.get("dtcContainer").setVisible(false);
    RootPanel.get("nodeContainer").setVisible(true);
    RootPanel.get("favoriteNodeContainer").setVisible(true);
  }

  private void displayTestPage() {
    RootPanel.get("nodeContainer").setVisible(false);
    RootPanel.get("favoriteNodeContainer").setVisible(false);
    RootPanel.get("dtcContainer").setVisible(true);
  }

  Document getDtcFrameDoc() {
    return IFrameElement.as(DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();
  }

  public String getDtcFrameSrc() {
    return this.getDtcFrameDoc().getURL();
  }

  public Map<String, String> getDtcRequestParameters() {
    return this.dtcRequestFormAccessor.getDtcRequestParameters();
  }

  protected DtcPageType getPageType(String url) {
    if (url.equals(DtcArdbeg.DTC_PROXY_URL)) {
      return DtcPageType.HOME;
    }

    int index = url.indexOf("?b=");
    if (index != -1) {
      return DtcPageType.DIRECTORY;
    }

    index = url.indexOf("?c=");
    if (index != -1) {
      return DtcPageType.TEST;
    }
    return DtcPageType.NONE;
  }

  private boolean hasVisitedService(List<Pair<Integer, Node>> rows) {
    return !rows.isEmpty() && rows.get(0).getKey() > 0;
  }

  private void initializeDtcFrame() {
    this.dtcFrame.setPixelSize(Window.getClientWidth() - 30, Window.getClientHeight() - 135);

    this.dtcFrame.addLoadHandler(this.createDtcFrameLoadHandler());

    Window.addResizeHandler(this.createDtcFrameResizeHandler());

    this.displayHomePage();

    RootPanel.get("dtcContainer").add(this.dtcFrame);

    this.dtcFrame.setUrl(this.calculateInitialDtcUrl());
  }

  private void initializeDtcNodeContainer() {

    this.dtcArdbegNodeData.initialize(this);

    this.dtcNodePanel.add(this.dtcArdbegNodeData.getDtcNodeCellList());
    this.dtcfavoriteNodePanel.add(this.dtcArdbegNodeData.getDtcFavoriteNodeCellList());

    Label dtcNodePanelLabel = new Label();
    dtcNodePanelLabel.setText("Services");

    Label dtcFavoriteNodePanelLabel = new Label();
    dtcFavoriteNodePanelLabel.setText("Favorites");

    RootPanel.get("nodeContainer").add(dtcNodePanelLabel);
    RootPanel.get("nodeContainer").add(this.dtcNodePanel);

    RootPanel.get("favoriteNodeContainer").add(dtcFavoriteNodePanelLabel);
    RootPanel.get("favoriteNodeContainer").add(this.dtcfavoriteNodePanel);

  }

  private void onLoadDtcHomePage(Document doc) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcHomeLoaded(doc);
    }

    this.addCssLinkIntoDtcFrame(doc);
    this.removeComaparePageAnchor(doc);

    this.dtcArdbegNodeData.refreshDtcNode("/");
    this.displayHomePage();
  }

  private void onLoadDtcResponseFrame(boolean success) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcResponseFrameLoaded(this.getDtcFrameDoc(), success);
    }
  }

  private void onLoadDtcServiceDirectoryPage(Document doc) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcDirectoryLoaded(doc);
    }

    int index = doc.getURL().indexOf("?b=");
    String directoryPath = doc.getURL().substring(index + 3);
    String serviceName = directoryPath.replaceAll("/", "");

    this.addCssLinkIntoDtcFrame(doc);
    if (doc.getReferrer().equals(DtcArdbeg.DTC_PROXY_URL)) {
      DtcArdbeg.serviceDao.addVisitCount(serviceName);
    }

    this.dtcArdbegNodeData.refreshDtcNode("/" + directoryPath);
    this.displayDirecotyPage();
  }

  private void onLoadDtcTestPage(Document dtcFrameDoc) {
    for (DtcArdbegObserver observer : this.dtcArdbegObservers) {
      observer.onDtcTestPageLoaded(dtcFrameDoc);
    }

    DtcArdbeg.addDtcResponseFrameLoadEventHandler(DtcArdbeg.this, dtcFrameDoc);
    DtcArdbeg.addDtcSearchButtonEventHandler(DtcArdbeg.this, dtcFrameDoc);
    DtcArdbeg.addDtcFormEventHandler(DtcArdbeg.this, dtcFrameDoc);

    this.displayTestPage();
  }

  @Override
  public void onModuleLoad() {
    this.initializeDtcFrame();
    this.initializeDtcNodeContainer();

    this.ipHistoryManager.initialize(this);
    this.navigationBar.initialize(this);
    this.dtcRequestFormAccessor.initialize(this);
    this.urlCopyManager.initialize(this);
    this.cookieHandler.initialize(this);
    new DtcChrono().initialize(this);
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
   * @param href
   */
  public void setDtcFrameUrl(String href) {
    this.dtcFrame.setUrl(href);
  }

  void sortDtcNodes() {
    DtcService.Util.getInstance().getDir("/", new AsyncCallback<List<DtcNodeInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeInfo> nodeInfos) {
        Document doc = DtcArdbeg.this.getDtcFrameDoc();
        Element oldTableBody = doc.getElementsByTagName("tbody").getItem(0);

        List<Pair<Integer, Node>> rows = DtcArdbeg.this.createTableRows(nodeInfos);
        DtcArdbeg.sortServicesByVisitCount(rows);
        DtcArdbeg.this.applyStylesToDtcDirectoryNodes(rows);
        Element sortedBody = DtcArdbeg.this.createSortedTableBody(doc, rows);

        oldTableBody.getParentNode().replaceChild(sortedBody, oldTableBody);
      }
    });
  }
}
