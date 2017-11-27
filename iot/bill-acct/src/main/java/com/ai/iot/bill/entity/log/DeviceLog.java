package com.ai.iot.bill.entity.log;

import java.io.Serializable;

/**设备处理日志信息
 * Created by geyunfeng on 2017/7/26.
 */
public class DeviceLog implements Serializable {

  private static final long serialVersionUID = -5176153688513005252L;
  private long dealId;
  private long acctId;
  private long deviceId;
  private int dealStage;
  private String dealTag;
  private String remark;
  private int dealTimes;
  private long startTime;
  private long endTime;
  private long updateTime;

  public DeviceLog() {
  }

  public DeviceLog(long dealId, long acctId, long deviceId, int dealStage, String dealTag) {
    this.dealId = dealId;
    this.acctId = acctId;
    this.deviceId = deviceId;
    this.dealStage = dealStage;
    this.dealTag = dealTag;
    this.dealTimes = 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceLog deviceLog = (DeviceLog) o;

    if (acctId != deviceLog.acctId) return false;
    return deviceId == deviceLog.deviceId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "DeviceLog{" +
        "dealId=" + dealId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", dealStage=" + dealStage +
        ", dealTag='" + dealTag + '\'' +
        ", remark='" + remark + '\'' +
        ", dealTimes=" + dealTimes +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", updateTime=" + updateTime +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getDealId() {
    return dealId;
  }

  public void setDealId(long dealId) {
    this.dealId = dealId;
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

  public int getDealStage() {
    return dealStage;
  }

  public void setDealStage(int dealStage) {
    this.dealStage = dealStage;
  }

  public String getDealTag() {
    return dealTag;
  }

  public void setDealTag(String dealTag) {
    this.dealTag = dealTag;
  }

  public int getDealTimes() {
    return dealTimes;
  }

  public void setDealTimes(int dealTimes) {
    this.dealTimes = dealTimes;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }
}
