package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**
 * 一次性费用+月费
 */
public class AcctMonthFeeBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = -6304205904455651896L;
  private long acctId;
  private int feeCode;
  private int feeType;
  private boolean billAble;
  private long fee;
  private long operateId;
  private String remark;
  private Date startTime;
  private Date endTime;


  public AcctMonthFeeBean() {
  }

  @Override
  public String toString() {
    return "AcctMonthFeeBean{" +
        "acctId=" + acctId +
        ", feeCode=" + feeCode +
        ", feeType=" + feeType +
        ", billAble=" + billAble +
        ", fee=" + fee +
        ", operateId=" + operateId +
        ", remark='" + remark + '\'' +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getFeeCode() {
    return feeCode;
  }

  public void setFeeCode(int feeCode) {
    this.feeCode = feeCode;
  }

  public int getFeeType() {
    return feeType;
  }

  public void setFeeType(int feeType) {
    this.feeType = feeType;
  }

  public boolean isBillAble() {
    return billAble;
  }

  public void setBillAble(boolean billAble) {
    this.billAble = billAble;
  }

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public long getOperateId() {
    return operateId;
  }

  public void setOperateId(long operateId) {
    this.operateId = operateId;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
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
