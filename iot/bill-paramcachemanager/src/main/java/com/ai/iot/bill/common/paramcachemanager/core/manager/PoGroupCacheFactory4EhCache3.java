package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.net.URL;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBaseLinkedList;
import com.ai.iot.bill.common.util.CheckNull;


public class PoGroupCacheFactory4EhCache3 {
	
		private static final Logger logger = LoggerFactory.getLogger(PoGroupCacheFactory4EhCache3.class);
		
		///ehcache的配置文件名
		private static final String EHCACHE_PARAM_XML_FILE_NAME="ehcache3-paramcachemanager.xml";
		
		///ehcache的模版cache名
		private static final String PARAM_CACHE_PO_GROUP_TEMPLATE="ParamCachePoGroupTemplate";
		
		///cache名称和下标的连接符
		private static final String EHCACHE_NAME_SPLIT=".";
		
		///是否已经初始化过
		private volatile boolean isInited = false;
		
		///cache管理器
		private CacheManager ehcacheManager;
		
		///模版创建器
		CacheConfigurationBuilder<String, PoBaseLinkedList> configureTemplateBuilder;
		
		///下面的是为了实现单例模式，线程安全
		private PoGroupCacheFactory4EhCache3(){ }  
		  
		public static PoGroupCacheFactory4EhCache3 getGlobalInstance()  
		{  
			return NestedSingleton.instance;
		}  
	    
	    //在第一次被引用时被加载  
	    private static class NestedSingleton
	    {
			private NestedSingleton() {}
	        private static PoGroupCacheFactory4EhCache3 instance = new PoGroupCacheFactory4EhCache3();  
	    } 
	    static {
	    	if(ParamCacheManagerConfigure.getGlobalInstance().isEhCache3Type()) {
	    		try {
					PoGroupCacheFactory4EhCache3.getGlobalInstance().create();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | POException e) {
					logger.error("ERROR:{}",e);
				}
	    	}
	    }
	    public synchronized void create() throws POException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	    	if(!isInited) {
	    		logger.info("PoGroupCacheFactory4EhCache3.create(ehcache3)...");
	    		URL url = getClass().getClassLoader().getResource(EHCACHE_PARAM_XML_FILE_NAME);
				if(CheckNull.isNull(url)) {
					throw new POException(POException.POExceptionCodeENUM.EHCACHE_PARAM_XML_FILE_NOT_EXIST,EHCACHE_PARAM_XML_FILE_NAME);
				}
				XmlConfiguration xmlConfig = new XmlConfiguration(url); 
				configureTemplateBuilder = xmlConfig.newCacheConfigurationBuilderFromTemplate(PARAM_CACHE_PO_GROUP_TEMPLATE, String.class, PoBaseLinkedList.class); 
				ehcacheManager = CacheManagerBuilder.newCacheManager(xmlConfig); 
				if(CheckNull.isNull(configureTemplateBuilder)||CheckNull.isNull(ehcacheManager)) {
					throw new POException(POException.POExceptionCodeENUM.EHCACHE_MANAGER_CREATE_FALSE,EHCACHE_PARAM_XML_FILE_NAME);
				}
				ehcacheManager.init();
				dumpConfig();
	    		isInited=true;
	    	}
	    }
	    public Cache<String, PoBaseLinkedList> getNewInstance(String cacheName, int index) {
			logger.info("Using ehcachePoGroupTemplate generator new Cache({}.{})...",cacheName,index);
			Cache<String, PoBaseLinkedList> cache = ehcacheManager.createCache(cacheName+EHCACHE_NAME_SPLIT+String.valueOf(index)
																																,configureTemplateBuilder);
	    	dumpConfig();
	    	return cache;
	    }
	    public void dumpConfig() {
	        if(ehcacheManager==null||ehcacheManager.getRuntimeConfiguration()==null) {
	            return ;
	        }
	    	logger.info("===>Print ehcacheManager activeConfigurationText: Not Supported Now!{}",ehcacheManager.getRuntimeConfiguration().toString());
		}

	
}
