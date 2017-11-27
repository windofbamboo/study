package com.ai.iot.mdb.common.autorule.mos;

import java.io.Serializable;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;

/**
 * 自动化规则工单重复检验
 * 
 * @author qianwx
 */
public class MoAutoruleJobDupcheck extends MoBase implements Serializable {
	private static final long serialVersionUID = -9114941981064864956L;
	private long jobId;// key
	private int sourceId;
	private long execTime;

	@Override
	public String getField() {
		return TABLE_ID.AUTORULE.RULE_JOB_DUPCHECK_HASHKEY;
	}

	@Override
	public int getFieldType() {
		return MdbTables.FIELD_TYPE_OBJECT;
	}

	/**关键字定义*/
	public boolean isSame(MoAutoruleJobDupcheck right) {
		return this.jobId==right.getJobId();
	}
	
	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}
}
