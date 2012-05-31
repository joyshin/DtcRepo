/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.shared.DtcNodeMetaModel;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author jujang@sk.com
 */
@RunWith(value = Parameterized.class)
public class DtcIniFactoryTest {

  private static final String CHARSET = "CHARACTER_SET";

  private static final String PATH = "path";

  private static final String PORT = "PORT";

  private static final String VERSION = "Version";

  private static final String DOC_LENGTH = "ResultDocLength";

  private static final String DOC_LENGTH_TYPE = "ResultDocLength.Type";

  private static final String CLIENT_URL = "ClientURL";

  private static final String VERSION_COMMENT_PREFIX = "Version.Comment";

  private static final String SERVER_ID = "ServerID";

  private static final String SERVER_ID_TYPE = "ServerID.Type";

  private static final String COOKED_QUERY = "CookedQuery";

  private static final String COOKED_QUERY_TYPE = "CookedQuery.Type";

  private static final String RESULT_DOC_COUNT = "ResultDocCount";

  private static final String RESULT_DOC_COUNT_ATTR = "ResultDocCount.Attr";

  private static final String EGLOO_ID_ATTR = "EglooID.Attr";

  private static final String EGLOO_ID = "EglooID";

  private static final String LIST_ATTR1 = "List.Attrs.1";

  private static final String LIST_ATTR2 = "List.Attrs.2";

  @Parameters
  public static Collection<Object[]> configs() {
    Map<String, String> map1 = new HashMap<String, String>();
    map1.put(DtcIniFactoryTest.PATH, "sample/kegloos_new.100.ini");
    map1.put(DtcIniFactoryTest.CHARSET, "utf8");
    map1.put(DtcIniFactoryTest.PORT, "21002");
    map1.put(DtcIniFactoryTest.VERSION, "100");
    map1.put(DtcIniFactoryTest.DOC_LENGTH, "256");
    map1.put(DtcIniFactoryTest.DOC_LENGTH_TYPE, "^INT");
    map1.put(DtcIniFactoryTest.CLIENT_URL, "");
    map1.put(DtcIniFactoryTest.VERSION_COMMENT_PREFIX, ": API");
    map1.put(DtcIniFactoryTest.SERVER_ID_TYPE, "^CH");
    map1.put(DtcIniFactoryTest.COOKED_QUERY_TYPE, "^CH");
    map1.put(DtcIniFactoryTest.RESULT_DOC_COUNT_ATTR, "RESULTCNT1");
    map1.put(DtcIniFactoryTest.EGLOO_ID_ATTR, "LIST_FIELD");
    map1.put(DtcIniFactoryTest.LIST_ATTR1, "CNT1");
    map1.put(DtcIniFactoryTest.LIST_ATTR2, "PAGE");

    Map<String, String> map2 = new HashMap<String, String>();
    map2.put(DtcIniFactoryTest.PATH, "sample/kshop2s.100.xml.ini");
    map2.put(DtcIniFactoryTest.CHARSET, "euckr");
    map2.put(DtcIniFactoryTest.PORT, "9001");
    map2.put(DtcIniFactoryTest.VERSION, "100");
    map2.put(DtcIniFactoryTest.VERSION_COMMENT_PREFIX, ": API");
    map2.put(DtcIniFactoryTest.DOC_LENGTH, null);

    return Arrays.asList(new Object[][] {
        { map1 },
        // { map2 }
    });
  }

  private Map<String, String> map;

  public DtcIniFactoryTest(Map<String, String> map) {
    this.map = map;
  }

