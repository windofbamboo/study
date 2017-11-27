package com.ai.iot.bill.common.paramcachemanager.pog;

import java.util.HashMap;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;

/**
 * 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [PO注册抽象类，将所有的PO注册到一个组] 
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 */
public abstract class PoGroupRegister {
	///po注册的对象集
	protected Map<String,PoBase> allPoObjects=new HashMap<String,PoBase>();
	
	public Map<String,PoBase> getAllPoObjects(){
		return allPoObjects;
	}
	
	///返回组名
	public PoBase getPoObjects(String poName){
		return allPoObjects.get(poName);
	}
	
	public abstract String getPoGroupName();
	
	///注册所有的Po对象
	public abstract void setAllPoObjects();
}
