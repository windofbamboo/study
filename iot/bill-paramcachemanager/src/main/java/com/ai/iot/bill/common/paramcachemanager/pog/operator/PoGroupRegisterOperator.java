package com.ai.iot.bill.common.paramcachemanager.pog.operator;

import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;

public class PoGroupRegisterOperator extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_OPERATOR.getName();
	}

	@Override
	public void setAllPoObjects() {
		//PoBase poBase;
		//poBase=new PoFeeBase();allPoObjects.put(poBase.getPoName(), poBase);
	}

}
