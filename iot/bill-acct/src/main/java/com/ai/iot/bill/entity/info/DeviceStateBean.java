package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**设备的状态
 * Created by geyunfeng on 2017/6/6.
 */
public class DeviceStateBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = 4672314784491325518L;
  private long deviceId;
  private String stateCode;
  private Date startTime;
  private Date endTime;


  public DeviceStateBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceStateBean that = (DeviceStateBean) o;

    if (deviceId != that.deviceId) return false;
    if (stateCode != null ? !stateCode.equals(that.stateCode) : that.stateCode != null) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (stateCode != null ? stateCode.hashCode() : 0);
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "DeviceState{" +
            "deviceId=" + deviceId +
            ", stateCode=" + stateCode +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
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

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
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
