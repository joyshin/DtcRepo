package net.skcomms.dtc.server.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DtcAtp {

  private static final byte LT = 0x1E;

  private static final byte FT = 0x1F;

  private static Logger logger = Logger.getLogger(DtcAtp.class);

  private String version;

  private int responseCode;

  private byte[] binary;

  private final List<DtcAtpRecord> records = new ArrayList<DtcAtpRecord>();

  private String signature;

  public void addRecord(DtcAtpRecord record) {
    this.records.add(record);
  }

  private void convertRecordToHtml(StringBuilder sb, DtcResponseProperty prop, DtcAtpRecord record) {
    sb.append("<div class=\"table_row\" name=\"" + prop.getFieldName() + "\" >");
    sb.append("\n");
    sb.append("<div class=\"key\" title=\"" + prop.getComment() + "\" >");
    sb.append(prop.getFieldName());
    sb.append("</div>");
    sb.append("<div class=\"value\">");
    sb.append(record);
    sb.append("</div>");
    sb.append("\n");
    sb.append("</div>");
    sb.append("\n");
  }

  private void convertResultHeaderToHtml(StringBuilder sb, DtcIni ini) {
    int index = 0;
    sb.append("<div class=\"depth_1\" name=\"ResultHeader\">");
    sb.append("\n");
    sb.append("ResultHeader");
    for (DtcResponseProperty prop : ini.getResultHeaderProps()) {
      DtcAtpRecord record = this.records.get(index++);
      this.convertRecordToHtml(sb, prop, record);
    }
    sb.append("</div>");
    sb.append("\n");
  }

  private void convertResultListPropertyToHtml(StringBuilder sb,
      List<DtcResponseProperty> listProps, int recordOffset) {
    sb.append("<div class=\"depth_2 Document\" name=\"Document\">");
    sb.append("\n");
    sb.append("Document");
    for (DtcResponseProperty prop : listProps) {
      DtcAtpRecord record = this.records.get(recordOffset++);
      this.convertRecordToHtml(sb, prop, record);
    }
    sb.append("</div>");
    sb.append("\n");
  }

  private void convertResultListToHtml(StringBuilder sb, DtcIni ini) {
    int offset = ini.getResultHeaderProps().size();
    List<DtcResponseProperty> listProps = ini.getResultListProps();
    int documentRecordCount = listProps.size();
    int totalRecordCount = this.records.size();

    if (documentRecordCount == 0) {
      return;
    }

    sb.append("<div class=\"depth_1\" name=\"ResultList\">");
    sb.append("\n");
    sb.append("ResultList");

    while (offset < totalRecordCount) {
      this.convertResultListPropertyToHtml(sb, listProps, offset);
      offset += documentRecordCount;
    }

    sb.append("</div>");
    sb.append("\n");
  }

  public byte[] getBytes(String charset) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    bos.write(this.signature.getBytes());
    bos.write(DtcAtp.LT);
    bos.write(DtcAtp.LT);

    for (DtcAtpRecord rec : this.records) {
      bos.write(rec.getBytes(charset));
    }
    bos.write(DtcAtp.LT);
    bos.write(Integer.toString(this.binary.length).getBytes());
    bos.write(DtcAtp.LT);
    bos.write(this.binary);

    if (DtcAtp.logger.isDebugEnabled()) {
      DtcAtp.logger.debug("Charaset:" + charset);
      DtcAtp.logger.debug("bytes:" + new String(bos.toByteArray(), charset));
    }

    return bos.toByteArray();
  }

  public String getVersion() {
    return this.version;
  }

  public void setBinary(byte[] bytes) {
    this.binary = bytes;
    System.out.println("binary:[" + new String(bytes) + "]");
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
    System.out.println("responseCode:[" + responseCode + "]");
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String toHtmlString(DtcIni ini) {
    StringBuilder sb = new StringBuilder();

    sb.append("<div class=\"depth_0\" id=\"Results\">");
    sb.append("\n");
    sb.append("Results");
    sb.append("\n");

    this.convertResultHeaderToHtml(sb, ini);
    this.convertResultListToHtml(sb, ini);

    sb.append("</div>");
    sb.append("\n");

    return sb.toString();
  }

  @Override
  public String toString() {
    return "Signature:[" + this.signature + "]\nResponseCode:[" + this.responseCode
        + "]\nRecords:" + this.records.toString()
        + "\nBinarySize:[" + this.binary.length + "]";
  }

}
