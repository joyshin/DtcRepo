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

public class DtcNodeData {

  private final List<DtcNodeInfo> dtcFavoriteNodeList = new ArrayList<DtcNodeInfo>();
  private final List<DtcNodeInfo> dtcNodeList = new ArrayList<DtcNodeInfo>();

  private static DtcNodeData instance = new DtcNodeData();

  private static final ServiceDao serviceDao = new ServiceDao();

  private static final CellList<DtcNodeInfo> dtcNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());
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

  private final List<DtcArdbeg.Pair<Integer, DtcNodeInfo>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeInfo>>();

  public DtcArdbeg owner;

  private void categorizeNodesByVisitCount(List<DtcNodeInfo> nodeInfos) {
    dtcNodeList.clear();
    favoritePairs.clear();

    for (DtcNodeInfo nodeInfo : nodeInfos) {
      Integer score = serviceDao.getVisitCount(nodeInfo.getName());
      if (score > 0) {
        favoritePairs.add(new Pair<Integer, DtcNodeInfo>(score, nodeInfo));
      } else {
        dtcNodeList.add(nodeInfo);
      }
    }
  }

  public void changeNodeListOf(DtcNodeInfo selected) {

    if (isDtcFavoriteNode(selected) == true) {
      moveToDtcNodeList(selected);
    } else {
      moveToDtcFavoriteNodeList(selected);
    }

    setDtcNodeCellList();
    setDtcFavoriteNodeCellList();
  }

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

  public void goToPageBasedOn(DtcNodeInfo selected) {
    DtcPageType type =
        DtcNodeData.this.getTypeOfSelected(selected.getPath(),
            selected.isLeaf());
    String href = DtcNodeData.this.getHrefWithTypeAndPath(type,
        selected.getPath());
    DtcNodeData.this.owner.setDtcFrameUrl(href);
  }

  public void initialize(DtcArdbeg dtcArdbeg)
  {
    this.owner = dtcArdbeg;
  }

  private boolean isDtcFavoriteNode(DtcNodeInfo selected) {
    return dtcFavoriteNodeList.contains(selected);
  }

  private void moveToDtcFavoriteNodeList(DtcNodeInfo selected) {
    serviceDao.addVisitCount(selected.getName());
    dtcFavoriteNodeList.add(selected);
    dtcNodeList.remove(selected);
  }

  private void moveToDtcNodeList(DtcNodeInfo selected) {
    serviceDao.removeServiceFromCookie(selected.getName());
    dtcFavoriteNodeList.remove(selected);
    dtcNodeList.add(selected);
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
        setDtcNodeList(nodeInfos);
        setDtcNodeCellList();
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
        categorizeNodesByVisitCount(nodeInfos);
        sortDtcFavoriteNodeList();

        setDtcNodeCellList();
        setDtcFavoriteNodeCellList();
      }
    });
  }

  private void setDtcFavoriteNodeCellList() {
    dtcFavoriteNodeCellList.setRowData(dtcFavoriteNodeList);
    dtcFavoriteNodeCellList.setRowCount(dtcFavoriteNodeList.size(),
        true);
  }

  private void setDtcNodeCellList() {
    dtcNodeCellList.setRowData(dtcNodeList);
    dtcNodeCellList.setRowCount(dtcNodeList.size(), true);
  }

  private void setDtcNodeList(List<DtcNodeInfo> nodeInfos) {
    dtcNodeList.clear();
    dtcNodeList.addAll(nodeInfos);
  }

  private void setFavoriteListWith(List<Pair<Integer, DtcNodeInfo>> favoritePairs) {
    for (Pair<Integer, DtcNodeInfo> favoritePair : favoritePairs) {
      dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }

  private void sortDtcFavoriteNodeList() {
    dtcFavoriteNodeList.clear();
    sortFavoritePairsByVisitCount(favoritePairs);
    setFavoriteListWith(favoritePairs);
  }
}