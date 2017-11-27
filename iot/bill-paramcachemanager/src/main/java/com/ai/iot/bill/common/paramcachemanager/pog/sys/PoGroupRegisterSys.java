package com.ai.iot.bill.common.paramcachemanager.pog.sys;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.pos.*;

/**
 * 系统PO组的注册器，本PO组包含哪些PO 需要在这里注册
 *
 */
public class PoGroupRegisterSys extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poBase = new PoCycle();
		allPoObjects.put(poBase.getPoName(), poBase);
		
		poBase = new PoScriptType();
		allPoObjects.put(poBase.getPoName(), poBase);
		
		poBase = new PoScriptCode();
		allPoObjects.put(poBase.getPoName(), poBase);
		
		poBase = new PoSystemParam();
		allPoObjects.put(poBase.getPoName(), poBase);

		poBase = new PoAutorule();
		allPoObjects.put(poBase.getPoName(), poBase);

		poBase = new PoAutoRuleOperCont();
		allPoObjects.put(poBase.getPoName(), poBase);

		poBase = new PoStatus();
		allPoObjects.put(poBase.getPoName(), poBase);
	}
}
