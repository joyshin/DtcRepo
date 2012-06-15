package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;

import net.skcomms.dtc.server.model.DtcAtp;

public class DtcAtpParser {

  private static final String LT = Character.toString((char) 0x0a);

  private static final String SP = Character.toString((char) 0x20);

  private static final String FT = Character.toString((char) 0x09);

  public static DtcAtp parse(byte[] bytes) {
    DtcAtpParser parser = new DtcAtpParser(bytes);
    parser.parseResponse();
    return parser.getAtp();
  }

  private DtcAtp atp;

  private Tokenizer tokenizer;

  private DtcAtpParser(byte[] bytes) {
    this.tokenizer = new Tokenizer(new ByteArrayInputStream(bytes));
    this.atp = new DtcAtp();
  }

  private void argumentData() {
    this.argumentList();
    this.binaryData();
  }

  private void argumentList() {
    while (true) {

      String token = this.tokenizer.getToken();
      if (token.equals(DtcAtpParser.LT)) {
        return;
      }

      this.tokenizer.ungetToken(token);
      this.record();

    }

  }

  private void binaryData() {
    // TODO Auto-generated method stub

  }

  private void field() {
    String token = this.tokenizer.getToken();
    System.out.println("field: " + token);
    this.match(this.tokenizer.getToken(), DtcAtpParser.FT);

  }

  private DtcAtp getAtp() {
    return this.atp;
  }

  private String getToken() {
    return this.tokenizer.getToken();
  }

  private void match(String token, String terminal) {
    if (!token.equals(terminal)) {
      throw new IllegalArgumentException("ERROR: [" + terminal + "] expected, actual:" + token
          + "]");
    }
  }

  private void parseResponse() {
    this.responseLine();
    this.responseHeader();
    this.match(this.getToken(), DtcAtpParser.LT);
    this.argumentData();
  }

  private void reason() {
    System.out.println("Reason: " + this.tokenizer.getTokenNoSpace());
  }

  private void record() {
    System.out.println("Record START !! ");
    do {
      this.field();
      String token = this.tokenizer.getToken();
      if (token.equals(DtcAtpParser.LT)) {
        break;
      }
      this.tokenizer.ungetToken(token);

    } while (true);
    System.out.println("Record END !! ");

  }

  private void responseCode() {
    this.atp.setResponseCode(Integer.parseInt(this.tokenizer.getTokenNoSpace()));
  }

  private void responseHeader() {
  }

  private void responseLine() {
    this.signature();
    this.match(this.tokenizer.getTokenNoSpace(), DtcAtpParser.SP);
    this.responseCode();
    this.match(this.tokenizer.getTokenNoSpace(), DtcAtpParser.SP);
    this.reason();
    this.match(this.tokenizer.getTokenNoSpace(), DtcAtpParser.LT);

  }

  private void signature() {
    System.out.println("Signature: " + this.tokenizer.getTokenNoSpace());
  }

  private void ungetToken() {

  }
}
