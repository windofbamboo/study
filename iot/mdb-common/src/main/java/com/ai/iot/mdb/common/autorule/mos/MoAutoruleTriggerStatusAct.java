package com.ai.iot.mdb.common.autorule.mos;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;
import com.ai.iot.bill.common.util.DateUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author qianwx
 */
public class MoAutoruleTriggerStatusAct extends MoBase implements Serializable {
  private static final long serialVersionUID = 7560911653000652804L;
  private static final long EXPIRE_DAYS = 32;//32天
  private long acctId;// key
  private long ruleId;
  private int ruleVerId;
  private long ratePlanInsId;
  private int zoneId;
  private int triggerId;
  private int triggerType;
  private long triggerTime;
  private long value;
  private int isLimited;
  private long devActiveNum;


  @Override
  public String getField() {
    return TABLE_ID.AUTORULE.RULE_TRIGGER_STATUS_ACT_HASHKEY;
  }

  @Override
  public int getFieldType() {
    return MdbTables.FIELD_TYPE_LIST;
  }

  @Override
  public boolean isExpire() {
    Date maxEndDate = new Date();
    maxEndDate.setTime(maxEndDate.getTime() + EXPIRE_DAYS * 24 * 3600 * 1000);
    if (this.triggerTime < Long.valueOf(DateUtil.getCurrentDateTime(maxEndDate, DateUtil.YYYYMMDD_HHMMSS))) {
      return true;
    }
    return false;
  }

  public MoAutoruleTriggerStatusAct copy(MoAutoruleTriggerStatusAct right) throws IllegalAccessException, InvocationTargetException {
    BeanUtils.copyProperties(this, right);
    return this;
  }

  /**
   * 关键字定义,按天
   */
  public boolean isSameByDay(MoAutoruleTriggerStatusAct right) {
    if (!(this.acctId == right.getAcctId() && this.ruleId == right.getRuleId() &&
            this.ruleVerId == right.getRuleVerId() && this.ratePlanInsId == right.getRatePlanInsId() &&this.zoneId == right.getZoneId() &&
            this.devActiveNum == right.getDevActiveNum() && this.triggerType == right.triggerType))
      return false;
    if (this.triggerTime / 1000000 == right.getTriggerTime() / 1000000)//只比较yyyymmdd
      return true;
    else
      return false;
  }

  /**
   * 关键字定义,按周期
   */
  public boolean isSameByCycle(MoAutoruleTriggerStatusAct right) {
    if (!(this.acctId == right.getAcctId() && this.ruleId == right.getRuleId() &&
            this.ruleVerId == right.getRuleVerId() && this.ratePlanInsId == right.getRatePlanInsId() &&this.zoneId == right.getZoneId() &&
            this.devActiveNum == right.getDevActiveNum() && this.triggerType == right.triggerType))
      return false;
    if (this.triggerTime == right.getTriggerTime())//按周期的开始时间比较,14位
      return true;
    else
      return false;
  }
  
  /**
   * 关键字定义,按阈值
   */
  public boolean isSameByThreshold(MoAutoruleTriggerStatusAct right) {
    if (!(this.acctId == right.getAcctId() && this.ruleId == right.getRuleId() &&
            this.ruleVerId == right.getRuleVerId() && this.ratePlanInsId == right.getRatePlanInsId() &&this.zoneId == right.getZoneId() &&
            this.devActiveNum == right.getDevActiveNum() && this.triggerType == right.triggerType)) {
      return false;
    }
    return true;
  }
  
  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getRuleId() {
    return ruleId;
  }

  public void setRuleId(long ruleId) {
    this.ruleId = ruleId;
  }

  public int getRuleVerId() {
    return ruleVerId;
  }

  public void setRuleVerId(int ruleVerId) {
    this.ruleVerId = ruleVerId;
  }

  public long getRatePlanInsId() {
    return ratePlanInsId;
  }

  public void setRatePlanInsId(long ratePlanInsId) {
    this.ratePlanInsId = ratePlanInsId;
  }

  public int getZoneId() {
    return zoneId;
}

public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
}

public int getTriggerId() {
    return triggerId;
  }

  public void setTriggerId(int triggerId) {
    this.triggerId = triggerId;
  }

  public int getTriggerType() {
    return triggerType;
  }

  public void setTriggerType(int triggerType) {
    this.triggerType = triggerType;
  }
  
  public long getTriggerTime() {
    return triggerTime;
  }

  public void setTriggerTime(long triggerTime) {
    this.triggerTime = triggerTime;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public int getIsLimited() {
    return isLimited;
  }

  public void setIsLimited(int isLimited) {
    this.isLimited = isLimited;
  }

  public long getDevActiveNum() {
    return devActiveNum;
  }

  public void setDevActiveNum(long devActiveNum) {
    this.devActiveNum = devActiveNum;
  }

}
