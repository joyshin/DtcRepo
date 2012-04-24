package net.skcomms.dtc.client;

import java.util.Date;

import com.google.gwt.i18n.client.NumberFormat;

public class IpOption {

  enum Origin {
    DTC, COOKIE;
  }

  private final String ip;

  private final String text;

  private Date lastSuccessTime;

  private Origin origin;

  public IpOption(String anIp, String aText, Origin anOrigin) {
    this.ip = anIp;
    text = aText;
    origin = anOrigin;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof IpOption)) {
      return false;
    }

    IpOption o = (IpOption) other;
    return this.ip.equals(o.getIp());
  }

  public String getDecoratedText() {
    String timeDesc = getTimeDescription();
    if (timeDesc.equals(""))
      return timeDesc;
    else
      return " : " + timeDesc;
  }

  public String getIp() {
    return ip;
  }

  public Date getLastSuccessTime() {
    return lastSuccessTime;
  }

  public Origin getOrigin() {
    return origin;
  }

  public String getText() {
    return text;
  }

  private String getTimeDescription() {
    if (lastSuccessTime == null) {
      return "";
    }

    long now = new Date().getTime();
    long lastTime = lastSuccessTime.getTime();
    double elapsed = (now - lastTime) / 1000;

    NumberFormat format = NumberFormat.getFormat("#");
    if (elapsed < 1) {
      return "now";
    }

    if (elapsed < 60) {
      return format.format(elapsed) + "s ago";
    }

    elapsed /= 60; // The unit is a minute.
    if (elapsed < 60) {
      return format.format(elapsed) + "m ago";
    }

    elapsed /= 60; // The unit is a hour.
    if (elapsed < 60) {
      return format.format(elapsed) + "h ago";
    }

    elapsed /= 24; // The unit is a days.
    if (elapsed < 24) {
      return format.format(elapsed) + "d ago";
    }

    elapsed /= 7; // The unit is a week.
    return format.format(elapsed) + "w ago";
  }

  @Override
  public int hashCode() {
    return ip.hashCode();
  }

  public void setLastSuccessTime(Date lastSuccessTime) {
    this.lastSuccessTime = lastSuccessTime;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }
}
