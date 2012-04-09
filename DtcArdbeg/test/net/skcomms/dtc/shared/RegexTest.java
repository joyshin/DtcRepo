/**
 * 
 */
package net.skcomms.dtc.shared;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jujang@sk.com
 * 
 */
public class RegexTest {

  @Test
  public void test() {
    String src = "http://dtc.skcomms.net/?b=100.xml.ini";
    Assert.assertEquals("http://dtc.skcomms.net?b=100.xml.ini", src.replaceAll("/[?]b=", "?b="));
    // Assert.assertTrue(src.matches("/[?]b="));
  }

}
