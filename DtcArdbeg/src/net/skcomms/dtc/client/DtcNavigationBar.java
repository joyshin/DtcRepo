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

  private final String baseUrl;

  private DtcArdbeg owner;

  public DtcNavigationBar(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private void addAnchor(String text, final String href) {
    Anchor anchor = new Anchor(text);

    anchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.stopPropagation();
        DtcNavigationBar.this.owner.setDtcFrameUrl(href);
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
    String addressSource = this.baseUrl;

    if (nodes.length == 0) {
      this.addLabel("Home");
    }
    else {
      addressSource = addressSource.concat("?b=");

      this.addAnchor("Home", this.baseUrl);
      this.addLabel(DtcNavigationBar.NAVIGATION_DELIMITER);
      String nodeHistory = "";
      for (int i = 0; i < nodes.length - 1; i++) {
        nodeHistory = nodeHistory + nodes[i] + "/";
        this.addAnchor(nodes[i], addressSource.concat(nodeHistory));
        this.addLabel(DtcNavigationBar.NAVIGATION_DELIMITER);
      }
      this.addLabel(nodes[nodes.length - 1]);
    }
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);

    this.naviPanel.clear();
    this.naviPanel.setSpacing(3);
    this.owner = dtcArdbeg;

    this.addAnchor("Home", this.baseUrl);
    RootPanel.get("naviBarContainer").add(this.naviPanel);
  }

  @Override
  public void onDtcDirectoryLoaded(Document dtcFrameDoc) {
    this.addPath(dtcFrameDoc.getURL());
  }

  @Override
  public void onDtcHomeLoaded(Document dtcFrameDoc) {
    this.addPath(dtcFrameDoc.getURL());
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    this.addPath(dtcFrameDoc.getURL());
  }
}
