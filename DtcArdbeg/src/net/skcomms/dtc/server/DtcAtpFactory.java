package net.skcomms.dtc.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcAtpRecord;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestParameter;

public class DtcAtpFactory {

  private static void addArguments(DtcRequest request, DtcAtp atp) {
    List<DtcRequestParameter> params = request.getRequestParameters();
    for (DtcRequestParameter param : params) {
      if (param.getKey().equals("IP") || param.getKey().equals("Port")) {
        continue;
      }

      DtcAtpRecord record = new DtcAtpRecord();
      record.addField(param.getValue() == null ? "" : param.getValue());
      atp.addRecord(record);
    }
  }

  private static void addDummyRecords(DtcAtp atp) {
    DtcAtpRecord record = new DtcAtpRecord();
    for (int i = 0; i < 4; i++) {
      record.addField("1");
    }
    atp.addRecord(record);
  }

  public static DtcAtp createFrom(DtcRequest request, DtcIni ini) {
    DtcAtp atp = new DtcAtp();
    DtcAtpFactory.setSignature(ini, atp);
    DtcAtpFactory.addDummyRecords(atp);
    DtcAtpFactory.addArguments(request, atp);
    atp.setBinary(new byte[0]);
    return atp;
  }

  public static DtcAtp createFrom(InputStream is, String charset) throws IOException {
    return DtcAtpParser.parse(is, charset);
  }

  private static void setSignature(DtcIni ini, DtcAtp atp) {
    String sign = "ATP/1.2 " + ini.getBaseProp("APP_NAME").getValue() + " "
        + ini.getBaseProp("API_NUM").getValue();
    atp.setSignature(sign);
  }
}
