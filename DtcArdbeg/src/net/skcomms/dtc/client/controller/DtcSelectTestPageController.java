package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DtcServiceListDaoObserver;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.model.DtcServiceListDao;
import net.skcomms.dtc.client.view.DtcSelectTestPageView;

import com.smartgwt.client.widgets.tree.TreeNode;

public class DtcSelectTestPageController implements DtcTestPageViewObserver,
    DtcServiceListDaoObserver {

  private DtcSelectTestPageView dtcSelectTestPageView;
  private DtcServiceListDao dtcServiceListDao;

  public void initialize(DtcSelectTestPageView dtcSelectTestPageView,
      DtcServiceListDao dtcServiceListDao) {
    this.dtcSelectTestPageView = dtcSelectTestPageView;
    this.dtcServiceListDao = dtcServiceListDao;

  }

  @Override
  public void onClickSelectTestPageButton() {
    this.dtcServiceListDao.getServiceTree();

  }

  @Override
  public void onGetServiceList() {

    this.dtcServiceListDao.getServiceList();
    List<TreeNode> list = new ArrayList<TreeNode>();

    this.dtcSelectTestPageView.setTreeNodeData(list);

  }

  @Override
  public void onReadyRequestData() {

  }

}
