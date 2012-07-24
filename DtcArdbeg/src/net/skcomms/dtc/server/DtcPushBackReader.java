/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jujang@sk.com
 */
public class DtcPushBackReader {

  private BufferedReader reader;

  private int lineCount;

  static final int MARK_LIMIT = 4096;

  public DtcPushBackReader(byte[] bytes, String encoding) throws UnsupportedEncodingException {
    this.reader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bytes), encoding));
    this.lineCount = 0;
  }

  public String getBaseLine() throws IOException {
    return this.getLine();
  }

  private String getLine() throws IOException {
    while (true) {
      this.reader.mark(DtcPushBackReader.MARK_LIMIT);
      String line = this.reader.readLine();
      if (line == null) {
        return null;
      }
      this.lineCount = this.getLineCount() + 1;
      line = line.trim();
      if (!line.startsWith("#") && !line.startsWith(";") && !line.equals("")) {
        return line;
      }
    }
  }

  public int getLineCount() {
    return this.lineCount;
  }

  public String getRequestLine() throws IOException {
    while (true) {
      String line = this.getLine();
      if (line == null) {
        return null;
      }
      else {
        String lower = line.toLowerCase();
        Pattern VALID_PREFIX = Pattern.compile("^\\^c|^ch|^\\^i|^in|^\\{l|^\\{/|^\\[");
        Matcher matcher = VALID_PREFIX.matcher(lower);
        if (matcher.find()) {
          return line;
        }
      }
    }
  }

  public String getResponseLine() throws IOException {
    return this.getRequestLine();
  }

  public void ungetLine() throws IOException {
    this.reader.reset();
    this.lineCount--;
  }
}