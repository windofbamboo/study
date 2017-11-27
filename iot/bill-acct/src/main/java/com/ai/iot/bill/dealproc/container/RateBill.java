package com.ai.iot.bill.dealproc.container;

import java.io.Serializable;

/** 用量批价的参数
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class RateBill implements Serializable, Cloneable {

  private static final long serialVersionUID = 7297998517163879926L;
  private int planVersionId;
  private int billId;
  private int paymentType;
  private int bizType;
  private boolean isRoam;
  private boolean isBulk; // 表示是否批量超额
  private int zoneId;
  private int billingGroupId;
  private long value;
  private long ratio;
  private int times;
  private int unit;
  private int precision;
  private boolean isPrice; //表示批价部分是否生效
  private boolean isShare; //表示此分支是否共享
  private int chargeMode; //mo,mt方式

  public RateBill() {
  }

  public RateBill(int planVersionId, int billId, int paymentType, int bizType, boolean isRoam,boolean isBulk, int zoneId, int billingGroupId, long value, long ratio, int times, int unit, int precision, boolean isPrice, boolean isShare, int chargeMode) {
    this.planVersionId = planVersionId;
    this.billId = billId;
    this.paymentType = paymentType;
    this.bizType = bizType;
    this.isRoam = isRoam;
    this.isBulk = isBulk;
    this.zoneId = zoneId;
    this.billingGroupId = billingGroupId;
    this.value = value;
    this.ratio = ratio;
    this.times = times;
    this.unit = unit;
    this.precision = precision;
    this.isPrice = isPrice;
    this.isShare = isShare;
    this.chargeMode = chargeMode;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RateBill rateData = (RateBill) o;

    if (planVersionId != rateData.planVersionId) return false;
    return billId == rateData.billId;

  }

  @Override
  public int hashCode() {
    int result = planVersionId;
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "RateBill{" +
        "planVersionId=" + planVersionId +
        ", billId=" + billId +
        ", paymentType=" + paymentType +
        ", bizType=" + bizType +
        ", isRoam=" + isRoam +
        ", isBulk=" + isBulk +
        ", zoneId=" + zoneId +
        ", billingGroupId=" + billingGroupId +
        ", value=" + value +
        ", ratio=" + ratio +
        ", times=" + times +
        ", unit=" + unit +
        ", precision=" + precision +
        ", isPrice=" + isPrice +
        ", isShare=" + isShare +
        ", chargeMode=" + chargeMode +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
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

  public boolean isBulk() {
    return isBulk;
  }

  public void setBulk(boolean bulk) {
    isBulk = bulk;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getBillingGroupId() {
    return billingGroupId;
  }

  public void setBillingGroupId(int billingGroupId) {
    this.billingGroupId = billingGroupId;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public long getRatio() {
    return ratio;
  }

  public void setRatio(long ratio) {
    this.ratio = ratio;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public int getUnit() {
    return unit;
  }

  public void setUnit(int unit) {
    this.unit = unit;
  }

  public boolean isPrice() {
    return isPrice;
  }

  public void setPrice(boolean price) {
    isPrice = price;
  }

  public boolean isShare() {
    return isShare;
  }

  public void setShare(boolean share) {
    isShare = share;
  }

  public int getChargeMode() {
    return chargeMode;
  }

  public void setChargeMode(int chargeMode) {
    this.chargeMode = chargeMode;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
