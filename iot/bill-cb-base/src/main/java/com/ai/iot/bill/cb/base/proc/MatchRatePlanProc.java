package com.ai.iot.bill.cb.base.proc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.cb.base.entity.RateConst;
import com.ai.iot.bill.cb.base.entity.RateError;
import com.ai.iot.bill.cb.base.entity.RateGlobalInfo;
import com.ai.iot.bill.cb.base.entity.RatePlan;
import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoBillGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.pos.PoCustomerValue;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.pos.PoRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.pos.PoFeeBase;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoProvider;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.pos.PoZone;
import com.ai.iot.bill.common.util.CdrConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.StringUtil;

/**
 * 区域资费计划参数类
 * @author xue
 *
 */
public class MatchRatePlanProc {

	private static final Logger logger = LoggerFactory.getLogger(MatchRatePlanProc.class);

	private RateGlobalInfo rateGlobalInfo;

	public MatchRatePlanProc(RateGlobalInfo rateGlobalInfo) {
		this.rateGlobalInfo = rateGlobalInfo;
	}
	
	/**
	 * 根据话单属性匹配区域
	 * @param cdr
	 * @param row
	 * @param poRatePlan
	 * @return
	 */
	public RatePlan matchRatePlan(Cdr cdr, int row, PoRatePlan poRatePlan) {
		logger.info("======MatchRatePlanProc matchRatePlan======");
		if (CheckNull.isNull(poRatePlan)) {
			cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_NO_RATE_PLAN);
			return null;
		}
		
