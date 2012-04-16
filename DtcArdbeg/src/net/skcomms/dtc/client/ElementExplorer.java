package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;

public class ElementExplorer {
  
  //Document doc;
  /*
  public ElementExplorer() {
    
    doc = Document.get();
    
    Element dtcFrame = Document.get().getElementsByTagName("iframe").getItem(1);
    Document doc = IFrameElement.as(dtcFrame).getContentDocument();
    //this.requestFrame = FrameElement.as(doc.getElementsByTagName("frame").getItem(0));
  }
    */
  
    
  public FrameElement getFrameElement(Document doc, String name) {
    String attribute;
    FrameElement element = null;
    NodeList<Element> elementList = doc.getElementsByTagName("frame");
    
    for(int i=0; i < elementList.getLength(); i++) {
      attribute = elementList.getItem(i).getAttribute("name");
      
      if (attribute.equals(name)) {
        element = FrameElement.as(elementList.getItem(i));
        break;
      }
    }
    return element;
  }
  
  public FormElement getFormElement(Document doc, String name) {
    
    //Document doc = IFrameElement.as(DtcArdbeg.this.dtcFrame.getElement()).getContentDocument();
    String attribute;
    FormElement element = null;
    NodeList<Element> elementList = doc.getElementsByTagName("Form");
    
    for (int i=0; i < elementList.getLength(); i++ ) {
      attribute = elementList.getItem(i).getAttribute("name");
      
      if (attribute.equals(name)) {                           
        element = FormElement.as(elementList.getItem(i));
        break;
      }
    }
    return element;
  }

  
  
}
