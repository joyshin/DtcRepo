package net.skcomms.dtc.server.model;

import org.junit.Assert;
import org.junit.Test;

public class DtcIniTest {

  private static void checkCommonAssertions(DtcIni ini, int ipCount, int errorCount) {
    Assert.assertEquals(ipCount, ini.getIps().size());
    Assert.assertEquals(errorCount, ini.getErrors().size());
  }

  @Test
  public void testIpPattern() {
    DtcIni ini = new DtcIni();

    ini.setBaseProp(new DtcBaseProperty("IP", "10.101.2.1"));
    DtcIniTest.checkCommonAssertions(ini, 1, 0);

    ini.setBaseProp(new DtcBaseProperty("IP", "{ \"10.101.2.2\" }"));
    DtcIniTest.checkCommonAssertions(ini, 2, 0);

    ini.setBaseProp(new DtcBaseProperty("IP", " { \"10.101.2.3\" \"abc de\" } "));
    Assert.assertEquals("abc de", ini.getIps().get("10.101.2.3"));
    DtcIniTest.checkCommonAssertions(ini, 3, 0);

    ini.setBaseProp(new DtcBaseProperty("IP",
        " { \"10.101.2.4\" \"abc de\" } { \"10.101.2.5\" \"1289381.1\" } "));
    DtcIniTest.checkCommonAssertions(ini, 5, 0);

    ini.setBaseProp(new DtcBaseProperty("IP", "10.101.2.!"));
    DtcIniTest.checkCommonAssertions(ini, 5, 1);

  }

  @Test
  public void testQuotelessIpPattern() {
    DtcIni ini = new DtcIni();
    ini.setBaseProp(new DtcBaseProperty(
        "IP",
        "{10.141.151.52 ssttlb02} {10.141.151.51 ssttlb01} {10.141.242.38 vm2} {10.141.144.120 LB} {10.141.15.250 xcschtest20}"));
    DtcIniTest.checkCommonAssertions(ini, 5, 0);
  }

}
