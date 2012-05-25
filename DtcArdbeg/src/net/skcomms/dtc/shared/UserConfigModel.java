package net.skcomms.dtc.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "userId") })
public class UserConfigModel implements Serializable {

  public class UserConfigView {
    public String getService() {
      return UserConfigModel.this.getService();
    }

    public String getUserId() {
      return UserConfigModel.this.getUserId();
    }

    public int getVisitCount() {
      return UserConfigModel.this.getVisitCount();
    }
  }

  public static final UserConfigModel EMPTY_CONFIG = new UserConfigModel();

  @SuppressWarnings("unused")
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "native")
  private Long id;

  private String userId;

  private String service;

  private Integer visitCount;

  private final transient UserConfigView userConfigView = new UserConfigView();

  public UserConfigModel() {
  }

  public UserConfigModel(String anUserId) {
    this.userId = anUserId;
  }

  public String getService() {
    return this.service;
  }

  public String getUserId() {
    return this.userId;
  }

  public UserConfigView getView() {
    return this.userConfigView;
  }

  public Integer getVisitCount() {
    return this.visitCount;
  }

  public void setVisitCount(String aService, int count) {
    this.service = aService;
    this.visitCount = count;
  }

  @Override
  public String toString() {
    return "{userId:" + this.userId + ", service:" + this.service + ", visitCount:"
        + this.visitCount + "}";
  }
}
