package net.skcomms.dtc.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Cookies;

public class CookieHandler {
  private ElementExplorer explorer ;
  
  private static char FORM_VALUE_DELIMETER = 0x0b;
  private static char FORM_FIELD_DELIMETER = 0x0C;
  private HashMap<String,String> formValueMap;
    
  private String cookie;
  private String cookieKey;
  
  
  public CookieHandler() {
    explorer = new ElementExplorer();    
    formValueMap = new HashMap<String,String>();
    cookie = "";
    cookieKey = "";
  }
    
  public void writeCookie(Object obj) {
    GWT.log("Here Write Cookie");

    Document doc = (Document) obj;
    Document requestDocument = null;
    FrameElement frameElement = null;
    FormElement formElement = null;
    
    frameElement = explorer.getFrameElement(doc, "request");
    if ( frameElement == null ) {
      return;
    }
    
    requestDocument = frameElement.getContentDocument();
    
    formElement = explorer.getFormElement(requestDocument, "frmMain");
    if ( formElement == null ) { 
      return;
    }
        
    NodeCollection<Element> nodeCollection = formElement.getElements();
    if (nodeCollection == null ) 
      return;
    
    String formAttrName = "";
    String formAttrValue = "";
    
    InputElement inputElem = null;
    SelectElement selectElem = null;
    
    cookie = "";
    cookieKey ="";
    
    for (int i=0; i<nodeCollection.getLength(); i++) {
      formAttrName = nodeCollection.getItem(i).getAttribute("name");
      if (formAttrName.equals("c")) {
        cookieKey = nodeCollection.getItem(i).getAttribute("value");
      } else {   
        if (formAttrName.matches("REQUEST[0-9]+") || formAttrName.equals("ip_text") || formAttrName.equals("port")) {
          inputElem = InputElement.as(nodeCollection.getItem(i));
          formAttrValue = inputElem.getValue();
        } else if (formAttrName.equals("ip_select") ) {
          selectElem = SelectElement.as(nodeCollection.getItem(i));
          formAttrValue = selectElem.getValue();
        } 
        cookie = cookie + formAttrName + Character.toString(FORM_VALUE_DELIMETER) + formAttrValue + Character.toString(FORM_FIELD_DELIMETER);
      }
    }
    
    Date now = new Date();
    
    long nowLong = now.getTime();
    nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);
    now.setTime(nowLong);
    Cookies.setCookie(cookieKey, cookie, now);
    //GWT.log("setCookie: " + cookieKey + " : " + cookie);
    return;
  }
  
  public void readCookie(Document doc) {

    Document requestDocument = null;
    FrameElement frameElement = null;
    FormElement formElement = null;    
    
    frameElement = explorer.getFrameElement(doc, "request");
    if ( frameElement == null ) {
      return;
    }
    
    requestDocument = frameElement.getContentDocument();
    
    formElement = explorer.getFormElement(requestDocument, "frmMain");
    if ( formElement == null ) { 
      return;
    }
    
    NodeCollection<Element> nodeCollection = formElement.getElements();
    if (nodeCollection == null ) 
      return;
    
    String formAttrName;
    
    cookie = "";
    cookieKey ="";
    
    for (int i=0; i<nodeCollection.getLength(); i++) {
      formAttrName = nodeCollection.getItem(i).getAttribute("name");
      if (formAttrName.equals("c")) {
        cookieKey = nodeCollection.getItem(i).getAttribute("value");
        break;
      }                   
    }
    
    cookie = Cookies.getCookie(cookieKey);
    GWT.log("getCookie: " + cookieKey + " : " + cookie);
    
    if (cookie == null) {
      return;
    }
    
    String[] formFieldArray = cookie.split(Character.toString(FORM_FIELD_DELIMETER));    
    String[] formValueArray;
    if (formFieldArray == null)
      return;
    
    for(int i=0; i < formFieldArray.length; i++) {
      formValueArray = formFieldArray[i].split(Character.toString(FORM_VALUE_DELIMETER));
      if (formValueArray == null) 
        continue;        
      if (formValueArray.length != 2)
        continue;
      GWT.log("Name :" + formValueArray[0] + " Value :" + formValueArray[1]);
      formValueMap.put(formValueArray[0], formValueArray[1]);
    }
    
    InputElement inputElem = null;
    SelectElement selectElem = null;

    for (int i=0; i<nodeCollection.getLength(); i++) {
      formAttrName = nodeCollection.getItem(i).getAttribute("name");
      if (formAttrName.matches("REQUEST[0-9]+") || formAttrName.equals("ip_text") || formAttrName.equals("port")) {
        inputElem = InputElement.as(nodeCollection.getItem(i));
        inputElem.setValue(formValueMap.get(formAttrName));
      } else if (formAttrName.equals("ip_select") ) {
        selectElem = SelectElement.as(nodeCollection.getItem(i));
        selectElem.setValue(formValueMap.get(formAttrName));         
      }
    }
    return;
  }
}
