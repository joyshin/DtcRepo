package net.skcomms.dtc.client.view;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.smartgwt.client.widgets.Label;

public class DtcChronoView extends Label {

  private static class ChronoAnimation extends Animation {

    public static String getCurrentTimeString() {
      Date currentDate = new Date();
      return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(currentDate);
    }

    private long startTime;

    private final StringBuilder contents;
    private String elapsedTimeText;
    private String searchTimeText;
    private final Label timeLabel;

    public ChronoAnimation(Label label, String searchTime, String elapsedTime) {
      // GWT.log("searchTime: " + searchTime);
      // GWT.log("elapsedTime: " + elapsedTime);

      this.timeLabel = label;
      this.searchTimeText = searchTime;
      this.elapsedTimeText = elapsedTime;

      this.contents = new StringBuilder();
      this.contents.append("<font color=\"gray\" family=\"normal\" size=\"2\" weight=\"bold\">");
      this.contents.append(this.searchTimeText);
      this.contents.append(" ");
      this.contents.append(this.elapsedTimeText);
      this.contents.append("</font>");
      this.timeLabel.setContents(this.contents.toString());
    }

    /*
     * private void changeColor(String value) { contents.append("<font color=\""
     * + value + "\" family=\"normal\" size=\"12\" weight=\"bold\">"); }
     */

    @Override
    protected void onCancel() {
      this.updateTimeText();
      this.contents.setLength(0);
      this.contents.append("<font color=\"gray\" family=\"normal\" size=\"2\" weight=\"bold\">");
      this.contents.append(this.searchTimeText);
      this.contents.append(" ");
      this.contents.append(this.elapsedTimeText);
      this.contents.append("</font>");
      this.timeLabel.setContents(this.contents.toString());
      // this.timeLabel.redraw();
    }

    @Override
    protected void onComplete() {
      this.updateTimeText();
      // this.timeLabel.clear();
      this.contents.setLength(0);
      this.contents.append("<font color=\"gray\" family=\"normal\" size=\"2\" weight=\"bold\">");
      this.contents.append(this.searchTimeText);
      this.contents.append(" ");
      this.contents.append(this.elapsedTimeText);
      this.contents.append("</font>");
      this.timeLabel.setContents(this.contents.toString());
      this.timeLabel.redraw();
    }

    @Override
    protected void onStart() {
      // this.timeLabel.clear();
      this.contents.setLength(0);
      this.searchTimeText = ChronoAnimation.getCurrentTimeString().toString();
      this.startTime = new Date().getTime();
      this.contents.append("<font color=\"red\" family=\"normal\" size=\"2\" weight=\"bold\">");
      this.contents.append(this.searchTimeText);
      this.contents.append(" ");
      this.contents.append(this.elapsedTimeText);
      this.contents.append("</font>");
      this.timeLabel.setContents(this.contents.toString());
      this.timeLabel.redraw();

      // this.changeColor("red");
    }

    @Override
    protected void onUpdate(double progress) {
      this.updateTimeText();
    }

    private void updateTimeText() {
      long currentTime = new Date().getTime();
      this.elapsedTimeText = " " + Long.toString(currentTime - this.startTime) + " ms";
      GWT.log("elapsedTime: " + this.elapsedTimeText);
      // this.timeLabel.clear();
      this.contents.setLength(0);
      this.contents.append("<font color=\"red\" family=\"normal\" size=\"2\" weight=\"bold\">");
      this.contents.append(this.searchTimeText);
      this.contents.append(" ");
      this.contents.append(this.elapsedTimeText);
      this.contents.append("</font>");
      this.timeLabel.setContents(this.contents.toString());
      this.timeLabel.redraw();

    }
  }

  ChronoAnimation chronoAnimation;

  public DtcChronoView() {

    String searchTime = ChronoAnimation.getCurrentTimeString().toString();
    GWT.log("searchTime: " + searchTime);
    String elapsedTime = "";
    this.chronoAnimation = new ChronoAnimation(this, searchTime, elapsedTime);
  }

  public void end() {
    this.chronoAnimation.cancel();
  }

  public void start() {
    this.chronoAnimation.run(10000);
  }
}
