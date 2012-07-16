package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DtcServiceListDaoObserver;
import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.client.model.DtcServiceListDao;
import net.skcomms.dtc.client.view.DtcSelectTestPageView;

import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DtcSelectTestPageController implements DtcTestPageViewObserver,
    DtcServiceListDaoObserver {
  DtcSelectTestPageView dtcSelectTestPageView;
  private DtcServiceListDao dtcServiceListDao;

  public void initialize(
      DtcServiceListDao dtcServiceListDao) {
    this.dtcServiceListDao = dtcServiceListDao;

  }

  @Override
  public void onClickSelectTestPageButton() {
    this.dtcServiceListDao.getServiceTree();

  }

  @Override
  public void onGetServiceList() {
    this.dtcSelectTestPageView = new DtcSelectTestPageView();

    this.dtcSelectTestPageView.show();
    List<TreeNode> list = new ArrayList<TreeNode>();

    this.dtcSelectTestPageView.setTreeNodeData(this.dtcServiceListDao.getServiceList());

    this.dtcSelectTestPageView.addCloseClickHandler(new CloseClickHandler() {
      @Override
      public void onCloseClick(CloseClickEvent event) {

        DtcSelectTestPageController.this.dtcSelectTestPageView.destroy();
      }
    });

    // this.dtcSelectTestPageView.draw();

  }

  @Override
  public void onReadyRequestData() {

  }

}
