package net.skcomms.dtc.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@Entity
public class UserConfig implements Serializable {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "native")
  private Long id;

  private String userId;

  private String service;

  private Integer visitCount;

  public UserConfig() {
    service = "kbook2s";
    visitCount = 7777;
  }

  public UserConfig(String anUserId) {
    userId = anUserId;
  }

  public String getService() {
    return service;
  }

  public String getUserId() {
    return userId;
  }

  public Integer getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(String aService, int count) {
    service = aService;
    visitCount = count;
  }
}
