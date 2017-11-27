package com.ai.iot.bill.common.param;

import com.ai.iot.bill.common.util.IdInterface;

public class ConnHostBean implements IdInterface {

	private int connId;
	private String hostType;
	private String hostIp;
	private int hostPort;

	public ConnHostBean() {
	}

	public ConnHostBean(int connId, String hostType, String hostIp, int hostPort) {
		this.connId = connId;
		this.hostType = hostType;
		this.hostIp = hostIp;
		this.hostPort = hostPort;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConnHostBean that = (ConnHostBean) o;

		return connId == that.connId;

	}

	@Override
	public int hashCode() {
		return connId;
	}

	@Override
	public String toString() {
		return "ConnHostBean{" +
				"connId=" + connId +
				", hostType=" + hostType +
				", hostIp='" + hostIp + '\'' +
				", hostPort=" + hostPort +
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

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public int getHostPort() {
		return hostPort;
	}

	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
	
	
}
