/**
 * 
 */
package net.skcomms.dtc.server;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author jujang@sk.com
 * 
 */
@Entity
public class DtcLog {

  @SuppressWarnings("unused")
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "native")
  private Long id;

  @SuppressWarnings("unused")
  private String message;

  @SuppressWarnings("unused")
  private Date date = new Date();

  public DtcLog() {
  }

  public DtcLog(String aMessage) {
    this.message = aMessage;
  }

}
