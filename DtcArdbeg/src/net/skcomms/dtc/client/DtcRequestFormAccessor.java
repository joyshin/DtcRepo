package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;

public class DtcRequestFormAccessor extends DefaultDtcArdbegObserver {

  private enum InputElementType {
    SELECT, TEXT;
  }

  private static final String EMPTY = "";

  private static native void toggleIpElement(JavaScriptObject requestFrame) /*-{
		requestFrame.contentWindow.fnCHANGE_IP();
  }-*/;

  private FrameElement requestFrame;
  private SelectElement ipSelectElement;
  private InputElement ipTextElement;

  private boolean isAvailable = false;

  public DtcRequestFormAccessor() {

    updateRequestFormElements();
  }

  private boolean containsInOptions(String value) {
    if (!isAvailable) {
      return false;
    }

    if (ipSelectElement == null) {
      return false;
    }

    NodeList<OptionElement> options = ipSelectElement.getOptions();
    for (int i = 0; i < options.getLength(); i++) {
      if (options.getItem(i).getValue().equals(value)) {
        return true;
      }
    }

    return false;
  }

  private void enableIpInputElement(InputElementType type) {
    boolean ipTextEnabling = (type == InputElementType.TEXT);

    if (isIpTextEnabled() != ipTextEnabling) {
      DtcRequestFormAccessor.toggleIpElement(requestFrame.cast());
    }
  }

  private InputElement findInputElementByCellText(String name) {
    Element requestTable = requestFrame.getContentDocument().getElementById(
        "tblREQUEST");
    NodeList<TableRowElement> tableRows = TableElement.as(requestTable).getRows();

    for (int i = 0; i < tableRows.getLength(); i++) {
      TableRowElement row = tableRows.getItem(i);
      if (row.getInnerText().trim().equals(name)) {
        NodeList<TableCellElement> cells = row.getCells();
        for (int j = 0; j < cells.getItem(1).getChildCount(); j++) {
          if (cells.getItem(1).getChild(j).getNodeType() == Node.ELEMENT_NODE) {
            return InputElement.as(Element.as(cells.getItem(1).getChild(j)));
          }
        }
      }
    }
    return null;
  }

  private String getDtcIpRequestParameter() {
    String ipValue;
    if (isIpTextEnabled()) {
      ipValue = ipTextElement.getValue();
    } else {
      ipValue = ipSelectElement.getValue();
    }

    return ipValue != null ? ipValue : DtcRequestFormAccessor.EMPTY;
  }

  public String getDtcRequestParameter(String name) {

    if (!isAvailable()) {
      return DtcRequestFormAccessor.EMPTY;
    }

    if (name.equals("IP")) {
      return getDtcIpRequestParameter();
    } else {
      InputElement inputElement = findInputElementByCellText(name);
      return inputElement != null ? inputElement.getValue() : DtcRequestFormAccessor.EMPTY;
    }
  }

  public Map<String, String> getDtcRequestParameters() {
    Map<String, String> pairs = new HashMap<String, String>();
    if (!isAvailable()) {
      return pairs;
    }

    List<String> names = getParameterNames();
    for (String name : names) {
      pairs.put(name, getDtcRequestParameter(name));
    }

    return pairs;
  }

  String getParameterFromDtcFrame(Document dtcFrameDoc, String name) {
    return getParameterMapFromDtcFrame(dtcFrameDoc).get(name);
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

  private List<String> getParameterNames() {
    Element requestTable = requestFrame.getContentDocument().getElementById("tblREQUEST");
    NodeList<TableRowElement> tableRows = TableElement.as(requestTable).getRows();

    List<String> names = new ArrayList<String>();
    for (int i = 0; i < tableRows.getLength(); i++) {
      TableCellElement cell = tableRows.getItem(i).getCells().getItem(0);
      names.add(cell.getInnerText().trim());
    }

    return names;
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  private boolean isIpTextEnabled() {
    return ipTextElement.getStyle().getDisplay().equals(Display.BLOCK.getCssName());
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
    update();
  }

  @Override
  public void onDtcHomeLoaded(String path) {
    update();
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    update();
    setUrlParameters(dtcFrameDoc);
  }

  private void setAvailable(boolean available) {
    isAvailable = available;
  }

  private void setDtcIpRequestParameter(String value) {
    if (containsInOptions(value)) {
      ipSelectElement.setValue(value);
      enableIpInputElement(InputElementType.SELECT);
    } else {
      ipTextElement.setValue(value);
      enableIpInputElement(InputElementType.TEXT);
    }
  }

  public void setDtcRequestParameter(String name, String value) {
    if (!isAvailable()) {
      return;
    }

    if (name.equals("IP")) {
      setDtcIpRequestParameter(value);
    } else {
      InputElement inputElement = findInputElementByCellText(name);
      if (inputElement != null) {
        inputElement.setValue(value);
      }
    }
  }

  public void setDtcRequestParameters(Map<String, String> paramValues) {
    if (!isAvailable()) {
      return;
    }

    Set<Entry<String, String>> entries = paramValues.entrySet();
    for (Entry<String, String> entry : entries) {
      setDtcRequestParameter(entry.getKey(), entry.getValue());
    }
  }

  private void setUrlParameters(Document dtcFrameDoc) {
    String ardbegParam = Window.Location.getParameter("c");
    String dtcFrameParam = getParameterFromDtcFrame(dtcFrameDoc, "c");
    if (ardbegParam != null && ardbegParam.equals(dtcFrameParam)) {
      Set<Entry<String, List<String>>> paramValues = Window.Location.getParameterMap().entrySet();
      for (Entry<String, List<String>> entry : paramValues) {
        setDtcRequestParameter(entry.getKey(), entry.getValue().get(0));
      }
    }
  }

  public void update() {
    updateRequestFormElements();
  }

  private void updateRequestFormElements() {
    Element dtcFrame = Document.get().getElementsByTagName("iframe").getItem(1);
    Document doc = IFrameElement.as(dtcFrame).getContentDocument();
    if (doc.getElementsByTagName("frame").getLength() == 0) {
      setAvailable(false);
      return;
    }

    requestFrame = FrameElement.as(doc.getElementsByTagName("frame").getItem(0));
    ipTextElement = InputElement.as(requestFrame.getContentDocument()
        .getElementById("ip_text"));
    Element element = requestFrame.getContentDocument().getElementById("ip_select");
    if (element != null) {
      ipSelectElement = SelectElement.as(element);
    }

    setAvailable(true);
  }
}
