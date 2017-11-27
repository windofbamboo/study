package com.ai.iot.bill.common.mdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class JedisSlaveConnectionHandler extends
		JedisSlotBasedConnectionHandler {

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock w = rwl.writeLock();
	private volatile boolean rediscovering;
	  
	private Set<HostAndPort> initNodes;
	
	//正式环境需设置为false
	private boolean initNodesOnly=false;

	private String passWd = null;
	
	private static WeakHashMap<Jedis,Boolean> readOnlyNodes = new WeakHashMap<>();

	public JedisSlaveConnectionHandler(Set<HostAndPort> nodes,
			GenericObjectPoolConfig poolConfig, int connectionTimeout,
			int soTimeout, String password) {
		super(nodes, poolConfig, connectionTimeout, soTimeout, password);

		initNodes = nodes;
		passWd = password;

		if(initNodesOnly)
			initInitNodesCache();
		else
			initSlaveNodesCache();
	}
	
	private void initInitNodesCache() {
		cache.reset();

		for (HostAndPort hp : initNodes) {
			Jedis jedis = new Jedis(hp.getHost(), hp.getPort());
			try {
				if (passWd != null) {
					jedis.auth(passWd);
				}
				initInitSlotsCache(jedis, hp);
			} catch (JedisConnectionException e) {
				// try next nodes
			} finally {
				jedis.close();
			}
		}
	}

	private void initInitSlotsCache(Jedis jedis, HostAndPort hp) {
		String cns = jedis.clusterNodes();
		ClusterNode myself = null;

		Map<String, ClusterNode> nodes = new HashMap<>();
		for (String cn : cns.split("\n")) {
			ClusterNode node = parseClusterNode(cn, hp);

			if (node.isMyself) {
				myself = node;
			}

			nodes.put(node.nodeId, node);
			
			if(myself!=null && nodes.containsKey(myself.masterNodeId)){
				break;
			}
		}

		if (null!=myself){
			if(!myself.isMaster && nodes.containsKey(myself.masterNodeId)) {
				myself.slots = nodes.get(myself.masterNodeId).slots;
			}
			
			cache.setupNodeIfNotExist(myself.hp);
			cache.assignSlotsToNode(myself.slots, myself.hp);
		}
	}

	private void initSlaveNodesCache() {
		cache.reset();

		for (HostAndPort hp : initNodes) {
			Jedis jedis = new Jedis(hp.getHost(), hp.getPort());
			try {
				if (passWd != null) {
					jedis.auth(passWd);
				}
				initSlaveSlotsCache(jedis, hp);
				break;
			} catch (JedisConnectionException e) {
				// try next nodes
			} finally {
				jedis.close();
			}
		}
	}

	private void initSlaveSlotsCache(Jedis jedis, HostAndPort hp) {
		if (!rediscovering) {
			try {
				w.lock();
				rediscovering = true;

				String cns = jedis.clusterNodes();

				Map<String, ClusterNode> masterNodes = new HashMap<>();
				Map<String, ClusterNode> slaveNodes = new HashMap<>();
				for (String cn : cns.split("\n")) {
					ClusterNode node = parseClusterNode(cn, hp);

					if (node.isConnected) {
						if (node.isMaster) {
							masterNodes.put(node.nodeId, node);
						} else {
							if (slaveNodes.containsKey(node.masterNodeId)) {
								// 判断优先级
								if (slaveNodes.get(node.masterNodeId).ver < node.ver) {
									slaveNodes.put(node.masterNodeId, node);
								}
							} else {
								slaveNodes.put(node.masterNodeId, node);
							}
						}
					}
				}

				for (ClusterNode node : slaveNodes.values()) {
					if (masterNodes.containsKey(node.masterNodeId)) {
						cache.setupNodeIfNotExist(node.hp);
						cache.assignSlotsToNode(
								masterNodes.get(node.masterNodeId).slots,
								node.hp);
					}
				}

				for (ClusterNode node : masterNodes.values()) {
					if (!slaveNodes.containsKey(node.masterNodeId)) {
						cache.setupNodeIfNotExist(node.hp);
						cache.assignSlotsToNode(node.slots, node.hp);
					}
				}
			} finally {
				rediscovering = false;
				w.unlock();
			}
		}
	}

	@Override
	public Jedis getConnectionFromSlot(int slot) {
		JedisPool connectionPool = cache.getSlotPool(slot);
		Jedis jedis = null;
		if (connectionPool != null) {
			jedis = connectionPool.getResource();
		} else {
			if(!initNodesOnly){
				renewSlaveSlots(); 
			}
			connectionPool = cache.getSlotPool(slot);
			if (connectionPool != null) {
				jedis = connectionPool.getResource();
			} else {
				jedis = getConnection();
			}
		}
		
		if(jedis!=null && !initNodesOnly && !readOnlyNodes.containsKey(jedis)){
			//未发送过readonly
			jedis.readonly();
			readOnlyNodes.put(jedis, false);
		}
		
		return jedis;
	}
	
	public void renewSlaveSlots() {
		renewSlaveSlots(null);
	}

	public void renewSlaveSlots(Jedis jedis) {
		if (!rediscovering && !initNodesOnly) {
			try {
				w.lock();
				rediscovering = true;

				if (jedis != null) {
					try {
						initSlaveSlotsCache(jedis, null);
						return;
					} catch (JedisException e) {
						// try nodes from all pools
					}
				}

				for (JedisPool jp : cache.getShuffledNodesPool()) {
					Jedis jedis1 = null;
					try {
						jedis1 = jp.getResource();
						initSlaveSlotsCache(jedis1, null);
						return;
					} catch (JedisConnectionException e) {
						// try next nodes
					} finally {
						if (jedis1 != null) {
							jedis1.close();
						}
					}
				}
			} finally {
				rediscovering = false;
				w.unlock();
			}
		}
	}
	
	private ClusterNode parseClusterNode(String s,HostAndPort current) {
		String[] sa = s.split(" ");		
		
		ClusterNode node = new ClusterNode();

		node.nodeId = sa[0];		

		node.isMaster = sa[2].contains("master");
		node.isMyself = sa[2].contains("myself");		
		
		if(initNodesOnly && node.isMyself){
			node.hp = current;
		}else{
			String hp = sa[1];
			String[] hpa = hp.split(":");
			node.hp = new HostAndPort(hpa[0], Integer.parseInt(hpa[1]));
		}		

		if (!node.isMaster) {
			node.masterNodeId = sa[3];
		}
		
		node.ver = Integer.parseInt(sa[6]);
		node.isConnected = sa[7].equalsIgnoreCase("connected");

		if (sa.length > 8) {
			node.slots = new ArrayList<>();

			String ss;
			String[] ssa;
			for (int i = 8; i < sa.length; i++) {
				ss = sa[i];
				if (ss.startsWith("[")) { // slot is in transition
					continue;
				}

				if (ss.contains("-")) { // rang
					ssa = ss.split("-");
					for (int j = Integer.parseInt(ssa[0]); j <= Integer
							.parseInt(ssa[1]); j++) {
						node.slots.add(j);
					}
				} else { // single
					node.slots.add(Integer.parseInt(ss));
				}
			}
		}

		return node;
	}
	
	class ClusterNode {
		String nodeId;

		String masterNodeId;

		boolean isMaster;
		
		boolean isMyself;
		
		boolean isConnected;
		
		int ver;

		HostAndPort hp;

		List<Integer> slots;
	}
}
