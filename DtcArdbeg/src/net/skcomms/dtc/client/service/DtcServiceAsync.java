/**
 * 
 */
package net.skcomms.dtc.client.service;

import java.util.List;

import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jujang@sk.com
 */
public interface DtcServiceAsync {

  void getDir(String path, AsyncCallback<List<DtcNodeMeta>> callback);

  void getDtcRequestMeta(String path, AsyncCallback<DtcRequestMeta> callback);

  void getDtcResponse(DtcRequest httpRequestInfo, AsyncCallback<DtcResponse> callback);

}
