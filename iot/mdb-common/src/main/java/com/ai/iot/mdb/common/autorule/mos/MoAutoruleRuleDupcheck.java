package com.ai.iot.mdb.common.autorule.mos;

import java.io.Serializable;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;

/**
 * 自动化规则规则重复校验
 * 
 * @author qianwx
 */
public class MoAutoruleRuleDupcheck extends MoBase implements Serializable {
	private static final long serialVersionUID = 8878750561676548447L;
	private String guid;// key
	private int sourceId;
	private long ruleId;
	private int ruleVerId;
	private long execTime;
	private long jobId;

	@Override
	public String getField() {
		return TABLE_ID.AUTORULE.RULE_RULE_DUPCHECK_HASHKEY;
	}

	@Override
	public int getFieldType() {
		return MdbTables.FIELD_TYPE_OBJECT;
	}

	/**关键字定义*/
	public boolean isSame(MoAutoruleRuleDupcheck right) {
		return this.guid.equals(right.getGuid());
	}
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public int getRuleVerId() {
		return ruleVerId;
	}

	public void setRuleVerId(int ruleVerId) {
		this.ruleVerId = ruleVerId;
	}

	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
}
