package net.skcomms.dtc.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

public class DtcArdbegTest extends GWTTestCase {

  public String getModuleName() {
    return "net.skcomms.dtc.DtcArdbeg";
  }

  public void gwtSetUp() {
    // Window.Location
    // .assign("http://127.0.0.1:8888/DtcArdbeg.html?gwt.codesvr=127.0.0.1:9997&c=kbook2s/old/100h.ini&Query=%EA%BD%83");
    String host = Window.Location.getHost();
    host.concat("?c=kardcpts/100.xml.ini&Query=%EA%BD%83");
    Window.Location.assign(host);

  }

  public void testUrlParameter() {

    assertEquals(null, Window.Location.getParameter("d"));
    assertEquals(null, Window.Location.getParameter("c"));

  }
}
