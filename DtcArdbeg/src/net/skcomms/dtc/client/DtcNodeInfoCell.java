package net.skcomms.dtc.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

import net.skcomms.dtc.client.DaemonInfo;

final class DaemonInfoCell extends AbstractCell<DaemonInfo> {
  private static final DaemonInfoCell instance = new DaemonInfoCell();
  
  public static DaemonInfoCell getInstance() {
    return instance;
  }
  
  private DaemonInfoCell() {}
  
  @Override
  public void render(Context context, DaemonInfo daemonInfo, SafeHtmlBuilder sb) {
    if(daemonInfo == null) {
      return;
    }
    
    Image image = new Image();
    
    image.setUrl("http://t1.gstatic.com/images?q=tbn:ANd9GcSlCSbbm87uDJiPgjXdvfq6msCJf3v9Jb6XYrEn2CtkyYGrFhBJ3Q");
    
    image.setSize("100%", "100%");
    sb.appendHtmlConstant("<div>");
//      sb.appendHtmlConstant("<a href = '" + daemonInfo.getHref() + "'>");
        sb.appendHtmlConstant("<div style=\"width:62px; height:70px; float:left; margin-right:5px; \">");
          sb.append(SafeHtmlUtils.fromTrustedString(image.toString()));
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div>");
        sb.appendHtmlConstant("<div style=\"height:26px; display:table; vertical-align:middle; \"><b>");
          sb.appendEscaped(daemonInfo.getName());
        sb.appendHtmlConstant("</b></div>");
        sb.appendHtmlConstant("<div style=\"height:22px; display:table; vertical-align:middle; \">");
          sb.appendEscaped(daemonInfo.getDescription());
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("<div style=\"height:22px; display:table; vertical-align:middle; \">");
          sb.appendEscaped(daemonInfo.getLastModifiedDate());
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("</div>");
//      sb.appendHtmlConstant("</a>");
    sb.appendHtmlConstant("</div>");
  }
}
