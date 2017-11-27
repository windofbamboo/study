package com.ai.iot.bill.entity.res;

import java.io.Serializable;
import java.sql.Date;

/**
 * 设备级核减信息
 * Created by geyunfeng on 2017/6/2.
 */
public class ResUsedDevice implements Serializable {

  private static final long serialVersionUID = 1900965524504614639L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private int zoneId;
  private int billingGroupId;
  private int billId;
  private long usedValue;
  private Date startTime;
  private Date endTime;

  public ResUsedDevice() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResUsedDevice that = (ResUsedDevice) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (tpInsId != that.tpInsId) return false;
    if (billId != that.billId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + billId;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ResUsedDevice{" +
            "acctId=" + acctId +
            ", deviceId=" + deviceId +
            ", cycleId=" + cycleId +
            ", tpInsId=" + tpInsId +
            ", zoneId=" + zoneId +
            ", billingGroupId=" + billingGroupId +
            ", billId=" + billId +
            ", usedValue=" + usedValue +
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

  public long getUsedValue() {
    return usedValue;
  }

  public void setUsedValue(long usedValue) {
    this.usedValue = usedValue;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
