package com.ai.iot.bill.common.paramcachemanager.pog.grant.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.common.util.StringUtil;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * Copyright (C) aisainfo
 *
 * @className:PoGrantRule
 * @description:
 * @version:v1.0.0
 * @author:xue
 * @date: 2017-8-1 15:59:21
 */
public class PoGrantRule extends PoBase implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields ruleId :
	 */
	private int ruleId;
	/**
	 * @Fields rulePriority :
	 */
	private int rulePriority;
	/**
	 * @Fields providerId :
	 */
	private int providerId;
	/**
	 * @Fields apnId :
	 */
	private long apnId;
	/**
	 * @Fields rgId :
	 */
	private String rgId;
	/**
	 * @Fields limitValue :
	 */
	private long limitValue;
	/**
	 * @Fields grantDataMin :
	 */
	private long grantDataMin;
	/**
	 * @Fields grantDataMax :
	 */
	private long grantDataMax;
	/**
	 * @Fields grantDurationMin :
	 */
	private int grantDurationMin;
	/**
	 * @Fields grantDurationMax :
	 */
	private int grantDurationMax;
	/**
	 * @Fields startDate :
	 */
	private Date startDate;
	/**
	 * @Fields endDate :
	 */
	private Date endDate;

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getRuleId() {
		return this.ruleId;
	}
	
	public int getRulePriority() {
		return rulePriority;
	}

	public void setRulePriority(int rulePriority) {
		this.rulePriority = rulePriority;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public long getApnId() {
		return apnId;
	}

	public void setApnId(long apnId) {
		this.apnId = apnId;
	}

	public String getRgId() {
		return rgId;
	}

	public void setRgId(String rgId) {
		this.rgId = rgId;
	}

	public void setDataValue(long dataValue) {
		this.limitValue = dataValue;
	}

	public long getDataValue() {
		return this.limitValue;
	}

	public void setGrantDataMin(long grantDataMin) {
		this.grantDataMin = grantDataMin;
	}

	public long getGrantDataMin() {
		return this.grantDataMin;
	}

	public void setGrantDataMax(long grantDataMax) {
		this.grantDataMax = grantDataMax;
	}

	public long getGrantDataMax() {
		return this.grantDataMax;
	}

	public void setGrantDurationMin(int grantDurationMin) {
		this.grantDurationMin = grantDurationMin;
	}

	public int getGrantDurationMin() {
		return this.grantDurationMin;
	}

	public void setGrantDurationMax(int grantDurationMax) {
		this.grantDurationMax = grantDurationMax;
	}

	public int getGrantDurationMax() {
		return this.grantDurationMax;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return this.endDate;
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
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		ruleId = StringUtil.toInt(fields.get(0));
		rulePriority = StringUtil.toInt(fields.get(1));
		providerId = StringUtil.toInt(fields.get(2));
		apnId = StringUtil.toLong(fields.get(3));
		rgId = fields.get(4);
		limitValue = StringUtil.toLong(fields.get(5));
		grantDataMin = StringUtil.toLong(fields.get(6));
		grantDataMax = StringUtil.toLong(fields.get(7));
		grantDurationMin = StringUtil.toInt(fields.get(8));
		grantDurationMax = StringUtil.toInt(fields.get(9));
		startDate = DateUtil.string2Date(fields.get(10));
		endDate = DateUtil.string2Date(fields.get(11));
		
		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_GRANT.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String, Method> indexMethods = new HashMap<String, Method>();
		indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.ruleId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

}
