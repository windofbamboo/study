package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**账户最低消费/最低订户设置
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctPromiseBean implements Serializable, TimeInterface {

  private static final long serialVersionUID = -7512454082210599852L;
  private long acctId;
  private int activeGracePeriod;
  private long minimumActivationTerm; //最低消费
  private long miniSubs; //最低订户
  private long chargePerSub; //对每个不足额订户的收费
  private Date startTime;
  private Date endTime;

  public AcctPromiseBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctPromiseBean that = (AcctPromiseBean) o;

    if (acctId != that.acctId) return false;
    return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AcctPromise{" +
            "acctId=" + acctId +
            ", activeGracePeriod=" + activeGracePeriod +
            ", minimumActivationTerm=" + minimumActivationTerm +
            ", miniSubs=" + miniSubs +
            ", chargePerSub=" + chargePerSub +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
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

  public int getActiveGracePeriod() {
    return activeGracePeriod;
  }

  public void setActiveGracePeriod(int activeGracePeriod) {
    this.activeGracePeriod = activeGracePeriod;
  }

  public long getMinimumActivationTerm() {
    return minimumActivationTerm;
  }

  public void setMinimumActivationTerm(long minimumActivationTerm) {
    this.minimumActivationTerm = minimumActivationTerm;
  }

  public long getMiniSubs() {
    return miniSubs;
  }

  public void setMiniSubs(long miniSubs) {
    this.miniSubs = miniSubs;
  }

  public long getChargePerSub() {
    return chargePerSub;
  }

  public void setChargePerSub(long chargePerSub) {
    this.chargePerSub = chargePerSub;
  }

  @Override
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
