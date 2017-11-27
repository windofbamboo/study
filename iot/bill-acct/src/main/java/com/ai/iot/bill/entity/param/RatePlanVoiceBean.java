package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**语音批价规则 TD_B_RATE_PLAN_VOICE
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanVoiceBean implements Serializable, IdInterface,RatePlanVoiceSms,Comparable<RatePlanVoiceBean> {

  private static final long serialVersionUID = 8943358068753523409L;
  private int planVersionId;
  private int zoneId;
  private int zoneBillingGroupId;
  private long includeVoice;
  private int billId;
  private long unitRatio;
  private long includeVoiceMo;
  private int billIdMo;
  private long unitRatioMo;
  private long includeVoiceMt;
  private int billIdMt;
  private long unitRatioMt;
  private int baseUnit;
  private int precision;
  private boolean roamTag;

  public RatePlanVoiceBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanVoiceBean that = (RatePlanVoiceBean) o;

    if (planVersionId != that.planVersionId) return false;
    if (zoneId != that.zoneId) return false;
    if (zoneBillingGroupId != that.zoneBillingGroupId) return false;
    if (billId != that.billId) return false;
    return roamTag == that.roamTag;
  }

  public int compareTo(RatePlanVoiceBean o) {
    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.zoneId < o.zoneId) return -1;
    if (this.zoneId > o.zoneId) return 1;

    if (this.zoneBillingGroupId < o.zoneBillingGroupId) return -1;
    if (this.zoneBillingGroupId > o.zoneBillingGroupId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    if (this.roamTag != o.roamTag) {
      if (!this.roamTag) return -1;
      if (this.roamTag) return 1;
    }
    return 0;
  }

  @Override
  public int hashCode() {
    int result = planVersionId;
    result = 31 * result + zoneId;
    result = 31 * result + zoneBillingGroupId;
    result = 31 * result + billId;
    result = 31 * result + (roamTag ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "RatePlanVoiceBean{" +
        "planVersionId=" + planVersionId +
        ", zoneId=" + zoneId +
        ", zoneBillingGroupId=" + zoneBillingGroupId +
        ", includeVoice=" + includeVoice +
        ", billId=" + billId +
        ", unitRatio=" + unitRatio +
        ", includeVoiceMo=" + includeVoiceMo +
        ", billIdMo=" + billIdMo +
        ", unitRatioMo=" + unitRatioMo +
        ", includeVoiceMt=" + includeVoiceMt +
        ", billIdMt=" + billIdMt +
        ", unitRatioMt=" + unitRatioMt +
        ", baseUnit=" + baseUnit +
        ", precision=" + precision +
        ", roamTag=" + roamTag +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }
  @Override
  public int getId() {
    return planVersionId;
  }
  @Override
  public long getInclude(){
    return includeVoice;
  }
  @Override
  public long getIncludeMo(){
    return includeVoiceMo;
  }
  @Override
  public long getIncludeMt(){
    return includeVoiceMt;
  }
  @Override
  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }
  @Override
  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }
  @Override
  public int getZoneBillingGroupId() {
    return zoneBillingGroupId;
  }

  public void setZoneBillingGroupId(int zoneBillingGroupId) {
    this.zoneBillingGroupId = zoneBillingGroupId;
  }

  public long getIncludeVoice() {
    return includeVoice;
  }

  public void setIncludeVoice(long includeVoice) {
    this.includeVoice = includeVoice;
  }
  @Override
  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }
  @Override
  public long getUnitRatio() {
    return unitRatio;
  }

  public void setUnitRatio(long unitRatio) {
    this.unitRatio = unitRatio;
  }

  public long getIncludeVoiceMo() {
    return includeVoiceMo;
  }

  public void setIncludeVoiceMo(long includeVoiceMo) {
    this.includeVoiceMo = includeVoiceMo;
  }
  @Override
  public int getBillIdMo() {
    return billIdMo;
  }

  public void setBillIdMo(int billIdMo) {
    this.billIdMo = billIdMo;
  }
  @Override
  public long getUnitRatioMo() {
    return unitRatioMo;
  }

  public void setUnitRatioMo(long unitRatioMo) {
    this.unitRatioMo = unitRatioMo;
  }

  public long getIncludeVoiceMt() {
    return includeVoiceMt;
  }

  public void setIncludeVoiceMt(long includeVoiceMt) {
    this.includeVoiceMt = includeVoiceMt;
  }
  @Override
  public int getBillIdMt() {
    return billIdMt;
  }

  public void setBillIdMt(int billIdMt) {
    this.billIdMt = billIdMt;
  }
  @Override
  public long getUnitRatioMt() {
    return unitRatioMt;
  }

  public void setUnitRatioMt(long unitRatioMt) {
    this.unitRatioMt = unitRatioMt;
  }
  @Override
  public int getBaseUnit() {
    return baseUnit;
  }

  public void setBaseUnit(int baseUnit) {
    this.baseUnit = baseUnit;
  }
  @Override
  public boolean isRoamTag() {
    return roamTag;
  }

  public void setRoamTag(boolean roamTag) {
    this.roamTag = roamTag;
  }
  @Override
  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
