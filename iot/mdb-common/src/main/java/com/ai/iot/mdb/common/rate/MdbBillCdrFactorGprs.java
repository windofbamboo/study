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
 * 4要素累计量实体类
 * @author xue
 *
 */
public class MdbBillCdrFactorGprs implements Serializable, Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(MdbBillCdrFactorGprs.class);
	
	private static final long serialVersionUID = -8917671284953008512L;
	/**
	 * 订购实例
	 */
	private long ratePlanInsId;
	/**
	 * 日期
	 */
	private int day;
	/**
	 * 运营商
	 */
	private int operatorId;
	/**
	 * 忙闲时标记
	 */
	private String busyTimeFlag;
	/**
	 * apni
	 */
	private String apnni;
	/**
	 * rgId
	 */
	private String rgId;
	/**
	 * zoneId
	 */
	private int zoneId;
	/**
	 * 累计值
	 */
	private long value;

	public long getRatePlanInsId() {
		return ratePlanInsId;
	}

	public void setRatePlanInsId(long ratePlanInsId) {
		this.ratePlanInsId = ratePlanInsId;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public String getBusyTimeFlag() {
		return busyTimeFlag;
	}

	public void setBusyTimeFlag(String busyTimeFlag) {
		this.busyTimeFlag = busyTimeFlag;
	}

	public String getApnni() {
		return apnni;
	}

	public void setApnni(String apnni) {
		this.apnni = apnni;
	}

	public String getRgId() {
		return rgId;
	}

	public void setRgId(String rgId) {
		this.rgId = rgId;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
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
	public MdbBillCdrFactorGprs clone() {
		MdbBillCdrFactorGprs mdbBillCdrFactorGprs = null;
		try {
			mdbBillCdrFactorGprs = (MdbBillCdrFactorGprs) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillCdrFactorGprs;
	}
}
