package net.skcomms.dtc.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skcomms.dtc.client.DtcTestPageViewObserver;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcRequestParameterModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
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

public class DtcTestPageView {

  static class RequestGridRecord extends ListGridRecord {

    public RequestGridRecord() {
    }

    public RequestGridRecord(int id, String key, String name, ComboBoxItem value) {

      this.setId(id);
      this.setKey(key);
      this.setName(name);
      this.setValue(value);
    }

    public RequestGridRecord(int id, String key, String name, String value) {

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

  private int invalidRecordIdx;

  private HLayout layout;

  private VLayout vLayoutLeft;

  private ListGrid requestFormGrid;

  private ListGridField nameField;

  private ListGridField valueField;

  private Button searchButton;

  private Button modalButton;

  private VLayout vLayoutLeftBottom;

  private HLayout hLayoutRight;

  private HTMLPane htmlPane;

  private DtcRequestMeta requestInfo;

  private DtcChronoView chronoView;

  private DtcSelectTestPageView dtcSelectTestPageView;

  private static final RegExp IP_PATTERN = RegExp.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");

  private final List<DtcTestPageViewObserver> dtcTestPageViewObservers = new ArrayList<DtcTestPageViewObserver>();

  public void addObserver(DtcTestPageViewObserver observer) {
    this.dtcTestPageViewObservers.add(observer);
  }

  public void chronoStart() {
    this.chronoView.start();
  }

  public void chronoStop() {
    this.chronoView.end();
  }

  private FormItem createComboBoxControl() {
    ComboBoxItem cbItem = new ComboBoxItem();
    cbItem.setType("comboBox");
    String[] ipList = new String[DtcTestPageView.this.requestInfo.getIpInfo()
        .getOptions().size()];

    for (int i = 0; i < ipList.length; i++) {
      cbItem.setAttribute("key",
          DtcTestPageView.this.requestInfo.getIpInfo().getOptions().get(i).getKey());
      cbItem.setAttribute("name", DtcTestPageView.this.requestInfo.getIpInfo().getOptions()
          .get(i).getName());
      ipList[i] = DtcTestPageView.this.requestInfo.getIpInfo().getOptions().get(i).getValue();
    }

    cbItem.setValueMap(ipList);
    return cbItem;
  }

  private void createGridRecord() {
    List<DtcRequestParameterModel> params = this.requestInfo.getParams();
    List<RequestGridRecord> records = new ArrayList<RequestGridRecord>();

    int index = 0;
    for (DtcRequestParameterModel param : params) {
      records
          .add(new RequestGridRecord(index++, param.getKey(), param.getName(), param.getValue()));
    }

    this.requestFormGrid.setData(records.toArray(new RequestGridRecord[0]));

    // add IP combo box
    RequestGridRecord ipRrecord = new RequestGridRecord(params.size(), "IP", "ip_select",
        this.requestInfo.getIpInfo().getIpText());

    this.requestFormGrid.addData(ipRrecord);
    this.requestFormGrid.setEditorCustomizer(this.createListGridEditorCustomizer());
  }

  private ListGridEditorCustomizer createListGridEditorCustomizer() {
    return new ListGridEditorCustomizer() {

      @Override
      public FormItem getEditor(ListGridEditorContext context) {
        ListGridField field = context.getEditField();

        if (field.getName().equals("value")) {
          RequestGridRecord record = (RequestGridRecord) context.getEditedRecord();
          if (record.getAttributeAsString("key").equals("IP")) {
            return DtcTestPageView.this.createComboBoxControl();
          } else {
            return new TextItem();
          }
        }
        return context.getDefaultProperties();
      }
    };
  }

  public String createRequestData() {
    StringBuffer requestData = new StringBuffer();
    for (ListGridRecord record : this.requestFormGrid.getRecords()) {
      requestData.append("&");

      String request = "";
      GWT.log(record.getAttribute("value"));
      if (record.getAttribute("name").toLowerCase().equals("ip_select")) {
        MatchResult match = DtcTestPageView.IP_PATTERN.exec(record.getAttribute("value"));
        request = record.getAttribute("name") + "=" + match.getGroup(0);
      } else {
        String value = record.getAttribute("value");
        String encodedValue = (value == null) ? "" : URL.encode(value);
        request = record.getAttribute("name") + "=" + encodedValue;
      }
      requestData.append(request);
    }
    GWT.log("Request: " + requestData.toString());
    return requestData.toString();
  }

  public void draw() {
    this.setupVLayoutLeft();
    this.setupHLayoutRight();
    this.setupContentsLayout();

    this.layout.redraw();
    this.layout.setVisible(true);
  }

  private void fireReadyToRequest() {
    for (DtcTestPageViewObserver observer : this.dtcTestPageViewObservers) {
      observer.onReadyRequestData();
    }
  }

  public Map<String, String> getRequestParameters() {
    Map<String, String> params = new HashMap<String, String>();
    for (ListGridRecord record : this.requestFormGrid.getRecords()) {
      String value;
      if (record.getAttribute("name").toLowerCase().equals("ip_select")) {
        MatchResult match = DtcTestPageView.IP_PATTERN.exec(record.getAttribute("value"));
        value = match.getGroup(0);
      } else {
        value = record.getAttribute("value");
      }
      params.put(record.getAttribute("key"), (value == null ? "" : value));
    }

    return params;
  }

  public void setHTMLData(String convertedHTML) {
    this.htmlPane.setContents(convertedHTML);
  }

  public void setRequestInfo(DtcRequestMeta requestInfo) {
    this.requestInfo = requestInfo;
  }

  private void setupChronoView() {
    this.chronoView = new DtcChronoView();
    this.chronoView.setWidth(300);
    this.chronoView.setHeight(10);
  }

  private void setupContentsLayout() {
    if (this.layout != null) {
      RootPanel.get("dtcContainer").remove(this.layout);
    }

    this.layout = new HLayout();
    RootPanel.get("dtcContainer").add(this.layout);

    this.layout.setWidth100();
    this.layout.setHeight(800);
    this.layout.setMembersMargin(20);
    this.layout.addMember(this.vLayoutLeft);
    this.layout.addMember(this.hLayoutRight);
    this.layout.setLayoutMargin(10);
  }

  private void setupHLayoutRight() {
    this.hLayoutRight = new HLayout();
    this.hLayoutRight.setShowEdges(true);

    this.hLayoutRight.setMembersMargin(5);
    this.hLayoutRight.setLayoutMargin(10);

    this.htmlPane = new HTMLPane();
    this.htmlPane.setTop(40);
    this.htmlPane.setWidth100();
    this.htmlPane.setStyleName("response_panel");

    this.hLayoutRight.addMember(this.htmlPane);
  }

  private void setupModalButton() {
    this.modalButton = new Button("Modal");
    this.modalButton.setWidth(120);
    this.modalButton.setLeft(60);
    this.modalButton.setTop(45);
    this.modalButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

        DtcTestPageView.this.dtcSelectTestPageView = new DtcSelectTestPageView();
        DtcTestPageView.this.dtcSelectTestPageView.setWidth(800);
        DtcTestPageView.this.dtcSelectTestPageView.setHeight(600);
        DtcTestPageView.this.dtcSelectTestPageView.setTitle("Select Test Page Window");
        DtcTestPageView.this.dtcSelectTestPageView.setShowMinimizeButton(false);
        DtcTestPageView.this.dtcSelectTestPageView.setIsModal(true);
        DtcTestPageView.this.dtcSelectTestPageView.setShowModalMask(true);
        DtcTestPageView.this.dtcSelectTestPageView.centerInPage();
        DtcTestPageView.this.dtcSelectTestPageView.setDismissOnOutsideClick(true);
        for (DtcTestPageViewObserver observer : DtcTestPageView.this.dtcTestPageViewObservers) {
          observer.onClickSelectTestPageButton();
        }
        DtcTestPageView.this.dtcSelectTestPageView.addCloseClickHandler(new CloseClickHandler() {
          @Override
          public void onCloseClick(CloseClickEvent event) {

            DtcTestPageView.this.dtcSelectTestPageView.destroy();
          }
        });

        DtcTestPageView.this.dtcSelectTestPageView.show();
      }
    });
  }

  private void setupNameField() {
    this.nameField = new ListGridField("key", "Name", 120);
    this.nameField.setCanEdit(false);
    this.nameField.setCanFilter(false);
    this.nameField.setCanSort(false);
    this.nameField.setCanReorder(false);
    this.nameField.setCanGroupBy(false);
  }

  private void setupPreviewHandler() {
    Event.addNativePreviewHandler(new Event.NativePreviewHandler() {

      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
        if (event.isFirstHandler() &&
            event.getTypeInt() == Event.ONKEYUP &&
            event.getNativeEvent().getKeyCode() == 13 &&
            DtcTestPageView.this.validateRequestData() == 0) {
          DtcTestPageView.this.fireReadyToRequest();
        }
      }

    });
  }

  private void setupRequestFormGrid() {
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
    this.requestFormGrid.setCanAutoFitFields(false);
    this.requestFormGrid.setCanCollapseGroup(false);

    this.createGridRecord();
    this.setupNameField();
    this.setupValueField();
    this.requestFormGrid.setFields(this.nameField, this.valueField);

    this.setupPreviewHandler();
  }

  private void setupSearchButton() {
    this.searchButton = new Button("Search");
    this.searchButton.setWidth(120);
    this.searchButton.setLeft(60);
    this.searchButton.setTop(45);
    this.searchButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (DtcTestPageView.this.validateRequestData() == 0) {
          DtcTestPageView.this.fireReadyToRequest();
        }
      }
    });
  }

  private void setupValueField() {
    this.valueField = new ListGridField("value", "Value", 180);
    this.valueField.setCanFilter(false);
    this.valueField.setCanSort(false);
    this.valueField.setCanReorder(false);
    this.valueField.setCanGroupBy(false);
  }

  private void setupVLayoutBottom() {
    this.vLayoutLeftBottom = new VLayout();
    this.vLayoutLeftBottom.setShowEdges(true);
    this.vLayoutLeftBottom.setWidth(300);
    this.vLayoutLeftBottom.setHeight100();

    this.vLayoutLeftBottom.setMembersMargin(5);
    this.vLayoutLeftBottom.setLayoutMargin(0);
  }

  private void setupVLayoutLeft() {
    this.setupChronoView();
    this.setupRequestFormGrid();
    this.setupSearchButton();
    this.setupModalButton();
    this.setupVLayoutBottom();

    this.vLayoutLeft = new VLayout();
    this.vLayoutLeft.setShowEdges(true);
    this.vLayoutLeft.setWidth(300);
    this.vLayoutLeft.setMembersMargin(10);
    this.vLayoutLeft.setLayoutMargin(10);

    this.wireVLayoutLeft();

  }

  public int validateRequestData() {
    for (ListGridRecord record : this.requestFormGrid.getRecords()) {
      if (record.getAttribute("key").toLowerCase().equals("query")) {
        if (record.getAttribute("value") == null) {
          this.invalidRecordIdx = this.requestFormGrid.getRecordIndex(record);
          SC.warn("Invalid Query", new BooleanCallback() {

            @Override
            public void execute(Boolean value) {
              int recordIdx = DtcTestPageView.this.invalidRecordIdx;
              DtcTestPageView.this.requestFormGrid.selectRecord(recordIdx);
            }

          });
          return -1;
        }
      } else if (record.getAttribute("name").toLowerCase().equals("ip_select")) {
        RegExp regExp = RegExp.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
        MatchResult match = regExp.exec(record.getAttribute("value"));
        this.invalidRecordIdx = this.requestFormGrid.getRecordIndex(record);

        if (match.getGroup(0) == null) {
          SC.warn("Invalid IP value", new BooleanCallback() {

            @Override
            public void execute(Boolean value) {
              int recordIdx = DtcTestPageView.this.invalidRecordIdx;
              DtcTestPageView.this.requestFormGrid.selectRecord(recordIdx);
            }

          });
          return -1;
        }

      }
    }
    return 0;
  }

  private void wireVLayoutLeft() {
    this.vLayoutLeft.addMember(this.chronoView);
    this.vLayoutLeft.addMember(this.requestFormGrid);
    this.vLayoutLeft.addMember(this.searchButton);
    this.vLayoutLeft.addMember(this.modalButton);
    this.vLayoutLeft.addMember(this.vLayoutLeftBottom);
  }
}
