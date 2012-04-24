package net.skcomms.dtc.client;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Text;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

public class DtcChrono {

  private static class ChronoAnimation extends Animation {

    public static String getCurrentTimeString() {
      Date currentDate = new Date();
      return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(currentDate);
    }

    private long startTime;

    private Style chronoStyle;

    private Text elapsedTimeText;

    private Text searchTimeText;

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

  ChronoAnimation chronoAnimation;

  public void createChrono(Document dtcRequestDoc) {
    DivElement chronoElement = dtcRequestDoc.createDivElement();
    chronoElement.setId("responseTimeContainer");

    Text searchTimeText = dtcRequestDoc.createTextNode(ChronoAnimation.getCurrentTimeString());
    Text elapsedTimeText = dtcRequestDoc.createTextNode("");
    chronoElement.appendChild(searchTimeText);
    chronoElement.appendChild(elapsedTimeText);

    Style chronoStyle = chronoElement.getStyle();
    chronoStyle.setFontStyle(Style.FontStyle.NORMAL);
    chronoStyle.setFontSize(12, Style.Unit.PX);
    chronoStyle.setFontWeight(Style.FontWeight.BOLD);
    chronoStyle.setColor("gray");

    this.chronoAnimation = new ChronoAnimation(chronoStyle, searchTimeText, elapsedTimeText);

    dtcRequestDoc.getBody().insertFirst(chronoElement);
  }

  public void end() {
    this.chronoAnimation.cancel();
  }

  public void start() {

    this.chronoAnimation.run(10000);
  }
}
