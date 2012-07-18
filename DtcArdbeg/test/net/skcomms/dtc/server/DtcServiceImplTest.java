/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skcomms.dtc.server.util.DtcHelper;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcServiceVerifier;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author jujang@sk.com
 */
public class DtcServiceImplTest {

  /**
   * @param url
   * @return
   * @throws IOException
   */
  public static byte[] getHtmlContents(String href) throws IOException {
    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcHelper.readAllBytes(conn.getInputStream());

    return contents;
  }

  @Test
  public void testAtpHttpApi() throws IOException {
    String url = "http://dtc.skcomms.net/sccu/dtcardbeg/dtcservice?path=/kkeywords/204.ini&charset=euc-kr&appName=KKEYWORDSD&apiNumber=100&APIVersion=204&Nativequery=dkdlvhs&RevisionLevel=1&FindKeywordAlias=Y&FindKeywordAdult=Y&FindKeywordList=Y&Port=7777&IP=10.141.11.143";
    URL conUrl = new URL(url);
    Date start = new Date();
    for (int i = 0; i < 1000; i++) {
      HttpURLConnection httpCon = (HttpURLConnection) conUrl.openConnection();
      httpCon.setDoInput(true);
      httpCon.setRequestProperty("Content-Type", "text/html; charset=euc-kr");
      httpCon.connect();
      InputStreamReader inputStreamReader = new InputStreamReader(httpCon.getInputStream(),
          "euc-kr");
      char[] cbuf = new char[1024];
      inputStreamReader.read(cbuf);
      httpCon.getInputStream().close();
      System.out.println(i);
    }
    Date end = new Date();
    System.out.println("Time:" + ((end.getTime() - start.getTime()) / 1000));
  }

  @Test
  public void testExtractItemsFrom() throws IOException, ParseException {
    List<DtcNodeMeta> items = new DtcServiceImpl().getDirImpl("/");
    for (DtcNodeMeta item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));
        Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }

    Assert.assertEquals(176, items.size());

    Assert.assertFalse(items.isEmpty());

    items = new DtcServiceImpl().getDirImpl("/kshop2s/");
    for (DtcNodeMeta item : items) {
      Assert.assertNotNull(item.getName());
      Assert.assertNotNull(item.getDescription());
      Assert.assertNotNull(item.getUpdateTime());
      Assert.assertNotNull(item.getPath());

      if (item.isLeaf()) {
        Assert.assertTrue(item.getPath().endsWith(".ini"));

        Assert.assertFalse(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      } else {
        Assert.assertEquals("디렉토리", item.getDescription());
        Assert.assertTrue(DtcServiceVerifier.isValidDirectoryPath(item.getPath()));
      }

      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.getUpdateTime());

      System.out.println("[" + item.getName() + ":" + item.getDescription() + ":"
          + item.getUpdateTime() + ":" + item.getPath() + "]");
    }
    Assert.assertEquals(6, items.size());

    Assert.assertFalse(items.isEmpty());
  }

  @Test
  public void testGetDir() throws IOException {
    List<DtcNodeMeta> nodes = new DtcServiceImpl().getDirImpl("/common/");
    for (DtcNodeMeta node : nodes) {
      System.out.println(node);
    }
  }

  @Test
  public void testGetRootPath() throws IOException {
    Assert.assertEquals("sample/dtc/", DtcHelper.getRootPath());
    String absolutePath = DtcHelper.getRootPath() + "/";
    File file = new File(absolutePath);

    for (File item : file.listFiles(new DtcServiceImpl.DtcNodeFilter())) {
      System.out.println(item.getName());
    }
  }

  @Test
  public void testNodeComparator() {
    File file1 = new File("sample/dtc/dtc.ini");
    File file2 = new File("sample/dtc/habong");
    File[] files = { file1, file2 };
    Arrays.sort(files, DtcHelper.NODE_COMPARATOR);

    Assert.assertEquals(false, file1.isDirectory());
    Assert.assertEquals(true, file2.isDirectory());
    Assert.assertEquals("habong", files[0].getName());
  }

  @Test
  public void testParseTestPage() throws IOException {
    DtcRequestMeta requestInfo = new DtcServiceImpl()
        .getDtcRequestMeta("/kadcpts/100.ini");
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());

    requestInfo = new DtcServiceImpl().getDtcRequestMeta("/kegloos_new/100.ini");
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());
  }

  @Test
  public void testSaxParser() throws IOException, ParserConfigurationException, SAXException {
    byte[] htmlContents = DtcServiceImplTest
        // .getHtmlContents("http://10.173.2.120:9001/KSHOP2SD/100?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1&Version=100&Query=%B3%AA%C0%CC%C5%B0&ResultStartPos=1&ResultCount=2&Sort=PD&Property=&Adult=1&ClientCode=TAA&ClientURI=DTC");
        .getHtmlContents("http://10.141.242.31:21002/KEGLOOSD/100?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1&Version=100&ClientCode=NSB&ClientURL=&Query=%EB%A7%8C%ED%99%94&ResultStartPos=1&ResultCount=10&OrderBy=PD&SearchField=AL&ResultDocLength=256");
    SAXParserFactory sax = SAXParserFactory.newInstance();
    SAXParser p = sax.newSAXParser();
    p.parse(new ByteArrayInputStream(htmlContents), new DefaultHandler() {

      @Override
      public void characters(char[] ch, int start, int length) {
        System.out.print(new String(ch, start, length));
        System.out.println("start:" + start + ", length" + length);
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes)
          throws SAXException {
        System.out.println("<" + qName);
        for (int i = 0; i < attributes.getLength(); i++) {
          System.out.println("*" + attributes.getQName(i));
        }
      }
    });
  }

}
