package net.skcomms.dtc.server;

import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class DtcProxyTest {

  DtcJsonToXmlHandler test = new DtcJsonToXmlHandler();
  JSONParser parser = new JSONParser();

  @Test
  public void test() throws ParseException, IOException {
    byte[] jsonContents = DtcServiceImpl
        .getHtmlContents("http://10.141.1.138:7002/KBOOK2SD/100?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1&Version=102&ClientCode=EBB&ClientURI=&Query=%C7%E2%BC%F6&SearchDomain=bk_information&GroupStartPos=1&GroupCount=1&ResultStartPos=1&ResultCount=1&MaxSummaryLength=640&ID=");
    // System.out.println(new String(htmlContents));
    String jsonString = new String(jsonContents, "euc-kr");
    System.out.println("json:" + jsonString);
    parser
        .parse(jsonString.replaceAll("\\\\u000B", "&#11;").replaceAll("\\\\f", "&#12;"), test);
    byte[] convertedXml = test.toString().getBytes();

    byte[] xmlContents = DtcServiceImpl
        .getHtmlContents("http://10.141.15.192:7001/KBOOK2SD/100?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1&Version=102&ClientCode=EBB&ClientURI=&Query=%C7%E2%BC%F6&SearchDomain=bk_information&GroupStartPos=1&GroupCount=1&ResultStartPos=1&ResultCount=1&MaxSummaryLength=640&ID=");

    String string = new String(xmlContents, "euc-kr");
    System.out.println(string);
    String string2 = new String(convertedXml);
    System.out.println(string2);

    Assert.assertEquals(string, string2);
  }
}
