package com.ai.iot.mdb.common.rate;

import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * 池累计量实体类
 * @author xue
 *
 */
public class MdbBillPoolSum2Gprs implements Serializable, Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(MdbBillPoolSum2Gprs.class);
	
	private static final long serialVersionUID = 3975435352492241329L;

	/**
	 * 账期
	 */
	private int cycleId;
	/**
	 * 版本ID
	 */
	private long ratePlanVersionId;
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
	 * 累计值
	 */
	private long value;
	/**
	 * 规整值
	 */
	private long roundValue;

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public long getRatePlanVersionId() {
		return ratePlanVersionId;
	}

	public void setRatePlanVersionId(long ratePlanVersionId) {
		this.ratePlanVersionId = ratePlanVersionId;
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
	public MdbBillPoolSum2Gprs clone() {
		MdbBillPoolSum2Gprs mdbBillPoolSum2Gprs = null;
		try {
			mdbBillPoolSum2Gprs = (MdbBillPoolSum2Gprs) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillPoolSum2Gprs;
	}
}
