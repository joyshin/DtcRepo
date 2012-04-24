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
    Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath("/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath("/kshop2s/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath("/kshop2s/old/"));
    Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath("/kshop2s/new/old/"));

    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(null));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(""));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("//"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("kshop2s/"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("/kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("/kshop2s/old"));
    Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath("/kshop2s/old/100.ini"));
  }

  @Test
  public void testTestPageValidation() {
    Assert.assertTrue(DtcServiceVerifier.isValidTestPage("/dtc.ini"));
    Assert.assertTrue(DtcServiceVerifier.isValidTestPage("/kshop2s/dtc.ini"));
    Assert.assertTrue(DtcServiceVerifier.isValidTestPage("/kshop2s/old/dtc.ini"));
    Assert.assertTrue(DtcServiceVerifier.isValidTestPage("/kshop2s/new/old/dtc.ini"));

    Assert.assertFalse(DtcServiceVerifier.isValidTestPage(null));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("100.ini"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("//100.ini"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("kshop2s/100.ini"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("kshop2s/100.ini/"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("/kshop2s"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("/kshop2s/old"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("/kshop2s/old/dtc.ini/"));
    Assert.assertFalse(DtcServiceVerifier.isValidTestPage("/kshop2s/old/dtc..ini/"));
  }

}
