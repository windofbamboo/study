package com.ai.iot.bill.common.paramcachemanager.pog.zone.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @className:PoZone
 * @description:
 * @author:xue
 * @date: 2017-7-24 14:48:10
 */
public class PoZone extends PoBase implements Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(PoZone.class);
	
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields zoneId :
	 */
	private int zoneId;
	/**
	 * @Fields zoneType : 0:普通 1:其它-没有命中的时候，走此区域资费，进行控制 2:标准-标准资费使用
	 */
	private int zoneType;
	/**
	 * @Fields timeId : 0：高峰 1：非高峰
	 * 
	 */
	private int timeId;
	/**
	 * @Fields providerFilter :
	 */
	private int providerFilter;
	/**
	 * @Fields apnFilter :
	 */
	private int apnFilter;
	/**
	 * @Fields rgFilter :
	 */
	private int rgFilter;
	/**
	 * @Fields apns :
	 */
	private String apns;
	/**
	 * @Fields dataStreams :
	 */
	private String dataStreams;
	/**
	 * @Fields 运营商ID列表
	 */
	private List<Integer> providerIdList;
	
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public int getZoneId() {
		return this.zoneId;
	}

	public void setZoneType(int zoneType) {
		this.zoneType = zoneType;
	}

	public int getZoneType() {
		return this.zoneType;
	}

	public void setTimeId(int timeId) {
		this.timeId = timeId;
	}

	public int getTimeId() {
		return this.timeId;
	}

	public void setApnFilter(int apnFilter) {
		this.apnFilter = apnFilter;
	}

	public int getApnFilter() {
		return this.apnFilter;
	}

	public int getProviderFilter() {
		return providerFilter;
	}

	public void setProviderFilter(int providerFilter) {
		this.providerFilter = providerFilter;
	}

	public int getRgFilter() {
		return rgFilter;
	}

	public void setRgFilter(int rgFilter) {
		this.rgFilter = rgFilter;
	}

	public String getApns() {
		return apns;
	}

	public void setApns(String apns) {
		this.apns = apns;
	}

	public String getDataStreams() {
		return dataStreams;
	}

	public void setDataStreams(String dataStreams) {
		this.dataStreams = dataStreams;
	}

	public List<Integer> getProviderIdList() {
		return providerIdList;
	}

	public void setProviderIdList(List<Integer> providerIdList) {
		this.providerIdList = providerIdList;
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
		logger.info("======PoZone fillData======");
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		zoneId = StringUtil.toInt(fields.get(0));
		zoneType = StringUtil.toInt(fields.get(1));
		timeId = StringUtil.toInt(fields.get(2));
		providerFilter = StringUtil.toInt(fields.get(3));
		apnFilter = StringUtil.toInt(fields.get(4));
		rgFilter = StringUtil.toInt(fields.get(5));
		
		StringBuffer sql = new StringBuffer();
		StringBuffer zoneBuffer = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" APN_NAME ");
		sql.append(" FROM TD_B_APN ");
		sql.append(" WHERE APN_ID IN ( ");
		sql.append(" SELECT ");
		sql.append(" APN_ID ");
		sql.append(" FROM TD_B_ZONE_APN ");
		sql.append(" WHERE ZONE_ID = ? ) ");
		List<List<String>> apns = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql.toString(),
				String.valueOf(zoneId));
		zoneBuffer.setLength(0);
		if (!CheckNull.isNull(apns)) {
			for (List<String> apn : apns) {
				String apnnName = apn.get(0);
				if (zoneBuffer.length() > 0) {
					zoneBuffer.append(";").append(apnnName);
				} else {
					zoneBuffer.append(apnnName);
				}
			}
			this.apns = zoneBuffer.toString();
		}

		sql.setLength(0);
		sql.append(" SELECT ");
		sql.append(" DATA_STREAMS_CODE ");
		sql.append(" FROM TD_B_DATA_STREAM ");
		sql.append(" WHERE DATA_STREAMS_ID IN ( ");
		sql.append(" SELECT ");
		sql.append(" DATA_STREAMS_ID ");
		sql.append(" FROM TD_B_ZONE_DATA_STREAM_GROUP ");
		sql.append(" WHERE ZONE_ID = ?  ) ");
		
		List<List<String>> dataStreams = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql.toString(),
				String.valueOf(zoneId));
		zoneBuffer.setLength(0);
		if (!CheckNull.isNull(dataStreams)) {
			for (List<String> dataStream : dataStreams) {
				String dataStreamsCode = dataStream.get(0);
				if (zoneBuffer.length() > 0) {
					zoneBuffer.append(";").append(dataStreamsCode);
				} else {
					zoneBuffer.append(dataStreamsCode);
				}
			}
			this.dataStreams = zoneBuffer.toString();
		}
		
		sql.setLength(0);
		sql.append(" SELECT PROVIDER_ID ");
		sql.append(" FROM TD_B_ZONE_PROVIDER ");
		sql.append(" WHERE ZONE_ID = ? ");
		List<List<String>> providerIds = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql.toString(),
				String.valueOf(zoneId));
		if (!CheckNull.isNull(providerIds)) {
			providerIdList = new ArrayList<Integer>();
			for (List<String> providerId : providerIds) {
				providerIdList.add(StringUtil.toInt(providerId.get(0)));
				logger.info("providerIdList:" + StringUtil.toInt(providerId.get(0)));
			}
		}
		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_ZONE.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String, Method> indexMethods = new HashMap<String, Method>();
		indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.zoneId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}
}
