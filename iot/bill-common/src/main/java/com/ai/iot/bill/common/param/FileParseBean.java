package com.ai.iot.bill.common.param;

import java.util.List;

public class FileParseBean {
	// 本批次数据
	private List<String> batchDataList;
	// 本批次读取到文件的位置
	private long offset;
	// 是否读到文件结尾
	private boolean finished;

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public List<String> getBatchDataList() {
		return batchDataList;
	}

	public void setBatchDataList(List<String> batchDataList) {
		this.batchDataList = batchDataList;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
