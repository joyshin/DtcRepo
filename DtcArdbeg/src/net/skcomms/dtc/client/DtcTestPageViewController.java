package net.skcomms.dtc.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.shared.DtcRequestInfo;
import net.skcomms.dtc.shared.DtcRequestParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
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

    public RequestFormRecord(int id, String key, String name, ComboBoxItem value) {
      this.setId(id);
      this.setKey(key);
      this.setName(name);
      this.setValue(value);
    }

    public RequestFormRecord(int id, String key, String name, String value) {
      this.setId(id);
      this.setKey(key);
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

    private void setKey(String key) {
      this.setAttribute("key", key);

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
  private HLayout hLayoutRight;
  private String currentPath;
  final HTMLPane htmlPane = new HTMLPane();

  private String createRequest()
  {
    StringBuffer requestData = new StringBuffer();
    String testURL = "c" + "=" + this.currentPath;
    String process = "process=1";
    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);
    requestData.append("&");

    for (ListGridRecord record : this.requestFormGrid.getRecords()) {

      String request = "";
      if (record.getAttribute("name").toLowerCase().equals("ip_select")) {
        RegExp regExp = RegExp.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
        MatchResult match = regExp.exec(record.getAttribute("value"));
        request = record.getAttribute("name") + "=" + match.getGroup(0);
      } else {
        request = record.getAttribute("name") + "=" + record.getAttribute("value");
      }

      requestData.append(request);
      requestData.append("&");
      GWT.log("request: " + request);
    }
    requestData.deleteCharAt(requestData.length() - 1);
    GWT.log("Request: " + requestData.toString());

    return URL.encode(requestData.toString());
  }

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
        params.append('&');
        params.append(name);
        params.append('=');
        params.append(value);
      }
    }

    StringBuffer urlParam = new StringBuffer();
    String serviceName = this.currentPath.split("/")[0] + "D";
    String apiNum = this.currentPath.split("/")[1].split("\\.")[0];

    urlParam.append("http://");
    urlParam.append(ip);
    urlParam.append(':');
    urlParam.append(port);
    urlParam.append('/');
    urlParam.append(serviceName.toUpperCase());
    urlParam.append('/');
    urlParam.append(apiNum);
    urlParam.append("?Dummy1=1&Dummy2=1&Dummy3=1&Dummy4=1");
    urlParam.append(params.toString());

    return urlParam.toString();
  }

  private void drawPanel() {

    this.layout = new HLayout();
    this.layout.setWidth100();
    // this.layout.setWidth(1000);
    this.layout.setHeight(800);
    // this.layout.setHeight100();
    this.layout.setMembersMargin(20);
    this.layout.setBackgroundColor("gray");

    this.vLayoutLeft = new VLayout();
    this.vLayoutLeft.setShowEdges(true);
    this.vLayoutLeft.setWidth(400);
    this.vLayoutLeft.setHeight100();
    this.vLayoutLeft.setMembersMargin(5);
    this.vLayoutLeft.setLayoutMargin(10);
    this.vLayoutLeft.setBackgroundColor("cyan");
    // vLayout.addMember(new BlueBox(null, 50, "height 50"));
    // vLayout.addMember(new BlueBox((String) null, "*", "height *"));
    // vLayout.addMember(new BlueBox((String) null, "30%", "height 30%"));
    // layout.addMember(vLayout);

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

    this.nameField = new ListGridField("key", "Name", 120);
    this.nameField.setCanEdit(false);

    this.valueField = new ListGridField("value", "Value", 280);

    this.requestFormGrid.setFields(this.nameField, this.valueField);

    this.searchButton = new Button("Search");
    this.searchButton.setWidth(120);
    this.searchButton.setLeft(60);
    this.searchButton.setTop(45);

    this.searchButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub
        String requestData = DtcTestPageViewController.this.createRequest();
        GWT.log("createRequest : " + requestData);
        String targetURL = URL.encode(DtcArdbeg.getDtcProxyUrl() + "response.html");

        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, targetURL);

        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Host", "dtc.skcomms.net");
        request.setHeader("Origin", "http://dtc.skcomms.net");
        request.setHeader("Content-Length", Integer.toString(requestData.length()));
        request.setHeader("Cache-Control", "max-age=0");
        request.setRequestData(requestData);
        request.setCallback(new RequestCallback() {

          @Override
          public void onError(Request request, Throwable exception) {
            // TODO Auto-generated method stub
            GWT.log(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            // TODO Auto-generated method stub
            GWT.log(response.getText());
            String rawUrl = response.getText();
            RegExp regExp = RegExp.compile("src=\"([^\"]*)");
            MatchResult match = regExp.exec(rawUrl);
            String responseUrl = match.getGroup(0);
            GWT.log("responseUrl: " + responseUrl);

            DtcTestPageViewController.this.htmlPane.setContentsURL(DtcArdbeg.getDtcProxyUrl()
                + responseUrl.split("/")[1]);
            // DtcTestPageViewController.this.htmlFlow.setContents(response.getText());

          }
        });

        try {
          request.send();
        } catch (RequestException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });

    this.vLayoutLeftBottom = new VLayout();
    this.vLayoutLeftBottom.setShowEdges(true);
    this.vLayoutLeftBottom.setWidth(400);
    this.vLayoutLeftBottom.setHeight(100);
    this.vLayoutLeftBottom.setMembersMargin(5);
    this.vLayoutLeftBottom.setLayoutMargin(0);

    this.vLayoutLeft.addMember(this.requestFormGrid);
    this.vLayoutLeft.addMember(this.searchButton);
    this.vLayoutLeft.addMember(this.vLayoutLeftBottom);

    this.hLayoutRight = new HLayout();
    this.hLayoutRight.setShowEdges(true);
    this.hLayoutRight.setHeight100();
    this.hLayoutRight.setMembersMargin(5);
    this.hLayoutRight.setLayoutMargin(10);
    this.hLayoutRight.setBackgroundColor("yellow");

    this.htmlPane.setTop(40);
    this.htmlPane.setWidth100();
    this.htmlPane.setStyleName("response_panel");
    this.htmlPane.setShowEdges(true);
    this.htmlPane.setContentsType(ContentsType.PAGE);
    this.htmlPane.setContentsURL(DtcArdbeg.getDtcProxyUrl() + "response.html");

    this.hLayoutRight.addMember(this.htmlPane);

    this.layout.addMember(this.vLayoutLeft);
    this.layout.addMember(this.hLayoutRight);
    this.layout.setLayoutMargin(10);
    this.layout.draw();
    this.layout.setVisible(true);

  }

  public HLayout getLayout() {
    return this.layout;
  }

  public void initialize(DtcArdbeg dtcArdbeg) {
    dtcArdbeg.addDtcArdbegObserver(this);

  }

  private List<FormItem> makeFormItem() {

    List<FormItem> formItems = new ArrayList<FormItem>();
    FormItem path = new FormItem();
    path.setAttribute("c", this.currentPath);
    path.setName("c");
    path.setValue(this.currentPath);
    formItems.add(path);

    for (ListGridRecord record : this.requestFormGrid.getRecords()) {

      FormItem item = new FormItem();
      item.setAttribute(record.getAttribute("name"), record.getAttribute("value"));
      item.setName(record.getAttribute("name"));
      item.setValue(record.getAttribute("value"));
      // GWT.log(item.getName() + ":" + item.getValue().toString());
      formItems.add(item);
    }

    return formItems;
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
          records[i] = new RequestFormRecord(i, params.get(i).getKey(), params.get(i).getName(),
              params.get(i).getValue());
        }
        DtcTestPageViewController.this.requestFormGrid.setData(records);

        // add IP combo box
        RequestFormRecord ipRrecord = new RequestFormRecord(params.size(), "IP", "ip_select",
            requestInfo.getIpInfo().getIpText());
        DtcTestPageViewController.this.requestFormGrid.addData(ipRrecord);

        DtcTestPageViewController.this.requestFormGrid
            .setEditorCustomizer(new ListGridEditorCustomizer() {
              @Override
              public FormItem getEditor(ListGridEditorContext context) {
                ListGridField field = context.getEditField();
                if (field.getName().equals("value")) {
                  RequestFormRecord record = (RequestFormRecord) context.getEditedRecord();
                  // int id = record.getId();

                  if (record.getAttributeAsString("key").equals("IP")) {
                    ComboBoxItem cbItem = new ComboBoxItem();

                    cbItem.setType("comboBox");

                    String[] ipList = new String[requestInfo.getIpInfo()
                        .getOptions().size()];
                    for (int i = 0; i < ipList.length; i++) {
                      cbItem.setAttribute("key", requestInfo.getIpInfo().getOptions().get(i)
                          .getKey());
                      cbItem.setAttribute("name", requestInfo.getIpInfo().getOptions().get(i)
                          .getName());

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
