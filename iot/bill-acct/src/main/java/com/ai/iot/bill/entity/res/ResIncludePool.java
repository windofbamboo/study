package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 预付共享池中包含的用量
 * Created by geyunfeng on 2017/6/2.
 */
public class ResIncludePool implements Serializable,ResInterface, Comparable<ResIncludePool> {

  private static final long serialVersionUID = -8834062489789646288L;
  private long acctId;
  private long poolId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int groupId;
  private int billId;
  private long totalValue; //总的可用量
  private long currValue;  //当月可用量
  private Date startTime;
  private Date endTime;

  public ResIncludePool() {
  }

  public ResIncludePool(long acctId, long poolId, int billId) {
    this.acctId = acctId;
    this.poolId = poolId;
    this.billId = billId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResIncludePool that = (ResIncludePool) o;

    if (acctId != that.acctId) return false;
    if (poolId != that.poolId) return false;
    return billId == that.billId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (poolId ^ (poolId >>> 32));
    result = 31 * result + billId;
    return result;
  }

  public int compareTo(ResIncludePool o) {
    if (this.acctId < o.acctId) return -1;
    if (this.acctId > o.acctId) return 1;

    if (this.poolId < o.poolId) return -1;
    if (this.poolId > o.poolId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    return 0;
  }

  @Override
  public String toString() {
    return "ResIncludePool{" +
            "acctId=" + acctId +
            ", poolId=" + poolId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", groupId=" + groupId +
            ", billId=" + billId +
            ", totalValue=" + totalValue +
            ", currValue=" + currValue +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getValue() {
    return currValue;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
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

  public long getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(long totalValue) {
    this.totalValue = totalValue;
  }

  public long getCurrValue() {
    return currValue;
  }

  public void setCurrValue(long currValue) {
    this.currValue = currValue;
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
