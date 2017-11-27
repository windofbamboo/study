package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 灵活共享,总的使用情况
 * Created by geyunfeng on 2017/6/2.
 */
public class ResUsedShareTotal implements Serializable {

  private static final long serialVersionUID = -5778345986145093837L;
  private long acctId;
  private int cycleId;
  private int planVersionId;
  private int zoneId;
  private int groupId;
  private int billId;
  private long currValue;
  private Date startTime;
  private Date endTime;

  public ResUsedShareTotal() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResUsedShareTotal that = (ResUsedShareTotal) o;

    if (acctId != that.acctId) return false;
    if (planVersionId != that.planVersionId) return false;
    if (billId != that.billId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + billId;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ResUsedShareTotal{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", groupId=" + groupId +
            ", billId=" + billId +
            ", currValue=" + currValue +
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

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public long getCurrValue() {
    return currValue;
  }

  public void setCurrValue(long currValue) {
    this.currValue = currValue;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
