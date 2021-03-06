package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**语音账单
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceBillVoice implements Serializable,Cloneable,Comparable<DeviceBillVoice> {

  private static final long serialVersionUID = -6220407261546115236L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private int planId;
  private int planType;
  private int zoneId;
  private int groupId;
  private boolean isRemote;
  private long voiceValue;
  private String voiceCharge;
  private int includeMode;
  private String includeValue;
  private int callType;

  public DeviceBillVoice() {
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillVoice that = (DeviceBillVoice) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (planId != that.planId) return false;
    if (zoneId != that.zoneId) return false;
    if (groupId != that.groupId) return false;
    return callType == that.callType;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + planId;
    result = 31 * result + zoneId;
    result = 31 * result + groupId;
    result = 31 * result + callType;
    return result;
  }

  @Override
  public int compareTo(DeviceBillVoice o) {
    if (this.getAcctId() < o.getAcctId()) return -1;
    if (this.getAcctId() > o.getAcctId()) return 1;

    if (this.getDeviceId() < o.getDeviceId()) return -1;
    if (this.getDeviceId() > o.getDeviceId()) return 1;

    if (this.getPlanId() < o.getPlanId()) return -1;
    if (this.getPlanId() > o.getPlanId()) return 1;

    if (this.getZoneId() < o.getZoneId()) return -1;
    if (this.getZoneId() > o.getZoneId()) return 1;

    if (this.getGroupId() < o.getGroupId()) return -1;
    if (this.getGroupId() > o.getGroupId()) return 1;

    if (this.getCallType() < o.getCallType()) return -1;
    if (this.getCallType() > o.getCallType()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    return "DeviceBillVoice{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", planId=" + planId +
        ", planType=" + planType +
        ", zoneId=" + zoneId +
        ", groupId=" + groupId +
        ", isRemote=" + isRemote +
        ", voiceValue=" + voiceValue +
        ", voiceCharge=" + voiceCharge +
        ", includeMode=" + includeMode +
        ", includeValue=" + includeValue +
        ", callType=" + callType +
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

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPlanType() {
    return planType;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public boolean isRemote() {
    return isRemote;
  }

  public void setRemote(boolean remote) {
    isRemote = remote;
  }

  public long getVoiceValue() {
    return voiceValue;
  }

  public void setVoiceValue(long voiceValue) {
    this.voiceValue = voiceValue;
  }

  public int getIncludeMode() {
    return includeMode;
  }

  public void setIncludeMode(int includeMode) {
    this.includeMode = includeMode;
  }

  public String getVoiceCharge() {
    return voiceCharge;
  }

  public void setVoiceCharge(String voiceCharge) {
    this.voiceCharge = voiceCharge;
  }

  public String getIncludeValue() {
    return includeValue;
  }

  public void setIncludeValue(String includeValue) {
    this.includeValue = includeValue;
  }

  public int getCallType() {
    return callType;
  }

  public void setCallType(int callType) {
    this.callType = callType;
  }
}
