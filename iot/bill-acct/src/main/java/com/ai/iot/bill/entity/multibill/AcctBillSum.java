package com.ai.iot.bill.entity.multibill;

import java.io.Serializable;
import java.sql.Date;

/**账户汇总账单
 * Created by geyunfeng on 2017/6/2.
 */
public class AcctBillSum implements Serializable {

  private static final long serialVersionUID = -944035632912317062L;
  private long seqId;        //账单ID
  private long acctId;        //账户ID
  private String operAcctId;    //运营商账户ID
  private Date billDate;       //账单日期
  private Date lastPayDate;    //截止日期
  private int cycleId;        //账期
  private String rateTag;       //计费
  private String publishTag;    //发布标志
  private int rateDevices;    //设备数
  private int nomalDevices;   //活跃订户
  private long gprsValue;     //流量
  private long smsValue;      //短信总量
  private long voiceValue;    //通话
  private long serviceCharge; //服务收入
  private int discountRate;   //折扣率
  private long orderCharge;   //订购费
  private long gprsCharge;    //流量费
  private long smsCharge;     //短信费
  private long voiceCharge;   //通话费
  private long avtivationCharge;//激活费
  private long otherCharge;   //其它费
  private long discountCharge;//折扣金额
  private long totalCharge;   //总费用
  private long standardCharge;//标准超额
  private long standardRoamCharge;//标准漫游
  private int events;             //事件数
  private long eventGprsValue;  //事件流量
  private long eventCharge;     //事件费用
  private long platSmsValue;      //平台下发短信数量
  private long platSmsCharge;     //平台下发短信费用
  private long platSmsLevel;     //平台下发短信批价等级


  public AcctBillSum() {
  }

  public AcctBillSum(long acctId, String operAcctId, Date billDate, Date lastPayDate, int cycleId, String rateTag, String publishTag, int rateDevices, int nomalDevices, long gprsValue, long smsValue, long voiceValue, long serviceCharge, int discountRate, long orderCharge, long gprsCharge, long smsCharge, long voiceCharge, long avtivationCharge, long otherCharge, long discountCharge, long totalCharge, long standardCharge, long standardRoamCharge) {
    this.acctId = acctId;
    this.operAcctId = operAcctId;
    this.billDate = billDate;
    this.lastPayDate = lastPayDate;
    this.cycleId = cycleId;
    this.rateTag = rateTag;
    this.publishTag = publishTag;
    this.rateDevices = rateDevices;
    this.nomalDevices = nomalDevices;
    this.gprsValue = gprsValue;
    this.smsValue = smsValue;
    this.voiceValue = voiceValue;
    this.serviceCharge = serviceCharge;
    this.discountRate = discountRate;
    this.orderCharge = orderCharge;
    this.gprsCharge = gprsCharge;
    this.smsCharge = smsCharge;
    this.voiceCharge = voiceCharge;
    this.avtivationCharge = avtivationCharge;
    this.otherCharge = otherCharge;
    this.discountCharge = discountCharge;
    this.totalCharge = totalCharge;
    this.standardCharge = standardCharge;
    this.standardRoamCharge = standardRoamCharge;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcctBillSum that = (AcctBillSum) o;

    return acctId == that.acctId;

  }

  @Override
  public int hashCode() {
    return (int) (acctId ^ (acctId >>> 32));
  }

