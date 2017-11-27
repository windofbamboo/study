package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 共享资费，资源转移关系
 * Created by geyunfeng on 2017/6/19.
 */
public class ResIncludeShareTurn implements Serializable,DeviceResInterface {

  private static final long serialVersionUID = -8783404239428899643L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId; //订购实例
  private long poolId;  //预付固定共享,填写共享池ID;否则为0
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int billingGroupId;
  private int billId;
  private long value;
  private long pileValue;
  private Date startTime;
  private Date endTime;

  public ResIncludeShareTurn() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResIncludeShareTurn that = (ResIncludeShareTurn) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (tpInsId != that.tpInsId) return false;
    if (planVersionId != that.planVersionId) return false;
    if (billId != that.billId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + billId;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ResIncludeShareTurn{" +
        "acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", tpInsId=" + tpInsId +
        ", poolId=" + poolId +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", zoneId=" + zoneId +
        ", billingGroupId=" + billingGroupId +
        ", billId=" + billId +
        ", value=" + value +
        ", pileValue=" + pileValue +
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

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
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

  public long getPileValue() {
    return pileValue;
  }

  public void setPileValue(long pileValue) {
    this.pileValue = pileValue;
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
