package com.ai.iot.bill.entity.usage;

import com.ai.iot.bill.entity.res.ResIncludePool;

import java.io.Serializable;

/**
 * 预付固定共享的累积量
 * Created by geyunfeng on 2017/6/2.
 */
public class UsedAddPoolTotal implements Serializable, Comparable<UsedAddPoolTotal>,BulkAdjustInterface,ShareAddInterface {

  private static final long serialVersionUID = -3866429763662844750L;
  private long acctId;
  private int cycleId;
  private long poolId;
  private int planVersionId;
  private int billId;
  private long currValue;  //规整后的当月累计值(不包括往月值在内)
  private long lastValue;  //规整后的往月累计值
  private long roundAdjust; //每日调整
  private long bulkAdjust;  //批量超额
  private long upperValue; //核减后超出的用量(计算时使用)

  public UsedAddPoolTotal() {
  }

  public UsedAddPoolTotal(long acctId, long poolId, int billId) {
    this.acctId = acctId;
    this.poolId = poolId;
    this.billId = billId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UsedAddPoolTotal that = (UsedAddPoolTotal) o;

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

  @Override
  public int compareTo(UsedAddPoolTotal o) {
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
    return "UsedAddPoolTotal{" +
        "acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", poolId=" + poolId +
        ", planVersionId=" + planVersionId +
        ", billId=" + billId +
        ", currValue=" + currValue +
        ", lastValue=" + lastValue +
        ", roundAdjust=" + roundAdjust +
        ", bulkAdjust=" + bulkAdjust +
        ", upperValue=" + upperValue +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  @Override
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

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  @Override
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

  public long getLastValue() {
    return lastValue;
  }

  public void setLastValue(long lastValue) {
    this.lastValue = lastValue;
  }

  public long getRoundAdjust() {
    return roundAdjust;
  }

  public void setRoundAdjust(long roundAdjust) {
    this.roundAdjust = roundAdjust;
  }

  @Override
  public long getBulkAdjust() {
    return bulkAdjust;
  }

  @Override
  public void setBulkAdjust(long bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }

  public long getUpperValue() {
    return upperValue;
  }

  public void setUpperValue(long upperValue) {
    this.upperValue = upperValue;
  }
}
