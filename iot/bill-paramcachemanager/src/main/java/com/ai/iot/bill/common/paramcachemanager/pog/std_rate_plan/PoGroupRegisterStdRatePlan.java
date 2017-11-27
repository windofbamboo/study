package com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.pos.PoFeeBase;

public class PoGroupRegisterStdRatePlan extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_STD_RATE_PLAN.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poFeeBase = new PoFeeBase();
		allPoObjects.put(poFeeBase.getPoName(), poFeeBase);
	}

}
