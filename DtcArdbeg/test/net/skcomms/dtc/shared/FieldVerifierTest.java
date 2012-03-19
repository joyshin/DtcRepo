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
public class FieldVerifierTest {

    @Test
    public void test() {
        Assert.assertFalse(FieldVerifier.isValidName("kim"));
        Assert.assertTrue(FieldVerifier.isValidName("sccu"));
    }

}
