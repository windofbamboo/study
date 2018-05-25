package com.common.frame.zkpath;

import java.util.ArrayList;
import java.util.List;

public class ZkFileTypeValue {
	private String fileType;
	private List<ZkOutput> zkOutputs=new ArrayList<>();
	private List<ZkOutput> zkErrOuts=new ArrayList<>();
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<ZkOutput> getZkOutputs() {
		return zkOutputs;
	}
	public List<ZkOutput> getZkErrOuts() {
		return zkErrOuts;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		result = prime * result + ((zkErrOuts == null) ? 0 : zkErrOuts.hashCode());
		result = prime * result + ((zkOutputs == null) ? 0 : zkOutputs.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ZkFileTypeValue))
			return false;
		ZkFileTypeValue other = (ZkFileTypeValue) obj;
		if (fileType == null) {
			if (other.fileType != null)
				return false;
		} else if (!fileType.equals(other.fileType))
			return false;
		if (zkErrOuts == null) {
			if (other.zkErrOuts != null)
				return false;
		} else if (!zkErrOuts.equals(other.zkErrOuts))
			return false;
		if (zkOutputs == null) {
			if (other.zkOutputs != null)
				return false;
		} else if (!zkOutputs.equals(other.zkOutputs))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "{\"fileType\":\"" + fileType + "\",\"zkOutputs\":\"" + zkOutputs + "\",\"zkErrOuts\":\"" + zkErrOuts
				+ "\"} ";
	}

}
