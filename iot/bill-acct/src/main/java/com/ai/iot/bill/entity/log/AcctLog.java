package com.ai.iot.bill.entity.log;

import java.io.Serializable;

/**账户处理日志信息
 * Created by geyunfeng on 2017/7/27.
 */
public class AcctLog implements Serializable {

  private static final long serialVersionUID = 7239288256171064556L;
  private long dealId;
  private long acctId;
  private int dealStage;
  private String dealTag;
  private String remark;
  private long acctStartTime;
  private long acctEndTime;
  private long billStartTime;
  private long billEndTime;
  private long updateTime;

  public AcctLog() {
  }

  public AcctLog(long dealId,long acctId, int dealStage, String dealTag) {
    this.dealId = dealId;
    this.acctId = acctId;
    this.dealStage = dealStage;
    this.dealTag = dealTag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctLog acctLog = (AcctLog) o;

    return acctId == acctLog.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctLog{" +
        "dealId=" + dealId +
        ", acctId=" + acctId +
        ", dealStage=" + dealStage +
        ", dealTag='" + dealTag + '\'' +
        ", remark='" + remark + '\'' +
        ", acctStartTime=" + acctStartTime +
        ", acctEndTime=" + acctEndTime +
        ", billStartTime=" + billStartTime +
        ", billEndTime=" + billEndTime +
        ", updateTime=" + updateTime +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getDealId() {
    return dealId;
  }

  public void setDealId(long dealId) {
    this.dealId = dealId;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getDealStage() {
    return dealStage;
  }

  public void setDealStage(int dealStage) {
    this.dealStage = dealStage;
  }

  public String getDealTag() {
    return dealTag;
  }

  public void setDealTag(String dealTag) {
    this.dealTag = dealTag;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public long getAcctStartTime() {
    return acctStartTime;
  }

  public void setAcctStartTime(long acctStartTime) {
    this.acctStartTime = acctStartTime;
  }

  public long getAcctEndTime() {
    return acctEndTime;
  }

  public void setAcctEndTime(long acctEndTime) {
    this.acctEndTime = acctEndTime;
  }

  public long getBillStartTime() {
    return billStartTime;
  }

  public void setBillStartTime(long billStartTime) {
    this.billStartTime = billStartTime;
  }

  public long getBillEndTime() {
    return billEndTime;
  }

  public void setBillEndTime(long billEndTime) {
    this.billEndTime = billEndTime;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }
}
