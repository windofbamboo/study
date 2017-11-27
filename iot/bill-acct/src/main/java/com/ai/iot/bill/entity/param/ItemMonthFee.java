package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**
 * TD_B_ITEM_MONTHFEE
 */
public class ItemMonthFee implements Serializable {

  private static final long serialVersionUID = -1284032731714087403L;
  private int feeCode;
  private int chargeType;

  public ItemMonthFee() {
  }

  @Override
  public String toString() {
    return "ItemMonthFee{" +
        "feeCode=" + feeCode +
        ", chargeType=" + chargeType +
        '}';
  }

  public int getFeeCode() {
    return feeCode;
  }

  public void setFeeCode(int feeCode) {
    this.feeCode = feeCode;
  }

  public int getChargeType() {
    return chargeType;
  }

  public void setChargeType(int chargeType) {
    this.chargeType = chargeType;
  }
}
