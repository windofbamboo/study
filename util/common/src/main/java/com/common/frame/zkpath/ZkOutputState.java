package com.common.frame.zkpath;

public class ZkOutputState extends ZkOutput {
	private long totalFiles;

	public long getTotalFiles() {
		return totalFiles;
	}

	public void setTotalFiles(long totalFiles) {
		this.totalFiles = totalFiles;
	}
	public ZkOutputState clone() {
		ZkOutputState zkOutputState=new ZkOutputState();
		zkOutputState.assign(this);
		return zkOutputState;
	}
	public ZkOutputState assign(ZkOutputState zkOutputState) {
		if(this==zkOutputState)
			return this;
		super.assign(zkOutputState);
		totalFiles=zkOutputState.getTotalFiles();
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (totalFiles ^ (totalFiles >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ZkOutputState))
			return false;
		ZkOutputState other = (ZkOutputState) obj;
		if (totalFiles != other.totalFiles)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "{\"totalFiles\":\"" + totalFiles + "\",\"toString()\":\"" + super.toString() + "\"} ";
	}


}
