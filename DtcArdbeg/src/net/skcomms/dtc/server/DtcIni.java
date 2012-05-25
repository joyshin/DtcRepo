/**
 * 
 */
package net.skcomms.dtc.server;

/**
 * @author jujang@sk.com
 */
public class DtcIni {

  private String charset = "euckr";

  public String getCharacterSet() {
    return this.getCharset();
  }

  public String getCharset() {
    return this.charset;
  }

  public void setCharset(String charset) {
    this.charset = charset.toLowerCase();
  }

}
