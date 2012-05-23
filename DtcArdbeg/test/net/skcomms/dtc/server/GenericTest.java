/**
 * 
 */
package net.skcomms.dtc.server;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author jujang@sk.com
 * 
 */
public class GenericTest {

  public static <K, V> HashMap<K, V> createHashMap() {
    return new HashMap<K, V>();
  }

  @Test
  public void test() {
    Map<Object, Object> map = GenericTest.createHashMap();

  }

}
