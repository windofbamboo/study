package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;
import java.sql.Date;

/**账户追加账单
 * Created by geyunfeng on 2017/6/2.
 */
public class AcctBillAdd implements Serializable {

  private static final long serialVersionUID = -3047126464515808551L;
  private long seqId;        //账单ID
  private long acctId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private long addValues;
  private long addFee;
  private int basePlanId;
  private Date orderTime;

  public AcctBillAdd() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillAdd that = (AcctBillAdd) o;

    if (acctId != that.acctId) return false;
    if (planId != that.planId) return false;
    if (planVersionId != that.planVersionId) return false;
    return zoneId == that.zoneId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planId;
    result = 31 * result + planVersionId;
    result = 31 * result + zoneId;
    return result;
  }

  @Override
  public String toString() {
    return "AcctBillAdd{" +
        "seqId=" + seqId +
        ", acctId=" + acctId +
        ", cycleId=" + cycleId +
        ", planId=" + planId +
        ", planVersionId=" + planVersionId +
        ", zoneId=" + zoneId +
        ", addValues=" + addValues +
        ", addFee=" + addFee +
        ", basePlanId=" + basePlanId +
        ", orderTime=" + orderTime +
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

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
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

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public long getAddValues() {
    return addValues;
  }

  public void setAddValues(long addValues) {
    this.addValues = addValues;
  }

  public long getAddFee() {
    return addFee;
  }

  public void setAddFee(long addFee) {
    this.addFee = addFee;
  }

  public int getBasePlanId() {
    return basePlanId;
  }

  public void setBasePlanId(int basePlanId) {
    this.basePlanId = basePlanId;
  }

  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Date orderTime) {
    this.orderTime = orderTime;
  }
}
