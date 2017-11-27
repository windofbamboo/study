package com.ai.iot.mdb.common.rate;

import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdbBillStatSms implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 895712320394975343L;

	private static final Logger logger = LoggerFactory.getLogger(MdbBillStatSms.class);
	
	/**
	 * 账期
	 */
	private int cycleId;
	
	/**
	 * 总用量
	 */
	private long totalAmount;

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public MdbBillStatSms clone() {
		MdbBillStatSms mdbBillStatSms = null;
		try {
			mdbBillStatSms = (MdbBillStatSms) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillStatSms;
	}
}
