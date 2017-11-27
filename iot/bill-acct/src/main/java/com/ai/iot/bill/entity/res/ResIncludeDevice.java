package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 资费计划中包含的用量
 * * Created by geyunfeng on 2017/6/2.
 */
public class ResIncludeDevice implements Serializable,DeviceResInterface,Cloneable, Comparable<ResIncludeDevice> {

  private static final long serialVersionUID = 1083864591643897162L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int billingGroupId;
  private int billId;
  private long totalValue; //总的可用量
  private long currValue;  //当月可用量
  private Date startTime;
  private Date endTime;
  private long poolId;

  public ResIncludeDevice() {
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResIncludeDevice that = (ResIncludeDevice) o;

    if (tpInsId != that.tpInsId) return false;
    return billId == that.billId;
  }

  public int compareTo(ResIncludeDevice o) {
    if (this.tpInsId < o.tpInsId) return -1;
    if (this.tpInsId > o.tpInsId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    return 0;
  }


  @Override
  public int hashCode() {
    int result = (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "ResIncludeDevice{" +
            "acctId=" + acctId +
            ", deviceId=" + deviceId +
            ", cycleId=" + cycleId +
            ", tpInsId=" + tpInsId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", billingGroupId=" + billingGroupId +
            ", billId=" + billId +
            ", totalValue=" + totalValue +
            ", currValue=" + currValue +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", poolId=" + poolId +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getValue() {
    return currValue;
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

  public long getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(long totalValue) {
    this.totalValue = totalValue;
  }

  public long getCurrValue() {
    return currValue;
  }

  public void setCurrValue(long currValue) {
    this.currValue = currValue;
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

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
  }
}


