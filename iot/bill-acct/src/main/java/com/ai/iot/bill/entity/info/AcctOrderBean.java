package com.ai.iot.bill.entity.info;

import java.io.Serializable;

/**
 * Created by geyunfeng on 2017/7/28.
 */
public class AcctOrderBean implements Serializable {

  private static final long serialVersionUID = 2840793930581926421L;
  private long acctId;
  private long orderId;
  private String opnCode;

  public AcctOrderBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctOrderBean acctOrderBean = (AcctOrderBean) o;

    if (acctId != acctOrderBean.acctId) return false;
    return orderId == acctOrderBean.orderId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (orderId ^ (orderId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "OrderBean{" +
            "acctId=" + acctId +
            ", orderId=" + orderId +
            ", opnCode='" + opnCode + '\'' +
            '}';
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public String getOpnCode() {
    return opnCode;
  }

  public void setOpnCode(String opnCode) {
    this.opnCode = opnCode;
  }
}