		// 如果RatPlan业务类型与话单业务类型不匹配 返回
		if(poRatePlan.getBizType() != cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE)) {
			return null;
		}
		
		// 求取累计量参数
		if (poRatePlan.getBillId() == 0) {
			cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_NO_ADDUP);
			return null;
		}
		
		// 准备返回值
		RatePlan ratePlan = setRatePlan(poRatePlan);
		
		// 求取区域
		PoZone poZone = (PoZone) rateGlobalInfo.getParamManager().getZone(poRatePlan.getZoneId());
		if (CheckNull.isNull(poZone)) {
			cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_NO_ZONE);
			return null;
		}
		
		// 求取到访运营商参数
		String visitProviderId = cdr.get(CdrAttri.ATTRI_VISIT_PROVIDER_ID);
		if (CheckNull.isNull(visitProviderId)) {
			if (!getVisitProvider(cdr, row)) {
				return null;
			}
		}
		
		// 匹配
		if (RateConst.ZONE_TYPE_NORMAL == poZone.getZoneType()) {
			// 普通区域
			if (!matchAddup(cdr, row, poRatePlan.getBizType(), poRatePlan.getCallType(), poRatePlan.getGroupId())) {
				cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_ADDUP);
				return null;
			}
			if (matchZone(cdr, row, poZone)) {
				cdr.setInt(CdrAttri.ATTRI_ZONE_ID, row, poZone.getZoneId());
				return ratePlan;
			}
		} else if (RateConst.ZONE_TYPE_OTHER == poZone.getZoneType()) {
			// 其它区域
			PoFeeBase poFeeBase = matchStdRatePlan(cdr, row);
			if (!CheckNull.isNull(poFeeBase)) {
				cdr.setInt(CdrAttri.ATTRI_ZONE_ID, row, poZone.getZoneId());
				ratePlan.setBillId(poFeeBase.getBillId());
//				ratePlan.setZoneId(poFeeBase.getZoneId());
				return ratePlan;
			}
		}

		return null;
	}

	/**
	 * 匹配累计量表
	 * @param cdr
	 * @param row
	 * @param addupInfo
	 * @return
	 */
	private boolean matchAddup(Cdr cdr, int row, int bizType, int callType, int groupId) {
		// 匹配业务类型
		if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) != bizType) {
			return false;
		}
		
		if (!matchCallType(cdr, callType)) {
//			cdr.set(CdrAttri.ATTRI_ERROR_CODE, row, String.valueOf(RateError.E_ADDUP_CALL_TYPE));
			return false;
		}
		
		// 求取目标计费组，如果group_id为0，则支持所有
		if (!matchBillGroup(cdr, groupId)) {
//			cdr.set(CdrAttri.ATTRI_ERROR_CODE, row, String.valueOf(RateError.E_BILLING_GROUP));
			return false;
		}
		
		return true;
	}
	
	/**
	 * 匹配计费组：只有语音和短信业务才有
	 * 除了拓展组，其他计费组只比较前缀
	 * @param cdr
	 * @param poBillGroup
	 * @return
	 */
	private boolean matchBillGroup(Cdr cdr, int billGroupId) {
		if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
			return true;
		}
		
		// 不做判断
		if (billGroupId <= 0) {
			return true;
		}
		
		PoBillGroup poBillGroup = (PoBillGroup) rateGlobalInfo.getParamManager().getBillGroup(billGroupId);
		if (CheckNull.isNull(poBillGroup)) {
			return false;
		}

		if (poBillGroup.getGroupType() == RateConst.BILL_GROUP_VALUE_TYPE_EXTEND) {
			// 拓展计费组
			List<Integer> extendGroupList = poBillGroup.getExtendGroupList();
			if (CheckNull.isNull(extendGroupList)) {
				return false;
			}
			for (int groupId : extendGroupList) {
				PoBillGroup poExtend = (PoBillGroup) rateGlobalInfo.getParamManager().getBillGroup(groupId);
				if (CheckNull.isNull(poExtend)) {
					continue;
				}
				
				// 拓展计费组的成员不会是拓展计费组
				if (matchCustomerBillGroup(cdr, groupId)) {
					return true;
				}
			}
		} else {
			// 非拓展计费组
			return matchCustomerBillGroup(cdr, billGroupId);
		}
		
		return false;
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	private boolean matchCustomerBillGroup(Cdr cdr, int groupId) {
		// 对方号码
		String msisdn_B = cdr.get(CdrAttri.ATTRI_MSISDN_B);
		List<PoBase> customerValueList = rateGlobalInfo.getParamManager().getCustomerValue(groupId);
		if (CheckNull.isNull(customerValueList)) {
			return false;
		}
		
		for (PoBase poBase : customerValueList) {
			PoCustomerValue poCustomerValue = (PoCustomerValue) poBase;
			if (poCustomerValue.getValueType() == RateConst.BILL_GROUP_VALUE_TYPE_PREFIX) {
				// 前缀匹配
				if (msisdn_B.startsWith(poCustomerValue.getValue())) {
					return true;
				}
			} else if (poCustomerValue.getValueType() == RateConst.BILL_GROUP_VALUE_TYPE_ALL) {
				// 全值匹配
				if (msisdn_B.equals(poCustomerValue.getValue())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 匹配区域
	 * 
	 * @param cdr
	 * @param row
	 * @param poZone
	 * @return
	 */
	private boolean matchZone(Cdr cdr, int row, PoZone poZone) {
		logger.info("======matchZone======");
		// 运营商筛选器:到区域运营商表中匹配，如果是流量业务则匹配漫游运营商编码，如果是短信业务则MSC，如果是语音业务则匹配漫游运营商IMSI
		int providerFilter = poZone.getProviderFilter();
		List<Integer> providerIdList = poZone.getProviderIdList();
		if (providerFilter == RateConst.FILTER_INCLUDE) {
			// 计划内
			if (CheckNull.isNull(providerIdList)) {
				return false;
			}
			boolean found = false;
			for (int providerId : providerIdList) {
				logger.info("======providerId======:" + providerId);
				if (cdr.getInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, row) == providerId) {
					found = true;
					logger.info("======right providerId======");
					break;
				}
				
				
//				PoProvider poProvider = (PoProvider) rateGlobalInfo.getParamManager().getPoProviderById(providerId);
//				if (CheckNull.isNull(poProvider)) {
//					continue;
//				}
//				logger.info("======poProvider======:" + poProvider.toString());
//				if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_VOICE) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getProviderImsi())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getProviderImsi())) {
//						found = true;
//						break;
//					}
//				} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_SMS) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getMscCode())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getMscCode())) {
//						found = true;
//						break;
//					}
//				} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getProviderImsi())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getProviderImsi())) {
//						found = true;
//						break;
//					}
//					logger.info("======ProviderImsi======" + poProvider.getProviderImsi());
//				}
			}
			
			// 如果不在计划内则返回false
			if (!found) {
				return false;
			}
		} else if (providerFilter == RateConst.FILTER_EXCLUDE) {
			// 计划外
			if (CheckNull.isNull(providerIdList)) {
				return false;
			}
			boolean found = false;
			for (int providerId : providerIdList) {
				if (cdr.getInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, row) == providerId) {
					found = true;
					break;
				}
				
				
//				PoProvider poProvider = (PoProvider) rateGlobalInfo.getParamManager().getPoProviderById(providerId);
//				if (CheckNull.isNull(poProvider)) {
//					continue;
//				}
//				if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_VOICE) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getProviderImsi())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getProviderImsi())) {
//						return false;
//					}
//				} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_SMS) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getMscCode())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getMscCode())) {
//						return false;
//					}
//				} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
//					if (CheckNull.isNull(visitProviderCode) || CheckNull.isNull(poProvider.getProviderImsi())) {
//						continue;
//					}
//					if (visitProviderCode.startsWith(poProvider.getProviderImsi())) {
//						return false;
//					}
//				}
			}
			
			if(found) {
				return false;
			}
		} else if (providerFilter == RateConst.FILTER_NONE) {
			// 不做判断
		}
		
		if (poZone.getTimeId() != StringUtil.toInt(cdr.get(CdrAttri.ATTRI_TIME_IDLE, row))) {
			return false;
		}

		// APN筛选器
		int apnFilter = poZone.getApnFilter();
		String apn = cdr.get(CdrAttri.ATTRI_APNNI);
		String apns = poZone.getApns();
		// 计划内
		if (apnFilter == RateConst.FILTER_INCLUDE) {
			if (CheckNull.isNull(apn) || CheckNull.isNull(apns)) {
				return false;
			}
			if (apns.indexOf(apn) == -1) {
				return false;
			}
		} else if (apnFilter == RateConst.FILTER_EXCLUDE) {
			// 计划外
			if (CheckNull.isNull(apn) || CheckNull.isNull(apns)) {
				return false;
			}
			if (apns.indexOf(apn) > 0) {
				return false;
			}
		} else if (apnFilter == RateConst.FILTER_NONE) {
			// 不做判断
		}

		int rgFilter = poZone.getRgFilter();
		String dataStream = cdr.get(CdrAttri.ATTRI_DATA_STREAMS_CODE, row);
		String dataStreams = poZone.getDataStreams();
		// 计划内
		if (rgFilter == RateConst.FILTER_INCLUDE) {
			if (CheckNull.isNull(dataStream) || CheckNull.isNull(dataStreams)) {
				return false;
			}
			if (dataStreams.indexOf(dataStream) == -1) {
				return false;
			}
		} else if (rgFilter == RateConst.FILTER_EXCLUDE) {
			// 计划外
			if (CheckNull.isNull(dataStream) || CheckNull.isNull(dataStreams)) {
				return false;
			}
			if (dataStreams.indexOf(dataStream) > 0) {
				return false;
			}
		} else if (rgFilter == RateConst.FILTER_NONE) {
			// 不做判断
		}
		
		return true;
	}

	/**
	 * 匹配标准资费
	 * 
	 * @param cdr
	 * @return
	 */
	private PoFeeBase matchStdRatePlan(Cdr cdr, int row) {
		List<PoBase> stdRatePlans = rateGlobalInfo.getParamManager().getStdRatePlans();
		if (CheckNull.isNull(stdRatePlans)) {
			cdr.set(CdrAttri.ATTRI_ERROR_CODE, row, String.valueOf(RateError.E_NO_STD_RATE_PLAN));
			return null;
		}

		for (PoBase poStdRatePlan : stdRatePlans) {
			PoFeeBase poFeeBase = (PoFeeBase) poStdRatePlan;
			if (poFeeBase.getImsi().equals("*")) {
				
			} else {
				if (CheckNull.isNull(cdr.get(CdrAttri.ATTRI_IMSI)) || CheckNull.isNull(poFeeBase.getImsi())) {
					continue;
				}
				if (!cdr.get(CdrAttri.ATTRI_IMSI).startsWith(poFeeBase.getImsi())) {
					continue;
				}
			}

			// 求取标准资费的区域
			PoZone poZone = (PoZone) rateGlobalInfo.getParamManager().getZone(poFeeBase.getZoneId());
			if (CheckNull.isNull(poZone)) {
				continue;
			}
			if (!matchAddup(cdr, row, poFeeBase.getBizType(), poFeeBase.getCallType(), poFeeBase.getGroupId())) {
				continue;
			}
			if (!matchZone(cdr, row, poZone)) {
				continue;
			}
			return poFeeBase;
		}

		return null;
	}

	/**
	 * 匹配呼叫类型
	 * @param cdr
	 * @param callType
	 * @return
	 */
	private boolean matchCallType(Cdr cdr, int callType) {
		logger.info("======matchCallType======");
		logger.info("param callType:" + callType);
		logger.info("cdr callType:" + cdr.getInt(CdrAttri.ATTRI_CALL_TYPE));
		if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
			return true;
		}
		
		if (callType == CdrConst.CALL_TYPE_ALL) {
			return true;
		}
		
		if (callType == cdr.getInt(CdrAttri.ATTRI_CALL_TYPE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据传入频率计费day
	 * @param poRatePlan
	 * @return
	 */
	public int roundingFreq2Day(RatePlan ratePlan) {
		int day = 0;
		if (ratePlan.getRoundingFreq() == RateConst.ROUNDING_FREQ_DAY) {
			day = StringUtil.toInt(rateGlobalInfo.getCdr().get(CdrAttri.ATTRI_BEGIN_DATE));
		}
		return day;
	}
	
	/**
	 * 规整累计量
	 * 
	 * @param roundingUnit
	 * @param value
	 * @return
	 */
	public long roundValue(long value, long roundingUnit) {
		if (roundingUnit > 1) {
			long mod = value % roundingUnit;
			if (mod > 0) {
				return value + roundingUnit - mod;
			}
		}
		
		return value;
	}
	
	/**
	 * 将PoRatePlan参数设置到RatePlan公共类中
	 * @param poRatePlan
	 * @return
	 */
	private RatePlan setRatePlan(PoRatePlan poRatePlan) {
		RatePlan ratePlan = new RatePlan();
		ratePlan.setPlanVersionId(poRatePlan.getPlanVersionId());
		ratePlan.setPlanType(poRatePlan.getPlanType());
		ratePlan.setBillId(poRatePlan.getBillId());
		ratePlan.setBizType(poRatePlan.getBizType());
		ratePlan.setCallType(poRatePlan.getCallType());
		ratePlan.setGroupId(poRatePlan.getGroupId());
		ratePlan.setLimitTag(poRatePlan.getLimitTag());
		ratePlan.setRoundingFreq(poRatePlan.getRoundingFreq());
		ratePlan.setRoundingUnit(poRatePlan.getRoundingUnit());
		ratePlan.setShared(poRatePlan.getShared());
		ratePlan.setUsageLimit(poRatePlan.getUsageLimit());
		ratePlan.setZoneId(poRatePlan.getZoneId());
		ratePlan.setZoneUsageLimit(poRatePlan.getZoneUsageLimit());
		ratePlan.setInlcudedValueUsage(poRatePlan.getIncludedValueUsage());
		return ratePlan;
	}
	
	/**
	 * 求取运营商参数
	 * @param cdr
	 * @param row
	 */
	private boolean getVisitProvider(Cdr cdr, int row) {
		// 运营商筛选器:到区域运营商表中匹配，如果是流量业务则匹配漫游运营商编码，如果是短信业务则MSC，如果是语音业务则匹配漫游运营商IMSI
		String visitProviderCode = cdr.get(CdrAttri.ATTRI_VISIT_PROVIDER_CODE);
		logger.info("======matchZone======");
		logger.info("======visitProviderCode======:" + visitProviderCode);
		// 根据visitProviderCode求取visitProviderId，输出到详单表
		if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_VOICE
				|| cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_DATA) {
			PoProvider poProvider = (PoProvider) rateGlobalInfo.getParamManager().getPoProviderByImsi(visitProviderCode);
			if (CheckNull.isNull(poProvider)) {
				cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_NO_PROVIDER);
				return false;
			}
			
			cdr.setInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, poProvider.getProviderId());
			cdr.setInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, row, poProvider.getProviderId());
		} else if (cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE) == CdrConst.BIZ_SMS) {
			PoProvider poProvider = (PoProvider) rateGlobalInfo.getParamManager().getPoProviderByMscCode(visitProviderCode);
			if (CheckNull.isNull(poProvider)) {
				cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, row, RateError.E_NO_PROVIDER);
				return false;
			}
			cdr.setInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, poProvider.getProviderId());
			cdr.setInt(CdrAttri.ATTRI_VISIT_PROVIDER_ID, row, poProvider.getProviderId());
		}
		return true;
	}
}
