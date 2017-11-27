package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**区域用量
 * Created by geyunfeng on 2017/6/6.
 */
public class DeviceZoneUsage implements Serializable {

  private static final long serialVersionUID = -5362857221263371590L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private int zoneId;
  private int bizType;
  private long usageValue;

  public DeviceZoneUsage() {
  }

  @Override
  public String toString() {
    return "DeviceZoneUsage{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", zoneId=" + zoneId +
        ", bizType=" + bizType +
        ", usageValue=" + usageValue +
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

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  public long getUsageValue() {
    return usageValue;
  }

  public void setUsageValue(long usageValue) {
    this.usageValue = usageValue;
  }
}
