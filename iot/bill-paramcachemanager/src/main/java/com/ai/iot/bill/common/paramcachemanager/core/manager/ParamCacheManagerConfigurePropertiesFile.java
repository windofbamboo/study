package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.PropertiesUtil;

import clojure.asm.Type;

public class ParamCacheManagerConfigurePropertiesFile extends ParamCacheManagerConfigure {
	public ParamCacheManagerConfigurePropertiesFile(){super(); }  
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	///从配置文件中读取配置,覆盖默认配置,可以只配置需要修改的信息
	///覆盖次序(最右边为默认): 
	///$IOT_HOME/conf/paramcachemanager.properties -> resource/paramcachemanager.properties ->default
	@Override
	public synchronized ParamCacheManagerConfigure init() throws Exception {
		if(!isInitialized) {
	    	Map<String,String>  confs=PropertiesUtil.getPropertiesConfigWithHomeEnv(ParamCacheConfigure.PARAM_CACHE_CONFIGURE_PROPERTIES_FILE_NAME);
			Iterator<Entry<String,String>> iter=confs.entrySet().iterator();
			Map.Entry<String, String> entry;
			Field field;
			
			while(iter.hasNext()) {
				entry=iter.next();
				field = ParamCacheConfigure.class.getDeclaredField(entry.getKey());
				if(CheckNull.isNull(field)) {
					continue;
				}
				setPropertyValue(field,entry.getValue());
			}
			isInitialized=true;
    	}
		return this;
    }
	
	private void setPropertyValue(Field field,String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
	    if(field.getType().getName().equals(Type.INT_TYPE.getClassName())) {
            field.set(this, Integer.parseInt(value));
        }else if(field.getType().getName().equals(Type.LONG_TYPE.getClassName())) {
            field.set(this, Long.parseLong(value));
        }else if(field.getType().getName().equals(Type.BOOLEAN_TYPE.getClassName())) {
            field.set(this, Boolean.parseBoolean(value));
        }else if(field.getType().getName().equals(String.class.getTypeName())) {
            field.set(this, value);
        }
        //还有其他的赋值逻辑,需要特殊处理
        //序列化配置
        if(field.getName()=="serializeType") {
            setSerializeType(Integer.parseInt(value));
        }
        //密码解密处理
        if(field.getName()=="mdbParamPassword") {
            setMdbParamPassword(value);
        }
	}
}
