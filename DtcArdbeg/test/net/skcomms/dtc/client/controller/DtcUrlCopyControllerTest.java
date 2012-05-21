/**
 * 
 */
package net.skcomms.dtc.client.controller;

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
    public String getHref() {
      return "dtc.skcomms.net";
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
  }

  private DtcUrlCopyController controller;
  private MockDtcUrlCopyButtonView button;

  @Before
  public void before() {
    this.controller = new DtcUrlCopyController();
    this.button = new MockDtcUrlCopyButtonView();
    this.controller.initialize(new MockDtcArdbeg(), this.button,
        new MockDtcUrlCopyDialogBoxView());
  }

  @Test
  public void test() {
    this.button.fireClickButton();
  }
}
