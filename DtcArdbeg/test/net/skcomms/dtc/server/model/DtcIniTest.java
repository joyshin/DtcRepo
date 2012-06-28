package net.skcomms.dtc.server.model;

import org.junit.Assert;
import org.junit.Test;

public class DtcIniTest {

  @Test
  public void testIpPattern() {
    DtcIni ini = new DtcIni();

    ini.setBaseProp(new DtcBaseProperty("IP", "10.101.2.1"));
    Assert.assertEquals(1, ini.getIps().size());
    Assert.assertEquals(0, ini.getErrors().size());

    ini.setBaseProp(new DtcBaseProperty("IP", "{ \"10.101.2.2\" }"));
    System.out.println(ini.getIps());
    Assert.assertEquals(2, ini.getIps().size());
    Assert.assertEquals(0, ini.getErrors().size());

    ini.setBaseProp(new DtcBaseProperty("IP", " { \"10.101.2.3\" \"abc de\" } "));
    System.out.println(ini.getIps());
    Assert.assertEquals(3, ini.getIps().size());
    Assert.assertEquals("abc de", ini.getIps().get("10.101.2.3"));
    Assert.assertEquals(0, ini.getErrors().size());

    ini.setBaseProp(new DtcBaseProperty("IP",
        " { \"10.101.2.4\" \"abc de\" } { \"10.101.2.5\" \"1289381.1\" } "));
    System.out.println(ini.getIps());
    Assert.assertEquals(5, ini.getIps().size());
    Assert.assertEquals(0, ini.getErrors().size());

    ini.setBaseProp(new DtcBaseProperty("IP", "10.101.2.!"));
    Assert.assertEquals(5, ini.getIps().size());
    Assert.assertEquals(1, ini.getErrors().size());
    System.out.println(ini.getErrors());

    ini.setBaseProp(new DtcBaseProperty(
        "IP",
        "{10.141.151.52 ssttlb02} {10.141.151.51 ssttlb01} {10.141.242.38 vm2} {10.141.144.120 LB} {10.141.15.250 xcschtest20}"));
    System.out.println(ini.getIps());
    Assert.assertEquals(10, ini.getIps().size());

  }

}
