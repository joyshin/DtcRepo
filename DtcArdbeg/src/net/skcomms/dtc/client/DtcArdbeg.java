package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    K key;
    V value;

    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private final static String BASE_URL = DtcArdbeg.calculateBaseUrl();

  private final static String DTC_PROXY_URL = DtcArdbeg.BASE_URL + "_dtcproxy_/";

  private static ServiceDao serviceDao = new ServiceDao();

  private native static void addDtcFrameScrollEventHandler(DtcArdbeg ardbeg) /*-{
                                                                             if ($doc.cssInserted == null) {
                                                                             $doc.cssInserted = true;
                                                                             $doc.styleSheets[0]
                                                                             .insertRule(
                                                                             "div#dtcContainer iframe { background-position: 0px 0px; }",
                                                                             0);
                                                                             }

                                                                             dtc = $doc.getElementsByTagName("iframe")[1];
                                                                             $doc.styleSheets[0].cssRules[0].style.backgroundPositionY = "-100px";
                                                                             dtc.contentWindow.onscroll = function() {
                                                                             $doc.styleSheets[0].cssRules[0].style.backgroundPositionY = "-"
                                                                             + parseInt((dtc.contentWindow.pageYOffset * 0.02 + 100))
                                                                             + "px";
                                                                             ardbeg.@net.skcomms.dtc.client.DtcArdbeg::onScrollDtcFrame()();
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

  private static String createNewDtcUrl() {
    String newUrl = "";

    if (Window.Location.getParameter("b") != null) {
      newUrl = DtcArdbeg.DTC_PROXY_URL + "?b=" + Window.Location.getParameter("b");
    } else if (Window.Location.getParameter("c") != null) {
      newUrl = DtcArdbeg.DTC_PROXY_URL + "?c=" + Window.Location.getParameter("c");
    } else {
      newUrl = DtcArdbeg.DTC_PROXY_URL;
    }

    return newUrl;
  }

  public static String getBaseUrl() {
    return DtcArdbeg.BASE_URL;
  }

  public static String getDtcProxyUrl() {
    return DTC_PROXY_URL;
  }

  private static void sortServicesByVisitCount(List<Pair<Integer, Node>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, Node>>() {
      @Override
      public int compare(Pair<Integer, Node> arg0, Pair<Integer, Node> arg1) {
        return -arg0.key.compareTo(arg1.key);
      }
    });
  }

  private final FlowPanel dtcNodePanel = new FlowPanel();

  private final FlowPanel dtcfavoriteNodePanel = new FlowPanel();

  private final Frame dtcFrame = new Frame();

  private final DtcNavigationBar navigationBar = new DtcNavigationBar(DtcArdbeg.DTC_PROXY_URL);

  private final DtcRequestFormAccessor dtcRequestFormAccesser = new DtcRequestFormAccessor();

  private final DtcUrlCopyHelper urlCopyHelper = new DtcUrlCopyHelper();

  private final DtcNodeData dtcArdbegNodeData = DtcNodeData.getInstance();

  /**
   * @param doc
   */
  void addCssIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");

    int index = Document.get().getURL().lastIndexOf('/');
    String path = Document.get().getURL().substring(0, index + 1);
    link.setAttribute("href", path + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  private void addCssLinkIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", DtcArdbeg.BASE_URL + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  private void applyStylesToDtcDirectoryNodes(List<Pair<Integer, Node>> pairs) {
    for (Pair<Integer, Node> pair : pairs) {
      if (pair.key == 0) {
        Element.as(pair.value).setAttribute("style", "color:gray; ");
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
        Document doc = IFrameElement.as(
            DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();

        if (doc == null) {
          return;
        }

        DtcArdbeg.addDtcFrameScrollEventHandler(DtcArdbeg.this);

        DtcArdbeg.this.onLoadDtcFrame(doc);

        DtcPageType type = DtcArdbeg.this.getTypeOfPage(doc.getURL());

        switch (type) {
        case HOME:
          DtcArdbeg.this.onLoadDtcHomePage(doc);
          break;
        case DIRECTORY:
          DtcArdbeg.this.onLoadDtcServiceDirectoryPage(doc);
          break;
        case TEST:
          DtcArdbeg.this.onLoadDtcTestPage();
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
      if (prevScore != 0 && pair.key.equals(0)) {
        newTableBody.appendChild(oldTableBody.getFirstChild());
      }
      newTableBody.appendChild(pair.value);
      prevScore = pair.key;
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

  public String getDtcFrameSrc() {
    return IFrameElement.as(
        DtcArdbeg.this.dtcFrame.getElement()).getContentDocument().getURL();
  }

  public Map<String, String> getDtcRequestParameters() {
    return this.dtcRequestFormAccesser.getDtcRequestParameters();
  }

  private String getParameterFromDtcFrame(String name) {
    return this.getParameterMapFromDtcFrame().get(name);
  }

  private Map<String, String> getParameterMapFromDtcFrame() {
    Map<String, String> params = new HashMap<String, String>();
    String url = this.getDtcFrameSrc();
    String queryString = "";
    if (url.indexOf('?') != -1) {
      queryString = url.substring(url.indexOf('?') + 1);
    }
    String[] dtcParams = queryString.split("&");
    for (String param : dtcParams) {
      String[] entry = param.split("=");
      if (entry.length < 2) {
        params.put(entry[0], null);
      } else {
        params.put(entry[0], entry[1]);
      }
    }
    return params;
  }

  protected DtcPageType getTypeOfPage(String url) {
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
    return !rows.isEmpty() && rows.get(0).key > 0;
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
    dtcNodePanelLabel.setText("Services"); // 한글 인코딩 문제 발견

    Label dtcFavoriteNodePanelLabel = new Label();
    dtcFavoriteNodePanelLabel.setText("Favorites");

    RootPanel.get("nodeContainer").add(dtcNodePanelLabel);
    RootPanel.get("nodeContainer").add(this.dtcNodePanel);

    RootPanel.get("favoriteNodeContainer").add(dtcFavoriteNodePanelLabel);
    RootPanel.get("favoriteNodeContainer").add(this.dtcfavoriteNodePanel);

  }

  private void loadDtcPage(String path, boolean isLeaf) {
  }

  private void onLoadDtcFrame(Document doc) {
    this.updateNavigationBar(doc);
    this.dtcRequestFormAccesser.update();
  }

  /**
   * @param doc
   * 
   */
  private void onLoadDtcHomePage(Document doc) {
    this.addCssLinkIntoDtcFrame(doc);
    this.removeComaparePageAnchor(doc);

    this.dtcArdbegNodeData.refreshDtcNode("/");
    this.displayHomePage();
    // this.sortDtcNodes();
  }

  /**
   * @param doc
   */
  private void onLoadDtcServiceDirectoryPage(Document doc) {

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

  protected void onLoadDtcTestPage() {
    String ardbegParam = Window.Location.getParameter("c");
    String dtcFrameParam = this.getParameterFromDtcFrame("c");
    GWT.log("ardbeg:" + ardbegParam + ", dtc:" + dtcFrameParam);
    if (ardbegParam != null && ardbegParam.equals(dtcFrameParam)) {
      this.setUrlParameters();
    }

    this.displayTestPage();
  }

  @Override
  public void onModuleLoad() {
    this.navigationBar.initialize(this);
    this.urlCopyHelper.initialize(this);
    this.initializeDtcFrame();
    this.initializeDtcNodeContainer();
  }

  private void onScrollDtcFrame() {
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

  public void setDtcFrameUrl(String href) {
    this.dtcFrame.setUrl(href);
  }

  private void setUrlParameters() {
    Set<Entry<String, List<String>>> paramValues = Window.Location.getParameterMap().entrySet();
    for (Entry<String, List<String>> entry : paramValues) {
      this.dtcRequestFormAccesser.setDtcRequestParameter(entry.getKey(), entry.getValue().get(0));
    }
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
        Document doc = IFrameElement.as(DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();
        Element oldTableBody = doc.getElementsByTagName("tbody").getItem(0);

        List<Pair<Integer, Node>> rows = DtcArdbeg.this.createTableRows(nodeInfos);
        DtcArdbeg.sortServicesByVisitCount(rows);
        DtcArdbeg.this.applyStylesToDtcDirectoryNodes(rows);
        Element sortedBody = DtcArdbeg.this.createSortedTableBody(doc, rows);

        oldTableBody.getParentNode().replaceChild(sortedBody, oldTableBody);
      }
    });
  }

  private void updateNavigationBar(Document doc) {
    this.navigationBar.addPath(doc.getURL());
  }
}
