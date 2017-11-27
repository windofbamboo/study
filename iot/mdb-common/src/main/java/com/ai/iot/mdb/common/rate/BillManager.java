package com.ai.iot.mdb.common.rate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.util.CdrConst;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 累计量管理类
 * 
 * @author xue
 *
 */
public class BillManager {
	
	private static final Logger logger = LoggerFactory.getLogger(BillManager.class);
	
	private CustJedisCluster custJedisCluster;
	
	private MdbTables mdbDeviceTables;
	
	private MdbTables mdbPoolTables;
	
	private Map<Integer, MdbTableRating> ratingMap = new HashMap<>();
	
	public BillManager(CustJedisCluster custJedisCluster) {
		this.custJedisCluster = custJedisCluster;
		
		mdbDeviceTables = new MdbTables(custJedisCluster);
		
		mdbPoolTables = new MdbTables(custJedisCluster);
		
		// 按类型初始化MdbTableRating
		for (int i = 0; i <= 6; i++) {
			ratingMap.put(i, new MdbTableRating(i));
		}
	}
	
	/**
	 * 查询设备累计量
	 * @param cdr
	 * @return
	 */
	public boolean selectDeviceRatingSum(Cdr cdr) {
		logger.info("======BillManager ATTRI_BIZ_TYPE======:" + cdr.get(CdrAttri.ATTRI_BIZ_TYPE));
		logger.info("======BillManager ATTRI_DEVICE_STATUS======:" + cdr.get(CdrAttri.ATTRI_DEVICE_STATUS));
		logger.info("======BillManager ATTRI_CYCLE_ID======:" + cdr.get(CdrAttri.ATTRI_CYCLE_ID));
		logger.info("======BillManager ATTRI_DEVICE_ID======:" + cdr.get(CdrAttri.ATTRI_DEVICE_ID));
		logger.info("======BillManager ATTRI_GUID======:" + cdr.get(CdrAttri.ATTRI_GUID));
		logger.info("======BillManager ATTRI_POOL_ID======:" + cdr.get(CdrAttri.ATTRI_POOL_ID));

		MdbTableRating mdbTableRating = null;
		if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
			if (cdr.getInt(CdrAttri.ATTRI_DEVICE_STATUS) == CdrConst.DEVICE_STATUS_ACTIVATED) {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_DATA);
			} else {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_TEST_DATA);
			}
		} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_SMS) {
			if (cdr.getInt(CdrAttri.ATTRI_DEVICE_STATUS) == CdrConst.DEVICE_STATUS_ACTIVATED) {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_SMS);
			} else {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_TEST_SMS);
			}
		} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_VOICE) {
			if (cdr.getInt(CdrAttri.ATTRI_DEVICE_STATUS) == CdrConst.DEVICE_STATUS_ACTIVATED) {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_VOICE);
			} else {
				mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_TEST_VOICE);
			}
		}
		if (CheckNull.isNull(mdbTableRating)) {
			return false;
		}
		mdbTableRating.reset(cdr.get(CdrAttri.ATTRI_CYCLE_ID), cdr.get(CdrAttri.ATTRI_DEVICE_ID),
				cdr.get(CdrAttri.ATTRI_GUID), cdr.get(CdrAttri.ATTRI_ACCT_ID), cdr.get(CdrAttri.ATTRI_POOL_ID));

		return mdbDeviceTables.selectTables(mdbTableRating);
	}
	
	/**
	 * 读取设备累计量
	 * @param bizType
	 * @param cycleId
	 * @param deviceId
	 * @param guid
	 * @param acctId
	 * @param poolId
	 * @return
	 */
	public boolean selectDeviceRatingSum(int bizType, String cycleId, String deviceId, String guid, String acctId, String poolId) {
		MdbTableRating mdbTableRating = null;
		if (bizType == CdrConst.BIZ_VOICE) {
			mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_VOICE);
		} else if (bizType == CdrConst.BIZ_SMS) {
			mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_SMS);
		} else if (bizType == CdrConst.BIZ_DATA) {
			mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_DATA);
		}
		if (CheckNull.isNull(mdbTableRating)) {
			return false;
		}
		mdbTableRating.reset(cycleId, deviceId, guid, acctId, poolId);

		return mdbDeviceTables.selectTables(mdbTableRating);
	}
	
	/**
	 * 求取指定FIELD累计量
	 * @param field
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getDeviceRatingSum(String field) {
		return (List) mdbDeviceTables.getData(field);
	}
	
	/**
	 * 读取池累计量
	 * @param cdr
	 * @return
	 */
	public boolean selectPoolRatingSum(Cdr cdr) {
		if (cdr.getInt(CdrAttri.ATTRI_POOL_ID) == 0) {
			return false;
		}
		MdbTableRating mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_POOL);
		mdbTableRating.reset(cdr.get(CdrAttri.ATTRI_CYCLE_ID), cdr.get(CdrAttri.ATTRI_DEVICE_ID),
				cdr.get(CdrAttri.ATTRI_GUID), cdr.get(CdrAttri.ATTRI_ACCT_ID), cdr.get(CdrAttri.ATTRI_POOL_ID));
		
		return mdbPoolTables.selectTables(mdbTableRating);
	}
	/**
	 * 读取池累积量
	 * @param cycleId
	 * @param deviceId
	 * @param guid
	 * @param acctId
	 * @param poolId
	 * @return
	 * @since 1.0
	 */
	public boolean selectPoolRatingSum(String cycleId, String deviceId, String guid, String acctId, String poolId) {
		if (CheckNull.isNull(poolId)) {
			return false;
		}
		MdbTableRating mdbTableRating = ratingMap.get(MdbTableRating.TABLE_TYPE_POOL);
		mdbTableRating.reset(cycleId, deviceId,guid, acctId, poolId);
		
		return mdbPoolTables.selectTables(mdbTableRating);
	}
	/**
	 * 读取指定FIELD池累计量
	 * @param field
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getPoolRatingSum(String field) {
		return (List) mdbPoolTables.getData(field);
	}
	
	/**
	 * 更新设备累计量
	 * @param field
	 * @param sumList
	 */
	@SuppressWarnings("rawtypes")
	public void updateDeviceRatingSum(String field, List sumList) {
		mdbDeviceTables.setData(field, sumList);
	}
	
	/**
	 * 提交设备累计量
	 * @return
	 */
	public int commitDevice(String guid) {
		mdbDeviceTables.setData(guid, System.currentTimeMillis() / 1000);
		return mdbDeviceTables.updateTablesWithVer();
	}
	
	public int commitDevice() {
		return mdbDeviceTables.updateTablesWithVer();
	}
	
	/**
	 * 更新池累计量
	 * @param field
	 * @param sumList
	 */
	@SuppressWarnings("rawtypes")
	public void updatePoolRatingSum(String field, List sumList) {
		mdbPoolTables.setData(field, sumList);
	}
	
	/**
	 * 提交池累计量
	 * @return
	 */
	public int commitPool(String guid) {
		mdbPoolTables.setData(guid, System.currentTimeMillis() / 1000);
		return mdbPoolTables.updateTablesWithVer();
	}
	
	public int commitPool() {
		return mdbPoolTables.updateTablesWithVer();
	}
	
	/**
	 * 删除key
	 * @param key
	 * @param field
	 * @return
	 * @since 1.0
	 */
	public long delete(String key,String field){
		return custJedisCluster.hdel(key.getBytes(), field.getBytes());
	}
}
