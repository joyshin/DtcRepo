package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DtcNodeData {

  private static List<DtcNodeInfo> dtcNodeList = new ArrayList<DtcNodeInfo>();

  private static DtcNodeData instance = new DtcNodeData();

  private static final CellList<DtcNodeInfo> dtcNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());

  private static final SelectionModel<DtcNodeInfo> SELECTION_MODEL =
      new SingleSelectionModel<DtcNodeInfo>();

  private static final CellList<DtcNodeInfo> dtcFavoriteNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());

  public static DtcNodeData getInstance() {
    return instance;
  }

  DtcArdbeg owner;

  public CellList<DtcNodeInfo> getDtcFavoriteNodeCellList() {
    return dtcFavoriteNodeCellList;
  }

  public CellList<DtcNodeInfo> getDtcNodeCellList() {
    return dtcNodeCellList;
  }

  public void initialize(final DtcArdbeg dtcArdbeg)
  {
    owner = dtcArdbeg;

    SELECTION_MODEL.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        DtcNodeInfo selected = ((SingleSelectionModel<DtcNodeInfo>) SELECTION_MODEL)
            .getSelectedObject();

        dtcArdbeg.setDtcFramePath(selected.getPath());
      }
    });

    List<DtcNodeInfo> testNodeList = new ArrayList<DtcNodeInfo>();
    DtcNodeInfo tmpNode = new DtcNodeInfo();
    tmpNode.setDescription("DTC_PROXY_URL");
    tmpNode.setName("Favorite Test");
    tmpNode.setPath("/");
    tmpNode.setUpdateTime("11");
    testNodeList.add(tmpNode);
    dtcFavoriteNodeCellList.setRowData(testNodeList);

    dtcNodeCellList.setSelectionModel(SELECTION_MODEL);
    dtcFavoriteNodeCellList.setSelectionModel(SELECTION_MODEL);
  }

  public void refreshDtcNode(final String path) {

    DtcService.Util.getInstance().getDir(path, new AsyncCallback<List<DtcNodeInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeInfo> nodeInfos) {
        dtcNodeList.clear();
        dtcNodeList.addAll(nodeInfos);

        dtcNodeCellList.setRowData(dtcNodeList);
        dtcNodeCellList.setRowCount(dtcNodeList.size(), true);

        System.out.println("Callback : " + path);
        if (path.equals("/")) {
          owner.onLoadDtcHomePage(path);
        }
        else {
          owner.onLoadDtcServiceDirectoryPage(path);
        }
      }
    });
  }
}