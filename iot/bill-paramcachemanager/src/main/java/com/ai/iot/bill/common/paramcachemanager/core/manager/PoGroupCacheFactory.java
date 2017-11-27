package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.lang.management.ManagementFactory;
import java.net.URL;

import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.util.CheckNull;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

public class PoGroupCacheFactory {
	private static final Logger logger = LoggerFactory.getLogger(PoGroupCacheFactory.class);
	
	///ehcache的配置文件名
	private static final String EHCACHE_PARAM_XML_FILE_NAME="ehcache-paramcachemanager.xml";
	
	///ehcache的模版cache名
	private static final String PARAM_CACHE_PO_GROUP_TEMPLATE="ParamCachePoGroupTemplate";
	
	///cache名称和下标的连接符
	private static final String EHCACHE_NAME_SPLIT=".";
	
	///是否已经初始化过
	private volatile boolean isInited = false;
	
	///cache管理器
	private CacheManager ehcacheManager;
	
	///模版cache,仅仅拿来创建Po组对应的cache的用途
	private Cache ehcachePoGroupTemplate;
	
	///下面的是为了实现单例模式，线程安全
	private PoGroupCacheFactory(){ }  
	  
	public static PoGroupCacheFactory getGlobalInstance()  
	{  
		return NestedSingleton.instance;
	}  
    
    //在第一次被引用时被加载  
    private static class NestedSingleton
    {
		private NestedSingleton() {}
        private static PoGroupCacheFactory instance = new PoGroupCacheFactory();  
    } 
    static {
    	if(ParamCacheManagerConfigure.getGlobalInstance().isEhCacheType()) {
    		try {
				PoGroupCacheFactory.getGlobalInstance().create(ParamCacheManagerConfigure.getGlobalInstance().isEhCacheType()&&
						ParamCacheManagerConfigure.getGlobalInstance().isRegisterMBeansForJMX);
			} catch (POException e) {
				logger.error("ERROR:{}",e);
			}
    	}
    }
    public synchronized void create(boolean isRegisterMBeansForJMX) throws POException {
    	if(!isInited) {
    		logger.info("PoGroupCacheFactory.create(ehcache2)...");
    		URL url = getClass().getClassLoader().getResource(EHCACHE_PARAM_XML_FILE_NAME);
			if(CheckNull.isNull(url)) {
				throw new POException(POException.POExceptionCodeENUM.EHCACHE_PARAM_XML_FILE_NOT_EXIST,EHCACHE_PARAM_XML_FILE_NAME);
			}
			ehcacheManager = CacheManager.newInstance(url);
			if(isRegisterMBeansForJMX) {
				//注册到JMX,方便核查问题,可以看见内存里边的数据.
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				ManagementService.registerMBeans(ehcacheManager, mBeanServer, true, true, true,true);
			}
			ehcachePoGroupTemplate=ehcacheManager.getCache(PARAM_CACHE_PO_GROUP_TEMPLATE);
			dumpConfig();
    		isInited=true;
    	}
    }
    public Cache getNewInstance(String cacheName, int index) {
		logger.info("Using ehcachePoGroupTemplate generator new Cache({}.{})...",cacheName,index);
    	Cache cache=new Cache(ehcachePoGroupTemplate.getCacheConfiguration());
    	cache.setName(cacheName+EHCACHE_NAME_SPLIT+String.valueOf(index));
    	ehcacheManager.addCache(cache);
    	dumpConfig();
    	return cache;
    }
    public void dumpConfig() {
    	logger.info("===>Print ehcacheManager activeConfigurationText:");
    	logger.info(ehcacheManager.getActiveConfigurationText());
	}

}