  @Override
  public String toString() {
    return "AcctBillSum{" +
            "seqId=" + seqId +
            ", acctId=" + acctId +
            ", operAcctId=" + operAcctId +
            ", billDate=" + billDate +
            ", lastPayDate=" + lastPayDate +
            ", cycleId=" + cycleId +
            ", rateTag='" + rateTag + '\'' +
            ", publishTag='" + publishTag + '\'' +
            ", rateDevices=" + rateDevices +
            ", nomalDevices=" + nomalDevices +
            ", gprsValue=" + gprsValue +
            ", smsValue=" + smsValue +
            ", voiceValue=" + voiceValue +
            ", serviceCharge=" + serviceCharge +
            ", discountRate=" + discountRate +
            ", orderCharge=" + orderCharge +
            ", gprsCharge=" + gprsCharge +
            ", smsCharge=" + smsCharge +
            ", voiceCharge=" + voiceCharge +
            ", avtivationCharge=" + avtivationCharge +
            ", otherCharge=" + otherCharge +
            ", discountCharge=" + discountCharge +
            ", totalCharge=" + totalCharge +
            ", standardCharge=" + standardCharge +
            ", standardRoamCharge=" + standardRoamCharge +
            ", events=" + events +
            ", eventGprsValue=" + eventGprsValue +
            ", eventCharge=" + eventCharge +
            ", platSmsValue=" + platSmsValue +
            ", platSmsCharge=" + platSmsCharge +
            ", platSmsLevel=" + platSmsLevel +
            '}';
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getSeqId() {
    return seqId;
  }

  public void setSeqId(long seqId) {
    this.seqId = seqId;
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

  public Date getBillDate() {
    return billDate;
  }

  public void setBillDate(Date billDate) {
    this.billDate = billDate;
  }

  public Date getLastPayDate() {
    return lastPayDate;
  }

  public void setLastPayDate(Date lastPayDate) {
    this.lastPayDate = lastPayDate;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public String getRateTag() {
    return rateTag;
  }

  public void setRateTag(String rateTag) {
    this.rateTag = rateTag;
  }

  public String getPublishTag() {
    return publishTag;
  }

  public void setPublishTag(String publishTag) {
    this.publishTag = publishTag;
  }

  public int getRateDevices() {
    return rateDevices;
  }

  public void setRateDevices(int rateDevices) {
    this.rateDevices = rateDevices;
  }

  public int getNomalDevices() {
    return nomalDevices;
  }

  public void setNomalDevices(int nomalDevices) {
    this.nomalDevices = nomalDevices;
  }

  public long getGprsValue() {
    return gprsValue;
  }

  public void setGprsValue(long gprsValue) {
    this.gprsValue = gprsValue;
  }

  public long getSmsValue() {
    return smsValue;
  }

  public void setSmsValue(long smsValue) {
    this.smsValue = smsValue;
  }

  public long getVoiceValue() {
    return voiceValue;
  }

  public void setVoiceValue(long voiceValue) {
    this.voiceValue = voiceValue;
  }

  public long getServiceCharge() {
    return serviceCharge;
  }

  public void setServiceCharge(long serviceCharge) {
    this.serviceCharge = serviceCharge;
  }

  public int getDiscountRate() {
    return discountRate;
  }

  public void setDiscountRate(int discountRate) {
    this.discountRate = discountRate;
  }

  public long getOrderCharge() {
    return orderCharge;
  }

  public void setOrderCharge(long orderCharge) {
    this.orderCharge = orderCharge;
  }

  public long getGprsCharge() {
    return gprsCharge;
  }

  public void setGprsCharge(long gprsCharge) {
    this.gprsCharge = gprsCharge;
  }

  public long getSmsCharge() {
    return smsCharge;
  }

  public void setSmsCharge(long smsCharge) {
    this.smsCharge = smsCharge;
  }

  public long getVoiceCharge() {
    return voiceCharge;
  }

  public void setVoiceCharge(long voiceCharge) {
    this.voiceCharge = voiceCharge;
  }

  public long getAvtivationCharge() {
    return avtivationCharge;
  }

  public void setAvtivationCharge(long avtivationCharge) {
    this.avtivationCharge = avtivationCharge;
  }

  public long getOtherCharge() {
    return otherCharge;
  }

  public void setOtherCharge(long otherCharge) {
    this.otherCharge = otherCharge;
  }

  public long getDiscountCharge() {
    return discountCharge;
  }

  public void setDiscountCharge(long discountCharge) {
    this.discountCharge = discountCharge;
  }

  public long getTotalCharge() {
    return totalCharge;
  }

  public void setTotalCharge(long totalCharge) {
    this.totalCharge = totalCharge;
  }

  public long getStandardCharge() {
    return standardCharge;
  }

  public void setStandardCharge(long standardCharge) {
    this.standardCharge = standardCharge;
  }

  public long getStandardRoamCharge() {
    return standardRoamCharge;
  }

  public void setStandardRoamCharge(long standardRoamCharge) {
    this.standardRoamCharge = standardRoamCharge;
  }

  public int getEvents() {
    return events;
  }

  public void setEvents(int events) {
    this.events = events;
  }

  public long getEventGprsValue() {
    return eventGprsValue;
  }

  public void setEventGprsValue(long eventGprsValue) {
    this.eventGprsValue = eventGprsValue;
  }

  public long getEventCharge() {
    return eventCharge;
  }

  public void setEventCharge(long eventCharge) {
    this.eventCharge = eventCharge;
  }

  public long getPlatSmsValue() {
    return platSmsValue;
  }

  public void setPlatSmsValue(long platSmsValue) {
    this.platSmsValue = platSmsValue;
  }

  public long getPlatSmsCharge() {
    return platSmsCharge;
  }

  public void setPlatSmsCharge(long platSmsCharge) {
    this.platSmsCharge = platSmsCharge;
  }

  public long getPlatSmsLevel() {
    return platSmsLevel;
  }

  public void setPlatSmsLevel(long platSmsLevel) {
    this.platSmsLevel = platSmsLevel;
  }
}
