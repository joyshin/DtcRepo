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
    Assert.assertTrue(DtcServiceVerifier.isValidDirectory("/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectory("kshop2s/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectory("kshop2s/old/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectory("kshop2s/new/old/"));

    Assert.assertFalse(DtcServiceVerifier.isValidDirectory(null));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory(""));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("//"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("/kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("/kshop2s/"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("/kshop2s/old"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectory("/kshop2s/old/100.ini"));
  }

}
