package com.ai.iot.mdb.common.session.mos;

import java.util.List;

public class MoCdrTransInfo {
	/**
	 * 更新过的KEY列表
	 */
	private List<MoModifyKey> modifiKeyList;
	/**
	 * 话单提交成功时间14位
	 */
	private String commitTime;
	public List<MoModifyKey> getModifiKeyList() {
		return modifiKeyList;
	}
	public void setModifiKeyList(List<MoModifyKey> modifiKeyList) {
		this.modifiKeyList = modifiKeyList;
	}
	public String getCommitTime() {
		return commitTime;
	}
	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime;
	}
	
}
