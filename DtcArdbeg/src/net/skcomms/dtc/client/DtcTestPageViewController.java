package net.skcomms.dtc.client;

import java.util.List;

import net.skcomms.dtc.shared.DtcRequestInfo;
import net.skcomms.dtc.shared.DtcRequestParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridEditorContext;
import com.smartgwt.client.widgets.grid.ListGridEditorCustomizer;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class DtcTestPageViewController extends DefaultDtcArdbegObserver {

  class RequestFormRecord extends ListGridRecord {
    public RequestFormRecord() {
    }

    public RequestFormRecord(int id, String name, ComboBoxItem value) {
      this.setId(id);
      this.setName(name);
      this.setValue(value);
    }

    public RequestFormRecord(int id, String name, String value) {
      this.setId(id);
      this.setName(name);
      this.setValue(value);
    }

    public int getId() {
      return this.getAttributeAsInt("ID");
    }

    public String getName() {
      return this.getAttributeAsString("name");
    }

    private void setId(int id) {
      this.setAttribute("ID", id);
    }

    private void setName(String name) {
      this.setAttribute("name", name);
    }

    private void setValue(ComboBoxItem value) {
      this.setAttribute("value", value);
    }

    private void setValue(String value) {
      this.setAttribute("value", value);
    }
  }

  private HLayout layout;

  private VLayout vLayoutLeft;
  private ListGrid requestFormGrid;
  private ListGridField nameField;
  private ListGridField valueField;
  private Button searchButton;
  private VLayout vLayoutLeftBottom;
  private HLayout hLayout;
  byte[] htmlContents;

  private String currentPath;

  private String createUrlParamter() {

    ListGridRecord[] records = this.requestFormGrid.getRecords();

    String ip = "";
    String port = "";
    StringBuffer params = new StringBuffer();

    for (int i = 0; i < records.length; i++) {
      String name = records[i].getAttributeAsString("name");
      String value = records[i].getAttributeAsString("value");

      GWT.log("value : " + value);
      if (name.toLowerCase().equals("ip")) {
        RegExp regExp = RegExp.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
        MatchResult match = regExp.exec(value);
        ip = match.getGroup(0);

      } else if (name.toLowerCase().equals("port")) {
        port = value;
      } else {
        params.append(name);
        params.append("=");
        params.append(value);
        params.append("&");
      }
    }
    String urlParam = "http://";
    urlParam = urlParam + ip + ":" + port + "/";

    return urlParam;
  }

  private void drawPanel() {
    // 최 상위 레이아웃

    this.layout = new HLayout();
    this.layout.setWidth100();
    this.layout.setHeight100();
    this.layout.setMembersMargin(20);
    this.layout.setBackgroundColor("gray");

    // 입력 폼, 검색버튼, 검색정보를 포함
    this.vLayoutLeft = new VLayout();
    this.vLayoutLeft.setShowEdges(true);
    this.vLayoutLeft.setWidth(400);
    this.vLayoutLeft.setMembersMargin(5);
    this.vLayoutLeft.setLayoutMargin(10);
    this.vLayoutLeft.setBackgroundColor("cyan");
    // vLayout.addMember(new BlueBox(null, 50, "height 50"));
    // vLayout.addMember(new BlueBox((String) null, "*", "height *"));
    // vLayout.addMember(new BlueBox((String) null, "30%", "height 30%"));
    // layout.addMember(vLayout);

    // 입력폼
    this.requestFormGrid = new ListGrid();
    this.requestFormGrid.setWidth(400);
    this.requestFormGrid.setShowAllRecords(true);

    this.requestFormGrid.setCanEdit(true);
    this.requestFormGrid.setEditEvent(ListGridEditEvent.CLICK);
    this.requestFormGrid.setEditByCell(true);
    this.requestFormGrid.setHeight(1);
    this.requestFormGrid.setShowAllRecords(true);
    this.requestFormGrid.setBodyOverflow(Overflow.VISIBLE);
    this.requestFormGrid.setOverflow(Overflow.VISIBLE);
    this.requestFormGrid.setLeaveScrollbarGap(false);

    // 입력폼 내부 필드 - name
    this.nameField = new ListGridField("name", "Name", 120);
    this.nameField.setCanEdit(false);

    // 입력폼 내부 필드 - value
    this.valueField = new ListGridField("value", "Value", 280);

    this.requestFormGrid.setFields(this.nameField, this.valueField);

    // 검색 버튼
    this.searchButton = new Button("Search");
    this.searchButton.setWidth(120);
    this.searchButton.setLeft(60);
    this.searchButton.setTop(45);

    this.searchButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {

        String urlParameter = DtcTestPageViewController.this.createUrlParamter();
        // try {
        // DtcTestPageViewController.this.htmlContents = DtcServiceImpl
        // .getHtmlContents(urlParameter);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // // }

      }

    });

    // 검색관련 정보
    this.vLayoutLeftBottom = new VLayout();
    this.vLayoutLeftBottom.setShowEdges(true);
    this.vLayoutLeftBottom.setWidth(400);
    this.vLayoutLeftBottom.setHeight(250);
    this.vLayoutLeftBottom.setMembersMargin(5);
    this.vLayoutLeftBottom.setLayoutMargin(0);

    this.vLayoutLeft.addMember(this.requestFormGrid);
    this.vLayoutLeft.addMember(this.searchButton);
    this.vLayoutLeft.addMember(this.vLayoutLeftBottom);

    // 검색 결과 레이아웃
    this.hLayout = new HLayout();
    this.hLayout.setShowEdges(true);
    this.hLayout.setHeight(150);
    this.hLayout.setMembersMargin(5);
    this.hLayout.setLayoutMargin(10);
    this.hLayout.setBackgroundColor("yellow");

    this.layout.addMember(this.vLayoutLeft);
    this.layout.addMember(this.hLayout);
    this.layout.setLayoutMargin(10);
    this.layout.draw();
    this.layout.setVisible(true);

    // 검색 결과
  }

  public HLayout getLayout() {
    return this.layout;
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);

  }

  @Override
  public void onDtcResponseFrameLoaded(Document dtcFrameDoc, boolean success) {
  }

  @Override
  public void onDtcTestPageLoaded(Document dtcFrameDoc) {

    GWT.log(dtcFrameDoc.getURL());
    this.currentPath = dtcFrameDoc.getURL().split("c=")[1];
    this.refreshDtcTestPage("/" + this.currentPath);
    this.drawPanel();

  }

  @Override
  public void onSubmittingDtcRequest() {
  }

  public void refreshDtcTestPage(String path) {

    DtcService.Util.getInstance().getDtcRequestPageInfo(path, new AsyncCallback<DtcRequestInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        GWT.log(caught.getMessage());
      }

      @Override
      public void onSuccess(final DtcRequestInfo requestInfo) {
        GWT.log(requestInfo.toString());

        List<DtcRequestParameter> params = requestInfo.getParams();
        RequestFormRecord[] records = new RequestFormRecord[params.size()];

        for (int i = 0; i < params.size(); i++) {
          records[i] = new RequestFormRecord(i, params.get(i).getKey(), params.get(i).getValue());
        }
        DtcTestPageViewController.this.requestFormGrid.setData(records);

        // add IP combo box
        RequestFormRecord ipRrecord = new RequestFormRecord(params.size(), "IP", requestInfo
            .getIpInfo().getIpText());
        DtcTestPageViewController.this.requestFormGrid.addData(ipRrecord);

        DtcTestPageViewController.this.requestFormGrid
            .setEditorCustomizer(new ListGridEditorCustomizer() {
              @Override
              public FormItem getEditor(ListGridEditorContext context) {
                ListGridField field = context.getEditField();
                if (field.getName().equals("value")) {
                  RequestFormRecord record = (RequestFormRecord) context.getEditedRecord();
                  // int id = record.getId();

                  if (record.getAttributeAsString("name").equals("IP")) {
                    ComboBoxItem cbItem = new ComboBoxItem();
                    cbItem.setType("comboBox");

                    String[] ipList = new String[requestInfo.getIpInfo()
                        .getOptions().size()];
                    for (int i = 0; i < ipList.length; i++) {
                      ipList[i] = requestInfo.getIpInfo().getOptions().get(i).getValue();
                      // GWT.log("value " +
                      // requestInfo.getIpInfo().getOptions().get(i).getValue());
                      // GWT.log("key " +
                      // requestInfo.getIpInfo().getOptions().get(i).getKey());
                      // GWT.log("name " +
                      // requestInfo.getIpInfo().getOptions().get(i).getName());
                    }
                    cbItem.setValueMap(ipList);

                    return cbItem;
                  } else {
                    TextItem textItem = new TextItem();
                    textItem.setShowHint(true);
                    textItem.setShowHintInField(true);
                    textItem.setHint("Some Hint");

                    return textItem;
                  }
                }
                return context.getDefaultProperties();
              }
            });
      }
    });
  }
}
