package net.skcomms.dtc.client;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

final class DtcNodeInfoCell extends AbstractCell<DtcNodeInfo> {
  private static final DtcNodeInfoCell instance = new DtcNodeInfoCell();
  private static final String CELL_STYLE_OPENER = "<div style=\"position: relative; width:280px; height:74px; border: 1px solid; border-radius: 15px; float: left; \">";
  private static final String CELL_STYLE_CLOSER = "</div>";

  private static final String CELL_IMAGE_URL = "https://encrypted-tbn2.google.com/images?q=tbn:ANd9GcScwAPeLjzjxfXlOmzXBIZZ65qTSKib647JumG-J8f2o1CBOhYD";
  private static final String CELL_IMAGE_STYLE_OPENER = "<div style=\"width:62px; height:69px; float:left; margin:2px;\">";
  private static final String CELL_IMAGE_STYLE_CLOSER = "</div>";

  private static final String CELL_BUTTON_ID = "dtc_favorite_btn";
  private static final String CELL_BUTTON_IMAGE_SRC = "http://files.softicons.com/download/toolbar-icons/vista-arrow-icons-by-icons-land/png/24x24/UpDown1Yellow.png";
  private static final String CELL_BUTTON_STYLE_OPENER = "<div style=\"height:26px; float:right;  \">";
  private static final String CELL_BUTTON_STYLE_BODY = "<img id=\""
      + DtcNodeInfoCell.CELL_BUTTON_ID + "\" src=\""
      + DtcNodeInfoCell.CELL_BUTTON_IMAGE_SRC + "\" ></img>";

  private static final String CELL_BUTTON_STYLE_CLOSER = "</div>";

  private static final String CELL_BOX_STYLE_OPENER = "<div style=\"width:280px; height:74px; \">";
  private static final String CELL_BOX_STYLE_CLOSER = "</div>";

  private static final String CELL_NAME_STYLE_OPENER = "<div style=\"height:26px; display:table; vertical-align:middle; \"><b>";
  private static final String CELL_NAME_STYLE_CLOSER = "</b></div>";

  private static final String CELL_DESCRIPTION_STYLE_OPENER = "<div style=\"height:22px; display:table; vertical-align:middle; \">";
  private static final String CELL_DESCRIPTION_STYLE_CLOSER = "</div>";

  private static final String CELL_UPDATETIME_STYLE_OPENER = "<div style=\"height:22px; display:table; vertical-align:middle; \">";
  private static final String CELL_UPDATETIME_STYLE_CLOSER = "</div>";

  public static DtcNodeInfoCell getInstance() {
    return DtcNodeInfoCell.instance;
  }

  private final DtcNodeData dtcNodeData = DtcNodeData.getInstance();

  private DtcNodeInfoCell() {
    super("click");
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, DtcNodeInfo
      selected, NativeEvent event,
      ValueUpdater<DtcNodeInfo> valueUpdater) {

    if (DtcNodeInfoCell.CELL_BUTTON_ID.equals(Element.as(event.getEventTarget()).getId())) {
      if (!selected.isDirectoryPageNode()) {
        this.dtcNodeData.changeNodeListOf(selected);
      } else {
        Window.alert("누르지마!!");
      }
    } else {
      this.dtcNodeData.goToPageBasedOn(selected);
    }
    event.preventDefault();
    event.stopPropagation();

    super.onBrowserEvent(context, parent, selected, event, valueUpdater);
  }

  @Override
  public void render(Context context, DtcNodeInfo dtcNodeInfo, SafeHtmlBuilder sb) {
    if (dtcNodeInfo == null) {
      return;
    }

    Image image = new Image();
    image.setUrl(DtcNodeInfoCell.CELL_IMAGE_URL);
    image.setSize("100%", "100%");
    image.setStyleName("gwt-DtcNodeCellImageStyle");

    // 1. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_STYLE_OPENER);

    // 2. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_IMAGE_STYLE_OPENER);
    sb.append(SafeHtmlUtils.fromTrustedString(image.toString()));
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_IMAGE_STYLE_CLOSER);
    // 2

    // 3
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_BUTTON_STYLE_OPENER);
    sb.append(SafeHtmlUtils.fromTrustedString(DtcNodeInfoCell.CELL_BUTTON_STYLE_BODY));
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_BUTTON_STYLE_CLOSER);
    // 3

    // 4. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_BOX_STYLE_OPENER);

    // 5. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_NAME_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getName());
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_NAME_STYLE_CLOSER);
    // 5

    // 6. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_DESCRIPTION_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getDescription());
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_DESCRIPTION_STYLE_CLOSER);
    // 6

    // 7. div
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_UPDATETIME_STYLE_OPENER);
    sb.appendEscaped(dtcNodeInfo.getUpdateTime());
    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_UPDATETIME_STYLE_CLOSER);
    // 7

    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_BOX_STYLE_CLOSER);
    // 4

    sb.appendHtmlConstant(DtcNodeInfoCell.CELL_STYLE_CLOSER);
    // 1
  }
}
