package com.ai.iot.bill.common.paramcachemanager.pog.sys;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.pos.*;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shade.storm.org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 系统参数PM
 * @author xue
 *
 */
public class PmSys extends PmBase {
  public static final Logger logger = LoggerFactory.getLogger(PmSys.class);
  private PoCycle poCycle = new PoCycle();

  // private PoCdrFormat poCdrFormat = new PoCdrFormat();

  // private PoCdrField poCdrField = new PoCdrField();

  private PoScriptType poScriptType = new PoScriptType();

  private PoScriptCode poScriptCode = new PoScriptCode();

  private PoSystemParam poSystemParam = new PoSystemParam();

  private PoAutorule poAutorule = new PoAutorule();

  private PoAutoRuleOperCont poAutoRuleOperCont = new PoAutoRuleOperCont();

  private PoStatus poStatus = new PoStatus();

  public PmSys(ParamClient paramClient) {
    super(paramClient);
  }

  /**
   * 根据传入日期，判断所属账期
   *
   * @param sysDate
   * @return
   */
  public PoCycle getCycle(String sysDate) {
    Date date = DateUtil.string2Date(sysDate, DateUtil.YYYYMMDD_HHMMSS);
    List<PoBase> cycleList = paramClient.getAllDatas(poCycle.getPoGroupName(), poCycle.getPoName());
    if (null != cycleList) {
      for (PoBase poBase : cycleList) {
        PoCycle poCycle = (PoCycle) poBase;
        if (date.getTime() >= poCycle.getCycStartTime().getTime()
                && date.getTime() <= poCycle.getCycEndTime().getTime()) {
          return poCycle;
        }
      }
    }
    return null;
  }
  

  /**
   * 根据传入日期，获取所属账期
   *
   * @param sysDate
   * @return
   */
  public PoCycle getCycleByDate(String sysDate) {
    Date date = DateUtil.string2Date(sysDate);
    List<PoBase> cycleList = paramClient.getAllDatas(poCycle.getPoGroupName(), poCycle.getPoName());
    if (null != cycleList) {
      for (PoBase poBase : cycleList) {
        PoCycle poCycle = (PoCycle) poBase;
        if (date.getTime() >= poCycle.getCycStartTime().getTime()
                && date.getTime() <= poCycle.getCycEndTime().getTime()) {
          return poCycle;
        }
      }
    }
    return null;
  }
  
  /**
   * 获取当前账期
   * @param date
   * @return
   * @since 1.0
   */
  public int getCycle(Date date) {
	  List<PoBase> cycleList = paramClient.getAllDatas(poCycle.getPoGroupName(), poCycle.getPoName());
	  if (!CheckNull.isNull(cycleList)) {
		  for (PoBase poBase : cycleList) {
			  PoCycle poCycle = (PoCycle) poBase;
			  if (date.getTime() >= poCycle.getCycStartTime().getTime()
					  && date.getTime() <= poCycle.getCycEndTime().getTime()) {
				  return poCycle.getCycleId();
			  }
		  }
	  }
	  return 0;
  }
  
  /**
   * 根据账期ID获取账期对象
   * @param cycleId
   * @return
   */
  public PoCycle getPoCycle(int cycleId){
	  poCycle.setCycleId(cycleId);
	  List<PoBase> poCycles = paramClient.getDatasByKey(poCycle.getPoGroupName(), poCycle.getPoName(), PoCycle.getIndex1Name(), poCycle.getIndex1Key());
	  if(CheckNull.isNull(poCycles)){
		  return null;
	  }
	  return (PoCycle) poCycles.get(0);
  }

  /**
   * 根据模块名获取脚本规则类型
   *
   * @param module
   * @return
   */
  public List<PoBase> getScriptType(String module) {
    poScriptType.setModule(module);
    return paramClient.getDatasByKey(poScriptType.getPoGroupName(), poScriptType.getPoName(),
            PoScriptType.getIndex1Name(), poScriptType.getIndex1Key());
  }
  

  /**
   * 根据规则类型ID获取执行代码
   *
   * @param typeId
   * @return
   */
  public List<PoBase> getScriptCode(int typeId) {
    poScriptCode.setTypeId(typeId);
    return paramClient.getDatasByKey(poScriptCode.getPoGroupName(), poScriptCode.getPoName(),
            PoScriptCode.getIndex1Name(), poScriptCode.getIndex1Key());
  }

  /**
   * 获取会话扫描间隔时间
   *
   * @return
   */
  public String getScanTime(String paramType) {
    List<PoBase> systemParams = getSystemType(paramType);
    if (CheckNull.isNull(systemParams)) {
      return null;
    }
    for (PoBase poBase : systemParams) {
      PoSystemParam po = (PoSystemParam) poBase;
      if ("SCAN_INTERVAL".equalsIgnoreCase(po.getParamName())) {
        return po.getParamValue();
      }
    }
    return null;
  }

  /**
   * 获取通用自动化规则
   *
   * @return
   */
  public List<PoAutorule> getCommonAutorules() {
    return paramClient.getAllTrueDatas(poAutorule.getPoGroupName(), poAutorule.getPoName());
//		List<PoAutorule> autorules = null;
//		if (bases != null && bases.size() > 0) {
//			for (PoBase base : bases) {
//				PoAutorule rule = (PoAutorule) base;
//				if (autorules == null) {
//					autorules = new ArrayList<>();
//				}
//				autorules.add(rule);
//			}
//		}
//		return autorules;
  }

  /**
   * 获取规则的操作
   *
   * @param ruleId
   * @return
   */
  public List<PoAutoRuleOperCont> getRuleOperConts(long ruleId) {
    return paramClient.getTrueDatasByKey(poAutoRuleOperCont.getPoGroupName(),
            poAutoRuleOperCont.getPoName(), PoAutoRuleOperCont.getIndex1Name(), String.valueOf(ruleId));
//		List<PoAutoRuleOperCont> poAutoRuleOperConts = null;
//		if (bases != null && bases.size() > 0) {
//			for (PoBase base : bases) {
//				PoAutoRuleOperCont operCont = (PoAutoRuleOperCont) base;
//				if (poAutoRuleOperConts == null) {
//					poAutoRuleOperConts = new ArrayList<>();
//				}
//				poAutoRuleOperConts.add(operCont);
//			}
//		}
//		return poAutoRuleOperConts;
  }

  /**
   * 获取状态名称
   *
   * @param stateCode
   * @return
   */
  public String getStatus(int stateCode){
    List<PoBase> statusList = paramClient.getAllDatas(poStatus.getPoGroupName(), poStatus.getPoName());
    if (!CheckNull.isNull(statusList)) {
      for (PoBase poBase : statusList) {
        PoStatus poStatus = (PoStatus) poBase;
        if (poStatus.getStateCode() == stateCode) {
          return poStatus.getStateName();
        }
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * 根据参数类型读取参数值
   * @param paramType
   * @return
   */
  public List<PoBase> getSystemType(String paramType) {
    poSystemParam.setParamType(paramType);
    List<PoBase> systemParams = paramClient.getDatasByKey(poSystemParam.getPoGroupName(), poSystemParam.getPoName(),
            PoSystemParam.getIndex1Name(), poSystemParam.getIndex1Key());
    return systemParams;
  }
}
