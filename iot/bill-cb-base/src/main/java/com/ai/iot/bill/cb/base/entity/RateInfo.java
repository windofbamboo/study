package com.ai.iot.bill.cb.base.entity;

import java.io.Serializable;
import java.util.List;

import com.ai.iot.bill.common.cdr.Cdr;

/**
 * 批价信息类
 * @author xue
 *
 */
public class RateInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3790751521215587152L;

	/**
	 * 话单
	 */
	private Cdr cdr;
	
	/**
	 * 通知消息
	 */
	private List<CrmNotifyInfo> crmNotifyList;

	public Cdr getCdr() {
		return cdr;
	}

	public void setCdr(Cdr cdr) {
		this.cdr = cdr;
	}

	public List<CrmNotifyInfo> getCrmNotifyList() {
		return crmNotifyList;
	}

	public void setCrmNotifyList(List<CrmNotifyInfo> crmNotifyList) {
		this.crmNotifyList = crmNotifyList;
	}
	
	
}
