package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 堆叠的轨迹
 * Created by geyunfeng on 2017/6/19.
 */
public class ResIncludePile implements Serializable,ResInterface {

  private static final long serialVersionUID = -5720159424023739812L;

  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int billingGroupId;
  private int billId;
  private long value;
  private long baseTpInsId; //依赖的基本资费实例号
  private long basePlanId; //依赖的基本资费ID
  private int basePlanType; //依赖的基本资费的类型
  private Date startTime;
  private Date endTime;

  public ResIncludePile() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResIncludePile that = (ResIncludePile) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (tpInsId != that.tpInsId) return false;
    if (planVersionId != that.planVersionId) return false;
    if (zoneId != that.zoneId) return false;
    if (billingGroupId != that.billingGroupId) return false;
    return billId == that.billId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + zoneId;
    result = 31 * result + billingGroupId;
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "ResIncludePile{" +
            "acctId=" + acctId +
            ", deviceId=" + deviceId +
            ", tpInsId=" + tpInsId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", billingGroupId=" + billingGroupId +
            ", billId=" + billId +
            ", value=" + value +
            ", baseTpInsId=" + baseTpInsId +
            ", basePlanId=" + basePlanId +
            ", basePlanType=" + basePlanType +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
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

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public long getTpInsId() {
    return tpInsId;
  }

  public void setTpInsId(long tpInsId) {
    this.tpInsId = tpInsId;
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

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public long getBaseTpInsId() {
    return baseTpInsId;
  }

  public void setBaseTpInsId(long baseTpInsId) {
    this.baseTpInsId = baseTpInsId;
  }

  public long getBasePlanId() {
    return basePlanId;
  }

  public void setBasePlanId(long basePlanId) {
    this.basePlanId = basePlanId;
  }

  public int getBasePlanType() {
    return basePlanType;
  }

  public void setBasePlanType(int basePlanType) {
    this.basePlanType = basePlanType;
  }

  @Override
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
