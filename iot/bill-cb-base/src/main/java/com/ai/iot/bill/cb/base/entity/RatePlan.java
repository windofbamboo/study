package com.ai.iot.bill.cb.base.entity;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

public class RatePlan {

	/**
	 * 资费计划版本ID
	 */
	private int planVersionId;
	
	/**
	 * 资费计划类型
	 */
	private int planType;
	
	/**
	 * 累计量ID
	 */
	private int billId;
	
	/**
	 * 业务类型
	 */
	private int bizType;
	
	/**
	 * 区域ID
	 */
	private int zoneId;
	
	/**
	 * 目标计费组ID
	 */
	private int groupId;
	
	/**
	 * 呼叫类型
	 */
	private int callType;
	
	/**
	 * 是否限量
	 */
	private String limitTag;
	
	/**
	 * 是否共享
	 */
	private String shared;

	/**
	 * 区域用量限额
	 */
	private long zoneUsageLimit;
	
	/**
	 * 计划内用量
	 */
	private long inlcudedValueUsage;
	
	/**
	 * 问题限额
	 */
	private long usageLimit;
	
	/**
	 * 舍入单位
	 */
	private long roundingUnit;
	
	/**
	 * 舍入频率
	 */
	private int roundingFreq;

	public int getPlanVersionId() {
		return planVersionId;
	}

	public void setPlanVersionId(int planVersionId) {
		this.planVersionId = planVersionId;
	}

	public int getPlanType() {
		return planType;
	}

	public void setPlanType(int planType) {
		this.planType = planType;
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public String getLimitTag() {
		return limitTag;
	}

	public void setLimitTag(String limitTag) {
		this.limitTag = limitTag;
	}

	public String getShared() {
		return shared;
	}

	public void setShared(String shared) {
		this.shared = shared;
	}

	public long getZoneUsageLimit() {
		return zoneUsageLimit;
	}

	public void setZoneUsageLimit(long zoneUsageLimit) {
		this.zoneUsageLimit = zoneUsageLimit;
	}

	public long getUsageLimit() {
		return usageLimit;
	}

	public void setUsageLimit(long usageLimit) {
		this.usageLimit = usageLimit;
	}

	public long getInlcudedValueUsage() {
		return inlcudedValueUsage;
	}

	public void setInlcudedValueUsage(long inlcudedValueUsage) {
		this.inlcudedValueUsage = inlcudedValueUsage;
	}

	public long getRoundingUnit() {
		return roundingUnit;
	}

	public void setRoundingUnit(long roundingUnit) {
		this.roundingUnit = roundingUnit;
	}

	public int getRoundingFreq() {
		return roundingFreq;
	}

	public void setRoundingFreq(int roundingFreq) {
		this.roundingFreq = roundingFreq;
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
