package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

  private static class Pair<K, V> {
    K key;

    V value;

    /**
     * @param name
     * @param node
     */
    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private final static String BASE_URL;
  static {
    int queryStringStart = Document.get().getURL().lastIndexOf('?');
    int index;
    if (queryStringStart == -1) {
      index = Document.get().getURL().lastIndexOf('/');
    }
    else {
      index = Document.get().getURL().lastIndexOf('/', queryStringStart);
    }
    BASE_URL = Document.get().getURL().substring(0, index + 1);
  }

  private final static String DTC_HOME_URL = DtcArdbeg.BASE_URL + "_dtcproxy_/";

  private static ServiceDao serviceDao = new ServiceDao();

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

  private static String createNewDtcUrl() {
    String newUrl = "";

    if (Window.Location.getParameter("b") != null) {
      newUrl = DtcArdbeg.DTC_HOME_URL + "?b=" + Window.Location.getParameter("b");
    } else if (Window.Location.getParameter("c") != null) {
      newUrl = DtcArdbeg.DTC_HOME_URL + "?c=" + Window.Location.getParameter("c");
    } else {
      newUrl = DtcArdbeg.DTC_HOME_URL;
    }

    return newUrl;
  }

  public static String getBaseUrl() {
    return DtcArdbeg.BASE_URL;
  }

  private static void sortServicesByVisitCount(List<Pair<Integer, Node>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, Node>>() {
      @Override
      public int compare(Pair<Integer, Node> arg0,
          Pair<Integer, Node> arg1) {
        return -arg0.key.compareTo(arg1.key);
      }
    });
  }

  private final Frame dtcFrame = new Frame();

  private final DtcNavigationBar navigationBar = new DtcNavigationBar(DtcArdbeg.DTC_HOME_URL);

  /**
   * @param doc
   */
  private void addCssIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", DtcArdbeg.BASE_URL + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  private void applyStylesToServiceRows(List<Pair<Integer, Node>> rows) {
    for (Pair<Integer, Node> pair : rows) {
      if (pair.key == 0) {
        Element.as(pair.value).setAttribute("style", "color:gray; ");
      }
    }
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

  private boolean hasVisitedService(List<Pair<Integer, Node>> rows) {
    return !rows.isEmpty() && rows.get(0).key > 0;
  }

  private void initializeDtcFrame() {
    this.dtcFrame.setPixelSize(Window.getClientWidth() - 30, Window.getClientHeight() - 135);

    String dtcUrl = DtcArdbeg.createNewDtcUrl();
    this.dtcFrame.setUrl(dtcUrl);
    GWT.log("url => " + dtcUrl);

    this.dtcFrame.addLoadHandler(new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        Document doc = IFrameElement.as(
            DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();

        if (doc == null) {
          return;
        }

        DtcArdbeg.addDtcFrameScrollEventHandler(DtcArdbeg.this);

        DtcArdbeg.this.onLoadDtcFrame(doc);

        if (doc.getURL().equals(DtcArdbeg.DTC_HOME_URL)) {
          DtcArdbeg.this.onLoadDtcHomePage(doc);
        }

        int index = doc.getURL().indexOf("?b=");
        if (index != -1) {
          String serviceName = doc.getURL().substring(index + 3)
              .replaceAll("/", "");
          DtcArdbeg.this.onLoadDtcServiceDirectoryPage(doc, serviceName);
        }

        index = doc.getURL().indexOf("?c=");
        if (index != -1) {
          DtcArdbeg.this.onLoadDtcTestPage();
        }
      }

    });

    RootPanel.get("dtcContainer").add(this.dtcFrame);

    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        DtcArdbeg.this.dtcFrame.setPixelSize(Window.getClientWidth() - 30,
            Window.getClientHeight() - 200);
      }
    });
  }

  /**
   * @param doc
   */
  private void onLoadDtcFrame(Document doc) {
    this.updateNavigationBar(doc);
  }

  /**
   * @param doc
   * 
   */
  private void onLoadDtcHomePage(Document doc) {
    this.addCssIntoDtcFrame(doc);
    this.removeComaparePageAnchor(doc);
    this.sortServices();
  }

  /**
   * @param doc
   * @param serviceName
   */
  private void onLoadDtcServiceDirectoryPage(Document doc, String serviceName) {
    this.addCssIntoDtcFrame(doc);
    if (doc.getReferrer().equals(DtcArdbeg.DTC_HOME_URL)) {
      DtcArdbeg.serviceDao.addVisitCount(serviceName);
    }
  }

  protected void onLoadDtcTestPage() {
    RequestParameterSetter.execute();
  }

  @Override
  public void onModuleLoad() {
    this.initializeDtcFrame();
    this.navigationBar.initialize(this);
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

  /**
   * @param href
   */
  public void setDtcFrameUrl(String href) {
    this.dtcFrame.setUrl(href);
  }

  /**
	 * 
	 */
  void sortServices() {
    String path = "/";

    DtcService.Util.getInstance().getDir(path, new AsyncCallback<List<DtcNodeInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeInfo> result) {
        Document doc = IFrameElement.as(DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();
        Element oldTableBody = doc.getElementsByTagName("tbody").getItem(0);

        List<Pair<Integer, Node>> rows = DtcArdbeg.this.createTableRows(result);
        DtcArdbeg.sortServicesByVisitCount(rows);
        DtcArdbeg.this.applyStylesToServiceRows(rows);
        Element sortedBody = DtcArdbeg.this.createSortedTableBody(doc, rows);

        oldTableBody.getParentNode().replaceChild(sortedBody, oldTableBody);
      }
    });

  }

  private void updateNavigationBar(Document doc) {
    this.navigationBar.addPath(doc.getURL());
  }

}
