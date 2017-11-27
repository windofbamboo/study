package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoSystemParam extends PoBase implements Serializable {
	private static final long serialVersionUID = 2017072217545405103L;
	// 参数类型
	private String paramType;
	// 参数名称
	private String paramName;
	// 参数值
	private String paramValue;
	// 参数值2
	private String paramValue2;

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamValue2() {
		return paramValue2;
	}

	public void setParamValue2(String paramValue2) {
		this.paramValue2 = paramValue2;
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		paramType = fields.get(0);
		paramName = fields.get(1);
		paramValue = fields.get(2);
		paramValue2 = fields.get(3);
		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String, Method> indexMethods = new HashMap<String, Method>();
		indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return paramType;
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

}
