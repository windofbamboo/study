package com.ai.iot.bill.entity.usage;

import java.io.Serializable;

/**
 * 预付灵活共享,月付灵活共享,月付固定共享的累积量,
 * Created by geyunfeng on 2017/6/20.
 */
public class UsedAddShare implements Serializable,BulkAdjustInterface,ShareAddInterface {

  private static final long serialVersionUID = -5622482977079915810L;

  private long acctId;
  private int cycleId;
  private int planVersionId;
  private int billId;
  private long currValue;   //规整后的当月累计值(不包括往月累计值)
  private long roundAdjust; //每日调整
  private long bulkAdjust;  //批量超额
  private long upperValue; //核减后超出的用量(计算时使用)

  public UsedAddShare() {
  }

  public UsedAddShare(long acctId, int planVersionId, int billId) {
    this.acctId = acctId;
    this.planVersionId = planVersionId;
    this.billId = billId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UsedAddShare that = (UsedAddShare) o;

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


  @Override
  public String toString() {
    return "UsedAddShare{" +
        "acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", planVersionId=" + planVersionId +
        ", billId=" + billId +
        ", currValue=" + currValue +
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
