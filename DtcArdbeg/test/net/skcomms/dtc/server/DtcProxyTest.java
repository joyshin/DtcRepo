package net.skcomms.dtc.server;

import java.io.IOException;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class DtcProxyTest {

  class TestHandler implements ContentHandler {

    private final Stack<String> stack = new Stack<String>();

    @Override
    public boolean endArray() throws ParseException, IOException {
      System.out.print("]");
      return true;
    }

    @Override
    public void endJSON() throws ParseException, IOException {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean endObject() throws ParseException, IOException {
      System.out.print("");
      return true;
    }

    @Override
    public boolean endObjectEntry() throws ParseException, IOException {
      String key = stack.pop();
      System.out.println("</" + key + ">");
      return true;
    }

    @Override
    public boolean primitive(Object value) throws ParseException, IOException {
      System.out.print(value);
      return true;
    }

    @Override
    public boolean startArray() throws ParseException, IOException {
      System.out.print("[");

      return true;
    }

    @Override
    public void startJSON() throws ParseException, IOException {

    }

    @Override
    public boolean startObject() throws ParseException, IOException {
      return true;
    }

    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
      stack.push(key);
      System.out.print("<" + key + ">");
      return true;
    }

  }

  TestHandler test = new TestHandler();
  JSONParser parser = new JSONParser();

  @Test
  public void test() throws ParseException {
    String jsonResults = "{ \"Encoding\":\"euc-kr\", \"ResultSet\":{ \"ResponseInfo\":{ \"Code\":\"100\", \"Message\":\"SUCCESS\" }, \"Results\":{ \"ResultHeader\":{ \"ServerID\":\"c2\", \"CookedQuery\":\"향수 향수\", \"ReadyAnswer\":\"\", \"TotalGroupCount\":\"1\", \"GroupStartPos\":\"1\", \"GroupCount\":\"1\", \"TotalGroupResultCountInfo\":\"4071\", \"GroupResultStartPosInfo\":\"1\", \"GroupResultCountInfo\":\"1\" }, \"ResultList\":[] }}}";
    parser.parse(jsonResults, test);

  }
}
