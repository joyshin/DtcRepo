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
    byte[] contents = DtcServiceImpl.getHtmlContents("/");
    List<DtcNodeInfo> items = DtcServiceImpl.createDtcNodeInfosFrom(contents);
    for (DtcNodeInfo item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));
        Assert.assertFalse(DtcServiceVerifier.isValidPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }

    Assert.assertEquals(174, items.size());

    Assert.assertFalse(items.isEmpty());

    contents = DtcServiceImpl.getHtmlContents("/kshop2s/");
    items = DtcServiceImpl.createDtcNodeInfosFrom(contents);

    for (DtcNodeInfo item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));

        Assert.assertFalse(DtcServiceVerifier.isValidPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }
    Assert.assertEquals(6, items.size());

    Assert.assertFalse(items.isEmpty());
  }

  @Test
  public void testGetHtmlContents() throws IOException {
    String path = "/";
    String contents = new String(DtcServiceImpl.getHtmlContents(path));
    Assert.assertNotNull(contents);
    Assert.assertTrue(contents.substring(0, 16), contents.startsWith("<a"));
  }

  @Test
  public void testParseTestPage() throws IOException {
    String href = "http://10.141.6.198/request.html?c=kadcpts/100.ini";
    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());
    // System.out.println(new String(contents));
    DtcRequestInfo requestInfo = DtcServiceImpl.createDtcRequestInfoFrom(contents);
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
