package com.ai.iot.bill.cb.base.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.pos.PoCycle;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.pos.PoSystemParam;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManager;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManagerConfigure;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.PmBillGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.grant.PmGrant;
import com.ai.iot.bill.common.paramcachemanager.pog.grant.pos.PoGrantRule;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.PmRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.PmStdRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.PmSys;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.PmZone;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 参数管理类
 * 
 * @author xue
 *
 */
public class ParamManager {
	private static final Logger logger = LoggerFactory.getLogger(ParamManager.class);
	private ParamCacheManager manager;
	private ParamClient client;
	private PmRatePlan pmRatePlan;
	private PmStdRatePlan pmStdRatePlan;
	private PmSys pmSys;
	private PmZone pmZone;
	private PmBillGroup pmBillGroup;
	private PmGrant pmGrant;

	public ParamManager() {
		try {
			initClient();
		} catch (Exception e) {
			logger.error("init ParamClient fail" + Arrays.toString(e.getStackTrace()));
		}

		initPm();
	}

	public void initClient() throws Exception {
		 ParamCacheManagerConfigure.getGlobalInstance().init();
		 // TODO:serivalType从配置文件中读取
		 ParamCacheConfigure configure =
		 ParamCacheManagerConfigure.getGlobalInstance().setSerializeType(1);
		 configure.setSerializeType(1);
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_RATE_PLAN.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_STD_RATE_PLAN.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_ADDUP.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_ZONE.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_BILL_GROUP.getName());
		 configure.addUsedPoGroupNames(PoGroupRegisterFactory.PoGroupNameEnum.POG_GRANT.getName());
		 manager = ParamCacheManager.getGlobalInstance();
		 manager.initialize(configure);
		 client = manager.getInstanceClient();
		 client.register();
	}

	public void initPm() {
		pmRatePlan = new PmRatePlan(client);
		pmStdRatePlan = new PmStdRatePlan(client);
		pmSys = new PmSys(client);
		pmZone = new PmZone(client);
		pmBillGroup = new PmBillGroup(client);
		pmGrant = new PmGrant(client);
	}

	/**
	 * 根据版本ID读取资费计划参数
	 * @param planVersionId
	 * @return
	 */
	public List<PoBase> getRatePlan(int planVersionId) {
		List<PoBase> ratePlans = pmRatePlan.getPoRatePlanByVersionId(planVersionId);
		if (!CheckNull.isNull(ratePlans)) {
			return ratePlans;
		}
		return Collections.emptyList();
	}

	/**
	 * 根据资费计划ID读取标准资费计划参数
	 * @param planId
	 * @return
	 */
	public PoBase getStdRatePlan(int planId) {
		List<PoBase> stdRatePlans = pmStdRatePlan.getPoStdRatePlanByPlanId(planId);
		if (!CheckNull.isNull(stdRatePlans)) {
			return stdRatePlans.get(0);
		}
		return null;
	}

	/**
	 * 读取所有标准资费计划
	 * @return
	 */
	public List<PoBase> getStdRatePlans() {
		return pmStdRatePlan.getPoStdRatePlans();
	}

	/**
	 * 根据区域ID读取区域参数
	 * @param zoneId
	 * @return
	 */
	public PoBase getZone(int zoneId) {
		List<PoBase> zones = pmZone.getPoZoneByZoneId(zoneId);
		if (!CheckNull.isNull(zones)) {
			return zones.get(0);
		}
		return null;
	}

	/**
	 * 读取账期
	 * @param sysDate
	 * @return
	 */
	public PoCycle getBillCycle(String sysDate) {
		 return pmSys.getCycle(sysDate);
	}

	/**
	 * 读取JS脚本类型
	 * @param module
	 * @return
	 */
	public List<PoBase> getScriptType(String module) {
		 return pmSys.getScriptType(module);
	}

	/**
	 * 根据JS脚本类型读取执行过程
	 * @param typeId
	 * @return
	 */
	public List<PoBase> getScriptCode(int typeId) {
		 return pmSys.getScriptCode(typeId);
	}

	/**
	 * 根据规则ID读取授权规则参数
	 * @param ruleId
	 * @return
	 */
	public List<PoBase> getGrantRule(int ruleId) {
		return pmGrant.getGrantRule(ruleId);
	}

	/**
	 * 根据计费 组ID读取计费组参数
	 * @param groupId
	 * @return
	 */
	public PoBase getBillGroup(int groupId) {
		List<PoBase> billGroups = pmBillGroup.getBillGroup(groupId);
		if (!CheckNull.isNull(billGroups)) {
			return billGroups.get(0);
		}
		return null;
	}
	
	/**
	 * 根据自定义组ID求取自定义组值
	 * @param groupId
	 * @return
	 */
	public List<PoBase> getCustomerValue(int groupId) {
		return pmBillGroup.getCustomerValue(groupId);
	}

	/**
	 * 获取APN
	 * 
	 * @param apnCode
	 * @return
	 */
	public List<PoBase> getPoApn() {
		return pmZone.getPoApn();
	}

	/**
	 * 获取运营商
	 * 
	 * @param providerCode
	 * @return
	 */
	public List<PoBase> getPoProvider() {
		return pmZone.getPoProvider();
	}

	/**
	 * 获取数据流
	 * 
	 * @param dataStreamsCode
	 * @return
	 */
	public List<PoBase> getPoDataSreamGroup() {
		return pmZone.getPoDataSreamGroup();
	}

	/**
	 * 读取APN参数
	 * @param apnCode
	 * @return
	 */
	public int getApnId(String apnCode) {
		return pmZone.getApnId(apnCode);
	}

	/**
	 * 读取数据流参数
	 * @param dataCode
	 * @return
	 */
	public int getDataId(String dataCode) {
		return pmZone.getDataSreamGroupId(dataCode);
	}

	/**
	 * 读取运营商参数
	 * @param providerCode
	 * @return
	 */
	public int getProviderId(String providerCode) {
		return pmZone.getProviderId(providerCode);
	}
	
	/**
	 * 匹配授权量
	 * @param apn
	 * @param operator
	 * @param dataStream
	 * @param timeIdle
	 * @return
	 */
	public PoBase getGrantRule(String apnni, int providerId, String rgId) {
		List<PoBase> rules = pmGrant.getAllGrantRule();
		if (!CheckNull.isNull(rules)) {
			for (PoBase poBase : rules) {
				PoGrantRule poGrantRule = (PoGrantRule) poBase;
				int apnId = getApnId(apnni);
				
				if ((poGrantRule.getApnId() == 0 || poGrantRule.getApnId() == apnId)
						&& (poGrantRule.getProviderId() == 0 || poGrantRule.getApnId() == providerId)
						&& (poGrantRule.getRgId().equals("*") || poGrantRule.getRgId().equals(rgId))) {
					return poGrantRule;
				}
			}
		}
		return null;
	}
	
	/**
	 * 读取系统参数
	 * @param paramType
	 * @param paramName
	 * @return
	 */
	public PoBase getSystemParam(String paramType, String paramName) {
		List<PoBase> systemParams = pmSys.getSystemType(paramType);
		if (CheckNull.isNull(systemParams)) {
			return null;
		}

		PoBase pobase = null;
		for (PoBase poBase : systemParams) {
			PoSystemParam poSystemParam = (PoSystemParam)poBase;
			if (poSystemParam.getParamName().equals(paramName)) {
				pobase = poBase;
				break;
			}
		}
		return pobase;
	}
	
	/**
	 * 根据类型查找参数
	 * @param paramType
	 * @return
	 */
	public PoBase getSystemParam(String paramType) {
		List<PoBase> systemParams = pmSys.getSystemType(paramType);
		if (!CheckNull.isNull(systemParams)) {
			return systemParams.get(0);
		}
		return null;
	}
	
	/**
	 * 根据运营商ID读取运营商编码参数
	 * @param providerId
	 * @return
	 */
	public PoBase getPoProviderById(int providerId) {
		return pmZone.getPoProviderById(providerId);
	}
	
	/**
	 * 获取当前账期
	 * 
	 * @return
	 * @since 1.0
	 */
	public int getCurrentCycleId(Date date) {
		return pmSys.getCycle(date);
	}

	/**
	 * 获取账期对象
	 * 
	 * @param cycleId
	 * @return
	 * @since 1.0
	 */
	public PoCycle getPoCycle(int cycleId) {
		return pmSys.getPoCycle(cycleId);
	}
	
	/**
	 * 根据IMSI读取运营商参数
	 * @param imsi
	 * @return
	 */
	public PoBase getPoProviderByImsi(String imsi) {
		return pmZone.getProviderByImsi(imsi);
	}
	
	/**
	 * 根据MSC读取运营商参数
	 * @param mscCode
	 * @return
	 */
	public PoBase getPoProviderByMscCode(String mscCode) {
		return pmZone.getProviderByMscCode(mscCode);
	}
}
