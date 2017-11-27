package com.ai.iot.bill.common.mdb;

import java.util.Set;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;

import java.lang.reflect.Field;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustJedisCluster extends BinaryJedisCluster {
	private static final Logger logger = LoggerFactory.getLogger(CustJedisCluster.class);
	
	private Set<HostAndPort> initNodes; // 初始nodes
	private GenericObjectPoolConfig poolConf;
	private int connectionTimeout = DEFAULT_TIMEOUT;
	private int soTimeout = 2000;
	private String passWd = null;

	public CustJedisCluster(Set<HostAndPort> nodes, int timeout) {
		this(nodes, timeout, DEFAULT_MAX_REDIRECTIONS,
				new GenericObjectPoolConfig());
	}

	public CustJedisCluster(Set<HostAndPort> nodes) {
		this(nodes, DEFAULT_TIMEOUT);
	}

	public CustJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout,
			int maxAttempts, final GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, timeout, maxAttempts, poolConfig);

		initNodes = jedisClusterNode;
		poolConf = poolConfig;
		connectionTimeout = timeout;
	}

	public CustJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout,
			int sTimeout, int maxAttempts,
			final GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, timeout, sTimeout, maxAttempts, poolConfig);

		initNodes = jedisClusterNode;
		poolConf = poolConfig;
		connectionTimeout = timeout;
		soTimeout = sTimeout;
	}

	public CustJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout,
			int sTimeout, int maxAttempts, String password,
			GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, timeout, sTimeout, maxAttempts, password,
				poolConfig);

		initNodes = jedisClusterNode;
		poolConf = poolConfig;
		connectionTimeout = timeout;
		soTimeout = sTimeout;
		passWd = password;
	}

	public boolean setSlaveOnly() {
		// 反射修改父类JedisCluster中的connectionHandler
		try {
			JedisSlaveConnectionHandler connHandler = new JedisSlaveConnectionHandler(
					initNodes, poolConf, connectionTimeout, soTimeout, passWd);
			
			Field connHandlerField = BinaryJedisCluster.class
					.getDeclaredField("connectionHandler");
			connHandlerField.setAccessible(true);
			connHandlerField.set(this, connHandler);
			connHandlerField.setAccessible(false);

			return true;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			logger.error("setSlaveOnly error:{}",e);
		}

		return false;
	}
}
