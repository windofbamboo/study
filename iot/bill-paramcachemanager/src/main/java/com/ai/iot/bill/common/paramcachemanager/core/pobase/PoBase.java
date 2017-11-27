package com.ai.iot.bill.common.paramcachemanager.core.pobase;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.JSONUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [Po实体基类]
 * 数据实体的基类，不包含索引信息
 * 建议将索引方法名作为索引名称
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 * */
public abstract class PoBase implements Serializable{
	private static final long serialVersionUID = 8793518884712743067L;

	/**
	 * 根据输入字段映射到对象具体的字段里边,需判断返回值
	 * */
//	public abstract boolean fillData(String [] fields) ;
    public abstract boolean fillData(Object obj) ;
	
    /** 表组即po组名称，全英文*/
    public abstract String getPoGroupName();
    
     /** 该实体对应的索引名列表，全英文，如：POSampleIndex1，以及所有索引对应的key获取方法*/
    public abstract Map<String,Method> createIndexMethods() throws NoSuchMethodException, SecurityException;
    
    ////////////////////////////////////////////////////////////////////////////
    /** keyPrefix=POGroupName+PONAME+UPDATEFLAG+*/
    public String getPoPrefix(long version) {
    	return RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX_DATA+Const.KEY_JOIN+this.getPoGroupName()+Const.KEY_JOIN+
    				this.getPoName()+Const.KEY_JOIN+
    				String.valueOf(version)+Const.KEY_JOIN;
    }
    
    /** 数据打印的格式，逗号分割，取自类的json格式，继承的子类不需要覆盖*/
    public String toString() {
    	return JSONUtil.toJson(this);
    }
    
	/** 实体名称，全英文，如POSample，取自类名，继承的子类不需要覆盖*/
    public String getPoName() {
    	return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1,this.getClass().getName().length());
    }
    
    
}
