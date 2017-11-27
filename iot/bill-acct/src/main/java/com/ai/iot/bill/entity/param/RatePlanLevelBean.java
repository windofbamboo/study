package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**资费计划的档次 TD_B_RATE_PLAN_LEVEL
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanLevelBean implements Serializable, IdInterface, Comparable<RatePlanLevelBean> {

  private static final long serialVersionUID = 5226117132277154915L;
  private int levelId;
  private int levelPriority;
  private int minAmount;
  private int maxAmount;
  private long subscriberCharge;


  public RatePlanLevelBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanLevelBean that = (RatePlanLevelBean) o;

    if (levelId != that.levelId) return false;
    return levelPriority == that.levelPriority;

  }

  public int compareTo(RatePlanLevelBean o) {
    if (this.levelId < o.levelId) return -1;
    if (this.levelId > o.levelId) return 1;

    if (this.levelPriority < o.levelPriority) return -1;
    if (this.levelPriority > o.levelPriority) return 1;
    return 0;
  }


  @Override
  public int hashCode() {
    int result = levelId;
    result = 31 * result + levelPriority;
    return result;
  }

  @Override
  public String toString() {
    return "RatePlanLevel{" +
            "levelId=" + levelId +
            ", levelPriority=" + levelPriority +
            ", minAmount=" + minAmount +
            ", maxAmount=" + maxAmount +
            ", subscriberCharge=" + subscriberCharge +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getId() {
    return levelId;
  }

  public int getLevelId() {
    return levelId;
  }

  public void setLevelId(int levelId) {
    this.levelId = levelId;
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

  public long getSubscriberCharge() {
    return subscriberCharge;
  }

  public void setSubscriberCharge(long subscriberCharge) {
    this.subscriberCharge = subscriberCharge;
  }
}
