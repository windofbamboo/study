package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**设备的资费订购信息
 * Created by geyunfeng on 2017/6/6.
 */
public class DeviceRatePlanBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = -1520371377032642904L;
  private long deviceId;
  private long orderId;
  private int planType;
  private int planId;
  private int planVersionId;
  private String activeFlag;
  private String state;
  private Date startTime;
  private Date endTime;
  private long poolId;

  public DeviceRatePlanBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeviceRatePlanBean that = (DeviceRatePlanBean) o;

    if (deviceId != that.deviceId) return false;
    return orderId == that.orderId;

  }

  @Override
  public int hashCode() {
    int result = (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + (int) (orderId ^ (orderId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "DeviceRatePlanBean{" +
            "deviceId=" + deviceId +
            ", orderId=" + orderId +
            ", planType=" + planType +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", activeFlag='" + activeFlag + '\'' +
            ", state='" + state + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", poolId=" + poolId +
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

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public int getPlanType() {
    return planType;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
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

  public String getActiveFlag() {
    return activeFlag;
  }

  public void setActiveFlag(String activeFlag) {
    this.activeFlag = activeFlag;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
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

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
  }
}
