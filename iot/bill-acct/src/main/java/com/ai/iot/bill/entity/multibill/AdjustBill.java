package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;
import java.sql.Date;

/**
 * 调账账单
 */
public class AdjustBill implements Serializable {

  private static final long serialVersionUID = 5113372331496431343L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int type;
  private int adjustType;
  private long adjustFee;
  private Date adjustTime;
  private String description;

  public AdjustBill() {
  }

  @Override
  public String toString() {
    return "AdjustBill{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", type=" + type +
        ", adjustType=" + adjustType +
        ", adjustFee=" + adjustFee +
        ", adjustTime=" + adjustTime +
        ", description='" + description + '\'' +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getSeqId() {
    return seqId;
  }

  public void setSeqId(long seqId) {
    this.seqId = seqId;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getAdjustType() {
    return adjustType;
  }

  public void setAdjustType(int adjustType) {
    this.adjustType = adjustType;
  }

  public long getAdjustFee() {
    return adjustFee;
  }

  public void setAdjustFee(long adjustFee) {
    this.adjustFee = adjustFee;
  }

  public Date getAdjustTime() {
    return adjustTime;
  }

  public void setAdjustTime(Date adjustTime) {
    this.adjustTime = adjustTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
