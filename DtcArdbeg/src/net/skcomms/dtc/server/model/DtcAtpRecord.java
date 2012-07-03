package net.skcomms.dtc.server.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DtcAtpRecord {

  private final List<String> fields = new ArrayList<String>();

  public void addField(String value) {
    this.fields.add(value);
  }

  public byte[] getBytes(String charset) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    for (String str : this.fields) {
      bos.write(str.getBytes(charset));
      bos.write((byte) 0x09);
    }
    bos.write((byte) 0x0a);
    return bos.toByteArray();
  }

  @Override
  public String toString() {
    return this.fields.toString();
  }
}
