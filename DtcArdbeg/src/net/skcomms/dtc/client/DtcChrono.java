package net.skcomms.dtc.client;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Text;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.RootPanel;

public class DtcChrono extends DefaultDtcArdbegObserver {

  private static class ChronoAnimation extends Animation {

    public static String getCurrentTimeString() {
      Date currentDate = new Date();
      return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(currentDate);
    }

    private long startTime;

    private final Style chronoStyle;

    private final Text elapsedTimeText;

    private final Text searchTimeText;

    public ChronoAnimation(Style aChronoStyle, Text aSearchTimeText, Text anElapsedTimeText) {
      chronoStyle = aChronoStyle;
      searchTimeText = aSearchTimeText;
      elapsedTimeText = anElapsedTimeText;

    }

    private void changeColor(String value) {
      chronoStyle.setColor(value);
    }

    @Override
    protected void onCancel() {
      updateTimeText();
      changeColor("grey");
    }

    @Override
    protected void onComplete() {
      updateTimeText();
      changeColor("grey");
    }

    @Override
    protected void onStart() {
      searchTimeText.setData(ChronoAnimation.getCurrentTimeString());
      startTime = new Date().getTime();
      changeColor("red");
    }

    @Override
    protected void onUpdate(double progress) {
      updateTimeText();
    }

    private void updateTimeText() {
      long currentTime = new Date().getTime();
      elapsedTimeText.setData(" " + Long.toString(currentTime - startTime) + " ms");
    }
  }

  public native static void addDtcSearchButtonEventHandler(DtcChrono chrono, Document dtcDoc) /*-{
		var searchButton = dtcDoc.getElementsByTagName("frame")[0].contentWindow.document
				.getElementById("div_search");
		searchButton.onclick = function() {
			chrono.@net.skcomms.dtc.client.DtcArdbeg::onSubmitRequestForm()();
		};
  }-*/;

  ChronoAnimation chronoAnimation;

  public void createChrono(Document dtcFrameDoc) {
    DivElement chronoElement = dtcFrameDoc.createDivElement();
    chronoElement.setId("responseTimeContainer");

    Text searchTimeText = dtcFrameDoc.createTextNode(ChronoAnimation.getCurrentTimeString());
    Text elapsedTimeText = dtcFrameDoc.createTextNode("");
    chronoElement.appendChild(searchTimeText);
    chronoElement.appendChild(elapsedTimeText);

    Style chronoStyle = chronoElement.getStyle();
    chronoStyle.setFontStyle(Style.FontStyle.NORMAL);
    chronoStyle.setFontSize(12, Style.Unit.PX);
    chronoStyle.setFontWeight(Style.FontWeight.BOLD);
    chronoStyle.setColor("gray");

    chronoAnimation = new ChronoAnimation(chronoStyle, searchTimeText, elapsedTimeText);

    RootPanel.get("chronoContainer").getElement().appendChild(chronoElement);
  }

  public void end() {
    chronoAnimation.cancel();
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
    createChrono(dtcArdbeg.getDtcFrameDoc());
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
    RootPanel.get("chronoContainer").setVisible(false);
  }

  @Override
  public void onDtcHomeLoaded(String path) {
    RootPanel.get("chronoContainer").setVisible(false);
  }

  @Override
  public void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success) {
    end();
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    RootPanel.get("chronoContainer").setVisible(true);
  }

  @Override
  public void onSubmittingDtcRequest() {
    start();
  }

  public void start() {
    chronoAnimation.run(10000);
  }
}
