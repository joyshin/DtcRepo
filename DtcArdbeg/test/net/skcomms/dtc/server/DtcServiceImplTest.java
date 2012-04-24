/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.skcomms.dtc.shared.DtcNodeInfo;
import net.skcomms.dtc.shared.DtcRequestInfo;
import net.skcomms.dtc.shared.DtcServiceVerifier;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcServiceImplTest {

  @Test
  public void testExtractItemsFrom() throws IOException, ParseException {
    byte[] contents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net/");
    List<DtcNodeInfo> items = DtcServiceImpl.createDtcNodeInfosFrom(contents);
    for (DtcNodeInfo item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));
        Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }

    Assert.assertEquals(174, items.size());

    Assert.assertFalse(items.isEmpty());

    contents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net/?b=kshop2s/");
    items = DtcServiceImpl.createDtcNodeInfosFrom(contents);

    for (DtcNodeInfo item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));

        Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }
    Assert.assertEquals(6, items.size());

    Assert.assertFalse(items.isEmpty());
  }

  @Test
  public void testParseTestPage() throws IOException {
    String href = "http://10.141.6.198/request.html?c=kadcpts/100.ini";
    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());
    DtcRequestInfo requestInfo = DtcServiceImpl.createDtcRequestInfoFrom(contents);
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());

    href = "http://10.141.6.198/request.html?c=kegloos_new/100.ini";
    url = new URL(href);
    conn = url.openConnection();
    contents = DtcServiceImpl.readAllBytes(conn.getInputStream());
    requestInfo = DtcServiceImpl.createDtcRequestInfoFrom(contents);
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());
  }

  @Test
  public void testPattern() {
    String input = "'<option value=\"10.141.242.31\">10.141.242.31 - sstgss1</option>'";
    Pattern pattern = Pattern.compile("<option value=\"(.*)\">(.*)</option>");
    Matcher m = pattern.matcher(input);
    m.find();
    System.out.println(m.group(1));
    System.out.println(m.group(2));
  }
}
