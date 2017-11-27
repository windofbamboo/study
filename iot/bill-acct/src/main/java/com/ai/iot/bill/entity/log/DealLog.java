package com.ai.iot.bill.entity.log;

import java.io.Serializable;

/** 处理进度信息
 * Created by geyunfeng on 2017/9/15.
 */
public class DealLog implements Serializable {

  private static final long serialVersionUID = 9025052596278970962L;
  private long dealId;
  private int  totalNum;
  private int  dealNum;
  private int  sucessNum;
  private int  failNum;
  private int  ignoreNum;
  private long mqCreateTime;
  private long mqEndTime;
  private long updateTime;
  
  public DealLog(){
	  //do nothing
  }

  public DealLog(long dealId, int totalNum, int dealNum, int sucessNum, int failNum, int ignoreNum) {
    this.dealId = dealId;
    this.totalNum = totalNum;
    this.dealNum = dealNum;
    this.sucessNum = sucessNum;
    this.failNum = failNum;
    this.ignoreNum = ignoreNum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DealLog dealLog = (DealLog) o;

    return dealId == dealLog.dealId;

  }

  @Override
  public int hashCode() {
    return (int) (dealId ^ (dealId >>> 32));
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

  public int getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(int totalNum) {
    this.totalNum = totalNum;
  }

  public int getDealNum() {
    return dealNum;
  }

  public void setDealNum(int dealNum) {
    this.dealNum = dealNum;
  }

  public int getSucessNum() {
    return sucessNum;
  }

  public void setSucessNum(int sucessNum) {
    this.sucessNum = sucessNum;
  }

  public int getFailNum() {
    return failNum;
  }

  public void setFailNum(int failNum) {
    this.failNum = failNum;
  }

  public int getIgnoreNum() {
    return ignoreNum;
  }

  public void setIgnoreNum(int ignoreNum) {
    this.ignoreNum = ignoreNum;
  }

  public long getMqCreateTime() {
    return mqCreateTime;
  }

  public void setMqCreateTime(long mqCreateTime) {
    this.mqCreateTime = mqCreateTime;
  }

  public long getMqEndTime() {
    return mqEndTime;
  }

  public void setMqEndTime(long mqEndTime) {
    this.mqEndTime = mqEndTime;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }
}
