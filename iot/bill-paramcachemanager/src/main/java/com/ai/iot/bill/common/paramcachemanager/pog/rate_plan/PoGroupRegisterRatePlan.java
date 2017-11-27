package com.ai.iot.bill.common.paramcachemanager.pog.rate_plan;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.pos.PoRatePlan;

public class PoGroupRegisterRatePlan extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_RATE_PLAN.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poRatePlan = new PoRatePlan();

		allPoObjects.put(poRatePlan.getPoName(), poRatePlan);
	}

}
