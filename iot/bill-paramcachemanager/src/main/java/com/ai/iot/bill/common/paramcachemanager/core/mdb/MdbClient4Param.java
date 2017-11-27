package com.ai.iot.bill.common.paramcachemanager.core.mdb;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import redis.clients.jedis.HostAndPort;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [MDB操作类，全局实例仅一个]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class MdbClient4Param  {
    	
    	///连接MDB的集群管理对象
    	private CustJedisCluster mdbClient4Cluster;
    
    	private static final Logger logger = LoggerFactory.getLogger(MdbClient4Param.class);
    	
		///下面的是为了实现单例模式，线程安全
		private MdbClient4Param(){ }  
		  
		public static MdbClient4Param getGlobalInstance()
		{  
			return NestedSingleton.instance;
		}
	    
	    //在第一次被引用时被加载  
	    private static class NestedSingleton
	    {
			private NestedSingleton() {}
	        private static MdbClient4Param instance = new MdbClient4Param();  
	    } 
	    
	    /** 初始化
	     * @param mdbPassword 
	     * @param mdbHostAndPorts 
	     * @param isMdbMaster 
	     * @throws POException */
	    public void initialize(int mdbConnectType, String mdbHostAndPorts, String mdbPassword, boolean isMdbMaster) throws POException {
	    	if(CheckNull.isNull(mdbClient4Cluster)) {
	    		logger.info("{}.initialize({})...",this.getClass().getName(),mdbConnectType);
	    		Set<HostAndPort> hostInfos=null;
	    		if(!CheckNull.isNull(mdbHostAndPorts)&&!mdbHostAndPorts.isEmpty()) {
	    			hostInfos=new HashSet<>();
		    		for(String hostInfo:mdbHostAndPorts.split(Const.PROPERTIES_VALUE_SPLIT)) {
		    			if(hostInfo.isEmpty()) {
		    				continue;
		    			}
		    			hostInfos.add(HostAndPort.parseString(hostInfo));	    			
		    		}
	    		}
	    		if(isMdbMaster)
	    			mdbClient4Cluster=RedisMgr.getJedisCluster(mdbConnectType,RedisMgr.MDB_MASTER,hostInfos,mdbPassword);
	    		else
	    			mdbClient4Cluster=RedisMgr.getJedisCluster(mdbConnectType,RedisMgr.MDB_SLAVE,hostInfos,mdbPassword);
	    		if(CheckNull.isNull(mdbClient4Cluster)) {
	    		    String hostInfo;
	    		    if(hostInfos==null) {
	    		        hostInfo="";
	    		    }else {
	    		        hostInfo=hostInfos.toString();
	    		    }
	    		    
	    			throw new POException(POException.POExceptionCodeENUM.MDB_CLIENT_INIT_FALSE,"mdbClient4Cluster is null:hostInfos="+hostInfo+",mdbPassword="+mdbPassword);
	    		}
	    	}
	    }
	    
	    ///返回集群访问对象
	    public CustJedisCluster getCluster() {
	    	return this.mdbClient4Cluster;
	    }
	    
}
