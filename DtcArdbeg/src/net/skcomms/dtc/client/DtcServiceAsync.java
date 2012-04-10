/**
 * 
 */
package net.skcomms.dtc.client;

import java.util.List;
import java.util.Map;

import net.skcomms.dtc.shared.Item;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jujang@sk.com
 * 
 */
public interface DtcServiceAsync {
  void getDir(String path, AsyncCallback<List<Item>> callback);

  void getRequestParameters(String path, AsyncCallback<Map<String, String>> callback);

  void getDtcResponseFormat(String path, AsyncCallback<DtcResponseType> callback);
}