  @Ignore
  @Test
  public void testAllInis() throws IOException {
    DtcIniFactory p = new DtcIniFactory();
    InputStream is = new FileInputStream("sample/dtc/ejej_kkbs_adult/101.ini");
    DtcIni ini = p.createFrom(is);
    System.out.println(ini.getErrors());
    Assert.assertEquals(0, ini.getErrors().size());

    byte[] htmlContents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net");
    List<DtcNodeMetaModel> nodes = DtcServiceImpl.createDtcNodeInfosFrom(htmlContents);
    List<DtcNodeMetaModel> queue = new LinkedList<DtcNodeMetaModel>();
    queue.addAll(nodes);
    while (!queue.isEmpty()) {
      DtcNodeMetaModel node = queue.remove(0);
      if (node.isLeaf()) {
        if (node.getPath().equals("/ejej_kkbs_adult/101.ini") ||
            node.getPath().equals("/kterm3s/10000_100.ini") ||
            node.getPath().equals("/semdtc/ESTATE/210.ini") ||
            node.getPath().equals("/semdtc2/JOB/200.ini") ||
            node.getPath().equals("/kuccfrontbs/1200-100.ini") ||
            node.getPath().equals("/ksfclubbrds/API2000_100_BROKER_XML.ini") ||
            node.getPath().equals("/ksfclubbrds/API2001_100_BROKER_XML.ini") ||
            node.getPath().equals("/kemails/100_xml.ini") ||
            node.getPath().equals("/kemail2s/100_xml.ini")) {
          continue;
        }
        System.out.println(node.getPath());
        is = new FileInputStream("sample/dtc" + node.getPath());
        p.createFrom(is);
      } else {
        System.out.println(node.getPath());
        byte[] contents = DtcServiceImpl.getHtmlContents("http://dtc.skcomms.net/?b="
            + node.getPath().substring(1));
        List<DtcNodeMetaModel> subnodes = DtcServiceImpl.createDtcNodeInfosFrom(contents);
        queue.addAll(subnodes);
      }
    }
  }

  @Test
  public void testApi() throws IOException {
    DtcIniFactory p = new DtcIniFactory();
    InputStream is = new FileInputStream(this.map.get(DtcIniFactoryTest.PATH));
    DtcIni ini = p.createFrom(is);

    Assert.assertNotNull(ini);

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.CHARSET), ini.getCharacterSet());
    Assert.assertEquals(this.map.get(DtcIniFactoryTest.CHARSET),
        ini.getBaseProp(DtcIniFactoryTest.CHARSET).getValue());

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.PORT),
        ini.getBaseProp(DtcIniFactoryTest.PORT).getValue());

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.VERSION),
        ini.getRequestProp(DtcIniFactoryTest.VERSION).getValue());
    Assert.assertEquals(this.map.get(DtcIniFactoryTest.VERSION_COMMENT_PREFIX),
        ini.getRequestProp(DtcIniFactoryTest.VERSION).getComment().substring(0, 5));

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.DOC_LENGTH),
        ini.getRequestProp(DtcIniFactoryTest.DOC_LENGTH).getValue());
    if (ini.getRequestProp(DtcIniFactoryTest.DOC_LENGTH) != null) {
      Assert.assertEquals(this.map.get(DtcIniFactoryTest.DOC_LENGTH_TYPE),
          ini.getRequestProp(DtcIniFactoryTest.DOC_LENGTH).getType());
    }

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.CLIENT_URL),
        ini.getRequestProp(DtcIniFactoryTest.CLIENT_URL).getValue());

    Assert.assertNotNull(ini.getResponseProp(DtcIniFactoryTest.SERVER_ID));
    Assert.assertEquals(this.map.get(DtcIniFactoryTest.SERVER_ID_TYPE),
        ini.getResponseProp(DtcIniFactoryTest.SERVER_ID).getType());

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.COOKED_QUERY_TYPE),
        ini.getResponseProp(DtcIniFactoryTest.COOKED_QUERY).getType());

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.RESULT_DOC_COUNT_ATTR),
        ini.getResponseProp(DtcIniFactoryTest.RESULT_DOC_COUNT).getAttrs().get(0));

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.RESULT_DOC_COUNT_ATTR),
        ini.getResponseProp(DtcIniFactoryTest.RESULT_DOC_COUNT).getAttrs().get(0));

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.EGLOO_ID_ATTR),
        ini.getResponseProp(DtcIniFactoryTest.EGLOO_ID).getAttrs().get(0));

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.LIST_ATTR1), ini.getListAttrs().get(0));

    Assert.assertEquals(this.map.get(DtcIniFactoryTest.LIST_ATTR2), ini.getListAttrs().get(1));
  }

}
