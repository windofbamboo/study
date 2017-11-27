package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**资费组
 * Created by geyunfeng on 2017/6/21.
 */
public class AcctRateDiscountBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = -2871410797536309267L;
  private long acctId;
  private int rateGroup;          //资费组ID
  private double chiefDiscountRate;  //总监折扣比例
  private int gradeType;          //分档类别
  private int gradeDiscountId;    //分档折扣ID
  private Date startTime;
  private Date endTime;

  public AcctRateDiscountBean() {
  }

  @Override
  public String toString() {
    return "AcctRateDiscountBean{" +
            "acctId=" + acctId +
            ", rateGroup=" + rateGroup +
            ", chiefDiscountRate=" + chiefDiscountRate +
            ", gradeType=" + gradeType +
            ", gradeDiscountId=" + gradeDiscountId +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getRateGroup() {
    return rateGroup;
  }

  public void setRateGroup(int rateGroup) {
    this.rateGroup = rateGroup;
  }

  public double getChiefDiscountRate() {
    return chiefDiscountRate;
  }

  public void setChiefDiscountRate(double chiefDiscountRate) {
    this.chiefDiscountRate = chiefDiscountRate;
  }

  public int getGradeType() {
    return gradeType;
  }

  public void setGradeType(int gradeType) {
    this.gradeType = gradeType;
  }

  public int getGradeDiscountId() {
    return gradeDiscountId;
  }

  public void setGradeDiscountId(int gradeDiscountId) {
    this.gradeDiscountId = gradeDiscountId;
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
