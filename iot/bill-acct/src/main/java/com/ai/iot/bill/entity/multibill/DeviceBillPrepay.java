package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**预付账单
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceBillPrepay implements Serializable {

  private static final long serialVersionUID = 6475049883579529234L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private int planId;
  private long planType;
  private int planVersionId;
  private double usageProrationIndex;
  private long orderCharge;
  private long termStartDate;
  private long termEndDate;
  private String includeSmsValue;
  private String includeDataValue;
  private String includeSmsMoValue;
  private String includeSmsMtValue;
  private String includeVoiceValue;
  private String includeVoiceMoValue;
  private String includeVoiceMtValue;
  private long termDataUsage;
  private long currPeroidData;
  private long termSmsUsage;
  private long currPeroidSms;
  private long currPeroidSmsMo;
  private long currPeroidSmsMt;
  private long termVoiceUsage;
  private long currPeroidVoice;
  private long currPeroidVoiceMo;
  private long currPeroidVoiceMt;

  public DeviceBillPrepay() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillPrepay that = (DeviceBillPrepay) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (cycleId != that.cycleId) return false;
    if (planId != that.planId) return false;
    if (planVersionId != that.planVersionId) return false;
    if (termStartDate != that.termStartDate) return false;
    return termEndDate == that.termEndDate;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + cycleId;
    result = 31 * result + planId;
    result = 31 * result + planVersionId;
    result = 31 * result + (int) (termStartDate ^ (termStartDate >>> 32));
    result = 31 * result + (int) (termEndDate ^ (termEndDate >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "DeviceBillPrepay{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", planId=" + planId +
        ", planType=" + planType +
        ", planVersionId=" + planVersionId +
        ", usageProrationIndex=" + usageProrationIndex +
        ", orderCharge=" + orderCharge +
        ", termStartDate=" + termStartDate +
        ", termEndDate=" + termEndDate +
        ", includeSmsValue='" + includeSmsValue + '\'' +
        ", includeDataValue='" + includeDataValue + '\'' +
        ", includeSmsMoValue='" + includeSmsMoValue + '\'' +
        ", includeSmsMtValue='" + includeSmsMtValue + '\'' +
        ", includeVoiceValue='" + includeVoiceValue + '\'' +
        ", includeVoiceMoValue='" + includeVoiceMoValue + '\'' +
        ", includeVoiceMtValue='" + includeVoiceMtValue + '\'' +
        ", termDataUsage=" + termDataUsage +
        ", currPeroidData=" + currPeroidData +
        ", termSmsUsage=" + termSmsUsage +
        ", currPeroidSms=" + currPeroidSms +
        ", currPeroidSmsMo=" + currPeroidSmsMo +
        ", currPeroidSmsMt=" + currPeroidSmsMt +
        ", termVoiceUsage=" + termVoiceUsage +
        ", currPeroidVoice=" + currPeroidVoice +
        ", currPeroidVoiceMo=" + currPeroidVoiceMo +
        ", currPeroidVoiceMt=" + currPeroidVoiceMt +
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

  public long getPlanType() {
    return planType;
  }

  public void setPlanType(long planType) {
    this.planType = planType;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public double getUsageProrationIndex() {
    return usageProrationIndex;
  }

  public void setUsageProrationIndex(double usageProrationIndex) {
    this.usageProrationIndex = usageProrationIndex;
  }

  public long getOrderCharge() {
    return orderCharge;
  }

  public void setOrderCharge(long orderCharge) {
    this.orderCharge = orderCharge;
  }

  public long getTermStartDate() {
    return termStartDate;
  }

  public void setTermStartDate(long termStartDate) {
    this.termStartDate = termStartDate;
  }

  public long getTermEndDate() {
    return termEndDate;
  }

  public void setTermEndDate(long termEndDate) {
    this.termEndDate = termEndDate;
  }

  public String getIncludeSmsValue() {
    return includeSmsValue;
  }

  public void setIncludeSmsValue(String includeSmsValue) {
    this.includeSmsValue = includeSmsValue;
  }

  public String getIncludeDataValue() {
    return includeDataValue;
  }

  public void setIncludeDataValue(String includeDataValue) {
    this.includeDataValue = includeDataValue;
  }

  public String getIncludeSmsMoValue() {
    return includeSmsMoValue;
  }

  public void setIncludeSmsMoValue(String includeSmsMoValue) {
    this.includeSmsMoValue = includeSmsMoValue;
  }

  public String getIncludeSmsMtValue() {
    return includeSmsMtValue;
  }

  public void setIncludeSmsMtValue(String includeSmsMtValue) {
    this.includeSmsMtValue = includeSmsMtValue;
  }

  public String getIncludeVoiceValue() {
    return includeVoiceValue;
  }

  public void setIncludeVoiceValue(String includeVoiceValue) {
    this.includeVoiceValue = includeVoiceValue;
  }

  public String getIncludeVoiceMoValue() {
    return includeVoiceMoValue;
  }

  public void setIncludeVoiceMoValue(String includeVoiceMoValue) {
    this.includeVoiceMoValue = includeVoiceMoValue;
  }

  public String getIncludeVoiceMtValue() {
    return includeVoiceMtValue;
  }

  public void setIncludeVoiceMtValue(String includeVoiceMtValue) {
    this.includeVoiceMtValue = includeVoiceMtValue;
  }

  public long getTermDataUsage() {
    return termDataUsage;
  }

  public void setTermDataUsage(long termDataUsage) {
    this.termDataUsage = termDataUsage;
  }

  public long getCurrPeroidData() {
    return currPeroidData;
  }

  public void setCurrPeroidData(long currPeroidData) {
    this.currPeroidData = currPeroidData;
  }

  public long getTermSmsUsage() {
    return termSmsUsage;
  }

  public void setTermSmsUsage(long termSmsUsage) {
    this.termSmsUsage = termSmsUsage;
  }

  public long getCurrPeroidSms() {
    return currPeroidSms;
  }

  public void setCurrPeroidSms(long currPeroidSms) {
    this.currPeroidSms = currPeroidSms;
  }

  public long getCurrPeroidSmsMo() {
    return currPeroidSmsMo;
  }

  public void setCurrPeroidSmsMo(long currPeroidSmsMo) {
    this.currPeroidSmsMo = currPeroidSmsMo;
  }

  public long getCurrPeroidSmsMt() {
    return currPeroidSmsMt;
  }

  public void setCurrPeroidSmsMt(long currPeroidSmsMt) {
    this.currPeroidSmsMt = currPeroidSmsMt;
  }

  public long getTermVoiceUsage() {
    return termVoiceUsage;
  }

  public void setTermVoiceUsage(long termVoiceUsage) {
    this.termVoiceUsage = termVoiceUsage;
  }

  public long getCurrPeroidVoice() {
    return currPeroidVoice;
  }

  public void setCurrPeroidVoice(long currPeroidVoice) {
    this.currPeroidVoice = currPeroidVoice;
  }

  public long getCurrPeroidVoiceMo() {
    return currPeroidVoiceMo;
  }

  public void setCurrPeroidVoiceMo(long currPeroidVoiceMo) {
    this.currPeroidVoiceMo = currPeroidVoiceMo;
  }

  public long getCurrPeroidVoiceMt() {
    return currPeroidVoiceMt;
  }

  public void setCurrPeroidVoiceMt(long currPeroidVoiceMt) {
    this.currPeroidVoiceMt = currPeroidVoiceMt;
  }
}
