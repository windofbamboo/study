package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**其它账单
 * Created by geyunfeng on 2017/6/2.
 */
public class AcctBillOther implements Serializable {

  private static final long serialVersionUID = 8371664825930389569L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int chargeType;
  private boolean discountTag;
  private String description;
  private int numbers;
  private long fee;
  private long orderInsId;

  public AcctBillOther() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillOther that = (AcctBillOther) o;

    if (acctId != that.acctId) return false;
    if (chargeType != that.chargeType) return false;
    return orderInsId == that.orderInsId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + chargeType;
    result = 31 * result + (int) (orderInsId ^ (orderInsId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "AcctBillOther{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", chargeType=" + chargeType +
        ", discountTag=" + discountTag +
        ", description='" + description + '\'' +
        ", numbers=" + numbers +
        ", fee=" + fee +
        ", orderInsId=" + orderInsId +
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

  public int getChargeType() {
    return chargeType;
  }

  public void setChargeType(int chargeType) {
    this.chargeType = chargeType;
  }

  public boolean discountTag() {
    return discountTag;
  }

  public void setDiscountTag(boolean discountTag) {
    this.discountTag = discountTag;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getNumbers() {
    return numbers;
  }

  public void setNumbers(int numbers) {
    this.numbers = numbers;
  }

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public long getOrderInsId() {
    return orderInsId;
  }

  public void setOrderInsId(long orderInsId) {
    this.orderInsId = orderInsId;
  }
}
