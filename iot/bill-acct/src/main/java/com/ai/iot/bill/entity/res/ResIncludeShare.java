package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 灵活共享，总的共享量--账户级别
 * Created by geyunfeng on 2017/6/2.
 */
public class ResIncludeShare implements Serializable,ResInterface, Comparable<ResIncludeShare> {

  private static final long serialVersionUID = 5510323835021778287L;
  private long acctId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int groupId;
  private int billId;
  private long value;
  private Date startTime;
  private Date endTime;

  public ResIncludeShare() {
  }

  public ResIncludeShare(long acctId, int planVersionId, int billId) {
    this.acctId = acctId;
    this.planVersionId = planVersionId;
    this.billId = billId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResIncludeShare that = (ResIncludeShare) o;

    if (acctId != that.acctId) return false;
    if (planVersionId != that.planVersionId) return false;
    return billId == that.billId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + billId;
    return result;
  }

  public int compareTo(ResIncludeShare o) {
    if (this.acctId < o.acctId) return -1;
    if (this.acctId > o.acctId) return 1;

    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    return 0;
  }


  @Override
  public String toString() {
    return "ResIncludeShare{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", groupId=" + groupId +
            ", billId=" + billId +
            ", value=" + value +
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

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
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

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
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
