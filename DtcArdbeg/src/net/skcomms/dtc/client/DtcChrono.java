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
      this.chronoStyle = aChronoStyle;
      this.searchTimeText = aSearchTimeText;
      this.elapsedTimeText = anElapsedTimeText;

    }

    private void changeColor(String value) {
      this.chronoStyle.setColor(value);
    }

    @Override
    protected void onCancel() {
      this.updateTimeText();
      this.changeColor("grey");
    }

    @Override
    protected void onComplete() {
      this.updateTimeText();
      this.changeColor("grey");
    }

    @Override
    protected void onStart() {
      this.searchTimeText.setData(ChronoAnimation.getCurrentTimeString());
      this.startTime = new Date().getTime();
      this.changeColor("red");
    }

    @Override
    protected void onUpdate(double progress) {
      this.updateTimeText();
    }

    private void updateTimeText() {
      long currentTime = new Date().getTime();
      this.elapsedTimeText.setData(" " + Long.toString(currentTime - this.startTime) + " ms");
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

    this.chronoAnimation = new ChronoAnimation(chronoStyle, searchTimeText, elapsedTimeText);

    RootPanel.get("chronoContainer").getElement().appendChild(chronoElement);
  }

  public void end() {
    this.chronoAnimation.cancel();
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);
    this.createChrono(dtcArdbeg.getDtcFrameDoc());
  }

  @Override
  public void onDtcDirectoryLoaded(String path) {
    RootPanel.get("chronoContainer").setVisible(false);
  }

  @Override
  public void onDtcHomeLoaded() {
    RootPanel.get("chronoContainer").setVisible(false);
  }

  @Override
  public void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success) {
    this.end();
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {
    RootPanel.get("chronoContainer").setVisible(true);
  }

  @Override
  public void onSubmittingDtcRequest() {
    this.start();
  }

  public void start() {
    this.chronoAnimation.run(10000);
  }
}
