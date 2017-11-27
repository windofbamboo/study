package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;

/**
 * 资费的账户费,追加资费
 * Created by geyunfeng on 2017/6/20.
 */
public class AcctBillOrder implements Serializable, Comparable<AcctBillOrder> {

  private static final long serialVersionUID = 2065742365494562461L;

  private long acctId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int itemId;
  private long acctFee;

  public AcctBillOrder() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillOrder that = (AcctBillOrder) o;

    if (acctId != that.acctId) return false;
    if (planId != that.planId) return false;
    return planVersionId == that.planVersionId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planId;
    result = 31 * result + planVersionId;
    return result;
  }

  public int compareTo(AcctBillOrder o) {
    if (this.acctId < o.acctId) return -1;
    if (this.acctId > o.acctId) return 1;

    if (this.planId < o.planId) return -1;
    if (this.planId > o.planId) return 1;

    if (this.planVersionId < o.planVersionId) return -1;
    if (this.planVersionId > o.planVersionId) return 1;
    return 0;
  }

  @Override
  public String toString() {
    return "AcctBillOrder{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", itemId=" + itemId +
            ", acctFee=" + acctFee +
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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public long getAcctFee() {
    return acctFee;
  }

  public void setAcctFee(long acctFee) {
    this.acctFee = acctFee;
  }
}
