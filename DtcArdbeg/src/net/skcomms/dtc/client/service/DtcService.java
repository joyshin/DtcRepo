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

import net.skcomms.dtc.client.model.DtcResponse;
import net.skcomms.dtc.shared.DtcNodeMeta;
import net.skcomms.dtc.shared.DtcRequest;
import net.skcomms.dtc.shared.DtcRequestMeta;
import net.skcomms.dtc.shared.DtcServiceVerifier;

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
   * 디렉토리 리스트를 가져온다.
   * 
   * @param path
   *          디렉토리 경로. 경로의 유효성은
   *          {@link DtcServiceVerifier#isValidDirectoryPath(String)}을 참고.
   * @return 디렉토리 아이템 리스트.
   * @throws IllegalArgumentException
   *           디렉토리 경로가 유효하지 않은 경우.
   */
  List<DtcNodeMeta> getDir(String path) throws IllegalArgumentException;

  DtcRequestMeta getDtcRequestPageInfo(String path);

  DtcResponse getDtcResponse(DtcRequest httpRequestInfo);
}
