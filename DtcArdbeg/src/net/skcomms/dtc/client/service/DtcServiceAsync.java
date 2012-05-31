/**
 * 
 */
package net.skcomms.dtc.client.service;

import java.util.List;

import net.skcomms.dtc.shared.DtcNodeMetaModel;
import net.skcomms.dtc.shared.DtcRequestInfoModel;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jujang@sk.com
 * 
 */
public interface DtcServiceAsync {

  void getDir(String path, AsyncCallback<List<DtcNodeMetaModel>> callback);

  void getDtcRequestPageInfo(String path, AsyncCallback<DtcRequestInfoModel> callback);

  void getDtcTestPageResponse(Method httpMethod, String url, String requestData,
      AsyncCallback<String> callback);

}
