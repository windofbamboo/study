package com.ai.iot.bill.common.paramcachemanager.pog.grant;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.grant.pos.PoGrantRule;

public class PoGroupRegisterGrant extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_GRANT.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poBase;
		poBase = new PoGrantRule();
		allPoObjects.put(poBase.getPoName(), poBase);
	}
}
