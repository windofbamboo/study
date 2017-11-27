package com.ai.iot.bill.dealproc.util;

import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.dealproc.container.AcctBillContainer;
import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.dealproc.container.RateBill;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.BillIAcctnterface;
import com.ai.iot.bill.entity.computebill.AcctBill2Bss;
import com.ai.iot.bill.entity.computebill.AcctBillUsage;
import com.ai.iot.bill.entity.computebill.BillAcct;
import com.ai.iot.bill.entity.computebill.BillTrackAcct;
import com.ai.iot.bill.entity.computebill.BillUsageInterface;
import com.ai.iot.bill.entity.computebill.DeviceBillActivation;
import com.ai.iot.bill.entity.computebill.DeviceBillInterface;
import com.ai.iot.bill.entity.computebill.DeviceBillOrder;
import com.ai.iot.bill.entity.computebill.DeviceBillUsage;
import com.ai.iot.bill.entity.multibill.*;
import com.ai.iot.bill.entity.param.AddupIdBean;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.param.RatePlanBean;
import com.ai.iot.bill.entity.res.ResIncludePool;
import com.ai.iot.bill.entity.res.ResIncludeShare;
import com.ai.iot.bill.entity.res.ResIncludeShareTurn;
import com.ai.iot.bill.entity.res.ResInterface;
import com.ai.iot.bill.entity.usage.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

