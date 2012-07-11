/**
 * 
 */
package net.skcomms.dtc.client.view;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.model.DtcNodeModel;
import net.skcomms.dtc.shared.DtcRequestMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcNavigationBarView extends DefaultDtcArdbegObserver implements DtcNodeObserver {

  static String[] getNavigationNodes(String path) {
    return path.split("/");
  }

  private final HorizontalPanel naviPanel = new HorizontalPanel();

  private final static String NAVIGATION_DELIMITER = "/";

  private final String rootPath = "/";

  private DtcArdbeg owner;

  public DtcNavigationBarView() {
  }

  private void addAnchor(String text, final String path) {
    Anchor anchor = new Anchor(text);

    anchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // FIXME: selectionPage 호출 코드로 변경해야할 부분
        GWT.log("Path=" + path);
        event.preventDefault();
        event.stopPropagation();
        DtcNavigationBarView.this.owner.setPath(path);
      }
    });

    this.naviPanel.add(anchor);
  }

  private void addLabel(String Text) {
    Label label = new Label(Text);
    this.naviPanel.add(label);
  }

  private void createHomeButton() {
    Button homeButton = new Button();

    homeButton.addStyleName("homeButton");
    homeButton.setHTML("<span>go to Home</span>");
    homeButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        // FIXME: selectionPage 호출 코드로 변경해야할 부분
        GWT.log("Path=" + DtcNavigationBarView.this.rootPath);
        event.stopPropagation();
        DtcNavigationBarView.this.owner.setPath(DtcNavigationBarView.this.rootPath);
      }
    });

    RootPanel.get("naviBarContainer").add(homeButton);
  }

  public void initialize(DtcArdbeg dtcArdbeg, DtcNodeModel nodeModel) {
    dtcArdbeg.addDtcArdbegObserver(this);
    nodeModel.addObserver(this);

    this.naviPanel.clear();
    this.naviPanel.setSpacing(3);
    this.owner = dtcArdbeg;

    this.createHomeButton();
    RootPanel.get("naviBarContainer").add(this.naviPanel);
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
    this.updateNavigationBar(path);
  }

  @Override
  public void onDtcHomeLoaded() {
    this.updateNavigationBar("/");
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestMeta requestInfo) {
    this.updateNavigationBar(requestInfo.getPath());
  }

  @Override
  public void onFavoriteNodeListChanged() {
  }

  @Override
  public void onNodeListChanged() {
  }

  public void updateNavigationBar(String path) {
    this.naviPanel.clear();

    String[] nodes = DtcNavigationBarView.getNavigationNodes(path);
    String nodeHistory = this.rootPath;
    for (int i = 1; i < nodes.length; i++) {
      this.addLabel(DtcNavigationBarView.NAVIGATION_DELIMITER);
      if (i == nodes.length - 1) {
        this.addLabel(nodes[i]);
        continue;
      }
      nodeHistory = nodeHistory + nodes[i] + "/";
      this.addAnchor(nodes[i], nodeHistory);
    }
  }
}
