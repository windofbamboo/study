package com.ai.iot.bill.common.paramcachemanager.pog.grant;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.grant.pos.PoGrantRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 授权PM
 * @author xue
 *
 */
public class PmGrant extends PmBase{
	public static final Logger logger = LoggerFactory.getLogger(PmGrant.class);
	
	private PoGrantRule poGrantRule = new PoGrantRule();
	
	public PmGrant(ParamClient paramClient) {
		super(paramClient);
	}

	/**
	 * 获取授权量
	 * @param ruleId
	 * @return
	 */
	public List<PoBase> getGrantRule(int ruleId) {
		poGrantRule.setRuleId(ruleId);
		return paramClient.getDatasByKey(poGrantRule.getPoGroupName(), poGrantRule.getPoName(), PoGrantRule.getIndex1Name(),
				poGrantRule.getIndex1Key());
	}
	
	/**
	 * 获取所有授权规则
	 */
	public List<PoBase> getAllGrantRule(){
		return paramClient.getAllDatas(poGrantRule.getPoGroupName(), poGrantRule.getPoName());
	}
}
