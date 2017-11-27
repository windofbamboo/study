package com.ai.iot.bill.entity.info;

import java.io.Serializable;

/**账前调账表记录
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctAdjustBeforeBean implements Serializable {

  private static final long serialVersionUID = 3751422449367720453L;
  private long acctId;
  private long fee;
  private String remark;
  private int startCycId;
  private int endCycId;

  public AcctAdjustBeforeBean() {
  }

  @Override
  public String toString() {
    return "AcctAdjustBeforeBean{" +
        "acctId=" + acctId +
        ", fee=" + fee +
        ", remark='" + remark + '\'' +
        ", startCycId=" + startCycId +
        ", endCycId=" + endCycId +
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

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public int getStartCycId() {
    return startCycId;
  }

  public void setStartCycId(int startCycId) {
    this.startCycId = startCycId;
  }

  public int getEndCycId() {
    return endCycId;
  }

  public void setEndCycId(int endCycId) {
    this.endCycId = endCycId;
  }
}
