/**
 * 
 */
package net.skcomms.dtc.client;

import com.google.gwt.dom.client.Document;

/**
 * @author jujang@sk.com
 * 
 */
public abstract class DefaultDtcArdbegObserver implements DtcArdbegObserver {

  @Override
  public void onLoadDtcDirectory(Document dtcFrameDoc) {
  }

  @Override
  public void onLoadDtcHome(Document dtcFrameDoc) {
  }

  @Override
  public void onLoadDtcResponseFrame(Document dtcFrameDoc, boolean success) {
  }

  @Override
  public void onLoadDtcTestPage(Document dtcFrameDoc) {
  }

}
