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
public class DtcServiceVerifierTest {

  @Test
  public void testDirectoryPathValidation() {
    Assert.assertTrue(DtcServiceVerifier.isValidPath("/"));
    Assert.assertTrue(DtcServiceVerifier.isValidPath("/kshop2s/"));
    Assert.assertTrue(DtcServiceVerifier.isValidPath("/kshop2s/old/"));
    Assert.assertTrue(DtcServiceVerifier.isValidPath("/kshop2s/new/old/"));

    Assert.assertFalse(DtcServiceVerifier.isValidPath(null));
    Assert.assertFalse(DtcServiceVerifier.isValidPath(""));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("//"));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("kshop2s/"));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("/kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("/kshop2s/old"));
    Assert.assertFalse(DtcServiceVerifier.isValidPath("/kshop2s/old/100.ini"));
  }

}
