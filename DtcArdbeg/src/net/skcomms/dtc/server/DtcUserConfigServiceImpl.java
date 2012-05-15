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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.skcomms.dtc.client.DtcUserConfigService;
import net.skcomms.dtc.shared.UserConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DtcUserConfigServiceImpl extends RemoteServiceServlet implements DtcUserConfigService {

  private EntityManagerFactory emf;

  @Override
  public UserConfig getUserConfig(String userId) {
    EntityManager em = emf.createEntityManager();
    Query query = em.createQuery("select x from UserConfig x where x.userId = :userId");
    query.setParameter("userId", userId);
    return (UserConfig) query.getSingleResult();
    // CriteriaBuilder cb = em.getCriteriaBuilder();
    // CriteriaQuery<UserConfig> query = cb.createQuery(UserConfig.class);
    // Root<UserConfig> root = query.from(UserConfig.class);
    // Predicate condition = cb.equal(root.get(UserConfig_.userId), userId);
    // query.where(condition);
    // TypedQuery<UserConfig> q = em.createQuery(query);
    // List<UserConfig> resultList = q.getResultList();
    // System.out.println("UserConfigs:" + resultList.toString());
    // return resultList.get(0);
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    System.out.println("init() called.");
    super.init(config);
    WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext())
        .getAutowireCapableBeanFactory().autowireBean(this);
  }

  @Autowired
  void setEntityManagerFactory(EntityManagerFactory emf) {
    System.out.println("setEntityManagerFactory() called.");
    this.emf = emf;
  }

  @Override
  public void setUserConfig(String userId, UserConfig userConfig) {
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    em.persist(userConfig);
    em.getTransaction().commit();
    em.close();
  }
}
