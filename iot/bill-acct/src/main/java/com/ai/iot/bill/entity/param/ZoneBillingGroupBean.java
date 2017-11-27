package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**TD_B_ZONE_BILLINGGROUP
 * Created by geyunfeng on 2017/6/6.
 */
public class ZoneBillingGroupBean implements Serializable, IdInterface, Comparable<ZoneBillingGroupBean> {

  private static final long serialVersionUID = 3415619549809894488L;
  private int zoneBillingGroupId;
  private int billingGroupId;
  private long includeValue;
  private int billId;
  private long unitRatio;
  private long baseUnit;
  private int precision;

  public ZoneBillingGroupBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ZoneBillingGroupBean that = (ZoneBillingGroupBean) o;

    if (zoneBillingGroupId != that.zoneBillingGroupId) return false;
    if (billingGroupId != that.billingGroupId) return false;
    return billId == that.billId;
  }

  public int compareTo(ZoneBillingGroupBean o) {
    if (this.zoneBillingGroupId < o.zoneBillingGroupId) return -1;
    if (this.zoneBillingGroupId > o.zoneBillingGroupId) return 1;

    if (this.billingGroupId < o.billingGroupId) return -1;
    if (this.billingGroupId > o.billingGroupId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;
    return 0;
  }

  @Override
  public int hashCode() {
    int result = zoneBillingGroupId;
    result = 31 * result + billingGroupId;
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "ZoneBillingGroupBean{" +
        "zoneBillingGroupId=" + zoneBillingGroupId +
        ", billingGroupId=" + billingGroupId +
        ", includeValue=" + includeValue +
        ", billId=" + billId +
        ", unitRatio=" + unitRatio +
        ", baseUnit=" + baseUnit +
        ", precision=" + precision +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getId() {
    return zoneBillingGroupId;
  }

  public int getZoneBillingGroupId() {
    return zoneBillingGroupId;
  }

  public void setZoneBillingGroupId(int zoneBillingGroupId) {
    this.zoneBillingGroupId = zoneBillingGroupId;
  }

  public int getBillingGroupId() {
    return billingGroupId;
  }

  public void setBillingGroupId(int billingGroupId) {
    this.billingGroupId = billingGroupId;
  }

  public long getIncludeValue() {
    return includeValue;
  }

  public void setIncludeValue(long includeValue) {
    this.includeValue = includeValue;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public long getUnitRatio() {
    return unitRatio;
  }

  public void setUnitRatio(long unitRatio) {
    this.unitRatio = unitRatio;
  }

  public long getBaseUnit() {
    return baseUnit;
  }

  public void setBaseUnit(long baseUnit) {
    this.baseUnit = baseUnit;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
