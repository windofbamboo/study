package com.ai.iot.mdb.common.rate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 流量非按月累计量实体类
 *
 * @author xue
 */
public class MdbBillUserSum2Gprs implements Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(MdbBillUserSum2Gprs.class);

  private static final long serialVersionUID = -9222127456069346602L;
  /**
   * 账期
   */
  int cycleId;
  /**
   * 订购实例
   */
  long ratePlanInsId;
  /**
   * billId
   */
  int billId;
  /**
   * day
   */
  int day;
  /**
   * 基本值
   */
  long baseValue;
  /**
   * 往月累计值
   */
  long value;
  /**
   * 规整值
   */
  long roundValue;

  public MdbBillUserSum2Gprs() {
  }

  public MdbBillUserSum2Gprs(int cycleId, long ratePlanInsId, int billId, int day, long baseValue, long value, long roundValue) {
    this.cycleId = cycleId;
    this.ratePlanInsId = ratePlanInsId;
    this.billId = billId;
    this.day = day;
    this.baseValue = baseValue;
    this.value = value;
    this.roundValue = roundValue;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public long getRatePlanInsId() {
    return ratePlanInsId;
  }

  public void setRatePlanInsId(long ratePlanInsId) {
    this.ratePlanInsId = ratePlanInsId;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public long getBaseValue() {
    return baseValue;
  }

  public void setBaseValue(long baseValue) {
    this.baseValue = baseValue;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public long getRoundValue() {
    return roundValue;
  }

  public void setRoundValue(long roundValue) {
    this.roundValue = roundValue;
  }

  /*
   * (非 Javadoc) <p>Title: </p>
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /*
   * (非 Javadoc) <p>Title: </p>
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /*
   * (非 Javadoc) <p>Title: </p>
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
  }

  @Override
  public MdbBillUserSum2Gprs clone() {
    MdbBillUserSum2Gprs mdbBillUserSum2Gprs = null;
    try {
      mdbBillUserSum2Gprs = (MdbBillUserSum2Gprs) super.clone();
    } catch (CloneNotSupportedException e) {
      logger.error(Arrays.toString(e.getStackTrace()));
    }
    return mdbBillUserSum2Gprs;
  }

  /**
   * 累积量求和
   *
   * @return
   */
  public long getCurrentUsage() {
    return baseValue + roundValue;
  }
}
