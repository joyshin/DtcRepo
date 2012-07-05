package net.skcomms.dtc.server;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Tokenizer {

  Pattern pattern = Pattern.compile("[^ \\u001E\\u001F]+|\\u001E|\\u001F| ");

  Pattern patternSpace = Pattern.compile("[^\\u001E\\u001F]+|\\u001E|\\u001F");

  private final Scanner scanner;

  private String recalledToken = null;

  public Tokenizer(InputStream is, String charset) {
    this.scanner = new Scanner(is, charset);

  }

  public byte[] getBinaryData(int binarySize) {
    // FIXME 바이너리 데이터를 처리하려면 Scanner를 걷어내고 직접 토큰을 생성한다.
    if (!this.scanner.hasNext(".*")) {
      return new byte[0];
    }

    byte[] bytes = this.scanner.next(".*").getBytes();
    if (binarySize != bytes.length) {
      throw new IllegalArgumentException("ERROR: expectedSize:" + binarySize + ", actual:"
          + bytes.length);
    }
    return bytes;
  }

  public String getToken() {
    if (this.recalledToken != null) {
      String token = this.recalledToken;
      this.recalledToken = null;
      return token;
    }
    return this.scanner.findWithinHorizon(this.patternSpace, 0);
  }

  public String getTokenNoSpace() {
    if (this.recalledToken != null) {
      String token = this.recalledToken;
      this.recalledToken = null;
      return token;
    }
    return this.scanner.findWithinHorizon(this.pattern, 0);
  }

  public void ungetToken(String token) {
    this.recalledToken = token;
  }
}
