package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;

/**设备的订购费
 * Created by geyunfeng on 2017/6/19.
 */
public class DeviceBillOrder implements Serializable, DeviceBillInterface {

  private static final long serialVersionUID = 1037031399034056925L;

  private long acctId;
  private long deviceId;
  private int cycleId;
  private long tpInsId;
  private int planId;
  private int planVersionId;
  private int planType;
  private int itemId;
  private long fee;//订户费
  private long acctFee;//分摊的账户费
  private double orderNum; //订户数折算,在0-1之间 按百分比存放
  private int levelPriority;

  public DeviceBillOrder() {
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillOrder that = (DeviceBillOrder) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    if (tpInsId != that.tpInsId) return false;
    return itemId == that.itemId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (tpInsId ^ (tpInsId >>> 32));
    result = 31 * result + itemId;
    return result;
  }

  @Override
  public String toString() {
    return "DeviceBillOrder{" +
        "acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", tpInsId=" + tpInsId +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", planType=" + planType +
        ", itemId=" + itemId +
        ", fee=" + fee +
        ", acctFee=" + acctFee +
        ", orderNum=" + orderNum +
        ", levelPriority=" + levelPriority +
        '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }
  @Override
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
  @Override
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
  @Override
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

  public int getPlanType() {
    return planType;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
  }
  @Override
  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }
  @Override
  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public long getAcctFee() {
    return acctFee;
  }

  public void setAcctFee(long acctFee) {
    this.acctFee = acctFee;
  }

  public double getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(double orderNum) {
    this.orderNum = orderNum;
  }

  public int getLevelPriority() {
    return levelPriority;
  }

  public void setLevelPriority(int levelPriority) {
    this.levelPriority = levelPriority;
  }
}
