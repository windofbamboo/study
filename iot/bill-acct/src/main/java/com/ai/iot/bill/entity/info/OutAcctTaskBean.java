package com.ai.iot.bill.entity.info;

import java.io.Serializable;
import java.util.List;

/**
 * 出账任务对象信息  Created by zhaojiajun on 2017/8/13.
 */
public class OutAcctTaskBean implements Serializable {

	private static final long serialVersionUID = -91002160985223485L;
	private long dealId;
	private String acctId;
	private String provinceCode;
	private List<String> provAcctList;
	private int totalNum;
	
	public OutAcctTaskBean(){
		// do nothing
	}

	public OutAcctTaskBean(long dealId, String provinceCode){
		this.dealId = dealId;
		this.provinceCode = provinceCode;
	}
	
	public OutAcctTaskBean(long dealId, String provinceCode, List<String> provAcctList){
		this.dealId = dealId;
		this.provinceCode = provinceCode;
		this.provAcctList = provAcctList;
	}
	
	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public long getDealId() {
		return dealId;
	}

	public void setDealId(long dealId) {
		this.dealId = dealId;
	}

	public List<String> getProvAcctList() {
		return provAcctList;
	}

	public void setProvAcctList(List<String> provAcctList) {
		this.provAcctList = provAcctList;
	}

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}
	
	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("dealId=").append(this.dealId).append(",");
		sb.append("acctId=").append(this.acctId).append(",");
		sb.append("provinceCode=").append(this.provinceCode).append(",");
		if (provAcctList != null) {
			sb.append("provAcctList=").append(provAcctList.toString()).append(",");
		}
		sb.append("totalNum=").append(this.totalNum);
		return sb.toString();
	}
}
