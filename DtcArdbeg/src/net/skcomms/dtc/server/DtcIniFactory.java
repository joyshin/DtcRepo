/**
 * 
 */
package net.skcomms.dtc.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.skcomms.dtc.server.model.DtcBaseProperty;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcRequestProperty;
import net.skcomms.dtc.server.model.DtcResponseProperty;

/**
 * @author jujang@sk.com
 */
public class DtcIniFactory {

  private static final String CHARACTER_SET = "CHARACTER_SET";

  private static String examineCharset(byte[] bytes) throws IOException {
    String line;
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(bytes), "utf-8"));
    while ((line = reader.readLine()) != null) {
      if (line.startsWith(DtcIniFactory.CHARACTER_SET)) {
        return line.substring(DtcIniFactory.CHARACTER_SET.length() + 1);
      }
    }
    return "euckr";
  }

  private DtcIni ini;

  private DtcPushBackReader reader;

  private String currentHeader;

  private static final Pattern SINGLE_PROP_PATTERN = Pattern
      .compile("(\\S+)\\s+(\\S+)\\s*(.*)$");

  private static final Pattern LIST_FIELD_PATTERN = Pattern
      .compile("(\\S+)\\s+(\\S+)\\s*(.*)$");

  private static final Pattern REQUEST_PROP_PATTERN = Pattern
      .compile("(\\S+)\\s+([\\w-]+)(=(\\S+([ \\f]\\S+)*))?\\s*(.*)?$");

  private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("<#(\\w+)>");

  private void baseHeader() throws IOException {
    String line = this.reader.getBaseLine();
    if (!line.equals("[BASE]")) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: [BASE], " + "actual: " + line);
      return;
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  private void baseKeyValue() throws IOException {
    String line = this.reader.getBaseLine();
    String[] kv = line.split("=");
    if (kv.length != 2) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: BASE_KEY_VAL, " + "actual: " + line);
      return;
    }
    System.out.println("Key:" + kv[0] + ", Value:" + kv[1]);
    this.ini.setBaseProp(new DtcBaseProperty(kv[0], kv[1]));
  }

  private void baseKeyValueComment() throws IOException {
    String line = this.reader.getBaseLine();
    String[] tokens = line.split(";");
    String comment = tokens[1];
    String[] kv = tokens[0].split("=");
    if (kv.length != 2) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: BASE_KEY_VAL_COMMENT, " + "actual: " + line);
      return;
    }
    System.out.println("Key:" + kv[0] + ", Value:" + kv[1]);
    this.ini.setBaseProp(new DtcBaseProperty(kv[0], kv[1], comment));
  }

  private void baseProp() throws IOException {
    String line = this.reader.getBaseLine();
    if (line.indexOf(';') == -1) {
      this.reader.ungetLine();
      this.baseKeyValue();
    } else {
      this.reader.ungetLine();
      this.baseKeyValueComment();
    }
  }

  private void baseProps() throws IOException {
    String line = this.reader.getBaseLine();
    if (line == null || line.startsWith("[")) {
      this.reader.ungetLine();
      System.out.println("=== END OF SECTION ===");
      return;
    }
    this.reader.ungetLine();
    this.baseProp();
    this.baseProps();
  }

  private void baseSection() throws IOException {
    this.baseHeader();
    this.baseProps();
  }

  public DtcIni createFrom(InputStream is) throws IOException {

    byte[] bytes = DtcServiceImpl.readAllBytes(is);

    String encoding = DtcIniFactory.examineCharset(bytes);
    return this.parse(bytes, encoding);
  }

  private void ini() throws IOException {
    this.sections();
  }

  private void list() throws IOException {
    this.listOpenTag();
    this.listProps();
    this.listEndTag();
  }

  private void listEndTag() throws IOException {
    String line = this.reader.getResponseLine();
    if (!line.toLowerCase().startsWith("{/")) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: LIST_END_TAG, " + "actual: " + line);
      return;
    }
    System.out.println("LIST_END:" + line);
  }

  private void listOpenTag() throws IOException {
    String line = this.reader.getResponseLine();
    if (!line.toLowerCase().startsWith("{list")) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: LIST_START_TAG, " + "actual: " + line);
      return;
    }

    System.out.println("LIST_START: " + line);
    Matcher matcher = DtcIniFactory.ATTRIBUTE_PATTERN.matcher(line);
    while (matcher.find()) {
      this.ini.setListAttr(matcher.group(1));
    }
  }

  private void listProp() throws IOException {
    String line = this.reader.getResponseLine();
    System.out.println("LINE:" + line);
    Matcher matcher = DtcIniFactory.LIST_FIELD_PATTERN.matcher(line);
    boolean found = matcher.find();
    if (!found) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": invalid list property:" + line);
      return;
    }

    String type = matcher.group(1);
    String fieldName = matcher.group(2);
    String comment = matcher.group(3);
    List<String> attrs = new ArrayList<String>();
    attrs.add("LIST_FIELD");

    if (comment != null) {
      Pattern attrPattern = Pattern.compile("<#(\\w+)>");
      matcher = attrPattern.matcher(comment);
      while (matcher.find()) {
        attrs.add(matcher.group(1));
      }
    }
    this.ini.setResponseProp(new DtcResponseProperty(fieldName, type, comment, attrs));
  }

  private void listProps() throws IOException {
    String line = this.reader.getResponseLine();
    this.reader.ungetLine();
    if (line.toLowerCase().startsWith("{/")) {
      return;
    }

    this.listProp();
    this.listProps();
  }

  private DtcIni parse(byte[] bytes, String encoding) throws IOException {
    this.ini = new DtcIni();
    this.ini.setCharset(encoding);

    this.reader = new DtcPushBackReader(bytes, encoding);

    this.ini();

    return this.ini;
  }

  private void requestHeader() throws IOException {
    String line = this.reader.getRequestLine();
    if (!line.equals("[REQUEST]")) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: [REQUEST], " + "actual: " + line);
      return;
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  private void requestProp() throws IOException {
    String line = this.reader.getRequestLine();
    System.out.println("LINE:" + line);
    Matcher matcher = DtcIniFactory.REQUEST_PROP_PATTERN.matcher(line);
    boolean found = matcher.find();
    if (!found) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": invalid list property:" + line);
      return;
    }

    String type = matcher.group(1).trim();
    String key = matcher.group(2).trim();
    String value = matcher.group(4) == null ? "" : matcher.group(4).trim();
    String comment = matcher.group(6) == null ? "" : matcher.group(6).trim();
    List<String> attrs = new ArrayList<String>();
    if (comment != null) {
      Pattern attrPattern = Pattern.compile("<#(\\w+)>");
      matcher = attrPattern.matcher(comment);
      while (matcher.find()) {
        System.out.println("key: " + key + ", attr:" + matcher.group(1));
        attrs.add(matcher.group(1).trim());
      }
    }

    this.ini.setRequestProp(new DtcRequestProperty(key, type, value, comment, attrs));
  }

  private void requestProps() throws IOException {
    String line = this.reader.getRequestLine();
    if (line == null || line.startsWith("[")) {
      this.reader.ungetLine();
      System.out.println("=== END OF SECTION ===");
      return;
    }
    this.reader.ungetLine();
    this.requestProp();
    this.requestProps();
  }

  private void requestSection() throws IOException {
    this.requestHeader();
    this.requestProps();
  }

  private void responseHeader() throws IOException {
    String line = this.reader.getResponseLine();
    if (!line.equals("[RESPONSE]")) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": expected: [RESPONSE], " + "actual: " + line);
      return;
    }
    this.currentHeader = line.substring(1, line.length() - 1);
    System.out.println("HEADER:" + this.currentHeader);
  }

  private void responseProp() throws IOException {
    String line = this.reader.getResponseLine().toLowerCase();
    this.reader.ungetLine();
    if (line.startsWith("{list")) {
      this.list();
    } else {
      this.singleProp();
    }
  }

  private void responseProps() throws IOException {
    String line = this.reader.getResponseLine();
    if (line == null) {
      return;
    }
    this.reader.ungetLine();
    this.responseProp();
    this.responseProps();
  }

  private void responseSection() throws IOException {
    String line = this.reader.getResponseLine();
    if (line == null) {
      return;
    }
    this.reader.ungetLine();
    this.responseHeader();
    this.responseProps();
  }

  private void sections() throws IOException {
    this.baseSection();
    this.requestSection();
    this.responseSection();
  }

  private void singleProp() throws IOException {
    String line = this.reader.getResponseLine();
    System.out.println("LINE:" + line);
    Matcher matcher = DtcIniFactory.SINGLE_PROP_PATTERN.matcher(line);
    boolean found = matcher.find();
    if (!found) {
      this.ini.addErrorMessage("Error: line " + this.reader.getLineCount()
          + ": invalid list property:" + line);
      return;
    }

    String type = matcher.group(1);
    String fieldName = matcher.group(2);
    String comment = matcher.group(3);
    List<String> attrs = new ArrayList<String>();

    if (comment != null) {
      matcher = DtcIniFactory.ATTRIBUTE_PATTERN.matcher(comment);
      while (matcher.find()) {
        System.out.println("attr:" + matcher.group(1));
        attrs.add(matcher.group(1));
      }
    }
    System.out.println("Field:" + fieldName);
    this.ini.setResponseProp(new DtcResponseProperty(fieldName, type, comment, attrs));
  }
}
