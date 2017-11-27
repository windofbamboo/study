package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**流量批价规则 TD_B_RATE_PLAN_DATA
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanDataBean implements Serializable, IdInterface, Comparable<RatePlanDataBean> {

  private static final long serialVersionUID = 4230941794737489900L;
  private int planVersionId;
  private int zoneId;
  private long includeData;
  private int billId;
  private int baseUnit;
  private int baseTimes;
  private long unitRatio;
  private int precision;
  private boolean roamTag;
  private boolean bulkTag;


  public RatePlanDataBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanDataBean that = (RatePlanDataBean) o;

    if (planVersionId != that.planVersionId) return false;
    if (zoneId != that.zoneId) return false;
    if (billId != that.billId) return false;
    return roamTag == that.roamTag;

  }

  @Override
  public int hashCode() {
    int result = planVersionId;
    result = 31 * result + zoneId;
    result = 31 * result + billId;
    result = 31 * result + (roamTag ? 1 : 0);
    return result;
  }

  public int compareTo(RatePlanDataBean o) {
    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.zoneId < o.zoneId) return -1;
    if (this.zoneId > o.zoneId) return 1;

    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    if (this.roamTag != o.roamTag) {
      if (!this.roamTag) return -1;
      if (this.roamTag) return 1;
    }
    return 0;
  }

  @Override
  public String toString() {
    return "RatePlanDataBean{" +
        "planVersionId=" + planVersionId +
        ", zoneId=" + zoneId +
        ", includeData=" + includeData +
        ", billId=" + billId +
        ", baseUnit=" + baseUnit +
        ", baseTimes=" + baseTimes +
        ", unitRatio=" + unitRatio +
        ", precision=" + precision +
        ", roamTag=" + roamTag +
        ", bulkTag=" + bulkTag +
        '}';
  }

  public int getId() {
    return planVersionId;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public long getIncludeData() {
    return includeData;
  }

  public void setIncludeData(long includeData) {
    this.includeData = includeData;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getBaseUnit() {
    return baseUnit;
  }

  public void setBaseUnit(int baseUnit) {
    this.baseUnit = baseUnit;
  }

  public int getBaseTimes() {
    return baseTimes;
  }

  public void setBaseTimes(int baseTimes) {
    this.baseTimes = baseTimes;
  }

  public long getUnitRatio() {
    return unitRatio;
  }

  public void setUnitRatio(long unitRatio) {
    this.unitRatio = unitRatio;
  }

  public boolean isRoamTag() {
    return roamTag;
  }

  public void setRoamTag(boolean roamTag) {
    this.roamTag = roamTag;
  }

  public boolean isBulkTag() {
    return bulkTag;
  }

  public void setBulkTag(boolean bulkTag) {
    this.bulkTag = bulkTag;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
