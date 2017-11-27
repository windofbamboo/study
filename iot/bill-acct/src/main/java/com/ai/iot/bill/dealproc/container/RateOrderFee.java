package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.entity.param.RatePlanLevelBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**订户费的参数
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class RateOrderFee implements Serializable {

  private static final long serialVersionUID = 4568197975067444336L;
  private int planVersionId;
  private int planType;
  private int paymentType;
  private long accountCharge;
  private int subscriberChargeFerquency;
  private long subscriberCharge;
  private int levelId;
  private List<RatePlanLevelBean> ratePlanLevelBeanList = new ArrayList<>();

  public RateOrderFee() {
  }

  public RateOrderFee(int planVersionId, int planType, int paymentType, long accountCharge, int subscriberChargeFerquency, long subscriberCharge, int levelId) {
    this.planVersionId = planVersionId;
    this.planType = planType;
    this.paymentType = paymentType;
    this.accountCharge = accountCharge;
    this.subscriberChargeFerquency = subscriberChargeFerquency;
    this.subscriberCharge = subscriberCharge;
    this.levelId = levelId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RateOrderFee that = (RateOrderFee) o;

    return planVersionId == that.planVersionId;

  }

  @Override
  public int hashCode() {
    return planVersionId;
  }

  @Override
  public String toString() {
    return "RateOrderFee{" +
            "planVersionId=" + planVersionId +
            ", planType=" + planType +
            ", accountCharge=" + accountCharge +
            ", subscriberChargeFerquency=" + subscriberChargeFerquency +
            ", subscriberCharge=" + subscriberCharge +
            ", levelId=" + levelId +
            ", ratePlanLevelList=" + ratePlanLevelBeanList +
            '}';
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

  public List<RatePlanLevelBean> getRatePlanLevelBeanList() {
    return ratePlanLevelBeanList;
  }

  public void setRatePlanLevelBeanList(List<RatePlanLevelBean> ratePlanLevelBeanList) {
    this.ratePlanLevelBeanList = ratePlanLevelBeanList;
  }
}
