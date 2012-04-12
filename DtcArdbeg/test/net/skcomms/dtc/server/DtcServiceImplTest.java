/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import net.skcomms.dtc.shared.DtcNodeInfo;
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
    List<DtcNodeInfo> items = DtcServiceImpl.extractItemsFrom("/", contents);
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

    Assert.assertEquals(173, items.size());

    Assert.assertFalse(items.isEmpty());

    contents = DtcServiceImpl.getHtmlContents("/kshop2s/");
    items = DtcServiceImpl.extractItemsFrom("/kshop2s/", contents);

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
}
