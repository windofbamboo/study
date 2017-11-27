package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**资费组成员
 * Created by geyunfeng on 2017/7/28.
 */
public class AcctRateDiscountMemberBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = -2871410797536309267L;
  private int rateGroup;          //资费组ID
  private int planId;
  private Date startTime;
  private Date endTime;

  public AcctRateDiscountMemberBean() {
  }

  @Override
  public String toString() {
    return "AcctRateDiscountMemberBean{" +
            "rateGroup=" + rateGroup +
            ", planId=" + planId +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getRateGroup() {
    return rateGroup;
  }

  public void setRateGroup(int rateGroup) {
    this.rateGroup = rateGroup;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  @Override
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
