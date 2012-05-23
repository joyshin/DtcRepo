package net.skcomms.dtc.client.controller;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.dtc.client.DefaultDtcArdbegObserver;
import net.skcomms.dtc.client.DtcArdbeg;
import net.skcomms.dtc.client.service.DtcService;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.DtcRequestParameterModel;

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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
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

  static class RequestFormRecord extends ListGridRecord {
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

  private HLayout layout = null;
  private VLayout vLayoutLeft;
  private ListGrid requestFormGrid;
  private ListGridField nameField;
  private ListGridField valueField;
  private Button searchButton;
  private VLayout vLayoutLeftBottom;
  private HLayout hLayoutRight;

  private String currentPath;

  final HTMLPane htmlPane = new HTMLPane();

  protected DtcRequestInfoModel requestInfo;

  private String createRequest() {
    StringBuffer requestData = new StringBuffer();
    String testURL = "c" + "=" + URL.encode(this.currentPath);
    String process = "process=1";
    requestData.append(testURL);
    requestData.append("&");
    requestData.append(process);

    for (ListGridRecord record : this.requestFormGrid.getRecords()) {
      requestData.append("&");

      String request = "";
      if (record.getAttribute("name").toLowerCase().equals("ip_select")) {
        RegExp regExp = RegExp.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
        MatchResult match = regExp.exec(record.getAttribute("value"));
        request = record.getAttribute("name") + "=" + match.getGroup(0);
      } else {
        String value = URL.encode(record.getAttribute("value"));
        request = record.getAttribute("name") + "=" + value;
      }
      requestData.append(request);
    }
    GWT.log("Request: " + requestData.toString());
    return requestData.toString();
  }

  private void drawPanel() {
    if (this.layout != null) {
      RootPanel.get("dtcContainer").remove(this.layout);
    }

    this.layout = new HLayout();
    RootPanel.get("dtcContainer").add(this.layout);

    this.layout.setWidth100();
    this.layout.setHeight(800);
    this.layout.setMembersMargin(20);

    this.vLayoutLeft = new VLayout();
    this.vLayoutLeft.setShowEdges(true);
    this.vLayoutLeft.setWidth(300);
    this.vLayoutLeft.setMembersMargin(10);
    this.vLayoutLeft.setLayoutMargin(10);

    this.requestFormGrid = new ListGrid();
    this.requestFormGrid.setWidth(300);
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

    this.valueField = new ListGridField("value", "Value", 180);

    this.requestFormGrid.setFields(this.nameField, this.valueField);

    this.searchButton = new Button("Search");
    this.searchButton.setWidth(120);
    this.searchButton.setLeft(60);
    this.searchButton.setTop(45);

    this.searchButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        String requestData = DtcTestPageViewController.this.createRequest();
        String targetURL = URL.encode(DtcArdbeg.getDtcProxyUrl() + "response.html");
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, targetURL);

        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestData(requestData);
        request.setCallback(new RequestCallback() {

          @Override
          public void onError(Request request, Throwable exception) {
            GWT.log(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {

            GWT.log(response.getText());
            String rawUrl = response.getText();
            RegExp regExp = RegExp.compile("src=\"([^\"]*)");
            MatchResult match = regExp.exec(rawUrl);
            String responseUrl = match.getGroup(0);
            GWT.log("responseUrl: " + responseUrl);

            RequestBuilder resultRequest = new RequestBuilder(RequestBuilder.GET,
                DtcArdbeg.getDtcProxyUrl() + responseUrl.split("/")[1]);
            resultRequest.setHeader("Content-Type", "text/html; charset="
                + DtcTestPageViewController.this.requestInfo.getEncoding());
            resultRequest.setCallback(new RequestCallback() {

              @Override
              public void onError(Request request, Throwable exception) {
                GWT.log(exception.getMessage());

              }

              @Override
              public void onResponseReceived(Request request, Response response) {
                String result = response.getText();

                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendEscaped(result);
                GWT.log(builder.toSafeHtml().asString());
                DtcTestPageViewController.this.htmlPane
                    .setContents(builder.toSafeHtml().asString());
              }
            });

            try {
              resultRequest.send();
            } catch (RequestException e) {
              e.printStackTrace();
            }

            DtcTestPageViewController.this.hLayoutRight.setHeight100();
          }
        });

        try {
          request.send();
        } catch (RequestException e) {
          e.printStackTrace();
        }
      }
    });

    this.vLayoutLeftBottom = new VLayout();
    this.vLayoutLeftBottom.setShowEdges(true);
    this.vLayoutLeftBottom.setWidth(300);
    this.vLayoutLeftBottom.setHeight(250);

    this.vLayoutLeftBottom.setMembersMargin(5);
    this.vLayoutLeftBottom.setLayoutMargin(0);

    this.vLayoutLeft.addMember(this.requestFormGrid);
    this.vLayoutLeft.addMember(this.searchButton);
    this.vLayoutLeft.addMember(this.vLayoutLeftBottom);

    this.hLayoutRight = new HLayout();
    this.hLayoutRight.setShowEdges(true);

    this.hLayoutRight.setMembersMargin(5);
    this.hLayoutRight.setLayoutMargin(10);

    this.htmlPane.setTop(40);
    this.htmlPane.setWidth100();
    this.htmlPane.setStyleName("response_panel");
    this.htmlPane.setContentsURL(URL.encode(DtcArdbeg.getDtcProxyUrl() + "response.html"));

    this.hLayoutRight.addMember(this.htmlPane);

    this.layout.addMember(this.vLayoutLeft);
    this.layout.addMember(this.hLayoutRight);
    this.layout.setLayoutMargin(10);
    this.layout.redraw();
    this.layout.setVisible(true);
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
  }

  @Override
  public void onSubmittingDtcRequest() {

  }

  public void refreshDtcTestPage(String path) {

    DtcService.Util.getInstance().getDtcRequestPageInfo(path,
        new AsyncCallback<DtcRequestInfoModel>() {

          @Override
          public void onFailure(Throwable caught) {
            caught.printStackTrace();
            GWT.log(caught.getMessage());
          }

          @Override
          public void onSuccess(final DtcRequestInfoModel requestInfo) {
            DtcTestPageViewController.this.requestInfo = requestInfo;
            GWT.log(requestInfo.toString());

            List<DtcRequestParameterModel> params = requestInfo.getParams();
            List<RequestFormRecord> records = new ArrayList<RequestFormRecord>();

            int index = 0;
            for (DtcRequestParameterModel param : params) {
              records.add(new RequestFormRecord(index++, param.getKey(), param.getName(), param
                  .getValue()));
            }
            DtcTestPageViewController.this.requestFormGrid.setData(records
                .toArray(new RequestFormRecord[0]));

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
    this.drawPanel();
  }
}
