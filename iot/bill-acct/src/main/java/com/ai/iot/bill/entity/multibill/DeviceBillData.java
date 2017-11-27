package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**流量账单
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceBillData implements Serializable,Cloneable,Comparable<DeviceBillData> {

  private static final long serialVersionUID = -4784170717671929305L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private int planId;
  private int planType;
  private int zoneId;
  private boolean isRemote;
  private int isExpireTermByUsage;
  private long gprsValue;
  private String gprsFee;
  private int includeMode;
  private String includeValue;
  private long roundAdjust;
  private String bulkAdjust;

  public DeviceBillData() {
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillData that = (DeviceBillData) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (planId != that.planId) return false;
    return zoneId == that.zoneId;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + planId;
    result = 31 * result + zoneId;
    return result;
  }

  @Override
  public int compareTo(DeviceBillData o) {
    if (this.getAcctId() < o.getAcctId()) return -1;
    if (this.getAcctId() > o.getAcctId()) return 1;

    if (this.getDeviceId() < o.getDeviceId()) return -1;
    if (this.getDeviceId() > o.getDeviceId()) return 1;

    if (this.getPlanId() < o.getPlanId()) return -1;
    if (this.getPlanId() > o.getPlanId()) return 1;

    if (this.getZoneId() < o.getZoneId()) return -1;
    if (this.getZoneId() > o.getZoneId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    return "DeviceBillData{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", planId=" + planId +
        ", planType=" + planType +
        ", zoneId=" + zoneId +
        ", isRemote=" + isRemote +
        ", isExpireTermByUsage=" + isExpireTermByUsage +
        ", gprsValue=" + gprsValue +
        ", gprsFee=" + gprsFee +
        ", includeMode=" + includeMode +
        ", includeValue=" + includeValue +
        ", roundAdjust=" + roundAdjust +
        ", bulkAdjust=" + bulkAdjust +
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

  public boolean isRemote() {
    return isRemote;
  }

  public void setRemote(boolean remote) {
    isRemote = remote;
  }

  public int getIsExpireTermByUsage() {
    return isExpireTermByUsage;
  }

  public void setIsExpireTermByUsage(int isExpireTermByUsage) {
    this.isExpireTermByUsage = isExpireTermByUsage;
  }

  public int getIncludeMode() {
    return includeMode;
  }

  public void setIncludeMode(int includeMode) {
    this.includeMode = includeMode;
  }

  public long getGprsValue() {
    return gprsValue;
  }

  public void setGprsValue(long gprsValue) {
    this.gprsValue = gprsValue;
  }

  public String getGprsFee() {
    return gprsFee;
  }

  public void setGprsFee(String gprsFee) {
    this.gprsFee = gprsFee;
  }

  public String getIncludeValue() {
    return includeValue;
  }

  public void setIncludeValue(String includeValue) {
    this.includeValue = includeValue;
  }

  public long getRoundAdjust() {
    return roundAdjust;
  }

  public void setRoundAdjust(long roundAdjust) {
    this.roundAdjust = roundAdjust;
  }

  public String getBulkAdjust() {
    return bulkAdjust;
  }

  public void setBulkAdjust(String bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }
}
