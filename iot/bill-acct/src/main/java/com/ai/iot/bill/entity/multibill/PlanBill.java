package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**资费账单
 * Created by geyunfeng on 2017/8/1.
 */
public class PlanBill implements Serializable {

  private static final long serialVersionUID = 2920434671899757317L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int planType;
  private int paymentType;
  private double orders;
  private double prepayActivations;
  private double activeOrders;
  private double activationGracePeriodOrders;
  private double mininumActivationTermOrders;
  private long orderCharge;
  private long fixedPoolCharge;
  private long dataValue;
  private long dataCharge;
  private long remoteData;
  private long remoteDataCharge;
  private long localData;
  private long localDataCharge;
  private long smsValue;
  private long smsMoValue;
  private long smsMtValue;
  private long smsCharge;
  private long voiceValue;
  private long voiceMoValue;
  private long voiceMtValue;
  private long voiceCharge;
  private long totalCharge;
  private long includeDataPrimaryZone;
  private long includeSms;
  private long includeSmsMo;
  private long includeSmsMt;
  private long includeVoice;
  private long includeVoiceMo;
  private long includeVoiceMt;
  private int includeDataMode;
  private int includeSmsMode;
  private int includeSmsMoMode;
  private int includeSmsMtMode;
  private int includeVoiceMode;
  private int includeVoiceMoMode;
  private int includeVoiceMtMode;
  private int activeTier;
  private int rateGroup;
  private double discountRate;
  private long discountAmount;

  public PlanBill() {
  }

