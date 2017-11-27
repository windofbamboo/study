package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**账户优惠级别
 * Created by geyunfeng on 2017/8/3.
 */
public class AcctDiscountGrade implements Serializable, TimeInterface {

  private static final long serialVersionUID = -5678225208522033536L;
  private long acctId;
  private int grade;
  private int deviceUp;
  private int deviceLower;
  private long smsFee;
  private Date startTime;
  private Date endTime;

  public AcctDiscountGrade() {
  }

  @Override
  public String toString() {
    return "AcctDiscountGrade{" +
            "acctId=" + acctId +
            ", grade=" + grade +
            ", deviceUp=" + deviceUp +
            ", deviceLower=" + deviceLower +
            ", smsFee=" + smsFee +
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

  public int getDeviceUp() {
    return deviceUp;
  }

  public void setDeviceUp(int deviceUp) {
    this.deviceUp = deviceUp;
  }

  public int getDeviceLower() {
    return deviceLower;
  }

  public void setDeviceLower(int deviceLower) {
    this.deviceLower = deviceLower;
  }

  public long getSmsFee() {
    return smsFee;
  }

  public void setSmsFee(long smsFee) {
    this.smsFee = smsFee;
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
