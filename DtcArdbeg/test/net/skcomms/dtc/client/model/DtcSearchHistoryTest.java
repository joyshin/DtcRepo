package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.DtcRequestParameter;

import org.junit.Assert;
import org.junit.Test;

public class DtcSearchHistoryTest {

  @Test
  public void test() {
    String path = "path";
    List<DtcRequestParameter> params = new ArrayList<DtcRequestParameter>();
    params.add(new DtcRequestParameter("key1", null, "value1"));
    params.add(new DtcRequestParameter("key2", null, "value2"));
    DtcSearchHistory dtcSearchHistory = DtcSearchHistory.create(path, params, 5);

    String result = dtcSearchHistory.serialize();
    DtcSearchHistory dtcSearchHistory2 = DtcSearchHistory.deserialize(result);
    String result2 = dtcSearchHistory2.serialize();

    System.out.println(result);
    System.out.println(result2);

    Assert.assertEquals(result, result2);
  }

}
