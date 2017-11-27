package com.ai.iot.bill.entity.param;

import java.io.Serializable;
import java.sql.Date;

/**资费计划版本 TD_B_RATE_PLAN_VERSION
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanVersionBean implements Serializable, Comparable<RatePlanVersionBean> {

  private static final long serialVersionUID = 8822946292979733107L;
  private int planId;
  private int planVersionId;
  private int state;
  private long accountCharge;
  private int subscriberChargeFerquency;
  private long subscriberCharge;
  private int levelId;
  private int isExpireTermByUsage;
  private int smsChargeTag;
  private int voiceChargeTag;
  private int smsChargeMode;
  private int voiceChargeMode;
  private String sharedMoSms;
  private String sharedMtSms;
  private String sharedMoVoice;
  private String sharedMtVoice;
  private Date startTime;
  private Date rateExpireTime;


  public RatePlanVersionBean() {
  }

  public RatePlanVersionBean(int planId, int planVersionId) {
    this.planId = planId;
    this.planVersionId = planVersionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanVersionBean that = (RatePlanVersionBean) o;

    if (planId != that.planId) return false;
    return planVersionId == that.planVersionId;

  }

  public int compareTo(RatePlanVersionBean o) {
    if (this.planId < o.planId) return -1;
    if (this.planId > o.planId) return 1;

    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    return 0;
  }

  @Override
  public int hashCode() {
    int result = planId;
    result = 31 * result + planVersionId;
    return result;
  }

  @Override
  public String toString() {
    return "RatePlanVersionBean{" +
        "planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", state=" + state +
        ", accountCharge=" + accountCharge +
        ", subscriberChargeFerquency=" + subscriberChargeFerquency +
        ", subscriberCharge=" + subscriberCharge +
        ", levelId=" + levelId +
        ", isExpireTermByUsage=" + isExpireTermByUsage +
        ", smsChargeTag=" + smsChargeTag +
        ", voiceChargeTag=" + voiceChargeTag +
        ", smsChargeMode=" + smsChargeMode +
        ", voiceChargeMode=" + voiceChargeMode +
        ", sharedMoSms='" + sharedMoSms + '\'' +
        ", sharedMtSms='" + sharedMtSms + '\'' +
        ", sharedMoVoice='" + sharedMoVoice + '\'' +
        ", sharedMtVoice='" + sharedMtVoice + '\'' +
        ", startTime=" + startTime +
        ", rateExpireTime=" + rateExpireTime +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
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

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public long getAccountCharge() {
    return accountCharge;
  }

  public void setAccountCharge(long accountCharge) {
    this.accountCharge = accountCharge;
  }

  public int getSubscriberChargeFerquency() {
    return subscriberChargeFerquency;
  }

  public void setSubscriberChargeFerquency(int subscriberChargeFerquency) {
    this.subscriberChargeFerquency = subscriberChargeFerquency;
  }

  public long getSubscriberCharge() {
    return subscriberCharge;
  }

  public void setSubscriberCharge(long subscriberCharge) {
    this.subscriberCharge = subscriberCharge;
  }

  public int getLevelId() {
    return levelId;
  }

  public void setLevelId(int levelId) {
    this.levelId = levelId;
  }

  public int getIsExpireTermByUsage() {
    return isExpireTermByUsage;
  }

  public void setIsExpireTermByUsage(int isExpireTermByUsage) {
    this.isExpireTermByUsage = isExpireTermByUsage;
  }

  public int getSmsChargeTag() {
    return smsChargeTag;
  }

  public void setSmsChargeTag(int smsChargeTag) {
    this.smsChargeTag = smsChargeTag;
  }

  public int getVoiceChargeTag() {
    return voiceChargeTag;
  }

  public void setVoiceChargeTag(int voiceChargeTag) {
    this.voiceChargeTag = voiceChargeTag;
  }

  public int getSmsChargeMode() {
    return smsChargeMode;
  }

  public void setSmsChargeMode(int smsChargeMode) {
    this.smsChargeMode = smsChargeMode;
  }

  public int getVoiceChargeMode() {
    return voiceChargeMode;
  }

  public void setVoiceChargeMode(int voiceChargeMode) {
    this.voiceChargeMode = voiceChargeMode;
  }

  public String getSharedMoSms() {
    return sharedMoSms;
  }

  public void setSharedMoSms(String sharedMoSms) {
    this.sharedMoSms = sharedMoSms;
  }

  public String getSharedMtSms() {
    return sharedMtSms;
  }

  public void setSharedMtSms(String sharedMtSms) {
    this.sharedMtSms = sharedMtSms;
  }

  public String getSharedMoVoice() {
    return sharedMoVoice;
  }

  public void setSharedMoVoice(String sharedMoVoice) {
    this.sharedMoVoice = sharedMoVoice;
  }

  public String getSharedMtVoice() {
    return sharedMtVoice;
  }

  public void setSharedMtVoice(String sharedMtVoice) {
    this.sharedMtVoice = sharedMtVoice;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getRateExpireTime() {
    return rateExpireTime;
  }

  public void setRateExpireTime(Date rateExpireTime) {
    this.rateExpireTime = rateExpireTime;
  }
}
