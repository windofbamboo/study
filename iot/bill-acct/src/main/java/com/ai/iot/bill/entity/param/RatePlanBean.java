package com.ai.iot.bill.entity.param;

import com.ai.iot.bill.common.util.IdInterface;

import java.io.Serializable;

/**资费计划 TD_B_RATE_PLAN
 * Created by geyunfeng on 2017/6/6.
 */
public class RatePlanBean implements Serializable, IdInterface, Comparable<RatePlanBean> {

  private static final long serialVersionUID = 2610370816709234819L;
  private int planId;
  private int planType;
  private int paymentType;


  public RatePlanBean() {
  }

  public RatePlanBean(int planId) {
    this.planId = planId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatePlanBean ratePlanBean = (RatePlanBean) o;

    return planId == ratePlanBean.planId;

  }

  public int compareTo(RatePlanBean o) {
    if (this.planId < o.planId) return -1;
    if (this.planId > o.planId) return 1;

    return 0;
  }

  @Override
  public int hashCode() {
    return planId;
  }

  @Override
  public String toString() {
    return "RatePlan{" +
            "planId=" + planId +
            ", planType=" + planType +
            ", paymentType=" + paymentType +
            '}';
  }

  public int getId() {
    return planId;
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
}
