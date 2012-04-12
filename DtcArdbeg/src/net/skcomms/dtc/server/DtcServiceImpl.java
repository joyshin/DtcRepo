/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.skcomms.dtc.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import net.skcomms.dtc.client.DtcService;
import net.skcomms.dtc.shared.DtcNodeInfo;
import net.skcomms.dtc.shared.DtcServiceVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcServiceImpl extends RemoteServiceServlet implements DtcService {

  private static final String DTC_URL = "http://10.141.6.198/";

  private static ParserCallback createDtcParserCallback(final String basePath,
      final List<DtcNodeInfo> items) {
    ParserCallback callback = new ParserCallback() {
      private int textCount;
      private DtcNodeInfo currentItem = null;
      private boolean beforeHeaderRow = true;

      @Override
      public void handleEndOfLineString(String eol) {
      }

      @Override
      public void handleEndTag(HTML.Tag tag, int pos) {
        if (tag == Tag.TR) {
          if (this.currentItem != null) {
            GWT.log("path:" + this.currentItem.getPath());
            items.add(this.currentItem);
            this.currentItem = null;
          }
        }
      }

      @Override
      public void handleError(String errorMsg, int pos) {
      }

      @Override
      public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
      }

      @Override
      public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        if (this.beforeHeaderRow) {
          if (tag == Tag.TR) {
            this.beforeHeaderRow = false;
          }
          return;
        }

        if (tag == Tag.TR) {
          this.currentItem = new DtcNodeInfo();
          this.textCount = 0;
        } else if (tag == Tag.A) {
          Enumeration<?> names = a.getAttributeNames();
          while (names.hasMoreElements()) {
            Object name = names.nextElement();
            if (name.toString().equals("href")) {
              String href = (String) a.getAttribute(name);
              int index = href.indexOf('=');
              this.currentItem.setPath("/" + href.substring(index + 1));
              break;
            }
          }
        }
      }

      @Override
      public void handleText(char[] data, int pos) {
        if (this.currentItem != null) {
          this.setColumn(String.valueOf(data));
        }
      }

      private void setColumn(String value) {
        switch (this.textCount++) {
        case 0:
          this.currentItem.setName(value);
          if (!this.currentItem.isLeaf()) {
            this.textCount++;
          }
          break;
        case 2:
          this.currentItem.setDescription(value);
          break;
        case 3:
          this.currentItem.setUpdateTime(value);
        }
      }
    };
    return callback;
  }

  /**
   * @param path
   * @param contents
   * @return
   * @throws IOException
   */
  static List<DtcNodeInfo> extractItemsFrom(String path, byte[] contents) throws IOException {
    final List<DtcNodeInfo> items = new ArrayList<DtcNodeInfo>();

    HTMLEditorKit.ParserCallback callback = DtcServiceImpl.createDtcParserCallback(path, items);

    new ParserDelegator().parse(
        new InputStreamReader(new ByteArrayInputStream(contents), "windows-949"), callback, true);
    return items;
  }

  /**
   * @param url
   * @return
   * @throws IOException
   */
  static byte[] getHtmlContents(String path) throws IOException {
    String href;
    if (path.equals("/")) {
      href = DtcServiceImpl.DTC_URL;
    }
    else if (path.endsWith(".ini")) {
      href = DtcServiceImpl.DTC_URL + "?c=" + path.substring(1);
    }
    else {
      href = DtcServiceImpl.DTC_URL + "?b=" + path.substring(1);
    }

    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());

    return contents;
  }

  static byte[] readAllBytes(InputStream is) throws IOException {
    DataInputStream dis = new DataInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream(40960);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = dis.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    return bos.toByteArray();
  }

  @Override
  public List<DtcNodeInfo> getDir(String path) {
    if (!DtcServiceVerifier.isValidPath(path)) {
      throw new IllegalArgumentException("Invalid directory:" + path);
    }

    byte[] contents;
    try {
      contents = DtcServiceImpl.getHtmlContents(path);
      List<DtcNodeInfo> items = DtcServiceImpl.extractItemsFrom(path, contents);
      return items;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
  }
}
