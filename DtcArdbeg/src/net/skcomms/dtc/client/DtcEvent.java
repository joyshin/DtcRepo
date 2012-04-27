package net.skcomms.dtc.client;

public enum DtcEvent {
  DTC_HOME_LOAD(1 << 0),
  DTC_DIRECTORY_LOAD(1 << 1),
  DTC_TESTPAGE_LOAD(1 << 2);

  private int flag;

  DtcEvent(int aFlag) {
    flag = aFlag;
  }

  boolean contains(DtcEvent aFlag) {
    return (flag & aFlag.flag) == flag;
  }
}
