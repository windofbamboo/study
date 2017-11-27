package com.ai.iot.bill.common.paramcachemanager.pog.sample;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;

/**
 * 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [POGroupSample组类] 
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 */
public class PoGroupRegisterSample extends PoGroupRegister{
	
	///返回组名
	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SAMPLE.getName();
	}	
		
	///注册所有的Po对象
	@Override
	public void setAllPoObjects() {
		PoBase poBase;
		poBase=new PoSample();allPoObjects.put(poBase.getPoName(), poBase);
	}	
	
}
