package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

	private final static String DTC_HOME_URL = "http://127.0.0.1:8888/testpage/DtcList.html";
	private final static String TARGET_ATTRIBUTE_NAME = "class";
	private final static String KUWON_CLASS_ATTRIBUTE = "Kuwon";
	private final static String NAVIGATION_DELIMITER = " > ";

	private final static HorizontalPanel navPanel = new HorizontalPanel();
	
	@Override
	public void onModuleLoad() {
		final Frame frame = new Frame();
		frame.setPixelSize(Window.getClientWidth() - 30,
				Window.getClientHeight() - 120);
		frame.setUrl(DtcArdbeg.DTC_HOME_URL);
		frame.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				Document doc = IFrameElement.as(frame.getElement())
						.getContentDocument();

				if (doc.getURL().equals(DtcArdbeg.DTC_HOME_URL)) {
					DtcArdbeg.removeComaparePageAnchor(doc);
					initNavigationBar();
				} else {
					String currentUrl = getCurrentUrl(doc);
					String[] pathArray = getNavigationPath(currentUrl);
					addPathToNavigationBar(pathArray, currentUrl);					
				}
				
				NodeList<Element> searchResult = getXmlResultDocuments(doc);
				if(searchResult != null) {
					doSomethingOnLoad(searchResult);
				}

			}

		});

		RootPanel.get("dtcContainer").add(frame);
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				frame.setPixelSize(Window.getClientWidth() - 30,
						Window.getClientHeight() - 200);
			}
		});

	}

	
	protected void addPathToNavigationBar(String[] pathArray,
			String currentUrl) {
		// TODO Auto-generated method stub
		//int valueStartIndex = currentUrl.lastIndexOf("?");
		//String addressSource = currentUrl.substring(0,valueStartIndex+1);
		String addressSource = new String(DTC_HOME_URL);
		addressSource = addressSource.concat("?");

		navPanel.clear();
		addCurrentAnchorToNavigationBar("Home",DTC_HOME_URL);
		addCurrentLabelToNavigationBar(NAVIGATION_DELIMITER);
		for(int i=0; i<pathArray.length-1;i++) {
			addCurrentAnchorToNavigationBar(pathArray[i], addressSource.concat(pathArray[i]));
			addCurrentLabelToNavigationBar(NAVIGATION_DELIMITER);
		}
		addCurrentLabelToNavigationBar(pathArray[pathArray.length-1]);
		
	}


	protected String[] getNavigationPath(String currentUrl) {
		// TODO Auto-generated method stub
		int valueStartIndex = currentUrl.lastIndexOf("?");
		String valueSource = currentUrl.substring(valueStartIndex+1);
		return valueSource.split("/");

	}


	protected void addCurrentAnchorToNavigationBar(String Text, String Href) {
		// TODO Auto-generated method stub
		Anchor anchor = new Anchor();
		anchor.setText(Text);
		anchor.setHref(Href);
		navPanel.add(anchor);
	}

	protected void addCurrentLabelToNavigationBar(String Text) {
		// TODO Auto-generated method stub
		Label label = new Label(Text);
		navPanel.add(label);
	}

	private void initNavigationBar() {
		// TODO Auto-generated method stub
		
		addCurrentAnchorToNavigationBar("Home",DTC_HOME_URL);
		RootPanel.get("naviBarContainer").add(navPanel);
		
	}


	protected String getCurrentUrl(Document doc) {
		// TODO Auto-generated method stub
		return doc.getURL();
	}


	private static void removeComaparePageAnchor(Document doc) {
		NodeList<Element> nodes = doc.getElementsByTagName("a");
		Element anchor = nodes.getItem(0);
		anchor.getParentElement().removeChild(anchor);
	}

	private static NodeList<Element> getXmlResultDocuments(Document doc) {
		NodeList<Element> frames = doc.getElementsByTagName("frame");
		if(frames == null || frames.getLength() < 2) return null;
		
		Document response = FrameElement.as(frames.getItem(1)).getContentDocument();
		
		return getXmlResultElementList(response);

	}

	private static NodeList<Element> getXmlResultElementList(Document response) {
		if (response != null) { 
			Element element = response.getElementById("xmlresult");
			Document doc = IFrameElement.as(element).getContentDocument();

			return doc.getElementsByTagName("Document");
		}
		return null;
	}

	private static void doSomethingOnLoad(NodeList<Element> searchResult) {
		// TODO Auto-generated method stub
		int nLength = searchResult.getLength();
		
		alertOnLoad();
		
		for(int i=0;i<nLength;i++) {
			setClassAttribute(searchResult.getItem(i));
		}
	}
	
	private static void alertOnLoad() {
		// TODO Auto-generated method stub
		myAlert();
	}

	private static native void myAlert() /*-{ $wnd.alert("Here are results!"); }-*/;

	private static void setClassAttribute(Element element) {
		// TODO Auto-generated method stub
		String previousAttributeValue = element.getAttribute(TARGET_ATTRIBUTE_NAME);
		if ( previousAttributeValue== null) {
			element.setAttribute(TARGET_ATTRIBUTE_NAME, KUWON_CLASS_ATTRIBUTE);
		} else {
			String newAttributeValue = new String (previousAttributeValue + " " + KUWON_CLASS_ATTRIBUTE);
			element.setAttribute(TARGET_ATTRIBUTE_NAME, newAttributeValue);
		}
	}

}
