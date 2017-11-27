package com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
 * @className:PoBillGroup
 * @description:
 * @author:xue
 * @date: 2017-8-2 14:51:23
 */
public class PoBillGroup extends PoBase implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 组ID
	 */
    private int groupId;
    
    /**
     * 类型
     */
    private int groupType;
    
    /**
     * 状态
     */
    private int groupStatus;
    
    /**
     * 所属运营商
     */
    private String serviceProviderCode;
    
    /**
     * 包含组ID
     */
    private String groupIds;
    
    /**
     * 包含组MSISDN匹配
     */
    private String groupMsisdns;
    
    /**
     * 拓展組
     */
    private List<Integer> extendGroupList;
    
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public int getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getServiceProviderCode() {
		return serviceProviderCode;
	}

	public void setServiceProviderCode(String serviceProviderCode) {
		this.serviceProviderCode = serviceProviderCode;
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}

	public String getGroupMsisdns() {
		return groupMsisdns;
	}

	public void setGroupMsisdns(String groupMsisdns) {
		this.groupMsisdns = groupMsisdns;
	}

	public List<Integer> getExtendGroupList() {
		return extendGroupList;
	}

	public void setExtendGroupList(List<Integer> extendGroupList) {
		this.extendGroupList = extendGroupList;
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
		groupId = StringUtil.toInt(fields.get(0));
		groupType = StringUtil.toInt(fields.get(1));
		groupStatus = StringUtil.toInt(fields.get(2));
		serviceProviderCode = fields.get(3);
		groupIds = fields.get(4);
		groupMsisdns = fields.get(5);
		
		String sql = " SELECT BILLING_GROUP_ID FROM TD_B_EXTEND_GROUP WHERE GROUP_ID = ? " ;
		List<List<String>> recordList = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql,
				String.valueOf(groupId));
		if (!CheckNull.isNull(recordList)) {
			extendGroupList = new ArrayList<Integer>();
			for (List<String> group : recordList) {
				extendGroupList.add(StringUtil.toInt(group.get(0)));
			}
		}
		
		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_BILL_GROUP.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String, Method> indexMethods = new HashMap<String, Method>();
		indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.groupId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}
}
