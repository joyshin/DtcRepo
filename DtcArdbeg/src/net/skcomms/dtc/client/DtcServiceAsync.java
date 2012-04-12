/**
 * 
 */
package net.skcomms.dtc.client;

import java.util.List;

import net.skcomms.dtc.shared.DtcNodeInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jujang@sk.com
 * 
 */
public interface DtcServiceAsync {
  void getDir(String path, AsyncCallback<List<DtcNodeInfo>> callback);
}
