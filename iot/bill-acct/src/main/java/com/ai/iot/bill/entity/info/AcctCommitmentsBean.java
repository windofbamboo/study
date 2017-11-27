package com.ai.iot.bill.entity.info;

import java.io.Serializable;

/**账户承诺
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctCommitmentsBean implements Serializable {

  private static final long serialVersionUID = -8028563033621946099L;
  private long acctId;
  private int activationGracePeriod;         //激活宽限天数
  private int minActivationMonth;            //最小激活期限(月)
  private boolean billActivationGracePeriod; //激活宽限期收费标志
  private boolean billMininumActivationTerm; //最短激活期收费标志

  public AcctCommitmentsBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctCommitmentsBean that = (AcctCommitmentsBean) o;

    return acctId == that.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctCommitments{" +
            "acctId=" + acctId +
            ", activationGracePeriod=" + activationGracePeriod +
            ", minActivationMonth=" + minActivationMonth +
            ", billActivationGracePeriod=" + billActivationGracePeriod +
            ", billMininumActivationTerm=" + billMininumActivationTerm +
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

  public int getActivationGracePeriod() {
    return activationGracePeriod;
  }

  public void setActivationGracePeriod(int activationGracePeriod) {
    this.activationGracePeriod = activationGracePeriod;
  }

  public int getMinActivationMonth() {
    return minActivationMonth;
  }

  public void setMinActivationMonth(int minActivationMonth) {
    this.minActivationMonth = minActivationMonth;
  }

  public boolean isBillActivationGracePeriod() {
    return billActivationGracePeriod;
  }

  public void setBillActivationGracePeriod(boolean billActivationGracePeriod) {
    this.billActivationGracePeriod = billActivationGracePeriod;
  }

  public boolean isBillMininumActivationTerm() {
    return billMininumActivationTerm;
  }

  public void setBillMininumActivationTerm(boolean billMininumActivationTerm) {
    this.billMininumActivationTerm = billMininumActivationTerm;
  }
}
