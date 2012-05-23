/**
 * 
 */
package net.skcomms.dtc.client.controller;

import java.util.HashMap;
import java.util.Map;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.view.DtcUrlCopyButtonView;
import net.skcomms.dtc.client.view.DtcUrlCopyDialogBoxView;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcUrlCopyControllerTest {

  static class MockDtcArdbeg extends DtcArdbeg {
    @Override
    protected String calculateBaseUrl() {
      return null;
    }

    @Override
    protected FlowPanel createFlowPanel() {
      return null;
    }

    @Override
    protected Frame createFrame() {
      return null;
    }

    @Override
    public String getDtcFrameHref() {
      return "dtc.skcomms.net/_dtcproxy_/";
    }

    @Override
    public Map<String, String> getDtcRequestParameters() {
      return new HashMap<String, String>();
    }

    @Override
    public String getHref() {
      return "http://dtc.skcomms.net/";
    }
  }

  static class MockDtcUrlCopyButtonView extends DtcUrlCopyButtonView {

    private ClickHandler handler;

    @Override
    public void addUrlCopyButtonClickHandler(ClickHandler handler) {
      this.handler = handler;
    }

    @Override
    protected Button createButton() {
      return null;
    }

    public void fireClickButton() {
      this.handler.onClick(null);
    }

    @Override
    protected void initializeLinkCopyButton() {
    }

  }

  static class MockDtcUrlCopyDialogBoxView extends DtcUrlCopyDialogBoxView {

    @Override
    protected void initializeDialogBox() {
    }

    @Override
    public void showUrlText(String url) {
      System.out.println("URL:" + url);
    }
  }

  private DtcUrlCopyController controller;

  private MockDtcUrlCopyButtonView button;

  private DtcArdbeg ardbeg;

  private DtcUrlCopyDialogBoxView dialogBox;

  @Before
  public void before() {
    this.controller = new DtcUrlCopyController();
    this.button = new MockDtcUrlCopyButtonView();
    this.ardbeg = new MockDtcArdbeg();
    this.dialogBox = new MockDtcUrlCopyDialogBoxView();

    this.controller.initialize(this.ardbeg, this.button,
        this.dialogBox);
  }

  @Test
  public void test() {
    this.ardbeg.setDtcFramePath("/");
    this.button.fireClickButton();
  }
}
