package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.util.DtcHelper;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestParameter;

import org.junit.Assert;
import org.junit.Test;

public class DtcAtpParserTest {

  private static final String LT = Character.toString((char) 0x1E);

  private static final String SP = Character.toString((char) 0x20);

  private static final String FT = Character.toString((char) 0x1F);

  private static final String RS = Character.toString((char) 0x1E);

  private void assertAtp(byte[] bs) throws IOException {
    Socket soc = new Socket("10.141.10.51", 9100);

    OutputStream os_socket = soc.getOutputStream(); // 소켓에 쓰고
    os_socket.write(bs);
    os_socket.flush();

    DtcAtp atp = DtcAtpParser.parse(soc.getInputStream(), "utf-8");

    Assert.assertNotNull(atp);
    System.out.println(atp);
  }

  private void printByte(byte[] bytes) {
    for (byte b : bytes) {
      if (b == 0) {
        // break;
      }
      System.out.println("byte:[" + b + "], char:[" + (char) b + "]");
    }
  }

  // 10.141.10.51 | 9100 - 싸이블로그
  @Test
  public void testAtp() throws IOException {

    String[] messages = {
        // "ATP/1.2 KCBBSD 0" + DtcAtpParserTest.LT + DtcAtpParserTest.LT +
        // DtcAtpParserTest.LT + "0"
        // + DtcAtpParserTest.LT,
        "ATP/1.2 KCBBSD 100" + DtcAtpParserTest.LT + DtcAtpParserTest.LT +
            "" + DtcAtpParserTest.FT +
            "" + DtcAtpParserTest.FT +
            "" + DtcAtpParserTest.FT +
            "" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "100" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "blog" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "1" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "1" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "TS" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "PD" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "256" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            "TEST" + DtcAtpParserTest.FT + DtcAtpParserTest.LT +
            DtcAtpParserTest.LT +
            "0" + DtcAtpParserTest.LT
    };
    for (String msg : messages) {
      this.assertAtp(msg.getBytes());
    }
  }

  @Test
  public void testAtpRequest() throws IOException {

    List<DtcRequestParameter> requestParameter = new ArrayList<DtcRequestParameter>();
    requestParameter.add(new DtcRequestParameter("Version", null, "100"));
    requestParameter.add(new DtcRequestParameter("Query", null, "블로그"));
    requestParameter.add(new DtcRequestParameter("ResultStartPos", null, "1"));
    requestParameter.add(new DtcRequestParameter("ResultCount", null, "2"));
    requestParameter.add(new DtcRequestParameter("ClientCode", null, "TS"));
    requestParameter.add(new DtcRequestParameter("Sort", null, "PD"));
    requestParameter.add(new DtcRequestParameter("SummaryLen", null, "128"));
    requestParameter.add(new DtcRequestParameter("Referer", null, "TEST"));
    requestParameter.add(new DtcRequestParameter("Port", null, "9200"));
    requestParameter.add(new DtcRequestParameter("IP", null, "10.171.10.241"));

    DtcRequest request = new DtcRequest();
    request.setRequestParameters(requestParameter);

    String filePath = DtcHelper.getRootPath() + "kcbbs/blog.100.xml.ini";
    DtcIni ini = new DtcIniFactory().createFrom(filePath);

    DtcAtp dtcAtp = DtcAtpFactory.createFrom(request, ini);
    this.assertAtp(dtcAtp.getBytes(ini.getCharacterSet()));
  }

  @Test
  public void testParseResponse() throws IOException {
    String response = "ATP/1.2 100 Continue" + DtcAtpParserTest.LT + DtcAtpParserTest.LT + "s-id"
        + DtcAtpParserTest.FT + "s-key"
        + DtcAtpParserTest.FT + DtcAtpParserTest.LT + "s-id2" + DtcAtpParserTest.FT + "s-key2"
        + DtcAtpParserTest.FT + DtcAtpParserTest.LT
        + DtcAtpParserTest.LT + "3" + DtcAtpParserTest.LT + "abc";
    DtcAtp atp = DtcAtpParser.parse(response.getBytes("euc-kr"), "euc-kr");

    Assert.assertNotNull(atp);
    System.out.println(atp);
  }

  @Test
  public void testTokenizer() {
    String response = "ATP/1.2 100 Continue" + DtcAtpParserTest.LT + DtcAtpParserTest.LT + "0"
        + DtcAtpParserTest.LT;
    Tokenizer t = new Tokenizer(new ByteArrayInputStream(response.getBytes()), "euc-kr");

    Assert.assertEquals("ATP/1.2", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("100", t.getTokenNoSpace());
    Assert.assertEquals(" ", t.getTokenNoSpace());
    Assert.assertEquals("Continue", t.getTokenNoSpace());
    Assert.assertEquals(DtcAtpParserTest.LT, t.getToken());
    Assert.assertEquals(DtcAtpParserTest.LT, t.getToken());
    Assert.assertEquals("0", t.getToken());
    Assert.assertEquals(DtcAtpParserTest.LT, t.getToken());
  }
}
