package net.skcomms.dtc.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;

public class DtcRequestFormAccessor extends DefaultDtcArdbegObserver {

  private static final String EMPTY = "";

  private static native void toggleIpElement(JavaScriptObject requestFrame) /*-{
    requestFrame.contentWindow.fnCHANGE_IP();
  }-*/;

  private boolean isAvailable = false;

  public DtcRequestFormAccessor() {
  }

  private boolean containsInOptions(String value) {
    if (!this.isAvailable) {
      return false;
    }

    return false;
  }

  String getParameterFromDtcFrame(Document dtcFrameDoc, String name) {
    return this.getParameterMapFromDtcFrame(dtcFrameDoc).get(name);
  }

  Map<String, String> getParameterMapFromDtcFrame(Document dtcFrameDoc) {
    Map<String, String> params = new HashMap<String, String>();
    String url = dtcFrameDoc.getURL();
    String queryString = "";
    if (url.indexOf('?') != -1) {
      queryString = url.substring(url.indexOf('?') + 1);
    }
    String[] dtcParams = queryString.split("&");
    for (String param : dtcParams) {
      String[] entry = param.split("=");
      if (entry.length < 2) {
        params.put(entry[0], null);
      } else {
        params.put(entry[0], entry[1]);
      }
    }
    return params;
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
  }

  public boolean isAvailable() {
    return this.isAvailable;
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
  }

  @Override
  public void onDtcHomeLoaded() {
  }

  @Override
  public void onDtcTestPageLoaded(DtcRequestInfoModel requestInfo) {
    this.setRequestParametersFromUrl(requestInfo);
  }

  private void setAvailable(boolean available) {
    this.isAvailable = available;
  }

  /**
   * @param key
   * @param string
   */
  private void setDtcRequestParameter(String key, String string) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  private void setRequestParametersFromUrl(DtcRequestInfoModel requestInfo) {
    String ardbegParam = Window.Location.getParameter("c");
    if (ardbegParam != null && ardbegParam.equals(requestInfo.getPath().substring(1))) {
      Set<Entry<String, List<String>>> paramValues = Window.Location.getParameterMap().entrySet();
      for (Entry<String, List<String>> entry : paramValues) {
        this.setDtcRequestParameter(entry.getKey(), entry.getValue().get(0));
      }
    }
  }

}
