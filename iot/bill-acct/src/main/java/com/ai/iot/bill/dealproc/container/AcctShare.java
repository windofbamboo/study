package com.ai.iot.bill.dealproc.container;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/** 共享类资料
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctShare implements Serializable,Comparable<AcctShare> {

  private static final long serialVersionUID = -6576696071071249549L;
  private long acctId;
  private long poolId;
  private int planId;
  private int planVersionId;
  private Date startTime;
  private Date endTime;
  private List<RateBill> rateBillList;

  public AcctShare() {
  }

  public AcctShare(long acctId, long poolId, int planId, int planVersionId, Date startTime, Date endTime) {
    this.acctId = acctId;
    this.poolId = poolId;
    this.planId = planId;
    this.planVersionId = planVersionId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.rateBillList = new ArrayList<>();
  }

  public AcctShare(long acctId, long poolId, int planVersionId) {
    this.acctId = acctId;
    this.poolId = poolId;
    this.planVersionId = planVersionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctShare acctShare = (AcctShare) o;

    if (acctId != acctShare.acctId) return false;
    if (poolId != acctShare.poolId) return false;
    return planVersionId == acctShare.planVersionId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (poolId ^ (poolId >>> 32));
    result = 31 * result + planVersionId;
    return result;
  }

  public int compareTo(AcctShare o) {
    if (this.acctId < o.acctId) return -1;
    if (this.acctId > o.acctId) return 1;

    if (this.poolId < o.poolId) return -1;
    if (this.poolId > o.poolId) return 1;

    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;
    return 0;
  }

  @Override
  public String toString() {
    return "AcctShare{" +
            "acctId=" + acctId +
            ", poolId=" + poolId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", rateBillList=" + rateBillList +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
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

  public List<RateBill> getRateBillList() {
    return rateBillList;
  }

  public void setRateBillList(List<RateBill> rateBillList) {
    this.rateBillList = rateBillList;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
