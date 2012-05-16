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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.skcomms.dtc.client.DtcUserConfigService;
import net.skcomms.dtc.shared.UserConfigModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcUserConfigServiceImpl extends RemoteServiceServlet implements DtcUserConfigService {

  private EntityManagerFactory emf;

  @Override
  public UserConfigModel getUserConfig(String userId) {
    EntityManager em = this.emf.createEntityManager();
    Query query = em.createQuery("select x from UserConfig x where x.userId = :userId");
    query.setParameter("userId", userId);
    List<UserConfigModel> results = query.getResultList();

    if (results.isEmpty()) {
      return new UserConfigModel(userId);
    } else if (results.size() == 1) {
      return results.get(0);
    } else {
      throw new IllegalStateException("Duplicate rows for username:" + userId);
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    System.out.println("init() called.");
    super.init(config);
    WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext())
        .getAutowireCapableBeanFactory().autowireBean(this);
  }

  @Autowired
  void setEntityManagerFactory(EntityManagerFactory emf) {
    System.out.println("setEntityManagerFactory() called.");
    this.emf = emf;
  }

  @Override
  public void setUserConfig(String userId, UserConfigModel userConfig) {
    EntityManager em = this.emf.createEntityManager();
    em.getTransaction().begin();
    em.persist(userConfig);
    em.getTransaction().commit();
    em.close();
  }
}
