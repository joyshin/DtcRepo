package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.DtcArdbeg.DtcPageType;
import net.skcomms.dtc.client.DtcArdbeg.Pair;
import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DtcNodeData {

  private static List<DtcNodeInfo> dtcFavoriteNodeList = new ArrayList<DtcNodeInfo>();
  private static List<DtcNodeInfo> dtcNodeList = new ArrayList<DtcNodeInfo>();

  private static final List<DtcArdbeg.Pair<Integer, DtcNodeInfo>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeInfo>>();

  private static DtcNodeData instance = new DtcNodeData();

  private static final ServiceDao serviceDao = new ServiceDao();
  private static final CellList<DtcNodeInfo> dtcNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());

  private static final SelectionModel<DtcNodeInfo> SELECTION_MODEL =
      new SingleSelectionModel<DtcNodeInfo>();

  private static final CellList<DtcNodeInfo> dtcFavoriteNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());

  public static DtcNodeData getInstance() {
    return instance;
  }

  private static void sortFavoritePairsByVisitCount(List<Pair<Integer, DtcNodeInfo>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, DtcNodeInfo>>() {
      @Override
      public int compare(Pair<Integer, DtcNodeInfo> arg0, Pair<Integer, DtcNodeInfo> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  public DtcArdbeg owner;

  public CellList<DtcNodeInfo> getDtcFavoriteNodeCellList() {
    return dtcFavoriteNodeCellList;
  }

  public CellList<DtcNodeInfo> getDtcNodeCellList() {
    return dtcNodeCellList;
  }

  private String getHrefWithTypeAndPath(DtcPageType type, String path) {

    String href = DtcArdbeg.getDtcProxyUrl();

    switch (type) {
    case HOME:
      return href;
    case DIRECTORY:
      return href + "?b=" + path.substring(1);
    case TEST:
      return href + "?c=" + path.substring(1);
    }

    return null;
  }

  /**
   * ���õ� �������� DtcPageType�� �����´�.
   * 
   * @param path
   *          �̵��� ������ ���
   * 
   * @param isLeaf
   *          True: Test, False: ������
   * 
   * @return DtcPageType
   */
  private DtcPageType getTypeOfSelected(String path, boolean isLeaf) {
    if (isLeaf == true) {
      return DtcPageType.TEST;
    } else {
      if (path.equals("/")) {
        return DtcPageType.HOME;
      } else {
        return DtcPageType.DIRECTORY;
      }
    }
  }

  public void initialize(DtcArdbeg dtcArdbeg)
  {
    this.owner = dtcArdbeg;

    SELECTION_MODEL.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        DtcNodeInfo selected = ((SingleSelectionModel<DtcNodeInfo>) SELECTION_MODEL)
            .getSelectedObject();

        DtcPageType type = DtcNodeData.this.getTypeOfSelected(selected.getPath(),
            selected.isLeaf());
        String href = DtcNodeData.this.getHrefWithTypeAndPath(type, selected.getPath());
        DtcNodeData.this.owner.setDtcFrameUrl(href);
      }
    });

    // List<DtcNodeInfo> testNodeList = new ArrayList<DtcNodeInfo>();
    // for (int i = 0; i < 5; i++) {
    // DtcNodeInfo tmpNode = new DtcNodeInfo();
    // tmpNode.setDescription("DTC_PROXY_URL");
    // tmpNode.setName("Favorite Test");
    // tmpNode.setPath("/");
    // tmpNode.setUpdateTime("11");
    // testNodeList.add(tmpNode);
    // }
    // dtcFavoriteNodeCellList.setRowData(testNodeList);
    // dtcFavoriteNodeCellList.setRowCount(testNodeList.size(), true);

    dtcNodeCellList.setSelectionModel(SELECTION_MODEL);
    dtcFavoriteNodeCellList.setSelectionModel(SELECTION_MODEL);
  }

  public void refreshDtcDirectoryPageNode(String path) {
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
      }
    });
  }

  public void refreshDtcHomePageNode() {

    DtcService.Util.getInstance().getDir("/", new AsyncCallback<List<DtcNodeInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeInfo> nodeInfos) {
        dtcNodeList.clear();
        favoritePairs.clear();
        dtcFavoriteNodeList.clear();

        for (DtcNodeInfo nodeInfo : nodeInfos) {
          Integer score = serviceDao.getVisitCount(nodeInfo.getName());
          if (score > 0) {
            favoritePairs.add(new Pair<Integer, DtcNodeInfo>(score, nodeInfo));
          } else {
            dtcNodeList.add(nodeInfo);
          }
        }

        sortFavoritePairsByVisitCount(favoritePairs);
        setFavoriteListWithPairs(favoritePairs);

        dtcNodeCellList.setRowData(dtcNodeList);
        dtcNodeCellList.setRowCount(dtcNodeList.size(), true);

        dtcFavoriteNodeCellList.setRowData(dtcFavoriteNodeList);
        dtcFavoriteNodeCellList.setRowCount(dtcFavoriteNodeList.size(),
            true);
      }
    });
  }

  private void setFavoriteListWithPairs(List<Pair<Integer, DtcNodeInfo>> favoritePairs) {
    for (Pair<Integer, DtcNodeInfo> favoritePair : favoritePairs) {
      dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }
}