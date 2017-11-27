package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**优惠账单
 * Created by geyunfeng on 2017/6/2.
 */
public class AcctBillDiscount implements Serializable {

  private static final long serialVersionUID = 8134017988322297165L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int itemId;
  private int disountPercent;
  private long orignalCharge;
  private long disountCharge;

  public AcctBillDiscount() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillDiscount that = (AcctBillDiscount) o;

    if (acctId != that.acctId) return false;
    return itemId == that.itemId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + itemId;
    return result;
  }

  @Override
  public String toString() {
    return "AcctBillDiscount{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", itemId=" + itemId +
        ", disountPercent=" + disountPercent +
        ", orignalCharge=" + orignalCharge +
        ", disountCharge=" + disountCharge +
        '}';
  }

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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getDisountPercent() {
    return disountPercent;
  }

  public void setDisountPercent(int disountPercent) {
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
