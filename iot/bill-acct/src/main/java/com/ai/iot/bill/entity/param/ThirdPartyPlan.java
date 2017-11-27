package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**第三方计费 TD_B_THIRD_PARTY_PLAN
 * Created by geyunfeng on 2017/8/4.
 */
public class ThirdPartyPlan implements Serializable, CycleInterface, Comparable<ThirdPartyPlan> {

  private static final long serialVersionUID = -5314179058318267859L;
  private int planId;
  private int startCycleId;
  private int endCycleId;
  private long payAcctId;

  public ThirdPartyPlan() {
  }

  @Override
  public int compareTo(ThirdPartyPlan o) {
    if (this.planId < o.planId) return -1;
    if (this.planId > o.planId) return 1;

    if (this.startCycleId < o.startCycleId) return -1;
    if (this.startCycleId > o.startCycleId) return 1;

    if (this.payAcctId < o.payAcctId) return -1;
    if (this.payAcctId > o.payAcctId) return 1;
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThirdPartyPlan that = (ThirdPartyPlan) o;

    if (planId != that.planId) return false;
    if (startCycleId != that.startCycleId) return false;
    return payAcctId == that.payAcctId;

  }

  @Override
  public int hashCode() {
    int result = planId;
    result = 31 * result + startCycleId;
    result = 31 * result + (int) (payAcctId ^ (payAcctId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "ThirdPartyPlan{" +
            "planId=" + planId +
            ", startCycleId=" + startCycleId +
            ", endCycleId=" + endCycleId +
            ", payAcctId=" + payAcctId +
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

  @Override
  public int getStartCycleId() {
    return startCycleId;
  }

  public void setStartCycleId(int startCycleId) {
    this.startCycleId = startCycleId;
  }

  @Override
  public int getEndCycleId() {
    return endCycleId;
  }

  public void setEndCycleId(int endCycleId) {
    this.endCycleId = endCycleId;
  }

  public long getPayAcctId() {
    return payAcctId;
  }

  public void setPayAcctId(long payAcctId) {
    this.payAcctId = payAcctId;
  }
}
