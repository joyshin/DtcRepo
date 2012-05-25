/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jujang@sk.com
 */
public class DtcIniFactory {

  /**
   * 
   */
  private static final String CHARACTER_SET = "CHARACTER_SET";

  private static final int MARK_LIMIT = 4096;

  private static String determineCharset(byte[] bytes) throws IOException {
    String line;
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bytes), "euc-kr"));
    while ((line = reader.readLine()) != null) {
      if (line.startsWith(DtcIniFactory.CHARACTER_SET)) {
        return line.substring(DtcIniFactory.CHARACTER_SET.length() + 1);
      }
    }
    return "euckr";
  }

  private DtcIni ini;

  private BufferedReader reader;

  private int lineCount;

  private String currentHeader;

  /**
   * @throws IOException
   */
  private void baseHeader() throws IOException {
    String line = this.getLine();
    if (!line.equals("[BASE]")) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: [BASE], " + "actual: " + line);
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  /**
   * @throws IOException
   */
  private void baseKeyValue() throws IOException {
    String line = this.getLine();
    String[] kv = line.split("=");
    if (kv.length != 2) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: BASE_KEY_VAL, " + "actual: " + line);
    }
    System.out.println("Key:" + kv[0] + ", Value:" + kv[1]);
  }

  /**
   * @throws IOException
   */
  private void baseKeyValueComment() throws IOException {
    String line = this.getLine();
    String[] kv = line.split(";")[0].split("=");
    if (kv.length != 2) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: BASE_KEY_VAL_COMMENT, " + "actual: " + line);
    }
    System.out.println("Key:" + kv[0] + ", Value:" + kv[1]);
  }

  /**
   * @throws IOException
   */
  private void baseProp() throws IOException {
    String line = this.getLine();
    if (line.indexOf(';') == -1) {
      this.ungetLine();
      this.baseKeyValue();
    } else {
      this.ungetLine();
      this.baseKeyValueComment();
    }
  }

  /**
   * @throws IOException
   */
  private void baseProps() throws IOException {
    String line = this.getLine();
    if (line == null || line.startsWith("[")) {
      this.ungetLine();
      System.out.println("=== END OF SECTION ===");
      return;
    }
    this.ungetLine();
    this.baseProp();
    this.baseProps();
  }

  /**
   * @throws IOException
   */
  private void baseSection() throws IOException {
    this.baseHeader();
    this.baseProps();
  }

  /**
   * @param is
   * @throws IOException
   */
  public DtcIni createFrom(InputStream is) throws IOException {

    byte[] bytes = DtcServiceImpl.readAllBytes(is);

    String encoding = DtcIniFactory.determineCharset(bytes);
    return this.parse(bytes, encoding);
  }

  private String getLine() throws IOException {
    while (true) {
      this.reader.mark(DtcIniFactory.MARK_LIMIT);
      String line = this.reader.readLine();
      if (line == null) {
        return line;
      }
      this.lineCount++;
      line = line.trim();
      if (!line.startsWith("#") && !line.equals("")) {
        return line;
      }
    }
  }

  /**
   * @param reader
   * @throws IOException
   */
  private void ini() throws IOException {
    this.sections();
  }

  /**
   * @throws IOException
   */
  private void list() throws IOException {
    this.listOpenTag();
    this.listProps();
    this.listEndTag();
  }

  /**
   * @throws IOException
   */
  private void listEndTag() throws IOException {
    String line = this.getLine();
    if (!line.toLowerCase().startsWith("{/list}")) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: LIST_END_TAG, " + "actual: " + line);
    }
    System.out.println("LIST_END:" + line);
  }

  /**
   * @throws IOException
   */
  private void listOpenTag() throws IOException {
    String line = this.getLine();
    if (!line.toLowerCase().startsWith("{list")) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: LIST_START_TAG, " + "actual: " + line);
    }

    System.out.println("LIST_START: " + line);
    Pattern attrPattern = Pattern.compile("<#(\\w+)>");
    Matcher matcher = attrPattern.matcher(line);
    while (matcher.find()) {
      System.out.println("list attr:" + matcher.group(1));
    }
  }

  private void listProp() throws IOException {
    String line = this.getLine();
    Pattern pattern = Pattern.compile("(\\S+)\\s+(\\w+)\\s*([:#](.*))?$");
    Matcher matcher = pattern.matcher(line);
    matcher.find();
    System.out.println("type:" + matcher.group(1));
    System.out.println("field:" + matcher.group(2));
    System.out.println("comment:" + matcher.group(4));

    String comment = matcher.group(4);
    if (comment != null) {
      Pattern attrPattern = Pattern.compile("<#(\\w+)>");
      matcher = attrPattern.matcher(comment);
      while (matcher.find()) {
        System.out.println("attr:" + matcher.group(1));
      }
    }
  }

  /**
   * @throws IOException
   */
  private void listProps() throws IOException {
    String line = this.getLine();
    this.ungetLine();
    if (line.toLowerCase().startsWith("{/list}")) {
      return;
    }

    this.listProp();
    this.listProps();
  }

  /**
   * @param bytes
   * @param encoding
   * @return
   * @throws IOException
   */
  private DtcIni parse(byte[] bytes, String encoding) throws IOException {
    this.ini = new DtcIni();
    this.ini.setCharset(encoding);

    this.reader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bytes), encoding));
    this.lineCount = 0;
    this.ini();

    return this.ini;
  }

  /**
   * @throws IOException
   */
  private void requestHeader() throws IOException {
    String line = this.getLine();
    if (!line.equals("[REQUEST]")) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: [REQUEST], " + "actual: " + line);
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  /**
   * @throws IOException
   */
  private void requestProp() throws IOException {
    String line = this.getLine();
    Pattern pattern = Pattern.compile("(\\S+)\\s+(\\w+)(=([^:#]*))?\\s*([:#](.*))?$");
    Matcher matcher = pattern.matcher(line);
    matcher.find();
    System.out.println("type:" + matcher.group(1));
    System.out.println("key:" + matcher.group(2));
    System.out.println("val:" + matcher.group(4));
    System.out.println("comment:" + matcher.group(6));

    String comment = matcher.group(6);
    if (comment != null) {
      Pattern attrPattern = Pattern.compile("<#(\\w+)>");
      matcher = attrPattern.matcher(comment);
      while (matcher.find()) {
        System.out.println("attr:" + matcher.group(1));
      }
    }
  }

  /**
   * @throws IOException
   */
  private void requestProps() throws IOException {
    String line = this.getLine();
    if (line == null || line.startsWith("[")) {
      this.ungetLine();
      System.out.println("=== END OF SECTION ===");
      return;
    }
    this.ungetLine();
    this.requestProp();
    this.requestProps();
  }

  /**
   * @throws IOException
   */
  private void requestSection() throws IOException {
    this.requestHeader();
    this.requestProps();
  }

  private void responseHeader() throws IOException {
    String line = this.getLine();
    if (!line.equals("[RESPONSE]")) {
      throw new IllegalArgumentException("Error: line " + this.lineCount
          + ": expected: [RESPONSE], " + "actual: " + line);
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  private void responseProp() throws IOException {
    String line = this.getLine().toLowerCase();
    this.ungetLine();
    if (line.startsWith("{list")) {
      this.list();
    } else {
      this.scalarProp();
    }
  }

  /**
   * @throws IOException
   */
  private void responseProps() throws IOException {
    String line = this.getLine();
    if (line == null) {
      return;
    }
    this.responseProp();
    this.responseProps();
  }

  private void responseSection() throws IOException {
    this.responseHeader();
    this.responseProps();
  }

  private void scalarProp() throws IOException {
    String line = this.getLine();
    Pattern pattern = Pattern.compile("(\\S+)\\s+(\\w+)\\s*([:#](.*))?$");
    Matcher matcher = pattern.matcher(line);
    matcher.find();
    System.out.println("type:" + matcher.group(1));
    System.out.println("field:" + matcher.group(2));
    System.out.println("comment:" + matcher.group(4));

    String comment = matcher.group(4);
    if (comment != null) {
      Pattern attrPattern = Pattern.compile("<#(\\w+)>");
      matcher = attrPattern.matcher(comment);
      while (matcher.find()) {
        System.out.println("attr:" + matcher.group(1));
      }
    }
  }

  /**
   * @param reader
   * @throws IOException
   */
  private void sections() throws IOException {
    this.baseSection();
    this.requestSection();
    this.responseSection();
  }

  private void ungetLine() throws IOException {
    this.reader.reset();
    this.lineCount--;
  }
}