  @Override
  public String toString() {
    return "PlanBill{" +
            "seqId=" + seqId +
            ", acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", planType=" + planType +
            ", paymentType=" + paymentType +
            ", orders=" + orders +
            ", prepayActivations=" + prepayActivations +
            ", activeOrders=" + activeOrders +
            ", activationGracePeriodOrders=" + activationGracePeriodOrders +
            ", mininumActivationTermOrders=" + mininumActivationTermOrders +
            ", orderCharge=" + orderCharge +
            ", fixedPoolCharge=" + fixedPoolCharge +
            ", dataValue=" + dataValue +
            ", dataCharge=" + dataCharge +
            ", remoteData=" + remoteData +
            ", remoteDataCharge=" + remoteDataCharge +
            ", localData=" + localData +
            ", localDataCharge=" + localDataCharge +
            ", smsValue=" + smsValue +
            ", smsMoValue=" + smsMoValue +
            ", smsMtValue=" + smsMtValue +
            ", smsCharge=" + smsCharge +
            ", voiceValue=" + voiceValue +
            ", voiceMoValue=" + voiceMoValue +
            ", voiceMtValue=" + voiceMtValue +
            ", voiceCharge=" + voiceCharge +
            ", totalCharge=" + totalCharge +
            ", includeDataPrimaryZone=" + includeDataPrimaryZone +
            ", includeSms=" + includeSms +
            ", includeSmsMo=" + includeSmsMo +
            ", includeSmsMt=" + includeSmsMt +
            ", includeVoice=" + includeVoice +
            ", includeVoiceMo=" + includeVoiceMo +
            ", includeVoiceMt=" + includeVoiceMt +
            ", includeDataMode=" + includeDataMode +
            ", includeSmsMode=" + includeSmsMode +
            ", includeSmsMoMode=" + includeSmsMoMode +
            ", includeSmsMtMode=" + includeSmsMtMode +
            ", includeVoiceMode=" + includeVoiceMode +
            ", includeVoiceMoMode=" + includeVoiceMoMode +
            ", includeVoiceMtMode=" + includeVoiceMtMode +
            ", activeTier=" + activeTier +
            ", rateGroup=" + rateGroup +
            ", discountRate=" + discountRate +
            ", discountAmount=" + discountAmount +
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

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getPlanType() {
    return planType;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(int paymentType) {
    this.paymentType = paymentType;
  }

  public double getOrders() {
    return orders;
  }

  public void setOrders(double orders) {
    this.orders = orders;
  }

  public double getPrepayActivations() {
    return prepayActivations;
  }

  public void setPrepayActivations(double prepayActivations) {
    this.prepayActivations = prepayActivations;
  }

  public double getActiveOrders() {
    return activeOrders;
  }

  public void setActiveOrders(double activeOrders) {
    this.activeOrders = activeOrders;
  }

  public double getActivationGracePeriodOrders() {
    return activationGracePeriodOrders;
  }

  public void setActivationGracePeriodOrders(double activationGracePeriodOrders) {
    this.activationGracePeriodOrders = activationGracePeriodOrders;
  }

  public double getMininumActivationTermOrders() {
    return mininumActivationTermOrders;
  }

  public void setMininumActivationTermOrders(double mininumActivationTermOrders) {
    this.mininumActivationTermOrders = mininumActivationTermOrders;
  }

  public long getOrderCharge() {
    return orderCharge;
  }

  public void setOrderCharge(long orderCharge) {
    this.orderCharge = orderCharge;
  }

  public long getFixedPoolCharge() {
    return fixedPoolCharge;
  }

  public void setFixedPoolCharge(long fixedPoolCharge) {
    this.fixedPoolCharge = fixedPoolCharge;
  }

  public long getDataValue() {
    return dataValue;
  }

  public void setDataValue(long dataValue) {
    this.dataValue = dataValue;
  }

  public long getDataCharge() {
    return dataCharge;
  }

  public void setDataCharge(long dataCharge) {
    this.dataCharge = dataCharge;
  }

  public long getRemoteData() {
    return remoteData;
  }

  public void setRemoteData(long remoteData) {
    this.remoteData = remoteData;
  }

  public long getRemoteDataCharge() {
    return remoteDataCharge;
  }

  public void setRemoteDataCharge(long remoteDataCharge) {
    this.remoteDataCharge = remoteDataCharge;
  }

  public long getLocalData() {
    return localData;
  }

  public void setLocalData(long localData) {
    this.localData = localData;
  }

  public long getLocalDataCharge() {
    return localDataCharge;
  }

  public void setLocalDataCharge(long localDataCharge) {
    this.localDataCharge = localDataCharge;
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

  public long getSmsCharge() {
    return smsCharge;
  }

  public void setSmsCharge(long smsCharge) {
    this.smsCharge = smsCharge;
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

  public long getVoiceCharge() {
    return voiceCharge;
  }

  public void setVoiceCharge(long voiceCharge) {
    this.voiceCharge = voiceCharge;
  }

  public long getTotalCharge() {
    return totalCharge;
  }

  public void setTotalCharge(long totalCharge) {
    this.totalCharge = totalCharge;
  }

  public long getIncludeDataPrimaryZone() {
    return includeDataPrimaryZone;
  }

  public void setIncludeDataPrimaryZone(long includeDataPrimaryZone) {
    this.includeDataPrimaryZone = includeDataPrimaryZone;
  }

  public long getIncludeSms() {
    return includeSms;
  }

  public void setIncludeSms(long includeSms) {
    this.includeSms = includeSms;
  }

  public long getIncludeSmsMo() {
    return includeSmsMo;
  }

  public void setIncludeSmsMo(long includeSmsMo) {
    this.includeSmsMo = includeSmsMo;
  }

  public long getIncludeSmsMt() {
    return includeSmsMt;
  }

  public void setIncludeSmsMt(long includeSmsMt) {
    this.includeSmsMt = includeSmsMt;
  }

  public long getIncludeVoice() {
    return includeVoice;
  }

  public void setIncludeVoice(long includeVoice) {
    this.includeVoice = includeVoice;
  }

  public long getIncludeVoiceMo() {
    return includeVoiceMo;
  }

  public void setIncludeVoiceMo(long includeVoiceMo) {
    this.includeVoiceMo = includeVoiceMo;
  }

  public long getIncludeVoiceMt() {
    return includeVoiceMt;
  }

  public void setIncludeVoiceMt(long includeVoiceMt) {
    this.includeVoiceMt = includeVoiceMt;
  }

  public int getIncludeDataMode() {
    return includeDataMode;
  }

  public void setIncludeDataMode(int includeDataMode) {
    this.includeDataMode = includeDataMode;
  }

  public int getIncludeSmsMode() {
    return includeSmsMode;
  }

  public void setIncludeSmsMode(int includeSmsMode) {
    this.includeSmsMode = includeSmsMode;
  }

  public int getIncludeSmsMoMode() {
    return includeSmsMoMode;
  }

  public void setIncludeSmsMoMode(int includeSmsMoMode) {
    this.includeSmsMoMode = includeSmsMoMode;
  }

  public int getIncludeSmsMtMode() {
    return includeSmsMtMode;
  }

  public void setIncludeSmsMtMode(int includeSmsMtMode) {
    this.includeSmsMtMode = includeSmsMtMode;
  }

  public int getIncludeVoiceMode() {
    return includeVoiceMode;
  }

  public void setIncludeVoiceMode(int includeVoiceMode) {
    this.includeVoiceMode = includeVoiceMode;
  }

  public int getIncludeVoiceMoMode() {
    return includeVoiceMoMode;
  }

  public void setIncludeVoiceMoMode(int includeVoiceMoMode) {
    this.includeVoiceMoMode = includeVoiceMoMode;
  }

  public int getIncludeVoiceMtMode() {
    return includeVoiceMtMode;
  }

  public void setIncludeVoiceMtMode(int includeVoiceMtMode) {
    this.includeVoiceMtMode = includeVoiceMtMode;
  }

  public int getActiveTier() {
    return activeTier;
  }

  public void setActiveTier(int activeTier) {
    this.activeTier = activeTier;
  }

  public int getRateGroup() {
    return rateGroup;
  }

  public void setRateGroup(int rateGroup) {
    this.rateGroup = rateGroup;
  }

  public double getDiscountRate() {
    return discountRate;
  }

  public void setDiscountRate(double discountRate) {
    this.discountRate = discountRate;
  }

  public long getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(long discountAmount) {
    this.discountAmount = discountAmount;
  }
}
