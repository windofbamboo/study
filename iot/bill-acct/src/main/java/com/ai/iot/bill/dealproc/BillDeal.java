package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.dao.AcctBillDao;
import com.ai.iot.bill.dao.InfoDao;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dao.ParamDao;
import com.ai.iot.bill.dealproc.container.*;
import com.ai.iot.bill.dealproc.util.BillUtil;
import com.ai.iot.bill.dealproc.util.InfoUtil;
import com.ai.iot.bill.dealproc.util.ParamUtil;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.entity.computebill.*;
import com.ai.iot.bill.entity.info.AcctAdjustBeforeBean;
import com.ai.iot.bill.entity.info.AcctBillingGeneralBean;
import com.ai.iot.bill.entity.info.AcctDiscountBean;
import com.ai.iot.bill.entity.info.AcctDiscountGrade;
import com.ai.iot.bill.entity.info.AcctInfoBean;
import com.ai.iot.bill.entity.info.AcctMonthFeeBean;
import com.ai.iot.bill.entity.info.AcctOrderBean;
import com.ai.iot.bill.entity.info.AcctPromiseBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountBean;
import com.ai.iot.bill.entity.info.AcctSmsDiscount;
import com.ai.iot.bill.entity.info.AcctValumeDiscountBean;
import com.ai.iot.bill.entity.info.AppendFeepolicyBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.SharePoolBean;
import com.ai.iot.bill.entity.multibill.*;
import com.ai.iot.bill.entity.param.*;
import com.ai.iot.bill.entity.res.*;
import com.ai.iot.bill.entity.usage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 账户聚合阶段,账户级处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BillDeal extends BaseFeeDeal {

  private static final Logger logger = LoggerFactory.getLogger(BillDeal.class);

  private BillDeal() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 账户级费用处理
   */
  public static boolean deal(final DealAcct dealAcct) {

    //参数初始化
    long dealId = dealAcct.getDealId();
    long acctId = dealAcct.getAcctId();
    long seqId = dealAcct.getSeqId();

    ParamContainer paramContainer = ParamMgr.getParamContainer(dealId);
    if(paramContainer==null){
      LogDao.updateBillLog(dealId,acctId,ErrEnum.ParamErr.PARAM_INIT_ERR);
      return false;
    }
    LogDao.updateBillLog(dealId,acctId,ErrEnum.DEAL_START);
    //重新获取账户资料
    AcctInfoContainer acctInfoContainer = AcctInfoDeal.getAcctInfo(dealId,acctId);
    if(acctInfoContainer==null){
      LogDao.updateBillLog(dealId,acctId,ErrEnum.InfoErr.GET_ACCT_INFO_ERR);
      return false;
    }
    acctInfoContainer.setSeqId(seqId);

    boolean isThirdAcct = InfoEnum.IsPayBack.YES.equals(acctInfoContainer.getAcctInfoBean().getIsPayBack());

    //设备级信息的获取
    CycleBean cycleBean = paramContainer.getCycleBean();
    AcctBillContainer acctBillContainer = initDeviceBill(cycleBean, acctId);
    //累积量获取
    if(!isThirdAcct){
      try {
        initAcctAdd(paramContainer, acctBillContainer);
      }catch (Exception e){
        LogDao.updateBillLog(dealId,acctId,ErrEnum.DealErr.ACCT_GET_ADD);
        logger.error("initAcctAdd err :{}",e);
        return false;
      }
    }
    //费用处理
    try{
      dealFee(paramContainer, acctInfoContainer, acctBillContainer);
    }catch (Exception e){
      LogDao.updateBillLog(dealId,acctId,ErrEnum.DealErr.ACCT_COMPUTE_FEE);
      logger.error("dealFee err :{}" , e);
      return false;
    }
    //多维度账单生成
    try {
      setMultiBill(paramContainer, acctInfoContainer, acctBillContainer);
    }catch (Exception e){
      LogDao.updateBillLog(dealId,acctId,ErrEnum.DealErr.ACCT_MULTI_BILL);
      logger.error("setMultiBill err :{}",e);
      return false;
    }
    //数据提交
    ErrEnum.DealResult dealResult = updatePdb(cycleBean, acctId, acctBillContainer);
    //记录日志
    LogDao.updateBillLog(dealId,acctId,dealResult);

    return ErrEnum.DEAL_SUCESS.equals(dealResult);
  }

  /**
   * 提交数据
   * @param cycleBean 账期
   * @param acctId 账户
   * @param acctBillContainer 账单
   */
  private static ErrEnum.DealResult updatePdb(final CycleBean cycleBean,
                                              final long acctId,
                                              final AcctBillContainer acctBillContainer){

    String month = String.valueOf(cycleBean.getCycleId()).substring(4, 6);
    //删除原有账单
    ErrEnum.DealResult dealResult = AcctBillDao.deleteAcctPdb(month, acctId);
    if(ErrEnum.DEAL_SUCESS.equals(dealResult)){
      //账单更新
      dealResult = AcctBillDao.insertDevicePdb(month, acctId, acctBillContainer);
    }
    return dealResult;
  }

  /**
   * 获取设备级账单信息
   * @param cycleBean 账期
   * @param acctId 账户
   * @return 账户账单
   */
  private static AcctBillContainer initDeviceBill(final CycleBean cycleBean,
                                                  final long acctId) {

    AcctBillContainer acctBillContainer = new AcctBillContainer();

    String month = String.valueOf(cycleBean.getCycleId()).substring(4, 6);
    AcctBillDao.getDevicePdb(month, acctId, acctBillContainer);
    //账单原始费用处理
    List<BillTrackAcct> billTrackAcctList = acctBillContainer.getBillTrackAcctList();
    billTrackAcctList.clear();
//    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_ACTIVIE,acctBillContainer.getDeviceBillActivationList()));
//    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_ORDER,acctBillContainer.getDeviceBillOrderList()));
//    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_USAGE,acctBillContainer.getDeviceBillUsageList()));

    //共享总额处理
    //月付固定共享
    List<ResIncludeShare> resIncludeFixShareList =
        BillUtil.getResIncludeShareList(true,cycleBean,acctBillContainer.getResIncludeFixShareTurnList());
    if(!resIncludeFixShareList.isEmpty()){
      acctBillContainer.setResIncludeFixShareList(resIncludeFixShareList);
    }
    //灵活共享
    List<ResIncludeShare> resIncludeAgileShareList=
        BillUtil.getResIncludeShareList(false,cycleBean,acctBillContainer.getResIncludeAgileShareTrunList());
    if(!resIncludeAgileShareList.isEmpty()){
      acctBillContainer.setResIncludeAgileShareList(resIncludeAgileShareList);
    }
    return acctBillContainer;
  }

  /**
   * 累积量数据获取
   * @param paramContainer 参数
   * @param acctBillContainer 账单
   */
  private static void initAcctAdd(final ParamContainer paramContainer,
                                  AcctBillContainer acctBillContainer) throws Exception {
    //共享的累积量求取
    List<UsedAddShareDetail> usedAddShareDetailList=acctBillContainer.getUsedAddShareDetailList();

    if(usedAddShareDetailList!=null && !usedAddShareDetailList.isEmpty()){
      usedAddShareDetailList.sort( (o1, o2) -> {
        if (o1.getAcctId() < o2.getAcctId()) return -1;
        if (o1.getAcctId() > o2.getAcctId()) return 1;

        if (o1.getPoolId() < o2.getPoolId()) return -1;
        if (o1.getPoolId() > o2.getPoolId()) return 1;

        if (o1.getPlanVersionId() < o2.getPlanVersionId()) return -1;
        if (o1.getPlanVersionId() > o2.getPlanVersionId()) return 1;

        return Integer.compare(o1.getBillId(), o2.getBillId());
      });

      List<UsedAddShare> usedAddShareList = acctBillContainer.getUsedAddShareList();
      ////预付灵活共享、月付灵活共享、月付固定共享
      UsedAddShare usedAddShare = null;
      boolean needConstructor = true;
      for(UsedAddShareDetail usedAddShareDetail:usedAddShareDetailList){
        RatePlanBean ratePlanBean = paramContainer.getRatePlan(usedAddShareDetail.getPlanId());
        if(ratePlanBean==null){
          continue;
        }
        if(ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE &&
           ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE &&
           ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE){
          continue;
        }
        if (usedAddShare != null) {
          if (usedAddShare.getAcctId() == usedAddShareDetail.getAcctId() &&
              usedAddShare.getPlanVersionId() == usedAddShareDetail.getPlanVersionId() &&
              usedAddShare.getBillId() == usedAddShareDetail.getBillId()) {

            usedAddShare.setCurrValue(usedAddShare.getCurrValue()+usedAddShareDetail.getCurrValue());
            usedAddShare.setRoundAdjust(usedAddShare.getRoundAdjust()+usedAddShareDetail.getRoundAdjust());
            usedAddShare.setBulkAdjust(usedAddShare.getBulkAdjust()+usedAddShareDetail.getBulkAdjust());
            needConstructor = false;
          } else {
            usedAddShareList.add(usedAddShare);
            needConstructor = true;
          }
        }
        if (usedAddShare == null || needConstructor) {
          usedAddShare = new UsedAddShare();
          usedAddShare.setAcctId(usedAddShareDetail.getAcctId());
          usedAddShare.setPlanVersionId(usedAddShareDetail.getPlanVersionId());
          usedAddShare.setBillId(usedAddShareDetail.getBillId());
          usedAddShare.setCycleId(usedAddShareDetail.getCycleId());
          usedAddShare.setCurrValue(usedAddShareDetail.getCurrValue());
          usedAddShare.setRoundAdjust(usedAddShareDetail.getRoundAdjust());
          usedAddShare.setBulkAdjust(usedAddShareDetail.getBulkAdjust());
          usedAddShare.setUpperValue(0);
        }
      }
      if(usedAddShare!=null){
        usedAddShareList.add(usedAddShare);
      }
      //预付固定共享
      List<UsedAddPoolTotal> usedAddPoolTotalList = acctBillContainer.getUsedAddPoolTotalList();
      UsedAddPoolTotal usedAddPoolTotal = null;
      needConstructor = true;
      for(UsedAddShareDetail usedAddShareDetail:usedAddShareDetailList){
        RatePlanBean ratePlanBean = paramContainer.getRatePlan(usedAddShareDetail.getPlanId());
        if(ratePlanBean==null){
          continue;
        }
        if(ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
          continue;
        }
        if (usedAddPoolTotal != null) {
          if (usedAddPoolTotal.getAcctId() == usedAddShareDetail.getAcctId() &&
              usedAddPoolTotal.getPoolId() == usedAddShareDetail.getPoolId() &&
              usedAddPoolTotal.getPlanVersionId() == usedAddShareDetail.getPlanVersionId() &&
              usedAddPoolTotal.getBillId() == usedAddShareDetail.getBillId()) {

            usedAddPoolTotal.setLastValue(usedAddPoolTotal.getLastValue()+usedAddShareDetail.getLastValue());
            usedAddPoolTotal.setCurrValue(usedAddPoolTotal.getCurrValue()+usedAddShareDetail.getCurrValue());
            usedAddPoolTotal.setRoundAdjust(usedAddPoolTotal.getRoundAdjust()+usedAddShareDetail.getRoundAdjust());
            usedAddPoolTotal.setBulkAdjust(usedAddPoolTotal.getBulkAdjust()+usedAddShareDetail.getBulkAdjust());
            needConstructor = false;
          } else {
            usedAddPoolTotalList.add(usedAddPoolTotal);
            needConstructor = true;
          }
        }
        if (usedAddPoolTotal == null || needConstructor) {
          usedAddPoolTotal = new UsedAddPoolTotal();
          usedAddPoolTotal.setAcctId(usedAddShareDetail.getAcctId());
          usedAddPoolTotal.setPoolId(usedAddShareDetail.getPoolId());
          usedAddPoolTotal.setPlanVersionId(usedAddShareDetail.getPlanVersionId());
          usedAddPoolTotal.setBillId(usedAddShareDetail.getBillId());
          usedAddPoolTotal.setCycleId(usedAddShareDetail.getCycleId());
          usedAddPoolTotal.setCurrValue(usedAddShareDetail.getCurrValue());
          usedAddPoolTotal.setLastValue(usedAddShareDetail.getLastValue());
          usedAddPoolTotal.setRoundAdjust(usedAddShareDetail.getRoundAdjust());
          usedAddPoolTotal.setBulkAdjust(usedAddShareDetail.getBulkAdjust());
          usedAddPoolTotal.setUpperValue(0);
        }
      }
      if(usedAddPoolTotal!=null){
        usedAddPoolTotalList.add(usedAddPoolTotal);
      }
    }

  }

  /**
   * 费用计算
   */
  private static void dealFee(final ParamContainer paramContainer,
                              final AcctInfoContainer acctInfoContainer,
                              AcctBillContainer acctBillContainer)
      throws Exception{
    if(InfoEnum.IsPayBack.NO.equals(acctInfoContainer.getAcctInfoBean().getIsPayBack())){
      //订购费处理（月付/预付固定共享,涉及按档次收取订户费）
      dealFixShareOrderFee(paramContainer, acctInfoContainer, acctBillContainer);
      //追加资费费用收取
      dealAddPlanOrderFee(paramContainer, acctInfoContainer, acctBillContainer);
      //资费组折扣or总监折扣
      dealGroupDiscount(paramContainer, acctInfoContainer, acctBillContainer);
      //账户级用量核减
      dealAcctUsageFee(paramContainer, acctInfoContainer, acctBillContainer);
      //订单费用
      dealAcctOrderFee(paramContainer, acctInfoContainer, acctBillContainer);
      //一次性费用+月费
      dealAcctMonth(paramContainer, acctInfoContainer, acctBillContainer);
      //账前调账费用
      dealAcctAdjustBefore(paramContainer, acctInfoContainer, acctBillContainer);
      //账户折扣
      dealAcctValumeDiscount(paramContainer, acctInfoContainer, acctBillContainer);
      //平台短信
      dealPlatSms(paramContainer, acctInfoContainer, acctBillContainer);
      //账户承诺
      dealAcctPromise(paramContainer, acctInfoContainer, acctBillContainer);
    }else{
      //第三方计费
      dealThirdPartyPlan(paramContainer, acctInfoContainer, acctBillContainer);
    }

    BillUtil.trimBillTrackAcct(acctBillContainer.getBillTrackAcctList());
    //费用汇总
    List<BillAcct> billAcctList = BillUtil.getBillAcctList(acctBillContainer.getBillTrackAcctList());
    acctBillContainer.setBillAcctList(billAcctList);
  }

  /**
   * 固定共享的订购费用处理
   * 账户费处理：收取账户费，再按照设备，进行平摊,
   * 订户费处理：如果订户费是按档次处理的，则需要进行对费用进行重置，如果不是，则保持不变
   * @param paramContainer 参数
   * @param acctInfoContainer 账户资料
   * @param acctBillContainer 账户账单
   */
  private static void dealFixShareOrderFee(final ParamContainer paramContainer,
                                           final AcctInfoContainer acctInfoContainer,
                                           AcctBillContainer acctBillContainer)
      throws Exception{

    acctBillContainer.setOrderNo(0);

    Map<AcctShare, Set<Long>> monthFixShareMap = acctInfoContainer.getMonthFixShareMap();
    Map<AcctShare, Set<Long>> prepayFixShareMap = acctInfoContainer.getPrepayFixShareMap();
    //订户费处理
    dealFixShare(paramContainer, monthFixShareMap,acctInfoContainer.getDeviceRatePlanBeanList(),acctInfoContainer.getAcctBillingGeneralBean(),acctBillContainer);
    dealFixShare(paramContainer, prepayFixShareMap,acctInfoContainer.getDeviceRatePlanBeanList(),acctInfoContainer.getAcctBillingGeneralBean(),acctBillContainer);

    if(!acctBillContainer.getResIncludeFixShareList().isEmpty()){
      BillUtil.trimResIncludeShare(acctBillContainer.getResIncludeFixShareList());
    }

    //账单原始费用处理
    List<BillTrackAcct> billTrackAcctList = acctBillContainer.getBillTrackAcctList();
    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_ACTIVIE,acctBillContainer.getDeviceBillActivationList()));
    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_ORDER,acctBillContainer.getDeviceBillOrderList()));
    billTrackAcctList.addAll(BillUtil.getBillTrackAcct(BillEnum.AcctTrackStage.DEVICE_SUM_USAGE,acctBillContainer.getDeviceBillUsageList()));
  }

  /**
   * 设置账户费，额度等信息
   */
  private static void dealFixShare(final ParamContainer paramContainer,
                                   final Map<AcctShare, Set<Long>> fixShareMap,
                                   final List<DeviceRatePlanBean> deviceRatePlanBeanList,
                                   final AcctBillingGeneralBean acctBillingGeneralBean,
                                   AcctBillContainer acctBillContainer) throws Exception{

    CycleBean cycleBean = paramContainer.getCycleBean();

    for(Map.Entry<AcctShare, Set<Long>> entry:fixShareMap.entrySet()){
        AcctShare acctShare =entry.getKey();

      RateOrderFee rateOrderFee = paramContainer.getRateOrderFee(acctShare.getPlanVersionId());
      RatePlanBean ratePlanBean = paramContainer.getRatePlan(acctShare.getPlanId());
      int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_ACCT_PLAN, rateOrderFee.getPaymentType());
      //获取订户数
      List<DeviceBillOrder> newDeviceBillOrderList =
          acctBillContainer.getDeviceBillOrderList(rateOrderFee.getPlanVersionId(), fixShareMap.get(acctShare));

      long[] ratio = {1, 1};
      double orderNum = newDeviceBillOrderList.size();// 预付固定,按设备数计算
      double singleOrder = 1;
      if(ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE){
        ratio = calculateRatio(acctShare.getPlanId(),
                              cycleBean,
                              deviceRatePlanBeanList,
                              acctBillingGeneralBean);// 月付固定，按最早订购的设备来计算 分摊比例
        singleOrder = Math.floor((double) ratio[0] * 100 / (double) ratio[1]) / 100;
        orderNum = singleOrder * newDeviceBillOrderList.size(); // 月付固定，按最早订购的设备的比例来计算订购数
      }
      //额外订户费,月付固定，每个月都收取；预付固费，则判断PER_SUBSCRIBER_CHARGE标志，判断其是首月收取或者每月收取
      boolean singleOrderTag=false;
      if((ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE &&
         (rateOrderFee.getSubscriberChargeFerquency() == ParamEnum.SubscriberChargeFerquency.EVERY_MONTH ||
         acctShare.getStartTime().getTime()>cycleBean.getCycStartTime().getTime()) ) ||
         ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE)
      {
        singleOrderTag=true;
      }
      //获取订户费的档次参数
      long subscriberCharge = 0L;
      int levelPriority = 0;
      if(singleOrderTag){//求取订户费的设置值
        if (!rateOrderFee.getRatePlanLevelBeanList().isEmpty()) {
          for (RatePlanLevelBean ratePlanLevelBean : rateOrderFee.getRatePlanLevelBeanList()) {
            if (ratePlanLevelBean.getMinAmount() < orderNum &&
                ratePlanLevelBean.getMaxAmount() >= orderNum) {
              subscriberCharge = ratePlanLevelBean.getSubscriberCharge();
              levelPriority = ratePlanLevelBean.getLevelPriority();
              break;
            }
          }
        }else{
          subscriberCharge = rateOrderFee.getSubscriberCharge();
          levelPriority = 1;
        }
      }
      //计算订户费
      long singleCharge= subscriberCharge * ratio[0] / ratio[1];
      //账户费用
      long acctFee=0;
      //月付固定共享/ 预付固定共享的首月 ，收取账户费
      if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE ||
          acctShare.getStartTime().getTime() >= cycleBean.getCycStartTime().getTime()) {
        acctFee = rateOrderFee.getAccountCharge() * ratio[0] / ratio[1];
        //费用
        AcctBillOrder acctBillOrder = new AcctBillOrder();
        acctBillOrder.setAcctId(acctShare.getAcctId());
        acctBillOrder.setCycleId(cycleBean.getCycleId());
        acctBillOrder.setPlanId(acctShare.getPlanId());
        acctBillOrder.setPlanVersionId(acctShare.getPlanVersionId());
        acctBillOrder.setItemId(itemId);
        acctBillOrder.setAcctFee(acctFee);
        acctBillContainer.addAcctBillOrderList(acctBillOrder);
        //轨迹
        BillTrackAcct billTrackAcct = new BillTrackAcct();
        billTrackAcct.setAcctId(acctShare.getAcctId());
        billTrackAcct.setCycleId(cycleBean.getCycleId());
        billTrackAcct.setStage(BillEnum.AcctTrackStage.PLAN_ACCT_FEE);
        billTrackAcct.setSourceId(acctShare.getPlanVersionId());
        billTrackAcct.setItemId(itemId);
        billTrackAcct.setFee(acctFee);
        acctBillContainer.addBillTrackAcctList(billTrackAcct);
      }

      long shareAcctFee= Math.round((double) acctFee/ (double) newDeviceBillOrderList.size());
      //重置设备平摊的账户费，设备的订户费
      long lastFee=acctFee;
      for (int a=0;a<newDeviceBillOrderList.size();a++) {
        DeviceBillOrder deviceBillOrder = newDeviceBillOrderList.get(a);
        deviceBillOrder.setFee(singleCharge);
        deviceBillOrder.setLevelPriority(levelPriority);
        deviceBillOrder.setOrderNum(singleOrder);
        if(a!=newDeviceBillOrderList.size()-1){
          deviceBillOrder.setAcctFee(shareAcctFee);
          lastFee-=shareAcctFee;
        }else {
          deviceBillOrder.setAcctFee(lastFee);
        }
        acctBillContainer.addFixShareDeviceBillOrderList(deviceBillOrder);
      }
      //额度处理
      List<RateBill> rateBillList = paramContainer.getRateBill(acctShare.getPlanVersionId());
      for(RateBill rateBill:rateBillList){
        if( !rateBill.isShare()  ){
            continue;
        }
        if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE) {
          ResIncludePool resIncludePool = new ResIncludePool();

          resIncludePool.setAcctId(acctShare.getAcctId());
          resIncludePool.setPoolId(acctShare.getPoolId());
          resIncludePool.setCycleId(cycleBean.getCycleId());
          resIncludePool.setPlanId(acctShare.getPlanId());
          resIncludePool.setPlanVersionId(rateBill.getPlanVersionId());
          resIncludePool.setZoneId(rateBill.getZoneId());
          resIncludePool.setGroupId(rateBill.getBillingGroupId());
          resIncludePool.setBillId(rateBill.getBillId());
          resIncludePool.setTotalValue(rateBill.getValue());
          resIncludePool.setCurrValue(rateBill.getValue());
          resIncludePool.setStartTime(acctShare.getStartTime());
          resIncludePool.setEndTime(acctShare.getEndTime());

          acctBillContainer.addResIncludePoolList(resIncludePool);
        } else {
          ResIncludeShare resIncludeShare = new ResIncludeShare();

          resIncludeShare.setAcctId(acctShare.getAcctId());
          resIncludeShare.setCycleId(cycleBean.getCycleId());
          resIncludeShare.setPlanId(acctShare.getPlanId());
          resIncludeShare.setPlanVersionId(rateBill.getPlanVersionId());
          resIncludeShare.setZoneId(rateBill.getZoneId());
          resIncludeShare.setGroupId(rateBill.getBillingGroupId());
          resIncludeShare.setBillId(rateBill.getBillId());
          double tValue = (double)rateBill.getValue() * (double)ratio[0] / (double)ratio[1];
          long value = (long) Math.ceil(tValue) ;
          resIncludeShare.setValue(value);
          resIncludeShare.setStartTime(acctShare.getStartTime());
          resIncludeShare.setEndTime(acctShare.getEndTime());

          acctBillContainer.addResIncludeFixShare(resIncludeShare);
        }
      }

    }

  }


  /**
   * 折算比例计算
   */
  private static long[] calculateRatio(final int planId,
                                       final CycleBean cycleBean,
                                       final List<DeviceRatePlanBean> deviceRatePlanBeanList,
                                       final AcctBillingGeneralBean acctBillingGeneralBean) throws Exception{

    DeviceRatePlanBean firstRecord=null;
    for(DeviceRatePlanBean deviceRatePlanBean: deviceRatePlanBeanList){
      if(deviceRatePlanBean.getPlanId() == planId){
        if(firstRecord == null){
          firstRecord = deviceRatePlanBean;
        }
        if(firstRecord.getStartTime().getTime()<=cycleBean.getCycStartTime().getTime()){
          break;
        }
        if(firstRecord.getStartTime().getTime()>deviceRatePlanBean.getStartTime().getTime()){
          firstRecord = deviceRatePlanBean;
        }
      }
    }
    long[] ratio ={0, 1};
    if(firstRecord != null){
      String modTag = InfoEnum.DisountMode.NONE;
      if (firstRecord.getStartTime().getTime() > cycleBean.getCycStartTime().getTime()) {
        if (InfoEnum.ActiveFlag.RENEWAL.equals(firstRecord.getActiveFlag())) {
          modTag = acctBillingGeneralBean.getRenewalProration();
        } else if (InfoEnum.ActiveFlag.ACTIVATION.equals(firstRecord.getActiveFlag())) {
          modTag = acctBillingGeneralBean.getActivationProration();
        }
      }
      ratio = calculateRatiobyMod(cycleBean, firstRecord.getStartTime(), modTag);
    }
    return ratio;
  }

  /**
   * 追加资费费用收取
   * @param paramContainer 参数
   * @param acctInfoContainer 资料
   * @param acctBillContainer 账单
   */
  private static void dealAddPlanOrderFee(final ParamContainer paramContainer,
                                          final AcctInfoContainer acctInfoContainer,
                                          AcctBillContainer acctBillContainer) throws Exception{

    if (acctInfoContainer.getAppendFeepolicyBeanList() == null || acctInfoContainer.getAppendFeepolicyBeanList().isEmpty()){
      return;
    }
    acctBillContainer.setOrderNo(0);

    CycleBean cycleBean = paramContainer.getCycleBean();
    for (AppendFeepolicyBean appendFeepolicyBean : acctInfoContainer.getAppendFeepolicyBeanList()) {

      int basePlanId=paramContainer.getPlanId(appendFeepolicyBean.getPlanVersionId());
      RatePlanBean ratePlanBean = paramContainer.getRatePlan(basePlanId);
      if(ratePlanBean == null){
        continue;
      }

      if (ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE &&
          ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE){
        continue;
      }

      RateOrderFee rateOrderFee = paramContainer.getRateOrderFee(appendFeepolicyBean.getAppendPlanVersionId());
      if(rateOrderFee == null){
        continue;
      }
      int planId = paramContainer.getPlanId(appendFeepolicyBean.getAppendPlanVersionId());
      int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_DEVICE_PLAN, rateOrderFee.getPaymentType());

      int zoneId = 0;
      long addValues = 0;
      //额度处理
      List<RateBill> rateBillList = paramContainer.getRateBill(appendFeepolicyBean.getAppendPlanVersionId());
      if(rateBillList == null){
        continue;
      }
      for (RateBill rateBill : rateBillList) {
        if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {
          acctBillContainer.addResIncludeFixShareValue(appendFeepolicyBean.getPlanVersionId(),rateBill);
        } else if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE) {
          acctBillContainer.addResIncludeAgileShareValue(appendFeepolicyBean.getPlanVersionId(),rateBill);
        }
        if (rateBill.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
          zoneId = rateBill.getZoneId();
          addValues += rateBill.getValue();
        }
      }

      AcctBillAdd acctBillAdd = new AcctBillAdd();

      acctBillAdd.setAcctId(appendFeepolicyBean.getAcctId());
      acctBillAdd.setCycleId(cycleBean.getCycleId());
      acctBillAdd.setPlanId(planId);
      acctBillAdd.setPlanVersionId(appendFeepolicyBean.getAppendPlanVersionId());
      acctBillAdd.setZoneId(zoneId);
      acctBillAdd.setAddValues(addValues);
      acctBillAdd.setAddFee(rateOrderFee.getSubscriberCharge());
      acctBillAdd.setBasePlanId(basePlanId);
      acctBillAdd.setOrderTime(appendFeepolicyBean.getStartTime());

      acctBillContainer.addAcctBillAddList(acctBillAdd);

      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(appendFeepolicyBean.getAcctId());
      billTrackAcct.setCycleId(cycleBean.getCycleId());
      billTrackAcct.setStage(BillEnum.AcctTrackStage.APPEND_PLAN);
      billTrackAcct.setSourceId(appendFeepolicyBean.getAppendPlanVersionId());
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(rateOrderFee.getSubscriberCharge());

      acctBillContainer.addBillTrackAcctList(billTrackAcct);
    }
  }


  /**
   * 账户级用量核减
   * @param paramContainer    参数
   * @param acctInfoContainer 资料
   * @param acctBillContainer 账单
   */
  private static void dealAcctUsageFee(final ParamContainer paramContainer,
                                       final AcctInfoContainer acctInfoContainer,
                                       AcctBillContainer acctBillContainer) throws Exception{

    acctBillContainer.setOrderNo(0);
    CycleBean cycleBean = paramContainer.getCycleBean();
    if (!acctBillContainer.getUsedAddShareList().isEmpty()) {
      for (UsedAddShare usedAddShare : acctBillContainer.getUsedAddShareList()) {
        RateBill rateBill = paramContainer.getRateBill(usedAddShare.getPlanVersionId(), usedAddShare.getBillId());
        if (rateBill == null ) {
          continue;
        }
        int planId = paramContainer.getPlanId(usedAddShare.getPlanVersionId());
        RatePlanBean ratePlanBean = paramContainer.getRatePlan(planId);

        ResIncludeShare resIncludeShare = acctBillContainer.getResIncludeShare(usedAddShare.getAcctId(),
                                                                                usedAddShare.getPlanVersionId(),
                                                                                usedAddShare.getBillId(),
                                                                                ratePlanBean.getPlanType());
        //用于批价的量
        long upperValue = rateBill.isPrice() ? getUpperValue(resIncludeShare.getValue(), usedAddShare.getCurrValue()):0;
        usedAddShare.setUpperValue(upperValue);
        //核减信息
        ResUsedShareTotal resUsedShareTotal = new ResUsedShareTotal();

        resUsedShareTotal.setAcctId(usedAddShare.getAcctId());
        resUsedShareTotal.setCycleId(cycleBean.getCycleId());
        resUsedShareTotal.setPlanVersionId(usedAddShare.getPlanVersionId());
        resUsedShareTotal.setZoneId(resIncludeShare.getZoneId());
        resUsedShareTotal.setGroupId(resIncludeShare.getGroupId());
        resUsedShareTotal.setBillId(usedAddShare.getBillId());
        resUsedShareTotal.setCurrValue(usedAddShare.getCurrValue() - upperValue);
        resUsedShareTotal.setStartTime(resIncludeShare.getStartTime());
        resUsedShareTotal.setEndTime(resIncludeShare.getEndTime());

        acctBillContainer.addResUsedShareTotalList(resUsedShareTotal);

        //批价
        dealAcctUsageFee(paramContainer, rateBill, usedAddShare,
                upperValue, acctBillContainer.getAcctBillUsageList(), acctBillContainer.getBillTrackAcctList());
      }
    }
    if (!acctBillContainer.getUsedAddPoolTotalList().isEmpty()) {
      for (UsedAddPoolTotal usedAddPoolTotal : acctBillContainer.getUsedAddPoolTotalList()) {

        SharePoolBean sharePoolBean = acctInfoContainer.getSharePool(usedAddPoolTotal.getPoolId());
        if (sharePoolBean == null) {
          continue;
        }
        RateBill rateBill = paramContainer.getRateBill(sharePoolBean.getPlanVersionId(), usedAddPoolTotal.getBillId());
        if (rateBill == null) continue;

        ResIncludePool resIncludePool = acctBillContainer.getResIncldePool(usedAddPoolTotal.getAcctId(),
                                                                          usedAddPoolTotal.getPoolId(),
                                                                          usedAddPoolTotal.getBillId());
        //用量核减
        long currInclude = resIncludePool.getTotalValue() > usedAddPoolTotal.getLastValue() ? resIncludePool.getTotalValue()-usedAddPoolTotal.getLastValue():0L;
        resIncludePool.setCurrValue(currInclude);
        long upperValue = rateBill.isPrice() ? getUpperValue(currInclude,usedAddPoolTotal.getCurrValue()):0;
        usedAddPoolTotal.setUpperValue(upperValue);

        ResUsedPoolTotal resUsedPoolTotal = new ResUsedPoolTotal();

        resUsedPoolTotal.setAcctId(usedAddPoolTotal.getAcctId());
        resUsedPoolTotal.setPoolId(usedAddPoolTotal.getPoolId());
        resUsedPoolTotal.setCycleId(cycleBean.getCycleId());
        resUsedPoolTotal.setPlanVersionId(rateBill.getPlanVersionId());
        resUsedPoolTotal.setZoneId(rateBill.getZoneId());
        resUsedPoolTotal.setGroupId(rateBill.getBillingGroupId());
        resUsedPoolTotal.setBillId(usedAddPoolTotal.getBillId());
        resUsedPoolTotal.setCurrValue(usedAddPoolTotal.getCurrValue() - upperValue);
        resUsedPoolTotal.setStartTime(resIncludePool.getStartTime());
        resUsedPoolTotal.setEndTime(resIncludePool.getEndTime());

        acctBillContainer.addResUsedPoolTotalList(resUsedPoolTotal);
        //批价
        dealAcctUsageFee(paramContainer,rateBill, usedAddPoolTotal,
                upperValue, acctBillContainer.getAcctBillUsageList(), acctBillContainer.getBillTrackAcctList());
      }
    }
  }


  /**
   * 资费组折扣or总监折扣(只针对订户费)
   * @param paramContainer     参数
   * @param acctInfoContainer  资料
   * @param acctBillContainer  账单
   */
  private static void dealGroupDiscount(final ParamContainer paramContainer,
                                        final AcctInfoContainer acctInfoContainer,
                                        AcctBillContainer acctBillContainer) throws Exception{

    acctBillContainer.setOrderNo(0);
    //资费组折扣定义
    //资费组成员
    //运营商折扣档次表--参数
    //运营商折扣档次详情--参数
    List<AcctRateDiscountBean> acctRateDiscountBeanList = acctInfoContainer.getAcctRateDiscountBeanList();
    if (acctRateDiscountBeanList == null || acctRateDiscountBeanList.isEmpty()) {
      return;
    }
    for (AcctRateDiscountBean tRecord : acctRateDiscountBeanList) {
      Set<Integer> planIdSet = InfoUtil.getAcctRateDiscountMember(tRecord.getRateGroup(),
          acctInfoContainer.getAcctRateDiscountMemberBeanList());
      double gradeDiscount = 0; //分档折扣
      double majorDiscount = 0; //总监折扣

      if (tRecord.getGradeDiscountId() > 0) {
        double deviceNum = BillUtil.getOrderNum(planIdSet, acctBillContainer.getDeviceBillOrderList());
        DiscontGradeDetailBean discontGradeDetailBean = paramContainer.getDiscontGradeDetailBean(tRecord.getGradeDiscountId(), deviceNum);
        if (discontGradeDetailBean != null) {
          gradeDiscount = discontGradeDetailBean.getDiscount()/100;
        }
      }
      if (tRecord.getChiefDiscountRate() > 0) {
        majorDiscount = tRecord.getChiefDiscountRate();
      }
      double discount = 1 - (1-gradeDiscount) * (1-majorDiscount) ;
      //照抄原有的收费记录
      if (discount - 0d > 10E-8) {
        //订购费
        for(BillTrackAcct billTrackAcct:acctBillContainer.getBillTrackAcctList()){
          if(billTrackAcct.getStage() != BillEnum.AcctTrackStage.DEVICE_SUM_ORDER &&
             billTrackAcct.getStage() != BillEnum.AcctTrackStage.PLAN_ACCT_FEE){
            continue;
          }
          //对于固定共享的，额外订户费不打折
          if(billTrackAcct.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_ORDER ){
            RatePlanBean ratePlanBean = paramContainer.getRatePlan((int)billTrackAcct.getSourceId());
            if(ratePlanBean==null)
              continue;
            if(ratePlanBean.getPlanType()==ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE||
               ratePlanBean.getPlanType()==ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
              continue;
            }
          }
          //匹配账目
          if(!paramContainer.isOrderItem(billTrackAcct.getItemId())){
            continue;
          }
          int planId = (int) billTrackAcct.getSourceId();
          if (planIdSet.contains(planId)) {
            long discountFee = Math.round((-1) * discount * billTrackAcct.getFee());
            RateGroupDiscount rateGroupDiscount = new RateGroupDiscount();

            rateGroupDiscount.setAcctId(billTrackAcct.getAcctId());
            rateGroupDiscount.setCycleId(billTrackAcct.getCycleId());
            rateGroupDiscount.setRateGroup(tRecord.getRateGroup());
            rateGroupDiscount.setPlanId(planId);
//            rateGroupDiscount.setPlanVersionId((int) billTrackAcct.getSourceId());
            rateGroupDiscount.setItemId(billTrackAcct.getItemId());
            rateGroupDiscount.setDisountPercent(discount);
            rateGroupDiscount.setOrignalCharge(billTrackAcct.getFee());
            rateGroupDiscount.setDisountCharge(-discountFee);

            acctBillContainer.addRateGroupDiscountList(rateGroupDiscount);
          }
        }
      }
    }

    List<BillTrackAcct> billTrackAcctList = BillUtil.getBillTrackAcctList(acctBillContainer.getRateGroupDiscountList());
    if(!billTrackAcctList.isEmpty()){
      billTrackAcctList.forEach(acctBillContainer::addBillTrackAcctList);
    }
  }

  /**
   * 订单费用
   * @param paramContainer    参数
   * @param acctInfoContainer 资料
   * @param acctBillContainer 账单
   */
  private static void dealAcctOrderFee(final ParamContainer paramContainer,
                                       final AcctInfoContainer acctInfoContainer,
                                       AcctBillContainer acctBillContainer) throws Exception{

    acctBillContainer.setOrderNo(0);
    List<AcctOrderBean> acctOrderBeanList = acctInfoContainer.getAcctOrderBeanList();
    if (acctOrderBeanList == null || acctOrderBeanList.isEmpty()) {
      return;
    }

    CycleBean cycleBean = paramContainer.getCycleBean();
    int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_ORDER, 0);
    List<AcctBillOther> acctBillOtherList = acctBillContainer.getAcctBillOtherList();

    for (AcctOrderBean acctOrderBean : acctOrderBeanList) {
      int orderNum = InfoDao.getOrderNum(cycleBean, acctOrderBean.getAcctId(), acctOrderBean.getOrderId());
      long price = paramContainer.getOpnPrice(acctOrderBean.getOpnCode());
      long tFee = orderNum * price;

      if(tFee > 0){
        AcctBillOther acctBillOther = new AcctBillOther();
        acctBillOther.setAcctId(acctOrderBean.getAcctId());
        acctBillOther.setCycleId(cycleBean.getCycleId());
        acctBillOther.setChargeType(BillEnum.ChargeType.SIMCARD);
        acctBillOther.setDiscountTag(false);
        acctBillOther.setDescription("订单费用");
        acctBillOther.setOrderInsId(acctOrderBean.getOrderId());
        acctBillOther.setNumbers(orderNum);
        acctBillOther.setFee(tFee);
        acctBillOtherList.add(acctBillOther);

        BillTrackAcct billTrackAcct = new BillTrackAcct();

        billTrackAcct.setAcctId(acctOrderBean.getAcctId());
        billTrackAcct.setCycleId(cycleBean.getCycleId());
        billTrackAcct.setStage(BillEnum.AcctTrackStage.ACCT_ORDER);
        billTrackAcct.setSourceId(acctOrderBean.getOrderId());
        billTrackAcct.setItemId(itemId);
        billTrackAcct.setFee(tFee);

        acctBillContainer.addBillTrackAcctList(billTrackAcct);
      }
    }
  }

  /**
   * 一次性费用+月费
   */
  private static void dealAcctMonth(final ParamContainer paramContainer,
                                    final AcctInfoContainer acctInfoContainer,
                                    AcctBillContainer acctBillContainer) throws Exception{

    if(acctInfoContainer.getAcctMonthFeeBeanList() == null ||
        acctInfoContainer.getAcctMonthFeeBeanList().isEmpty())
      return;

    acctBillContainer.setOrderNo(0);

    CycleBean cycleBean = paramContainer.getCycleBean();
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();

    List<AcctBillOther> acctBillOtherList = acctBillContainer.getAcctBillOtherList();
    for(AcctMonthFeeBean acctMonthFeeBean :acctInfoContainer.getAcctMonthFeeBeanList()){
      if(acctMonthFeeBean.getFee() == 0 || !acctMonthFeeBean.isBillAble()){
        continue;
      }
      //一次性月费，判断首月生效
      if(acctMonthFeeBean.getFeeType() == 1 &&
         (acctMonthFeeBean.getStartTime().getTime() > cycleBean.getCycEndTime().getTime() ||
          acctMonthFeeBean.getStartTime().getTime() < cycleBean.getCycStartTime().getTime()) ){
        continue;
      }

      int chargeType = paramContainer.parseFeeCode(acctMonthFeeBean.getFeeCode());
      int itemId = paramContainer.getSumbillFix(chargeType,0);
      //其它账单
      AcctBillOther acctBillOther = new AcctBillOther();
      acctBillOther.setAcctId(acctId);
      acctBillOther.setCycleId(cycleBean.getCycleId());
      acctBillOther.setChargeType(chargeType);
      acctBillOther.setDiscountTag(false);
      acctBillOther.setDescription(acctMonthFeeBean.getRemark());
      acctBillOther.setOrderInsId(acctMonthFeeBean.getOperateId());
      acctBillOther.setNumbers(1);
      acctBillOther.setFee(acctMonthFeeBean.getFee());

      acctBillOtherList.add(acctBillOther);
      //收费轨迹
      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(acctId);
      billTrackAcct.setCycleId(cycleBean.getCycleId());
      billTrackAcct.setStage(BillEnum.AcctTrackStage.ACCT_OTHER);
      billTrackAcct.setSourceId(acctMonthFeeBean.getOperateId());
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(acctMonthFeeBean.getFee());

      acctBillContainer.addBillTrackAcctList(billTrackAcct);
    }
  }

  /**
   * 账前调账
   */
  private static void dealAcctAdjustBefore(final ParamContainer paramContainer,
                                           final AcctInfoContainer acctInfoContainer,
                                           AcctBillContainer acctBillContainer) throws Exception{

    if(acctInfoContainer.getAcctAdjustBeforeBeanList() == null ||
       acctInfoContainer.getAcctAdjustBeforeBeanList().isEmpty())
      return;

    acctBillContainer.setOrderNo(0);

    CycleBean cycleBean = paramContainer.getCycleBean();
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();
    int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_ADJUST_BEFORE, 0);
    long fee=0L;
    List<AdjustBill> adjustBillList= acctBillContainer.getAdjustBillList();
    for(AcctAdjustBeforeBean acctAdjustBeforeBean:acctInfoContainer.getAcctAdjustBeforeBeanList()){

      int adjustType = acctAdjustBeforeBean.getFee() > 0 ? InfoEnum.AdjustType.ADD:InfoEnum.AdjustType.SUBTRACT;
      fee += acctAdjustBeforeBean.getFee();

      AdjustBill adjustBill = new AdjustBill();
      adjustBill.setAcctId(acctId);
      adjustBill.setCycleId(cycleBean.getCycleId());
      adjustBill.setType(InfoEnum.AdjustOperateType.ADJUST_BEFORE);
      adjustBill.setAdjustType(adjustType);
      adjustBill.setAdjustFee( Math.abs(acctAdjustBeforeBean.getFee()));
      adjustBill.setAdjustTime(cycleBean.getCycEndTime());
      adjustBill.setDescription(acctAdjustBeforeBean.getRemark());
      adjustBillList.add(adjustBill);
    }

    if(fee != 0){
      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(acctId);
      billTrackAcct.setCycleId(cycleBean.getCycleId());
      billTrackAcct.setStage(BillEnum.AcctTrackStage.ADJUST_BEFORE);
      billTrackAcct.setSourceId(0);
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(fee);

      acctBillContainer.addBillTrackAcctList(billTrackAcct);
    }

  }

  /**
   * 账户折扣
   */
  private static void dealAcctValumeDiscount(final ParamContainer paramContainer,
                                             final AcctInfoContainer acctInfoContainer,
                                             AcctBillContainer acctBillContainer) throws Exception{

    AcctValumeDiscountBean acctValumeDiscountBean = acctInfoContainer.getAcctValumeDiscountBean();
    List<AcctDiscountBean> acctDiscountBeanList = acctInfoContainer.getAcctDiscountBeanList();

    if (acctValumeDiscountBean == null || acctDiscountBeanList == null) {
      return;
    }
    if (acctDiscountBeanList.isEmpty())
      return;

    acctBillContainer.setOrderNo(0);
    CycleBean cycleBean = paramContainer.getCycleBean();

    //档次划分依据
    long gradeSum = 0L;
    if(acctValumeDiscountBean.getGradeBaseOn() == 1){
      //订户数
      if(!acctBillContainer.getDeviceBillOrderList().isEmpty()){
        double deviceNum = acctBillContainer.getDeviceBillOrderList().stream()
                .filter(t -> t.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE ||
                        t.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
                        t.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE||
                        t.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE||
                        t.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE||
                        t.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE)
                .mapToDouble(DeviceBillOrder::getOrderNum).sum();
        gradeSum = Math.round(deviceNum);
      }
    }else{
      //总收入
      if(!acctBillContainer.getBillTrackAcctList().isEmpty()){
        gradeSum = acctBillContainer.getBillTrackAcctList().stream().mapToLong(BillTrackAcct::getFee).sum();
      }
    }

    int discount = 0;//百分比
    for (AcctDiscountBean acctDiscountBean : acctDiscountBeanList) {
      if (acctDiscountBean.getStartTime().getTime() < cycleBean.getCycEndTime().getTime() &&
          acctDiscountBean.getEndTime().getTime() >= cycleBean.getCycEndTime().getTime() &&
          acctDiscountBean.getDeviceLowerLimit() <= gradeSum &&
          acctDiscountBean.getDeviceUpperLimit() >= gradeSum) {
        discount = acctDiscountBean.getDiscount();
        break;
      }
    }

    acctBillContainer.setDiscount(discount);

    List<BillAcct> billAcctList = BillUtil.getBillAcctList(acctBillContainer.getBillTrackAcctList());
    for (BillAcct billAcct : billAcctList) {
      int chargeType = paramContainer.getChargeType(billAcct.getItemId());

      boolean isDisount = false;
      switch (chargeType) {
        case ParamEnum.ChargeType.CHARGE_TYPE_USAGE:
          int bizType = paramContainer.getBizType(billAcct.getItemId());
          if ((bizType == ParamEnum.BizType.BIZ_TYPE_DATA && acctValumeDiscountBean.isDataEffectFlag()) ||
              (bizType == ParamEnum.BizType.BIZ_TYPE_SMS && acctValumeDiscountBean.isSmsEffectFlag()) ||
              (bizType == ParamEnum.BizType.BIZ_TYPE_VOICE && acctValumeDiscountBean.isVoiceEffectFlag())) {
            isDisount = true;
          }
          break;
        case ParamEnum.ChargeType.CHARGE_TYPE_DEVICE_PLAN:
        case ParamEnum.ChargeType.CHARGE_TYPE_ACCT_PLAN:
          if (acctValumeDiscountBean.isOrderEffectFlag())
            isDisount = true;
          break;
        case ParamEnum.ChargeType.CHARGE_TYPE_ACTIVATION:
          if (acctValumeDiscountBean.isActiveEffectFlag())
            isDisount = true;
          break;
        case ParamEnum.ChargeType.CHARGE_TYPE_ORDER:
        case ParamEnum.ChargeType.CHARGE_TYPE_ACCT_SET:
        case ParamEnum.ChargeType.CHARGE_TYPE_VPN_SET:
        case ParamEnum.ChargeType.CHARGE_TYPE_DEFINE_VPN:
        case ParamEnum.ChargeType.CHARGE_TYPE_DEFINE_BRAND:
        case ParamEnum.ChargeType.CHARGE_TYPE_PERFORMANCE_ANALYSIS:
        case ParamEnum.ChargeType.CHARGE_TYPE_VPN_MONTH:
        case ParamEnum.ChargeType.CHARGE_TYPE_PREMIUM_SUPPORT:
          if (acctValumeDiscountBean.isOtherEffectFlag())
            isDisount = true;
          break;
        default:
          isDisount = false;
          break;
      }

      if (isDisount) {
        long fee = billAcct.getFee() * discount / 100;
        if(fee>0){
          BillTrackAcct billTrackAcct = new BillTrackAcct();

          billTrackAcct.setAcctId(acctValumeDiscountBean.getAcctId());
          billTrackAcct.setCycleId(cycleBean.getCycleId());
          billTrackAcct.setStage(BillEnum.AcctTrackStage.ACCT_DISCOUNT);
          billTrackAcct.setSourceId(acctValumeDiscountBean.getAcctId());
          billTrackAcct.setItemId(billAcct.getItemId());
          billTrackAcct.setFee(-fee);
          acctBillContainer.addBillTrackAcctList(billTrackAcct);

          AcctBillDiscount acctBillDiscount = new AcctBillDiscount();

          acctBillDiscount.setAcctId(acctValumeDiscountBean.getAcctId());
          acctBillDiscount.setCycleId(cycleBean.getCycleId());
          acctBillDiscount.setItemId(billAcct.getItemId());
          acctBillDiscount.setDisountPercent(discount);
          acctBillDiscount.setOrignalCharge(billAcct.getFee());
          acctBillDiscount.setDisountCharge(fee);
          acctBillContainer.addAcctBillDiscountList(acctBillDiscount);
        }

      }
    }
  }

  /**
   * 平台短信的费用
   */
  private static void dealPlatSms(final ParamContainer paramContainer,
                                  final AcctInfoContainer acctInfoContainer,
                                  AcctBillContainer acctBillContainer) throws Exception{

    if (acctInfoContainer.getPlatSmsValue() == 0) {
      return;
    }

    List<AcctSmsDiscount> acctSmsDiscountList = acctInfoContainer.getAcctSmsDiscountList();
    List<AcctDiscountGrade> acctDiscountGradeList = acctInfoContainer.getAcctDiscountGradeList();
    if (acctSmsDiscountList == null || acctSmsDiscountList.isEmpty()) {
      return;
    }
    if (acctDiscountGradeList == null || acctDiscountGradeList.isEmpty()) {
      return;
    }
    long smsFee = 0;
    for (AcctDiscountGrade acctDiscountGrade : acctDiscountGradeList) {
      if (acctDiscountGrade.getDeviceUp() >= acctInfoContainer.getPlatSmsValue() &&
          acctDiscountGrade.getDeviceLower() <= acctInfoContainer.getPlatSmsValue()) {
        if (acctSmsDiscountList.stream().anyMatch(t -> t.getGrade() == acctDiscountGrade.getGrade() && t.isOpen()) ) {
          smsFee = acctDiscountGrade.getSmsFee();
          acctBillContainer.setPlatSmsLevel(acctDiscountGrade.getGrade());
          break;
        }
      }
    }
    if (smsFee > 0) {
      acctBillContainer.setOrderNo(0);

      int cycleId = paramContainer.getCycleBean().getCycleId();
      long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();

      int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_PLATSMS, 0);
      long fee = smsFee*acctInfoContainer.getPlatSmsValue();
      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(acctId);
      billTrackAcct.setCycleId(cycleId);
      billTrackAcct.setStage(BillEnum.AcctTrackStage.PLAT_SMS);
      billTrackAcct.setSourceId(acctId);
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(fee);

      acctBillContainer.addBillTrackAcctList(billTrackAcct);
    }
  }

  /**
   * 账户承诺
   */
  private static void dealAcctPromise(final ParamContainer paramContainer,
                                      final AcctInfoContainer acctInfoContainer,
                                      AcctBillContainer acctBillContainer) throws Exception{

    acctBillContainer.setOrderNo(0);

    if (acctInfoContainer.getAcctPromiseBean() != null) {
      CycleBean cycleBean = paramContainer.getCycleBean();
      AcctPromiseBean acctPromiseBean = acctInfoContainer.getAcctPromiseBean();
      long promiseFee = acctPromiseBean.getMinimumActivationTerm();
      int deviceNum = acctInfoContainer.getDeviceBeanList().size(); //计费设备数量

      //最低订户数
      if (acctPromiseBean.getMiniSubs() > deviceNum) {
        long fee = (acctPromiseBean.getMiniSubs() - deviceNum) * acctPromiseBean.getChargePerSub();
        int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_MINISUBS, 0);

        BillTrackAcct billTrackAcct = new BillTrackAcct();

        billTrackAcct.setAcctId(acctPromiseBean.getAcctId());
        billTrackAcct.setCycleId(cycleBean.getCycleId());
        billTrackAcct.setStage(BillEnum.AcctTrackStage.ACCT_PROMISE);
        billTrackAcct.setSourceId(acctPromiseBean.getAcctId());
        billTrackAcct.setItemId(itemId);
        billTrackAcct.setFee(fee);

        acctBillContainer.addBillTrackAcctList(billTrackAcct);
      }
      //最低消费
      long factFee = acctBillContainer.getSumFee();
      if (factFee < promiseFee) {
        int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_LOWERLIMIT, 0);
        BillTrackAcct billTrackAcct = new BillTrackAcct();

        billTrackAcct.setAcctId(acctPromiseBean.getAcctId());
        billTrackAcct.setCycleId(cycleBean.getCycleId());
        billTrackAcct.setStage(BillEnum.AcctTrackStage.ACCT_PROMISE);
        billTrackAcct.setSourceId(acctPromiseBean.getAcctId());
        billTrackAcct.setItemId(itemId);
        billTrackAcct.setFee(promiseFee - factFee);

        acctBillContainer.addBillTrackAcctList(billTrackAcct);
      }
    }
  }

  /**
   * 第三方计费
   */
  private static void dealThirdPartyPlan(final ParamContainer paramContainer,
                                         final AcctInfoContainer acctInfoContainer,
                                         AcctBillContainer acctBillContainer) throws Exception{
    acctBillContainer.setOrderNo(0);

    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();
    List<Integer> planList = paramContainer.getThirdPartyPlanList(acctId);
    if (planList == null || planList.isEmpty()) {
      return;
    }
    List<UsedAddThirdParty> usedAddThirdPartyList = acctInfoContainer.getUsedAddThirdPartyList();
    if (usedAddThirdPartyList == null || usedAddThirdPartyList.isEmpty()) {
      return;
    }
    for (Integer planId : planList) {
      ThirdPartyVersion thirdPartyVersion = paramContainer.getThirdPartyVersion(planId);
      if (thirdPartyVersion == null) {
        continue;
      }
      List<ThirdPartyZone> thirdPartyZoneList = paramContainer.getThirdPartyZoneList(thirdPartyVersion.getPlanVersionId());
      List<ThirdPartyLevel> thirdPartyLevelList = paramContainer.getThirdPartyLevelList(thirdPartyVersion.getPlanVersionId());
      if (thirdPartyZoneList == null || thirdPartyZoneList.isEmpty() || thirdPartyLevelList == null || thirdPartyLevelList.isEmpty()) {
        continue;
      }
      Set<Integer> zoneSet = thirdPartyZoneList.stream().map(ThirdPartyZone::getZoneId).collect(Collectors.toSet());
      //获取累计值
      long value = 0;
      for (UsedAddThirdParty usedAddThirdParty : usedAddThirdPartyList) {
        if (zoneSet.contains(usedAddThirdParty.getZoneId()) ) {
          value += usedAddThirdParty.getCurrValue();
        }
      }
      //匹配参数,进行批价
      long fee = 0L;
//      long bulkAdjust = 0L;
      if (thirdPartyVersion.getLevelModel() == ParamEnum.LevelModel.MAX_LEVEL) {
        for (ThirdPartyLevel thirdPartyLevel : thirdPartyLevelList) {
          long minValue = getBitValue(thirdPartyLevel.getLevelUnit(), thirdPartyLevel.getMinAmount());
          long maxValue = getBitValue(thirdPartyLevel.getLevelUnit(), thirdPartyLevel.getMaxAmount());
          if (minValue <= value && maxValue >= value) {
            long[] result = calculateUsageFee(ParamEnum.BizType.BIZ_TYPE_DATA, thirdPartyLevel.getBaseUnit(), thirdPartyLevel.getBaseTimes(),false,
                thirdPartyLevel.getUnitRatio(),thirdPartyLevel.getPrecision(), value);
            fee = result[0];
//            bulkAdjust = result[1];
            break;
          }
        }
      } else if (thirdPartyVersion.getLevelModel() == ParamEnum.LevelModel.SUBSECTION) {
        Collections.sort(thirdPartyLevelList);
        boolean isEnd=false;
        for (ThirdPartyLevel thirdPartyLevel : thirdPartyLevelList) {
          long minValue = getBitValue(thirdPartyLevel.getLevelUnit(), thirdPartyLevel.getMinAmount());
          long maxValue = getBitValue(thirdPartyLevel.getLevelUnit(), thirdPartyLevel.getMaxAmount());
          long upperValue = 0L;
          if (value >= maxValue) {
            upperValue = maxValue - minValue;
          } else if (value <= maxValue && value > minValue) {
            upperValue = value - minValue;
            isEnd=true;
          }
          long[] result = calculateUsageFee(ParamEnum.BizType.BIZ_TYPE_DATA, thirdPartyLevel.getBaseUnit(), thirdPartyLevel.getBaseTimes(),false,
                                   thirdPartyLevel.getUnitRatio(),thirdPartyLevel.getPrecision(), upperValue);
          fee += result[0];
//          bulkAdjust += result[1];
          if(isEnd){
            break;
          }
        }
      }

      int itemId = paramContainer.getSumbillUse(ParamEnum.ChargeMode.CHARGE_MODE_MERGE, ParamEnum.Payment.PAYMENT_TYPE_MONTH,
                                                ParamEnum.BizType.BIZ_TYPE_DATA, false);
      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(acctId);
      billTrackAcct.setCycleId(paramContainer.getCycleBean().getCycleId());
      billTrackAcct.setStage(BillEnum.AcctTrackStage.THIRD_PARTY);
      billTrackAcct.setSourceId(planId);
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(fee);

      acctBillContainer.addBillTrackAcctList(billTrackAcct);
    }
  }

  /**
   * 多维度账单生成
   */
  private static void setMultiBill(final ParamContainer paramContainer,
                                   final AcctInfoContainer acctInfoContainer,
                                   AcctBillContainer acctBillContainer)
      throws Exception{

    resSetDeviceBillMonth(paramContainer,acctBillContainer);
//    setShareBillUsage(paramContainer,acctBillContainer);
    setPlanBill(paramContainer, acctInfoContainer, acctBillContainer);
    setPlanZoneBill(paramContainer,acctBillContainer);
    setAcctBillSum(paramContainer, acctInfoContainer, acctBillContainer);
    setAcctBill2Bss(paramContainer, acctInfoContainer, acctBillContainer);
    setSeqId(acctInfoContainer.getSeqId(),acctBillContainer);
  }

  /**
   * 重置设备账单中，月付固定资费计划的 订户费，账户费分摊
   */
  private static void resSetDeviceBillMonth(final ParamContainer paramContainer,
                                            AcctBillContainer acctBillContainer){

    //设备级的固定共享账单
    Map<Long, List<DeviceBillOrder>> deviceBillOrderMap =
        acctBillContainer.getFixShareDeviceBillOrderList().stream().collect(groupingBy(DeviceBillOrder::getDeviceId));
    if(deviceBillOrderMap == null || deviceBillOrderMap.isEmpty()){
      return;
    }
    List<DeviceBill> deviceBillList = acctBillContainer.getDeviceBillList();
    for(DeviceBill deviceBill:deviceBillList){
      //只处理月付固定资费计划
      RatePlanBean ratePlanBean = paramContainer.getRatePlan(deviceBill.getStandradPlanId());
      if(ratePlanBean == null)
        continue;
      if(ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE &&
         ratePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
        continue;
      }
      List<DeviceBillOrder> deviceBillOrderList = deviceBillOrderMap.get(deviceBill.getDeviceId());
      if(deviceBillOrderList==null || deviceBillOrderList.isEmpty()){
        continue;
      }
      DeviceBillOrder deviceBillOrder = deviceBillOrderList.stream().filter(t -> t.getPlanId()==deviceBill.getStandradPlanId()).findAny().orElse(null);
      if(deviceBillOrder!=null){
        deviceBill.setOrderCharge(deviceBillOrder.getFee());
        deviceBill.setShareCharge(deviceBillOrder.getAcctFee());
        acctBillContainer.addFixShareDeviceBillList(deviceBill);
      }
    }
  }

  /**
   * 资费计划账单生成
   */
  private static void setPlanBill(final ParamContainer paramContainer,
                                  final AcctInfoContainer acctInfoContainer,
                                  AcctBillContainer acctBillContainer) throws Exception{

    List<DeviceRatePlanBean> deviceRatePlanBeanList = acctInfoContainer.getDeviceRatePlanBeanList();
    if (deviceRatePlanBeanList == null || deviceRatePlanBeanList.isEmpty()) {
      return;
    }
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();
    int cycleId = paramContainer.getCycleBean().getCycleId();

    List<PlanBill> planBillList = new ArrayList<>();
    for (DeviceRatePlanBean tRecord : deviceRatePlanBeanList) {
      if (tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE &&
          tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE &&
          tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE &&
          tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE &&
          tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE &&
          tRecord.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
        continue;
      }
      if (acctInfoContainer.geteDeviceType(tRecord.getDeviceId()) == -1) {
        continue;
      }
      PlanBill planBill = BillUtil.getPlanBill(tRecord.getPlanVersionId(), planBillList);
      //设置参数化的属性
      if (planBill == null) {
        planBill = new PlanBill();

        int includeDataMode = 0;
        int includeSmsMode = 0;
        int includeVoiceMode = 0;

        RatePlanVersionBean ratePlanVersionBean = paramContainer.getRatePlanVersion(tRecord.getPlanId(), tRecord.getPlanVersionId());
        switch (tRecord.getPlanType()){
          case ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE:
          case ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE:
            includeDataMode = BillEnum.IncludeMode.MONTH_SHARE;
            includeSmsMode = (ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoSms())) ? BillEnum.IncludeMode.MONTH_SHARE : BillEnum.IncludeMode.MONTH;
            includeVoiceMode = (ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoVoice())) ? BillEnum.IncludeMode.MONTH_SHARE : BillEnum.IncludeMode.MONTH;
            break;
          case ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE:
          case ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE:
            includeDataMode = BillEnum.IncludeMode.MONTH_SHARE;
            includeSmsMode = (ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoSms())) ? BillEnum.IncludeMode.MONTH_SHARE : BillEnum.IncludeMode.PERPAY;
            includeVoiceMode = (ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoVoice())) ? BillEnum.IncludeMode.MONTH_SHARE : BillEnum.IncludeMode.PERPAY;
            break;
          case ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE:
            includeDataMode = BillEnum.IncludeMode.MONTH;
            includeSmsMode = BillEnum.IncludeMode.MONTH;
            includeVoiceMode = BillEnum.IncludeMode.MONTH;
            break;
          case ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE:
            includeDataMode = BillEnum.IncludeMode.PERPAY;
            includeSmsMode = BillEnum.IncludeMode.PERPAY;
            includeVoiceMode = BillEnum.IncludeMode.PERPAY;
            break;
          default:
            break;
        }
        planBill.setIncludeDataMode(includeDataMode);
        planBill.setIncludeSmsMode(includeSmsMode);
        planBill.setIncludeSmsMoMode(includeSmsMode);
        planBill.setIncludeSmsMtMode(includeSmsMode);
        planBill.setIncludeVoiceMode(includeVoiceMode);
        planBill.setIncludeVoiceMoMode(includeVoiceMode);
        planBill.setIncludeVoiceMtMode(includeVoiceMode);

        List<RateBill> rateBillList = paramContainer.getRateBill(tRecord.getPlanVersionId());
        Map<String,Long> valueMap = new HashMap<>();
        BillUtil.getRateBillInclude(rateBillList,valueMap);

        planBill.setAcctId(acctId);
        planBill.setCycleId(cycleId);
        planBill.setPlanId(tRecord.getPlanId());
        planBill.setPlanVersionId(tRecord.getPlanVersionId());
        planBill.setPlanType(tRecord.getPlanType());
        int paymentType = ParamUtil.getPaymentType(tRecord.getPlanType());
        planBill.setPaymentType(paymentType);
        planBill.setIncludeDataPrimaryZone(valueMap.getOrDefault("includeDataValue",0L));
        planBill.setIncludeSms(valueMap.getOrDefault("includeSmsValue",0L));
        planBill.setIncludeSmsMo(valueMap.getOrDefault("includeSmsMoValue",0L));
        planBill.setIncludeSmsMt(valueMap.getOrDefault("includeSmsMtValue",0L));
        planBill.setIncludeVoice(valueMap.getOrDefault("includeVoiceValue",0L));
        planBill.setIncludeVoiceMo(valueMap.getOrDefault("includeVoiceMoValue",0L));
        planBill.setIncludeVoiceMt(valueMap.getOrDefault("includeVoiceMtValue",0L));

        planBillList.add(planBill);
      }
    }

    for (PlanBill tRecord : planBillList) {
      List<DeviceBillOrder> deviceBillOrderList ;
      if (tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {
        deviceBillOrderList = acctBillContainer.getFixShareDeviceBillOrderList();
      } else {
        deviceBillOrderList = acctBillContainer.getDeviceBillOrderList();
      }
      //设置订购费信息
      double orders = 0;
      double prepayActivations = 0;
      double activeOrders = 0;
      double activationGracePeriodOrders = 0;
      double mininumActivationTermOrders = 0;
      long orderCharge = 0;
      long fixedPoolCharge = 0;
      int activeTier = 0;
      for (DeviceBillOrder deviceBillOrder : deviceBillOrderList) {
        if (tRecord.getPlanVersionId() == deviceBillOrder.getPlanVersionId()) {
          //设置资费等级
          if(activeTier == 0){
            activeTier = deviceBillOrder.getLevelPriority();
          }
          //订购费
          orderCharge += deviceBillOrder.getFee();
          //设置订购数量等属性
          orders += deviceBillOrder.getOrderNum();
          if ((tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE && deviceBillOrder.getFee()>0)       ||
              (tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE && deviceBillOrder.getAcctFee()>0) ||
              (tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE && deviceBillOrder.getFee()>0) ) {
            prepayActivations += deviceBillOrder.getOrderNum();//预付订购，只在第一个月计算数量
          }
          int rateType = acctInfoContainer.geteDeviceType(deviceBillOrder.getDeviceId());
          if (rateType == InfoEnum.RateType.NOMAL) {
            activeOrders += deviceBillOrder.getOrderNum();
          } else if (rateType == InfoEnum.RateType.BILL_ACTIVATION_GRACE_PERIOD) {
            activationGracePeriodOrders += deviceBillOrder.getOrderNum();
          } else if (rateType == InfoEnum.RateType.BILL_MININUM_ACTIVATION_TERM) {
            mininumActivationTermOrders += deviceBillOrder.getOrderNum();
          }
          tRecord.setActiveTier(deviceBillOrder.getLevelPriority());
        }
      }
      //固定共享费用(从收费轨迹中获取)
      if (tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          tRecord.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {

        if(!acctBillContainer.getAcctBillOrderList().isEmpty()){
          fixedPoolCharge = acctBillContainer.getAcctBillOrderList().stream()
              .filter(t -> t.getPlanId() == tRecord.getPlanId()).mapToLong(AcctBillOrder::getAcctFee).sum();
        }
      }

      tRecord.setOrders(orders);
      tRecord.setPrepayActivations(prepayActivations);
      tRecord.setActiveOrders(activeOrders);
      tRecord.setMininumActivationTermOrders(mininumActivationTermOrders);
      tRecord.setActivationGracePeriodOrders(activationGracePeriodOrders);
      tRecord.setOrderCharge(orderCharge);
      tRecord.setFixedPoolCharge(fixedPoolCharge);
      tRecord.setActiveTier(activeTier);
      //设置使用费
      Map<String,Long> valueMap= new HashMap<>();
      BillUtil.getBillUsage(acctBillContainer.getDeviceBillUsageList(), tRecord.getPlanVersionId(),valueMap);
      BillUtil.getBillUsage(acctBillContainer.getAcctBillUsageList(), tRecord.getPlanVersionId(), valueMap);

      tRecord.setDataCharge(valueMap.getOrDefault("dataCharge",0L));
      tRecord.setRemoteDataCharge(valueMap.getOrDefault("remoteDataCharge",0L));
      tRecord.setLocalDataCharge(valueMap.getOrDefault("localDataCharge",0L));
      tRecord.setSmsCharge(valueMap.getOrDefault("smsCharge",0L));
      tRecord.setVoiceCharge(valueMap.getOrDefault("voiceCharge",0L));

      tRecord.setTotalCharge(tRecord.getOrderCharge() + tRecord.getFixedPoolCharge() + tRecord.getDataCharge() + tRecord.getSmsCharge() + tRecord.getVoiceCharge());
      //累计量信息
      BillUtil.getAddValue(acctBillContainer.getUsedAddDeviceList(),tRecord.getPlanVersionId(),paramContainer,valueMap);

      tRecord.setDataValue(valueMap.getOrDefault("dataValue",0L));
      tRecord.setRemoteData(valueMap.getOrDefault("remoteData",0L));
      tRecord.setLocalData(valueMap.getOrDefault("localData",0L));
      tRecord.setSmsValue(valueMap.getOrDefault("smsValue",0L));
      tRecord.setSmsMoValue(valueMap.getOrDefault("smsMoValue",0L));
      tRecord.setSmsMtValue(valueMap.getOrDefault("smsMtValue",0L));
      tRecord.setVoiceValue(valueMap.getOrDefault("voiceValue",0L));
      tRecord.setVoiceMoValue(valueMap.getOrDefault("voiceMoValue",0L));
      tRecord.setVoiceMtValue(valueMap.getOrDefault("voiceMtValue",0L));
      //折扣组设置
      List<RateGroupDiscount> rateGroupDiscountList = acctBillContainer.getRateGroupDiscountList();
      for (RateGroupDiscount tRecord1 : rateGroupDiscountList) {
        if (tRecord1.getPlanVersionId() == tRecord.getPlanVersionId()) {
          tRecord.setRateGroup(tRecord1.getRateGroup());
          tRecord.setDiscountRate(tRecord1.getDisountPercent());
          tRecord.setDiscountAmount(tRecord1.getDisountCharge());
          break;
        }
      }
    }
    acctBillContainer.setPlanBillList(planBillList);
  }

  /**
   * 资费区域账单
   */
  private static void setPlanZoneBill(ParamContainer paramContainer,
                                      AcctBillContainer acctBillContainer) {
    //非共享部分
    List<PlanZoneBill> planZoneBillList1 =
            BillUtil.getPlanZoneBillList(acctBillContainer.getDeviceBillDataList());
    //灵活共享池
    List<PlanZoneBill> planZoneBillList2 =
            BillUtil.getPlanZoneBillList(paramContainer,
                    acctBillContainer.getResIncludeAgileShareList(),
                    acctBillContainer.getUsedAddShareList(),
                    acctBillContainer.getAcctBillUsageList());
    //月付固定共享池
    List<PlanZoneBill> planZoneBillList3 =
            BillUtil.getPlanZoneBillList(paramContainer,
                    acctBillContainer.getResIncludeFixShareList(),
                    acctBillContainer.getUsedAddShareList(),
                    acctBillContainer.getAcctBillUsageList());
    //预付固定共享池
    List<PlanZoneBill> planZoneBillList4 =
            BillUtil.getPlanZoneBillList(paramContainer,
                                        acctBillContainer.getResIncludePoolList(),
                                        acctBillContainer.getUsedAddPoolTotalList(),
                                        acctBillContainer.getAcctBillUsageList());

    acctBillContainer.addPlanZoneBillList(planZoneBillList1);
    acctBillContainer.addPlanZoneBillList(planZoneBillList2);
    acctBillContainer.addPlanZoneBillList(planZoneBillList3);
    acctBillContainer.addPlanZoneBillList(planZoneBillList4);
  }

  /**
   * 账户汇总账单
   */
  private static void setAcctBillSum(final ParamContainer paramContainer,
                                     final AcctInfoContainer acctInfoContainer,
                                     AcctBillContainer acctBillContainer) throws Exception{

    AcctInfoBean acctInfoBean = acctInfoContainer.getAcctInfoBean();
    CycleBean cycleBean = paramContainer.getCycleBean();

    java.sql.Date lastPayDate = InfoUtil.addDays(cycleBean.getCycEndTime(),30);

    AcctBillSum acctBillSum = new AcctBillSum();

    acctBillSum.setAcctId(acctInfoBean.getAcctId());
    acctBillSum.setOperAcctId(acctInfoBean.getOperAcctId());
    acctBillSum.setCycleId(cycleBean.getCycleId());
    acctBillSum.setBillDate(cycleBean.getCycEndTime());
    acctBillSum.setLastPayDate(lastPayDate);
    String rateTag = acctInfoContainer.isRate() ? "1" : "0";
    acctBillSum.setRateTag(rateTag);
    acctBillSum.setPublishTag("0");
    //活跃用户数
    int rateDevices = 0;    //设备数
    int nomalDevices = 0;   //活跃订户
    Map<Long, Integer> deviceIdMap = acctInfoContainer.getDeviceIdMap();
    if (deviceIdMap != null && !deviceIdMap.isEmpty()) {
      rateDevices = deviceIdMap.size();
      for (Map.Entry<Long, Integer> entry:deviceIdMap.entrySet()){
           Long deviceId = entry.getKey();
        int rateType = deviceIdMap.get(deviceId);
        if (rateType == InfoEnum.RateType.NOMAL) {
          nomalDevices += 1;
        }
      }
    }
    acctBillSum.setRateDevices(rateDevices);
    acctBillSum.setNomalDevices(nomalDevices);

    long gprsValue = 0;     //流量
    long smsValue = 0;      //短信总量
    long voiceValue = 0;    //通话
    List<DeviceUsage> deviceUsageList = acctBillContainer.getDeviceUsageList();
    if (deviceUsageList != null && !deviceUsageList.isEmpty()) {
      for (DeviceUsage deviceUsage : deviceUsageList) {
        gprsValue += deviceUsage.getDataUsage();
        smsValue += deviceUsage.getSmsUsage();
        voiceValue += deviceUsage.getVoiceUsage();
      }
    }
    acctBillSum.setGprsValue(gprsValue);
    acctBillSum.setSmsValue(smsValue);
    acctBillSum.setVoiceValue(voiceValue);

    long eventGprsValue =0L;
    List<DeviceBillData> deviceBillDataList = acctBillContainer.getDeviceBillDataList();
    if(deviceBillDataList!=null && !deviceBillDataList.isEmpty()){
      eventGprsValue = deviceBillDataList.stream().filter(t -> t.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT )
                                                  .mapToLong(DeviceBillData::getGprsValue).sum();
    }
    acctBillSum.setEventGprsValue(eventGprsValue);

    Map<String,Long> valueMap=new HashMap<>();
    BillUtil.getCharge(acctBillContainer,valueMap);

    int discountRate = acctBillContainer.getDiscount();   //折扣率
    long orderCharge = valueMap.getOrDefault("orderCharge",0L);   //订购费
    long gprsCharge = valueMap.getOrDefault("gprsCharge",0L);    //流量费
    long smsCharge = valueMap.getOrDefault("smsCharge",0L);     //短信费
    long voiceCharge = valueMap.getOrDefault("voiceCharge",0L);   //通话费
    long avtivationCharge = valueMap.getOrDefault("avtivationCharge",0L); //激活费
    long otherCharge = valueMap.getOrDefault("otherCharge",0L);   //其它费
    long discountCharge = valueMap.getOrDefault("discountCharge",0L); //折扣金额
    long totalCharge = valueMap.getOrDefault("totalCharge",0L);    //总费用
    long standardCharge = valueMap.getOrDefault("standardCharge",0L); //标准超额
    long standardRoamCharge = valueMap.getOrDefault("standardRoamCharge",0L); //标准漫游
    int events = (valueMap.getOrDefault("events",0L)).intValue();             //事件数
    long eventCharge = valueMap.getOrDefault("eventCharge",0L); //事件费用
    long platSmsCharge = valueMap.getOrDefault("platSmsCharge",0L); //平台短信的费用
    long serviceCharge = valueMap.getOrDefault("serviceCharge",0L);

    acctBillSum.setServiceCharge(serviceCharge);
    acctBillSum.setDiscountRate(discountRate);
    acctBillSum.setOrderCharge(orderCharge);
    acctBillSum.setGprsCharge(gprsCharge);
    acctBillSum.setSmsCharge(smsCharge);
    acctBillSum.setVoiceCharge(voiceCharge);
    acctBillSum.setAvtivationCharge(avtivationCharge);
    acctBillSum.setOtherCharge(otherCharge);
    acctBillSum.setDiscountCharge(discountCharge);
    acctBillSum.setTotalCharge(totalCharge);
    acctBillSum.setStandardCharge(standardCharge);
    acctBillSum.setStandardRoamCharge(standardRoamCharge);
    acctBillSum.setEvents(events);
    acctBillSum.setEventCharge(eventCharge);
    acctBillSum.setCycleId(cycleBean.getCycleId());
    //平台下发短信数量
    acctBillSum.setPlatSmsValue(acctInfoContainer.getPlatSmsValue());
    //平台下发短信费用
    acctBillSum.setPlatSmsCharge(platSmsCharge);
    acctBillSum.setPlatSmsLevel(acctBillContainer.getPlatSmsLevel());

    acctBillContainer.setAcctBillSum(acctBillSum);
  }

  /**
   * 计算接口账单
   * @param paramContainer 参数
   * @param acctInfoContainer 资料
   * @param acctBillContainer 账单
   */
  private static void setAcctBill2Bss(final ParamContainer paramContainer,
                                      final AcctInfoContainer acctInfoContainer,
                                      AcctBillContainer acctBillContainer) throws Exception{

    List<BillTrackAcct> billTrackAcctList = acctBillContainer.getBillTrackAcctList();
    if (billTrackAcctList == null || billTrackAcctList.isEmpty()) {
      return;
    }

    AcctInfoBean acctInfoBean = acctInfoContainer.getAcctInfoBean();
    CycleBean cycleBean = paramContainer.getCycleBean();

    String cityCode = acctInfoBean.getCityCode();
    String provCode = acctInfoBean.getProvinceCode();
    String areaCode = "";
    long acctId = acctInfoBean.getAcctId();
    String operAcctId = acctInfoBean.getOperAcctId();
    int cycleId = cycleBean.getCycleId();
    int billStartDate = (int) (cycleBean.getsTime() / 1000000);
    int billEndDate = (int) (cycleBean.geteTime() / 1000000);

    List<AcctBill2Bss> acctBill2BssList = new ArrayList<>();
    for (BillTrackAcct tRecord : billTrackAcctList) {
      ItemBean itemBean = paramContainer.getItemBean(tRecord.getItemId());
      if (itemBean == null) {
        continue;
      }
      int billType = itemBean.getBillType();
      int headquarterItemId = itemBean.getHeadquarterItemId();
      int provItemId = itemBean.getProvItemId();

      AcctBill2Bss acctBill2Bss = BillUtil.getAcctBill2Bss(billType, headquarterItemId, provItemId, acctBill2BssList);
      if (acctBill2Bss == null) {
        acctBill2Bss = new AcctBill2Bss();

        acctBill2Bss.setCityCode(cityCode);
        acctBill2Bss.setProvCode(provCode);
        acctBill2Bss.setAreaCode(areaCode);
        acctBill2Bss.setOperAcctId(operAcctId);
        acctBill2Bss.setAcctId(acctId);
        acctBill2Bss.setCycleId(cycleId);
        acctBill2Bss.setBillType(billType);
        acctBill2Bss.setHeadquarterItemId(headquarterItemId);
        acctBill2Bss.setProvItemId(provItemId);
        acctBill2Bss.setBillStartDate(billStartDate);
        acctBill2Bss.setBillEndDate(billEndDate);

        acctBill2BssList.add(acctBill2Bss);
      }
      acctBill2Bss.setFee(acctBill2Bss.getFee() + tRecord.getFee());
      if (tRecord.getStage() == BillEnum.AcctTrackStage.ACCT_DISCOUNT ||
          tRecord.getStage() == BillEnum.AcctTrackStage.GROUP_DISCOUNT) {
        acctBill2Bss.setDiscountFee(acctBill2Bss.getDiscountFee() + tRecord.getFee());
      }

      if (tRecord.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_ACTIVIE ||
          tRecord.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_ORDER ||
          tRecord.getStage() == BillEnum.AcctTrackStage.DEVICE_SUM_USAGE ||
          tRecord.getStage() == BillEnum.AcctTrackStage.PLAN_ACCT_FEE ||
          tRecord.getStage() == BillEnum.AcctTrackStage.APPEND_PLAN ||
          tRecord.getStage() == BillEnum.AcctTrackStage.SHARE_USAGE ||
          tRecord.getStage() == BillEnum.AcctTrackStage.ACCT_ORDER ||
          tRecord.getStage() == BillEnum.AcctTrackStage.ADJUST_BEFORE ||
          tRecord.getStage() == BillEnum.AcctTrackStage.PLAT_SMS ||
          tRecord.getStage() == BillEnum.AcctTrackStage.THIRD_PARTY) {
        acctBill2Bss.setOriginalFee(acctBill2Bss.getOriginalFee() + tRecord.getFee());
      }
    }
    acctBillContainer.setAcctBill2BssList(acctBill2BssList);
  }


  private static void setSeqId(final long seqId,AcctBillContainer acctBillContainer) throws Exception{

    //多维度账单
    acctBillContainer.getPlanBillList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getPlanZoneBillList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getAcctBillAddList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getRateGroupDiscountList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getAcctBillOtherList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getAcctBillDiscountList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getAdjustBillList().forEach(t -> t.setSeqId(seqId));
    acctBillContainer.getAcctBillSum().setSeqId(seqId);
  }



}
