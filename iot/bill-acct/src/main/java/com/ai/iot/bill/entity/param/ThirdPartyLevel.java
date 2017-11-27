package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**第三方计费 TD_B_THIRD_PARTY_LEVEL
 * Created by geyunfeng on 2017/8/4.
 */
public class ThirdPartyLevel implements Serializable, IdInterface, Comparable<ThirdPartyLevel> {

  private static final long serialVersionUID = 6328402632450229825L;
  private int planVersionId;
  private int levelPriority;
  private int minAmount;
  private int maxAmount;
  private int levelUnit;
  private int baseUnit;
  private int baseTimes;
  private long unitRatio;
  private int precision;

  public ThirdPartyLevel() {
  }

  @Override
  public int compareTo(ThirdPartyLevel o) {
    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;

    if (this.levelPriority < o.levelPriority) return -1;
    if (this.levelPriority > o.levelPriority) return 1;

    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThirdPartyLevel that = (ThirdPartyLevel) o;

    if (planVersionId != that.planVersionId) return false;
    return levelPriority == that.levelPriority;
  }

  @Override
  public int hashCode() {
    int result = planVersionId;
    result = 31 * result + levelPriority;
    return result;
  }

  @Override
  public String toString() {
    return "ThirdPartyLevel{" +
        "planVersionId=" + planVersionId +
        ", levelPriority=" + levelPriority +
        ", minAmount=" + minAmount +
        ", maxAmount=" + maxAmount +
        ", levelUnit=" + levelUnit +
        ", baseUnit=" + baseUnit +
        ", baseTimes=" + baseTimes +
        ", unitRatio=" + unitRatio +
        ", precision=" + precision +
        '}';
  }

  @Override
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

  public int getLevelPriority() {
    return levelPriority;
  }

  public void setLevelPriority(int levelPriority) {
    this.levelPriority = levelPriority;
  }

  public int getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(int minAmount) {
    this.minAmount = minAmount;
  }

  public int getMaxAmount() {
    return maxAmount;
  }

  public void setMaxAmount(int maxAmount) {
    this.maxAmount = maxAmount;
  }

  public int getLevelUnit() {
    return levelUnit;
  }

  public void setLevelUnit(int levelUnit) {
    this.levelUnit = levelUnit;
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

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
}
