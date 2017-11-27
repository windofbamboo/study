package com.ai.iot.bill.entity.computebill;

import com.ai.iot.bill.entity.BillIAcctnterface;

import java.io.Serializable;

/**
 * 账户的费用来源表
 * Created by geyunfeng on 2017/6/2.
 */
public class BillTrackAcct implements Serializable,BillIAcctnterface {

  private static final long serialVersionUID = -1163321537889203106L;
  private long acctId;
  private int cycleId;
  private int stage; //收费阶段
  private long sourceId;
  private int itemId;
  private long fee;
  private int orderNo;

  public BillTrackAcct() {
  }

  public BillTrackAcct(long acctId, int cycleId, int stage, long sourceId, int itemId, long fee) {
    this.acctId = acctId;
    this.cycleId = cycleId;
    this.stage = stage;
    this.sourceId = sourceId;
    this.itemId = itemId;
    this.fee = fee;
    this.orderNo = 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BillTrackAcct that = (BillTrackAcct) o;

    if (acctId != that.acctId) return false;
    if (stage != that.stage) return false;
    if (sourceId != that.sourceId) return false;
    return itemId == that.itemId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + stage;
    result = 31 * result + (int) (sourceId ^ (sourceId >>> 32));
    result = 31 * result + itemId;
    return result;
  }

  @Override
  public String toString() {
    return "BillTrackAcct{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", stage=" + stage +
            ", sourceId=" + sourceId +
            ", itemId=" + itemId +
            ", fee=" + fee +
            ", orderNo=" + orderNo +
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

  public int getStage() {
    return stage;
  }

  public void setStage(int stage) {
    this.stage = stage;
  }

  public long getSourceId() {
    return sourceId;
  }

  public void setSourceId(long sourceId) {
    this.sourceId = sourceId;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public int getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
  }
}
