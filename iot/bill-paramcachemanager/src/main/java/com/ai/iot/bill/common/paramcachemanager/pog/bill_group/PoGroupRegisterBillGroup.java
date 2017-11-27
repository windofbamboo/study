package com.ai.iot.bill.common.paramcachemanager.pog.bill_group;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoBillGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoCustomerValue;

public class PoGroupRegisterBillGroup extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_BILL_GROUP.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poBase;
		poBase = new PoBillGroup();
		allPoObjects.put(poBase.getPoName(), poBase);
		
		poBase = new PoCustomerValue();
		allPoObjects.put(poBase.getPoName(), poBase);
	}

}
