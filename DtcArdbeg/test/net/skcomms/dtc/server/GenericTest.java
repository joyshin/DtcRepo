/**
 * 
 */
package net.skcomms.dtc.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jujang@sk.com
 */
public class GenericTest {

  static <E> void broker(Collection<? extends E> src, Collection<? super E> target) {
    for (E e : src) {
      target.add(e);
    }
    // target.addAll(src);
  }

  public static <K, V> HashMap<K, V> createHashMap() {
    return new HashMap<K, V>();
  }

  @Test
  public void test() {
    Map<String, Object> map = GenericTest.createHashMap();
    Assert.assertEquals(HashMap.class, map.getClass());
  }
}
