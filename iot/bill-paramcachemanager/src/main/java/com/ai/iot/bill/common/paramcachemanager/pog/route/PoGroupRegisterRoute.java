package com.ai.iot.bill.common.paramcachemanager.pog.route;

import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;

public class PoGroupRegisterRoute extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_ROUTE.getName();
	}

	@Override
	public void setAllPoObjects() {
		//PoBase poBase;
		//poBase=new PoFeeBase();allPoObjects.put(poBase.getPoName(), poBase);
	}

}
