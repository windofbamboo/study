package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;
import java.sql.Date;

/**
 * 设备的激活费用的账单
 * Created by geyunfeng on 2017/6/23.
 */
public class DeviceBillActivation implements Serializable, DeviceBillInterface {

  private static final long serialVersionUID = -2565623194341588534L;
  private long acctId;
  private long deviceId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private Date activationTime;
  private int itemId;
  private long fee;
  private int activationType;

  public DeviceBillActivation() {
  }

  public DeviceBillActivation(long acctId, long deviceId, int cycleId, int planId, int planVersionId, Date activationTime, int itemId, long fee, int activationType) {
    this.acctId = acctId;
    this.deviceId = deviceId;
    this.cycleId = cycleId;
    this.planId = planId;
    this.planVersionId = planVersionId;
    this.activationTime = activationTime;
    this.itemId = itemId;
    this.fee = fee;
    this.activationType = activationType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillActivation that = (DeviceBillActivation) o;

    if (acctId != that.acctId) return false;
    return deviceId == that.deviceId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "DeviceBillActivation{" +
            "acctId=" + acctId +
            ", deviceId=" + deviceId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", activationTime=" + activationTime +
            ", itemId=" + itemId +
            ", fee=" + fee +
            ", activationType=" + activationType +
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

  public Date getActivationTime() {
    return activationTime;
  }

  public void setActivationTime(Date activationTime) {
    this.activationTime = activationTime;
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

  public int getActivationType() {
    return activationType;
  }

  public void setActivationType(int activationType) {
    this.activationType = activationType;
  }
}
