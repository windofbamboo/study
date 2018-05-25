package com.common.frame.zkpath;

import com.common.util.Const.SSFtpGetOrPutEnum;

public class ZkOutput {
	private String fileType;
	private String hostOrIp;
	private String dirId;
	private String path;
	//private String outFileType;
	private int servType;
	private ZkOutTypeEnum outType;
	private SSFtpGetOrPutEnum getOrPut;
	private int outSplitNum;
	private String concWorkPath;
	private String workPath;
	private String workPrefix;
	private String loginUser;
	private String loginPassword;
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getHostOrIp() {
		return hostOrIp;
	}
	public void setHostOrIp(String hostOrIp) {
		this.hostOrIp = hostOrIp;
	}
	public String getDirId() {
		return dirId;
	}
	public void setDirId(String dirId) {
		this.dirId = dirId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getServType() {
		return servType;
	}
	public void setServType(int servType) {
		this.servType = servType;
	}
//	public String getOutFileType() {
//		return outFileType;
//	}
//	public void setOutFileType(String outFileType) {
//		this.outFileType = outFileType;
//	}
	private ZkOutTypeEnum getOutType(int outSplitNum) {
		if(outSplitNum<=1)
			return ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR;
		else
			return ZkOutTypeEnum.OUT_TYPE_SUB_DIR;
	}
	public ZkOutTypeEnum getOutType() {
		return outType;
	}
	public void setOutType(ZkOutTypeEnum outType) {
		this.outType = outType;
	}
	public SSFtpGetOrPutEnum getGetOrPut() {
		return getOrPut;
	}
	public void setGetOrPut(SSFtpGetOrPutEnum getOrPut) {
		this.getOrPut = getOrPut;
	}
	public int getOutSplitNum() {
		return outSplitNum;
	}
	public void setOutSplitNum(int outSplitNum) {
		this.outSplitNum = outSplitNum;
		setOutType(getOutType(outSplitNum));
	}
	public String getConcWorkPath() {
		return concWorkPath;
	}
	public void setConcWorkPath(String concWorkPath) {
		this.concWorkPath = concWorkPath;
	}
	public String getWorkPath() {
		return workPath;
	}
	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}
	public String getWorkPrefix() {
		return workPrefix;
	}
	public void setWorkPrefix(String workPrefix) {
		this.workPrefix = workPrefix;
	}
	public String getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	public ZkOutput clone() {
		ZkOutput zkOutput=new ZkOutput();
		zkOutput.assign(this);
		return zkOutput;
	}
	public ZkOutput assign(ZkOutput zkOutput) {
		if(this==zkOutput)
			return this;
		fileType=zkOutput.getFileType();
		getOrPut=zkOutput.getGetOrPut();
		hostOrIp=zkOutput.getHostOrIp();
		outSplitNum=zkOutput.getOutSplitNum();
		dirId=zkOutput.getDirId();
		path=zkOutput.getPath();
		servType=zkOutput.getServType();
		outType=zkOutput.getOutType();
		concWorkPath=zkOutput.getConcWorkPath();
		workPath=zkOutput.getWorkPath();
		workPrefix=zkOutput.getWorkPrefix();
		loginUser=zkOutput.getLoginUser();
		loginPassword=zkOutput.getLoginPassword();
		
		return this;
	}
	
	@Override
	public String toString() {
		return "{\"fileType\":\"" + fileType + "\",\"hostOrIp\":\"" + hostOrIp + "\",\"dirId\":\"" + dirId
				+ "\",\"path\":\"" + path + "\",\"servType\":\"" + servType + "\",\"outType\":\"" + outType
				+ "\",\"getOrPut\":\"" + getOrPut + "\",\"outSplitNum\":\"" + outSplitNum + "\",\"concWorkPath\":\""
				+ concWorkPath + "\",\"workPath\":\"" + workPath + "\",\"workPrefix\":\"" + workPrefix
				+ "\",\"loginUser\":\"" + loginUser + "\",\"loginPassword\":\"" + loginPassword + "\"} ";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concWorkPath == null) ? 0 : concWorkPath.hashCode());
		result = prime * result + ((dirId == null) ? 0 : dirId.hashCode());
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		result = prime * result + ((getOrPut == null) ? 0 : getOrPut.hashCode());
		result = prime * result + ((hostOrIp == null) ? 0 : hostOrIp.hashCode());
		result = prime * result + ((loginPassword == null) ? 0 : loginPassword.hashCode());
		result = prime * result + ((loginUser == null) ? 0 : loginUser.hashCode());
		result = prime * result + outSplitNum;
		result = prime * result + ((outType == null) ? 0 : outType.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + servType;
		result = prime * result + ((workPath == null) ? 0 : workPath.hashCode());
		result = prime * result + ((workPrefix == null) ? 0 : workPrefix.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ZkOutput))
			return false;
		ZkOutput other = (ZkOutput) obj;
		if (concWorkPath == null) {
			if (other.concWorkPath != null)
				return false;
		} else if (!concWorkPath.equals(other.concWorkPath))
			return false;
		if (dirId == null) {
			if (other.dirId != null)
				return false;
		} else if (!dirId.equals(other.dirId))
			return false;
		if (fileType == null) {
			if (other.fileType != null)
				return false;
		} else if (!fileType.equals(other.fileType))
			return false;
		if (getOrPut != other.getOrPut)
			return false;
		if (hostOrIp == null) {
			if (other.hostOrIp != null)
				return false;
		} else if (!hostOrIp.equals(other.hostOrIp))
			return false;
		if (loginPassword == null) {
			if (other.loginPassword != null)
				return false;
		} else if (!loginPassword.equals(other.loginPassword))
			return false;
		if (loginUser == null) {
			if (other.loginUser != null)
				return false;
		} else if (!loginUser.equals(other.loginUser))
			return false;
		if (outSplitNum != other.outSplitNum)
			return false;
		if (outType != other.outType)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (servType != other.servType)
			return false;
		if (workPath == null) {
			if (other.workPath != null)
				return false;
		} else if (!workPath.equals(other.workPath))
			return false;
		if (workPrefix == null) {
			if (other.workPrefix != null)
				return false;
		} else if (!workPrefix.equals(other.workPrefix))
			return false;
		return true;
	}
}
