package com.ai.iot.bill.cb.base.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.cb.base.entity.RateConst;
import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ParamBean;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.HashUtil;
import com.ai.iot.bill.common.util.StringUtil;

/**
 * 在线会话管理类
 * 
 * @author xue
 *
 */
public class SessionManager {
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private StringBuffer sb = new StringBuffer();
	private CustJedisCluster jc;

	public SessionManager(CustJedisCluster jc) {
		this.jc = jc;
	}

	/**
	 * 获取在线会话数据
	 * 
	 * @param deviceId
	 * @return
	 */
	public String getSession(String deviceId, String sessionId) {
		String key = RedisConst.SessionMdbKey.MDB_KEY_SESSION + Const.KEY_JOIN + deviceId;
		byte[] infos = jc.hget(key.getBytes(), sessionId.getBytes());
		if(CheckNull.isNull(infos)) {
			return null;
		}
		return new String(infos,Const.UTF8);
	}

	/**
	 * 更新在线会话数据
	 * 
	 * @param deviceId
	 * @param sessionId
	 * @param ms
	 * @return
	 */
	public Long updateSession(String deviceId, String cdrInfo, String sessionId) {
		String key = RedisConst.SessionMdbKey.MDB_KEY_SESSION + Const.KEY_JOIN + deviceId;
		return jc.hset(key.getBytes(), sessionId.getBytes(), cdrInfo.getBytes());
	}

	/**
	 * 删除会话
	 * 
	 * @param deviceId
	 * @param sessionId
	 * @return
	 */
	public long deleteSession(String deviceId, String sessionId) {
		String key = RedisConst.SessionMdbKey.MDB_KEY_SESSION + Const.KEY_JOIN + deviceId;
		return jc.hdel(key.getBytes(), sessionId.getBytes());
	}

	/**
	 * 写超时会话到超时列表中
	 * 
	 * @param deviceId
	 * @param sessionId
	 * @param updateTime
	 *            会话最后更新时间（到秒的时间戳）
	 * @return
	 */
	public long saveOverTimeSession(String deviceId, String sessionId) {
		ParamBean param = BaseParamDao.getParamByTypeAndName(RateConst.SESSION_TIME_OUT, RateConst.TIME_OUT_COUNT);
		// 超时会话Mod值
		int modValue = StringUtil.toInt(param.getParamValue(), 10);

		String sessionMsg = deviceId + Const.KEY_JOIN + sessionId;
		
		sb.setLength(0);
		sb.append(RedisConst.SessionMdbKey.MDB_KEY_SESSION_EXPIRE);
		sb.append(Const.KEY_JOIN);
		sb.append(HashUtil.ELFHash(sessionId) % modValue);
		return jc.zadd(sb.toString().getBytes(), Double.valueOf(System.currentTimeMillis() / 1000),
				sessionMsg.getBytes());
	}
}
