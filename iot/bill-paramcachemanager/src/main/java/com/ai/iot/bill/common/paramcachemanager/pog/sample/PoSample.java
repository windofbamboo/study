package com.ai.iot.bill.common.paramcachemanager.pog.sample;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.Const;

/** PoBase基类的样例,建议将索引方法名作为索引名称*/
public class PoSample extends PoBase  implements Serializable{
	private static final long serialVersionUID = -6946066897096950817L;
	/** 样例字段列表,以及get set方法*/
	private String tabGroupName;
	private long      updateFlag;

	public String getTabGroupName() {
		return tabGroupName;
	}

	public void setTabGroupName(String tabGroupName) {
		this.tabGroupName = tabGroupName;
	}

	public long getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(long updateFlag) {
		this.updateFlag = updateFlag;
	}
	
	/**根据输入字段映射到对象具体的字段里边,需判断返回值*/
	@Override
	public boolean fillData(Object obj) {
//		tabGroupName=fields.get(0);
//		updateFlag=Long.valueOf(fields.get(1)).longValue();
		return false;
	}
	
	/** 表组即po组名称，全英文，一般pog_打头*/
	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName();
	}
	
	/** 该实体对应的索引名列表，全英文，如：POSampleIndex1，以及所有索引对应的key获取方法*/
	@Override
	public Map<String,Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String,Method> indexMethods=new HashMap<String,Method>();
		indexMethods.put(getIndex1Name(),getClass().getMethod(getIndex1Name()));
		indexMethods.put(getIndex2Name(),getClass().getMethod(getIndex2Name()));
		indexMethods.put(getIndex3Name(),getClass().getMethod(getIndex3Name()));
		return indexMethods;
	}

	/** 索引1:建议将索引方法名作为索引名称*/
	public static String getIndex1Name() {
		return "getIndex1Key";
	}
	public String getIndex1Key() {
		return this.tabGroupName;
	}
	/** 索引2:建议将索引方法名作为索引名称*/
	public static String getIndex2Name() {
		return "getIndex2Key";
	}
	public String getIndex2Key() {
		return String.valueOf(this.updateFlag);
	}
	/** 索引3:建议将索引方法名作为索引名称*/
	public static String getIndex3Name() {
		return "getIndex3Key";
	}
	public String getIndex3Key() {
		return this.tabGroupName+Const.KEY_JOIN+String.valueOf(this.updateFlag);
	}
}
