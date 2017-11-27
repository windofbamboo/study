package com.ai.iot.bill.entity.usage;

import java.io.Serializable;

/**
 * 设备的累积量
 * Created by geyunfeng on 2017/6/2.
 */
public class UsedAddDevice implements Serializable,BulkAdjustInterface {

  private static final long serialVersionUID = 3164320879838408447L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private int billId;
  private long currValue;   //规整后的当月累计值(不包括往月值在内)
  private long roundAdjust; //每日调整
  private long bulkAdjust;  //批量超额
  private long lastValue;   //规整后的往月累计值
  private long upperValue;  //核减后超出的用量(计算时使用)
  private long moValue;     //mo当月累计值(只针对短信、语音)
  private long mtValue;     //mt当月累计值(只针对短信、语音)
  private int bizType;      //累积量的类型
  private int planId;
  private int planVersionId;
  private boolean shareTag;  //是否为共享

  public UsedAddDevice() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UsedAddDevice that = (UsedAddDevice) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (tpInsId != that.tpInsId) return false;
    return billId == that.billId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "UsedAddDevice{" +
        "acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", tpInsId=" + tpInsId +
        ", billId=" + billId +
        ", currValue=" + currValue +
        ", roundAdjust=" + roundAdjust +
        ", bulkAdjust=" + bulkAdjust +
        ", lastValue=" + lastValue +
        ", upperValue=" + upperValue +
        ", moValue=" + moValue +
        ", mtValue=" + mtValue +
        ", bizType=" + bizType +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", shareTag=" + shareTag +
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

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public long getTpInsId() {
    return tpInsId;
  }

  public void setTpInsId(long tpInsId) {
    this.tpInsId = tpInsId;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public long getCurrValue() {
    return currValue;
  }

  public void setCurrValue(long currValue) {
    this.currValue = currValue;
  }

  public long getRoundAdjust() {
    return roundAdjust;
  }

  public void setRoundAdjust(long roundAdjust) {
    this.roundAdjust = roundAdjust;
  }

  public long getBulkAdjust() {
    return bulkAdjust;
  }

  public void setBulkAdjust(long bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }

  public long getLastValue() {
    return lastValue;
  }

  public void setLastValue(long lastValue) {
    this.lastValue = lastValue;
  }

  public long getUpperValue() {
    return upperValue;
  }

  public void setUpperValue(long upperValue) {
    this.upperValue = upperValue;
  }

  public long getMoValue() {
    return moValue;
  }

  public void setMoValue(long moValue) {
    this.moValue = moValue;
  }

  public long getMtValue() {
    return mtValue;
  }

  public void setMtValue(long mtValue) {
    this.mtValue = mtValue;
  }

  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public boolean shareTag() {
    return shareTag;
  }

  public void setShareTag(boolean shareTag) {
    this.shareTag = shareTag;
  }
}
