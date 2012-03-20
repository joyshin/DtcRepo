package net.skcomms.dtc.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

	private final static String DTC_HOME_URL = "http://127.0.0.1:8888/testpage/DtcList.html";

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

	private static void removeComaparePageAnchor(Document doc) {
		NodeList<Element> nodes = doc.getElementsByTagName("a");
		Element anchor = nodes.getItem(0);
		anchor.getParentElement().removeChild(anchor);
	}

}
