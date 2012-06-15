package net.skcomms.dtc.server.model;

public class DtcAtp {

  private String version;
  private int responseCode;
  private byte[] binary;

  public String getVersion() {
    return this.version;
  }

  public void setBinary(byte[] bytes) {
    this.binary = bytes;
    System.out.println("binary:[" + new String(bytes) + "]");
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

}
