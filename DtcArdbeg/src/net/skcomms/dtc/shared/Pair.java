/**
 * 
 */
package net.skcomms.dtc.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pair<K, V> implements Serializable {
  private K key;
  private V value;

  public Pair(K key, V value) {
    this.setKey(key);
    this.setValue(value);
  }

  public K getKey() {
    return this.key;
  }

  public V getValue() {
    return this.value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public void setValue(V value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.key + ":" + this.value;
  }
}