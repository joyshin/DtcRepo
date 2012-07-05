package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.shared.DtcRequest;

import org.junit.Assert;
import org.junit.Test;

public class DtcAtpParserTest {

  private static final String LT = Character.toString((char) 0x0a);

  private static final String SP = Character.toString((char) 0x20);

  private static final String FT = Character.toString((char) 0x09);

  private static final String RS = Character.toString((char) 0x1E);

  private void assertAtp(byte[] bs) throws IOException {
    Socket soc = new Socket("10.141.10.51", 9100);

    OutputStream os_socket = soc.getOutputStream(); // 소켓에 쓰고
    os_socket.write(bs);
    os_socket.flush();

    // byte[] buffer = new byte[1024 * 1024];
    byte[] buffer = new byte[1024];
    InputStream is_socket = soc.getInputStream();
    int nRead = is_socket.read(buffer);
    this.printByte(buffer);
    System.out.println(new String(buffer));
    byte[] copied = Arrays.copyOf(buffer, nRead);

    DtcAtp atp = DtcAtpParser.parse(copied);

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

    String[] messages = { "ATP/1.2 KCBBSD 0" + LT + LT + LT + "0" + LT,
        "ATP/1.2 KCBBSD 100" + LT + LT +
            "" + FT +
            "" + FT +
            "" + FT +
            "" + FT + LT +
            "100" + FT + LT +
            "blog" + FT + LT +
            "1" + FT + LT +
            "1" + FT + LT +
            "TS" + FT + LT +
            "PD" + FT + LT +
            "256" + FT + LT +
            "TEST" + FT + LT +
            LT +
            "0" + LT
    };
    for (String msg : messages) {
      this.assertAtp(msg.getBytes());
    }
  }

  @Test
  public void testAtpRequest() throws IOException {

    Map<String, String> requestParameter = new HashMap<String, String>();
    requestParameter.put("Version", "100");
    requestParameter.put("Query", "블로그");
    requestParameter.put("ResultStartPos", "1");
    requestParameter.put("ResultCount", "2");
    requestParameter.put("ClientCode", "TS");
    requestParameter.put("Sort", "PD");
    requestParameter.put("SummaryLen", "128");
    requestParameter.put("Referer", "TEST");
    requestParameter.put("Port", "9200");
    requestParameter.put("IP", "10.171.10.241");

    DtcRequest request = new DtcRequest();
    request.setRequestParameters(requestParameter);

    String filePath = DtcServiceImpl.getRootPath() + "kcbbs/blog.100.xml.ini";
    DtcIni ini = new DtcIniFactory().createFrom(filePath);

    DtcAtp dtcAtp = DtcAtpFactory.createFrom(request, ini);
    this.assertAtp(dtcAtp.getBytes(ini.getCharacterSet()));
  }

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
