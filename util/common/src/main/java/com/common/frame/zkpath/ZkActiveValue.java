package com.common.frame.zkpath;

import com.common.util.SysUtil;

public class ZkActiveValue {
	private String hostOrIp=null;//主机名或主机ip
	private String procId=null;//系统进程标识，系统产生
	private String channelId="";//通道id，即进程实例标识
	public ZkActiveValue() {
		
	}
	public ZkActiveValue(String channelId) {
		hostOrIp= SysUtil.getHostName();
		procId=SysUtil.getProcessId();
		this.channelId=channelId;
	}
	public String getHostOrIp() {
		return hostOrIp;
	}

	public void setHostOrIp(String hostOrIp) {
		this.hostOrIp = hostOrIp;
	}

	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getProcId() {
		return procId;
	}

	public void setProcId(String procId) {
		this.procId = procId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + ((hostOrIp == null) ? 0 : hostOrIp.hashCode());
		result = prime * result + ((procId == null) ? 0 : procId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ZkActiveValue))
			return false;
		ZkActiveValue other = (ZkActiveValue) obj;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (hostOrIp == null) {
			if (other.hostOrIp != null)
				return false;
		} else if (!hostOrIp.equals(other.hostOrIp))
			return false;
		if (procId == null) {
			if (other.procId != null)
				return false;
		} else if (!procId.equals(other.procId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "{\"hostOrIp\":\"" + hostOrIp + "\",\"procId\":\"" + procId + "\",\"channelId\":\"" + channelId + "\"} ";
	}
		
}
