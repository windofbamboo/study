package com.ai.iot.bill.common.paramcachemanager.pog.rate_plan;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.pos.PoRatePlan;

/**
 * 资费计划PM
 * @author xue
 *
 */
public class PmRatePlan extends PmBase {

	private PoRatePlan poRatePlan = new PoRatePlan();
	
	public PmRatePlan(ParamClient paramClient) {
		super(paramClient);
	}

	/**
	 * 根据资费计划版本ID获取资费计划列表
	 * @param VersionId
	 * @return
	 */
	public List<PoBase> getPoRatePlanByVersionId(int versionId) {
		poRatePlan.setPlanVersionId(versionId);
		return paramClient.getDatasByKey(poRatePlan.getPoGroupName(), poRatePlan.getPoName(), PoRatePlan.getIndex1Name(),
				poRatePlan.getIndex1Key());
	}
	
	/**
	 * 根据资费计划id获取资费计划列表
	 * @param acctId
	 * @return
	 */
	public List<PoRatePlan> getPoRatePlanByPlanId(int planId){
		poRatePlan.setPlanId(planId);
		return paramClient.getTrueDatasByKey(poRatePlan.getPoGroupName(), poRatePlan.getPoName(), PoRatePlan.getIndex2Name(),
				poRatePlan.getIndex2Key());
	}
	
}
