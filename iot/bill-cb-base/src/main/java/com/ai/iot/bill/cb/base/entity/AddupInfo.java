package com.ai.iot.bill.cb.base.entity;

public class AddupInfo {

	/**
	 * 累计量ID
	 */
	private int billId;
	
	/**
	 * 区域ID
	 */
	private int zoneId;
	
	/**
	 * 计费组ID
	 */
	private int groupId;
	
	/**
	 * 呼叫类型
	 */
	private int callType;

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
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
}
