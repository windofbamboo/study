package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qianwx
 */
public class PoAutorule extends PoBase implements Serializable {
	private static final long serialVersionUID = -8399881171176158431L;
	@JSONField(name = "acctId")
	private long acctId;// 目标账户id
	@JSONField(name = "ruleId")
	private long ruleId;// 规则id
	@JSONField(name = "ruleVerId")
	private int ruleVerId;// 版本id
	@JSONField(name = "ruleType")
	private int ruleType;// 规则类型 0 普通账户 1 运营商 2 省份
	@JSONField(name = "ruleStatus")
	private int ruleStatus;// 规则状态 0 激活 1 停用 2删除
	@JSONField(name = "isUse")
	private int isUse;// 规则是否可用 0 false 1 true
	@JSONField(name = "operatorCode")
	private String operatorCode;// 目标运营商
	@JSONField(name = "provCode")
	private String provCode; // 目标省份
	@JSONField(name = "triggerId")
	private int triggerId = -1;// 触发器id
	@JSONField(name = "triggerArg1")
	private String triggerArg1;// 触发器参数1
	@JSONField(name = "triggerArg2")
	private String triggerArg2;// 触发器参数2
	@JSONField(name = "triggerArg3")
	private String triggerArg3;// 触发器参数3
	@JSONField(name = "filter1Arg")
	private String filter1Arg;// 筛选器参数1
	@JSONField(name = "filter2Arg")
	private String filter2Arg;// 筛选器参数2
	@JSONField(name = "filter3Arg")
	private String filter3Arg;// 筛选器参数3
	@JSONField(name = "filter101Arg")
	private String filter101Arg;// 筛选器101参数
	@JSONField(name = "filter102Arg")
	private String filter102Arg;// 筛选器102参数
	@JSONField(name = "filter103Arg")
	private String filter103Arg;// 筛选器103参数
	@JSONField(name = "filter104Arg")
	private String filter104Arg;// 筛选器104参数
	@JSONField(name = "filter105Arg")
	private String filter105Arg;// 筛选器105参数
	@JSONField(name = "filter106Arg")
	private String filter106Arg;// 筛选器106参数
	@JSONField(name = "filter107Arg")
	private String filter107Arg;// 筛选器107参数
	@JSONField(name = "filter108Arg")
	private String filter108Arg;// 筛选器108参数
	@JSONField(name = "filter109Arg")
	private String filter109Arg;// 筛选器109参数
	@JSONField(name = "filter110Arg")
	private String filter110Arg;// 筛选器110参数
	@JSONField(name = "filter201Arg")
	private String filter201Arg;// 筛选器201参数
	@JSONField(name = "filter202Arg")
	private String filter202Arg;// 筛选器202参数
	@JSONField(name = "filter203Arg")
	private String filter203Arg;// 筛选器203参数
	@JSONField(name = "filter204Arg")
	private String filter204Arg;// 筛选器204参数
	@JSONField(name = "filter205Arg")
	private String filter205Arg;// 筛选器205参数
	@JSONField(name = "filter301Arg")
	private String filter301Arg;// 筛选器301参数
	@JSONField(name = "filter302Arg")
	private String filter302Arg;// 筛选器302参数
	@JSONField(name = "filter303Arg")
	private String filter303Arg;// 筛选器303参数
	@JSONField(name = "filter304Arg")
	private String filter304Arg;// 筛选器304参数
	@JSONField(name = "filter305Arg")
	private String filter305Arg;// 筛选器305参数
	@JSONField(name = "filter401Arg")
	private String filter401Arg;// 筛选器401参数
	@JSONField(name = "filter402Arg")
	private String filter402Arg;// 筛选器402参数
	@JSONField(name = "filter403Arg")
	private String filter403Arg;// 筛选器403参数
	@JSONField(name = "filter404Arg")
	private String filter404Arg;// 筛选器404参数
	@JSONField(name = "filter405Arg")
	private String filter405Arg;// 筛选器405参数
	@JSONField(name = "updateTime")
	private long updateTime;// 更新时间

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		ruleId = Long.valueOf(fields.get(0));
		ruleVerId = Integer.valueOf(fields.get(1));
		ruleType = Integer.valueOf(fields.get(2));
		ruleStatus = Integer.valueOf(fields.get(3));
		isUse = Integer.valueOf(fields.get(4));
		operatorCode = String.valueOf(fields.get(5));
		provCode = String.valueOf(fields.get(6));
		acctId = Long.valueOf(fields.get(7));
		String _triggerId = fields.get(8);
		if (null != _triggerId && !"".equals(_triggerId)) {
			triggerId = Integer.valueOf(_triggerId);
		} else {
			triggerId = 0;
		}
		triggerArg1 = fields.get(9);
		triggerArg2 = fields.get(10);
		triggerArg3 = fields.get(11);
		filter1Arg = fields.get(12);
		filter2Arg = fields.get(13);
		filter3Arg = fields.get(14);
		filter101Arg = fields.get(15);
		filter102Arg = fields.get(16);
		filter103Arg = fields.get(17);
		filter104Arg = fields.get(18);
		filter105Arg = fields.get(19);
		filter106Arg = fields.get(20);
		filter107Arg = fields.get(21);
		filter108Arg = fields.get(22);
		filter109Arg = fields.get(23);
		filter110Arg = fields.get(24);
		filter201Arg = fields.get(25);
		filter202Arg = fields.get(26);
		filter203Arg = fields.get(27);
		filter204Arg = fields.get(28);
		filter205Arg = fields.get(29);
		filter301Arg = fields.get(30);
		filter302Arg = fields.get(31);
		filter303Arg = fields.get(32);
		filter304Arg = fields.get(33);
		filter305Arg = fields.get(34);
		filter401Arg = fields.get(35);
		filter402Arg = fields.get(36);
		filter403Arg = fields.get(37);
		filter404Arg = fields.get(38);
		filter405Arg = fields.get(39);
		String _updateTime = fields.get(40);
		if (null != _updateTime && !"".equals(_updateTime)) {
			updateTime = Long.valueOf(_updateTime);
		} else {
			updateTime = 0;
		}
		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
		Map<String, Method> indexMethods = new HashMap<String, Method>();
		indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
		return indexMethods;
	}

	public String getIndex1Key() {
		return String.valueOf(this.triggerId);
	}

	public static String getIndex1Name() {
		return "getIndex1Key";
	}

	public long getAcctId() {
		return acctId;
	}

	public long getRuleId() {
		return ruleId;
	}

	public int getRuleVerId() {
		return ruleVerId;
	}

	public int getRuleType() {
		return ruleType;
	}

	public int getRuleStatus() {
		return ruleStatus;
	}

	public int getIsUse() {
		return isUse;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public String getProvCode() {
		return provCode;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public String getTriggerArg1() {
		return triggerArg1;
	}

	public String getTriggerArg2() {
		return triggerArg2;
	}

	public String getTriggerArg3() {
		return triggerArg3;
	}

	public String getFilter1Arg() {
		return filter1Arg;
	}

	public String getFilter2Arg() {
		return filter2Arg;
	}

	public String getFilter3Arg() {
		return filter3Arg;
	}

	public String getFilter101Arg() {
		return filter101Arg;
	}

	public String getFilter102Arg() {
		return filter102Arg;
	}

	public String getFilter103Arg() {
		return filter103Arg;
	}

	public String getFilter104Arg() {
		return filter104Arg;
	}

	public String getFilter105Arg() {
		return filter105Arg;
	}

	public String getFilter106Arg() {
		return filter106Arg;
	}

	public String getFilter107Arg() {
		return filter107Arg;
	}

	public String getFilter108Arg() {
		return filter108Arg;
	}

	public String getFilter109Arg() {
		return filter109Arg;
	}

	public String getFilter110Arg() {
		return filter110Arg;
	}

	public String getFilter201Arg() {
		return filter201Arg;
	}

	public String getFilter202Arg() {
		return filter202Arg;
	}

	public String getFilter203Arg() {
		return filter203Arg;
	}

	public String getFilter204Arg() {
		return filter204Arg;
	}

	public String getFilter205Arg() {
		return filter205Arg;
	}

	public String getFilter301Arg() {
		return filter301Arg;
	}

	public String getFilter302Arg() {
		return filter302Arg;
	}

	public String getFilter303Arg() {
		return filter303Arg;
	}

	public String getFilter304Arg() {
		return filter304Arg;
	}

	public String getFilter305Arg() {
		return filter305Arg;
	}

	public String getFilter401Arg() {
		return filter401Arg;
	}

	public String getFilter402Arg() {
		return filter402Arg;
	}

	public String getFilter403Arg() {
		return filter403Arg;
	}

	public String getFilter404Arg() {
		return filter404Arg;
	}

	public String getFilter405Arg() {
		return filter405Arg;
	}

	public long getUpdateTime() {
		return updateTime;
	}
}
