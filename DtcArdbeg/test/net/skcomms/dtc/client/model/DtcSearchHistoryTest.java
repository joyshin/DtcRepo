package net.skcomms.dtc.client.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class DtcSearchHistoryTest {

  @Test
  public void test() {
    String path = "path";
    Map<String, String> params = new HashMap<String, String>();
    params.put("key1", "value1");
    params.put("key2", "value2");
    DtcSearchHistory dtcSearchHistory = DtcSearchHistory.create(path, params, 5);

    String result = dtcSearchHistory.serialize();
    DtcSearchHistory dtcSearchHistory2 = DtcSearchHistory.deserialize(result);
    String result2 = dtcSearchHistory2.serialize();

    System.out.println(result);
    System.out.println(result2);

    Assert.assertEquals(result, result2);
  }

}
