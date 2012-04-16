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

public class DtcRequestFormAccessor {

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

    this.updateRequestFormElements();
  }

  private boolean containsInOptions(String value) {
    NodeList<OptionElement> options = this.ipSelectElement.getOptions();
    for (int i = 0; i < options.getLength(); i++) {
      if (options.getItem(i).getValue().equals(value)) {
        return true;
      }
    }

    return false;
  }

  private void enableIpInputElement(InputElementType type) {
    boolean ipTextEnabling = (type == InputElementType.TEXT);

    if (this.isIpTextEnabled() != ipTextEnabling) {
      toggleIpElement(this.requestFrame.cast());
    }
  }

  private InputElement findInputElementByCellText(String name) {
    Element requestTable = this.requestFrame.getContentDocument().getElementById(
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
    if (this.isIpTextEnabled()) {
      ipValue = this.ipTextElement.getValue();
    } else {
      ipValue = this.ipSelectElement.getValue();
    }

    return ipValue != null ? ipValue : EMPTY;
  }

  public String getDtcRequestParameter(String name) {

    if (!this.isAvailable()) {
      return EMPTY;
    }

    if (name.equals("IP")) {
      return this.getDtcIpRequestParameter();
    } else {
      InputElement inputElement = this.findInputElementByCellText(name);
      return inputElement != null ? inputElement.getValue() : EMPTY;
    }
  }

  public Map<String, String> getDtcRequestParameters() {
    Map<String, String> pairs = new HashMap<String, String>();
    if (!this.isAvailable()) {
      return pairs;
    }

    List<String> names = this.getParameterNames();
    for (String name : names) {
      pairs.put(name, this.getDtcRequestParameter(name));
    }

    return pairs;
  }

  private List<String> getParameterNames() {
    Element requestTable = this.requestFrame.getContentDocument().getElementById("tblREQUEST");
    NodeList<TableRowElement> tableRows = TableElement.as(requestTable).getRows();

    List<String> names = new ArrayList<String>();
    for (int i = 0; i < tableRows.getLength(); i++) {
      TableCellElement cell = tableRows.getItem(i).getCells().getItem(0);
      names.add(cell.getInnerText().trim());
    }

    return names;
  }

  public boolean isAvailable() {
    return this.isAvailable;
  }

  private boolean isIpTextEnabled() {
    return this.ipTextElement.getStyle().getDisplay().equals(Display.BLOCK.getCssName());
  }

  private void setAvailable(boolean available) {
    this.isAvailable = available;
  }

  private void setDtcIpRequestParameter(String value) {
    if (this.containsInOptions(value)) {
      this.ipSelectElement.setValue(value);
      this.enableIpInputElement(InputElementType.SELECT);
    } else {
      this.ipTextElement.setValue(value);
      this.enableIpInputElement(InputElementType.TEXT);
    }
  }

  public void setDtcRequestParameter(String name, String value) {
    if (!this.isAvailable()) {
      return;
    }

    if (name.equals("IP")) {
      this.setDtcIpRequestParameter(value);
    } else {
      InputElement inputElement = this.findInputElementByCellText(name);
      if (inputElement != null) {
        inputElement.setValue(value);
      }
    }
  }

  public void setDtcRequestParameters(Map<String, String> paramValues) {
    if (!this.isAvailable()) {
      return;
    }

    Set<Entry<String, String>> entries = paramValues.entrySet();
    for (Entry<String, String> entry : entries) {
      this.setDtcRequestParameter(entry.getKey(), entry.getValue());
    }
  }

  public void update() {
    this.updateRequestFormElements();
  }

  private void updateRequestFormElements() {
    Element dtcFrame = Document.get().getElementsByTagName("iframe").getItem(1);
    Document doc = IFrameElement.as(dtcFrame).getContentDocument();
    if (doc.getElementsByTagName("frame").getLength() == 0) {
      this.setAvailable(false);
      return;
    }

    this.requestFrame = FrameElement.as(doc.getElementsByTagName("frame").getItem(0));
    this.ipSelectElement = SelectElement.as(this.requestFrame
        .getContentDocument()
        .getElementById("ip_select"));
    this.ipTextElement = InputElement.as(this.requestFrame.getContentDocument()
        .getElementById("ip_text"));

    this.setAvailable(true);
  }
}
