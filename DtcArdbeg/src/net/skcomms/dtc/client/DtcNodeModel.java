package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.DtcArdbeg.Pair;
import net.skcomms.dtc.shared.DtcNodeMetaModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcNodeModel {

  private final List<DtcNodeMetaModel> dtcFavoriteNodeList = new ArrayList<DtcNodeMetaModel>();
  private final List<DtcNodeMetaModel> dtcNodeList = new ArrayList<DtcNodeMetaModel>();

  private static DtcNodeModel instance = new DtcNodeModel();

  private static final CellList<DtcNodeMetaModel> dtcNodeCellList = new CellList<DtcNodeMetaModel>(
      DtcNodeMetaCellView.getInstance());
  private static final CellList<DtcNodeMetaModel> dtcFavoriteNodeCellList = new CellList<DtcNodeMetaModel>(
      DtcNodeMetaCellView.getInstance());

  public static DtcNodeModel getInstance() {
    return DtcNodeModel.instance;
  }

  private static void sortFavoritesByVisitCount(List<Pair<Integer, DtcNodeMetaModel>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, DtcNodeMetaModel>>() {
      @Override
      public int compare(Pair<Integer, DtcNodeMetaModel> arg0, Pair<Integer, DtcNodeMetaModel> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final List<DtcArdbeg.Pair<Integer, DtcNodeMetaModel>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeMetaModel>>();

  DtcArdbeg owner;

  private DtcNodeModel() {
  }

  private void categorizeNodesByVisitCount(List<DtcNodeMetaModel> nodeInfos) {
    this.dtcNodeList.clear();
    this.favoritePairs.clear();

    for (DtcNodeMetaModel nodeInfo : nodeInfos) {
      Integer score = PersistenceManager.getInstance().getVisitCount(nodeInfo.getName());
      if (score > 0) {
        this.favoritePairs.add(new Pair<Integer, DtcNodeMetaModel>(score, nodeInfo));
      } else {
        this.dtcNodeList.add(nodeInfo);
      }
    }
  }

  public void changeNodeListOf(DtcNodeMetaModel selected) {

    if (this.isDtcFavoriteNode(selected)) {
      this.moveToDtcNodeList(selected);
    } else {
      this.moveToDtcFavoriteNodeList(selected);
    }

    this.setDtcNodeCellList();
    this.setDtcFavoriteNodeCellList();
  }

  public CellList<DtcNodeMetaModel> getDtcFavoriteNodeCellList() {
    return DtcNodeModel.dtcFavoriteNodeCellList;
  }

  public CellList<DtcNodeMetaModel> getDtcNodeCellList() {
    return DtcNodeModel.dtcNodeCellList;
  }

  public void goToPageBasedOn(DtcNodeMetaModel selected) {
    DtcNodeModel.this.owner.setDtcFramePath(selected.getPath());
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    this.owner = dtcArdbeg;
  }

  private boolean isDtcFavoriteNode(DtcNodeMetaModel selected) {
    return this.dtcFavoriteNodeList.contains(selected);
  }

  private void moveToDtcFavoriteNodeList(DtcNodeMetaModel selected) {
    PersistenceManager.getInstance().addVisitCount(selected.getName());
    this.dtcFavoriteNodeList.add(selected);
    this.dtcNodeList.remove(selected);
  }

  private void moveToDtcNodeList(DtcNodeMetaModel selected) {
    PersistenceManager.getInstance().removeItem(selected.getName());
    this.dtcFavoriteNodeList.remove(selected);
    this.dtcNodeList.add(selected);
  }

  public void refreshDtcDirectoryPageNode(final String path) {
    DtcService.Util.getInstance().getDir(path, new AsyncCallback<List<DtcNodeMetaModel>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMetaModel> nodeInfos) {
        DtcNodeModel.this.setDtcNodeList(nodeInfos);
        DtcNodeModel.this.setDtcNodeCellList();

        System.out.println("Callback : " + path);
        if (path.equals("/")) {
        }
        else {
          DtcNodeModel.this.owner.fireDtcServiceDirectoryPageLoaded(path);
        }

      }
    });
  }

  public void refreshDtcHomePageNode() {

    DtcService.Util.getInstance().getDir("/", new AsyncCallback<List<DtcNodeMetaModel>>() {
      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMetaModel> nodeInfos) {
        DtcNodeModel.this.categorizeNodesByVisitCount(nodeInfos);
        DtcNodeModel.this.sortDtcFavoriteNodeList();

        DtcNodeModel.this.setDtcNodeCellList();
        DtcNodeModel.this.setDtcFavoriteNodeCellList();
        DtcNodeModel.this.owner.fireDtcHomePageLoaded();
      }
    });
  }

  private void setDtcFavoriteNodeCellList() {
    DtcNodeModel.dtcFavoriteNodeCellList.setRowData(this.dtcFavoriteNodeList);
    DtcNodeModel.dtcFavoriteNodeCellList.setRowCount(this.dtcFavoriteNodeList.size(),
        true);
  }

  private void setDtcNodeCellList() {
    DtcNodeModel.dtcNodeCellList.setRowData(this.dtcNodeList);
    DtcNodeModel.dtcNodeCellList.setRowCount(this.dtcNodeList.size(), true);
  }

  private void setDtcNodeList(List<DtcNodeMetaModel> nodeInfos) {
    this.dtcNodeList.clear();
    this.dtcNodeList.addAll(nodeInfos);
  }

  private void setFavoriteListFrom(List<Pair<Integer, DtcNodeMetaModel>> favoritePairs) {
    this.dtcFavoriteNodeList.clear();
    for (Pair<Integer, DtcNodeMetaModel> favoritePair : favoritePairs) {
      this.dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }

  private void sortDtcFavoriteNodeList() {
    DtcNodeModel.sortFavoritesByVisitCount(this.favoritePairs);
    this.setFavoriteListFrom(this.favoritePairs);
  }
}
