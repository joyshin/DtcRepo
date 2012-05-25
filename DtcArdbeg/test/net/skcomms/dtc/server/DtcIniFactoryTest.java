/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author jujang@sk.com
 */
@RunWith(value = Parameterized.class)
public class DtcIniFactoryTest {

  private static final String ENCODING = "encoding";

  private static final String PATH = "path";

  @Parameters
  public static Collection<Object[]> configs() {
    Map<String, String> map1 = new HashMap<String, String>();
    map1.put(DtcIniFactoryTest.PATH, "sample/kegloos_new.100.ini");
    map1.put(DtcIniFactoryTest.ENCODING, "utf8");

    Map<String, String> map2 = new HashMap<String, String>();
    map2.put(DtcIniFactoryTest.PATH, "sample/kshop2s.100.xml.ini");
    map2.put(DtcIniFactoryTest.ENCODING, "euckr");

    return Arrays.asList(new Object[][] {
        { map1 },
        // { map2 }
    });
  }

  private Map<String, String> map;

  public DtcIniFactoryTest(Map<String, String> map) {
    this.map = map;
  }

  @Test
  public void testKegloos_new_100() throws IOException {
    DtcIniFactory p = new DtcIniFactory();
    InputStream is = new FileInputStream(this.map.get(DtcIniFactoryTest.PATH));
    DtcIni ini = p.createFrom(is);
    Assert.assertNotNull(ini);
    Assert.assertEquals(this.map.get(DtcIniFactoryTest.ENCODING), ini.getCharacterSet());
  }

}
