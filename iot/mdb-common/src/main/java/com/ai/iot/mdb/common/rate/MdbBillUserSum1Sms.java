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
 * 短信月累计量实体类
 * @author xue
 *
 */
public class MdbBillUserSum1Sms implements Serializable, Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(MdbBillUserSum1Sms.class);
	
	private static final long serialVersionUID = 7883469504880674289L;
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

	public MdbBillUserSum1Sms() {
	}

	public MdbBillUserSum1Sms(long ratePlanInsId, int billId, int day, long value, long roundValue, long moValue, long mtValue) {
		this.ratePlanInsId = ratePlanInsId;
		this.billId = billId;
		this.day = day;
		this.value = value;
		this.roundValue = roundValue;
		this.moValue = moValue;
		this.mtValue = mtValue;
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
	public MdbBillUserSum1Sms clone() {
		MdbBillUserSum1Sms mdbBillUserSum1Sms = null;
		try {
			mdbBillUserSum1Sms = (MdbBillUserSum1Sms) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillUserSum1Sms;
	}
}
