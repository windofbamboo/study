package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;

/**账户汇总账单
 * Created by geyunfeng on 2017/6/2.
 */
public class BillAcct implements Serializable {

  private static final long serialVersionUID = -1735289207261239537L;
  private long acctId;
  private int cycleId;
  private int itemId;
  private long fee;

  public BillAcct() {
  }

  public BillAcct(long acctId, int cycleId, int itemId, long fee) {
    this.acctId = acctId;
    this.cycleId = cycleId;
    this.itemId = itemId;
    this.fee = fee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BillAcct billAcct = (BillAcct) o;

    if (acctId != billAcct.acctId) return false;
    return itemId == billAcct.itemId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + itemId;
    return result;
  }

  @Override
  public String toString() {
    return "BillAcct{" +
            "acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", itemId=" + itemId +
            ", fee=" + fee +
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

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }
}
