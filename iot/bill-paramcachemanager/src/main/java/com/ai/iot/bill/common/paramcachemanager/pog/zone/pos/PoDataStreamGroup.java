package com.ai.iot.bill.common.paramcachemanager.pog.zone.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.StringUtil;

public class PoDataStreamGroup extends PoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	private int dataStreamsId;
	private String dataStreamsName;
	private String dataStreamsCode;
	private boolean validTag;
	private String ips;
	private int filter;

	public int getDataStreamsId() {
		return dataStreamsId;
	}

	public void setDataStreamsId(int dataStreamsId) {
		this.dataStreamsId = dataStreamsId;
	}

	public String getDataStreamsName() {
		return dataStreamsName;
	}

	public void setDataStreamsName(String dataStreamsName) {
		this.dataStreamsName = dataStreamsName;
	}

	public String getDataStreamsCode() {
		return dataStreamsCode;
	}

	public void setDataStreamsCode(String dataStreamsCode) {
		this.dataStreamsCode = dataStreamsCode;
	}

	public boolean isValidTag() {
		return validTag;
	}

	public void setValidTag(boolean validTag) {
		this.validTag = validTag;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public int getFilter() {
		return filter;
	}

	public void setFilter(int filter) {
		this.filter = filter;
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		dataStreamsId = StringUtil.toInt(fields.get(0));
		dataStreamsName = fields.get(1);
		dataStreamsCode = fields.get(2);
		validTag = "1".equals(fields.get(3));
		ips = fields.get(4);
		filter = Integer.valueOf(fields.get(5));
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
		return String.valueOf(this.dataStreamsId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

	public String getIndex2Key() {
		return String.valueOf(this.dataStreamsCode);
	}

	public static String getIndex2Name() {
		return "getIndex1Key";
	}

}
