package net.skcomms.dtc.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcArdbeg implements EntryPoint {

	private final static ServiceDao serviceDao = new ServiceDao();

	private final static String DTC_HOME_URL = "http://127.0.0.1:8888/testpage/DtcList.html";
	private final Frame frame = new Frame();

	@Override
	public void onModuleLoad() {
		this.initializeDtcFrame();

		this.initializeNavigationBar();

	}

	/**
	 * 
	 */
	private void loadCookies() {
		String cookieValue = Cookies.getCookie("visit");
		int visitCount = 0;
		if (cookieValue == null) {
			visitCount = 1;
		} else {
			visitCount = Integer.parseInt(cookieValue) + 1;
		}
		Cookies.setCookie("visit", Integer.toString(visitCount));
		Window.alert("You visit here " + visitCount + " times.");
	}

	private void initializeNavigationBar() {
		Anchor navi = new Anchor("Home");

		navi.getElement().addClassName("navi-bar");

		navi.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DtcArdbeg.this.frame.setUrl(DtcArdbeg.DTC_HOME_URL);
			}
		});
		RootPanel.get("naviBarContainer").add(navi);
	}

	private void initializeDtcFrame() {
		this.frame.setPixelSize(Window.getClientWidth() - 30,
				Window.getClientHeight() - 120);
		this.frame.setUrl(DtcArdbeg.DTC_HOME_URL);
		this.frame.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				Document doc = IFrameElement.as(
						DtcArdbeg.this.frame.getElement()).getContentDocument();

				if (doc == null) {
					return;
				}

				// TODO 서비스 선택 판정을 링크 클릭으로 변경해야 함.
				if (doc.getReferrer().equals(DtcArdbeg.DTC_HOME_URL)) {
					int index = doc.getURL().indexOf("?b=");
					if (index != -1) {
						String serviceName = doc.getURL().substring(index + 3)
								.replaceAll("/", "");
						DtcArdbeg.serviceDao.addVisitCount(serviceName);
					}
				}

				if (doc.getURL().equals(DtcArdbeg.DTC_HOME_URL)) {
					DtcArdbeg.removeComaparePageAnchor(doc);
				}

			}
		});

		RootPanel.get("dtcContainer").add(this.frame);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				DtcArdbeg.this.frame.setPixelSize(Window.getClientWidth() - 30,
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
