package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.util.SuperClassReflectionUtils;
import com.ai.iot.bill.common.util.SysUtil;

public  class ParamCacheConfigure{
	protected static final Logger logger = LoggerFactory.getLogger(ParamCacheConfigure.class);
	///配置文件名
	public static final String PARAM_CACHE_CONFIGURE_PROPERTIES_FILE_NAME="paramcachemanager.properties";
	
	///使用的缓存类型:0=hashmap;1=采用ehcache2版本;2=采用ehcache3版本,默认为0
  	protected int cacheType=0;
	
	/** 定时扫描MDB的睡眠时间*/
    protected int scanSleepSeconds = 3;
    
    /** mdb连接编码*/
    protected int mdbConnectType=BaseDefine.CONNTYPE_REDIS_PARAM;//"param"
    
    ///定时汇报实例信息的时间间隔，单位：秒
    protected long reportFixTimeSeconds=60;
    
    ///访问者心跳超时时间，单位：秒,0=永远不超时
    protected long visitorHbExpireSeconds=60*5L;
    
    ///访问者心跳时间，单位：秒
    protected long visitorHbSeconds=60;
    
    ///不判断访问者状态,强制立即切换,默认为false
    protected boolean isForcedImmediatlySwitch=false;
    
    ///序列化类型:0=FST;1=Json,仅对参数数据作用,其他信息默认用json序列化
  	protected int serializeType=0;
  	
	///mdb-param的主从类型:true=use master;false=use slave
  	protected boolean isMdbParamUseMaster=true;

	///mdb-param的密码配置,默认为空,非空时需配置加密后的串,通过SysUtil.encode进行加密,目前是Base64
  	protected String mdbParamPassword=null;
  	
	///mdb-param的连接配置,格式为host_or_ip:port,可以配置多个,使用逗号分割,为空时将从物理库获取
  	protected String mdbParamHostAndPorts="127.0.0.1:6379";
  	
  	///使用ehcache的时候,是否需要注册到JMX,默认为true
  	protected boolean isRegisterMBeansForJMX=true;

	///允许使用的Po组名,外部模块手工设置,默认是全集
  	protected Set<String> usedPoGroupNames=new HashSet<>();
  	
	public int getCacheType() {
		return cacheType;
	}
	
	public boolean isEhCacheType() {
		return cacheType==1;
	}
	
	public boolean isEhCache3Type() {
		return cacheType==2;
	}
	
	public synchronized ParamCacheConfigure setCacheType(int cacheType) {
		this.cacheType = cacheType;
		return this;
	}

  	public int getScanSleepSeconds() {
		return scanSleepSeconds;
	}

	public synchronized ParamCacheConfigure setScanSleepSeconds(int scanSleepSeconds) {
		this.scanSleepSeconds = scanSleepSeconds;
		return this;
	}

	public int getMdbConnectType() {
		return mdbConnectType;
	}

	public synchronized ParamCacheConfigure setMdbConnectType(int mdbConnectType) {
		this.mdbConnectType = mdbConnectType;
		return this;
	}

	public long getReportFixTimeSeconds() {
		return reportFixTimeSeconds;
	}

	public synchronized ParamCacheConfigure setReportFixTimeSeconds(long reportFixTimeSeconds) {
		this.reportFixTimeSeconds = reportFixTimeSeconds;
		return this;
	}

	public long getVisitorHbExpireSeconds() {
		return visitorHbExpireSeconds;
	}

	public synchronized ParamCacheConfigure setVisitorHbExpireSeconds(long visitorHbExpireSeconds) {
		this.visitorHbExpireSeconds = visitorHbExpireSeconds;
		return this;
	}

	public long getVisitorHbSeconds() {
		return visitorHbSeconds;
	}

	public synchronized ParamCacheConfigure setVisitorHbSeconds(long visitorHbSeconds) {
		this.visitorHbSeconds = visitorHbSeconds;
		return this;
	}

	public int getSerializeType() {
		this.serializeType=MdbParamDataWrapper.getSerializeType();
		return this.serializeType;
	}

	public synchronized ParamCacheConfigure setSerializeType(int serializeType) {
		this.serializeType=serializeType;
		MdbParamDataWrapper.setSerializeType(serializeType);
		return this;
	}
	
  	
  	public boolean isMdbParamUseMaster() {
		return isMdbParamUseMaster;
	}

	public synchronized ParamCacheConfigure setMdbParamUseMaster(boolean isMdbParamUseMaster) {
		this.isMdbParamUseMaster = isMdbParamUseMaster;
		return this;
	}

  	public String getMdbParamPassword() {
		return mdbParamPassword;
	}

	public synchronized ParamCacheConfigure setMdbParamPassword(String mdbParamPassword) {
		this.mdbParamPassword = SysUtil.decode(mdbParamPassword);
		return this;
	}

	public String getMdbParamHostAndPorts() {
		return mdbParamHostAndPorts;
	}

	public synchronized ParamCacheConfigure setMdbParamHostAndPorts(String mdbParamHostAndPorts) {
		this.mdbParamHostAndPorts = mdbParamHostAndPorts;
		return this;
	}

  	public boolean isForcedImmediatlySwitch() {
		return isForcedImmediatlySwitch;
	}

	public synchronized ParamCacheConfigure setForcedImmediatlySwitch(boolean isForcedImmediatlySwitch) {
		this.isForcedImmediatlySwitch = isForcedImmediatlySwitch;
		return this;
	}

	public Set<String> getUsedPoGroupNames() {
		return usedPoGroupNames;
	}

	public synchronized ParamCacheConfigure setUsedPoGroupNames(Set<String> usedPoGroupNames) {
		this.usedPoGroupNames = usedPoGroupNames;
		return this;
	}
	
	public synchronized ParamCacheConfigure addUsedPoGroupNames(String usedPoGroupName) {
		this.usedPoGroupNames.add(usedPoGroupName);
		return this;
	}

  	public boolean isRegisterMBeansForJMX() {
		return isRegisterMBeansForJMX;
	}

	public synchronized ParamCacheConfigure setRegisterMBeansForJMX(boolean isRegisterMBeansForJMX) {
		this.isRegisterMBeansForJMX = isRegisterMBeansForJMX;
		return this;
	}

	///已经包含了父类和子类的所有成员了
	public String toString() {
	    StringBuilder  outStr=new StringBuilder();
		boolean tempB;
		
		outStr.append("=====================================\n");
		for (Field f:SuperClassReflectionUtils.getDeclaredField(this)){
			tempB=f.isAccessible();
			try {				
				f.setAccessible(true);
				//跳过枚举类定义和logger属性
				if(f.getName().endsWith(ParamCacheManagerConfigure.ConfigureEnum.class.getSimpleName())||
						f.getName().equals("logger")) {
					continue;
				}else {
					outStr.append(f.getName()+"="+f.get(this)+"\n");
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(e.getLocalizedMessage(),e);
			}finally {
				f.setAccessible(tempB);
			}
		}
		outStr.append("=====================================\n");
		return outStr.toString();
	}
}

