package com.ai.iot.mdb.common.rate;

import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdbBillStatGsm implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 895712320394975343L;

	private static final Logger logger = LoggerFactory.getLogger(MdbBillStatGsm.class);
	
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
	
	public MdbBillStatGsm clone() {
		MdbBillStatGsm mdbBillStatGsm = null;
		try {
			mdbBillStatGsm = (MdbBillStatGsm) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return mdbBillStatGsm;
	}
}
