package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

final class DtcNodeInfoCell extends AbstractCell<DtcNodeInfo> {
  private static final DtcNodeInfoCell instance = new DtcNodeInfoCell();
  
  public static DtcNodeInfoCell getInstance() {
    return instance;
  }
  
  private DtcNodeInfoCell() {}
  
  @Override
  public void render(Context context, DtcNodeInfo daemonInfo, SafeHtmlBuilder sb) {
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
          sb.appendEscaped(daemonInfo.getUpdateTime());
        sb.appendHtmlConstant("</div>");
        sb.appendHtmlConstant("</div>");
//      sb.appendHtmlConstant("</a>");
    sb.appendHtmlConstant("</div>");
  }
}
