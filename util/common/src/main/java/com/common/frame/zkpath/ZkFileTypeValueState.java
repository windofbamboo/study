package com.common.frame.zkpath;

import java.util.ArrayList;
import java.util.List;

public class ZkFileTypeValueState {
	private String fileType;
	private List<ZkOutputState> zkOutputStates=new ArrayList<>();
	private List<ZkOutputState> zkErrOutStates=new ArrayList<>();
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<ZkOutputState> getZkOutputStates() {
		return zkOutputStates;
	}
	public List<ZkOutputState> getZkErrOutStates() {
		return zkErrOutStates;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		result = prime * result + ((zkErrOutStates == null) ? 0 : zkErrOutStates.hashCode());
		result = prime * result + ((zkOutputStates == null) ? 0 : zkOutputStates.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ZkFileTypeValueState))
			return false;
		ZkFileTypeValueState other = (ZkFileTypeValueState) obj;
		if (fileType == null) {
			if (other.fileType != null)
				return false;
		} else if (!fileType.equals(other.fileType))
			return false;
		if (zkErrOutStates == null) {
			if (other.zkErrOutStates != null)
				return false;
		} else if (!zkErrOutStates.equals(other.zkErrOutStates))
			return false;
		if (zkOutputStates == null) {
			if (other.zkOutputStates != null)
				return false;
		} else if (!zkOutputStates.equals(other.zkOutputStates))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "{\"fileType\":\"" + fileType + "\",\"zkOutputStates\":\"" + zkOutputStates + "\",\"zkErrOutStates\":\""
				+ zkErrOutStates + "\"} ";
	}
	
}
