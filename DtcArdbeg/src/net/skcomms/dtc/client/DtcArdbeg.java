package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

  private static class Pair<K, V> {
    /**
     * @param name
     * @param node
     */
    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }

    K key;
    V value;
  }

  // private final static String DTC_HOME_URL = "http://dtc.skcomms.net/";
  private final static String DTC_HOME_URL = "http://127.0.0.1:8888/dtcproxy/";
  private final static ServiceDao serviceDao = new ServiceDao();

  final static Frame dtcFrame = new Frame();
  private DtcNavigationBar navigationBar = new DtcNavigationBar(DtcArdbeg.DTC_HOME_URL);

  @Override
  public void onModuleLoad() {
    this.initializeDtcFrame();
    this.navigationBar.initialize();
  }

  private void initializeDtcFrame() {
    DtcArdbeg.dtcFrame.setPixelSize(Window.getClientWidth() - 30,
        Window.getClientHeight() - 135);
    DtcArdbeg.dtcFrame.setUrl(DtcArdbeg.DTC_HOME_URL);

    DtcArdbeg.dtcFrame.addLoadHandler(new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        Document doc = IFrameElement.as(
            DtcArdbeg.dtcFrame.getElement()).getContentDocument();

        if (doc == null) {
          return;
        }

        DtcArdbeg.this.onLoadDtcFrame(doc);

        if (doc.getURL().equals(DtcArdbeg.DTC_HOME_URL)) {
          DtcArdbeg.this.onLoadDtcHomePage(doc);
        }

        int index = doc.getURL().indexOf("?b=");
        if (index != -1) {
          String serviceName = doc.getURL().substring(index + 3)
              .replaceAll("/", "");
          DtcArdbeg.onLoadDtcServiceDirectoryPage(doc, serviceName);
        }
      }
    });

    RootPanel.get("dtcContainer").add(DtcArdbeg.dtcFrame);

    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        DtcArdbeg.dtcFrame.setPixelSize(Window.getClientWidth() - 30,
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

  private void updateNavigationBar(Document doc) {
    this.navigationBar.addPath(doc.getURL());
  }

  /**
   * @param doc
   * @param serviceName
   */
  protected static void onLoadDtcServiceDirectoryPage(Document doc, String serviceName) {
    // TODO 서비스 선택 판정을 링크 클릭으로 변경해야 함.
    if (doc.getReferrer().equals(DtcArdbeg.DTC_HOME_URL)) {
      DtcArdbeg.serviceDao.addVisitCount(serviceName);
    }
  }

  /**
   * @param doc
   * 
   */
  void onLoadDtcHomePage(Document doc) {
    this.addCssIntoDtcFrame(doc);

    this.removeComaparePageAnchor(doc);

    // this.sortServices();

  }

  /**
   * @param doc
   */
  void addCssIntoDtcFrame(Document doc) {
    LinkElement link = doc.createLinkElement();
    link.setType("text/css");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", Document.get().getURL() + "DtcFrame.css");
    doc.getBody().appendChild(link);
  }

  /**
	 * 
	 */
  void sortServices() {
    Document doc = IFrameElement.as(DtcArdbeg.dtcFrame.getElement()).getContentDocument();
    Element oldTableBody = doc.getElementsByTagName("tbody").getItem(0);

    List<Pair<Integer, Node>> rows = DtcArdbeg.extractServiceList(oldTableBody);
    DtcArdbeg.sortServicesByVisitCount(rows);
    this.applyStylesToServiceRows(rows);
    Element sortedBody = this.createSortedTableBody(doc, rows);

    oldTableBody.getParentNode().replaceChild(sortedBody, oldTableBody);
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

  private static void sortServicesByVisitCount(List<Pair<Integer, Node>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, Node>>() {
      @Override
      public int compare(Pair<Integer, Node> arg0,
          Pair<Integer, Node> arg1) {
        return -arg0.key.compareTo(arg1.key);
      }
    });
  }

  private static List<Pair<Integer, Node>> extractServiceList(Element tbody) {
    List<Pair<Integer, Node>> rows = new ArrayList<Pair<Integer, Node>>();
    for (int i = 1; i < tbody.getChildCount(); i++) {
      Node row = tbody.getChild(i);

      // 브라우저에 따라 white space로 구성된 텍스트 노드가 반환되면 건너뛴다.
      if (row.getChildCount() == 0) {
        continue;
      }

      String name;
      if (row.getChild(0).getNodeType() == Node.ELEMENT_NODE) { // IE8, 7
        name = row.getChild(0).getChild(0).getChild(0).getNodeValue();
      } else { // chrome, ff10
        if (row.getChild(1).getChild(0).getNodeType() == Node.ELEMENT_NODE) { // dtc.ini
          name = row.getChild(1).getChild(0).getChild(0).getNodeValue();
        }
        else {
          name = row.getChild(1).getChild(1).getChild(0).getNodeValue();
        }
      }

      Integer score = DtcArdbeg.serviceDao.getVisitCount(name);
      rows.add(new Pair<Integer, Node>(score, row));
    }
    return rows;
  }

  private void applyStylesToServiceRows(List<Pair<Integer, Node>> rows) {
    for (Pair<Integer, Node> pair : rows) {
      if (pair.key == 0) {
        Element.as(pair.value).setAttribute("style", "color:gray; ");
      }
    }
  }

  private boolean hasVisitedService(List<Pair<Integer, Node>> rows) {
    return !rows.isEmpty() && rows.get(0).key > 0;
  }

  private void removeComaparePageAnchor(Document doc) {
    Node anchor = doc.getBody().getChild(0);
    doc.getBody().removeChild(anchor);

    Node br = doc.getBody().getChild(0);
    doc.getBody().removeChild(br);
  }

}
