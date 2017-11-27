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
public class MoAutoruleTriggerAddUp extends MoBase implements Serializable {

  private static final long serialVersionUID = -4194465495361886045L;
  private static final long EXPIRE_DAYS = 32;//32天
  private long deviceId;// key
  private int addupType;
  private int zoneId;
  private long startDate;
  private long endDate;
  private long moValue;
  private long mtValue;

  @Override
  public String getField() {
    return TABLE_ID.AUTORULE.RULE_TRIGGER_ADDUP_HASHKEY;
  }

  @Override
  public int getFieldType() {
    return MdbTables.FIELD_TYPE_LIST;
  }

  @Override
  public boolean isExpire() {
    Date maxEndDate = new Date();
    maxEndDate.setTime(maxEndDate.getTime() + EXPIRE_DAYS * 24 * 3600 * 1000);
    if (this.endDate < Long.valueOf(DateUtil.getCurrentDateTime(maxEndDate, DateUtil.YYYYMMDD_HHMMSS))) {
      return true;
    }
    return false;
  }

  public MoAutoruleTriggerAddUp addValue(MoAutoruleTriggerAddUp from) {
    moValue += from.moValue;
    mtValue += from.mtValue;
    return this;
  }

  public MoAutoruleTriggerAddUp copy(MoAutoruleTriggerAddUp right) throws IllegalAccessException, InvocationTargetException {
    BeanUtils.copyProperties(this, right);
    return this;
  }

  /**
   * 关键字定义,按天
   */
  public boolean isSameByDay(MoAutoruleTriggerAddUp right) {
    if (!(this.deviceId == right.getDeviceId() && this.addupType == right.getAddupType() &&
            this.zoneId == right.getZoneId()))
      return false;
    if (this.startDate / 1000000 == right.getStartDate() / 1000000)//只比较yyyymmdd
      return true;
    else
      return false;
  }

  /**
   * 关键字定义,按周期
   */
  public boolean isSameByCycle(MoAutoruleTriggerAddUp right) {
    if (!(this.deviceId == right.getDeviceId() && this.addupType == right.getAddupType() &&
            this.zoneId == right.getZoneId()))
      return false;
    if (this.startDate == right.getStartDate())//按周期的开始时间比较,14位
      return true;
    else
      return false;
  }

  public boolean isSameByAlways(MoAutoruleTriggerAddUp right) {
      return this.deviceId == right.getDeviceId() && this.addupType == right.getAddupType() &&
              this.zoneId == right.getZoneId();
    }
  
  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public int getAddupType() {
    return addupType;
  }

  public void setAddupType(int addupType) {
    this.addupType = addupType;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public void setEndDate(long endDate) {
    this.endDate = endDate;
  }

  public long getMoValue() {
    return moValue;
  }

  public void setMoValue(long moValue) {
    this.moValue = moValue;
  }

  public long getMtValue() {
    return mtValue;
  }

  public void setMtValue(long mtValue) {
    this.mtValue = mtValue;
  }

}
