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
package net.skcomms.dtc.client.service;

import java.util.List;

import net.skcomms.dtc.shared.DtcNodeMetaModel;
import net.skcomms.dtc.shared.DtcRequestInfoModel;
import net.skcomms.dtc.shared.DtcServiceVerifier;
import net.skcomms.dtc.shared.HttpRequestInfoModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dtcservice")
public interface DtcService extends RemoteService {

  /**
   * Utility class for simplifying access to the instance of async service.
   */
  public static class Util {
    private static DtcServiceAsync instance;

    public static DtcServiceAsync getInstance() {
      if (Util.instance == null) {
        Util.instance = GWT.create(DtcService.class);
      }
      return Util.instance;
    }
  }

  /**
   * �����━ 由ъ��몃� 媛���⑤�.
   * 
   * @param path
   *          �����━ 寃쎈�. 寃쎈�������깆�
   *          {@link DtcServiceVerifier#isValidDirectoryPath(String)}��李멸�.
   * @return �����━ �����由ъ���
   * @throws IllegalArgumentException
   *           �����━ 寃쎈�媛������� ��� 寃쎌�.
   */
  List<DtcNodeMetaModel> getDir(String path) throws IllegalArgumentException;

  DtcRequestInfoModel getDtcRequestPageInfo(String path);

  String getDtcTestPageResponse(HttpRequestInfoModel httpRequestInfo);
}
