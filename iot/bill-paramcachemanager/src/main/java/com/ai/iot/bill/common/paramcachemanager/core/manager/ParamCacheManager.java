package com.ai.iot.bill.common.paramcachemanager.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException.POExceptionCodeENUM;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.SysUtil;

/**
 * 1）单例模式
 * 2）初始化管理线程，仅初始化一次，需支持多线程并发
 * 3）可以获取多个业务处理对应的客户端对象
 * TODO 
 * 1）使用ecache和map配置化选择
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [参数管理组件对外接口类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class ParamCacheManager  {
	/** 管理线程对象*/
    private static ParamCacheManagerImp paramCacheManagerImp;
    
    private final static Logger logger = LoggerFactory.getLogger(ParamCacheManager.class);
    
    ///下面的是为了实现单例模式，线程安全
	private ParamCacheManager(){ }  
	  
	public static ParamCacheManager getGlobalInstance()  
	{  
		return NestedSingleton.instance;
	}  
    
    //在第一次被引用时被加载  
    private static class NestedSingleton
    {
		private NestedSingleton() {}
        private static ParamCacheManager instance = new ParamCacheManager();  
    } 

    /**
     * @throws POException  
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException */
    public synchronized void initialize(ParamCacheConfigure paramCacheManagerConfigure) throws POException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	logger.info("ParamCacheManager.initialize()...");
    	//synchronized (paramCacheManagerImp) {
    		if(CheckNull.isNull(paramCacheManagerImp)) {
    			logger.info(paramCacheManagerConfigure.toString());
        		paramCacheManagerImp=new ParamCacheManagerImp();
        		if(CheckNull.isNull(paramCacheManagerConfigure)) {
        			paramCacheManagerConfigure=ParamCacheManagerConfigure.getGlobalInstance();
        			logger.info("Use default ParamCacheManagerConfigure");
        		}
        		paramCacheManagerImp.initialize(paramCacheManagerConfigure);
        		logger.info("initialize OK!");
        		return ;
        	}
		//}
    	if(CheckNull.isNull(paramCacheManagerImp)) {
    		logger.error("initialize ERROR!");
    		throw new POException(POExceptionCodeENUM.PARAM_CACHE_MANAGER_NULL,"paramCacheManagerImp");
    	}
    	logger.info("initialize OK!");
    	return ;
    }
    
    /**
     * 获取客户端对象,每个线程一个对象就够用了，返回的客户端对象是线程安全的。
     * 该方法默认使用SysUtil.getThreadInstanceId()获取线程标识，作为唯一的客户端标识。
     * @throws POException 
     * */
    public ParamClient getInstanceClient() throws POException {
    	return getInstanceClient(SysUtil.getThreadInstanceId());
    }
    
    /**
     * 返回管理实例的id，用于和mdb查询
     * */
    public String getManagerInstanceId() {
        return MdbParamDataWrapper.getManagerInstanceId();
    }
    /**
     * 获取客户端对象,暂时每个线程共用一个对象
     * @param clientId 客户端标识：hostname+'+'+procId+'+'+threadId,建议使用SysUtil.getThreadInstanceId()获取线程标识
     * @throws POException */
    public ParamClient getInstanceClient(String clientId) throws POException {
    	logger.info("getInstanceClient for clientId={}",clientId);
    	if(CheckNull.isNull(paramCacheManagerImp)) {
    		logger.error("getInstanceClient ERROR!");
    		throw new POException(POExceptionCodeENUM.PARAM_CACHE_MANAGER_NULL,clientId);
    	}
    	logger.info("getInstanceClient OK!clientId={}",clientId);
    	synchronized(paramCacheManagerImp) {
    	return paramCacheManagerImp.getInstanceClient(clientId);
    	}
    }
    
    /**删除客户端对象
     * @param clientId	客户端标识
     * */
    public void removeClient(String clientId) {
    	synchronized(paramCacheManagerImp) {
    			paramCacheManagerImp.removeClient(clientId);
        	}
    }
    
    /*///进程退出时的处理
    protected void finalize() {
    	logger.info("finalize()...");
    	if(!CheckNull.isNull(paramCacheManagerImp)) {
    		synchronized(paramCacheManagerImp) {
    			paramCacheManagerImp.finalize();
    		}
    	}
    	logger.info("finalize() DONE!");
    }*/
    
    ///打印简要信息
    public void dump() {
    	paramCacheManagerImp.dump();
	}
	
    ///打印数据信息
	public void dumpDataAndIndex() {
		paramCacheManagerImp.dumpDataAndIndex();
	}
	
	///打印简要信息
    public void dumpLastVersion() {
    	paramCacheManagerImp.dumpLastVersion();
	}
	
    ///打印数据信息
	public void dumpDataAndIndexLastVersion() {
		paramCacheManagerImp.dumpDataAndIndexLastVersion();
	}
}
