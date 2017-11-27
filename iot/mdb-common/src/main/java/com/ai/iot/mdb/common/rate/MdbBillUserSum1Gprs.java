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
 * 流量月累计量实体类
 * @author xue
 *
 */
public class MdbBillUserSum1Gprs implements Serializable, Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(MdbBillUserSum1Gprs.class);
	
	private static final long serialVersionUID = -6108171191985697513L;
	/**
	 * 订购实例
	 */
	long ratePlanInsId;
	/**
	 * 累计量ID
	 */
	int billId;
	/**
	 * day
	 */
	int day;
	/**
	 * 累计值
	 */
	long value;
	/**
	 * 规整值
	 */
	long roundValue;

	public MdbBillUserSum1Gprs() {
	}

	public MdbBillUserSum1Gprs(long ratePlanInsId, int billId, int day, long value, long roundValue) {
		this.ratePlanInsId = ratePlanInsId;
		this.billId = billId;
		this.day = day;
		this.value = value;
		this.roundValue = roundValue;
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
	public MdbBillUserSum1Gprs clone() {
		MdbBillUserSum1Gprs mdbBillUserSum1Gprs = null;
		try {
			mdbBillUserSum1Gprs = (MdbBillUserSum1Gprs) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillUserSum1Gprs;
	}
}
