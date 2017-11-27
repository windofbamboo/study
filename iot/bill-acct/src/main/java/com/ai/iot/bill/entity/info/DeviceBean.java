package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**设备信息
 * Created by geyunfeng on 2017/6/6.
 */
public class DeviceBean implements Serializable {

  private static final long serialVersionUID = 7455785104049054623L;
  private long deviceId;
  private long acctId;
  private long subAcctId;
  private Date activationTime;
  private Date shippedTime;

  public DeviceBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceBean deviceBean = (DeviceBean) o;

    return deviceId == deviceBean.deviceId;

  }

  @Override
  public int hashCode() {
    return (int) (deviceId ^ (deviceId >>> 32));
  }

  @Override
  public String toString() {
    return "DeviceBean{" +
            "deviceId=" + deviceId +
            ", acctId=" + acctId +
            ", subAcctId=" + subAcctId +
            ", activationTime=" + activationTime +
            ", shippedTime=" + shippedTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public Date getActivationTime() {
    return activationTime;
  }

  public void setActivationTime(Date activationTime) {
    this.activationTime = activationTime;
  }

  public Date getShippedTime() {
    return shippedTime;
  }

  public void setShippedTime(Date shippedTime) {
    this.shippedTime = shippedTime;
  }

  public long getSubAcctId() {
    return subAcctId;
  }

  public void setSubAcctId(long subAcctId) {
    this.subAcctId = subAcctId;
  }
}
