package com.ai.iot.bill.common.paramcachemanager.core.manager;

/**
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [定义一些可配置化的信息,也是单例]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class ParamCacheManagerConfigure extends ParamCacheConfigure {
	//private final static Logger logger = LoggerFactory.getLogger(ParamCacheManagerConfigure.class);
	private static ConfigureEnum configureEnum=ConfigureEnum.PROPERTIES_FILE;
	
	protected static volatile boolean isInitialized=false;
	
	///支持的配置格式
  	public static enum ConfigureEnum{
  		DEFAULT,PROPERTIES_FILE	;
  	}
  	
  	///下面的是单例实现
    protected ParamCacheManagerConfigure(){ }  
	  
	public static ParamCacheManagerConfigure getGlobalInstance()  
	{  
		switch(configureEnum) {
		case PROPERTIES_FILE:
			return NestedSingleton.instancePropertiesFile;
		default:
			return NestedSingleton.instance;
    	}		
	}  
    
    //在第一次被引用时被加载  
    private static class NestedSingleton
    {  
	    private NestedSingleton() {}
        private static ParamCacheManagerConfigure instance = new ParamCacheManagerConfigure();  
        private static ParamCacheManagerConfigure instancePropertiesFile = new ParamCacheManagerConfigurePropertiesFile();  
    } 
    
    /////////////////////////////////////////////////////////////
    //功能实现
    /////////////////////////////////////////////////////////////
    ///用于继承和个性化实现
    public static synchronized ParamCacheManagerConfigure initialize(ConfigureEnum configureEnum) {
    	ParamCacheManagerConfigure.configureEnum=configureEnum;
    	return getGlobalInstance();
    }
    
    ///用于继承和个性化实现
    public synchronized ParamCacheManagerConfigure init() throws Exception {
    	if(!isInitialized) {
    		//logger.info(super.toString());
    		isInitialized=true;
    	}
    	
    	return this;
    }

    public String toString() {
		return super.toString();
	}
}
