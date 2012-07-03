package net.skcomms.dtc.server.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DtcAtp {

  private static final byte LT = 0x0a;

  private static final byte FT = 0x09;

  private String version;
  private int responseCode;
  private byte[] binary;
  private final List<DtcAtpRecord> records = new ArrayList<DtcAtpRecord>();
  private String signature;

  public void addRecord(DtcAtpRecord record) {
    this.records.add(record);
  }

  public byte[] getBytes(String charset) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    bos.write(this.signature.getBytes());
    bos.write(LT);
    bos.write(LT);

    for (DtcAtpRecord rec : this.records) {
      bos.write(rec.getBytes(charset));
    }
    bos.write(LT);
    bos.write(Integer.toString(this.binary.length).getBytes());
    bos.write(LT);
    bos.write(this.binary);

    return bos.toByteArray();
  }

  public String getVersion() {
    return this.version;
  }

  public void setBinary(byte[] bytes) {
    this.binary = bytes;
    System.out.println("binary:[" + new String(bytes) + "]");
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
    System.out.println("responseCode:[" + responseCode + "]");
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  @Override
  public String toString() {
    return "Signature:[" + this.signature + "]\nResponseCode:[" + this.responseCode
        + "]\nRecords:" + this.records.toString()
        + "\nBinarySize:[" + this.binary.length + "]";
  }

}
