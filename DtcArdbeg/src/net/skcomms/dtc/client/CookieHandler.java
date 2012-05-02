package net.skcomms.dtc.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Cookies;

public class CookieHandler extends DefaultDtcArdbegObserver {

  private static final String EMPTY_STRING = "";

  private static final char FORM_VALUE_DELIMETER = 0x0b;

  private static final char FORM_FIELD_DELIMETER = 0x0C;

  private static String findCookieKey(NodeCollection<Element> nodeCollection) {
    String nameAttr;
    for (int i = 0; i < nodeCollection.getLength(); i++) {
      nameAttr = nodeCollection.getItem(i).getAttribute("name");
      if (nameAttr.equals("c")) {
        return nodeCollection.getItem(i).getAttribute("value");
      }
    }
    return CookieHandler.EMPTY_STRING;
  }

  private static Map<String, String> getLastParametersFromCookie(String cookieKey) {
    String cookie = Cookies.getCookie(cookieKey);
    // GWT.log("getCookie: " + cookieKey + " : " + cookie);

    Map<String, String> map = new HashMap<String, String>();

    if (cookie == null) {
      return map;
    }

    String[] formFields = cookie.split(Character.toString(CookieHandler.FORM_FIELD_DELIMETER));
    if (formFields == null) {
      return map;
    }

    for (String element : formFields) {
      String[] pair = element.split(Character.toString(CookieHandler.FORM_VALUE_DELIMETER));
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

  public void loadAndSetRequestParameters(Document doc) {
    NodeCollection<Element> nodeCollection = this.getFormControlElements(doc);
    if (nodeCollection == null) {
      return;
    }

    String cookieKey = CookieHandler.findCookieKey(nodeCollection);
    Map<String, String> params = CookieHandler.getLastParametersFromCookie(cookieKey);
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
    if (success) {
      this.storeRequestParametersIntoCookie(dtcFrameDoc);
    }
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    this.loadAndSetRequestParameters(dtcFrameDoc);
  }

  @Override
  public void onSubmittingDtcRequest() {
  }

  public void storeRequestParametersIntoCookie(Document dtcFrameDoc) {
    NodeCollection<Element> nodeCollection = this.getFormControlElements(dtcFrameDoc);
    if (nodeCollection == null) {
      return;
    }

    String cookieKey = CookieHandler.EMPTY_STRING;
    StringBuilder cookieValue = new StringBuilder();
    for (int i = 0; i < nodeCollection.getLength(); i++) {
      String name = nodeCollection.getItem(i).getAttribute("name");
      if (name.equals("c")) {
        cookieKey = nodeCollection.getItem(i).getAttribute("value");
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
        cookieValue.append(name);
        cookieValue.append(CookieHandler.FORM_VALUE_DELIMETER);
        cookieValue.append(value);
        cookieValue.append(CookieHandler.FORM_FIELD_DELIMETER);
      }
    }

    Date now = new Date();
    long nowLong = now.getTime();
    nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);
    now.setTime(nowLong);
    Cookies.setCookie(cookieKey, cookieValue.toString(), now);
  }
}
