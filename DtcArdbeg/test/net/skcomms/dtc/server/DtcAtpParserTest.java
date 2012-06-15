package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.skcomms.dtc.server.model.DtcAtp;

import org.junit.Assert;
import org.junit.Test;

public class DtcAtpParserTest {
  String LT = Character.toString((char) 0x0a);
  String FT = Character.toString((char) 0x09);

  @Test
  public void testParseResponse() throws IOException {
    String response = "ATP/1.2 100 Continue" + this.LT + this.LT + "s-id" + this.FT + this.LT
        + this.LT + "2" + this.LT + "11";
    DtcAtp atp = DtcAtpParser.parse(response.getBytes());

    Assert.assertNotNull(atp);
  }

  @Test
  public void testTokenizer() {
    String response = "ATP/1.2 100 Continue" + this.LT + this.LT + "0" + this.LT;
    Tokenizer t = new Tokenizer(new ByteArrayInputStream(response.getBytes()));

    String token;
    Assert.assertEquals("ATP/1.2", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("100", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("Continue", t.getTokenNoSpace());
    Assert.assertEquals(this.LT, t.getToken());
    Assert.assertEquals(this.LT, t.getToken());
    Assert.assertEquals("0", t.getToken());
    Assert.assertEquals(this.LT, t.getToken());
    // while ((token = t.getToken()) != null) {
    // System.out.println("TOKEN:[" + token + "]");
    // }
  }
}
