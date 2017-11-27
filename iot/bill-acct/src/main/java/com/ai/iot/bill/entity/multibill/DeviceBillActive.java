package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;
import java.sql.Date;

/**激活账单
 * Created by geyunfeng on 2017/6/2.
 */
public class DeviceBillActive implements Serializable {

  private static final long serialVersionUID = -214833790806725689L;
  private long seqId;        //账单ID
  private long acctId;
  private long deviceId;
  private int cycleId;
  private Date activeTime;
  private long activeCharge;
  private int activeType;

  public DeviceBillActive() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBillActive that = (DeviceBillActive) o;

    if (acctId != that.acctId) return false;
    if (deviceId != that.deviceId) return false;
    return cycleId == that.cycleId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + cycleId;
    return result;
  }

  @Override
  public String toString() {
    return "DeviceBillActive{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", deviceId=" + deviceId +
        ", cycleId=" + cycleId +
        ", activeTime=" + activeTime +
        ", activeCharge=" + activeCharge +
        ", activeType=" + activeType +
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

  public Date getActiveTime() {
    return activeTime;
  }

  public void setActiveTime(Date activeTime) {
    this.activeTime = activeTime;
  }

  public long getActiveCharge() {
    return activeCharge;
  }

  public void setActiveCharge(long activeCharge) {
    this.activeCharge = activeCharge;
  }

  public int getActiveType() {
    return activeType;
  }

  public void setActiveType(int activeType) {
    this.activeType = activeType;
  }
}
