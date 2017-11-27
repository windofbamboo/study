package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;

/**账户的超额使用费
 * Created by geyunfeng on 2017/6/20.
 */
public class AcctBillUsage implements Serializable,BillUsageInterface {

  private static final long serialVersionUID = 4909847973493293341L;

  private long acctId;
  private int cycleId;
  private int planId;
  private int planVersionId;
  private int zoneId;
  private int groupId;
  private int billId;
  private int itemId;
  private long fee;
  private int bizType;
  private boolean isRoam;

  public AcctBillUsage() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillUsage that = (AcctBillUsage) o;

    if (acctId != that.acctId) return false;
    if (planVersionId != that.planVersionId) return false;
    if (zoneId != that.zoneId) return false;
    if (groupId != that.groupId) return false;
    return billId == that.billId;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + planVersionId;
    result = 31 * result + zoneId;
    result = 31 * result + groupId;
    result = 31 * result + billId;
    return result;
  }

  @Override
  public String toString() {
    return "AcctBillUsage{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", planId=" + planId +
            ", planVersionId=" + planVersionId +
            ", zoneId=" + zoneId +
            ", groupId=" + groupId +
            ", billId=" + billId +
            ", itemId=" + itemId +
            ", fee=" + fee +
            ", bizType=" + bizType +
            ", isRoam=" + isRoam +
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

  @Override
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

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

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

  @Override
  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  @Override
  public boolean isRoam() {
    return isRoam;
  }

  public void setRoam(boolean roam) {
    isRoam = roam;
  }
}
