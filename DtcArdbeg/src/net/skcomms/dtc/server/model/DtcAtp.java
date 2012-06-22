package net.skcomms.dtc.server.model;

import java.util.ArrayList;
import java.util.List;

public class DtcAtp {

  private String version;
  private int responseCode;
  private byte[] binary;
  private List<DtcAtpRecord> records = new ArrayList<DtcAtpRecord>();
  private String signature;

  public void addRecord(DtcAtpRecord record) {
    this.records.add(record);
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
