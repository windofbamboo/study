package com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.StringUtil;

public class PoCustomerValue extends PoBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1394747819356018642L;

	/**
	 * 组ID
	 */
	private int groupId;
	
	/**
	 * 值类型
	 */
	private int valueType;
	
	/**
	 * 值
	 */
	private String value;
		
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		this.groupId = StringUtil.toInt(fields.get(0));
		this.valueType = StringUtil.toInt(fields.get(1));
		this.value = fields.get(2);
		
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
