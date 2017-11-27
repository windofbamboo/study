package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**
 * 用量批价累账表 TD_B_SUMBILL
 */
public class SumbillUseBean implements Serializable, Comparable<SumbillUseBean> {

  private static final long serialVersionUID = -1865700332564016776L;
  private int itemId;
  private int chargeMode;
  private int paymentType;
  private int bizType;
  private boolean isRoam;
  private int startCycId;
  private int endCycId;


  public SumbillUseBean() {
  }

  public SumbillUseBean(int chargeMode, int paymentType, int bizType, boolean isRoam) {
    this.chargeMode = chargeMode;
    this.paymentType = paymentType;
    this.bizType = bizType;
    this.isRoam = isRoam;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SumbillUseBean that = (SumbillUseBean) o;

    if (chargeMode != that.chargeMode) return false;
    if (paymentType != that.paymentType) return false;
    if (bizType != that.bizType) return false;
    return isRoam == that.isRoam;

  }

  @Override
  public int hashCode() {
    int result = chargeMode;
    result = 31 * result + paymentType;
    result = 31 * result + bizType;
    result = 31 * result + (isRoam ? 1 : 0);
    return result;
  }

  public int compareTo(SumbillUseBean o) {
    if (this.chargeMode < o.chargeMode) return -1;
    if (this.chargeMode > o.chargeMode) return 1;

    if (this.paymentType < o.paymentType) return -1;
    if (this.paymentType > o.paymentType) return 1;

    if (this.bizType < o.bizType) return -1;
    if (this.bizType > o.bizType) return 1;

    if (this.isRoam != o.isRoam) {
      if (!this.isRoam) return -1;
      if (this.isRoam) return 1;
    }
    return 0;
  }


  @Override
  public String toString() {
    return "SumbillUseBean{" +
            "itemId=" + itemId +
            ", chargeMode=" + chargeMode +
            ", paymentType=" + paymentType +
            ", bizType=" + bizType +
            ", isRoam=" + isRoam +
            ", startCycId=" + startCycId +
            ", endCycId=" + endCycId +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getChargeMode() {
    return chargeMode;
  }

  public void setChargeMode(int chargeMode) {
    this.chargeMode = chargeMode;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(int paymentType) {
    this.paymentType = paymentType;
  }

  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  public boolean isRoam() {
    return isRoam;
  }

  public void setRoam(boolean roam) {
    isRoam = roam;
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
