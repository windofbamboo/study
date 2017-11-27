package com.ai.iot.mdb.common.rate;

import java.io.Serializable;
import java.util.Date;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * 周期内总用量实体类
 * @author xue
 *
 */
public class MdbBillStatGprs implements Serializable {

	private static final long serialVersionUID = 6140952405849239771L;

	/**
	 * 账期
	 */
	private int cycleId;
	/**
	 * 已达限额的资费计划订购实例id
	 */
	private long limitedRatePlanInsId;
	/**
	 * 周期内总用量
	 */
	private long totalAmount;
	/**
	 * 周期内事件总用量
	 */
	private long eventAmount;
	/**
	 * 最后连接时间（14位）
	 */
	private Date lastTime;
	/**
	 * 当前sim卡在线状态
	 */
	private String currentStatus;
	/**
	 * 上次的设备串号
	 */
	private String lastImei;

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public long getLimitedRatePlanInsId() {
		return limitedRatePlanInsId;
	}

	public void setLimitedRatePlanInsId(long limitedRatePlanInsId) {
		this.limitedRatePlanInsId = limitedRatePlanInsId;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getEventAmount() {
		return eventAmount;
	}

	public void setEventAmount(long eventAmount) {
		this.eventAmount = eventAmount;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getLastImei() {
		return lastImei;
	}

	public void setLastImei(String lastImei) {
		this.lastImei = lastImei;
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
}
