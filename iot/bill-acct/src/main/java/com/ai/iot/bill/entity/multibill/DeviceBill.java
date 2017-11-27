package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceBill implements Serializable {

  private static final long serialVersionUID = -8448216359301168311L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long subAcctId;
  private int planId;
  private int prepayTermNums;
  private int standradPlanId;
  private double usageProrationIndex;
  private int status;
  private long orderCharge;
  private long shareCharge;
  private int activeEvents;
  private long eventCharge;
  private long dataValue;
  private long smsValue;
  private long smsMoValue;
  private long smsMtValue;
  private long platformSmsValue;
  private long voiceValue;
  private long voiceMoValue;
  private long voiceMtValue;

  public DeviceBill() {
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBill that = (DeviceBill) o;

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
    return "DeviceBill{" +
            "seqId=" + seqId +
            ", acctId=" + acctId +
            ", deviceId=" + deviceId +
            ", cycleId=" + cycleId +
            ", subAcctId=" + subAcctId +
            ", planId=" + planId +
            ", prepayTermNums=" + prepayTermNums +
            ", standradPlanId=" + standradPlanId +
            ", usageProrationIndex=" + usageProrationIndex +
            ", status=" + status +
            ", orderCharge=" + orderCharge +
            ", shareCharge=" + shareCharge +
            ", activeEvents=" + activeEvents +
            ", eventCharge=" + eventCharge +
            ", dataValue=" + dataValue +
            ", smsValue=" + smsValue +
            ", smsMoValue=" + smsMoValue +
            ", smsMtValue=" + smsMtValue +
            ", platformSmsValue=" + platformSmsValue +
            ", voiceValue=" + voiceValue +
            ", voiceMoValue=" + voiceMoValue +
            ", voiceMtValue=" + voiceMtValue +
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

  public long getSubAcctId() {
    return subAcctId;
  }

  public void setSubAcctId(long subAcctId) {
    this.subAcctId = subAcctId;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPrepayTermNums() {
    return prepayTermNums;
  }

  public void setPrepayTermNums(int prepayTermNums) {
    this.prepayTermNums = prepayTermNums;
  }

  public int getStandradPlanId() {
    return standradPlanId;
  }

  public void setStandradPlanId(int standradPlanId) {
    this.standradPlanId = standradPlanId;
  }

  public double getUsageProrationIndex() {
    return usageProrationIndex;
  }

  public void setUsageProrationIndex(double usageProrationIndex) {
    this.usageProrationIndex = usageProrationIndex;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getOrderCharge() {
    return orderCharge;
  }

  public void setOrderCharge(long orderCharge) {
    this.orderCharge = orderCharge;
  }

  public long getShareCharge() {
    return shareCharge;
  }

  public void setShareCharge(long shareCharge) {
    this.shareCharge = shareCharge;
  }

  public int getActiveEvents() {
    return activeEvents;
  }

  public void setActiveEvents(int activeEvents) {
    this.activeEvents = activeEvents;
  }

  public long getEventCharge() {
    return eventCharge;
  }

  public void setEventCharge(long eventCharge) {
    this.eventCharge = eventCharge;
  }

  public long getDataValue() {
    return dataValue;
  }

  public void setDataValue(long dataValue) {
    this.dataValue = dataValue;
  }

  public long getSmsValue() {
    return smsValue;
  }

  public void setSmsValue(long smsValue) {
    this.smsValue = smsValue;
  }

  public long getSmsMoValue() {
    return smsMoValue;
  }

  public void setSmsMoValue(long smsMoValue) {
    this.smsMoValue = smsMoValue;
  }

  public long getSmsMtValue() {
    return smsMtValue;
  }

  public void setSmsMtValue(long smsMtValue) {
    this.smsMtValue = smsMtValue;
  }

  public long getPlatformSmsValue() {
    return platformSmsValue;
  }

  public void setPlatformSmsValue(long platformSmsValue) {
    this.platformSmsValue = platformSmsValue;
  }

  public long getVoiceValue() {
    return voiceValue;
  }

  public void setVoiceValue(long voiceValue) {
    this.voiceValue = voiceValue;
  }

  public long getVoiceMoValue() {
    return voiceMoValue;
  }

  public void setVoiceMoValue(long voiceMoValue) {
    this.voiceMoValue = voiceMoValue;
  }

  public long getVoiceMtValue() {
    return voiceMtValue;
  }

  public void setVoiceMtValue(long voiceMtValue) {
    this.voiceMtValue = voiceMtValue;
  }
}
