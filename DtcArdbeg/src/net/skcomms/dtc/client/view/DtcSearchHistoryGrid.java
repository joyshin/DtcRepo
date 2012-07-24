package net.skcomms.dtc.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import net.skcomms.dtc.client.controller.DtcSearchHistoryController;
import net.skcomms.dtc.client.model.DtcSearchHistory;
import net.skcomms.dtc.client.model.DtcSearchHistoryDao;
import net.skcomms.dtc.client.view.DtcTestPageView.RequestGridRecord;
import net.skcomms.dtc.shared.DtcRequestParameter;

public class DtcSearchHistoryGrid extends ListGrid{
	static class HistoryGridRecord extends ListGridRecord {

		public HistoryGridRecord() {
		}
		
		public HistoryGridRecord(int id, String time) { //, String query) {
			this.setId(id);
			this.setTime(time);
			//this.setQuery(query);
		}

		public void setId(int id) {
			this.setAttribute("ID", id);
		}

//		public void setQuery(String value) {
//			this.setAttribute("query", value);
//		}
		
		public int getId() {
			return this.getAttributeAsInt("ID");
		}

		public void setTime(String value) {
			this.setAttribute("time", value);
		}
		
		public String getTime() {
			return this.getAttributeAsString("time");
		}
		
//		public String getQuery() {
//			return this.getAttributeAsString("query");
//		}
	}

	private List<DtcSearchHistory> params;
	
	private ListGridField nameField;
	
	public DtcSearchHistoryGrid() {
		this.setupNameField();
		this.setFields(nameField);
	}
	
	private void setupNameField() {
	    this.nameField = new ListGridField("key", "Name", 120);
	    this.nameField.setCanEdit(false);
	    this.nameField.setCanFilter(false);
	    this.nameField.setCanSort(false);
	    this.nameField.setCanReorder(false);
	    this.nameField.setCanGroupBy(false);
	}
	
	public void recordUpdate(List<DtcSearchHistory> params) {
		this.params = params;
	    List<HistoryGridRecord> records = new ArrayList<HistoryGridRecord>();

	    int index = 0;
	    String formattedTime = new String();
	    for (DtcSearchHistory param : params) {
	    	formattedTime = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(param.getSearchTime());
	    	records
	          .add(new HistoryGridRecord(index++, formattedTime));
	    }

	    this.setData(records.toArray(new RequestGridRecord[0]));
	}
	
}
