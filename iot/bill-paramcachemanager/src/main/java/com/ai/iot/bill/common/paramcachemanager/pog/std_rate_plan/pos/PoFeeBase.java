package com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.StringUtil;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * Copyright (C) aisainfo
 *
 * @className:PoFeeBase
 * @description:
 * @author:xue
 * @date: 2017-7-24 14:24:46
 */
public class PoFeeBase extends PoBase implements Serializable{
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields planId :
	 */
	private int planId;
	/**
	 * @Fields priority :
	 */
	private int priority;
	/**
	 * @Fields imsi : IMSI的前缀
	 */
	private String imsi;
	/**
	 * @Fields billId :
	 */
	private int billId;
	/**
	 * @Fields baseUnit : 0:k 1:m 2:G 3:T 对于前台界面，当枚举值不为0时，显示为批量超额额度
	 * 
	 */
	private int baseUnit;
	/**
	 * @Fields baseTimes : 数值，表示最小计费单元 比如:10（k:定价单位）
	 * 
	 *         对于前台页面，如果基本定价单元不是k时，则显示为 批量超额额度单位
	 */
	private int baseTimes;
	/**
	 * @Fields unitRatio : 基本跳次的价格，单位:分
	 */
	private long unitRatio;
	/**
	 * @Fields itemRoam : 0：非漫游 1：漫游
	 */
	private int itemRoam;
	/**
	 * @Fields bizType : 业务类型 1:语音 2:短信 3:流量
	 */
	private int bizType;
	/**
	 * @Fields zoneId :
	 */
	private int zoneId;
	/**
	 * @Fields groupId :
	 */
	private int groupId;
	/**
	 * @Fields callType : 0:mo 1:mt 2:不区分
	 */
	private int callType;
	
	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public int getPlanId() {
		return this.planId;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImsi() {
		return this.imsi;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public int getBillId() {
		return this.billId;
	}

	public void setBaseUnit(int baseUnit) {
		this.baseUnit = baseUnit;
	}

	public int getBaseUnit() {
		return this.baseUnit;
	}

	public void setBaseTimes(int baseTimes) {
		this.baseTimes = baseTimes;
	}

	public int getBaseTimes() {
		return this.baseTimes;
	}

	public void setUnitRatio(long unitRatio) {
		this.unitRatio = unitRatio;
	}

	public long getUnitRatio() {
		return this.unitRatio;
	}

	public void setItemRoam(int itemRoam) {
		this.itemRoam = itemRoam;
	}

	public int getItemRoam() {
		return this.itemRoam;
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
		planId = StringUtil.toInt(fields.get(0));
		priority = StringUtil.toInt(fields.get(1));
		imsi = fields.get(2);
		billId = StringUtil.toInt(fields.get(3));
		baseUnit = StringUtil.toInt(fields.get(4));
		baseTimes = StringUtil.toInt(fields.get(5));
		unitRatio = StringUtil.toLong(fields.get(6));
		itemRoam = StringUtil.toInt(fields.get(7));
		
		String sql = "SELECT BIZ_TYPE,ZONE_ID,GROUP_ID,CALL_TYPE FROM TD_B_BILLID WHERE BILL_ID = ? ;";
		List<List<String>> billList = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql,
				String.valueOf(billId));
		if (!CheckNull.isNull(billList)) {
			bizType = StringUtil.toInt(billList.get(0).get(0));
			zoneId = StringUtil.toInt(billList.get(0).get(1));
			groupId = StringUtil.toInt(billList.get(0).get(2));
			callType = StringUtil.toInt(billList.get(0).get(3));
		}

		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_STD_RATE_PLAN.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
        Map<String,Method> indexMethods=new HashMap<String,Method>();
        indexMethods.put(getIndex1Name(),getClass().getMethod(getIndex1Name()));
        return indexMethods;
	}

    public String getIndex1Key() {
        return String.valueOf(this.planId);
    }
    
	public static String getIndex1Name() {
		return "getIndex1Key";
	}
}
