/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.IOException;
import java.util.List;

import net.skcomms.dtc.shared.Item;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcServiceImplTest {

  @Test
  public void test() throws IOException {
    String path = "/";
    String contents = new String(DtcServiceImpl.getHtmlContents(path));
    Assert.assertNotNull(contents);
    Assert.assertTrue(contents.substring(0, 16), contents.startsWith("<a"));
  }

  @Test
  public void testExtractItemsFrom() throws IOException {
    byte[] contents = DtcServiceImpl.getHtmlContents("/");
    List<Item> items = DtcServiceImpl.extractItemsFrom(contents);
    for (Item item : items) {
      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":" + item.getDate()
          + "]");
    }

    Assert.assertEquals(173, items.size());

    Assert.assertFalse(items.isEmpty());

    contents = DtcServiceImpl.getHtmlContents("kshop2s/");
    items = DtcServiceImpl.extractItemsFrom(contents);

    for (Item item : items) {
      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":" + item.getDate()
          + "]");
    }
    Assert.assertEquals(6, items.size());

    Assert.assertFalse(items.isEmpty());
  }
}
