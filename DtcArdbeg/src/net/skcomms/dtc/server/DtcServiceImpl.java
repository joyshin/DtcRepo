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
import java.util.List;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import net.skcomms.dtc.client.DtcResponseType;
import net.skcomms.dtc.client.DtcService;
import net.skcomms.dtc.shared.DtcServiceVerifier;
import net.skcomms.dtc.shared.Item;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcServiceImpl extends RemoteServiceServlet implements DtcService {

  private static final String DTC_URL = "http://dtc.skcomms.net/";

  @Override
  public List<Item> getDir(String path) {
    if (!DtcServiceVerifier.isValidDirectory(path)) {
      throw new IllegalArgumentException("Invalid directory:" + path);
    }

    byte[] contents;
    try {
      contents = DtcServiceImpl.getHtmlContents(path);
      List<Item> items = DtcServiceImpl.extractItemsFrom(contents);
      return items;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * @param contents
   * @return
   * @throws IOException
   */
  static List<Item> extractItemsFrom(byte[] contents) throws IOException {
    final List<Item> items = new ArrayList<Item>();

    HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
      private Item currentItem = null;
      private int columnCount;
      private boolean encounterTableHeader = false;

      @Override
      public void handleEndOfLineString(String eol) {
        System.out.println(eol);
      }

      @Override
      public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        if (tag == Tag.TR) {
          // System.out.print("START:");
          // System.out.println(tag.toString() + ":" + pos);

          if (this.encounterTableHeader) {
            this.currentItem = new Item();
            this.columnCount = 0;
          }
          else {
            this.encounterTableHeader = true;
          }
        }
      }

      @Override
      public void handleEndTag(HTML.Tag tag, int pos) {
        if (tag == Tag.TR) {
          // System.out.print("END:");
          // System.out.println(tag.toString() + ":" + pos);

          if (this.currentItem != null) {
            items.add(this.currentItem);
            this.currentItem = null;
          }
        }
      }

      @Override
      public void handleError(String errorMsg, int pos) {
        // System.err.print("ERR:");
        // System.out.println(errorMsg + ":" + pos);
      }

      @Override
      public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        // System.out.print("SIMPLE:");
        // System.out.println(tag + ":" + a.toString() + ":" + pos);
      }

      @Override
      public void handleText(char[] data, int pos) {
        if (this.currentItem != null) {
          this.setColumn(new String(data));
        }
      }

      private void setColumn(String value) {
        switch (this.columnCount++) {
        case 0:
          this.currentItem.setName(value);
          if (!value.endsWith(".ini")) {
            this.columnCount++;
          }
          break;
        case 2:
          this.currentItem.setDescription(value);
          break;
        case 3:
          this.currentItem.setDate(value);
        }
      }
    };
    new ParserDelegator().parse(
        new InputStreamReader(new ByteArrayInputStream(contents)),
        callback,
        true);
    // TODO Auto-generated method stub
    return items;
  }

  @Override
  public Map<String, String> getRequestParameters(String path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DtcResponseType getDtcResponseFormat(String path) {
    // TODO Auto-generated method stub
    return null;
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
      href = DtcServiceImpl.DTC_URL + "?c=" + path;
    }
    else {
      href = DtcServiceImpl.DTC_URL + "?b=" + path;
    }

    URL url = new URL(href);
    URLConnection conn = url.openConnection();
    byte[] contents = DtcServiceImpl.readAllBytes(conn.getInputStream());

    return contents;
  }

  public static byte[] readAllBytes(InputStream is) throws IOException {
    DataInputStream dis = new DataInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream(40960);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = dis.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    return bos.toByteArray();
  }
}
