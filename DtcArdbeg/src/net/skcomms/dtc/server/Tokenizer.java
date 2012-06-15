package net.skcomms.dtc.server;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Tokenizer {

  Pattern pattern = Pattern.compile("[^ \\u000A]+|\\u000A| ");
  Pattern patternSpace = Pattern.compile("[^\\u000A\\u0009]+|\\u000A|\\u0009");
  private Scanner scanner;
  private String recalledToken = null;

  public Tokenizer(InputStream is) {
    this.scanner = new Scanner(is);

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
