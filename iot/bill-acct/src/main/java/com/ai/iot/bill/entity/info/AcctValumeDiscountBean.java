package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**账户折扣
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctValumeDiscountBean implements Serializable {

  private static final long serialVersionUID = -633235976486335601L;
  private long acctId;
  private boolean applicationDiscount; //是否应用折扣
  private int version;
  private boolean isGrade; //是否分档
  private int gradeBaseOn; //档次划分依据
  private boolean orderEffectFlag;  /*订购费是否打折*/
  private boolean dataEffectFlag;   /*流量费是否打折*/
  private boolean smsEffectFlag;    /*短信费是否打折*/
  private boolean voiceEffectFlag;  /*语音费是否打折*/
  private boolean activeEffectFlag; /*激活费用是否打折*/
  private boolean otherEffectFlag;  /*其它费用是否打折*/
  private Date updateTime;

  public AcctValumeDiscountBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctValumeDiscountBean that = (AcctValumeDiscountBean) o;

    return acctId == that.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctValumeDiscountBean{" +
            "acctId=" + acctId +
            ", applicationDiscount=" + applicationDiscount +
            ", version=" + version +
            ", isGrade=" + isGrade +
            ", orderEffectFlag=" + orderEffectFlag +
            ", dataEffectFlag=" + dataEffectFlag +
            ", smsEffectFlag=" + smsEffectFlag +
            ", voiceEffectFlag=" + voiceEffectFlag +
            ", activeEffectFlag=" + activeEffectFlag +
            ", otherEffectFlag=" + otherEffectFlag +
            ", updateTime=" + updateTime +
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

  public boolean isApplicationDiscount() {
    return applicationDiscount;
  }

  public void setApplicationDiscount(boolean applicationDiscount) {
    this.applicationDiscount = applicationDiscount;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public boolean isGrade() {
    return isGrade;
  }

  public void setGrade(boolean grade) {
    isGrade = grade;
  }

  public int getGradeBaseOn() {
    return gradeBaseOn;
  }

  public void setGradeBaseOn(int gradeBaseOn) {
    this.gradeBaseOn = gradeBaseOn;
  }

  public boolean isOrderEffectFlag() {
    return orderEffectFlag;
  }

  public void setOrderEffectFlag(boolean orderEffectFlag) {
    this.orderEffectFlag = orderEffectFlag;
  }

  public boolean isDataEffectFlag() {
    return dataEffectFlag;
  }

  public void setDataEffectFlag(boolean dataEffectFlag) {
    this.dataEffectFlag = dataEffectFlag;
  }

  public boolean isSmsEffectFlag() {
    return smsEffectFlag;
  }

  public void setSmsEffectFlag(boolean smsEffectFlag) {
    this.smsEffectFlag = smsEffectFlag;
  }

  public boolean isVoiceEffectFlag() {
    return voiceEffectFlag;
  }

  public void setVoiceEffectFlag(boolean voiceEffectFlag) {
    this.voiceEffectFlag = voiceEffectFlag;
  }

  public boolean isActiveEffectFlag() {
    return activeEffectFlag;
  }

  public void setActiveEffectFlag(boolean activeEffectFlag) {
    this.activeEffectFlag = activeEffectFlag;
  }

  public boolean isOtherEffectFlag() {
    return otherEffectFlag;
  }

  public void setOtherEffectFlag(boolean otherEffectFlag) {
    this.otherEffectFlag = otherEffectFlag;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }
}
