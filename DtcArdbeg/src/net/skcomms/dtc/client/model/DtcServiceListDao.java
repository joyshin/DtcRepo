package net.skcomms.dtc.client.model;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DtcServiceListDaoObserver;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.shared.DtcNodeMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DtcServiceListDao {
  // Map<Integer, DtcNodeMeta> serviceListMap = new HashMap();

  // TreeNode[] serviceNodes = new TreeNode;
  List<DtcServiceTreeNode> nodes1 = new ArrayList<DtcServiceTreeNode>();
  List<TreeNode> nodes = new ArrayList<TreeNode>();

  private final List<DtcServiceListDaoObserver> dtcServiceListDaoObservers = new ArrayList<DtcServiceListDaoObserver>();

  public DtcServiceListDao() {

  }

  public void addObserver(DtcServiceListDaoObserver observer) {
    this.dtcServiceListDaoObservers.add(observer);
  }

  public List<TreeNode> getServiceList() {
    TreeNode[] serviceNodes = new TreeNode[this.nodes.size()];
    this.nodes.toArray(serviceNodes);

    System.out.println(serviceNodes[0].getAttribute("Name"));
    Window.alert(serviceNodes[0].getAttribute("Name"));
    Window.alert(serviceNodes[1].getAttribute("Name"));

    // return serviceNodes;
    return this.nodes;
  }

  public void getServiceTree() {
    DtcService.Util.getInstance().getDir("/", new AsyncCallback<List<DtcNodeMeta>>() {
      @Override
      public void onFailure(Throwable caught) {
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(List<DtcNodeMeta> nodeInfos) {

        int serviceId = 1;
        int parentServiceId = 1;
        DtcServiceTreeNode root = new DtcServiceTreeNode(1, 0, "Name", "", "", "");
        DtcServiceListDao.this.nodes.add(root);

        for (DtcNodeMeta nodeMeta : nodeInfos) {
          serviceId++;
          DtcServiceTreeNode node = new DtcServiceTreeNode(serviceId, parentServiceId, nodeMeta
              .getName(),
              nodeMeta.getPath(), nodeMeta.getDescription(), nodeMeta.getUpdateTime());

          DtcServiceListDao.this.nodes.add(node);

          GWT.log(nodeMeta.getName());
        }

        for (DtcServiceListDaoObserver observer : DtcServiceListDao.this.dtcServiceListDaoObservers) {
          observer.onGetServiceList();
        }
        // DtcNodeModel.this.onHomeNodesReceived(nodeInfos);
      }
    });
  }

}
