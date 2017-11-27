package com.ai.iot.bill.entity.usage;

import java.io.Serializable;

/**
 * 预付灵活共享,月付灵活共享,月付固定共享的累积量-设备级明细信息
 * Created by geyunfeng on 2017/6/20.
 */
public class UsedAddShareDetail implements Serializable {

  private static final long serialVersionUID = -2023920076606126747L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private long poolId;
  private int planId;
  private int planVersionId;
  private int billId;
  private long currValue;   //规整后的当月累计值(不包括往月值在内)
  private long roundAdjust; //每日调整
  private long bulkAdjust;  //批量超额
  private long lastValue;   //规整后的往月累计值

  public UsedAddShareDetail() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UsedAddShareDetail that = (UsedAddShareDetail) o;

    if (acctId != that.acctId) return false;
    if (tpInsId != that.tpInsId) return false;
    if (planVersionId != that.planVersionId) return false;
    return billId == that.billId;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "UsedAddShareDetail{" +
        "acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", tpInsId=" + tpInsId +
        ", poolId=" + poolId +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", billId=" + billId +
        ", currValue=" + currValue +
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

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public long getTpInsId() {
    return tpInsId;
  }

  public void setTpInsId(long tpInsId) {
    this.tpInsId = tpInsId;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
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

  public long getRoundAdjust() {
    return roundAdjust;
  }

  public void setRoundAdjust(long roundAdjust) {
    this.roundAdjust = roundAdjust;
  }

  public long getBulkAdjust() {
    return bulkAdjust;
  }

  public void setBulkAdjust(long bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }

  public long getLastValue() {
    return lastValue;
  }

  public void setLastValue(long lastValue) {
    this.lastValue = lastValue;
  }
}
