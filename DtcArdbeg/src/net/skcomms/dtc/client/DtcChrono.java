package net.skcomms.dtc.client;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Text;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DtcChrono {
  
  private long startTime;
  private long endTime;
  private long elapsedTime;
  private String formattedTime;
  
  private Style chronoStyle;
  private DivElement chronoElement;
  Text formattedTimeText;
  Text elapsedTimeText;
  
  ChronoAnimation chronoAnimation;
  
  public void start() {
    Date currentDate = new Date();
    this.startTime = 0;
    this.startTime = currentDate.getTime();
    
    GWT.log("Search Time: " + this.getFormattedTime());
    GWT.log("StartTime: " + Long.toString(this.startTime));
    formattedTimeText.setData(this.getFormattedTime());
    chronoAnimation = new ChronoAnimation(this.chronoElement, this.elapsedTimeText);
    chronoAnimation.run(10000);
  }
  
  public void end() {
    
    chronoAnimation.cancel();
    Date currentDate = new Date();
    this.endTime = 0;
    this.endTime = currentDate.getTime();
    this.elapsedTime = 0;
    GWT.log("EndTime: " + Long.toString(this.endTime));
    GWT.log("Elapsed: " + Long.toString(this.getElapsedTime()));
  }
  
  public long getStartTime () {
    return this.startTime;
  }
  
  public long getEndTime () {
    return this.endTime;
  }

  public String getFormattedTime () {
    Date currentDate = new Date();
    this.formattedTime = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(currentDate);
    return this.formattedTime;
  }
  
  public long getElapsedTime() {
    this.elapsedTime = this.endTime - this.startTime;
    return this.elapsedTime;
  }
 
  public void createChrono(Document dtcDoc, Node parentNode) {
    
    chronoElement = dtcDoc.createDivElement();
    chronoElement.setId("responseTimeContainer");
   
    this.elapsedTime = 0;
    
    formattedTimeText = dtcDoc.createTextNode(this.getFormattedTime());
    elapsedTimeText = dtcDoc.createTextNode(" " + Long.toString(this.elapsedTime) + "ms");
    chronoElement.appendChild(dtcDoc.createBRElement());
    chronoElement.appendChild(formattedTimeText);
    chronoElement.appendChild(elapsedTimeText);
    chronoElement.appendChild(dtcDoc.createBRElement());
    parentNode.insertFirst(chronoElement);
    chronoStyle = chronoElement.getStyle();
    chronoStyle.setFontStyle(Style.FontStyle.NORMAL);
    chronoStyle.setFontSize(12, Style.Unit.PX);
    chronoStyle.setFontWeight(Style.FontWeight.BOLD);
    chronoStyle.setColor("red");
  }
  
  public class ChronoAnimation extends Animation {

    Element targetElement;
    Style elementStyle;
    Text targetText;
    
    private Long startTime;
    private Long currentTime;
    private Long endTime;
    
    public ChronoAnimation (Element element, Text text) {
      targetElement = element;
      targetText = text;
      
      Date currentDate = new Date();
      elementStyle = targetElement.getStyle();
      startTime = currentDate.getTime();
      currentTime = 0l;
      endTime = 0l;
      changeColor("red");

    }
    
    @Override
    protected double interpolate(double progress) {
      Date currentDate = new Date();
      currentTime = currentDate.getTime();
      //GWT.log("Progress: " + Double.toString(currentTime - startTime));
      return currentTime - startTime; 
    }
    
    @Override
    protected void onUpdate(double progress) {      
      // TODO Auto-generated method stub
      GWT.log("Progress: " + Double.toString(progress));
      String timeText = " " + Double.toString(progress) + " ms";
      targetText.setData(timeText);
    }
    
    @Override
    protected void onCancel() {
      Date currentDate = new Date();
      endTime = currentDate.getTime();

      String timeText = " " + Double.toString(endTime - startTime) + " ms";
      targetText.setData(timeText);
      startTime = 0l;
      currentTime = 0l;
      changeColor("grey");
    }
    
    @Override
    protected void onComplete() {
      Date currentDate = new Date();
      endTime = currentDate.getTime();

      String timeText = " " + Double.toString(endTime - startTime) + " ms";
      targetText.setData(timeText);
      startTime = 0l;
      currentTime = 0l;
      changeColor("grey");
    }
    
    private void setOpacity(double opacity) {
      elementStyle.setProperty("opacity", Double.toString(opacity));
      elementStyle.setProperty("filter", "alpha(opacity=" + 100 * opacity + ")");
    }
    
    private void changeColor(String value) {
      elementStyle.setColor(value);
    }
  }
}
