package com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.pos;

import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.StringUtil;
import shade.storm.org.apache.commons.lang.StringUtils;
import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) aisainfo
 *
 * @className:PoRatePlan
 * @description:
 * @author:xue
 * @date: 2017-7-24 14:10:46
 */
public class PoRatePlan extends PoBase implements Serializable {
  /**
   * @Fields serialVersionUID
   */
  private static final long serialVersionUID = 1L;

  /**
   * @Fields planId :
   */
  private int planId;
  /**
   * @Fields planVersionId :
   */
  private int planVersionId;
  /**
   * @Fields planType :
   */
  private int planType;
  /**
   * @Fields priority :
   */
  private int priority;
  /**
   * @Fields billId :
   */
  private int billId;
  /**
   * @Fields limitTag :
   */
  private String limitTag;
  /**
   * @Fields shared :
   */
  private String shared;
  /**
   * @Fields includedValueUsage :
   */
  private long includedValueUsage;
  /**
   * @Fields zoneUsageLimit :
   */
  private long zoneUsageLimit;
  /**
   * @Fields usageLimit :
   */
  private long usageLimit;
  /**
   * @Fields roundingUnit :
   */
  private long roundingUnit;
  /**
   * @Fields roundingFreq :
   */
  private int roundingFreq;
  /**
   * @Fields baseUnit :
   */
  private long baseUnit;
  /**
   * @Fields unitRatio :
   */
  private long unitRatio;
  /**
   * @Fields bizType : 业务类型 1:语音 2:短信 3:流量
   */
  private int bizType;
  /**
   * @Fields zoneId :
   */
  private int zoneId;
  /**
   * @Fields groupId :
   */
  private int groupId;
  /**
   * @Fields callType : 0:mo 1:mt 2:不区分
   */
  private int callType;

  private String planName;

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPlanId() {
    return this.planId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getPlanVersionId() {
    return this.planVersionId;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
  }

  public int getPlanType() {
    return this.planType;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public int getPriority() {
    return this.priority;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getBillId() {
    return this.billId;
  }

  public void setLimitTag(String limitTag) {
    this.limitTag = limitTag;
  }

  public String getLimitTag() {
    return this.limitTag;
  }

  public void setShared(String shared) {
    this.shared = shared;
  }

  public String getShared() {
    return this.shared;
  }

  public void setIncludedValueUsage(long includedValueUsage) {
    this.includedValueUsage = includedValueUsage;
  }

  public long getIncludedValueUsage() {
    return this.includedValueUsage;
  }

  public void setZoneUsageLimit(long zoneUsageLimit) {
    this.zoneUsageLimit = zoneUsageLimit;
  }

  public long getZoneUsageLimit() {
    return this.zoneUsageLimit;
  }

  public void setUsageLimit(long usageLimit) {
    this.usageLimit = usageLimit;
  }

  public long getUsageLimit() {
    return this.usageLimit;
  }

  public void setRoundingUnit(long roundingUnit) {
    this.roundingUnit = roundingUnit;
  }

  public long getRoundingUnit() {
    return this.roundingUnit;
  }

  public void setRoundingFreq(int roundingFreq) {
    this.roundingFreq = roundingFreq;
  }

  public int getRoundingFreq() {
    return this.roundingFreq;
  }

  public void setBaseUnit(long baseUnit) {
    this.baseUnit = baseUnit;
  }

  public long getBaseUnit() {
    return this.baseUnit;
  }

  public void setUnitRatio(long unitRatio) {
    this.unitRatio = unitRatio;
  }

  public long getUnitRatio() {
    return this.unitRatio;
  }

  public int getBizType() {
    return bizType;
  }

  public void setBizType(int bizType) {
    this.bizType = bizType;
  }

  public int getZoneId() {
    return zoneId;
  }

  public void setZoneId(int zoneId) {
    this.zoneId = zoneId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getCallType() {
    return callType;
  }

  public void setCallType(int callType) {
    this.callType = callType;
  }

  public String getPlanName() {
    return planName;
  }

  public void setPlanName(String planName) {
    this.planName = planName;
  }

  /*
       * (非 Javadoc) <p>Title: </p>
       */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /*
   * (非 Javadoc) <p>Title: </p>
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /*
   * (非 Javadoc) <p>Title: </p>
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
  }

  @Override
  public boolean fillData(Object obj) {
    @SuppressWarnings("unchecked")
    List<String> fields = (List<String>) obj;
    planId = StringUtil.toInt(fields.get(0));
    planVersionId = StringUtil.toInt(fields.get(1));
    planType = StringUtil.toInt(fields.get(2));
    priority = StringUtil.toInt(fields.get(3));
    billId = StringUtil.toInt(fields.get(4));
    limitTag = fields.get(5);
    shared = fields.get(6);
    includedValueUsage = StringUtil.toLong(fields.get(7));
    zoneUsageLimit = StringUtil.toLong(fields.get(8));
    usageLimit = StringUtil.toLong(fields.get(9));
    roundingUnit = StringUtil.toLong(fields.get(10));
    roundingFreq = StringUtil.toInt(fields.get(11));
    baseUnit = StringUtil.toLong(fields.get(12));
    unitRatio = StringUtil.toLong(fields.get(13));
    planName = StringUtil.toString(fields.get(14));

    String sql = "SELECT BIZ_TYPE,ZONE_ID,GROUP_ID,CALL_TYPE FROM TD_B_BILLID WHERE BILL_ID = ? ;";
    List<List<String>> billList = JdbcBaseDao.getStringList(BaseDefine.CONNCODE_MYSQL_PARAM, sql,
            String.valueOf(billId));
    if (!CheckNull.isNull(billList)) {
      bizType = StringUtil.toInt(billList.get(0).get(0));
      zoneId = StringUtil.toInt(billList.get(0).get(1));
      groupId = StringUtil.toInt(billList.get(0).get(2));
      callType = StringUtil.toInt(billList.get(0).get(3));
    }
    return true;
  }

  @Override
  public String getPoGroupName() {
    return PoGroupRegisterFactory.PoGroupNameEnum.POG_RATE_PLAN.toString();
  }

  @Override
  public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
    Map<String, Method> indexMethods = new HashMap<String, Method>();
    indexMethods.put(getIndex1Name(), getClass().getMethod(getIndex1Name()));
    indexMethods.put(getIndex2Name(), getClass().getMethod(getIndex2Name()));
    return indexMethods;
  }

  public String getIndex1Key() {
    return String.valueOf(this.planVersionId);
  }

  public static String getIndex1Name() {
    return "getIndex1Key";
  }

  public String getIndex2Key(){
    return String.valueOf(this.planId);
  }

  public static String getIndex2Name() {
    return "getIndex2Key";
  }

  public boolean isSpecial() {
    return StringUtils.equals(this.limitTag, "1") &&
            StringUtils.equals(this.shared, "1");
  }

  public boolean isNotSpecial() {
    return !isSpecial();
  }
}
