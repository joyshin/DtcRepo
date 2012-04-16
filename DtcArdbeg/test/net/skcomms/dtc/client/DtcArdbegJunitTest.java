package net.skcomms.dtc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DtcArdbegJunitTest {

  @Test
  public void testUrlParsing() {
    String url = "http://dtc.skcomms.net/shin/?c=kadcpts/100.xml.ini&Version=100&Query=&IP=127.0.0.1";
    String queryString = "";
    if (url.indexOf('?') != -1) {
      queryString = url.substring(url.indexOf('?') + 1);
    }
    String[] dtcParams = queryString.split("&");
    for (String param : dtcParams) {
      String[] entry = param.split("=");
      assertNotNull(param);

      if (entry[0].equals("Query")) {
        assertEquals(1, entry.length);
      }
    }
  }

}
