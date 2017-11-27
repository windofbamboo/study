package com.ai.iot.bill.entity.usage;

import java.io.Serializable;

/**
 * 第三方计费的账户累积量
 * Created by geyunfeng on 2017/6/2.
 */
public class UsedAddThirdParty implements Serializable {

  private static final long serialVersionUID = -161964251879128371L;
  private long acctId;
  private int zoneId;
  private long currValue;   //规整后的当月累计值
  private long bulkAdjust;  //批量超额

  public UsedAddThirdParty() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UsedAddThirdParty that = (UsedAddThirdParty) o;

    if (acctId != that.acctId) return false;
    return zoneId == that.zoneId;
  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + zoneId;
    return result;
  }

  @Override
  public String toString() {
    return "UsedAddThirdParty{" +
            "acctId=" + acctId +
            ", zoneId=" + zoneId +
            ", currValue=" + currValue +
            ", bulkAdjust=" + bulkAdjust +
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

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public long getCurrValue() {
    return currValue;
  }

  public void setCurrValue(long currValue) {
    this.currValue = currValue;
  }

  public long getBulkAdjust() {
    return bulkAdjust;
  }

  public void setBulkAdjust(long bulkAdjust) {
    this.bulkAdjust = bulkAdjust;
  }
}
