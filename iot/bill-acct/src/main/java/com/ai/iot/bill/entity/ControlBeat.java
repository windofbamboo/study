package com.ai.iot.bill.entity;

import java.io.Serializable;

/**
 * Created by geyunfeng on 2017/7/24.
 */
public class ControlBeat implements Serializable {

  public static final char BOLT_TYPE_ACCT_NONE   = 'A'; //AcctBolt,获取设备级资料后，向控制节点发送的信息，不触发账单级处理
  public static final char BOLT_TYPE_ACCT_BILL   = 'B'; //AcctBolt,无法获取设备级资料，向控制节点发送的信息，触发账单级处理
  public static final char BOLT_TYPE_DEVICE_BILL = 'D'; //DeviceBolt,处理完设备级账单后，向控制节点发送的信息，可能触发账单级处理

  private static final long serialVersionUID = -5223161403906596241L;

  private long dealId;
  private long acctId;
  private long seqId;
  private char boltType;

  public ControlBeat() {
  }

  public ControlBeat(long dealId, long acctId, long seqId, char boltType) {
    this.dealId = dealId;
    this.acctId = acctId;
    this.seqId = seqId;
    this.boltType = boltType;
  }

  @Override
  public String toString() {
    return "ControlBeat{" +
            "acctId=" + acctId +
            ", seqId=" + seqId +
            ", boltType=" + boltType +
            '}';
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

  public char getBoltType() {
    return boltType;
  }

  public void setBoltType(char boltType) {
    this.boltType = boltType;
  }
}
