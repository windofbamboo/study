package com.ai.iot.bill.cb.base.manager;

import clojure.lang.IFn;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.mdb.common.device.*;
import com.ai.iot.mdb.common.mdb.mog.DevInfoMoSet;
import com.ai.iot.mdb.common.session.mos.MoCdrTransInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MdbCommonException;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.HashUtil;
import com.ai.iot.bill.common.util.ProtostuffUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存库资料管理类
 * 
 * @author xue
 *
 */
public class MdbManager {

	private static final Logger logger = LoggerFactory.getLogger(MdbManager.class);
	private MMDevice mmDevice;
	private CustJedisCluster custJedisCluster;

	public MdbManager(CustJedisCluster custJedisCluster) {
		this.custJedisCluster = custJedisCluster;
		try {
			mmDevice = new MMDevice(custJedisCluster);
		} catch (MdbCommonException e) {
			logger.error("MMDevice创建失败");
		}
	}

	/**
	 * 根据Imsi读取设备
	 * @param imsi
	 * @return
	 */
	public DevInfoMoSet getMmDeviceByImsiOrMsisdn(String imsi, String msisdn, Long startTime) {
		DevInfoMoSet devInfoMoSet = null;
		if (CheckNull.isNull(imsi) && CheckNull.isNull(msisdn)) {
			logger.error("############Both imsi and msisdn is null.############");
			return null;
		}

		mmDevice.reset();
		DeviceImsiMo deviceImsiMo = null;
		DeviceMsisdnMo deviceMsisdnMo = null;
		if(!CheckNull.isNull(msisdn)) {
			deviceMsisdnMo = mmDevice.getDeviceMsisdnMo(msisdn, startTime);
			if (CheckNull.isNull(deviceImsiMo) && !CheckNull.isNull(imsi)) {
				deviceImsiMo = mmDevice.getDeviceImsiMo(imsi, startTime);
			}
			logger.info("############getMmDeviceByImsiOrMsisdn() get DeviceMo by msisdn:{}.############", msisdn);
		} else {
			deviceImsiMo = mmDevice.getDeviceImsiMo(imsi, startTime);
			if (CheckNull.isNull(deviceImsiMo) && !CheckNull.isNull(msisdn)) {
				deviceMsisdnMo = mmDevice.getDeviceMsisdnMo(msisdn, startTime);
			}
			logger.info("############getMmDeviceByImsiOrMsisdn() get DeviceMo by imsi:{}.############", imsi);
		}

		if (CheckNull.isNull(deviceImsiMo) && CheckNull.isNull(deviceMsisdnMo)) {
			logger.error("############getMmDeviceByImsiOrMsisdn() both deviceImsiMo and deviceMsisdnMo is null.############");
			return devInfoMoSet;
		}

		devInfoMoSet = new DevInfoMoSet();
		long deviceId = CheckNull.isNull(deviceImsiMo) ? deviceMsisdnMo.getDeviceId() : deviceImsiMo.getDeviceId();
		DeviceMo deviceMo = mmDevice.getDeviceInfo(deviceId);
		if (CheckNull.isNull(deviceMo)) {
			logger.error("############getMmDeviceByImsiOrMsisdn() get DeviceMo is null.############");
			return null;
		}
		deviceMo.setDeviceId(deviceId);
		devInfoMoSet.setDeviceMo(deviceMo);
		devInfoMoSet.setDeviceRatePlanMos(mmDevice.getDeviceRatePlanListByDeviceId(deviceId));
		devInfoMoSet.setDeviceImsiMo(deviceImsiMo);
		devInfoMoSet.setDeviceMsisdnMo(deviceMsisdnMo);

		devInfoMoSet.setAccountMo(mmDevice.getMdbAccount(devInfoMoSet.getDeviceMo().getAcctId()));
		devInfoMoSet.setAccountAppMo(mmDevice.getAcctApplication(devInfoMoSet.getDeviceMo().getAcctId()));
		return devInfoMoSet;
	}

	/**
	 * 根据设备ID读取资费计划订购列表
	 * @param deviceId
	 * @return
	 */
	public List<DeviceRatePlanMo> getDeviceRatePlanListByDeviceId(long deviceId) {
		mmDevice.reset();
		return mmDevice.getDeviceRatePlanListByDeviceId(deviceId);
	}



	/**
	 * 根据设备ID获取设备资料
	 *
	 * @param deviceId
	 * @return
	 */
	public DeviceMo getDeviceInfo(long deviceId) {
		mmDevice.reset();
		return mmDevice.getDeviceInfo(deviceId);
	}

	/**
	 * 根据账户ID获取可测试用量
	 * 
	 * @param acctId
	 * @return
	 */
	public AccountAppMo getAcctApplication(long acctId) {
		mmDevice.reset();
		return mmDevice.getAcctApplication(acctId);
	}

	/**
	 * 批价话单缓存
	 * 
	 * @param guid
	 *            guid
	 * @param cdrTransInfo
	 *            批价话单对象
	 */
	public long saveRateCdr(String guid, MoCdrTransInfo cdrTransInfo) {
		if (custJedisCluster != null) {
			return custJedisCluster.hset(
					(RedisConst.RatingCdrMdbKey.MDB_KEY_RATINGCDR + Const.KEY_JOIN + HashUtil.ELFHash(guid)%100).getBytes(), guid.getBytes(),
					ProtostuffUtil.serializer(cdrTransInfo));
		}
		return -1;
	}
}