package com.ai.iot.bill.cb.base.entity;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * CRM通知类
 * @author xue
 *
 */
public class CrmNotifyInfo {

	/**
	 * 事件类型
	 */
	private int eventType;
	
	/**
	 * 账户ID
	 */
	private long acctId;
	
	/**
	 * 设备ID
	 */
	private long deviceId;
	
	/**
	 * 订购实例
	 */
	private long ratePlanInsId;
	
	/**
	 * 池ID
	 */
	private long pooId;
	
	/**
	 * 归属省代码
	 */
	private String serviceProviderCode;
	
	/**
	 * 话单开始日期
	 */
	private String beginDateTime;

	/**
	 * 使用标记:1、正好用完，2、超出
	 */
	private int usedFlag;
	
	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public long getAcctId() {
		return acctId;
	}

	public void setAcctId(long acctId) {
		this.acctId = acctId;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public long getRatePlanInsId() {
		return ratePlanInsId;
	}

	public void setRatePlanInsId(long ratePlanInsId) {
		this.ratePlanInsId = ratePlanInsId;
	}

	public long getPooId() {
		return pooId;
	}

	public void setPooId(long pooId) {
		this.pooId = pooId;
	}

	public String getServiceProviderCode() {
		return serviceProviderCode;
	}

	public void setServiceProviderCode(String serviceProviderCode) {
		this.serviceProviderCode = serviceProviderCode;
	}

	public String getBeginDateTime() {
		return beginDateTime;
	}

	public void setBeginDateTime(String beginDateTime) {
		this.beginDateTime = beginDateTime;
	}

	public int getUsedFlag() {
		return usedFlag;
	}

	public void setUsedFlag(int usedFlag) {
		this.usedFlag = usedFlag;
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
