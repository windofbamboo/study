package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MMBase;
import com.ai.iot.bill.common.mdb.MdbCommonException;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.mdb.common.autorule.mos.MoAutoRuleWrapper;
import com.ai.iot.mdb.common.autorule.mos.MoAutorule;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleJobDupcheck;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleOperCont;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleRuleDupcheck;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerAddUp;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerStatusAct;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerStatusDev;
import com.ai.iot.mdb.common.autorule.mos.MoSubAcct;

import shade.storm.com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AUTORULE-MDB
 */
public class MMAutorule extends MMBase {
  // private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 累积类型
   * 0=暂不使用
   * 1=24小时内连接数
   * 2=24小时内流量值
   * 3=24小时内条数值
   * 4=24小时内时长值
   * 5=当前周期连接数
   * 6=每阈值累积流量值
   */
  public enum AddupEnum {
    ADDUP_NONE,
    ADDUP_DAILY_CONN,
    ADDUP_DAILY_DATA,
    ADDUP_DAILY_SMS,
    ADDUP_DAILY_CALLS,
    ADDUP_CYCLE_CONN,
    ADDUP_ALWAYS_DATA;
  }

  /**
   * 累积类型
   * 0=暂不使用
   * 1=每事件一次
   * 2=每周期每阈值一次
   * 3=每天每阈值一次
   * 4=每阈值一次
   */
  public enum TriggerTypeEnum {
    TRIGGER_TYPE_NONE,
    TRIGGER_TYPE_EVERY_EVENT,
    TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD,
    TRIGGER_TYPE_EVERY_DAILY_THRESHOLD,
    TRIGGER_TYPE_EVERY_THRESHOLD;
  }

  /**
   * 为减少内存的频繁申请引起gc,建议每个mo在此定义一个永久的对象,用于临时数据传递.
   */
  @SuppressWarnings("unused")
private MoAutoRuleWrapper moAutoRuleWrapper;
  private MtAutorule mtAutorule;
  private MoAutorule moAutorule;

  @SuppressWarnings("unused")
  private MoAutoruleOperCont moAutoruleOperCont;
  private MtAutoruleOperCont mtAutoruleOperCont;

  private MtSubAcct mtSubAcct;
  
  private MtAutoruleAcctId mtAutoruleAcctId;
  private MoAutoruleTriggerStatusAct moAutoruleTriggerStatusAct;

  private MtAutoruleDeviceId mtAutoruleDeviceId;
  private MoAutoruleTriggerAddUp moAutoruleTriggerAddUp;
  private MoAutoruleTriggerStatusDev moAutoruleTriggerStatusDev;

  private MtAutoruleJobDupCheck mtAutoruleJobDupCheck;
  private MoAutoruleJobDupcheck moAutoruleJobDupcheck;

  private MtAutoruleRuleDupCheck mtAutoruleRuleDupCheck;
  private MoAutoruleRuleDupcheck moAutoruleRuleDupcheck;

  ///////////////////////////////////////////////////////////////////////////////
  /// 产生一个MM对象
  ///////////////////////////////////////////////////////////////////////////////
  public MMAutorule() throws MdbCommonException {
    this(true);
  }

  public MMAutorule(boolean isMdbMaster) throws MdbCommonException {
    super(BaseDefine.CONNTYPE_REDIS_AUTORULE, isMdbMaster);
  }

  public MMAutorule(CustJedisCluster jc) throws MdbCommonException {
    super(jc);
  }

