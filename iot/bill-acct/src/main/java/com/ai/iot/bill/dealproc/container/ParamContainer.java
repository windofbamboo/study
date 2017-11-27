package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.param.AddupIdBean;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.param.DiscontGradeDetailBean;
import com.ai.iot.bill.entity.param.FeeBaseBean;
import com.ai.iot.bill.entity.param.ItemBean;
import com.ai.iot.bill.entity.param.ItemMonthFee;
import com.ai.iot.bill.entity.param.Opn;
import com.ai.iot.bill.entity.param.RatePlanBean;
import com.ai.iot.bill.entity.param.RatePlanVersionBean;
import com.ai.iot.bill.entity.param.SumbillFixBean;
import com.ai.iot.bill.entity.param.SumbillUseBean;
import com.ai.iot.bill.entity.param.ThirdPartyLevel;
import com.ai.iot.bill.entity.param.ThirdPartyPlan;
import com.ai.iot.bill.entity.param.ThirdPartyVersion;
import com.ai.iot.bill.entity.param.ThirdPartyZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 参数容器，存放参数对象
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ParamContainer {

  private Map<Integer, List<RateBill>> rateBillMap = new HashMap<>();
  private Map<Integer, RateOrderFee> rateOrderFeeMap = new HashMap<>();
  private Map<Integer, Integer> ratePlanVersionMap = new HashMap<>();
  private Map<Integer, Integer> ratePlanMap = new HashMap<>();
  private Map<Integer, FeeBaseBean> feeBaseMap = new HashMap<>();
  private List<SumbillUseBean> sumbillUseBeanList = new ArrayList<>();
  private List<SumbillFixBean> sumbillFixBeanList = new ArrayList<>();
  private List<AddupIdBean> addupIdBeanList = new ArrayList<>();
  private List<RatePlanVersionBean> ratePlanVersionBeanList = new ArrayList<>();
  private Map<Integer, RatePlanBean> ratePlanOMap = new HashMap<>();
  private Map<String, Long> opnMap = new HashMap<>();

  private Map<Long, List<Integer>> thirdPartyPlanMap = new HashMap<>(); //第三方资费计划
  private Map<Integer, List<ThirdPartyZone>> thirdPartyZoneMap = new HashMap<>(); //第三方资费计划版本
  private Map<Integer, List<ThirdPartyLevel>> thirdPartyLevelMap = new HashMap<>(); //第三方区域组
  private Map<Integer, ThirdPartyVersion> thirdPartyVersionMap = new HashMap<>(); //第三方计费升档表

  private Map<Integer, ItemBean> itemBeanMap = new HashMap<>(); //账目对应关系,用于接口账单生成时的 账目转换
  private Set<Integer> orderItemSet = new HashSet<>(); //订户费的账目
  private Map<Integer,Integer> monthfeeItemMap = new HashMap<>(); // 一次性费用/月费的账目 - feeCode chargeType

  private CycleBean cycleBean = null;
  private Map<Integer, List<DiscontGradeDetailBean>> discontGradeDetailBeanMap = new HashMap<>();

  public ParamContainer() {
  }

  public void addRateBillMap(int planVersionId, List<RateBill> rateBillList) {
    this.rateBillMap.put(planVersionId, rateBillList);
  }

  public void addRateOrderFeeMap(RateOrderFee rateOrderFee) {
    this.rateOrderFeeMap.put(rateOrderFee.getPlanVersionId(), rateOrderFee);
  }

  public void addRatePlanVersionMap(int planId, int planVersionId) {
    this.ratePlanVersionMap.put(planId, planVersionId);
  }

  public void addRatePlanMap(int planVersionId, int planId) {
    this.ratePlanMap.put(planVersionId, planId);
  }


  public void addFeeBaseMap(FeeBaseBean feeBaseBean) {
    this.feeBaseMap.put(feeBaseBean.getBillId(), feeBaseBean);
  }

  public void setAddupIdBeanList(List<AddupIdBean> addupIdBeanList) {
    this.addupIdBeanList = addupIdBeanList;
  }

  public void setSumbillUseBeanList(List<SumbillUseBean> sumbillUseBeanList) {
    this.sumbillUseBeanList = sumbillUseBeanList;
  }

  public void setSumbillFixBeanList(List<SumbillFixBean> sumbillFixBeanList) {
    this.sumbillFixBeanList = sumbillFixBeanList;
  }

  public ItemBean getItemBean(int itemId) {
    return itemBeanMap.get(itemId);
  }

  public void addItemBeanMap(ItemBean itemBean) {
    this.itemBeanMap.put(itemBean.getItemId(), itemBean);
  }

  public CycleBean getCycleBean() {
    return cycleBean;
  }

  public void setCycleBean(CycleBean cycleBean) {
    this.cycleBean = cycleBean;
  }

  public void addMonthfeeItemMap(ItemMonthFee itemMonthFee){
    this.monthfeeItemMap.put(itemMonthFee.getFeeCode(), itemMonthFee.getChargeType());
  }

  public int parseFeeCode(int feeCode){
    if(monthfeeItemMap.containsKey(feeCode))
      return monthfeeItemMap.get(feeCode);
    return -1;
  }

  public void setRatePlanVersionBeanList(List<RatePlanVersionBean> ratePlanVersionBeanList) {
    this.ratePlanVersionBeanList = ratePlanVersionBeanList;
  }

  public void addratePlanOMap(RatePlanBean ratePlanBean) {
    this.ratePlanOMap.put(ratePlanBean.getPlanId(), ratePlanBean);
  }

  public void addOpnMap(Opn opn) {
    this.opnMap.put(opn.getOpnCode(), opn.getPrice());
  }


  public List<Integer> getThirdPartyPlanList(long acctId) {

    if (thirdPartyPlanMap.containsKey(acctId)) {
      return thirdPartyPlanMap.get(acctId);
    }
    return Collections.emptyList();
  }

  public void addThirdPartyPlanMap(final ThirdPartyPlan thirdPartyPlan) {
    if (thirdPartyPlanMap.containsKey(thirdPartyPlan.getPayAcctId())) {
      thirdPartyPlanMap.get(thirdPartyPlan.getPayAcctId()).add(thirdPartyPlan.getPlanId());
    } else {
      List<Integer> planList = new ArrayList<>();
      planList.add(thirdPartyPlan.getPlanId());
      thirdPartyPlanMap.put(thirdPartyPlan.getPayAcctId(), planList);
    }
  }

  public List<ThirdPartyZone> getThirdPartyZoneList(final int planVersionId) {
    return thirdPartyZoneMap.get(planVersionId);
  }

  public void setThirdPartyZoneMap(Map<Integer, List<ThirdPartyZone>> thirdPartyZoneMap) {
    this.thirdPartyZoneMap = thirdPartyZoneMap;
  }

  public List<ThirdPartyLevel> getThirdPartyLevelList(final int planVersionId) {
    return thirdPartyLevelMap.get(planVersionId);
  }

  public void setThirdPartyLevelMap(Map<Integer, List<ThirdPartyLevel>> thirdPartyLevelMap) {
    this.thirdPartyLevelMap = thirdPartyLevelMap;
  }

  public ThirdPartyVersion getThirdPartyVersion(final int planId) {
    return thirdPartyVersionMap.get(planId);
  }

  public void addThirdPartyVersionMap(ThirdPartyVersion thirdPartyVersion) {
    this.thirdPartyVersionMap.put(thirdPartyVersion.getPlanId(), thirdPartyVersion);
  }

  public DiscontGradeDetailBean getDiscontGradeDetailBean(int gradeId, double deviceNum) {

    if (discontGradeDetailBeanMap == null || discontGradeDetailBeanMap.isEmpty()) {
      return null;
    }
    List<DiscontGradeDetailBean> tList = discontGradeDetailBeanMap.get(gradeId);
    if (tList == null || tList.isEmpty()) {
      return null;
    }
    for (DiscontGradeDetailBean t : tList) {
      if (t.getDeviceUp() > deviceNum && t.getDeviceLower() <= deviceNum) {
        return t;
      }
    }
    return null;
  }

  public void setDiscontGradeDetailBeanMap(Map<Integer, List<DiscontGradeDetailBean>> discontGradeDetailBeanMap) {
    this.discontGradeDetailBeanMap = discontGradeDetailBeanMap;
  }

  /**
   * 月付计划获取版本
   */
  public int getPlanVersionId(int planId) {
    if (ratePlanVersionMap.containsKey(planId)){
      return ratePlanVersionMap.get(planId);
    }
    return 0;
  }

  /**
   * 根据版本获取计划ID
   */
  public int getPlanId(int planVersionId) {
    if (ratePlanMap.containsKey(planVersionId)){
      return ratePlanMap.get(planVersionId);
    }
    return 0;
  }

  public RatePlanBean getRatePlan(int planId) {
    if (ratePlanOMap.containsKey(planId)){
      return ratePlanOMap.get(planId);
    }
    return null;
  }

  /**
   * 获取opn的费用
   */
  public long getOpnPrice(String opnCode) {
    if (opnMap.containsKey(opnCode)){
      return opnMap.get(opnCode);
    }
    return 0;
  }

  /**
   * 获取资费计划版本，对应的订户费、账户费设置
   */
  public RateOrderFee getRateOrderFee(int planVersionId) {

    if (rateOrderFeeMap.containsKey(planVersionId)){
      return rateOrderFeeMap.get(planVersionId);
    }
    return null;
  }

  /**
   * 获取资费计划版本,累计量；所对应的批价策略
   */
  public RateBill getRateBill(int planVersionId, int billId) {

    if (rateBillMap.containsKey(planVersionId)) {
      List<RateBill> rateBillList = rateBillMap.get(planVersionId);
      for (RateBill rateBill : rateBillList) {
        if (rateBill.getBillId() == billId){
          return rateBill;
        }
      }
    }
    return null;
  }

  /**
   * 获取资费计划版本下的所有的批价策略
   */
  public List<RateBill> getRateBill(int planVersionId) {

    if (rateBillMap.containsKey(planVersionId)) {
      return rateBillMap.get(planVersionId);
    }
    return Collections.emptyList();
  }


  /**
   * 获取标准资费，对应的累计量批价规则
   */
  public FeeBaseBean getFeeBase(int billId) {

    if (feeBaseMap.containsKey(billId)){
      return feeBaseMap.get(billId);
    }
    return null;
  }

  /**
   * 获取累计id的定义
   */
  public AddupIdBean getAddupId(int billId) {

    AddupIdBean addupIdBean = new AddupIdBean(billId);
    int a = addupIdBeanList.indexOf(addupIdBean);
    if (a == -1) {
      return null;
    } else {
      return addupIdBeanList.get(a);
    }
  }

  /**
   * 获取用量费用的账目
   */
  public int getSumbillUse(int chargeMode,
                           int paymentType,
                           int bizType,
                           boolean isRoam) {

    SumbillUseBean sumbillUseBean = new SumbillUseBean(chargeMode, paymentType, bizType, isRoam);
    int a = sumbillUseBeanList.indexOf(sumbillUseBean);
    if (a == -1) {
      return 0;
    } else {
      return sumbillUseBeanList.get(a).getItemId();
    }
  }

  /**
   * 获取非使用量的账目。
   */
  public int getSumbillFix(int chargeType, int paymentType) {

    SumbillFixBean sumbillFixBean = new SumbillFixBean(chargeType, paymentType);
    int a = sumbillFixBeanList.indexOf(sumbillFixBean);
    if (a == -1) {
      return 0;
    } else {
      return sumbillFixBeanList.get(a).getItemId();
    }
  }

  public void setOrderItemSet(Set<Integer> orderItemSet) {
    this.orderItemSet = orderItemSet;
  }

  public boolean isOrderItem(int itemId) {
    return orderItemSet.contains(itemId);
  }

  public int getChargeType(int itemId) {
    for (SumbillFixBean sumbillFixBean : sumbillFixBeanList) {
      if (sumbillFixBean.getItemId() == itemId){
        return sumbillFixBean.getChargeType();
      }
    }
    return ParamEnum.ChargeType.CHARGE_TYPE_USAGE;
  }

  public int getBizType(int itemId) {
    for (SumbillUseBean sumbillUseBean : sumbillUseBeanList) {
      if (sumbillUseBean.getItemId() == itemId){
        return sumbillUseBean.getBizType();
      }
    }
    return -1;
  }


  /**
   * 获取版本参数。
   */
  public RatePlanVersionBean getRatePlanVersion(int planId, int planVersionId) {
    RatePlanVersionBean ratePlanVersionBean = new RatePlanVersionBean(planId, planVersionId);
    int a = ratePlanVersionBeanList.indexOf(ratePlanVersionBean);
    if (a == -1) {
      return null;
    } else {
      return ratePlanVersionBeanList.get(a);
    }
  }


}
