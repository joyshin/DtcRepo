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
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcNodeModel {

  private final List<DtcNodeMetaModel> dtcFavoriteNodeList = new ArrayList<DtcNodeMetaModel>();

  private final List<DtcNodeMetaModel> dtcNodeList = new ArrayList<DtcNodeMetaModel>();

  private static DtcNodeModel instance = new DtcNodeModel();

  // FIXME 객체의 유일성을 보장하는 장점보다, 접근남용의 단점이 커진 듯...
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
    this.observers.add(observer);
  }

  private void categorizeNodesByVisitCount(List<DtcNodeMetaModel> nodeInfos) {
    this.dtcNodeList.clear();
    this.favoritePairs.clear();

    for (DtcNodeMetaModel nodeInfo : nodeInfos) {
      System.out.println("name:" + nodeInfo.getName() + ", path:" + nodeInfo.getPath());
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

    this.fireNodeListChanged();
    this.fireFavoriteNodeListChanged();
  }

  private void fireFavoriteNodeListChanged() {
    for (DtcNodeObserver observer : this.observers) {
      observer.onFavoriteNodeListChanged();
    }
  }

  private void fireNodeListChanged() {
    for (DtcNodeObserver observer : this.observers) {
      observer.onNodeListChanged();
    }
  }

  public List<DtcNodeMetaModel> getFavoriteNodeList() {
    return this.dtcFavoriteNodeList;
  }

  public List<DtcNodeMetaModel> getNodeList() {
    return this.dtcNodeList;
  }

  // FIXME 모델에서는 직접 메써드를 호출하는 대신 선택된 노드 혹은 경로를 통지하자.
  public void goToPageBasedOn(DtcNodeMetaModel selected) {
    DtcNodeModel.this.owner.setPath(selected.getPath());
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    // FIXME DtcArdbeg이 필요없도록 만들자.
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
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMetaModel> nodeInfos) {
        DtcNodeModel.this.setDtcNodeList(nodeInfos);
        DtcNodeModel.this.fireNodeListChanged();

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
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMetaModel> nodeInfos) {
        DtcNodeModel.this.categorizeNodesByVisitCount(nodeInfos);
        DtcNodeModel.this.sortDtcFavoriteNodeList();

        DtcNodeModel.this.fireNodeListChanged();
        DtcNodeModel.this.fireFavoriteNodeListChanged();
        DtcNodeModel.this.owner.fireDtcHomePageLoaded();
      }
    });
  }

  public void refreshDtcTestPage(final String path) {
    DtcService.Util.getInstance().getDtcRequestPageInfo(path,
        new AsyncCallback<DtcRequestInfoModel>() {

          @Override
          public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage());
          }

          @Override
          public void onSuccess(DtcRequestInfoModel requestInfo) {
            for (DtcNodeObserver observer : DtcNodeModel.this.observers) {
              observer.onDtcTestPageLoaded(requestInfo);
            }
          }
        });
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
