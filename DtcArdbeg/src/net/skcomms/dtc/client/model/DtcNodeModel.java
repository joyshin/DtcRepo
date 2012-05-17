package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.DtcArdbeg.Pair;
import net.skcomms.dtc.client.DtcNodeObserver;
import net.skcomms.dtc.client.PersistenceManager;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.shared.DtcNodeMetaModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcNodeModel {

  private final List<DtcNodeMetaModel> dtcFavoriteNodeList = new ArrayList<DtcNodeMetaModel>();
  private final List<DtcNodeMetaModel> dtcNodeList = new ArrayList<DtcNodeMetaModel>();

  private static DtcNodeModel instance = new DtcNodeModel();

  public static DtcNodeModel getInstance() {
    return DtcNodeModel.instance;
  }

  private static void sortFavoritesByVisitCount(List<Pair<Integer, DtcNodeMetaModel>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, DtcNodeMetaModel>>() {
      @Override
      public int
          compare(Pair<Integer, DtcNodeMetaModel> arg0, Pair<Integer, DtcNodeMetaModel> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final List<DtcArdbeg.Pair<Integer, DtcNodeMetaModel>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeMetaModel>>();

  DtcArdbeg owner;
  private final List<DtcNodeObserver> observers = new ArrayList<DtcNodeObserver>();

  private DtcNodeModel() {
  }

  public void addObserver(DtcNodeObserver observer) {
    observers.add(observer);
  }

  private void categorizeNodesByVisitCount(List<DtcNodeMetaModel> nodeInfos) {
    dtcNodeList.clear();
    favoritePairs.clear();

    for (DtcNodeMetaModel nodeInfo : nodeInfos) {
      Integer score = PersistenceManager.getInstance().getVisitCount(nodeInfo.getName());
      if (score > 0) {
        favoritePairs.add(new Pair<Integer, DtcNodeMetaModel>(score, nodeInfo));
      } else {
        dtcNodeList.add(nodeInfo);
      }
    }
  }

  public void changeNodeListOf(DtcNodeMetaModel selected) {

    if (isDtcFavoriteNode(selected)) {
      moveToDtcNodeList(selected);
    } else {
      moveToDtcFavoriteNodeList(selected);
    }

    fireNodeListChanged();
    fireFavoriteNodeListChanged();
  }

  private void fireFavoriteNodeListChanged() {
    for (DtcNodeObserver observer : observers) {
      observer.onFavoriteNodeListChanged();
    }
  }

  private void fireNodeListChanged() {
    for (DtcNodeObserver observer : observers) {
      observer.onNodeListChanged();
    }
  }

  public List<DtcNodeMetaModel> getFavoriteNodeList() {
    return dtcFavoriteNodeList;
  }

  public List<DtcNodeMetaModel> getNodeList() {
    return dtcNodeList;
  }

  public void goToPageBasedOn(DtcNodeMetaModel selected) {
    DtcNodeModel.this.owner.setDtcFramePath(selected.getPath());
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    owner = dtcArdbeg;
  }

  private boolean isDtcFavoriteNode(DtcNodeMetaModel selected) {
    return dtcFavoriteNodeList.contains(selected);
  }

  private void moveToDtcFavoriteNodeList(DtcNodeMetaModel selected) {
    PersistenceManager.getInstance().addVisitCount(selected.getName());
    dtcFavoriteNodeList.add(selected);
    dtcNodeList.remove(selected);
  }

  private void moveToDtcNodeList(DtcNodeMetaModel selected) {
    PersistenceManager.getInstance().removeItem(selected.getName());
    dtcFavoriteNodeList.remove(selected);
    dtcNodeList.add(selected);
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
        DtcNodeModel.this.fireNodeListChanged();

        System.out.println("Callback : " + path);
        if (path.equals("/")) {
        }
        else {
          owner.fireDtcServiceDirectoryPageLoaded(path);
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

        DtcNodeModel.this.fireNodeListChanged();
        DtcNodeModel.this.fireFavoriteNodeListChanged();
        owner.fireDtcHomePageLoaded();
      }
    });
  }

  private void setDtcNodeList(List<DtcNodeMetaModel> nodeInfos) {
    dtcNodeList.clear();
    dtcNodeList.addAll(nodeInfos);
  }

  private void setFavoriteListFrom(List<Pair<Integer, DtcNodeMetaModel>> favoritePairs) {
    dtcFavoriteNodeList.clear();
    for (Pair<Integer, DtcNodeMetaModel> favoritePair : favoritePairs) {
      dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }

  private void sortDtcFavoriteNodeList() {
    DtcNodeModel.sortFavoritesByVisitCount(favoritePairs);
    setFavoriteListFrom(favoritePairs);
  }
}
