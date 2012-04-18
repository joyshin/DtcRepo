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

  private DtcNodeInfoCell() {
  }

  @Override
  public void render(Context context, DtcNodeInfo dtcNodeInfo, SafeHtmlBuilder sb) {
    if (dtcNodeInfo == null) {
      return;
    }

    Image image = new Image();

    image
        .setUrl("http://t1.gstatic.com/images?q=tbn:ANd9GcSlCSbbm87uDJiPgjXdvfq6msCJf3v9Jb6XYrEn2CtkyYGrFhBJ3Q");

    image.setSize("100%", "100%");
    // sb.appendHtmlConstant("<a href = '" + daemonInfo.getHref() + "'>");

    // 1. div
    sb.appendHtmlConstant("<div style=\"width:362px; height:74px; \">");

    // 2. div
    sb.appendHtmlConstant("<div style=\"width:62px; height:70px; float:left; margin-right:5px; \">");
    sb.append(SafeHtmlUtils.fromTrustedString(image.toString()));
    sb.appendHtmlConstant("</div>");
    // 2

    // 3. div
    sb.appendHtmlConstant("<div style=\"width:300px; height:74px; \">");

    // 4. div
    sb.appendHtmlConstant("<div style=\"height:26px; display:table; vertical-align:middle; \"><b>");
    sb.appendEscaped(dtcNodeInfo.getName());
    sb.appendHtmlConstant("</b></div>");
    // 4

    // 5. div
    sb.appendHtmlConstant("<div style=\"height:22px; display:table; vertical-align:middle; \">");
    sb.appendEscaped(dtcNodeInfo.getDescription());
    sb.appendHtmlConstant("</div>");
    // 5

    // 6. div
    sb.appendHtmlConstant("<div style=\"height:22px; display:table; vertical-align:middle; \">");
    sb.appendEscaped(dtcNodeInfo.getUpdateTime());
    sb.appendHtmlConstant("</div>");
    // 6

    sb.appendHtmlConstant("</div>");
    // 3

    sb.appendHtmlConstant("</div>");
    // 1
  }
}
