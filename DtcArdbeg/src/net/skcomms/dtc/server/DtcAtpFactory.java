package net.skcomms.dtc.server;

import java.io.InputStream;
import java.util.Map;

import net.skcomms.dtc.server.model.DtcAtp;
import net.skcomms.dtc.server.model.DtcAtpRecord;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcRequestProperty;
import net.skcomms.dtc.shared.DtcRequest;

public class DtcAtpFactory {

  private static void addArguments(DtcRequest request, DtcIni ini, DtcAtp atp) {
    Map<String, String> actuals = request.getRequestParameters();
    for (DtcRequestProperty prop : ini.getRequestProps()) {

      System.out.println(prop);
      DtcAtpRecord record = new DtcAtpRecord();
      record.addField(actuals.get(prop.getKey()));
      atp.addRecord(record);
    }
  }

  private static void addDummyRecords(DtcAtp atp) {
    DtcAtpRecord record = new DtcAtpRecord();
    for (int i = 0; i < 4; i++) {
      record.addField("");
    }
    atp.addRecord(record);
  }

  public static DtcAtp createFrom(DtcRequest request, DtcIni ini) {
    DtcAtp atp = new DtcAtp();

    setSignature(ini, atp);
    addDummyRecords(atp);
    addArguments(request, ini, atp);
    atp.setBinary(new byte[0]);

    return atp;
  }

  public static DtcAtp createFrom(InputStream is) {
    return null;
  }

  private static void setSignature(DtcIni ini, DtcAtp atp) {
    String sign = "ATP/1.2 " + ini.getBaseProp("APP_NAME").getValue() + " 100";
    atp.setSignature(sign);
  }
}
