package com.ai.iot.bill.common.util;

import java.util.UUID;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
 * zookeeper分布式锁
 * @Description:  [zookeeper分布式锁操作]
 * @Author:       [xulr]   
 * @CreateDate:   [2017.9.14 16:54]   
 * @Version:      [v1.0]
 */
public class ZkLock {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ZkClient zk=null;
	//锁根节点
	private String rootPath="/lock_iot";
	//获取到的锁节点路径
	private String lockPath=null;
	//获取到的锁节点分区
	private int partition=Const.ERROR;
	//
	String uuid = null;
	
	public ZkLock(String zkServers){
		zk = new ZkClient(zkServers,10000,10000);
	}	
	
	public ZkClient getZkClient() {
		return zk;
	}

	/**
	*  获取分布式锁定的分区
	*  @return  锁定的分区
	*  @since  1.0
	*/
	public int getPartition() {
		return partition;
	}

	/**
	*  获取分布式锁
	*  @param node  锁节点
	*  @return  0=成功;1=失败;-1=错误
	*  @since  1.0
	*/
	public int getLock(String node){
		//释放前次获取的锁
		releaseLock();
		
		if(zk != null){
			if(!zk.exists(rootPath)){
				//初始创建根节点
				try{
					zk.createPersistent(rootPath);
				}catch(Exception e) {
					logger.error("create rootPath {} fail.",rootPath);
			        return Const.ERROR;
				}
			}
			
			String tmpNode = rootPath+"/"+node;
			
			try{
				//创建临时节点(分布式锁)
				zk.createEphemeral(tmpNode,uuid);
			}catch(ZkNodeExistsException e){
				//节点已存在，判断节点是否上次创建后断掉的节点
				String nodeValue = zk.readData(tmpNode, true);
				if(nodeValue==null || uuid.compareTo(nodeValue)!=0){
					//不是自己上次创建的
					return Const.FAIL;
				}
			}catch(Exception e){
				//错误
				logger.error("Exception node:{}",tmpNode,e);
				return Const.ERROR;
			}
			
			//记录获取的锁，用于以后释放
			lockPath = tmpNode;
			return Const.OK;
		}else{
			logger.error("zkClient is not connected.");
		}
		
		return Const.ERROR;
	}
	
	/**
	*  获取分区锁
	*  @param node  锁节点
	*  @param partitions  分区数
	*  @return  获取的分区
	*  @since  1.0
	*/
	public int getLock(String node, int parallel) {
		for(int i=0;i<parallel;i++){
			String tmpNode = node + String.valueOf(i);
			
			switch (getLock(tmpNode)) {
			case Const.OK:
				//获取成功
				this.partition = i;
				return Const.OK;
			case Const.ERROR:
				//错误
				return Const.ERROR;
			default:
				//节点已存在，继续尝试
				break;
			}
		}
		return Const.ERROR;
	}
	
	/**
	*  释放分布式锁
	*  @since  1.0
	*/
	public void releaseLock(){
		if(zk!=null && StringUtil.isNotEmpty(lockPath)){
			zk.delete(lockPath);
			lockPath = null;
			partition = Const.ERROR;
			
			uuid = UUID.randomUUID().toString();
		}
		
		if(uuid == null){
			uuid = UUID.randomUUID().toString();
		}
	}
	
	/**
	*  释放分布式锁
	*  @since  1.0
	*/
	public void close(){
		releaseLock();
		
		zk=null;
	}
}
