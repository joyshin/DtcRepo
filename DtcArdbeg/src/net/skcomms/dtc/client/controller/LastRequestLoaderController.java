package net.skcomms.dtc.client.controller;

import java.util.HashMap;
import java.util.Map;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DomExplorerHelper;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.PersistenceManager;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.dom.client.SelectElement;

public class LastRequestLoaderController extends DefaultDtcArdbegObserver {

  private static final String EMPTY_STRING = "";

  private static final char FORM_VALUE_DELIMETER = 0x0b;

  private static final char FORM_FIELD_DELIMETER = 0x0C;

  private static String findKey(NodeCollection<Element> nodeCollection) {
    String nameAttr;
    for (int i = 0; i < nodeCollection.getLength(); i++) {
      nameAttr = nodeCollection.getItem(i).getAttribute("name");
      if (nameAttr.equals("c")) {
        return nodeCollection.getItem(i).getAttribute("value");
      }
    }
    return LastRequestLoaderController.EMPTY_STRING;
  }

  private static Map<String, String> getLastParameters(String key) {
    String data = PersistenceManager.getInstance().getItem(key);

    Map<String, String> map = new HashMap<String, String>();

    if (data == null) {
      return map;
    }

    String[] formFields = data.split(Character.toString(LastRequestLoaderController.FORM_FIELD_DELIMETER));
    if (formFields == null) {
      return map;
    }

    for (String element : formFields) {
      String[] pair = element.split(Character.toString(LastRequestLoaderController.FORM_VALUE_DELIMETER));
      if (pair.length == 2) {
        // GWT.log("Name :" + pair[0] + " Value :" + pair[1]);
        map.put(pair[0], pair[1]);
      }
    }

    return map;
  }

  private NodeCollection<Element> getFormControlElements(Document doc) {
    Document requestDocument = null;
    FrameElement frameElement = null;
    FormElement formElement = null;

    frameElement = DomExplorerHelper.getFrameElement(doc, "request");
    if (frameElement == null) {
      return null;
    }

    requestDocument = frameElement.getContentDocument();

    formElement = DomExplorerHelper.getFormElement(requestDocument, "frmMain");
    if (formElement == null) {
      return null;
    }

    NodeCollection<Element> nodeCollection = formElement.getElements();
    return nodeCollection;
  }

  /**
   * @param dtcArdbeg
   */
  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
  }

  public void recall(Document doc) {
    NodeCollection<Element> nodeCollection = getFormControlElements(doc);
    if (nodeCollection == null) {
      return;
    }

    String key = LastRequestLoaderController.findKey(nodeCollection);
    Map<String, String> params = LastRequestLoaderController.getLastParameters(key);
    for (int i = 0; i < nodeCollection.getLength(); i++) {
      String name = nodeCollection.getItem(i).getAttribute("name");
      String value = params.get(name);
      if (value != null) {
        if (name.matches("REQUEST[0-9]+") || name.equals("ip_text") || name.equals("port")) {
          InputElement inputElem = InputElement.as(nodeCollection.getItem(i));
          inputElem.setValue(value);
        } else if (name.equals("ip_select")) {
          SelectElement selectElem = SelectElement.as(nodeCollection.getItem(i));
          selectElem.setValue(value);
        }
      }
    }
  }

  @Override
  public void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success) {
    persist(dtcFrameDoc);
    if (success) {
    }
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    recall(dtcFrameDoc);
  }

  @Override
  public void onSubmittingDtcRequest() {
  }

  public void persist(Document dtcFrameDoc) {
    NodeCollection<Element> nodeCollection = getFormControlElements(dtcFrameDoc);
    if (nodeCollection == null) {
      return;
    }

    String key = LastRequestLoaderController.EMPTY_STRING;
    StringBuilder data = new StringBuilder();
    for (int i = 0; i < nodeCollection.getLength(); i++) {
      String name = nodeCollection.getItem(i).getAttribute("name");
      if (name.equals("c")) {
        key = nodeCollection.getItem(i).getAttribute("value");
      } else {
        String value = null;
        if (name.matches("REQUEST[0-9]+") || name.equals("ip_text") || name.equals("port")) {
          InputElement inputElem = InputElement.as(nodeCollection.getItem(i));
          value = inputElem.getValue();
        } else if (name.equals("ip_select")) {
          SelectElement selectElem = SelectElement.as(nodeCollection.getItem(i));
          value = selectElem.getValue();
        } else {
          continue;
        }
        data.append(name);
        data.append(LastRequestLoaderController.FORM_VALUE_DELIMETER);
        data.append(value);
        data.append(LastRequestLoaderController.FORM_FIELD_DELIMETER);
      }
    }

    PersistenceManager.getInstance().setItem(key, data.toString());
  }
}