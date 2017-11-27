package com.ai.iot.bill.common.paramcachemanager.pog.bill_group;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoBillGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoCustomerValue;

/**
 * 计费组PM
 * @author xue
 *
 */
public class PmBillGroup extends PmBase {

	PoBillGroup poBillGroup = new PoBillGroup();
	
	PoCustomerValue poCustomerValue = new PoCustomerValue();
	
	public PmBillGroup(ParamClient paramClient) {
		super(paramClient);
	}

	/**
	 * 根据计费组ID读取计费组参数
	 * @param groupId
	 * @return
	 */
	public List<PoBase> getBillGroup(int groupId) {
		poBillGroup.setGroupId(groupId);
		return paramClient.getDatasByKey(poBillGroup.getPoGroupName(), poBillGroup.getPoName(), PoBillGroup.getIndex1Name(),
				poBillGroup.getIndex1Key());
	}
	
	/**
	 * 根据自定义组ID求取自定义组值
	 * @param groupId
	 * @return
	 */
	public List<PoBase> getCustomerValue(int groupId) {
		poCustomerValue.setGroupId(groupId);
		return paramClient.getDatasByKey(poCustomerValue.getPoGroupName(), poCustomerValue.getPoName(), PoCustomerValue.getIndex1Name(),
				poCustomerValue.getIndex1Key());
	}
}
