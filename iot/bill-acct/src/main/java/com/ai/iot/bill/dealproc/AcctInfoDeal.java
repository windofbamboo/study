package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.dao.AcctBillDao;
import com.ai.iot.bill.dao.InfoDao;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dealproc.container.AcctInfoContainer;
import com.ai.iot.bill.dealproc.container.AcctShare;
import com.ai.iot.bill.dealproc.container.DeviceInfoContainer;
import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.dealproc.util.InfoUtil;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.info.*;
import com.ai.iot.bill.entity.log.AcctLog;
import com.ai.iot.bill.entity.log.DeviceLog;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.param.RatePlanBean;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * 资料的处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctInfoDeal {

  private static final Logger logger = LoggerFactory.getLogger(AcctInfoDeal.class);

  private AcctInfoDeal() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 第一阶段账户资料获取
   */
  public static AcctInfoContainer acctDealFirst(final long dealId,
                                                final long acctId) {

    //参数初始化
    ParamContainer paramContainer = ParamMgr.getParamContainer(dealId);
    if(paramContainer==null){
      LogDao.updateAcctLog(dealId,acctId,ErrEnum.ParamErr.PARAM_INIT_ERR);
      return null;
    }
    //开始
    LogDao.insertAcctLog(dealId,acctId);

    //删除原有账单
//    CycleBean cycleBean = paramContainer.getCycleBean();
//    String month = String.valueOf(cycleBean.getCycleId()).substring(4, 6);
//    AcctBillDao.deleteAcctPdb(month, acctId);

    AcctInfoContainer acctInfoContainer=getAcctInfo(dealId,acctId);
    if(acctInfoContainer!=null){
      long seqId= SeqMgr.getSeqId("BILL_ID");
      acctInfoContainer.setSeqId(seqId);
    }
    return acctInfoContainer;
  }

  /**
   * 第一阶段账户资料获取
   */
  public static List<DeviceInfoContainer> acctDealSecond(AcctInfoContainer acctInfoContainer) {

    if(acctInfoContainer==null){
      return Collections.emptyList();
    }
    long dealId = acctInfoContainer.getDealId();
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();
    //数据转换
    List<DeviceInfoContainer> deviceInfoContainerList = InfoUtil.getDeviceInfo(acctInfoContainer);
    //结束
    setLogEnd(dealId,acctId, acctInfoContainer.getDeviceIdMap());
    return deviceInfoContainerList;
  }

  private static void setLogEnd(final long dealId,
                                final long acctId,
                                Map<Long, Integer> deviceIdMap) {

    List<DeviceLog> deviceLogList = new ArrayList<>();
    for (Long deviceId : deviceIdMap.keySet()) {
      DeviceLog deviceLog = new DeviceLog(dealId,acctId, deviceId, BillEnum.DealStage.ACCT_DEAL_END, ErrEnum.DealTag.SUCCESS);
      deviceLogList.add(deviceLog);
    }
    LogDao.updateAcctLog(dealId,acctId,ErrEnum.DEAL_SUCESS);
    LogDao.insertDeviceLog(deviceLogList);
  }

  /**
   * 账户基本资料获取，用于billDeal处理
   */
  static AcctInfoContainer getAcctInfo(final long dealId,
                                       final long acctId) {

    ParamContainer paramContainer = ParamMgr.getParamContainer(dealId);
    if(paramContainer==null){
      LogDao.updateAcctLog(dealId,acctId, ErrEnum.ParamErr.PARAM_INIT_ERR);
      return null;
    }
    CycleBean cycleBean = paramContainer.getCycleBean();
    AcctInfoContainer acctInfoContainer ;
    try {
      acctInfoContainer = InfoDao.getAcctInfo(cycleBean, acctId);
    } catch (Exception e) {
      logger.error("getAcctInfo err.{}", e);
      LogDao.updateAcctLog(dealId,acctId, ErrEnum.InfoErr.GET_ACCT_INFO_ERR);
      return null;
    }
    if (!judgeAcctInfo(cycleBean,acctInfoContainer)) {
      LogDao.updateAcctLog(dealId,acctId, ErrEnum.InfoErr.ACCT_INFO_INTACT);
      return null;
    }
    //平台短信数量
    if(InfoEnum.IsPayBack.NO.equals(acctInfoContainer.getAcctInfoBean().getIsPayBack())){
      int platSmsValue = InfoDao.getAcctPlatSmsValue(cycleBean, acctId);
      acctInfoContainer.setPlatSmsValue(platSmsValue);

      inti(paramContainer, acctInfoContainer);
    }
    acctInfoContainer.setDealId(dealId);
    return acctInfoContainer;
  }

  /**
   * 判断账户的有效性
   */
  private static boolean judgeAcctInfo(final CycleBean cycleBean,
                                       final AcctInfoContainer acctInfoContainer){

    if(acctInfoContainer == null){
      return false;
    }

    if(acctInfoContainer.getAcctInfoBean() == null ){
      return false;
    }
    AcctInfoBean acctInfoBean = acctInfoContainer.getAcctInfoBean();
//    if(InfoEnum.IsPayBack.NO.equals(acctInfoBean.getIsPayBack()) &&
//       (acctInfoContainer.getAcctBillingGeneralBean() == null || acctInfoContainer.getAcctCommitmentsBean() == null)){
//      return false;
//    }

    if (InfoEnum.AcctStatus.STOP.equals(acctInfoBean.getRemoveTag()) ){
      if(acctInfoBean.getRemoveTime()==null){
        return false;
      }
      if(acctInfoBean.getRemoveTime().getTime()<cycleBean.getCycStartTime().getTime()){
        return false;
      }
    }
    return true;
  }

  /**
   * 设备的有效性,共享信息，预付固定共享时间
   */
  private static void inti(final ParamContainer paramContainer,
                           AcctInfoContainer acctInfoContainer) {

    CycleBean cycleBean = paramContainer.getCycleBean();

    //状态过滤无效数据
    acctInfoContainer.getDeviceStateBeanList().removeIf(deviceStateBean -> deviceStateBean.getStartTime().getTime() > cycleBean.getCycEndTime().getTime() ||
            deviceStateBean.getEndTime().getTime() < cycleBean.getCycStartTime().getTime());

    //订购过滤无效数据
    Iterator<DeviceRatePlanBean> it = acctInfoContainer.getDeviceRatePlanBeanList().iterator();
    while (it.hasNext()) {
      DeviceRatePlanBean deviceRatePlanBean =it.next();
      if (deviceRatePlanBean.getStartTime().getTime() > cycleBean.getCycEndTime().getTime() ||
          deviceRatePlanBean.getEndTime().getTime() < cycleBean.getCycStartTime().getTime()){
        it.remove();
      }

      RatePlanBean ratePlanBean = paramContainer.getRatePlan(deviceRatePlanBean.getPlanId());
      if(ratePlanBean==null){
        it.remove();
      }else{
        deviceRatePlanBean.setPlanType(ratePlanBean.getPlanType());
        if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE ||
            ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
            ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {
          int planVersionId = paramContainer.getPlanVersionId(deviceRatePlanBean.getPlanId());
          deviceRatePlanBean.setPlanVersionId(planVersionId);
        }
      }
    }

    Map<Long, List<DeviceStateBean>> deviceStateMap =
        acctInfoContainer.getDeviceStateBeanList().stream().collect(groupingBy(DeviceStateBean::getDeviceId));

    for(DeviceBean deviceBean:acctInfoContainer.getDeviceBeanList()){
      List<DeviceStateBean> deviceStateBeanList = deviceStateMap.get(deviceBean.getDeviceId());
      if(deviceStateBeanList!=null){
        int rateType = InfoUtil.getDeviceRateType(deviceBean,deviceStateBeanList,acctInfoContainer.getAcctCommitmentsBean(), cycleBean);
        acctInfoContainer.addDeviceIdMap(deviceBean.getDeviceId(),rateType);
      }
    }

    acctInfoContainer.getDeviceIdMap().keySet().removeIf(t -> acctInfoContainer.getDeviceIdMap().get(t)<=0);

    getAcctShare(paramContainer, acctInfoContainer);
    reSetPrepayFixShareTime(acctInfoContainer);
  }

  /**
   * 设置共享相关的资料信息
   */
  private static void getAcctShare(final ParamContainer paramContainer,
                                   AcctInfoContainer acctInfoContainer) {

    CycleBean cycleBean = paramContainer.getCycleBean();
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();

    for (DeviceRatePlanBean deviceRatePlanBean : acctInfoContainer.getDeviceRatePlanBeanList()) {

      if (!acctInfoContainer.isRateDevice(deviceRatePlanBean.getDeviceId())){
        continue;
      }

      switch (deviceRatePlanBean.getPlanType()) {
        case ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE:
          addAcctShare(acctId, deviceRatePlanBean.getDeviceId(), deviceRatePlanBean.getPoolId(), deviceRatePlanBean.getPlanId(), deviceRatePlanBean.getPlanVersionId(),
                  cycleBean.getCycStartTime(), cycleBean.getCycEndTime(), acctInfoContainer.getPrepayFixShareMap());
          break;
        case ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE:
          Date startTime = deviceRatePlanBean.getStartTime().getTime() < cycleBean.getCycStartTime().getTime() ? cycleBean.getCycStartTime() : deviceRatePlanBean.getStartTime();
          addAcctShare(acctId, deviceRatePlanBean.getDeviceId(), deviceRatePlanBean.getPoolId(), deviceRatePlanBean.getPlanId(), deviceRatePlanBean.getPlanVersionId(),
                  startTime, cycleBean.getCycEndTime(), acctInfoContainer.getMonthFixShareMap());
          break;
        case ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE:
          addAcctShare(acctId, deviceRatePlanBean.getDeviceId(), deviceRatePlanBean.getPoolId(), deviceRatePlanBean.getPlanId(), deviceRatePlanBean.getPlanVersionId(),
                  cycleBean.getCycStartTime(), cycleBean.getCycEndTime(), acctInfoContainer.getMonthAgileShareMap());
          break;
        case ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE:
          addAcctShare(acctId, deviceRatePlanBean.getDeviceId(), deviceRatePlanBean.getPoolId(), deviceRatePlanBean.getPlanId(), deviceRatePlanBean.getPlanVersionId(),
                  cycleBean.getCycStartTime(), cycleBean.getCycEndTime(), acctInfoContainer.getPrepayAgileShareMap());
          break;
        default:
          break;
      }
    }
  }

  private static void addAcctShare(final long acctId,
                                   final long deviceId,
                                   final long poolId,
                                   final int planId,
                                   final int planVersionId,
                                   final Date startTime,
                                   final Date endTime,
                                   Map<AcctShare, Set<Long>> shareMap) {

    boolean isExist = false;

    for (Map.Entry<AcctShare, Set<Long>> entry : shareMap.entrySet()) {
        AcctShare acctShare = entry.getKey();
      //额度处理
      if (acctShare.getAcctId() == acctId &&
          acctShare.getPoolId() == poolId &&
          acctShare.getPlanVersionId() == planVersionId) {

        if (acctShare.getStartTime().getTime() > startTime.getTime()) {
          acctShare.setStartTime(startTime);
        }

        Set<Long> deviceSet = shareMap.get(acctShare);
        deviceSet.add(deviceId);
        isExist = true;
        break;
      }
    }
    if (!isExist) {
      AcctShare acctShare = new AcctShare(acctId, poolId, planId, planVersionId, startTime, endTime);
      Set<Long> deviceSet = new HashSet<>();
      deviceSet.add(deviceId);
      shareMap.put(acctShare, deviceSet);
    }
  }

  /**
   * 设置预付固定共享池的时间
   */
  private static void reSetPrepayFixShareTime(AcctInfoContainer acctInfoContainer) {

    if (acctInfoContainer.getPrepayFixShareMap().isEmpty()){
      return;
    }
    Iterator<Map.Entry<AcctShare, Set<Long>>> itr = acctInfoContainer.getPrepayFixShareMap().entrySet().iterator();
    while (itr.hasNext()) {
      AcctShare acctShare = itr.next().getKey();
      SharePoolBean sharePoolBean = acctInfoContainer.getSharePool(acctShare.getPoolId());
      if (sharePoolBean != null) {
        acctShare.setStartTime(sharePoolBean.getStartTime());
        acctShare.setEndTime(sharePoolBean.getEndTime());
      } else {
        itr.remove();
      }
    }
  }


}
