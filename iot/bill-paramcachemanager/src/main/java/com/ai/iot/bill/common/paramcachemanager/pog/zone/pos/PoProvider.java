package com.ai.iot.bill.common.paramcachemanager.pog.zone.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.StringUtil;

public class PoProvider extends PoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 运营商 ID
	 */
	private int providerId;
	/**
	 * 运营商编码
	 */
	private String providerCode;
	/**
	 * 运营商IMSI
	 */
	private String providerImsi;
	
	private String mscCode;
	
	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getProviderImsi() {
		return providerImsi;
	}

	public void setProviderImsi(String providerImsi) {
		this.providerImsi = providerImsi;
	}

	public String getMscCode() {
		return mscCode;
	}

	public void setMscCode(String mscCode) {
		this.mscCode = mscCode;
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		providerId = StringUtil.toInt(fields.get(0));
		providerImsi = fields.get(1);
		providerCode = fields.get(2);
		mscCode = fields.get(3);
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
		indexMethods.put(getIndex3Name(), getClass().getMethod(getIndex3Name()));
		indexMethods.put(getIndex4Name(), getClass().getMethod(getIndex4Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.providerId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

	public String getIndex2Key() {
		return String.valueOf(this.providerCode);
	}

	public static String getIndex2Name() {
		return "getIndex2Key";
	}

	public String getIndex3Key() {
		return String.valueOf(this.providerImsi);
	}

	public static String getIndex3Name() {
		return "getIndex3Key";
	}
	
	public String getIndex4Key() {
		return String.valueOf(this.mscCode);
	}

	public static String getIndex4Name() {
		return "getIndex4Key";
	}
}
