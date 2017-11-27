package com.ai.iot.bill.entity.multibill;

import com.ai.iot.bill.entity.BillIAcctnterface;

import java.io.Serializable;

/**资费组优惠账单
 * Created by geyunfeng on 2017/8/2.
 */
public class RateGroupDiscount implements Serializable,BillIAcctnterface {

  private static final long serialVersionUID = 5547399756171926468L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int rateGroup;
  private int planId;
  private int planVersionId;
  private int itemId;
  private double disountPercent;
  private long orignalCharge;
  private long disountCharge;

  public RateGroupDiscount() {
  }

  @Override
  public String toString() {
    return "RateGroupDiscount{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", rateGroup=" + rateGroup +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", itemId=" + itemId +
        ", disountPercent=" + disountPercent +
        ", orignalCharge=" + orignalCharge +
        ", disountCharge=" + disountCharge +
        '}';
  }

  public long getFee(){return disountCharge;}

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getSeqId() {
    return seqId;
  }

  public void setSeqId(long seqId) {
    this.seqId = seqId;
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

  public int getRateGroup() {
    return rateGroup;
  }

  public void setRateGroup(int rateGroup) {
    this.rateGroup = rateGroup;
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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public double getDisountPercent() {
    return disountPercent;
  }

  public void setDisountPercent(double disountPercent) {
    this.disountPercent = disountPercent;
  }

  public long getOrignalCharge() {
    return orignalCharge;
  }

  public void setOrignalCharge(long orignalCharge) {
    this.orignalCharge = orignalCharge;
  }

  public long getDisountCharge() {
    return disountCharge;
  }

  public void setDisountCharge(long disountCharge) {
    this.disountCharge = disountCharge;
  }
}