/**账单处理工具
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BillUtil {


  public static int getIncludeMode(final int planType){

    int includeMode = 0;
    switch (planType) {
      case ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE:
        includeMode = BillEnum.IncludeMode.MONTH;
        break;
      case ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE:
      case ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE:
        includeMode = BillEnum.IncludeMode.MONTH_SHARE;
        break;
      case ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE:
        includeMode = BillEnum.IncludeMode.PERPAY;
        break;
      default:
        break;
    }
    return includeMode;
  }

  public static void trimDeviceBillData(List<DeviceBillData> deviceBillDataList) {
    if (deviceBillDataList == null || deviceBillDataList.isEmpty()) {
      return;
    }
    Collections.sort(deviceBillDataList);

    DeviceBillData old = null;
    Iterator<DeviceBillData> it = deviceBillDataList.iterator();
    while (it.hasNext()) {
      DeviceBillData t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if (t.equals(old)   ) {
        old.setGprsValue( old.getGprsValue() + t.getGprsValue() );
        if(BillEnum.SHARE_VALUE.equals(old.getGprsFee()) || BillEnum.SHARE_VALUE.equals(t.getGprsFee()) ){
          old.setGprsFee(BillEnum.SHARE_VALUE);
        }else{
          long fee = Long.valueOf(old.getGprsFee()) + Long.valueOf(t.getGprsFee());
          old.setGprsFee(String.valueOf(fee));
        }
        it.remove();
      } else {
        old = t;
      }
    }
  }

  public static void trimDeviceBillSms(List<DeviceBillSms> deviceBillSmsList) {
    if (deviceBillSmsList == null || deviceBillSmsList.isEmpty()) {
      return;
    }
    Collections.sort(deviceBillSmsList);

    DeviceBillSms old = null;
    Iterator<DeviceBillSms> it = deviceBillSmsList.iterator();
    while (it.hasNext()) {
      DeviceBillSms t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if (t.equals(old)   ) {
        old.setSmsValue( old.getSmsValue() + t.getSmsValue() );
        if(BillEnum.SHARE_VALUE.equals(old.getSmsCharge()) || BillEnum.SHARE_VALUE.equals(t.getSmsCharge()) ){
          old.setSmsCharge(BillEnum.SHARE_VALUE);
        }else{
          long fee = Long.valueOf(old.getSmsCharge()) + Long.valueOf(t.getSmsCharge());
          old.setSmsCharge(String.valueOf(fee));
        }
        it.remove();
      } else {
        old = t;
      }
    }
  }

  public static void trimDeviceBillVoice(List<DeviceBillVoice> deviceBillVoiceList) {
    if (deviceBillVoiceList == null || deviceBillVoiceList.isEmpty()) {
      return;
    }
    Collections.sort(deviceBillVoiceList);

    DeviceBillVoice old = null;
    Iterator<DeviceBillVoice> it = deviceBillVoiceList.iterator();
    while (it.hasNext()) {
      DeviceBillVoice t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if (t.equals(old)   ) {
        old.setVoiceValue( old.getVoiceValue() + t.getVoiceValue() );
        if(BillEnum.SHARE_VALUE.equals(old.getVoiceCharge()) || BillEnum.SHARE_VALUE.equals(t.getVoiceCharge()) ){
          old.setVoiceCharge(BillEnum.SHARE_VALUE);
        }else{
          long fee = Long.valueOf(old.getVoiceCharge()) + Long.valueOf(t.getVoiceCharge());
          old.setVoiceCharge(String.valueOf(fee));
        }
        it.remove();
      } else {
        old = t;
      }
    }
  }

  /**
   * 对于从redis中查出来的设备端累积量，进行规则
   * 用于处理 按日规整的记录，把按天存放的记录，合并在一起
   * (约定:按天存放的记录，只有第一天的往月累积量是有值的，其它的记录中，填写的都是0)
   */
  public static void trimUsedAddDevice(List<UsedAddDevice> usedAddDeviceList) {

    if (usedAddDeviceList == null || usedAddDeviceList.isEmpty()) {
      return;
    }
    usedAddDeviceList.sort((o1, o2) -> {
      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;

      if (o1.getTpInsId() < o2.getTpInsId()) return -1;
      if (o1.getTpInsId() > o2.getTpInsId()) return 1;

      if (o1.getBillId() < o2.getBillId()) return -1;
      if (o1.getBillId() > o2.getBillId()) return 1;
      return 0;
    });

    UsedAddDevice old = null;
    Iterator<UsedAddDevice> it = usedAddDeviceList.iterator();
    while (it.hasNext()) {
      UsedAddDevice t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if ((t.getAcctId() == old.getAcctId()) && (t.getTpInsId() == old.getTpInsId()) && (t.getBillId() == old.getBillId())   ) {
        old.setCurrValue(old.getCurrValue() + t.getCurrValue());
        old.setLastValue(old.getLastValue() + t.getLastValue());
        old.setMoValue(old.getMoValue() + t.getMoValue());
        old.setMtValue(old.getMtValue() + t.getMtValue());
        it.remove();
      } else {
        old = t;
      }
    }
  }

  /**
   * 对于从redis中查出来的pool的累积量，进行规整
   * 用于处理 按日规整的记录，把按天存放的记录，合并在一起
   * 约定:按天存放的记录，只有第一天的往月累积量是有值的，其它的记录中，填写的都是0)
   */
  public static void trimUsedAddPoolTotal(List<UsedAddPoolTotal> usedAddPoolTotalList) {

    if (usedAddPoolTotalList == null || usedAddPoolTotalList.isEmpty()) {
      return;
    }
    usedAddPoolTotalList.sort((o1, o2) -> {
      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;

      if (o1.getPoolId() < o2.getPoolId()) return -1;
      if (o1.getPoolId() > o2.getPoolId()) return 1;
      return 0;
    });

    UsedAddPoolTotal old = null;
    Iterator<UsedAddPoolTotal> it = usedAddPoolTotalList.iterator();
    while (it.hasNext()) {
      UsedAddPoolTotal t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if ((t.getAcctId() == old.getAcctId()) && (t.getPoolId() == old.getPoolId())) {
        old.setCurrValue(old.getCurrValue() + t.getCurrValue());
        old.setLastValue(old.getLastValue() + t.getLastValue());
        it.remove();
      } else {
        old = t;
      }
    }
  }

  /**
   * 对于月付固定的总量，进行合并
   * 来源有两个: 1.设备订购堆叠转移而来 2.参数计算得到
   * 这两部分的数据，必须进行合并，否则会在数据存储的时候，引起主键冲突
   */
  public static void trimResIncludeShare(List<ResIncludeShare>  resIncludeFixShareList) {

    if (resIncludeFixShareList == null || resIncludeFixShareList.isEmpty()) {
      return;
    }
    Collections.sort(resIncludeFixShareList);

    ResIncludeShare old = null;
    Iterator<ResIncludeShare> it = resIncludeFixShareList.iterator();
    while (it.hasNext()) {
      ResIncludeShare t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if ((t.getAcctId() == old.getAcctId()) &&
          (t.getPlanVersionId() == old.getPlanVersionId()) &&
          (t.getBillId() == old.getBillId())    ) {
        old.setValue(old.getValue() + t.getValue());
        it.remove();
      } else {
        old = t;
      }
    }
  }

  /**
   * 对批价轨迹进行规整
   */
  public static void trimBillTrackAcct(List<BillTrackAcct>  billTrackAcctList) {

    if (billTrackAcctList == null || billTrackAcctList.isEmpty()) {
      return;
    }
    billTrackAcctList.sort((o1, o2) -> {
      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;

      if (o1.getStage() < o2.getStage()) return -1;
      if (o1.getStage() > o2.getStage()) return 1;

      if (o1.getSourceId() < o2.getSourceId()) return -1;
      if (o1.getSourceId() > o2.getSourceId()) return 1;

      if (o1.getItemId() < o2.getItemId()) return -1;
      if (o1.getItemId() > o2.getItemId()) return 1;

      return 0;
    });

    BillTrackAcct old = null;
    Iterator<BillTrackAcct> it = billTrackAcctList.iterator();
    while (it.hasNext()) {
      BillTrackAcct t = it.next();
      if (old == null) {
        old = t;
        continue;
      }
      if ((t.getAcctId() == old.getAcctId()) &&
          (t.getStage() == old.getStage()) &&
          (t.getSourceId() == old.getSourceId()) &&
          (t.getItemId() == old.getItemId())    ) {
        old.setFee (old.getFee() + t.getFee());
        it.remove();
      } else {
        old = t;
      }
    }
  }

  /**
   * 转换BillTrackAcct 到 BillAcct
   */
  public static <T extends BillIAcctnterface> List<BillAcct> getBillAcctList(final List<T> tList) {

    if (tList == null || tList.isEmpty()){
      return Collections.emptyList();
    }
    long acctId=tList.get(0).getAcctId();
    int cycleId=tList.get(0).getCycleId();

    Map<Integer, Long> tMap =
        tList.stream().collect(groupingBy(T::getItemId,summingLong(T::getFee)));

    List<BillAcct> billAcctList = new ArrayList<>();
    tMap.forEach((k,v)-> billAcctList.add(new BillAcct(acctId,cycleId,k,v)));

    return billAcctList;
  }



  public static List<BillTrackAcct> getBillTrackAcctList(final List<RateGroupDiscount> rateGroupDiscountList) {

    if (rateGroupDiscountList == null || rateGroupDiscountList.isEmpty()){
      return Collections.emptyList();
    }

    List<BillTrackAcct> billTrackAcctList = new ArrayList<>();

    rateGroupDiscountList.sort((o1, o2) -> {
      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;

      if (o1.getRateGroup() < o2.getRateGroup()) return -1;
      if (o1.getRateGroup() > o2.getRateGroup()) return 1;
      return Integer.compare(o1.getItemId(), o2.getItemId());
    });

    BillTrackAcct newRecord = null;
    for(RateGroupDiscount rateGroupDiscount:rateGroupDiscountList){
      if(newRecord == null){
        newRecord = new BillTrackAcct();
        newRecord.setAcctId(rateGroupDiscount.getAcctId());
        newRecord.setCycleId(rateGroupDiscount.getCycleId());
        newRecord.setStage(BillEnum.AcctTrackStage.GROUP_DISCOUNT);
        newRecord.setSourceId(rateGroupDiscount.getRateGroup());
        newRecord.setItemId(rateGroupDiscount.getItemId());
        newRecord.setFee(-rateGroupDiscount.getDisountCharge());
      }else{
        if(newRecord.getAcctId() == rateGroupDiscount.getAcctId() &&
                newRecord.getSourceId() == rateGroupDiscount.getRateGroup() &&
                newRecord.getItemId() == rateGroupDiscount.getItemId()){
          newRecord.setFee(newRecord.getFee()-rateGroupDiscount.getDisountCharge());
        }else{
          billTrackAcctList.add(newRecord);
          newRecord = new BillTrackAcct();
          newRecord.setAcctId(rateGroupDiscount.getAcctId());
          newRecord.setCycleId(rateGroupDiscount.getCycleId());
          newRecord.setStage(BillEnum.AcctTrackStage.GROUP_DISCOUNT);
          newRecord.setSourceId(rateGroupDiscount.getRateGroup());
          newRecord.setItemId(rateGroupDiscount.getItemId());
          newRecord.setFee(-rateGroupDiscount.getDisountCharge());
        }
      }
    }
    if(newRecord != null){
      billTrackAcctList.add(newRecord);
    }
    return billTrackAcctList;
  }


  /**
   * 设备级账单转换成 账户收费轨迹
   */
  public static <T extends DeviceBillInterface> List<BillTrackAcct> getBillTrackAcct(final int stage,final List<T> tList) {

    if(tList == null || tList.isEmpty()){
      return Collections.emptyList();
    }

    long acctId=tList.get(0).getAcctId();
    int cycleId=tList.get(0).getCycleId();

    Map<Integer, Map<Integer, Long>> tMap =
    tList.stream().collect(groupingBy(T::getPlanId,groupingBy(T::getItemId,summingLong(T::getFee))));

    List<BillTrackAcct> billTrackAcctList = new ArrayList<>();
    tMap.forEach((k,v)-> v.forEach((k2,v2)->billTrackAcctList.add(new BillTrackAcct(acctId,cycleId,stage,k,k2,v2)) ));

    return billTrackAcctList;
  }

  /**
   * 获取资费组的订户数
   */
  public static double getOrderNum(Set<Integer> planIdSet, List<DeviceBillOrder> deviceBillOrderList) {

    if (deviceBillOrderList == null || deviceBillOrderList.isEmpty()) {
      return 0;
    }
    return deviceBillOrderList.stream().filter(t -> planIdSet.contains( t.getPlanId()))
                                       .mapToDouble(DeviceBillOrder::getOrderNum)
                                       .sum();
  }

  /**
   * 流量账单 转换成 区域账单
   */
  public static List<PlanZoneBill> getPlanZoneBillList(final List<DeviceBillData> deviceBillDataList) {

    if (deviceBillDataList == null || deviceBillDataList.isEmpty()) {
      return Collections.emptyList();
    }
    deviceBillDataList.sort((o1, o2) -> {
      if (o1.getPlanId() < o2.getPlanId()) return -1;
      if (o1.getPlanId() > o2.getPlanId()) return 1;

      if (o1.getZoneId() < o2.getZoneId()) return -1;
      if (o1.getZoneId() > o2.getZoneId()) return 1;
      return 0;
    });
    List<PlanZoneBill> planZoneBillList = new ArrayList<>();

    PlanZoneBill planZoneBill = null;
    boolean needConstructor = true;
    for (DeviceBillData deviceBillData : deviceBillDataList) {

      if(deviceBillData.getPlanType()==ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE  ||
         deviceBillData.getPlanType()==ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
         deviceBillData.getPlanType()==ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE    ||
         deviceBillData.getPlanType()==ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
        continue;
      }

      if (planZoneBill != null) {
        if (planZoneBill.getPlanId() == deviceBillData.getPlanId() &&
            planZoneBill.getZoneId() == deviceBillData.getZoneId()) {

          planZoneBill.setIncludeValue(planZoneBill.getIncludeValue() + Long.valueOf(deviceBillData.getIncludeValue()));
          planZoneBill.setGprsValue(planZoneBill.getGprsValue() + deviceBillData.getGprsValue());
          planZoneBill.setGprsFee(planZoneBill.getGprsFee() + Long.valueOf(deviceBillData.getGprsFee()));
          planZoneBill.setRoundAdjust(planZoneBill.getRoundAdjust() + deviceBillData.getRoundAdjust());
          planZoneBill.setBulkAdjust(planZoneBill.getBulkAdjust() + Long.valueOf(deviceBillData.getBulkAdjust()));
          needConstructor = false;
        } else {
          planZoneBillList.add(planZoneBill);
          needConstructor = true;
        }
      }

      if (planZoneBill == null || needConstructor) {

        planZoneBill = new PlanZoneBill();
        planZoneBill.setAcctId(deviceBillData.getAcctId());
        planZoneBill.setCycleId(deviceBillData.getCycleId());
        planZoneBill.setPlanId(deviceBillData.getPlanId());
        planZoneBill.setPlanType(deviceBillData.getPlanType());
        planZoneBill.setZoneId(deviceBillData.getZoneId());
        planZoneBill.setRemote(deviceBillData.isRemote());
        planZoneBill.setIsExpireTermByUsage(deviceBillData.getIsExpireTermByUsage());
        planZoneBill.setGprsValue(deviceBillData.getGprsValue());
        planZoneBill.setGprsFee(Long.valueOf(deviceBillData.getGprsFee())  );
        planZoneBill.setIncludeMode(deviceBillData.getIncludeMode());
        planZoneBill.setIncludeValue(Long.valueOf(deviceBillData.getIncludeValue()));
        planZoneBill.setRoundAdjust(deviceBillData.getRoundAdjust());
        planZoneBill.setBulkAdjust(Long.valueOf(deviceBillData.getBulkAdjust()));
      }
    }
    if(planZoneBill!=null){
      planZoneBillList.add(planZoneBill);
    }
    return planZoneBillList;
  }

  /**
   * 共享类， 生成区域账单
   */
  public static <T extends ResInterface,Y extends ShareAddInterface>  List<PlanZoneBill>
          getPlanZoneBillList(ParamContainer paramContainer,
                              final List<T> resIncludeList,
                              final List<Y> shareAddList,
                              final List<AcctBillUsage> acctBillUsageList) {

    if (resIncludeList == null || resIncludeList.isEmpty()) {
      return Collections.emptyList();
    }
    resIncludeList.sort((o1, o2) -> {
      if (o1.getPlanId() < o2.getPlanId()) return -1;
      if (o1.getPlanId() > o2.getPlanId()) return 1;

      if (o1.getZoneId() < o2.getZoneId()) return -1;
      if (o1.getZoneId() > o2.getZoneId()) return 1;
      return 0;
    });

    int cycleId = paramContainer.getCycleBean().getCycleId();

    List<PlanZoneBill> planZoneBillList = new ArrayList<>();

    PlanZoneBill planZoneBill = null;
    boolean needConstructor = true;
    for(T resInclude:resIncludeList){

      RatePlanBean ratePlanBean = paramContainer.getRatePlan(resInclude.getPlanId());
      if(ratePlanBean == null) continue;

      RateBill rateBill= paramContainer.getRateBill(resInclude.getPlanVersionId(),resInclude.getBillId());
      if(rateBill == null) continue;
      if(rateBill.getBizType() != ParamEnum.BizType.BIZ_TYPE_DATA) continue;

      if (planZoneBill != null) {
        if (planZoneBill.getPlanId() == resInclude.getPlanId() &&
            planZoneBill.getZoneId() == resInclude.getZoneId()) {

          planZoneBill.setIncludeValue(planZoneBill.getIncludeValue() + resInclude.getValue()); //计划内流量
          for (Y shareAdd : shareAddList) {
            if (shareAdd.getAcctId() == resInclude.getAcctId() &&
                shareAdd.getPlanVersionId() == resInclude.getPlanVersionId() &&
                shareAdd.getBillId() == resInclude.getBillId()) {
              planZoneBill.setGprsValue(planZoneBill.getGprsValue() +  shareAdd.getCurrValue()); //流量
              planZoneBill.setRoundAdjust(planZoneBill.getRoundAdjust() + shareAdd.getRoundAdjust()); //每日舍入调整
              planZoneBill.setBulkAdjust(shareAdd.getBulkAdjust()); //批量超额
              break;
            }
          }
          needConstructor = false;
        } else {
          planZoneBillList.add(planZoneBill);
          needConstructor = true;
        }
      }

      if (planZoneBill == null || needConstructor) {

        planZoneBill = new PlanZoneBill();
        planZoneBill.setAcctId(resInclude.getAcctId());
        planZoneBill.setCycleId(cycleId);
        planZoneBill.setPlanId(resInclude.getPlanId());
        planZoneBill.setPlanType(ratePlanBean.getPlanType());
        planZoneBill.setZoneId(resInclude.getZoneId());
        planZoneBill.setRemote(rateBill.isRoam());
        planZoneBill.setIsExpireTermByUsage(0);
        int includeMode = getIncludeMode(ratePlanBean.getPlanType());
        planZoneBill.setIncludeMode(includeMode);
        planZoneBill.setIncludeValue(resInclude.getValue()); //计划内流量

        planZoneBill.setGprsValue(0L);
        planZoneBill.setRoundAdjust(0L);
        for (Y shareAdd : shareAddList) {
          if (shareAdd.getAcctId() == resInclude.getAcctId() &&
              shareAdd.getPlanVersionId() == resInclude.getPlanVersionId() &&
              shareAdd.getBillId() == resInclude.getBillId()) {
            planZoneBill.setGprsValue(shareAdd.getCurrValue());     //流量
            planZoneBill.setRoundAdjust(shareAdd.getRoundAdjust()); //每日舍入调整
            planZoneBill.setBulkAdjust(shareAdd.getBulkAdjust());
            break;
          }
        }
      }
    }
    if(planZoneBill!=null){
      planZoneBillList.add(planZoneBill);
    }
    //超出费用
    for(PlanZoneBill t:planZoneBillList){
      t.setGprsFee(0L);
      for(AcctBillUsage acctBillUsage:acctBillUsageList){
        if(acctBillUsage.getAcctId()==t.getAcctId() &&
           acctBillUsage.getPlanId()==t.getPlanId() &&
           acctBillUsage.getZoneId()==t.getZoneId()){
          t.setGprsFee(acctBillUsage.getFee());

          break;
        }
      }
    }
    return planZoneBillList;
  }

  /**
   * 检查资费计划账单 是否存在
   */
  public static PlanBill getPlanBill(final int planVersionId,
                                     final List<PlanBill> planBillList) {

    if (planBillList == null || planBillList.isEmpty()) {
      return null;
    }
    return planBillList.stream().filter(t -> t.getPlanVersionId() ==planVersionId ).findAny().orElse(null);
  }

  /**
   * 检查 接口账单 是否存在
   */
  public static AcctBill2Bss getAcctBill2Bss(final int billType, final int headquarterItemId, final int provItemId,
                                             final List<AcctBill2Bss> acctBill2BssList) {
    if (acctBill2BssList == null || acctBill2BssList.isEmpty()) {
      return null;
    }
    return
    acctBill2BssList.stream().filter(t -> t.getBillType() == billType &&
                                          t.getHeadquarterItemId() == headquarterItemId &&
                                          t.getProvItemId() == provItemId).findAny().orElse(null);
  }

  /**
   * 获取预付共享资费计划的 参数信息
   */
  public static <T extends ResInterface> void getPrepayInclude(final List<T> tList,
                                                               final ParamContainer paramContainer,
                                                               Map<String,Long> valueMap){

    if (tList == null || tList.isEmpty()) {
        return;
    }
    for (T tRecord : tList) {
      long startTime = Long.valueOf(DateUtil.getCurrentDateTime(tRecord.getStartTime(),DateUtil.YYYYMMDD_HHMMSS)) ;
      long endTime = Long.valueOf(DateUtil.getCurrentDateTime(tRecord.getEndTime(),DateUtil.YYYYMMDD_HHMMSS)) ;
      valueMap.put("termStartDate",startTime);
      valueMap.put("termEndDate",endTime);
      RateBill rateBill = paramContainer.getRateBill(tRecord.getPlanVersionId(), tRecord.getBillId());
      setIncludeValue(rateBill,valueMap);
    }
  }

  public static void getRateBillInclude(final List<RateBill> tList,
                                        Map<String,Long> valueMap){

    if (tList == null || tList.isEmpty()) {
      return;
    }
    tList.forEach(t -> setIncludeValue(t,valueMap));
  }

  private static void setIncludeValue(final RateBill tRecord,
                                      Map<String,Long> valueMap){

    if(tRecord == null){
      return;
    }
    if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
      addValue(valueMap,"includeDataValue",tRecord.getValue());
    } else if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
      addValue(valueMap,"includeVoiceValue",tRecord.getValue());
      if (tRecord.getChargeMode() == ParamEnum.ChargeMode.CHARGE_MODE_MO) {
        addValue(valueMap,"includeVoiceMoValue",tRecord.getValue());
      } else if (tRecord.getChargeMode() == ParamEnum.ChargeMode.CHARGE_MODE_MT) {
        addValue(valueMap,"includeVoiceMtValue",tRecord.getValue());
      }
    } else if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
      addValue(valueMap,"includeSmsValue",tRecord.getValue());
      if (tRecord.getChargeMode() == ParamEnum.ChargeMode.CHARGE_MODE_MO) {
        addValue(valueMap,"includeSmsMoValue",tRecord.getValue());
      } else if (tRecord.getChargeMode() == ParamEnum.ChargeMode.CHARGE_MODE_MT) {
        addValue(valueMap,"includeSmsMtValue",tRecord.getValue());
      }
    }
  }

  private static void addValue(Map<String,Long> valueMap,String key,long value){
    valueMap.put(key,value + valueMap.getOrDefault(key,0L));
  }

  public static <T extends BillUsageInterface> void getBillUsage(final List<T> tList,
                                                                 final int planVersionId,
                                                                 Map<String,Long> valueMap){
    if (tList == null || tList.isEmpty()) {
      return;
    }

    tList.stream().filter(tRecord -> tRecord.getPlanVersionId() == planVersionId).forEach(tRecord -> {
      if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
        addValue(valueMap, "dataCharge", tRecord.getFee());
        if (tRecord.isRoam()) {
          addValue(valueMap, "remoteDataCharge", tRecord.getFee());
        } else {
          addValue(valueMap, "localDataCharge", tRecord.getFee());
        }
      } else if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
        addValue(valueMap, "voiceCharge", tRecord.getFee());
      } else if (tRecord.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
        addValue(valueMap, "smsCharge", tRecord.getFee());
      }
    });
  }

  public static void getAddValue(final List<UsedAddDevice> usedAddDeviceList,
                                 final int planVersionId,
                                 final ParamContainer paramContainer,
                                 Map<String,Long> valueMap){
    usedAddDeviceList.stream().filter(usedAddDevice -> usedAddDevice.getPlanVersionId() == planVersionId).forEach(usedAddDevice -> {
      if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
        addValue(valueMap, "dataValue", usedAddDevice.getCurrValue());
        RateBill rateBill = paramContainer.getRateBill(usedAddDevice.getPlanVersionId(), usedAddDevice.getBillId());
        if (rateBill.isRoam()) {
          addValue(valueMap, "remoteData", usedAddDevice.getCurrValue());
        } else {
          addValue(valueMap, "localData", usedAddDevice.getCurrValue());
        }
      } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
        addValue(valueMap, "voiceValue", usedAddDevice.getCurrValue());
        addValue(valueMap, "voiceMoValue", usedAddDevice.getMoValue());
        addValue(valueMap, "voiceMtValue", usedAddDevice.getMtValue());
      } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
        addValue(valueMap, "smsValue", usedAddDevice.getCurrValue());
        addValue(valueMap, "smsMoValue", usedAddDevice.getMoValue());
        addValue(valueMap, "smsMtValue", usedAddDevice.getMtValue());
      }
    });
  }

  public static void getAddValue(final List<UsedAddDevice> usedAddDeviceList,
                                 Map<String,Long> valueMap){
    if (usedAddDeviceList != null && !usedAddDeviceList.isEmpty()) {
      for (UsedAddDevice usedAddDevice : usedAddDeviceList) {
        switch (usedAddDevice.getBizType()) {
          case ParamEnum.BizType.BIZ_TYPE_DATA:
            addValue(valueMap, "termDataUsage", usedAddDevice.getLastValue() + usedAddDevice.getCurrValue());
            addValue(valueMap, "currPeroidData", usedAddDevice.getCurrValue());
            break;
          case ParamEnum.BizType.BIZ_TYPE_VOICE:
            addValue(valueMap, "termVoiceUsage", usedAddDevice.getLastValue() + usedAddDevice.getCurrValue());
            addValue(valueMap, "currPeroidVoice", usedAddDevice.getCurrValue());
            addValue(valueMap, "currPeroidVoiceMt", usedAddDevice.getMoValue());
            addValue(valueMap, "currPeroidVoiceMt", usedAddDevice.getMtValue());
            break;
          case ParamEnum.BizType.BIZ_TYPE_SMS:
            addValue(valueMap, "termSmsUsage", usedAddDevice.getLastValue() + usedAddDevice.getCurrValue());
            addValue(valueMap, "currPeroidSms", usedAddDevice.getCurrValue());
            addValue(valueMap, "currPeroidSmsMo", usedAddDevice.getMoValue());
            addValue(valueMap, "currPeroidSmsMt", usedAddDevice.getMtValue());
            break;
          default:
            break;
        }
      }
    }


  }


  /**
   * 获取费用信息
   */
  public static void getCharge(final AcctBillContainer acctBillContainer,
                               Map<String,Long> valueMap){
    //订购费
    List<DeviceBillOrder> deviceBillOrderList = acctBillContainer.getDeviceBillOrderList();
    if (deviceBillOrderList != null && !deviceBillOrderList.isEmpty()) {
      deviceBillOrderList.forEach(deviceBillOrder -> {
        addValue(valueMap, "orderCharge", deviceBillOrder.getFee());
        if (deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT ||
            deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PILE) {
          addValue(valueMap, "events", 1);
          addValue(valueMap, "eventCharge", deviceBillOrder.getFee());
        }
      });
    }
    //使用费,标准超额，标准漫游
    List<DeviceBillUsage> deviceBillUsageList = acctBillContainer.getDeviceBillUsageList();
    if (deviceBillUsageList != null && !deviceBillUsageList.isEmpty()) {
      deviceBillUsageList.forEach(deviceBillUsage -> {
        if (deviceBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
          addValue(valueMap, "gprsCharge", deviceBillUsage.getFee());
          addValue(valueMap, "standardCharge", deviceBillUsage.getFee());
          if (deviceBillUsage.isRoam()) {
            addValue(valueMap, "standardRoamCharge", deviceBillUsage.getFee());
          }
        } else if (deviceBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
          addValue(valueMap, "voiceCharge", deviceBillUsage.getFee());
        } else if (deviceBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
          addValue(valueMap, "smsCharge", deviceBillUsage.getFee());
        }
      });
    }

    List<AcctBillUsage> acctBillUsageList = acctBillContainer.getAcctBillUsageList();
    if (acctBillUsageList != null && !acctBillUsageList.isEmpty()) {
      acctBillUsageList.forEach(acctBillUsage -> {
        if (acctBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
          addValue(valueMap, "gprsCharge", acctBillUsage.getFee());
          addValue(valueMap, "standardCharge", acctBillUsage.getFee());
          if (acctBillUsage.isRoam()) {
            addValue(valueMap, "standardRoamCharge", acctBillUsage.getFee());
          }
        } else if (acctBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
          addValue(valueMap, "voiceCharge", acctBillUsage.getFee());
        } else if (acctBillUsage.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
          addValue(valueMap, "smsCharge", acctBillUsage.getFee());
        }
      });
    }

    //激活费用
    List<DeviceBillActivation> deviceBillActivationList = acctBillContainer.getDeviceBillActivationList();
    if (deviceBillActivationList != null && !deviceBillActivationList.isEmpty()) {
      long avtivationCharge = deviceBillActivationList.stream().mapToLong(DeviceBillActivation::getFee).sum();
      valueMap.put("avtivationCharge",avtivationCharge);
    }
    //其它费用
    List<BillTrackAcct> billTrackAcctList = acctBillContainer.getBillTrackAcctList();
    if (billTrackAcctList != null && !billTrackAcctList.isEmpty()) {
      billTrackAcctList.forEach(billTrackAcct ->{
        if (billTrackAcct.getStage() == BillEnum.AcctTrackStage.ACCT_ORDER||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.ACCT_OTHER) {
          addValue(valueMap, "otherCharge", billTrackAcct.getFee());
        }
        if (billTrackAcct.getStage() == BillEnum.AcctTrackStage.PLAT_SMS) {
          addValue(valueMap, "platSmsCharge", billTrackAcct.getFee());
        }
        if (billTrackAcct.getStage() == BillEnum.AcctTrackStage.GROUP_DISCOUNT ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.ACCT_DISCOUNT) {
          addValue(valueMap, "discountCharge", billTrackAcct.getFee());
        }
        if (billTrackAcct.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_ACTIVIE ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_ORDER ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_USAGE ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.PLAN_ACCT_FEE ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.APPEND_PLAN ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.SHARE_USAGE ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.ACCT_ORDER ||
            billTrackAcct.getStage() == BillEnum.AcctTrackStage.ACCT_OTHER) {
          addValue(valueMap, "serviceCharge", billTrackAcct.getFee());
        }
        addValue(valueMap, "totalCharge", billTrackAcct.getFee());
      });
    }

  }

  /**
   * 设备的累积量，转换成共享累积量的明细
   */
  public static UsedAddShareDetail getUsedAddShareDetail(final long poolId,
                                                         final UsedAddDevice usedAddDevice){

    UsedAddShareDetail usedAddShareDetail = new UsedAddShareDetail();

    usedAddShareDetail.setAcctId(usedAddDevice.getAcctId());
    usedAddShareDetail.setDeviceId(usedAddDevice.getDeviceId());
    usedAddShareDetail.setCycleId(usedAddDevice.getCycleId());
    usedAddShareDetail.setTpInsId(usedAddDevice.getTpInsId());
    usedAddShareDetail.setPoolId(poolId);
    usedAddShareDetail.setPlanId(usedAddDevice.getPlanId());
    usedAddShareDetail.setPlanVersionId(usedAddDevice.getPlanVersionId());
    usedAddShareDetail.setBillId(usedAddDevice.getBillId());
    usedAddShareDetail.setCurrValue(usedAddDevice.getCurrValue());
    usedAddShareDetail.setRoundAdjust(usedAddDevice.getRoundAdjust());
    usedAddShareDetail.setBulkAdjust(usedAddDevice.getBulkAdjust());
    usedAddShareDetail.setLastValue(usedAddDevice.getLastValue());

    return usedAddShareDetail;
  }

  /**
   * 从共享转移明细中，获取共享的总量信息
   */
  public static List<ResIncludeShare> getResIncludeShareList(final boolean fixShare,
                                                             final CycleBean cycleBean,
                                                             List<ResIncludeShareTurn> resIncludeShareTrunList){

    if(resIncludeShareTrunList==null || resIncludeShareTrunList.isEmpty()){
      return Collections.emptyList();
    }

    resIncludeShareTrunList.sort((o1, o2) -> {
      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;

      if (o1.getPlanVersionId() < o2.getPlanVersionId()) return -1;
      if (o1.getPlanVersionId() > o2.getPlanVersionId()) return 1;

      if (o1.getBillId() < o2.getBillId()) return -1;
      if (o1.getBillId() > o2.getBillId()) return 1;
      return 0;
    });

    List<ResIncludeShare> resIncludeShareList=new ArrayList<>();
    ResIncludeShare resIncludeShare = null;
    boolean needConstructor = true;
    for(ResIncludeShareTurn resIncludeShareTrun:resIncludeShareTrunList){

      if (resIncludeShare != null) {
        if (resIncludeShare.getAcctId() == resIncludeShareTrun.getAcctId() &&
            resIncludeShare.getPlanVersionId() == resIncludeShareTrun.getPlanVersionId() &&
            resIncludeShare.getBillId() == resIncludeShareTrun.getBillId()) {

          if(fixShare){
            resIncludeShare.setValue(resIncludeShare.getValue() + resIncludeShareTrun.getPileValue());
          }else {
            resIncludeShare.setValue(resIncludeShare.getValue() + resIncludeShareTrun.getValue() + resIncludeShareTrun.getPileValue());
          }
          needConstructor = false;
        } else {
          if(resIncludeShare.getValue()>0){
            resIncludeShareList.add(resIncludeShare);
          }
          needConstructor = true;
        }
      }
      if (resIncludeShare == null || needConstructor) {
        resIncludeShare = new ResIncludeShare();
        resIncludeShare.setAcctId(resIncludeShareTrun.getAcctId());
        resIncludeShare.setPlanId(resIncludeShareTrun.getPlanId());
        resIncludeShare.setPlanVersionId(resIncludeShareTrun.getPlanVersionId());
        resIncludeShare.setBillId(resIncludeShareTrun.getBillId());
        resIncludeShare.setCycleId(resIncludeShareTrun.getCycleId());
        resIncludeShare.setZoneId(resIncludeShareTrun.getZoneId());
        resIncludeShare.setGroupId(resIncludeShareTrun.getBillingGroupId());

        if(fixShare){
          resIncludeShare.setValue(resIncludeShareTrun.getPileValue());
        }else {
          resIncludeShare.setValue(resIncludeShareTrun.getValue() + resIncludeShareTrun.getPileValue());
        }
        resIncludeShare.setStartTime(cycleBean.getCycStartTime());
        resIncludeShare.setEndTime(cycleBean.getCycEndTime());
      }
    }
    if(resIncludeShare!=null){
      if(resIncludeShare.getValue()>0){
        resIncludeShareList.add(resIncludeShare);
      }
    }
    return resIncludeShareList;
  }



}
