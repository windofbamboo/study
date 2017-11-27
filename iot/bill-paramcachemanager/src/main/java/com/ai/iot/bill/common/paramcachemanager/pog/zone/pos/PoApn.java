package com.ai.iot.bill.common.paramcachemanager.pog.zone.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.StringUtil;

public class PoApn extends PoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	private int apnId;
	private String apnName;

	public int getApnId() {
		return apnId;
	}

	public void setApnId(int apnId) {
		this.apnId = apnId;
	}

	public String getApnName() {
		return apnName;
	}

	public void setApnName(String apnName) {
		this.apnName = apnName;
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		apnId = StringUtil.toInt(fields.get(0));
		apnName = fields.get(1);
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
		indexMethods.put(getIndex2Name(), getClass().getMethod(getIndex2Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.apnId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

	public String getIndex2Key() {
		return String.valueOf(this.apnName);
	}

	public static String getIndex2Name() {
		return "getIndex2Key";
	}

}
