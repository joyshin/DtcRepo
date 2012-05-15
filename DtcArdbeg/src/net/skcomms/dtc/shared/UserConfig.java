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
public class UserConfig implements Serializable {

  public class UserConfigView {
    public String getService() {
      return UserConfig.this.getService();
    }

    public String getUserId() {
      return UserConfig.this.getUserId();
    }

    public int getVisitCount() {
      return UserConfig.this.getVisitCount();
    }
  }

  public static final UserConfig EMPTY_CONFIG;

  static {
    EMPTY_CONFIG = new UserConfig();
  }

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "native")
  private Long id;

  private String userId;

  private String service;

  private Integer visitCount;

  private final transient UserConfigView userConfigView = new UserConfigView();

  public UserConfig() {
  }

  public UserConfig(String anUserId) {
    this.userId = anUserId;
  }

  public String getService() {
    return this.service;
  }

  public String getUserId() {
    return this.userId;
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

  public UserConfigView UserConfigView() {
    return this.userConfigView;
  }
}
