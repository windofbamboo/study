package com.ai.iot.bill.common.mdb;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;

import com.ai.iot.bill.common.param.BaseParamDao;
import com.ai.iot.bill.common.param.ConnHostBean;
import com.ai.iot.bill.common.param.ConnectBean;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.PropertiesUtil;
import com.ai.iot.bill.common.util.StringUtil;
import com.ai.iot.bill.common.util.SysUtil;

public class RedisMgr {
	private static final Logger logger = LoggerFactory
			.getLogger(RedisMgr.class);

	public static final int MDB_MASTER = 0;
	public static final int MDB_SLAVE = 1;

	protected static final int REDIS_TIMEOUT = 5000;
	protected static final int REDIS_SOTIMEOUT = 5000;
	protected static final int REDIS_MAX_REDIRECTIONS = 5;
	
	private static CustJedisCluster initRedisCluster(int routeId, int dbType){
		Set<HostAndPort> handp = new HashSet<>();
		String pwd=null;
		
		ConnectBean objConn = BaseParamDao.getConnByRouteType(routeId);
		if (objConn != null) {
			pwd = objConn.getPassWord();

			List<ConnHostBean> objHosts = BaseParamDao.getConnHostByRouteType(routeId);

			if (objHosts != null && !objHosts.isEmpty()) {
				for (ConnHostBean obj : objHosts) {
					handp.add(new HostAndPort(obj.getHostIp(), obj.getHostPort()));
				}
			}
		}
		
		if (!handp.isEmpty()) {
			return initRedisCluster(routeId, dbType, handp, pwd);
		}else {
			logger.debug("==> Connect to Redis: routeID={},dbType={},HostAndPort=null",routeId,dbType);
			return null;
		}
	}

	private static CustJedisCluster initRedisCluster(int routeId, int dbType, Set<HostAndPort> handp, String pwd) {
		CustJedisCluster jc = null;

		try {
			Properties prop = PropertiesUtil.getProperties("redis");
			if (prop != null) {
				GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
				conf.setMaxTotal(SysUtil.getIntValue(prop.getProperty("maxTotal"), 500));
				conf.setMaxIdle(SysUtil.getIntValue(prop.getProperty("maxIdle"), 500));
				conf.setMinIdle(SysUtil.getIntValue(prop.getProperty("minIdle"), 1));
				conf.setMaxWaitMillis(SysUtil.getIntValue(prop.getProperty("maxWaitMillis"), 10000));
				conf.setTestOnBorrow(SysUtil.getBoolValue(prop.getProperty("testOnBorrow"), false));
				conf.setTestOnReturn(SysUtil.getBoolValue(prop.getProperty("testOnReturn"), false));

				logger.debug("==> Connect to Redis: dbType={},HostAndPort={}", dbType, handp);

				if (StringUtil.isEmpty(pwd)) {
					jc = new CustJedisCluster(handp, REDIS_TIMEOUT, REDIS_SOTIMEOUT, REDIS_MAX_REDIRECTIONS, conf);
				} else {
					jc = new CustJedisCluster(handp, REDIS_TIMEOUT, REDIS_SOTIMEOUT, REDIS_MAX_REDIRECTIONS, pwd, conf);
				}

				if (dbType == MDB_SLAVE) {
					jc.setSlaveOnly();
				}
			}
		} catch (Exception e) {
			jc = null;
			logger.error("==> Redis connect error:", e);
		}

		return jc;
	}

	public static CustJedisCluster getJedisCluster(int routeId) {
		return getJedisCluster(routeId, MDB_MASTER);
	}

	public static synchronized CustJedisCluster getJedisCluster(int routeId, int dbType) {
		return initRedisCluster(routeId, dbType);
	}
	
	public static synchronized CustJedisCluster getJedisCluster(int routeId, int dbType,Set<HostAndPort> handp, String pwd) {
		if(handp!=null && !handp.isEmpty()){
			return initRedisCluster(routeId, dbType,handp,pwd);
		}else{
			return initRedisCluster(routeId, dbType);
		}
	}
	
	public static boolean isConnected(CustJedisCluster jc){
		if(jc!=null){
			String randomKey=String.valueOf(System.nanoTime());
			byte[] bret=jc.echo(randomKey.getBytes(Const.UTF8));
			if(bret!=null && (new String(bret,Const.UTF8)).equals(randomKey)){
				return true;
			}
		}
		
		return false;
	}
	
	public static void close(CustJedisCluster jc){
		if(jc!=null){
			try {
				jc.close();
			} catch (IOException e) {
				logger.error("==> Redis close error:", e);
			}
		}
	}
	
	private RedisMgr() {
	    throw new IllegalStateException("Utility class");
	}
}
