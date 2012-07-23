package net.skcomms.dtc.server.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.server.DtcServiceImpl;
import net.skcomms.dtc.server.model.DtcIni;
import net.skcomms.dtc.server.model.DtcRequestProperty;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestParameter;

public class DtcRequestHttpAdapter {

  public static DtcRequest createDtcRequestFromHttpRequest(Map<String, String[]> params)
      throws IOException, FileNotFoundException {
    DtcRequest request = new DtcRequest();

    request.setPath(params.get("path")[0]);
    request.setEncoding(params.get("charset")[0]);
    request.setAppName(params.get("appName")[0]);
    request.setApiNumber(params.get("apiNumber")[0]);

    List<DtcRequestParameter> requestParams = DtcRequestHttpAdapter
        .getParametersFromParameterMap(params);
    request.setRequestParameters(requestParams);
    return request;
  }

  public static List<DtcRequestParameter> getParametersFromParameterMap(
      Map<String, String[]> urlParams)
      throws IOException, FileNotFoundException {
    DtcIni ini = DtcServiceImpl.getIni(urlParams.get("path")[0]);
    List<DtcRequestParameter> params = new ArrayList<DtcRequestParameter>();

    for (DtcRequestProperty prop : ini.getRequestProps()) {
      String value = urlParams.get(prop.getKey())[0];
      params.add(new DtcRequestParameter(prop.getKey(), null, value));
    }
    params.add(new DtcRequestParameter("IP", null, urlParams.get("IP")[0]));
    params.add(new DtcRequestParameter("Port", null, urlParams.get("Port")[0]));

    return params;
  }

  private final DtcRequest request;

  public DtcRequestHttpAdapter(DtcRequest request) {
    this.request = request;
  }

  private void appendOrigin(StringBuilder url) {
    url.append("http://");
    url.append(this.request.getRequestParameter("IP"));
    url.append(":");
    url.append(this.request.getRequestParameter("Port"));
    url.append("/");
    url.append(this.request.getAppName());
    url.append("/");
    url.append(this.request.getApiNumber());
  }

  private void appendQueryString(StringBuilder url) throws UnsupportedEncodingException {
    url.append("?");
    url.append("Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1");

    for (DtcRequestParameter param : this.request.getRequestParameters()) {
      if (param.getKey().equals("IP") || param.getKey().equals("Port")) {
        continue;
      }
      url.append("&");
      url.append(param.getKey());
      url.append("=");
      url.append(URLEncoder.encode(DtcHelper.getOrElse(param.getValue(), ""),
          this.request.getCharset()));
    }
  }

  public String combineUrl() throws UnsupportedEncodingException {
    StringBuilder url = new StringBuilder();
    this.appendOrigin(url);
    this.appendQueryString(url);
    return url.toString();
  }
}
