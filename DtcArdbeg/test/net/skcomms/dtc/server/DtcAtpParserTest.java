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
    String response = "ATP/1.2 100 Continue" + this.LT + this.LT + "s-id" + this.FT + "s-key"
        + this.FT + this.LT + "s-id2" + this.FT + "s-key2" + this.FT + this.LT
        + this.LT + "3" + this.LT + "abc";
    DtcAtp atp = DtcAtpParser.parse(response.getBytes());

    Assert.assertNotNull(atp);
    System.out.println(atp);
  }

  @Test
  public void testTokenizer() {
    String response = "ATP/1.2 100 Continue" + this.LT + this.LT + "0" + this.LT;
    Tokenizer t = new Tokenizer(new ByteArrayInputStream(response.getBytes()));

    Assert.assertEquals("ATP/1.2", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("100", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("Continue", t.getTokenNoSpace());
    Assert.assertEquals(this.LT, t.getToken());
    Assert.assertEquals(this.LT, t.getToken());
    Assert.assertEquals("0", t.getToken());
    Assert.assertEquals(this.LT, t.getToken());
  }
}
