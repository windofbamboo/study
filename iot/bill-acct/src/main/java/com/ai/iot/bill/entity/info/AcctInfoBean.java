package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.sql.Date;

/**账户基本信息
 * Created by geyunfeng on 2017/6/6.
 */
public class AcctInfoBean implements Serializable {

  private static final long serialVersionUID = 5424312138274252036L;
  private long acctId;
  private String operAcctId;
  private String isPayBack;
  private String status;
  private String acctSegment;
  private String provinceCode;
  private String cityCode;
  private String removeTag;
  private Date removeTime;


  public AcctInfoBean() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctInfoBean acctInfoBean = (AcctInfoBean) o;

    return acctId == acctInfoBean.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctInfoBean{" +
            "acctId=" + acctId +
            ", operAcctId=" + operAcctId +
            ", isPayBack='" + isPayBack + '\'' +
            ", status='" + status + '\'' +
            ", acctSegment='" + acctSegment + '\'' +
            ", provinceCode='" + provinceCode + '\'' +
            ", cityCode='" + cityCode + '\'' +
            ", removeTag='" + removeTag + '\'' +
            ", removeTime=" + removeTime +
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

  public String getOperAcctId() {
    return operAcctId;
  }

  public void setOperAcctId(String operAcctId) {
    this.operAcctId = operAcctId;
  }

  public String getIsPayBack() {
    return isPayBack;
  }

  public void setIsPayBack(String isPayBack) {
    this.isPayBack = isPayBack;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAcctSegment() {
    return acctSegment;
  }

  public void setAcctSegment(String acctSegment) {
    this.acctSegment = acctSegment;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  public String getRemoveTag() {
    return removeTag;
  }

  public void setRemoveTag(String removeTag) {
    this.removeTag = removeTag;
  }

  public Date getRemoveTime() {
    return removeTime;
  }

  public void setRemoveTime(Date removeTime) {
    this.removeTime = removeTime;
  }
}
