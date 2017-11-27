package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**追加资费
 * Created by geyunfeng on 2017/6/21.
 */
public class AppendFeepolicyBean implements Serializable,TimeInterface {

  private static final long serialVersionUID = -2871410797536309267L;
  private long acctId;
  private int appendPlanVersionId;
  private int planVersionId;
  private Date startTime;
  private Date endTime;

  public AppendFeepolicyBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AppendFeepolicyBean that = (AppendFeepolicyBean) o;

    if (acctId != that.acctId) return false;
    if (appendPlanVersionId != that.appendPlanVersionId) return false;
    if (planVersionId != that.planVersionId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + appendPlanVersionId;
    result = 31 * result + planVersionId;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AppendFeepolicy{" +
            "acctId=" + acctId +
            ", appendPlanVersionId=" + appendPlanVersionId +
            ", planVersionId=" + planVersionId +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
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

  public int getAppendPlanVersionId() {
    return appendPlanVersionId;
  }

  public void setAppendPlanVersionId(int appendPlanVersionId) {
    this.appendPlanVersionId = appendPlanVersionId;
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
