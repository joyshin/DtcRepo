package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcAtpRecord;

public class DtcAtpParser {

  private static final String LT = Character.toString((char) 0x1E);

  private static final String SP = Character.toString((char) 0x20);

  private static final String FT = Character.toString((char) 0x1F);

  private static final String RS = Character.toString((char) 0x1E);

  public static DtcAtp parse(byte[] bytes, String charset) throws IOException {
    return DtcAtpParser.parse(new ByteArrayInputStream(bytes), charset);
  }

  public static DtcAtp parse(InputStream is, String charset) throws IOException {
    DtcAtpParser parser = new DtcAtpParser(is, charset);
    parser.parseResponse();
    return parser.getAtp();
  }

  private final DtcAtp atp;

  private final Tokenizer tokenizer;

  private int binarySize;

  private final InputStream inputStream;

  private DtcAtpRecord currRecord;

  private DtcAtpParser(InputStream is, String charset) {
    this.inputStream = is;
    this.tokenizer = new Tokenizer(this.inputStream, charset);
    this.atp = new DtcAtp();
  }

  private void argumentData() throws IOException {
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

  private void binaryData() throws IOException {
    this.number();
    this.match(this.tokenizer.getToken(), DtcAtpParser.LT);
    this.octetStream();
  }

  private void field() {
    String token = this.tokenizer.getToken();
    if (token.equals(DtcAtpParser.LT) || token.equals(DtcAtpParser.FT)) {
      this.tokenizer.ungetToken(token);
      token = "";
    }
    System.out.println("field:[" + token + "], size:" + token.getBytes().length);
    this.currRecord.addField(token);
    this.match(this.tokenizer.getToken(), DtcAtpParser.FT);
  }

  private DtcAtp getAtp() {
    return this.atp;
  }

  private void match(String token, String terminal) {
    if (!token.equals(terminal)) {
      throw new IllegalArgumentException("ERROR: [" + terminal +
          "] expected, actual:[" + token + "]");
    }
  }

  private void number() {
    this.binarySize = Integer.parseInt(this.tokenizer.getToken());
  }

  private void octetStream() throws IOException {

    this.atp.setBinary(this.tokenizer.getBinaryData(this.binarySize));
  }

  private void parseResponse() throws IOException {
    this.responseLine();
    this.responseHeader();
    this.match(this.tokenizer.getTokenNoSpace(), DtcAtpParser.LT);
    this.argumentData();
  }

  private void reason() {
    String token = this.tokenizer.getTokenNoSpace();
    System.out.println("Reason:[" + token + "], size:" + token.getBytes().length);
  }

  private void record() {
    this.currRecord = new DtcAtpRecord();
    do {
      this.field();
      String token = this.tokenizer.getToken();
      if (token.equals(DtcAtpParser.LT)) {
        break;
      }
      this.tokenizer.ungetToken(token);
    } while (true);
    this.atp.addRecord(this.currRecord);
    this.currRecord = null;
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
    this.atp.setSignature(this.tokenizer.getTokenNoSpace());
  }
}
