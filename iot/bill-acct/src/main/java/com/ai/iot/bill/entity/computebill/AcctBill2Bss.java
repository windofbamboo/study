package com.ai.iot.bill.entity.computebill;

import java.io.Serializable;

/**送给bss的接口账单
 * Created by geyunfeng on 2017/8/4.
 */
public class AcctBill2Bss implements Serializable {

  private static final long serialVersionUID = -2859602400143488816L;
  private String cityCode; //归属地市
  private String provCode; //省分代码
  private String areaCode; //区号
  private String operAcctId; //运营商账户
  private long acctId;
  private int cycleId;    //帐单月份
  private int billType;   //帐单类型
  private int headquarterItemId; //总部帐目编码
  private int provItemId; //省分帐目编码
  private long fee;       //帐单费用
  private long originalFee; //原始费用
  private long discountFee; //优惠费用
  private int billStartDate; //计费起始时间
  private int billEndDate; //计费结束时间

  public AcctBill2Bss() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBill2Bss that = (AcctBill2Bss) o;

    if (acctId != that.acctId) return false;
    if (billType != that.billType) return false;
    if (headquarterItemId != that.headquarterItemId) return false;
    return provItemId == that.provItemId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + billType;
    result = 31 * result + headquarterItemId;
    result = 31 * result + provItemId;
    return result;
  }

  @Override
  public String toString() {
    return "AcctBill2Bss{" +
            "cityCode='" + cityCode + '\'' +
            ", provCode='" + provCode + '\'' +
            ", areaCode='" + areaCode + '\'' +
            ", operAcctId='" + operAcctId + '\'' +
            ", acctId=" + acctId +
            ", cycleId=" + cycleId +
            ", billType=" + billType +
            ", headquarterItemId=" + headquarterItemId +
            ", provItemId=" + provItemId +
            ", fee=" + fee +
            ", originalFee=" + originalFee +
            ", discountFee=" + discountFee +
            ", billStartDate=" + billStartDate +
            ", billEndDate=" + billEndDate +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  public String getProvCode() {
    return provCode;
  }

  public void setProvCode(String provCode) {
    this.provCode = provCode;
  }

  public String getAreaCode() {
    return areaCode;
  }

  public void setAreaCode(String areaCode) {
    this.areaCode = areaCode;
  }

  public String getOperAcctId() {
    return operAcctId;
  }

  public void setOperAcctId(String operAcctId) {
    this.operAcctId = operAcctId;
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

  public int getBillType() {
    return billType;
  }

  public void setBillType(int billType) {
    this.billType = billType;
  }

  public int getHeadquarterItemId() {
    return headquarterItemId;
  }

  public void setHeadquarterItemId(int headquarterItemId) {
    this.headquarterItemId = headquarterItemId;
  }

  public int getProvItemId() {
    return provItemId;
  }

  public void setProvItemId(int provItemId) {
    this.provItemId = provItemId;
  }

  public long getFee() {
    return fee;
  }

  public void setFee(long fee) {
    this.fee = fee;
  }

  public long getOriginalFee() {
    return originalFee;
  }

  public void setOriginalFee(long originalFee) {
    this.originalFee = originalFee;
  }

  public long getDiscountFee() {
    return discountFee;
  }

  public void setDiscountFee(long discountFee) {
    this.discountFee = discountFee;
  }

  public int getBillStartDate() {
    return billStartDate;
  }

  public void setBillStartDate(int billStartDate) {
    this.billStartDate = billStartDate;
  }

  public int getBillEndDate() {
    return billEndDate;
  }

  public void setBillEndDate(int billEndDate) {
    this.billEndDate = billEndDate;
  }


}
