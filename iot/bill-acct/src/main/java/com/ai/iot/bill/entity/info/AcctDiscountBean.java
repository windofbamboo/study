package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**账户优惠
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctDiscountBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = 5541666359216110284L;
  private long acctId;
  private int grade;
  private int deviceUpperLimit;
  private int deviceLowerLimit;
  private int discount;
  private Date startTime;
  private Date endTime;


  public AcctDiscountBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctDiscountBean that = (AcctDiscountBean) o;

    return acctId == that.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctDiscount{" +
            "acctId=" + acctId +
            ", grade=" + grade +
            ", deviceUpperLimit=" + deviceUpperLimit +
            ", deviceLowerLimit=" + deviceLowerLimit +
            ", discount=" + discount +
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

  public int getDeviceUpperLimit() {
    return deviceUpperLimit;
  }

  public void setDeviceUpperLimit(int deviceUpperLimit) {
    this.deviceUpperLimit = deviceUpperLimit;
  }

  public int getDeviceLowerLimit() {
    return deviceLowerLimit;
  }

  public void setDeviceLowerLimit(int deviceLowerLimit) {
    this.deviceLowerLimit = deviceLowerLimit;
  }

  public int getDiscount() {
    return discount;
  }

  public void setDiscount(int discount) {
    this.discount = discount;
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
