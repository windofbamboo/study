package com.ai.iot.bill.common.param;

import com.ai.iot.bill.common.util.IdInterface;

public class ConnPropertyBean implements IdInterface {

	private int connId;
	private String propType;
	private String propName;
	private String propValue;

	public ConnPropertyBean() {
	}

	public ConnPropertyBean(int connId, String propType, String propName, String propValue) {
		this.connId = connId;
		this.propType = propType;
		this.propName = propName;
		this.propValue = propValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConnPropertyBean that = (ConnPropertyBean) o;

		return connId == that.connId;

	}

	@Override
	public int hashCode() {
		return connId;
	}

	@Override
	public String toString() {
		return "ConnPropertyBean{" +
				"connId=" + connId +
				", propType=" + propType +
				", propName='" + propName + '\'' +
				", propValue='" + propValue + '\'' +
				'}';
	}

	public int getId() {
		return connId;
	}

	public int getConnId() {
		return connId;
	}

	public void setConnId(int connId) {
		this.connId = connId;
	}

	public String getPropType() {
		return propType;
	}

	public void setPropType(String propType) {
		this.propType = propType;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	
	
}
