package com.ai.iot.bill.entity;

import java.io.Serializable;

/**账户生成处理信息
 * Created by geyunfeng on 2017/9/15.
 */
public class DealAcct implements Serializable, Comparable<DealAcct> {

  private static final long serialVersionUID = -4975891290079091343L;
  private long dealId;
  private long acctId;
  private long seqId;

  public DealAcct(long dealId, long acctId, long seqId) {
    this.dealId = dealId;
    this.acctId = acctId;
    this.seqId = seqId;
  }

  @Override
  public String toString() {
    return "DealAcct{" +
            "dealId=" + dealId +
            ", acctId=" + acctId +
            ", seqId=" + seqId +
            '}';
  }

  @Override
  public int compareTo(DealAcct o) {
    if (this.dealId < o.dealId) return -1;
    if (this.dealId > o.dealId) return 1;

    if (this.acctId < o.acctId) return -1;
    if (this.acctId > o.acctId) return 1;
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DealAcct dealAcct = (DealAcct) o;

    if (dealId != dealAcct.dealId) return false;
    return acctId == dealAcct.acctId;

  }

  @Override
  public int hashCode() {
    int result = (int) (dealId ^ (dealId >>> 32));
    result = 31 * result + (int) (acctId ^ (acctId >>> 32));
    return result;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getDealId() {
    return dealId;
  }

  public void setDealId(long dealId) {
    this.dealId = dealId;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getSeqId() {
    return seqId;
  }

  public void setSeqId(long seqId) {
    this.seqId = seqId;
  }
}
