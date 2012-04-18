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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import net.skcomms.dtc.client.DtcService;
import net.skcomms.dtc.shared.DtcNodeInfo;
import net.skcomms.dtc.shared.DtcRequestInfo;
import net.skcomms.dtc.shared.DtcServiceVerifier;
import net.skcomms.dtc.shared.IpInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcServiceImpl extends RemoteServiceServlet implements DtcService {

  private static final String DTC_URL = "http://10.141.6.198/";

  private static ParserCallback createDtcDirectoryParserCallback(final List<DtcNodeInfo> items) {
    return new ParserCallback() {
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
  }

  /**
   * @param path
   * @param contents
   * @return
   * @throws IOException
   */
  static List<DtcNodeInfo> createDtcNodeInfosFrom(byte[] contents) throws IOException {
    final List<DtcNodeInfo> nodeInfos = new ArrayList<DtcNodeInfo>();

    ParserCallback callback = DtcServiceImpl.createDtcDirectoryParserCallback(nodeInfos);

    new ParserDelegator().parse(
        new InputStreamReader(new ByteArrayInputStream(contents), "windows-949"), callback, true);
    return nodeInfos;
  }

  /**
   * @param requestInfo
   * @param contents
   * @return
   */
  private static ParserCallback createDtcRequestFrameParserCallback(
      final DtcRequestInfo requestInfo,
      final byte[] contents) {
    return new ParserCallback() {

      private int scriptStart = 0;

      private int scriptEnd = 0;

      private IpInfo createIpInfoFrom(String javascript) {
        IpInfo ipInfo = new IpInfo();

        System.out.println(javascript);
        Pattern pattern = Pattern.compile("<option value=\"(.*)\">(.*)</option>");
        Matcher m = pattern.matcher(javascript);
        while (m.find()) {
          ipInfo.addOption(m.group(1), m.group(2));
        }

        return ipInfo;
      }

      @Override
      public void handleEndOfLineString(String eol) {
      }

      @Override
      public void handleEndTag(HTML.Tag tag, int pos) {
        System.out.println("/" + tag.toString() + ":" + pos);
        if (tag == Tag.SCRIPT) {
          if (this.scriptStart == 0) {
            this.scriptStart = pos;
          }
        }
      }

      @Override
      public void handleError(String errorMsg, int pos) {
        // System.out.println(errorMsg.toString() + ":" + pos);
      }

      @Override
      public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        System.out.println("-" + tag.toString() + ":" + pos);
      }

      @Override
      public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        if (tag == Tag.STYLE) {
          if (this.scriptEnd == 0) {
            this.scriptEnd = pos;
            String javascript = new String(contents, this.scriptStart, this.scriptEnd
                - this.scriptStart);
            IpInfo ipInfo = this.createIpInfoFrom(javascript);
            requestInfo.setIpInfo(ipInfo);
          }
        }

        System.out.println("<" + tag.toString() + ":" + pos);
      }

      @Override
      public void handleText(char[] data, int pos) {
        System.out.println(data);
      }

    };
  }

  /**
   * @param contents
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  public static DtcRequestInfo createDtcRequestInfoFrom(byte[] contents)
      throws UnsupportedEncodingException, IOException {
    DtcRequestInfo requestInfo = new DtcRequestInfo();

    String encoding = DtcServiceImpl.guessCharacterEncoding(contents);
    ParserCallback callback = DtcServiceImpl.createDtcRequestFrameParserCallback(requestInfo,
        contents);
    new ParserDelegator().parse(
        new InputStreamReader(new ByteArrayInputStream(contents), encoding), callback, true);

    return requestInfo;
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

  static String guessCharacterEncoding(byte[] bytes) throws IOException {
    String string;
    if (bytes.length > 1024) {
      string = new String(bytes, 0, 1024);
    }
    else {
      string = new String(bytes);
    }
    if (string.contains("charset=utf-8") || string.contains("encoding=\"utf-8\"")) {
      return "utf-8";
    }
    return "windows-949";
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
      List<DtcNodeInfo> items = DtcServiceImpl.createDtcNodeInfosFrom(contents);
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
