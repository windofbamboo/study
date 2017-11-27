package com.ai.iot.bill.common.paramcachemanager.pog.zone;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoZone;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoApn;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoDataStreamGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoProvider;

public class PoGroupRegisterZone extends PoGroupRegister {

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_ZONE.getName();
	}

	@Override
	public void setAllPoObjects() {
		PoBase poZone = new PoZone();
		allPoObjects.put(poZone.getPoName(), poZone);

		PoBase poApn = new PoApn();
		allPoObjects.put(poApn.getPoName(), poApn);

		PoBase poProvider = new PoProvider();
		allPoObjects.put(poProvider.getPoName(), poProvider);

		PoBase poDataStreamGroup = new PoDataStreamGroup();
		allPoObjects.put(poDataStreamGroup.getPoName(), poDataStreamGroup);
	}

}
