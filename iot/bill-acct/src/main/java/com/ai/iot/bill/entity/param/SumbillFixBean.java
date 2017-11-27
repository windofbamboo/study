package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**固定科目累账表 TD_B_ACCTFEE_SUMBILL
 * Created by geyunfeng on 2017/6/6.
 */
public class SumbillFixBean implements Serializable, Comparable<SumbillFixBean> {

  private static final long serialVersionUID = -6358581341597765387L;
  private int chargeType;
  private int paymentType;
  private int itemId;
  private int startCycId;
  private int endCycId;

  public SumbillFixBean() {
  }

  public SumbillFixBean(int chargeType, int paymentType) {
    this.chargeType = chargeType;
    this.paymentType = paymentType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SumbillFixBean that = (SumbillFixBean) o;

    if (chargeType != that.chargeType) return false;
    return paymentType == that.paymentType;

  }

  @Override
  public int hashCode() {
    int result = chargeType;
    result = 31 * result + paymentType;
    return result;
  }

  public int compareTo(SumbillFixBean o) {
    if (this.chargeType < o.chargeType) return -1;
    if (this.chargeType > o.chargeType) return 1;

    return 0;
  }


  @Override
  public String toString() {
    return "SumbillFixBean{" +
            "chargeType=" + chargeType +
            ", paymentType=" + paymentType +
            ", itemId=" + itemId +
            ", startCycId=" + startCycId +
            ", endCycId=" + endCycId +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getChargeType() {
    return chargeType;
  }

  public void setChargeType(int chargeType) {
    this.chargeType = chargeType;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(int paymentType) {
    this.paymentType = paymentType;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getStartCycId() {
    return startCycId;
  }

  public void setStartCycId(int startCycId) {
    this.startCycId = startCycId;
  }

  public int getEndCycId() {
    return endCycId;
  }

  public void setEndCycId(int endCycId) {
    this.endCycId = endCycId;
  }
}
