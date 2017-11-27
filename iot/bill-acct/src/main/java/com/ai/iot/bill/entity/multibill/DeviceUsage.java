package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**设备用量
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceUsage implements Serializable {

  private static final long serialVersionUID = 7742123394913451286L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long dataUsage;
  private long smsUsage;
  private long voiceUsage;
  private long eventDataUsage;
  private int eventNums;

  public DeviceUsage() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceUsage that = (DeviceUsage) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    return cycleId == that.cycleId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + cycleId;
    return result;
  }

  @Override
  public String toString() {
    return "DeviceUsage{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", dataUsage=" + dataUsage +
        ", smsUsage=" + smsUsage +
        ", voiceUsage=" + voiceUsage +
        ", eventDataUsage=" + eventDataUsage +
        ", eventNums=" + eventNums +
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

  public long getDataUsage() {
    return dataUsage;
  }

  public void setDataUsage(long dataUsage) {
    this.dataUsage = dataUsage;
  }

  public long getSmsUsage() {
    return smsUsage;
  }

  public void setSmsUsage(long smsUsage) {
    this.smsUsage = smsUsage;
  }

  public long getVoiceUsage() {
    return voiceUsage;
  }

  public void setVoiceUsage(long voiceUsage) {
    this.voiceUsage = voiceUsage;
  }

  public long getEventDataUsage() {
    return eventDataUsage;
  }

  public void setEventDataUsage(long eventDataUsage) {
    this.eventDataUsage = eventDataUsage;
  }

  public int getEventNums() {
    return eventNums;
  }

  public void setEventNums(int eventNums) {
    this.eventNums = eventNums;
  }
}
