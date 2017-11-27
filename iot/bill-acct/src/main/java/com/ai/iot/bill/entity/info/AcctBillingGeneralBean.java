package com.ai.iot.bill.entity.info;

import java.io.Serializable;

/**账户设置
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctBillingGeneralBean implements Serializable {

  private static final long serialVersionUID = 4122837246643400419L;
  private long acctId;
  private boolean billAble;
  private String activationProration; //不足月收费方式（激活）
  private String renewalProration;    //不足月收费方式（续约）
  private long defaultActivationPlan; //默认激活计划
  private long defaultActivationFee;  //默认激活费用

  public AcctBillingGeneralBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillingGeneralBean that = (AcctBillingGeneralBean) o;

    return acctId == that.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctBillingGeneral{" +
            "acctId=" + acctId +
            ", billAble=" + billAble +
            ", activationProration=" + activationProration +
            ", renewalProration=" + renewalProration +
            ", defaultActivationPlan=" + defaultActivationPlan +
            ", defaultActivationFee=" + defaultActivationFee +
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

  public boolean isBillAble() {
    return billAble;
  }

  public void setBillAble(boolean billAble) {
    this.billAble = billAble;
  }

  public String getActivationProration() {
    return activationProration;
  }

  public void setActivationProration(String activationProration) {
    this.activationProration = activationProration;
  }

  public String getRenewalProration() {
    return renewalProration;
  }

  public void setRenewalProration(String renewalProration) {
    this.renewalProration = renewalProration;
  }

  public long getDefaultActivationPlan() {
    return defaultActivationPlan;
  }

  public void setDefaultActivationPlan(long defaultActivationPlan) {
    this.defaultActivationPlan = defaultActivationPlan;
  }

  public long getDefaultActivationFee() {
    return defaultActivationFee;
  }

  public void setDefaultActivationFee(long defaultActivationFee) {
    this.defaultActivationFee = defaultActivationFee;
  }
}
