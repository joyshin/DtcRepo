/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skcomms.dtc.shared.DtcNodeMetaModel;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
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

  @Test
  public void testExtractItemsFrom() throws IOException, ParseException {
    byte[] contents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net/");
    List<DtcNodeMetaModel> items = DtcServiceImpl.createDtcNodeInfosFrom(contents);
    for (DtcNodeMetaModel item : items) {
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

    contents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net/?b=kshop2s/");
    items = DtcServiceImpl.createDtcNodeInfosFrom(contents);

    for (DtcNodeMetaModel item : items) {
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
    List<DtcNodeMetaModel> nodes = new DtcServiceImpl().getDirImpl("/common/");
    for (DtcNodeMetaModel node : nodes) {
      System.out.println(node);
    }
  }

  @Test
  public void testGetRootPath() throws IOException {
    Assert.assertEquals("sample/dtc/", DtcServiceImpl.getRootPath());
    String absolutePath = DtcServiceImpl.getRootPath() + "/";
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
    Arrays.sort(files, DtcServiceImpl.NODE_COMPARATOR);

    Assert.assertEquals(false, file1.isDirectory());
    Assert.assertEquals(true, file2.isDirectory());
    Assert.assertEquals("habong", files[0].getName());
  }

  @Test
  public void testParseTestPage() throws IOException {
    String href = "http://10.141.6.198/request.html?c=kadcpts/100.ini";
    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());
    DtcRequestInfoModel requestInfo = DtcServiceImpl.createDtcRequestInfoFrom("/kadcpts/100.ini",
        contents);
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());

    href = "http://10.141.6.198/request.html?c=kegloos_new/100.ini";
    url = new URL(href);
    conn = url.openConnection();
    contents = DtcServiceImpl.readAllBytes(conn.getInputStream());
    requestInfo = DtcServiceImpl.createDtcRequestInfoFrom("/kegloos_new/100.ini", contents);
    System.out.println(requestInfo.getParams().toString());
    System.out.println(requestInfo.getIpInfo());
  }

  @Test
  public void testSaxParser() throws IOException, ParserConfigurationException, SAXException {
    byte[] htmlContents = DtcServiceImpl
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
