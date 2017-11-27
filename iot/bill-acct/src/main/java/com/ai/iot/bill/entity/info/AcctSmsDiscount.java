package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**短信折扣
 * Created by geyunfeng on 2017/8/3.
 */
public class AcctSmsDiscount implements Serializable, TimeInterface {

  private static final long serialVersionUID = 7841423201030056809L;
  private long acctId;
  private int grade;
  private boolean open;
  private Date startTime;
  private Date endTime;

  public AcctSmsDiscount() {
  }

  @Override
  public String toString() {
    return "AcctSmsDiscount{" +
            "acctId=" + acctId +
            ", grade=" + grade +
            ", open=" + open +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getGrade() {
    return grade;
  }

  public void setGrade(int grade) {
    this.grade = grade;
  }

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  @Override
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
