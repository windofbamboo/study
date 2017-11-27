package com.ai.iot.bill.entity.param;

import java.io.Serializable;

/**累积量信息 TD_B_BILLID
 * Created by geyunfeng on 2017/6/20.
 */
public class AddupIdBean implements Serializable, Comparable<AddupIdBean> {

  private static final long serialVersionUID = -2035648228498720768L;
  private int billId;
  private int bizType;
  private int zoneId;
  private int groupId;
  private int callType;

  public AddupIdBean() {
  }

  public AddupIdBean(int billId) {
    this.billId = billId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AddupIdBean addupIdBean = (AddupIdBean) o;

    return billId == addupIdBean.billId;

  }

  public int compareTo(AddupIdBean o) {
    if (this.billId < o.billId) return -1;
    if (this.billId > o.billId) return 1;

    return 0;
  }

  @Override
  public int hashCode() {
    return billId;
  }

  @Override
  public String toString() {
    return "AddupId{" +
            "billId=" + billId +
            ", bizType=" + bizType +
            ", zoneId=" + zoneId +
            ", groupId=" + groupId +
            ", callType=" + callType +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getCallType() {
    return callType;
  }

  public void setCallType(int callType) {
    this.callType = callType;
  }
}
