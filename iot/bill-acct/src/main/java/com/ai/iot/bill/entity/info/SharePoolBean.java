package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**
 * 预付固定共享池
 */
public class SharePoolBean implements Serializable,TimeInterface   {

  private static final long serialVersionUID = -2728097893926007869L;
  private long poolId;
  private long acctId;
  private int planVersionId;
  private Date startTime;
  private Date endTime;

  public SharePoolBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SharePoolBean that = (SharePoolBean) o;

    if (poolId != that.poolId) return false;
    if (acctId != that.acctId) return false;
    if (planVersionId != that.planVersionId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (poolId ^ (poolId >>> 32));
    result = 31 * result + (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SharePoolBean{" +
            "poolId=" + poolId +
            ", acctId=" + acctId +
            ", planVersionId=" + planVersionId +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
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
