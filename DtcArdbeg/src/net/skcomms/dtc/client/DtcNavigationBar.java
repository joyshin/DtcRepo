/**
 * 
 */
package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcNavigationBar extends DefaultDtcArdbegObserver {

  static String[] getNavigationNodes(String url) {
    int valueStartIndex = url.lastIndexOf("?");
    if (valueStartIndex == -1) {
      return new String[0];
    }
    String valueSource = url.substring(valueStartIndex + 3);
    return valueSource.split("/");
  }

  private final HorizontalPanel naviPanel = new HorizontalPanel();

  private final static String NAVIGATION_DELIMITER = "/";

  private final String rootPath = "/";

  private DtcArdbeg owner;

  public DtcNavigationBar() {
  }

  private void addAnchor(String text, final String path) {
    Anchor anchor = new Anchor(text);

    anchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.stopPropagation();
        owner.setDtcFramePath(path);
      }
    });

    naviPanel.add(anchor);
  }

  private void addLabel(String Text) {
    Label label = new Label(Text);
    naviPanel.add(label);
  }

  public void addPath(String url) {
    naviPanel.clear();

    String[] nodes = DtcNavigationBar.getNavigationNodes(url);

    if (nodes.length == 0) {
      addLabel("Home");
    }
    else {
      addAnchor("Home", rootPath);
      addLabel(DtcNavigationBar.NAVIGATION_DELIMITER);
      String nodeHistory = rootPath;
      for (int i = 0; i < nodes.length - 1; i++) {
        nodeHistory = nodeHistory + nodes[i] + "/";
        addAnchor(nodes[i], nodeHistory);
        addLabel(DtcNavigationBar.NAVIGATION_DELIMITER);
      }
      addLabel(nodes[nodes.length - 1]);
    }
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);

    naviPanel.clear();
    naviPanel.setSpacing(3);
    owner = dtcArdbeg;

    addAnchor("Home", rootPath);
    RootPanel.get("naviBarContainer").add(naviPanel);
  }

  @Override
  public void onDtcDirectoryLoaded(Document dtcFrameDoc) {
    addPath(dtcFrameDoc.getURL());
  }

  @Override
  public void onDtcHomeLoaded(Document dtcFrameDoc) {
    addPath(dtcFrameDoc.getURL());
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    addPath(dtcFrameDoc.getURL());
  }
}
