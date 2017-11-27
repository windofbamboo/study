package com.ai.iot.bill.entity.param;

import java.io.Serializable;
import java.sql.Date;

/**账期表 TD_B_CYCLE
 * Created by geyunfeng on 2017/6/14.
 */
public class CycleBean implements Serializable, Comparable<CycleBean> {

  private static final long serialVersionUID = 300770325368449390L;
  private int cycleId;
  private Date cycStartTime;
  private Date cycHalfTime;
  private Date cycEndTime;
  private int monthAcctStatus;
  private long sTime;
  private long hTime;
  private long eTime;

  public CycleBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CycleBean cycleBean = (CycleBean) o;

    return cycleId == cycleBean.cycleId;

  }

  public int compareTo(CycleBean o) {
    if (this.cycleId < o.cycleId) return -1;
    if (this.cycleId > o.cycleId) return 1;

    return 0;
  }

  @Override
  public int hashCode() {
    return cycleId;
  }

  @Override
  public String toString() {
    return "CycleBean{" +
            "cycleId=" + cycleId +
            ", cycStartTime=" + cycStartTime +
            ", cycHalfTime=" + cycHalfTime +
            ", cycEndTime=" + cycEndTime +
            ", monthAcctStatus=" + monthAcctStatus +
            ", sTime=" + sTime +
            ", hTime=" + hTime +
            ", eTime=" + eTime +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public Date getCycStartTime() {
    return cycStartTime;
  }

  public void setCycStartTime(Date cycStartTime) {
    this.cycStartTime = cycStartTime;
  }

  public Date getCycHalfTime() {
    return cycHalfTime;
  }

  public void setCycHalfTime(Date cycHalfTime) {
    this.cycHalfTime = cycHalfTime;
  }

  public Date getCycEndTime() {
    return cycEndTime;
  }

  public void setCycEndTime(Date cycEndTime) {
    this.cycEndTime = cycEndTime;
  }

  public int getMonthAcctStatus() {
    return monthAcctStatus;
  }

  public void setMonthAcctStatus(int monthAcctStatus) {
    this.monthAcctStatus = monthAcctStatus;
  }

  public long getsTime() {
    return sTime;
  }

  public void setsTime(long sTime) {
    this.sTime = sTime;
  }

  public long gethTime() {
    return hTime;
  }

  public void sethTime(long hTime) {
    this.hTime = hTime;
  }

  public long geteTime() {
    return eTime;
  }

  public void seteTime(long eTime) {
    this.eTime = eTime;
  }
}
