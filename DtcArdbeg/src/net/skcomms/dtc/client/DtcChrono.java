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
  private static Date currentDate = new Date();
  
  private long startTime;
  private long endTime;
  private long elapsedTime;
  private String formattedTime;
  
  private Style chronoStyle;
  private DivElement chronoElement;
  Text formattedTimeText;
  Text elapsedTimeText;
  
  public void start() {
    this.startTime = 0;
    this.startTime = currentDate.getTime();
    
    GWT.log("Search Time: " + this.getFormattedTime());
    GWT.log("StartTime: " + Long.toString(this.startTime));
   
  }
  
  public void end() {
    this.endTime = 0;
    this.endTime = currentDate.getTime();
    
    GWT.log("EndTime: " + Long.toString(this.endTime));
    GWT.log("Elapsed: " + Long.toString(this.getElapsedTime()));
    ColorChangeAnimation colorChangeAnimation = new ColorChangeAnimation(this.chronoElement);
    colorChangeAnimation.run(3);
  }
  
  public long getStartTime () {
    return this.startTime;
  }
  
  public long getEndTime () {
    return this.endTime;
  }

  public String getFormattedTime () {
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
   
    formattedTimeText = dtcDoc.createTextNode(this.getFormattedTime());
    elapsedTimeText = dtcDoc.createTextNode(" " + Long.toString(this.elapsedTime) + "ms");
    chronoElement.appendChild(formattedTimeText);
    chronoElement.appendChild(elapsedTimeText);
    parentNode.insertFirst(chronoElement);
    chronoStyle = chronoElement.getStyle();
    chronoStyle.setFontStyle(Style.FontStyle.OBLIQUE);
    chronoStyle.setFontSize(14,Style.Unit.PX);
    chronoStyle.setColor("red");
  }
  
  public class ColorChangeAnimation extends Animation {

    Style elementStyle;
    
    public ColorChangeAnimation (Element element) {
      elementStyle = element.getStyle();
    }
    
    @Override
    protected void onUpdate(double progress) {
      // TODO Auto-generated method stub
      //setOpacity(1 - interpolate(progress));
      GWT.log("Progress: " + Double.toString(progress));
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
