package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.DtcArdbeg.Pair;
import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcNodeData {

  private final List<DtcNodeInfo> dtcFavoriteNodeList = new ArrayList<DtcNodeInfo>();
  private final List<DtcNodeInfo> dtcNodeList = new ArrayList<DtcNodeInfo>();

  private static DtcNodeData instance = new DtcNodeData();

  private static final CellList<DtcNodeInfo> dtcNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());
  private static final CellList<DtcNodeInfo> dtcFavoriteNodeCellList = new CellList<DtcNodeInfo>(
      DtcNodeInfoCell.getInstance());

  public static DtcNodeData getInstance() {
    return DtcNodeData.instance;
  }

  private static void sortFavoritesByVisitCount(List<Pair<Integer, DtcNodeInfo>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, DtcNodeInfo>>() {
      @Override
      public int compare(Pair<Integer, DtcNodeInfo> arg0, Pair<Integer, DtcNodeInfo> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final List<DtcArdbeg.Pair<Integer, DtcNodeInfo>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeInfo>>();

  DtcArdbeg owner;

  private DtcNodeData() {
  }

  private void categorizeNodesByVisitCount(List<DtcNodeInfo> nodeInfos) {
    this.dtcNodeList.clear();
    this.favoritePairs.clear();

    for (DtcNodeInfo nodeInfo : nodeInfos) {
      Integer score = PersistenceManager.getInstance().getVisitCount(nodeInfo.getName());
      if (score > 0) {
        this.favoritePairs.add(new Pair<Integer, DtcNodeInfo>(score, nodeInfo));
      } else {
        this.dtcNodeList.add(nodeInfo);
      }
    }
  }

  public void changeNodeListOf(DtcNodeInfo selected) {

    if (this.isDtcFavoriteNode(selected)) {
      this.moveToDtcNodeList(selected);
    } else {
      this.moveToDtcFavoriteNodeList(selected);
    }

    this.setDtcNodeCellList();
    this.setDtcFavoriteNodeCellList();
  }

  public CellList<DtcNodeInfo> getDtcFavoriteNodeCellList() {
    return DtcNodeData.dtcFavoriteNodeCellList;
  }

  public CellList<DtcNodeInfo> getDtcNodeCellList() {
    return DtcNodeData.dtcNodeCellList;
  }

  public void goToPageBasedOn(DtcNodeInfo selected) {
    DtcNodeData.this.owner.setDtcFramePath(selected.getPath());
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    this.owner = dtcArdbeg;
  }

  private boolean isDtcFavoriteNode(DtcNodeInfo selected) {
    return this.dtcFavoriteNodeList.contains(selected);
  }

  private void moveToDtcFavoriteNodeList(DtcNodeInfo selected) {
    PersistenceManager.getInstance().addVisitCount(selected.getName());
    this.dtcFavoriteNodeList.add(selected);
    this.dtcNodeList.remove(selected);
  }

  private void moveToDtcNodeList(DtcNodeInfo selected) {
    PersistenceManager.getInstance().removeItem(selected.getName());
    this.dtcFavoriteNodeList.remove(selected);
    this.dtcNodeList.add(selected);
  }

  public void refreshDtcDirectoryPageNode(final String path) {
    DtcService.Util.getInstance().getDir(path, new AsyncCallback<List<DtcNodeInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeInfo> nodeInfos) {
        DtcNodeData.this.setDtcNodeList(nodeInfos);
        DtcNodeData.this.setDtcNodeCellList();

        System.out.println("Callback : " + path);
        if (path.equals("/")) {
        }
        else {
          DtcNodeData.this.owner.fireDtcServiceDirectoryPageLoaded(path);
        }

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
        DtcNodeData.this.categorizeNodesByVisitCount(nodeInfos);
        DtcNodeData.this.sortDtcFavoriteNodeList();

        DtcNodeData.this.setDtcNodeCellList();
        DtcNodeData.this.setDtcFavoriteNodeCellList();
        DtcNodeData.this.owner.fireDtcHomePageLoaded();
      }
    });
  }

  private void setDtcFavoriteNodeCellList() {
    DtcNodeData.dtcFavoriteNodeCellList.setRowData(this.dtcFavoriteNodeList);
    DtcNodeData.dtcFavoriteNodeCellList.setRowCount(this.dtcFavoriteNodeList.size(),
        true);
  }

  private void setDtcNodeCellList() {
    DtcNodeData.dtcNodeCellList.setRowData(this.dtcNodeList);
    DtcNodeData.dtcNodeCellList.setRowCount(this.dtcNodeList.size(), true);
  }

  private void setDtcNodeList(List<DtcNodeInfo> nodeInfos) {
    this.dtcNodeList.clear();
    this.dtcNodeList.addAll(nodeInfos);
  }

  private void setFavoriteListFrom(List<Pair<Integer, DtcNodeInfo>> favoritePairs) {
    this.dtcFavoriteNodeList.clear();
    for (Pair<Integer, DtcNodeInfo> favoritePair : favoritePairs) {
      this.dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }

  private void sortDtcFavoriteNodeList() {
    DtcNodeData.sortFavoritesByVisitCount(this.favoritePairs);
    this.setFavoriteListFrom(this.favoritePairs);
  }
}
