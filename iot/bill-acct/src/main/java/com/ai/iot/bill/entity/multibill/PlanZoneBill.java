package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;

/**资费区域账单
 * Created by geyunfeng on 2017/8/1.
 */
public class PlanZoneBill implements Serializable {

  private static final long serialVersionUID = -7228692650373783935L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int planId;
  private int planType;
  private int zoneId;
  private boolean isRemote;
  private int isExpireTermByUsage;
  private long gprsValue;
  private long gprsFee;
  private int includeMode;
  private long includeValue;
  private long roundAdjust;
  private long bulkAdjust;

  public PlanZoneBill() {
  }

  @Override
  public String toString() {
    return "PlanZoneBill{" +
            "seqId=" + seqId +
            ", acctId=" + acctId +
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

  public long getGprsValue() {
    return gprsValue;
  }

  public void setGprsValue(long gprsValue) {
    this.gprsValue = gprsValue;
  }

  public long getGprsFee() {
    return gprsFee;
  }

  public void setGprsFee(long gprsFee) {
    this.gprsFee = gprsFee;
  }

  public int getIncludeMode() {
    return includeMode;
  }

  public void setIncludeMode(int includeMode) {
    this.includeMode = includeMode;
  }

  public long getIncludeValue() {
    return includeValue;
  }

  public void setIncludeValue(long includeValue) {
    this.includeValue = includeValue;
  }

  public long getRoundAdjust() {
    return roundAdjust;
  }

  public void setRoundAdjust(long roundAdjust) {
    this.roundAdjust = roundAdjust;
  }

  public long getBulkAdjust() {
    return bulkAdjust;
  }

  public void setBulkAdjust(long bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }
}
