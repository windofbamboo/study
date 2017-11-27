package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/** 短信批价规则 TD_B_RATE_PLAN_SMS
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanSmsBean implements Serializable, IdInterface,RatePlanVoiceSms,Comparable<RatePlanSmsBean> {

  private static final long serialVersionUID = 5895295551591047860L;
  private int planVersionId;
  private int zoneId;
  private int zoneBillingGroupId;
  private long includeSms;
  private int billId;
  private long unitRatio;
  private long includeSmsMo;
  private int billIdMo;
  private long unitRatioMo;
  private long includeSmsMt;
  private int billIdMt;
  private long unitRatioMt;
  private int precision;
  private boolean roamTag;


  public RatePlanSmsBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanSmsBean that = (RatePlanSmsBean) o;

    if (planVersionId != that.planVersionId) return false;
    if (zoneId != that.zoneId) return false;
    if (zoneBillingGroupId != that.zoneBillingGroupId) return false;
    if (billId != that.billId) return false;
    return roamTag == that.roamTag;

  }

  public int compareTo(RatePlanSmsBean o) {
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
    return "RatePlanSmsBean{" +
        "planVersionId=" + planVersionId +
        ", zoneId=" + zoneId +
        ", zoneBillingGroupId=" + zoneBillingGroupId +
        ", includeSms=" + includeSms +
        ", billId=" + billId +
        ", unitRatio=" + unitRatio +
        ", includeSmsMo=" + includeSmsMo +
        ", billIdMo=" + billIdMo +
        ", unitRatioMo=" + unitRatioMo +
        ", includeSmsMt=" + includeSmsMt +
        ", billIdMt=" + billIdMt +
        ", unitRatioMt=" + unitRatioMt +
        ", precision=" + precision +
        ", roamTag=" + roamTag +
        '}';
  }

  @Override
  public int getId() {
    return planVersionId;
  }
  @Override
  public long getInclude(){
    return includeSms;
  }
  @Override
  public long getIncludeMo(){
    return includeSmsMo;
  }
  @Override
  public long getIncludeMt(){
    return includeSmsMt;
  }
  @Override
  public int getBaseUnit() {
    return 1;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
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

  public long getIncludeSms() {
    return includeSms;
  }

  public void setIncludeSms(long includeSms) {
    this.includeSms = includeSms;
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

  public long getIncludeSmsMo() {
    return includeSmsMo;
  }

  public void setIncludeSmsMo(long includeSmsMo) {
    this.includeSmsMo = includeSmsMo;
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

  public long getIncludeSmsMt() {
    return includeSmsMt;
  }

  public void setIncludeSmsMt(long includeSmsMt) {
    this.includeSmsMt = includeSmsMt;
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
