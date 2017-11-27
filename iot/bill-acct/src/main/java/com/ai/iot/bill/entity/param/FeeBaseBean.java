package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**标准资费 TD_B_FEEBASE
 * Created by geyunfeng on 2017/6/6.
 */
public class FeeBaseBean implements Serializable, IdInterface, Comparable<FeeBaseBean> {

  private static final long serialVersionUID = -5635707578793397109L;
  private int planId;
  private int priority;
  private int billId;
  private int baseUnit;
  private int baseTimes;
  private long unitRatio;
  private int precision;
  private boolean isRoam;

  public FeeBaseBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FeeBaseBean feeBaseBean = (FeeBaseBean) o;

    return billId == feeBaseBean.billId;

  }

  public int compareTo(FeeBaseBean o) {
    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    return 0;
  }

  @Override
  public int hashCode() {
    return billId;
  }

  @Override
  public String toString() {
    return "FeeBaseBean{" +
        "planId=" + planId +
        ", priority=" + priority +
        ", billId=" + billId +
        ", baseUnit=" + baseUnit +
        ", baseTimes=" + baseTimes +
        ", unitRatio=" + unitRatio +
        ", precision=" + precision +
        ", isRoam=" + isRoam +
        '}';
  }

  public int getId() {
    return billId;
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

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
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

  public boolean isRoam() {
    return isRoam;
  }

  public void setRoam(boolean roam) {
    isRoam = roam;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
