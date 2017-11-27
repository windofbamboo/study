package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

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
 * @className:PoCycle
 * @description:
 * @author:xue
 * @date: 2017-7-27 9:37:31
 */
public class PoCycle extends PoBase implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @fields cycleId
	 */
	private int cycleId;

	/**
	 * @fields cycStartTime
	 */
	private Date cycStartTime;
	/**
	 * @fields cycHalfTime
	 */
	private Date cycHalfTime;
	/**
	 * @fields cycEndTime
	 */
	private Date cycEndTime;
	/**
	 * @fields monthAcctStatus
	 */
	private String monthAcctStatus;

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public Date getCycStartTime() {
		return cycStartTime;
	}

	public void setCycStartTime(Date cycStartTime) {
		this.cycStartTime = cycStartTime;
	}

	public Date getCycHalfTime() {
		return cycHalfTime;
	}

	public void setCycHalfTime(Date cycHalfTime) {
		this.cycHalfTime = cycHalfTime;
	}

	public Date getCycEndTime() {
		return cycEndTime;
	}

	public void setCycEndTime(Date cycEndTime) {
		this.cycEndTime = cycEndTime;
	}

	public String getMonthAcctStatus() {
		return monthAcctStatus;
	}

	public void setMonthAcctStatus(String monthAcctStatus) {
		this.monthAcctStatus = monthAcctStatus;
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
		cycleId = StringUtil.toInt(fields.get(0));
		cycStartTime = DateUtil.string2Date(fields.get(1));
		cycHalfTime = DateUtil.string2Date(fields.get(2));
		cycEndTime = DateUtil.string2Date(fields.get(3));
		monthAcctStatus = fields.get(4);

		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
        Map<String,Method> indexMethods=new HashMap<String,Method>();
        indexMethods.put(getIndex1Name(),getClass().getMethod(getIndex1Name()));
        return indexMethods;
	}

    public String getIndex1Key() {
        return String.valueOf(this.cycleId);
    }
    
	public static String getIndex1Name() {
		return "getIndex1Key";
	}

}
