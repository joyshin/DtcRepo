package net.skcomms.dtc.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

public class DomExplorerHelper {

  public static FormElement getFormElement(Document doc, String name) {
    NodeList<Element> elementList = doc.getElementsByTagName("Form");
    for (int i = 0; i < elementList.getLength(); i++) {
      String attribute = elementList.getItem(i).getAttribute("name");
      if (attribute.equals(name)) {
        return FormElement.as(elementList.getItem(i));
      }
    }
    return null;
  }

  public static FrameElement getFrameElement(Document doc, String name) {
    NodeList<Element> elementList = doc.getElementsByTagName("frame");
    for (int i = 0; i < elementList.getLength(); i++) {
      String attribute = elementList.getItem(i).getAttribute("name");
      if (attribute.equals(name)) {
        return FrameElement.as(elementList.getItem(i));
      }
    }
    return null;
  }
  
  public static DivElement getDivElement(Document doc, String name) {
    NodeList<Element> elementList = doc.getElementsByTagName("div");
    for (int i = 0; i < elementList.getLength(); i++) {
      String attribute = elementList.getItem(i).getAttribute("name");
      if (attribute.equals(name)) {
        return DivElement.as(elementList.getItem(i));
      }
    }
    return null;
  }
 
}
