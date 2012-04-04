package net.skcomms.dtc.client;

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
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;

public class RequestParameterSetter {

  private final FrameElement requestFrame;

  private RequestParameterSetter() {
    Element dtcFrame = Document.get().getElementsByTagName("iframe").getItem(1);
    Document doc = IFrameElement.as(dtcFrame).getContentDocument();
    this.requestFrame = FrameElement.as(doc.getElementsByTagName("frame").getItem(0));
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

  private static native void enableIpTextInputElement(JavaScriptObject requestFrame) /*-{
		requestFrame.contentWindow.fnCHANGE_IP();
  }-*/;

  private static void setInputElementValue(TableRowElement row, String value) {
    NodeList<TableCellElement> cells = row.getCells();
    for (int i = 0; i < cells.getItem(1).getChildCount(); i++) {
      if (cells.getItem(1).getChild(i).getNodeType() == Node.ELEMENT_NODE) {
        InputElement inputElement = InputElement.as(Element.as(cells.getItem(1).getChild(i)));
        inputElement.setValue(value);
        break;
      }
    }
  }

  private InputElement findInputElement(String name) {
    InputElement inputElement;
    if (name.equals("IP")) {
      Element ipElement = this.requestFrame.getContentDocument().getElementById("ip_text");
      inputElement = InputElement.as(ipElement);

    } else {
      inputElement = this.findInputElementByCellText(name);
    }
    return inputElement;
  }

  private void setDtcRequestParameter(String name, String value) {
    if (name.equals("IP")) {
      enableIpTextInputElement(this.requestFrame.cast());
    }

    InputElement inputElement = this.findInputElement(name);
    if (inputElement != null) {
      inputElement.setValue(value);
    }
  }

  private void setDtcRequestParameters() {
    Map<String, List<String>> paramValues = Window.Location.getParameterMap();

    Set<Entry<String, List<String>>> entries = paramValues.entrySet();
    for (Entry<String, List<String>> entry : entries) {
      this.setDtcRequestParameter(entry.getKey(), entry.getValue().get(0));
    }
  }

  public static void execute() {
    new RequestParameterSetter().setDtcRequestParameters();
  }
}
