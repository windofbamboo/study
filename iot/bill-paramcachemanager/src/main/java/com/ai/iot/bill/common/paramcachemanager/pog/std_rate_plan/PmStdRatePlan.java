package com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.pos.PoFeeBase;

/**
 * 标准资费计划PM
 * @author xue
 *
 */
public class PmStdRatePlan extends PmBase {

	private PoFeeBase poFeeBase = new PoFeeBase();

	public PmStdRatePlan(ParamClient paramClient) {
		super(paramClient);
	}

	/**
	 * 根据资费计划ID求取基本资费计划
	 * 
	 * @param tabGroupName
	 * @return
	 */
	public List<PoBase> getPoStdRatePlanByPlanId(int planId) {
		poFeeBase.setPlanId(planId);
		return paramClient.getDatasByKey(poFeeBase.getPoGroupName(), poFeeBase.getPoName(), PoFeeBase.getIndex1Name(),
				poFeeBase.getIndex1Key());
	}
	
	/**
	 * 求取全部标准资费计划
	 * @return
	 */
	public List<PoBase> getPoStdRatePlans() {
		return paramClient.getAllDatas(poFeeBase.getPoGroupName(), poFeeBase.getPoName());
	}
}
