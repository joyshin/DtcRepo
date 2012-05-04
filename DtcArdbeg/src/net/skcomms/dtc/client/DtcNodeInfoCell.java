package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

final class DtcNodeInfoCell extends AbstractCell<DtcNodeInfo> {
  private static final DtcNodeInfoCell instance = new DtcNodeInfoCell();
  private static final String CELL_STYLE_OPENER = "<div style=\"position: relative; width:280px; height:74px; border: 1px solid; border-radius: 15px; float: left; \">";
  private static final String CELL_STYLE_CLOSER = "</div>";

  private static final String CELL_IMAGE_STYLE_OPENER = "<div style=\"width:62px; height:69px; float:left; margin:2px;\">";
  private static final String CELL_IMAGE_STYLE_CLOSER = "</div>";

  private static final String CELL_BOX_STYLE_OPENER = "<div style=\"width:280px; height:74px; \">";
  private static final String CELL_BOX_STYLE_CLOSER = "</div>";

  private static final String CELL_NAME_STYLE_OPENER = "<div style=\"height:26px; display:table; vertical-align:middle; \"><b>";
  private static final String CELL_NAME_STYLE_CLOSER = "</b></div>";

  private static final String CELL_DESCRIPTION_STYLE_OPENER = "<div style=\"height:22px; display:table; vertical-align:middle; \">";
  private static final String CELL_DESCRIPTION_STYLE_CLOSER = "</div>";

  private static final String CELL_UPDATETIME_STYLE_OPENER = "<div style=\"height:22px; display:table; vertical-align:middle; \">";
  private static final String CELL_UPDATETIME_STYLE_CLOSER = "</div>";

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
        .setUrl("https://encrypted-tbn2.google.com/images?q=tbn:ANd9GcScwAPeLjzjxfXlOmzXBIZZ65qTSKib647JumG-J8f2o1CBOhYD");
    image.setSize("100%", "100%");
    image.setStyleName("gwt-DtcNodeCellImageStyle");

    // Button addLinkButton = new Button("Click ");
    //
    // addLinkButton.addClickHandler(new ClickHandler() {
    //
    // @Override
    // public void onClick(ClickEvent event) {
    //
    // GWT.log("You clicked ");
    //
    // }
    //
    // });
    // 1. div
    sb.appendHtmlConstant(CELL_STYLE_OPENER);

    // 2. div
    sb.appendHtmlConstant(CELL_IMAGE_STYLE_OPENER);
    sb.append(SafeHtmlUtils.fromTrustedString(image.toString()));
    sb.appendHtmlConstant(CELL_IMAGE_STYLE_CLOSER);
    // 2

    // // 2. div
    // sb.appendHtmlConstant(CELL_IMAGE_STYLE_OPENER);
    // sb.append(SafeHtmlUtils.fromTrustedString(addLinkButton.toString()));
    // sb.appendHtmlConstant(CELL_IMAGE_STYLE_CLOSER);
    // // 2

    // 3. div
    sb.appendHtmlConstant(CELL_BOX_STYLE_OPENER);

    // 4. div
    sb.appendHtmlConstant(CELL_NAME_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getName());
    sb.appendHtmlConstant(CELL_NAME_STYLE_CLOSER);
    // 4

    // 5. div
    sb.appendHtmlConstant(CELL_DESCRIPTION_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getDescription());
    sb.appendHtmlConstant(CELL_DESCRIPTION_STYLE_CLOSER);
    // 5

    // 6. div
    sb.appendHtmlConstant(CELL_UPDATETIME_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getUpdateTime());
    sb.appendHtmlConstant(CELL_UPDATETIME_STYLE_CLOSER);
    // 6

    sb.appendHtmlConstant(CELL_BOX_STYLE_CLOSER);
    // 3

    sb.appendHtmlConstant(CELL_STYLE_CLOSER);
    // 1
  }
}
