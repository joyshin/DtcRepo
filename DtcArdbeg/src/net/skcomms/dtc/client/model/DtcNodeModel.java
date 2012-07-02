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
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequestMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DtcNodeModel {

  private final List<DtcNodeMeta> dtcFavoriteNodeList = new ArrayList<DtcNodeMeta>();

  private final List<DtcNodeMeta> dtcNodeList = new ArrayList<DtcNodeMeta>();

  private static DtcNodeModel instance = new DtcNodeModel();

  // FIXME 객체의 유일성을 보장하는 장점보다, 접근남용의 단점이 커진 듯...
  public static DtcNodeModel getInstance() {
    return DtcNodeModel.instance;
  }

  private static void sortFavoritesByVisitCount(List<Pair<Integer, DtcNodeMeta>> rows) {
    Collections.sort(rows, new Comparator<Pair<Integer, DtcNodeMeta>>() {

      @Override
      public int
          compare(Pair<Integer, DtcNodeMeta> arg0, Pair<Integer, DtcNodeMeta> arg1) {
        return -arg0.getKey().compareTo(arg1.getKey());
      }
    });
  }

  private final List<DtcArdbeg.Pair<Integer, DtcNodeMeta>> favoritePairs = new ArrayList<DtcArdbeg.Pair<Integer, DtcNodeMeta>>();

  DtcArdbeg owner;

  private final List<DtcNodeObserver> observers = new ArrayList<DtcNodeObserver>();

  private DtcNodeModel() {
  }

  public void addObserver(DtcNodeObserver observer) {
    this.observers.add(observer);
  }

  private void categorizeNodesByVisitCount(List<DtcNodeMeta> nodeInfos) {
    this.dtcNodeList.clear();
    this.favoritePairs.clear();

    for (DtcNodeMeta nodeInfo : nodeInfos) {
      System.out.println("name:" + nodeInfo.getName() + ", path:" + nodeInfo.getPath());
      Integer score = PersistenceManager.getInstance().getVisitCount(nodeInfo.getName());
      if (score > 0) {
        this.favoritePairs.add(new Pair<Integer, DtcNodeMeta>(score, nodeInfo));
      } else {
        this.dtcNodeList.add(nodeInfo);
      }
    }
  }

  public void changeNodeListOf(DtcNodeMeta selected) {

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

  public List<DtcNodeMeta> getFavoriteNodeList() {
    return this.dtcFavoriteNodeList;
  }

  public List<DtcNodeMeta> getNodeList() {
    return this.dtcNodeList;
  }

  // FIXME 모델에서는 직접 메써드를 호출하는 대신 선택된 노드 혹은 경로를 통지하자.
  public void goToPageBasedOn(DtcNodeMeta selected) {
    DtcNodeModel.this.owner.setPath(selected.getPath());
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    // FIXME DtcArdbeg이 필요없도록 만들자.
    this.owner = dtcArdbeg;
  }

  private boolean isDtcFavoriteNode(DtcNodeMeta selected) {
    return this.dtcFavoriteNodeList.contains(selected);
  }

  private void moveToDtcFavoriteNodeList(DtcNodeMeta selected) {
    PersistenceManager.getInstance().addVisitCount(selected.getName());
    this.dtcFavoriteNodeList.add(selected);
    this.dtcNodeList.remove(selected);
  }

  private void moveToDtcNodeList(DtcNodeMeta selected) {
    PersistenceManager.getInstance().removeItem(selected.getName());
    this.dtcFavoriteNodeList.remove(selected);
    this.dtcNodeList.add(selected);
  }

  private void fireTestPageLoaded(DtcRequestMeta requestInfo) {
    for (DtcNodeObserver observer : DtcNodeModel.this.observers) {
      observer.onDtcTestPageLoaded(requestInfo);
    }
  }

  public void refreshDtcDirectoryPageNode(final String path) {
    DtcService.Util.getInstance().getDir(path, new AsyncCallback<List<DtcNodeMeta>>() {

      @Override
      public void onFailure(Throwable caught) {
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMeta> nodeInfos) {
        DtcNodeModel.this.setDtcNodeList(nodeInfos);
        DtcNodeModel.this.fireNodeListChanged();
        DtcNodeModel.this.owner.fireDtcServiceDirectoryPageLoaded(path);
      }
    });
  }

  public void refreshDtcHomePageNode() {
    DtcService.Util.getInstance().getDir("/", new AsyncCallback<List<DtcNodeMeta>>() {

      @Override
      public void onFailure(Throwable caught) {
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMeta> nodeInfos) {
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
        new AsyncCallback<DtcRequestMeta>() {

          @Override
          public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage());
          }

          @Override
          public void onSuccess(DtcRequestMeta requestInfo) {
            DtcNodeModel.this.fireTestPageLoaded(requestInfo);
          }
        });
  }

  private void setDtcNodeList(List<DtcNodeMeta> nodeInfos) {
    this.dtcNodeList.clear();
    this.dtcNodeList.addAll(nodeInfos);
  }

  private void setFavoriteListFrom(List<Pair<Integer, DtcNodeMeta>> favoritePairs) {
    this.dtcFavoriteNodeList.clear();
    for (Pair<Integer, DtcNodeMeta> favoritePair : favoritePairs) {
      this.dtcFavoriteNodeList.add(favoritePair.getValue());
    }
  }

  private void sortDtcFavoriteNodeList() {
    DtcNodeModel.sortFavoritesByVisitCount(this.favoritePairs);
    this.setFavoriteListFrom(this.favoritePairs);
  }
}
