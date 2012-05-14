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

  static String[] getNavigationNodes(String path) {
    int valueStartIndex = path.lastIndexOf("?");
    if (valueStartIndex == -1) {
      return ("Home" + path).split("/");
    }
    String valueSource = path.substring(valueStartIndex + 3);
    return ("Home/" + valueSource).split("/");
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
        DtcNavigationBar.this.owner.setDtcFramePath(path);
      }
    });

    this.naviPanel.add(anchor);
  }

  private void addLabel(String Text) {
    Label label = new Label(Text);
    this.naviPanel.add(label);
  }

  public void addPath(String url) {
    this.naviPanel.clear();

    String[] nodes = DtcNavigationBar.getNavigationNodes(url);

    String nodeHistory = this.rootPath;
    for (int i = 0; i < nodes.length - 1; i++) {
      if (i > 0) {
        nodeHistory = nodeHistory + nodes[i] + "/";
      }
      this.addAnchor(nodes[i], nodeHistory);
      this.addLabel(DtcNavigationBar.NAVIGATION_DELIMITER);
    }
    this.addLabel(nodes[nodes.length - 1]);
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);

    this.naviPanel.clear();
    this.naviPanel.setSpacing(3);
    this.owner = dtcArdbeg;

    this.addLabel("Home");
    RootPanel.get("naviBarContainer").add(this.naviPanel);
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
    this.addPath(path);
  }

  @Override
  public void onDtcHomeLoaded() {
    this.addPath("/");
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    this.addPath(dtcFrameDoc.getURL());
  }
}
