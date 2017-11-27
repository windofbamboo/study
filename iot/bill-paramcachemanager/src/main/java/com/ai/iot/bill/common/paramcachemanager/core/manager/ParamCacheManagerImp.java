package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbClient4Param;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 实现类内部不用同步机制
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [参数管理组件内部实现类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class ParamCacheManagerImp {
    
    /** 管理线程对象*/
    private ManagerThread managerThreadRunnable;
    
    /** 管理线程对象*/
    Thread managerThread;
    
    ParamCacheConfigure paramCacheManagerConfigure;
    
    ///分配给业务线程的客户端对象集
    private HashMap<String,ParamClient> paramClientMap=new HashMap<String,ParamClient>();
    
    private final static Logger logger = LoggerFactory.getLogger(ParamCacheManagerImp.class);
    
    private final static String threadName="MANAGER_THREAD";
    /**
     * 1）初始化物理库和MDB连接
     * 2）创建管理线程
     * 3）初始化客户端对象  
     * 4）单例功能在对外接口中实现，不在此实现
     * @throws POException 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * */
    public void initialize(ParamCacheConfigure paramCacheManagerConfigure) throws POException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	logger.info("ParamCacheManagerImp.initialize()...");
    	this.paramCacheManagerConfigure=paramCacheManagerConfigure;
    	///1）初始化各种对象
    	MdbClient4Param.getGlobalInstance().initialize(this.paramCacheManagerConfigure.getMdbConnectType(),
    			this.paramCacheManagerConfigure.getMdbParamHostAndPorts(),
    			this.paramCacheManagerConfigure.getMdbParamPassword(),
    			this.paramCacheManagerConfigure.isMdbParamUseMaster());
    	PoGroupRegisterFactory.create(this.paramCacheManagerConfigure.getUsedPoGroupNames());
//    	if(this.paramCacheManagerConfigure.isEhCacheType()) {
//    		PoGroupCacheFactory.getGlobalInstance().create(this.paramCacheManagerConfigure.isEhCacheType()&&
//    				this.paramCacheManagerConfigure.isRegisterMBeansForJMX);
//    	}
//    	if(this.paramCacheManagerConfigure.isEhCache3Type()) {
//    		PoGroupCacheFactory4EhCache3.getGlobalInstance().create(this.paramCacheManagerConfigure.isEhCache3Type()&&
//    				this.paramCacheManagerConfigure.isRegisterMBeansForJMX);
//    	}
    	///2）初始化管理线程
    	if(CheckNull.isNull(managerThreadRunnable)) {
    		managerThreadRunnable=new ManagerThread() ;
    		managerThread = new Thread(managerThreadRunnable, threadName);
    		///暂时采用非后台线程方式
    		//managerThread.setDaemon(true);
    		managerThread.start();
    		if(managerThreadRunnable.waitOk()==false)//将线程的异常信息带到业务线程
    			throw new POException(POException.POExceptionCodeENUM.PARAM_CACHE_MANAGER_START_FALSE,threadName,managerThreadRunnable.getExitErrorMessage());
    	}
    	///3）客户端对象暂时不初始化
    	return ;
    }
    
    /**获取客户端对象,暂时每个线程共用一个对象
     * @param clientId	客户端标识：hostname+'+'+procId+'+'+threadId，建议使用
     * @throws POException 
     * */
    public ParamClient getInstanceClient(String clientId) {
    	//synchronized(paramClientMap) {
    		if(paramClientMap.containsKey(clientId)) {
    			///暂时采用抛异常的方式，因为不允许一个客户端多次获取，性能低下
    			logger.warn("ERROR:clientId={} already exist!",clientId);
    			//throw new POException(POException.POExceptionCodeENUM.PARAM_CLIENT_EXIST,clientId);
    			return paramClientMap.get(clientId);
    		}else {
    			ParamClient pc=new ParamClient(clientId,
    					managerThreadRunnable.getLocalParamGroupLoaderMap().getLocalParamGroupCacheABMap());
    			paramClientMap.put(clientId, pc);
    			pc.register();
    			return pc;
    		}
    	//}
    }
    
    /**删除客户端对象
     * @param clientId	客户端标识
     * */
    public void removeClient(String clientId) {
    	//synchronized(paramClientMap) {
    	if(paramClientMap.containsKey(clientId)) {
    		paramClientMap.get(clientId).unRegister();
    		paramClientMap.remove(clientId);
    	}   	
    }
    
    /*///进程退出时的处理
    protected void finalize()  {
    	//logger.info("finalize()...");
    	if(paramClientMap.size()>0) {
    		logger.error("paramClientMap() has clients! Release...");
    		Iterator<Entry<String,ParamClient>> iter=paramClientMap.entrySet().iterator();
    		Map.Entry<String, ParamClient> entry;
    		while(iter.hasNext()) {
    			entry=iter.next();
    			logger.error("clients[{}] unRegister...",entry.getKey());
    			entry.getValue().unRegister();
    		}
    		//因为多线程退出的场景,所以不能抛异常
    		//throw new POException(POExceptionCodeENUM.PARAM_CLIENT_NO_EXIT);
    		//return ;
    	}    		
    	if(!CheckNull.isNull(managerThread)) {
    		logger.error("Stop ManagerThread...");
    		managerThreadRunnable.stop();
    		managerThread.interrupt();
    		try {
				managerThread.join();
			} catch (InterruptedException e) {
				logger.error("ERROR:join{}",e.getLocalizedMessage());
				logger.error(e.getLocalizedMessage(),e);
			}
    	}
    	if(this.paramCacheManagerConfigure.isEhCacheType()) {
    		PoGroupCacheFactory.getGlobalInstance().finalize();
    	}
    	if(this.paramCacheManagerConfigure.isEhCache3Type()) {
    		PoGroupCacheFactory4EhCache3.getGlobalInstance().finalize();
    	}
    	logger.error("ParamCacheManagerImp.finalize() DONE!");
    }*/
    
    ///打印简要信息
    public void dump() {
    	managerThreadRunnable.getLocalParamGroupLoaderMap().dump();
	}
	
    ///打印数据信息
	public void dumpDataAndIndex() {
		managerThreadRunnable.getLocalParamGroupLoaderMap().dumpDataAndIndex();
	}
	
	///打印简要信息
    public void dumpLastVersion() {
    	managerThreadRunnable.getLocalParamGroupLoaderMap().dumpLastVersion();
	}
	
    ///打印数据信息
	public void dumpDataAndIndexLastVersion() {
		managerThreadRunnable.getLocalParamGroupLoaderMap().dumpDataAndIndexLastVersion();
	}
}
