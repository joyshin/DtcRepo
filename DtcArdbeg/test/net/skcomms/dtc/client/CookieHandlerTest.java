/**
 * 
 */
package net.skcomms.dtc.client;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jujang@sk.com
 * 
 */
public class CookieHandlerTest {

  @Test
  public void testSplit() {
    String str = "abc";
    String[] tokens = str.split("/");

    Assert.assertEquals(1, tokens.length);
    Assert.assertTrue(tokens[0].equals("abc"));
  }

}