  ///////////////////////////////////////////////////////////////////////////////
  /// 初始化所有的内部对象
  ///////////////////////////////////////////////////////////////////////////////
  @Override
  protected void init() {
    mtAutorule = new MtAutorule();
    moAutoRuleWrapper = new MoAutoRuleWrapper();
    moAutorule = new MoAutorule();

    mtAutoruleOperCont = new MtAutoruleOperCont();
    moAutoruleOperCont = new MoAutoruleOperCont();
    
    mtSubAcct = new MtSubAcct();
    //moSubAcct = new MoSubAcct();
    
    mtAutoruleAcctId = new MtAutoruleAcctId();
    moAutoruleTriggerStatusAct = new MoAutoruleTriggerStatusAct();

    mtAutoruleDeviceId = new MtAutoruleDeviceId();
    moAutoruleTriggerAddUp = new MoAutoruleTriggerAddUp();
    moAutoruleTriggerStatusDev = new MoAutoruleTriggerStatusDev();

    mtAutoruleJobDupCheck = new MtAutoruleJobDupCheck();
    moAutoruleJobDupcheck = new MoAutoruleJobDupcheck();

    mtAutoruleRuleDupCheck = new MtAutoruleRuleDupCheck();
    moAutoruleRuleDupcheck = new MoAutoruleRuleDupcheck();
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutorule);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutoruleOperCont);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtSubAcct);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutoruleAcctId);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutoruleDeviceId);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutoruleJobDupCheck);
    super.addMdbTable(new MdbTables(this.mdbClient4Cluster), mtAutoruleRuleDupCheck);
  }
  ///////////////////////////////////////////////////////////////////////////////
  /// 下面的是业务函数实现
  ///////////////////////////////////////////////////////////////////////////////

  // 获取MoAutoruleTriggerStatusAct
  public List<MoAutoruleTriggerStatusAct> getMoAutoruleTriggerStatusActByAcctId(long acctId) {
    MdbTables mts = wholeMdb.get(mtAutoruleAcctId.getMdbTableKeyId());
    ((MtAutoruleAcctId) mts.getMdbTable()).setAcctId(acctId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusAct> triggerStatusActList = mts.getList(moAutoruleTriggerStatusAct.getField());
    for (MoAutoruleTriggerStatusAct triggerStatusAct : triggerStatusActList) {
      triggerStatusAct.setAcctId(acctId);
    }
    return triggerStatusActList;
  }

  // 获取MoAutoruleTriggerStatusDev
  public List<MoAutoruleTriggerStatusDev> getMoAutoruleTriggerStatusDevByDeviceId(long deviceId) {
    MdbTables mts = wholeMdb.get(mtAutoruleDeviceId.getMdbTableKeyId());
    ((MtAutoruleDeviceId) mts.getMdbTable()).setDeviceId(deviceId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusDev> triggerStatusDevLst = mts.getList(moAutoruleTriggerStatusDev.getField());
    for (MoAutoruleTriggerStatusDev triggerStatusDev : triggerStatusDevLst) {
      triggerStatusDev.setDeviceId(deviceId);
    }
    return triggerStatusDevLst;
  }

  // 获取MoAutoruleTriggerAddUp
  public List<MoAutoruleTriggerAddUp> getMoAutoruleTriggerAddUpByDeviceId(long deviceId) {
    MdbTables mts = wholeMdb.get(mtAutoruleDeviceId.getMdbTableKeyId());
    ((MtAutoruleDeviceId) mts.getMdbTable()).setDeviceId(deviceId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerAddUp> triggerAddUpLst = mts.getList(moAutoruleTriggerAddUp.getField());
    for (MoAutoruleTriggerAddUp triggerAddUp : triggerAddUpLst) {
      triggerAddUp.setDeviceId(deviceId);
    }
    return triggerAddUpLst;
  }

  /**
   * 获取账户规则
   *
   * @param acctId
   * @return 多个规则
   */
  public List<MoAutorule> getRulesByAcctId(long acctId) {
    MdbTables mts = wholeMdb.get(mtAutorule.getMdbTableKeyId());
    ((MtAutorule) mts.getMdbTable()).setAcctId(acctId);
    if (mts == null || mts.selectTables() == false) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<MoAutorule> rules = mts.getList(moAutorule.getField());
    for (MoAutorule rule : rules) {
      rule.setAcctId(acctId);
    }
    return rules;
  }

  /**
   * 获取账户规则对应的操作信息
   *
   * @param ruleId
   * @return 多个操作信息
   */
  public List<MoAutoruleOperCont> getRulesByRuleId(long ruleId) {
    MdbTables mts = wholeMdb.get(mtAutoruleOperCont.getMdbTableKeyId());
    ((MtAutoruleOperCont) mts.getMdbTable()).setRuleId(ruleId);
    if (mts == null || mts.selectTables() == false) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleOperCont> opers = mts.getList(moAutoruleOperCont.getField());
    for (MoAutoruleOperCont oper : opers) {
      oper.setRuleId(ruleId);
    }
    return opers;
  }
  
  /**
   * 获取子账户名称
   *
   * @param subAcctId
   * @return 多个操作信息
   */
  public MoSubAcct getSubAcctBySubAcctId(long subAcctId) {
    MdbTables mts = wholeMdb.get(mtSubAcct.getMdbTableKeyId());
    ((MtSubAcct) mts.getMdbTable()).setSubAcctId(subAcctId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    return  (MoSubAcct)mts.getObject(moAutoruleOperCont.getField());
  }

  /**
   * 按账户按天或按周期触发的规则是否已经触发过
   *
   * @param
   * @return true or false
   */
  public boolean isTriggeredByAcctId(long acctId, long ruleId, int ruleVerId, long ratePlanInsId, int zoneId, long triggerTime,
                                     int activeNum, TriggerTypeEnum triggerType) {

    if (triggerType == TriggerTypeEnum.TRIGGER_TYPE_NONE)
      return false;
    MdbTables mts = wholeMdb.get(mtAutoruleAcctId.getMdbTableKeyId());
    ((MtAutoruleAcctId) mts.getMdbTable()).setAcctId(acctId);
    if (mts == null || mts.selectTables() == false) {
      return false;
    }
    moAutoruleTriggerStatusAct.setAcctId(acctId);
    moAutoruleTriggerStatusAct.setRuleId(ruleId);
    moAutoruleTriggerStatusAct.setRuleVerId(ruleVerId);
    moAutoruleTriggerStatusAct.setRatePlanInsId(ratePlanInsId);
    moAutoruleTriggerStatusAct.setZoneId(zoneId);
    moAutoruleTriggerStatusAct.setTriggerTime(triggerTime);
    moAutoruleTriggerStatusAct.setDevActiveNum(activeNum);
    moAutoruleTriggerStatusAct.setTriggerType(triggerType.ordinal());
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusAct> triggerStatuses = mts.getList(moAutoruleTriggerStatusAct.getField());
    switch (triggerType) {
      /**
       * TRIGGER_TYPE_NONE,
       TRIGGER_TYPE_EVERY_EVENT,
       TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD,
       TRIGGER_TYPE_EVERY_DAILY_THRESHOLD,
       TRIGGER_TYPE_EVERY_THRESHOLD;
       */
      case TRIGGER_TYPE_EVERY_DAILY_THRESHOLD:
        for (MoAutoruleTriggerStatusAct triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByDay(moAutoruleTriggerStatusAct))
            return true;
        }
        return false;
      case TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD:
        for (MoAutoruleTriggerStatusAct triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByCycle(moAutoruleTriggerStatusAct))
            return true;
        }
        return false;
      case TRIGGER_TYPE_EVERY_THRESHOLD:
        for (MoAutoruleTriggerStatusAct triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByThreshold(moAutoruleTriggerStatusAct))
            return true;
        }
        return false;
      default:
        return false;
    }
  }

  /**
   * 根据设备保存触发信息
   *
   * @param acctId         账户id
   * @param ruleId         规则id
   * @param ruleVerId      版本id
   * @param ratePlanInsId  订购实例id
   * @param triggerId      触发器id
   * @param triggerTime    出发时间
   * @param value          触发值
   * @param isLimited      是否超量
   * @param devActiveNum   设备激活数
   * @param cycleBeginTime 周期开始时间
   * @param triggerType    触发类型
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public MoAutoruleTriggerStatusAct saveTriggerInfoByAcctId(long acctId, long ruleId, int ruleVerId, long ratePlanInsId,
                                                            int zoneId, int triggerId, long triggerTime, long value, int isLimited,
                                                            long devActiveNum, long cycleBeginTime,
                                                            TriggerTypeEnum triggerType)
          throws IllegalAccessException, InvocationTargetException {
    if (triggerType == TriggerTypeEnum.TRIGGER_TYPE_NONE || triggerType == TriggerTypeEnum.TRIGGER_TYPE_EVERY_EVENT)
      return null;
    MdbTables mts = wholeMdb.get(mtAutoruleAcctId.getMdbTableKeyId());
    ((MtAutoruleAcctId) mts.getMdbTable()).setAcctId(acctId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    moAutoruleTriggerStatusAct.setAcctId(acctId);
    moAutoruleTriggerStatusAct.setRuleId(ruleId);
    moAutoruleTriggerStatusAct.setRuleVerId(ruleVerId);
    moAutoruleTriggerStatusAct.setRatePlanInsId(ratePlanInsId);
    moAutoruleTriggerStatusAct.setZoneId(zoneId);
    moAutoruleTriggerStatusAct.setTriggerId(triggerId);
    moAutoruleTriggerStatusAct.setTriggerTime(triggerTime);
    moAutoruleTriggerStatusAct.setValue(value);
    moAutoruleTriggerStatusAct.setIsLimited(isLimited);
    moAutoruleTriggerStatusAct.setDevActiveNum(devActiveNum);
    switch (triggerType) {
      case TRIGGER_TYPE_EVERY_THRESHOLD:
      case TRIGGER_TYPE_EVERY_DAILY_THRESHOLD:// 默认用触发时间
        break;
      case TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD:
        moAutoruleTriggerStatusAct.setTriggerTime(cycleBeginTime);
        break;
      case TRIGGER_TYPE_EVERY_EVENT:
      case TRIGGER_TYPE_NONE:
      default:
        return null;
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusAct> addups = mts.getList(moAutoruleTriggerStatusAct.getField());
    List<MoAutoruleTriggerStatusAct> addupsWithNoExpires = deleteExpireDatas(addups);
    // 当前事件有资源量需要更新MDB
    List<MoAutoruleTriggerStatusAct> addupsCopy = Lists.newArrayList(addupsWithNoExpires);
    MoAutoruleTriggerStatusAct newStatus = new MoAutoruleTriggerStatusAct();
    newStatus.copy(moAutoruleTriggerStatusAct);
    addupsCopy.add(newStatus);
    mts.setData(moAutoruleTriggerStatusAct.getField(), addupsCopy);
    return newStatus;
  }

  /**
   * 按设备按天或按周期触发的规则是否已经触发过
   *
   * @param
   * @return true or false
   */
  public boolean isTriggeredByDeviceId(long deviceId, long ruleId, int ruleVerId, long ratePlanInsId,
                                       long triggerTime, TriggerTypeEnum triggerType) {
    if (triggerType == TriggerTypeEnum.TRIGGER_TYPE_NONE)
      return false;
    MdbTables mts = wholeMdb.get(mtAutoruleDeviceId.getMdbTableKeyId());
    if (mts == null || mts.selectTables() == false) {
      return false;
    }
    ((MtAutoruleDeviceId) mts.getMdbTable()).setDeviceId(deviceId);
    moAutoruleTriggerStatusDev.setDeviceId(deviceId);
    moAutoruleTriggerStatusDev.setRuleId(ruleId);
    moAutoruleTriggerStatusDev.setRuleVerId(ruleVerId);
    moAutoruleTriggerStatusDev.setRatePlanInsId(ratePlanInsId);
    moAutoruleTriggerStatusDev.setTriggerTime(triggerTime);
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusDev> triggerStatuses = mts.getList(moAutoruleTriggerStatusDev.getField());
    switch (triggerType) {
      case TRIGGER_TYPE_EVERY_DAILY_THRESHOLD:
        for (MoAutoruleTriggerStatusDev triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByDay(moAutoruleTriggerStatusDev))
            return true;
        }
        return false;
      case TRIGGER_TYPE_EVERY_THRESHOLD:
        for (MoAutoruleTriggerStatusDev triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByThreshold(moAutoruleTriggerStatusDev))
            return true;
        }
        return false;
      case TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD:
        for (MoAutoruleTriggerStatusDev triggerStatus : triggerStatuses) {
          if (triggerStatus.isSameByCycle(moAutoruleTriggerStatusDev))
            return true;
        }
        return false;
      default:
        return false;
    }
  }

  /**
   * 根据设备保存触发信息
   *
   * @param deviceId       设备id
   * @param ruleId         规则id
   * @param ruleVerId      版本id
   * @param ratePlanInsId  订购实例id
   * @param triggerId      触发器id
   * @param triggerTime    触发时间
   * @param value          触发值
   * @param isLimited      是否超量
   * @param cycleBeginTime 周期起始时间
   * @param triggerType    触发类别
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public MoAutoruleTriggerStatusDev saveTriggerInfoByDeviceId(long deviceId, long ruleId, int ruleVerId,
                                                              long ratePlanInsId, int triggerId, long triggerTime,
                                                              long value, int isLimited, long cycleBeginTime,
                                                              TriggerTypeEnum triggerType)
          throws IllegalAccessException, InvocationTargetException {
    if (triggerType == TriggerTypeEnum.TRIGGER_TYPE_NONE || triggerType == TriggerTypeEnum.TRIGGER_TYPE_EVERY_EVENT)
      return null;
    MdbTables mts = wholeMdb.get(mtAutoruleDeviceId.getMdbTableKeyId());
    ((MtAutoruleDeviceId) mts.getMdbTable()).setDeviceId(deviceId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    moAutoruleTriggerStatusDev.setDeviceId(deviceId);
    moAutoruleTriggerStatusDev.setRuleId(ruleId);
    moAutoruleTriggerStatusDev.setRuleVerId(ruleVerId);
    moAutoruleTriggerStatusDev.setRatePlanInsId(ratePlanInsId);
    moAutoruleTriggerStatusDev.setTriggerId(triggerId);
    moAutoruleTriggerStatusDev.setTriggerTime(triggerTime);
    moAutoruleTriggerStatusDev.setValue(value);
    moAutoruleTriggerStatusDev.setIsLimited(isLimited);
    moAutoruleTriggerStatusDev.setTriggerType(triggerType.ordinal());
    switch (triggerType) {
      case TRIGGER_TYPE_EVERY_DAILY_THRESHOLD:
      case TRIGGER_TYPE_EVERY_THRESHOLD:
        break;
      case TRIGGER_TYPE_EVERY_CYCLE_THRESHOLD:
        moAutoruleTriggerStatusDev.setTriggerTime(cycleBeginTime);
        break;
      case TRIGGER_TYPE_EVERY_EVENT:
      case TRIGGER_TYPE_NONE://默认用触发时间
      default:
        return null;
    }
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerStatusDev> addups = mts.getList(moAutoruleTriggerStatusDev.getField());
    List<MoAutoruleTriggerStatusDev> addupsWithNoExpires = deleteExpireDatas(addups);
    //当前事件有资源量需要更新MDB
    List<MoAutoruleTriggerStatusDev> addupsCopy = Lists.newArrayList(addupsWithNoExpires);
    MoAutoruleTriggerStatusDev newStatus = new MoAutoruleTriggerStatusDev();
    newStatus.copy(moAutoruleTriggerStatusDev);
    addupsCopy.add(newStatus);
    mts.setData(moAutoruleTriggerStatusDev.getField(), addupsCopy);
    return newStatus;
  }

  /**
   * 累积本事件对应的设备级信息
   *
   * @param deviceId       设备id
   * @param addupType      累积类型     0=暂不使用
   *                       1=24小时内连接数
   *                       2=24小时内流量值
   *                       3=24小时内条数值
   *                       4=24小时内时长值
   *                       5=当前周期连接数
   * @param zoneId         区域id
   * @param eventTime      当前时间
   * @param cycleBeginTime 开始时间
   * @param cycleEndTime   结束时间
   * @param moValue        mo用量
   * @param mtValue        mt用量
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public MoAutoruleTriggerAddUp addupByDeviceId(long deviceId, AddupEnum addupType, int zoneId, long eventTime,
                                                long cycleBeginTime, long cycleEndTime,
                                                long moValue, long mtValue)
          throws IllegalAccessException, InvocationTargetException {
    if (addupType == AddupEnum.ADDUP_NONE)
      return null;
    MdbTables mts = wholeMdb.get(mtAutoruleDeviceId.getMdbTableKeyId());
    ((MtAutoruleDeviceId) mts.getMdbTable()).setDeviceId(deviceId);
    if (mts == null || mts.selectTables() == false) {
      return null;
    }
    moAutoruleTriggerAddUp.setDeviceId(deviceId);
    moAutoruleTriggerAddUp.setAddupType(addupType.ordinal());
    moAutoruleTriggerAddUp.setZoneId(zoneId);
    switch (addupType) {
      case ADDUP_DAILY_CONN:
      case ADDUP_DAILY_DATA:
      case ADDUP_DAILY_SMS:
      case ADDUP_DAILY_CALLS:
        moAutoruleTriggerAddUp.setStartDate(eventTime);
        moAutoruleTriggerAddUp.setEndDate(eventTime / 1000000 * 1000000 + 235959);
        break;
      case ADDUP_CYCLE_CONN:
        moAutoruleTriggerAddUp.setStartDate(cycleBeginTime);
        moAutoruleTriggerAddUp.setEndDate(cycleEndTime);
        break;
      default:
        return null;
    }
    moAutoruleTriggerAddUp.setMoValue(moValue);
    moAutoruleTriggerAddUp.setMtValue(mtValue);
    @SuppressWarnings("unchecked")
    List<MoAutoruleTriggerAddUp> addups = mts.getList(moAutoruleTriggerAddUp.getField());
    List<MoAutoruleTriggerAddUp> addupsWithNoExpires = deleteExpireDatas(addups);
    if (moAutoruleTriggerAddUp.getMoValue() + moAutoruleTriggerAddUp.getMtValue() <= 0) {//当前事件没有资源量
      switch (addupType) {
        case ADDUP_DAILY_CONN:
        case ADDUP_DAILY_DATA:
        case ADDUP_DAILY_SMS:
        case ADDUP_DAILY_CALLS:
          for (MoAutoruleTriggerAddUp addup : addupsWithNoExpires) {
            if (addup.isSameByDay(moAutoruleTriggerAddUp)) {
              return addup;
            }
          }
          break;
        case ADDUP_CYCLE_CONN:
          for (MoAutoruleTriggerAddUp addup : addupsWithNoExpires) {
            if (addup.isSameByCycle(moAutoruleTriggerAddUp)) {
              return addup;
            }
          }
          break;
        case ADDUP_ALWAYS_DATA:
          for (MoAutoruleTriggerAddUp addup : addupsWithNoExpires) {
            if (addup.isSameByAlways(moAutoruleTriggerAddUp)) {
              return addup;
            }
          }
          break;
        default:
          return null;
      }
      if (addupsWithNoExpires != addups) {//有过期数据需要更新
        mts.setData(moAutoruleTriggerAddUp.getField(), addupsWithNoExpires);
      }
      return new MoAutoruleTriggerAddUp().copy(moAutoruleTriggerAddUp);
    }
    //当前事件有资源量需要更新MDB
    List<MoAutoruleTriggerAddUp> addupsCopy = Lists.newArrayList(addupsWithNoExpires);
    MoAutoruleTriggerAddUp found = null;
    switch (addupType) {
      case ADDUP_DAILY_CONN:
      case ADDUP_DAILY_DATA:
      case ADDUP_DAILY_SMS:
      case ADDUP_DAILY_CALLS:
        for (MoAutoruleTriggerAddUp addup : addupsCopy) {
          if (addup.isSameByDay(moAutoruleTriggerAddUp)) {
            found = addup;
            break;
          }
        }
        break;
      case ADDUP_CYCLE_CONN:
        for (MoAutoruleTriggerAddUp addup : addupsCopy) {
          if (addup.isSameByCycle(moAutoruleTriggerAddUp)) {
            found = addup;
            break;
          }
        }
        break;
      case ADDUP_ALWAYS_DATA:
        for (MoAutoruleTriggerAddUp addup : addupsWithNoExpires) {
          if (addup.isSameByAlways(moAutoruleTriggerAddUp)) {
            found = addup;
          }
        }
        break;
      default:
        return null;
    }
    if (found == null) {
      found = new MoAutoruleTriggerAddUp().copy(moAutoruleTriggerAddUp);
      addupsCopy.add(found);
    } else {
      found.addValue(moAutoruleTriggerAddUp);
    }
    mts.setData(moAutoruleTriggerAddUp.getField(), addupsCopy);
    return found;
  }

  /**
   * 如果有需要删除过期数据,就产生不含过期数据的新对象
   */
  private <T extends MoBase> List<T> deleteExpireDatas(List<T> datas) {
    List<T> newAddups = null;
    for (T addup : datas) {
      if (addup.isExpire()) {// 有需要删除过期数据,就产生不含过期数据的新对象
        if (newAddups == null) {
          newAddups = new ArrayList<T>();
        }
        for (T addupTmp : datas) {
          if (!addup.isExpire()) {
            newAddups.add(addupTmp);
          }
        }
        return newAddups;
      }
    }
    // 没有过期数据,就用原来的对象
    return datas;
  }

  /**
   * 工单是否重复处理
   *
   * @param
   * @return
   */
  public boolean isJobDupCheck(long jobId) {
    MdbTables mts = wholeMdb.get(mtAutoruleJobDupCheck.getMdbTableKeyId());
    ((MtAutoruleJobDupCheck) mts.getMdbTable()).setJobId(jobId);
    if (mts == null || mts.selectTables() == false) {
      return false;//
    }
    return mts.getObject(moAutoruleJobDupcheck.getField()) != null;
  }

  /**
   * 工单产生结束,插入排重信息,jobId多个时为任意一个即可
   *
   * @param
   * @return
   */
  public boolean saveJobDupCheck(long jobId, int sourceId, long execTime) {
    MdbTables mts = wholeMdb.get(mtAutoruleJobDupCheck.getMdbTableKeyId());
    ((MtAutoruleJobDupCheck) mts.getMdbTable()).setJobId(jobId);
    if (mts == null || mts.selectTables() == false) {
      return false;//
    }
    moAutoruleJobDupcheck.setJobId(jobId);
    moAutoruleJobDupcheck.setSourceId(sourceId);
    moAutoruleJobDupcheck.setExecTime(execTime);
    mts.setData(moAutoruleJobDupcheck.getField(), moAutoruleJobDupcheck);
    return true;
  }

  /**
   * 删除对应的过滤信息
   */
  public void removeJobDupCheck(long jobId) {
    mtAutoruleJobDupCheck.setJobId(jobId);
    this.mdbClient4Cluster.del(mtAutoruleJobDupCheck.getKey().getBytes());
  }

  /**
   * 规则是否重复处理
   *
   * @param
   * @return 多个操作信息
   */
  public boolean isRuleDupCheck(String guid) {
    MdbTables mts = wholeMdb.get(mtAutoruleRuleDupCheck.getMdbTableKeyId());
    ((MtAutoruleRuleDupCheck) mts.getMdbTable()).setGuid(guid);
    if (mts == null || mts.selectTables() == false) {
      return false;//
    }
    return mts.getObject(moAutoruleRuleDupcheck.getField()) != null;
  }

  /**
   * 工单产生结束,插入排重信息,jobId多个时为任意一个即可
   *
   * @param
   * @return
   */
  public boolean saveRuleDupCheck(String guid, int sourceId, long ruleId, int ruleVerId, long execTime, long jobId) {
    MdbTables mts = wholeMdb.get(mtAutoruleRuleDupCheck.getMdbTableKeyId());
    ((MtAutoruleRuleDupCheck) mts.getMdbTable()).setGuid(guid);
    if (mts == null || mts.selectTables() == false) {
      return false;//
    }
    moAutoruleRuleDupcheck.setGuid(guid);
    moAutoruleRuleDupcheck.setSourceId(sourceId);
    moAutoruleRuleDupcheck.setRuleId(ruleId);
    moAutoruleRuleDupcheck.setRuleVerId(ruleVerId);
    moAutoruleRuleDupcheck.setExecTime(execTime);
    moAutoruleRuleDupcheck.setJobId(jobId);
    mts.setData(moAutoruleRuleDupcheck.getField(), moAutoruleRuleDupcheck);
    return true;
  }

  /**
   * 删除对应的过滤信息
   */
  public void removeRuleDupCheck(String guid) {
    mtAutoruleRuleDupCheck.setGuid(guid);
    this.mdbClient4Cluster.del(mtAutoruleRuleDupCheck.getKey().getBytes());
  }

  /**
   * 会把所有的key都提交一遍,但不负责事务一致性,返回每个key对应的提交结果,暂不考虑失败的情况
   */
  @Override
  public Map<String, Integer> commit() {
    return super.commit();
  }
}
