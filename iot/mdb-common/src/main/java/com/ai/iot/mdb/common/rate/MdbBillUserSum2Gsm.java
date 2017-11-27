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
 * 语音非按月累计量实体类
 *
 * @author xue
 */
public class MdbBillUserSum2Gsm implements Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(MdbBillUserSum2Gsm.class);

  private static final long serialVersionUID = -6748076241811769576L;
  /**
   * 账期
   */
  private int cycleId;
  /**
   * 订购实例
   */
  private long ratePlanInsId;
  /**
   * 累计量ID
   */
  private int billId;
  /**
   * 日期
   */
  private int day;
  /**
   * 往月累计值
   */
  private long baseValue;
  /**
   * 累计量值
   */
  private long value;
  /**
   * 规整值
   */
  private long roundValue;
  /**
   * mo累计值
   */
  private long moValue;
  /**
   * mt累计值
   */
  private long mtValue;

  public MdbBillUserSum2Gsm() {
  }

  public MdbBillUserSum2Gsm(int cycleId, long ratePlanInsId, int billId, int day, long baseValue, long value, long roundValue, long moValue, long mtValue) {
    this.cycleId = cycleId;
    this.ratePlanInsId = ratePlanInsId;
    this.billId = billId;
    this.day = day;
    this.baseValue = baseValue;
    this.value = value;
    this.roundValue = roundValue;
    this.moValue = moValue;
    this.mtValue = mtValue;
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

  public long getMoValue() {
    return moValue;
  }

  public void setMoValue(long moValue) {
    this.moValue = moValue;
  }

  public long getMtValue() {
    return mtValue;
  }

  public void setMtValue(long mtValue) {
    this.mtValue = mtValue;
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
  public MdbBillUserSum2Gsm clone() {
    MdbBillUserSum2Gsm mdbBillUserSum2Gsm = null;
    try {
      mdbBillUserSum2Gsm = (MdbBillUserSum2Gsm) super.clone();
    } catch (CloneNotSupportedException e) {
      logger.error(Arrays.toString(e.getStackTrace()));
    }
    return mdbBillUserSum2Gsm;
  }

  public long getCurrentUsage() {
    return baseValue + roundValue;
  }
}
