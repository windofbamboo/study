package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.dao.DeviceBillDao;
import com.ai.iot.bill.dao.InfoDao;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dao.MdbAddDao;
import com.ai.iot.bill.dealproc.container.*;
import com.ai.iot.bill.dealproc.util.BillUtil;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.computebill.DeviceBillActivation;
import com.ai.iot.bill.entity.computebill.DeviceBillOrder;
import com.ai.iot.bill.entity.computebill.DeviceBillUsage;
import com.ai.iot.bill.entity.info.AcctBillingGeneralBean;
import com.ai.iot.bill.entity.info.DeviceBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.DeviceStateBean;
import com.ai.iot.bill.entity.multibill.*;
import com.ai.iot.bill.entity.param.*;
import com.ai.iot.bill.entity.res.*;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddShareDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.*;

/**设备的费用处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceDeal extends BaseFeeDeal {

  private static final Logger logger = LoggerFactory.getLogger(DeviceDeal.class);

  private DeviceDeal() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 设备级账单处理
   */
  public static boolean deal(DeviceInfoContainer deviceInfoContainer) {

    long dealId = deviceInfoContainer.getDealId();
    long acctId = deviceInfoContainer.getDeviceBean().getAcctId();
    long deviceId = deviceInfoContainer.getDeviceBean().getDeviceId();
    LogDao.updateDeviceLog(dealId,acctId,deviceId,ErrEnum.DEAL_START);

    ParamContainer paramContainer = ParamMgr.getParamContainer(dealId);
    if(paramContainer==null){
      LogDao.updateDeviceLog(dealId,acctId,deviceId,ErrEnum.ParamErr.PARAM_INIT_ERR);
      return false;
    }

    DeviceBillContainer deviceBillContainer = new DeviceBillContainer();
    //累积量获取
    try {
      initDeviceAdd(paramContainer.getCycleBean(), acctId, deviceId, deviceBillContainer);
    }catch (Exception e){
      LogDao.updateDeviceLog(dealId,acctId,deviceId,ErrEnum.DealErr.DEVICE_GET_ADD);
      logger.error("initDeviceAdd err :{}",e);
      return false;
    }
    //费用处理
    try {
      dealFee(paramContainer, deviceInfoContainer, deviceBillContainer);
    }catch (Exception e){
      LogDao.updateDeviceLog(dealId,acctId,deviceId,ErrEnum.DealErr.DEVICE_COMPUTE_FEE);
      logger.error("dealFee err :{}",e);
      return false;
    }
    //数据提交
    ErrEnum.DealResult dealResult = updatePdb(paramContainer.getCycleBean(), acctId, deviceId, deviceBillContainer);
    //记录日志
    LogDao.updateDeviceLog(dealId,acctId,deviceId,dealResult);

    return ErrEnum.DEAL_SUCESS.equals(dealResult);
  }

  /**
   * 账单更新方法
   */
  private static ErrEnum.DealResult updatePdb(final CycleBean cycleBean,
                                              final long acctId,
                                              final long deviceId,
                                              DeviceBillContainer deviceBillContainer) {

    String month = String.valueOf(cycleBean.getCycleId()).substring(4, 6);
    //删除原有账单
    ErrEnum.DealResult dealResult = DeviceBillDao.deleteDevicePdb(month, acctId, deviceId);
    if(ErrEnum.DEAL_SUCESS.equals(dealResult)){
      //账单更新
      dealResult = DeviceBillDao.insertDevicePdb(month, deviceBillContainer);
    }
    return dealResult;
  }

  /**
   * 设备级累积量获取方法
   */
  private static void initDeviceAdd(final CycleBean cycleBean,
                                    final long acctId,
                                    final long deviceId,
                                    DeviceBillContainer deviceBillContainer)
      throws Exception{

    List<UsedAddDevice> usedAddDeviceList = MdbAddDao.getDeviceAdd(cycleBean.getCycleId(), acctId, deviceId);
    deviceBillContainer.setUsedAddDeviceList(usedAddDeviceList);
  }

  /**
   * 处理设备级的费用
   */
  private static void dealFee(final ParamContainer paramContainer,
                              final DeviceInfoContainer deviceInfoContainer,
                              DeviceBillContainer deviceBillContainer) {

    dealActivationFee(paramContainer, deviceInfoContainer, deviceBillContainer);
    dealPlanOrderFee(paramContainer, deviceInfoContainer, deviceBillContainer);
    dealDeviceShareAdd(paramContainer,deviceInfoContainer,deviceBillContainer);
    dealDeviceUsageFee(paramContainer, deviceInfoContainer, deviceBillContainer);

    setMultiBill(paramContainer, deviceInfoContainer, deviceBillContainer);
  }


  /**
   * 激活费计算
   */
  private static void dealActivationFee(final ParamContainer paramContainer,
                                        final DeviceInfoContainer deviceInfoContainer,
                                        DeviceBillContainer deviceBillContainer) {

    AcctBillingGeneralBean acctBillingGeneralBean = deviceInfoContainer.getAcctBillingGeneralBean();

    //不收取激活费，直接返回
    if(acctBillingGeneralBean.getDefaultActivationPlan() != InfoEnum.DefaultActivationPlan.ONLY_FIRST &&
       acctBillingGeneralBean.getDefaultActivationPlan() != InfoEnum.DefaultActivationPlan.EVERY_TIME){
      return;
    }
    int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_ACTIVATION, 0);
    DeviceBean deviceBean = deviceInfoContainer.getDeviceBean();
    CycleBean cycleBean = paramContainer.getCycleBean();

    DeviceRatePlanBean deviceRatePlanBean = null;
    int activationType;

    List<DeviceBillActivation> deviceBillActivationList = deviceBillContainer.getDeviceBillActivationList();

    if (acctBillingGeneralBean.getDefaultActivationPlan() == InfoEnum.DefaultActivationPlan.ONLY_FIRST) {
      if (deviceBean.getActivationTime().getTime() > cycleBean.getCycStartTime().getTime() &&
          deviceBean.getActivationTime().getTime() < cycleBean.getCycEndTime().getTime()) {

        activationType = InfoEnum.ActivationType.FIRST;
        deviceRatePlanBean = deviceInfoContainer.getActivationPlan(deviceBean.getActivationTime());
        if(deviceRatePlanBean!=null){
          DeviceBillActivation deviceBillActivation
                  = new DeviceBillActivation(deviceBean.getAcctId(),deviceBean.getDeviceId(), cycleBean.getCycleId(),deviceRatePlanBean.getPlanId(),
                  deviceRatePlanBean.getPlanVersionId(), deviceBean.getActivationTime(),itemId,
                  acctBillingGeneralBean.getDefaultActivationFee(),activationType);
          deviceBillActivationList.add(deviceBillActivation);
        }
      }
    } else if (acctBillingGeneralBean.getDefaultActivationPlan() == InfoEnum.DefaultActivationPlan.EVERY_TIME) {
      for (DeviceStateBean deviceStateBean : deviceInfoContainer.getDeviceStateBeanList()) {
        if (InfoEnum.DeviceState.ACTIVE.equals(deviceStateBean.getStateCode()) &&
            deviceStateBean.getStartTime().getTime() > cycleBean.getCycStartTime().getTime() &&
            deviceStateBean.getStartTime().getTime() < cycleBean.getCycEndTime().getTime() &&
            deviceStateBean.getStartTime().getTime() < deviceStateBean.getEndTime().getTime()) {

          Date activationTime = deviceBean.getActivationTime().getTime()>deviceStateBean.getStartTime().getTime()? deviceBean.getActivationTime():deviceStateBean.getStartTime();
          if (deviceBean.getActivationTime().equals(deviceStateBean.getStartTime())) {
            activationType = InfoEnum.ActivationType.FIRST;
          } else {
            activationType = InfoEnum.ActivationType.AGAIN;
          }
          deviceRatePlanBean = deviceInfoContainer.getActivationPlan(activationTime);
          if(deviceRatePlanBean!=null){
            DeviceBillActivation deviceBillActivation
                    = new DeviceBillActivation(deviceBean.getAcctId(),deviceBean.getDeviceId(), cycleBean.getCycleId(),deviceRatePlanBean.getPlanId(),
                    deviceRatePlanBean.getPlanVersionId(), activationTime,itemId,
                    acctBillingGeneralBean.getDefaultActivationFee(),activationType);
            deviceBillActivationList.add(deviceBillActivation);
          }
        }
      }
    }

  }



  /**
   * 订户费计算
   */
  private static void dealPlanOrderFee(final ParamContainer paramContainer,
                                       final DeviceInfoContainer deviceInfoContainer,
                                       DeviceBillContainer deviceBillContainer) {

    CycleBean cycleBean = paramContainer.getCycleBean();
    List<DeviceRatePlanBean> deviceRatePlanBeanList = deviceInfoContainer.getDeviceRatePlanBeanList();
    AcctBillingGeneralBean acctBillingGeneralBean = deviceInfoContainer.getAcctBillingGeneralBean();

    for (DeviceRatePlanBean deviceRatePlanBean : deviceRatePlanBeanList) {
      if (deviceRatePlanBean.getStartTime().getTime() > cycleBean.getCycEndTime().getTime() ||
          deviceRatePlanBean.getEndTime().getTime() < cycleBean.getCycStartTime().getTime()){
        continue;
      }

      int planType = deviceRatePlanBean.getPlanType();
      int planVersionId = deviceRatePlanBean.getPlanVersionId();

      //预付资费计划的设备，承诺不生效
      if((planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
          planType == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          planType == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE) &&
         (deviceInfoContainer.getRateType() == InfoEnum.RateType.BILL_ACTIVATION_GRACE_PERIOD ||
          deviceInfoContainer.getRateType() == InfoEnum.RateType.BILL_MININUM_ACTIVATION_TERM)){
        continue;
      }
      //批价参数
      RateOrderFee rateOrderFee = paramContainer.getRateOrderFee(planVersionId);
      if (rateOrderFee == null) {
        continue;
      }
      //生成订户费的记录
      long[] ratio = calculateRatio(deviceRatePlanBean, rateOrderFee.getSubscriberChargeFerquency(), acctBillingGeneralBean, cycleBean);
      long orderFee = rateOrderFee.getSubscriberCharge() * ratio[0] / ratio[1];
      int itemId = paramContainer.getSumbillFix(ParamEnum.ChargeType.CHARGE_TYPE_DEVICE_PLAN, rateOrderFee.getPaymentType());

      double orderNum = Math.floor((double) ratio[0] * 100 / (double) ratio[1]) / 100;

      if(orderFee > 0){
        DeviceBillOrder deviceBillOrder = new DeviceBillOrder();

        deviceBillOrder.setAcctId(deviceInfoContainer.getDeviceBean().getAcctId());
        deviceBillOrder.setDeviceId(deviceInfoContainer.getDeviceBean().getDeviceId());
        deviceBillOrder.setCycleId(cycleBean.getCycleId());
        deviceBillOrder.setTpInsId(deviceRatePlanBean.getOrderId());
        deviceBillOrder.setPlanId(deviceRatePlanBean.getPlanId());
        deviceBillOrder.setPlanVersionId(planVersionId);
        deviceBillOrder.setPlanType(planType);
        deviceBillOrder.setItemId(itemId);
        deviceBillOrder.setFee(orderFee);
        deviceBillOrder.setAcctFee(0L);
        deviceBillOrder.setOrderNum(orderNum);

        deviceBillContainer.addDeviceBillOrderList(deviceBillOrder);
      }
      //资费内的用量计算(对于预付资费,当月没有计算费用，但是资源量是全量计算的)
      Date startTime ;
      Date endTime ;
      if (planType == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
          planType == ParamEnum.PlanType.PLANTYPE_PILE ||
          planType == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE) {

        startTime = deviceRatePlanBean.getStartTime();
        endTime = deviceRatePlanBean.getEndTime();
        ratio[0] = 100;
        ratio[1] = 100;
      } else {
        startTime = deviceRatePlanBean.getStartTime().getTime() > cycleBean.getCycStartTime().getTime() ? deviceRatePlanBean.getStartTime() : cycleBean.getCycStartTime();
        endTime = deviceRatePlanBean.getEndTime().getTime() < cycleBean.getCycEndTime().getTime() ? deviceRatePlanBean.getEndTime() : cycleBean.getCycEndTime();
      }

      List<RateBill> rateBillList = paramContainer.getRateBill(planVersionId);
      ResIncludeDevice resIncludeDevice = new ResIncludeDevice();

      resIncludeDevice.setAcctId(deviceInfoContainer.getDeviceBean().getAcctId());
      resIncludeDevice.setDeviceId(deviceInfoContainer.getDeviceBean().getDeviceId());
      resIncludeDevice.setCycleId(cycleBean.getCycleId());
      resIncludeDevice.setTpInsId(deviceRatePlanBean.getOrderId());
      resIncludeDevice.setPoolId(deviceRatePlanBean.getPoolId());
      resIncludeDevice.setPlanId(deviceRatePlanBean.getPlanId());
      resIncludeDevice.setPlanVersionId(planVersionId);
      resIncludeDevice.setStartTime(startTime);
      resIncludeDevice.setEndTime(endTime);

      addResInclude(rateBillList, resIncludeDevice, planType, ratio,
              cycleBean, deviceBillContainer);
    }
    dealResIncludePile(deviceInfoContainer, deviceBillContainer);

  }

  /**
   * 计算比例
   */
  private static long[] calculateRatio(final DeviceRatePlanBean deviceRatePlanBean,
                                       final int subscriberChargeFerquency,
                                       final AcctBillingGeneralBean acctBillingGeneralBean,
                                       final CycleBean cycleBean) {

    long[] ratio = {0, 1};
    switch (deviceRatePlanBean.getPlanType()) {
      case ParamEnum.PlanType.PLANTYPE_EVENT:
      case ParamEnum.PlanType.PLANTYPE_PILE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE:
        if (deviceRatePlanBean.getStartTime().getTime() < cycleBean.getCycEndTime().getTime() &&
            deviceRatePlanBean.getStartTime().getTime() >= cycleBean.getCycStartTime().getTime() &&
            deviceRatePlanBean.getStartTime().getTime() < deviceRatePlanBean.getEndTime().getTime()) {
          ratio[0] = 100;
          ratio[1] = 100;
        }
        break;
      case ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE:
        if (subscriberChargeFerquency == ParamEnum.SubscriberChargeFerquency.ONLY_FIRST) {
          if (deviceRatePlanBean.getStartTime().getTime() < cycleBean.getCycEndTime().getTime() &&
              deviceRatePlanBean.getStartTime().getTime() >= cycleBean.getCycStartTime().getTime() &&
              deviceRatePlanBean.getStartTime().getTime() < deviceRatePlanBean.getEndTime().getTime()) {
            ratio[0] = 100;
            ratio[1] = 100;
          }
        } else {
          ratio[0] = 100;
          ratio[1] = 100;
        }
        break;
      case ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE:
      case ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE:
      case ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE:
        String modTag = InfoEnum.DisountMode.NONE;
        if (deviceRatePlanBean.getStartTime().getTime() > cycleBean.getCycStartTime().getTime()) {
          if (InfoEnum.ActiveFlag.RENEWAL.equals(deviceRatePlanBean.getActiveFlag())) {
            modTag = acctBillingGeneralBean.getRenewalProration();
          }else if (InfoEnum.ActiveFlag.ACTIVATION.equals(deviceRatePlanBean.getActiveFlag())) {
            modTag = acctBillingGeneralBean.getActivationProration();
          }
        }
        ratio = calculateRatiobyMod(cycleBean, deviceRatePlanBean.getStartTime(), modTag);
        break;
      default:
        break;
    }
    return ratio;
  }

  /**
   * 处理额度信息
   */
  private static void addResInclude(final List<RateBill> rateBillList,
                                    final ResIncludeDevice resIncludeDeviceBase,
                                    final int planType,
                                    long[] feeRatio,
                                    final CycleBean cycleBean,
                                    DeviceBillContainer deviceBillContainer) {

    if (rateBillList == null || rateBillList.isEmpty()) {
      return;
    }
    Date startTime = resIncludeDeviceBase.getStartTime().getTime() > cycleBean.getCycStartTime().getTime() ? resIncludeDeviceBase.getStartTime() : cycleBean.getCycStartTime();
    Date endTime = resIncludeDeviceBase.getEndTime().getTime() < cycleBean.getCycEndTime().getTime() ? resIncludeDeviceBase.getEndTime() : cycleBean.getCycEndTime();

    for (RateBill rateBill : rateBillList) {
      //折算比例
      long[] ratio = feeRatio;
      //堆叠,预付灵活共享 按天折算
      if (planType == ParamEnum.PlanType.PLANTYPE_PILE ||
          (planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE  && rateBill.isShare() )) {
        ratio = calculateRatiobyDays(cycleBean,
                                     resIncludeDeviceBase.getStartTime(),
                                     resIncludeDeviceBase.getEndTime());
      }
      double tValue = (double)rateBill.getValue() * (double)ratio[0] / (double)ratio[1];
      long value = (long) Math.ceil(tValue) ;
      //事件,预付单个,预付单个 都放在个人头上
      //月付灵活共享,预付灵活共享,月付固定共享，预付固定共享 如果是短信和语音的事件,没有选择相应的共享，则当做个人独占看待
      if (planType == ParamEnum.PlanType.PLANTYPE_EVENT ||
          planType == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          planType == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE ||
          ((planType == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE || planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
            planType == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE || planType == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE) &&
           !rateBill.isShare() )) {

        try {
          ResIncludeDevice resIncludeDevice = (ResIncludeDevice) resIncludeDeviceBase.clone();

          resIncludeDevice.setZoneId(rateBill.getZoneId());
          resIncludeDevice.setBillingGroupId(rateBill.getBillingGroupId());
          resIncludeDevice.setBillId(rateBill.getBillId());
          resIncludeDevice.setTotalValue(value);
          resIncludeDevice.setCurrValue(value);

          deviceBillContainer.addResIncludeDeviceList(resIncludeDevice);
        } catch (CloneNotSupportedException e) {
          logger.error("clone ResIncludeDevice err.{}", e);
        }
      }
      //月付灵活共享,预付灵活共享 记录共享出来的额度
      else if ((planType == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE || planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) &&
               rateBill.isShare() ) {

        ResIncludeShareTurn resIncludeAgileShareTrun = new ResIncludeShareTurn();

        resIncludeAgileShareTrun.setAcctId(resIncludeDeviceBase.getAcctId());
        resIncludeAgileShareTrun.setDeviceId(resIncludeDeviceBase.getDeviceId());
        resIncludeAgileShareTrun.setCycleId(cycleBean.getCycleId());
        resIncludeAgileShareTrun.setTpInsId(resIncludeDeviceBase.getTpInsId());
        resIncludeAgileShareTrun.setPoolId(resIncludeDeviceBase.getPoolId());
        resIncludeAgileShareTrun.setPlanId(resIncludeDeviceBase.getPlanId());
        resIncludeAgileShareTrun.setPlanVersionId(rateBill.getPlanVersionId());
        resIncludeAgileShareTrun.setZoneId(rateBill.getZoneId());
        resIncludeAgileShareTrun.setBillingGroupId(rateBill.getBillingGroupId());
        resIncludeAgileShareTrun.setBillId(rateBill.getBillId());
        resIncludeAgileShareTrun.setValue(value);
        resIncludeAgileShareTrun.setPileValue(0);
        resIncludeAgileShareTrun.setStartTime(startTime);
        resIncludeAgileShareTrun.setEndTime(endTime);

        deviceBillContainer.addResIncludeAgileShareTrunList(resIncludeAgileShareTrun);
      }
      //月付固定共享，预付固定共享，在账户层计算；追加在账户上订购
      else if (planType == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE ||
              planType == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE) {

        ResIncludeShareTurn resIncludeFixShareTrun = new ResIncludeShareTurn();

        resIncludeFixShareTrun.setAcctId(resIncludeDeviceBase.getAcctId());
        resIncludeFixShareTrun.setDeviceId(resIncludeDeviceBase.getDeviceId());
        resIncludeFixShareTrun.setCycleId(cycleBean.getCycleId());
        resIncludeFixShareTrun.setTpInsId(resIncludeDeviceBase.getTpInsId());
        resIncludeFixShareTrun.setPoolId(resIncludeDeviceBase.getPoolId());
        resIncludeFixShareTrun.setPlanId(resIncludeDeviceBase.getPlanId());
        resIncludeFixShareTrun.setPlanVersionId(rateBill.getPlanVersionId());
        resIncludeFixShareTrun.setZoneId(rateBill.getZoneId());
        resIncludeFixShareTrun.setBillingGroupId(rateBill.getBillingGroupId());
        resIncludeFixShareTrun.setBillId(rateBill.getBillId());
        resIncludeFixShareTrun.setValue(value);
        resIncludeFixShareTrun.setPileValue(0);
        resIncludeFixShareTrun.setStartTime(startTime);
        resIncludeFixShareTrun.setEndTime(endTime);

        deviceBillContainer.addResIncludeFixShareTurnList(resIncludeFixShareTrun);
      }
      //堆叠, 单独记录
      else if (planType == ParamEnum.PlanType.PLANTYPE_PILE) {

        ResIncludePile resIncludePile = new ResIncludePile();

        resIncludePile.setAcctId(resIncludeDeviceBase.getAcctId());
        resIncludePile.setDeviceId(resIncludeDeviceBase.getDeviceId());
        resIncludePile.setCycleId(cycleBean.getCycleId());
        resIncludePile.setTpInsId(resIncludeDeviceBase.getTpInsId());
        resIncludePile.setPlanId(resIncludeDeviceBase.getPlanId());
        resIncludePile.setPlanVersionId(rateBill.getPlanVersionId());
        resIncludePile.setZoneId(rateBill.getZoneId());
        resIncludePile.setBillingGroupId(rateBill.getBillingGroupId());
        resIncludePile.setBillId(rateBill.getBillId());
        resIncludePile.setValue(value);
        resIncludePile.setBaseTpInsId(0);
        resIncludePile.setStartTime(startTime);
        resIncludePile.setEndTime(endTime);

        deviceBillContainer.addResIncludePileList(resIncludePile);
      }
    }
  }


  /**
   * 处理堆叠资费
   */
  private static void dealResIncludePile(final DeviceInfoContainer deviceInfoContainer,
                                         DeviceBillContainer deviceBillContainer) {

    if (deviceBillContainer.getResIncludePileList().isEmpty())
      return;

    List<DeviceRatePlanBean> deviceRatePlanBeanList = deviceInfoContainer.getDeviceRatePlanBeanList();

    Iterator<ResIncludePile> it = deviceBillContainer.getResIncludePileList().iterator();
    while (it.hasNext()) {
      ResIncludePile resIncludePile = it.next();
      boolean isActive=false;
      DeviceRatePlanBean ratePlanPile = deviceInfoContainer.getDeviceRatePlan(resIncludePile.getTpInsId());
      for (DeviceRatePlanBean deviceRatePlanBean : deviceRatePlanBeanList) {
        if ((deviceRatePlanBean.getStartTime().getTime() <= ratePlanPile.getStartTime().getTime() &&
             deviceRatePlanBean.getEndTime().getTime() >= ratePlanPile.getStartTime().getTime()) &&
             (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE ||
              deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
              deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE ||
              deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
              deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
              deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE)) {

          resIncludePile.setBaseTpInsId(deviceRatePlanBean.getOrderId());
          resIncludePile.setBasePlanType(deviceRatePlanBean.getPlanType());
          resIncludePile.setBasePlanId(deviceRatePlanBean.getPlanId());
          isActive=true;
          break;
        }
      }
      if(!isActive){
        it.remove();
      }
    }

    //把堆叠资费的资源值，加到基本资费上
    for (ResIncludePile resIncludePile : deviceBillContainer.getResIncludePileList()) {

      boolean matched = false;
      if (resIncludePile.getBasePlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
          resIncludePile.getBasePlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
        for (ResIncludeShareTurn resIncludeAgileShareTrun : deviceBillContainer.getResIncludeAgileShareTrunList()) {
          if (resIncludePile.getBaseTpInsId() == resIncludeAgileShareTrun.getTpInsId() &&
              resIncludePile.getBillId() == resIncludeAgileShareTrun.getBillId()) {
            resIncludeAgileShareTrun.setPileValue(resIncludeAgileShareTrun.getPileValue() + resIncludePile.getValue());
            matched = true;
            break;
          }
        }
      } else if (resIncludePile.getBasePlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
                 resIncludePile.getBasePlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {
        for (ResIncludeShareTurn resIncludeShareTurn : deviceBillContainer.getResIncludeFixShareTurnList()) {
          if (resIncludePile.getBaseTpInsId() == resIncludeShareTurn.getTpInsId() &&
              resIncludePile.getBillId() == resIncludeShareTurn.getBillId()) {
            resIncludeShareTurn.setPileValue(resIncludeShareTurn.getPileValue() + resIncludePile.getValue());
            matched = true;
            break;
          }
        }
      }

      if(!matched){
        for (ResIncludeDevice resIncludeDevice : deviceBillContainer.getResIncludeDeviceList()) {
          if (resIncludePile.getBaseTpInsId() == resIncludeDevice.getTpInsId() &&
              resIncludePile.getBillId() == resIncludeDevice.getBillId()) {
            resIncludeDevice.setTotalValue(resIncludeDevice.getTotalValue() + resIncludePile.getValue());
            resIncludeDevice.setCurrValue(resIncludeDevice.getCurrValue() + resIncludePile.getValue());
            break;
          }
        }
      }

    }
  }

  /**
   * 处理用于共享部分的累积量
   */
  private static void dealDeviceShareAdd(final ParamContainer paramContainer,
                                         final DeviceInfoContainer deviceInfoContainer,
                                         DeviceBillContainer deviceBillContainer){

    //没有累积量
    if (deviceBillContainer.getUsedAddDeviceList() == null || deviceBillContainer.getUsedAddDeviceList().isEmpty()) {
      return;
    }
    CycleBean cycleBean=paramContainer.getCycleBean();
    for (UsedAddDevice usedAddDevice : deviceBillContainer.getUsedAddDeviceList()) {
      usedAddDevice.setCycleId(cycleBean.getCycleId());

      ResIncludeDevice resIncludeDevice =
          deviceBillContainer.getResIncludeDevice(usedAddDevice.getTpInsId(), usedAddDevice.getBillId());
      if (resIncludeDevice != null) {
        usedAddDevice.setPlanId(resIncludeDevice.getPlanId());
        usedAddDevice.setPlanVersionId(resIncludeDevice.getPlanVersionId());
        continue;
      }

      int planId = 0;
      int planVersionId = 0;
      long poolId = 0;
      ResIncludeShareTurn resIncludeAgileShareTrun =
          deviceBillContainer.getResIncludeAgileShareTrun(usedAddDevice.getTpInsId(), usedAddDevice.getBillId());
      if (resIncludeAgileShareTrun != null) {
        planId = resIncludeAgileShareTrun.getPlanId();
        planVersionId = resIncludeAgileShareTrun.getPlanVersionId();
      }

      if (planId == 0) {
        ResIncludeShareTurn resIncludeFixShareTurn =
            deviceBillContainer.getResIncludeFixShareTurn(usedAddDevice.getTpInsId(), usedAddDevice.getBillId());
        if (resIncludeFixShareTurn != null) {
          planId = resIncludeFixShareTurn.getPlanId();
          planVersionId = resIncludeFixShareTurn.getPlanVersionId();
          poolId = resIncludeFixShareTurn.getPoolId();
        }
      }

      if (planId != 0) {
        usedAddDevice.setPlanId(planId);
        usedAddDevice.setPlanVersionId(planVersionId);
        usedAddDevice.setShareTag(true);

        UsedAddShareDetail usedAddShareDetail = BillUtil.getUsedAddShareDetail(poolId,usedAddDevice);
        deviceBillContainer.addUsedAddShareDetailList(usedAddShareDetail);
        continue;
      }

      DeviceRatePlanBean deviceRatePlanBean
          = deviceInfoContainer.getDeviceRatePlan(usedAddDevice.getTpInsId());
      if(deviceRatePlanBean != null){
        usedAddDevice.setPlanId(deviceRatePlanBean.getPlanId());
        usedAddDevice.setPlanVersionId(deviceRatePlanBean.getPlanVersionId());
      }

    }
  }

  /**
   * 设备级用量核减
   */
  private static void dealDeviceUsageFee(final ParamContainer paramContainer,
                                         final DeviceInfoContainer deviceInfoContainer,
                                         DeviceBillContainer deviceBillContainer) {

    if (deviceBillContainer.getUsedAddDeviceList() == null || deviceBillContainer.getUsedAddDeviceList().isEmpty()) {
      return;
    }
    CycleBean cycleBean = paramContainer.getCycleBean();
    for (UsedAddDevice usedAddDevice : deviceBillContainer.getUsedAddDeviceList()) {
      if(usedAddDevice.shareTag()){
        continue;
      }
      ResIncludeDevice resIncludeDevice = deviceBillContainer.getResIncludeDevice(usedAddDevice.getTpInsId(), usedAddDevice.getBillId());
      if (resIncludeDevice != null) {
        RateBill rateBill = paramContainer.getRateBill(resIncludeDevice.getPlanVersionId(), resIncludeDevice.getBillId());
        if (rateBill != null && !rateBill.isPrice()) {
            ResUsedDevice resUsedDevice = new ResUsedDevice();

            resUsedDevice.setAcctId(resIncludeDevice.getAcctId());
            resUsedDevice.setDeviceId(resIncludeDevice.getDeviceId());
            resUsedDevice.setCycleId(cycleBean.getCycleId());
            resUsedDevice.setTpInsId(resIncludeDevice.getTpInsId());
            resUsedDevice.setZoneId(resIncludeDevice.getZoneId());
            resUsedDevice.setBillingGroupId(resIncludeDevice.getBillingGroupId());
            resUsedDevice.setBillId(resIncludeDevice.getBillId());
            resUsedDevice.setUsedValue(usedAddDevice.getCurrValue());
            resUsedDevice.setStartTime(resIncludeDevice.getStartTime());
            resUsedDevice.setEndTime(resIncludeDevice.getEndTime());

            deviceBillContainer.addResUsedDeviceList(resUsedDevice);
            continue;
        }
        //用量核减
        long currInclude = resIncludeDevice.getTotalValue() > usedAddDevice.getLastValue() ? resIncludeDevice.getTotalValue()-usedAddDevice.getLastValue():0L;
        resIncludeDevice.setCurrValue(currInclude);
        long upperValue = getUpperValue(currInclude, usedAddDevice.getCurrValue());
        usedAddDevice.setUpperValue(upperValue);

        ResUsedDevice resUsedDevice = new ResUsedDevice();

        resUsedDevice.setAcctId(resIncludeDevice.getAcctId());
        resUsedDevice.setDeviceId(resIncludeDevice.getDeviceId());
        resUsedDevice.setCycleId(cycleBean.getCycleId());
        resUsedDevice.setTpInsId(resIncludeDevice.getTpInsId());
        resUsedDevice.setZoneId(resIncludeDevice.getZoneId());
        resUsedDevice.setBillingGroupId(resIncludeDevice.getBillingGroupId());
        resUsedDevice.setBillId(resIncludeDevice.getBillId());
        resUsedDevice.setUsedValue(usedAddDevice.getCurrValue()-upperValue);
        resUsedDevice.setStartTime(resIncludeDevice.getStartTime());
        resUsedDevice.setEndTime(resIncludeDevice.getEndTime());

        deviceBillContainer.addResUsedDeviceList(resUsedDevice);

        if (usedAddDevice.getUpperValue() <= 0) continue;
        //用量批价--基本资费的批价规则
        if (rateBill != null) {
          long[] result = calculateUsageFee(rateBill.getBizType(), rateBill.getUnit(), rateBill.getTimes(), rateBill.isBulk(),
                                            rateBill.getRatio(),rateBill.getPrecision(), usedAddDevice.getUpperValue());

          long fee = result[0];
          long bulkAdjust = result[1];
          int itemId = paramContainer.getSumbillUse(rateBill.getChargeMode(), rateBill.getPaymentType(), rateBill.getBizType(), rateBill.isRoam());

          DeviceBillUsage deviceBillUsage = new DeviceBillUsage();

          deviceBillUsage.setAcctId(resIncludeDevice.getAcctId());
          deviceBillUsage.setDeviceId(resIncludeDevice.getDeviceId());
          deviceBillUsage.setCycleId(cycleBean.getCycleId());
          deviceBillUsage.setTpInsId(resIncludeDevice.getTpInsId());
          deviceBillUsage.setPlanId(resIncludeDevice.getPlanId());
          deviceBillUsage.setPlanVersionId(resIncludeDevice.getPlanVersionId());
          deviceBillUsage.setZoneId(resIncludeDevice.getZoneId());
          deviceBillUsage.setGroupId(resIncludeDevice.getBillingGroupId());
          deviceBillUsage.setBillId(resIncludeDevice.getBillId());
          deviceBillUsage.setItemId(itemId);
          deviceBillUsage.setFee(fee);
          deviceBillUsage.setBizType(rateBill.getBizType());

          deviceBillContainer.addDeviceBillUsageList(deviceBillUsage);

          usedAddDevice.setBulkAdjust(bulkAdjust);//设置批量超额
        }
      }
      //用量批价--标准资费的批价规则
      else {
        DeviceRatePlanBean deviceRatePlanBean = deviceInfoContainer.getDeviceRatePlan(usedAddDevice.getTpInsId());
        if(deviceRatePlanBean == null){ //找不到订购，视为无效数据
          continue;
        }
        RatePlanBean ratePlanBean = paramContainer.getRatePlan(deviceRatePlanBean.getPlanId());

        usedAddDevice.setPlanId(deviceRatePlanBean.getPlanId());
        usedAddDevice.setPlanVersionId(deviceRatePlanBean.getPlanVersionId());

        FeeBaseBean feeBaseBean = paramContainer.getFeeBase(usedAddDevice.getBillId());
        AddupIdBean addupIdBean = paramContainer.getAddupId(usedAddDevice.getBillId());
        if (feeBaseBean != null && addupIdBean != null) {
          long[] result = calculateUsageFee(addupIdBean.getBizType(), feeBaseBean.getBaseUnit(), feeBaseBean.getBaseTimes(),false,
                                            feeBaseBean.getBaseUnit(),feeBaseBean.getPrecision(), usedAddDevice.getUpperValue());
          long fee = result[0];
          long bulkAdjust = result[1];
          int itemId = paramContainer.getSumbillUse(addupIdBean.getCallType(), ratePlanBean.getPaymentType(), addupIdBean.getBizType(), feeBaseBean.isRoam());

          DeviceBillUsage deviceBillUsage = new DeviceBillUsage();

          deviceBillUsage.setAcctId(deviceInfoContainer.getDeviceBean().getAcctId());
          deviceBillUsage.setDeviceId(usedAddDevice.getDeviceId());
          deviceBillUsage.setCycleId(cycleBean.getCycleId());
          deviceBillUsage.setTpInsId(usedAddDevice.getTpInsId());
          deviceBillUsage.setPlanId(deviceRatePlanBean.getPlanId());
          deviceBillUsage.setPlanVersionId(deviceRatePlanBean.getPlanVersionId());
          deviceBillUsage.setZoneId(addupIdBean.getZoneId());
          deviceBillUsage.setGroupId(addupIdBean.getGroupId());
          deviceBillUsage.setBillId(usedAddDevice.getBillId());
          deviceBillUsage.setItemId(itemId);
          deviceBillUsage.setFee(fee);

          deviceBillContainer.addDeviceBillUsageList(deviceBillUsage);

          usedAddDevice.setBulkAdjust(bulkAdjust);//设置批量超额
        }
      }
    }
  }


  private static void setMultiBill(final ParamContainer paramContainer,
                                   final DeviceInfoContainer deviceInfoContainer,
                                   DeviceBillContainer deviceBillContainer) {
    //DeviceUsage
    setDeviceUsage(paramContainer, deviceInfoContainer, deviceBillContainer);
    //DeviceZoneUsage

    //DeviceBillData
    //DeviceBillVoice
    //DeviceBillSms
    setDeviceBillUsage(paramContainer, deviceBillContainer);
    //DeviceBillActive
    setDeviceBillActive(deviceBillContainer);
    //DeviceBillPrepay
    setDeviceBillPrepay(paramContainer, deviceInfoContainer, deviceBillContainer);
    //DeviceBillMonth
    setDeviceBillMonth(paramContainer, deviceInfoContainer, deviceBillContainer);
    //设置seqId
    setSeqId(deviceInfoContainer.getSeqId(),deviceBillContainer);
  }

  /**
   * 设备用量生成
   */
  private static void setDeviceUsage(final ParamContainer paramContainer,
                                     final DeviceInfoContainer deviceInfoContainer,
                                     DeviceBillContainer deviceBillContainer) {

    int cycleId = paramContainer.getCycleBean().getCycleId();
    long seqId = deviceInfoContainer.getSeqId();

    DeviceUsage deviceUsage = new DeviceUsage();
    deviceUsage.setAcctId(deviceInfoContainer.getDeviceBean().getAcctId());
    deviceUsage.setDeviceId(deviceInfoContainer.getDeviceBean().getDeviceId());
    deviceUsage.setCycleId(cycleId);

    long dataUsage = 0;
    long smsUsage = 0;
    long voiceUsage = 0;
    long eventDataUsage = 0;
    int eventNums = 0;

    for (UsedAddDevice usedAddDevice : deviceBillContainer.getUsedAddDeviceList()) {
      if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
        dataUsage += usedAddDevice.getCurrValue();
        DeviceRatePlanBean deviceRatePlanBean = deviceInfoContainer.getDeviceRatePlan(usedAddDevice.getTpInsId());
        if (deviceRatePlanBean != null) {
          if (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT) {
            eventDataUsage += usedAddDevice.getCurrValue();
          }
        }
      } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
        voiceUsage += usedAddDevice.getCurrValue();
      } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
        smsUsage += usedAddDevice.getCurrValue();
      }
    }
    for (DeviceRatePlanBean deviceRatePlanBean : deviceInfoContainer.getDeviceRatePlanBeanList()) {
      if (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT) {
        eventNums += 1;
      }
    }

    deviceUsage.setSeqId(seqId);
    deviceUsage.setDataUsage(dataUsage);
    deviceUsage.setSmsUsage(smsUsage);
    deviceUsage.setVoiceUsage(voiceUsage);
    deviceUsage.setEventDataUsage(eventDataUsage);
    deviceUsage.setEventNums(eventNums);

    deviceBillContainer.setDeviceUsage(deviceUsage);
  }

  /**
   * 流量账单、语音账单、短信账单生成
   */
  private static void setDeviceBillUsage(final ParamContainer paramContainer,
                                         DeviceBillContainer deviceBillContainer) {
    //个人独占部分
    for (ResIncludeDevice resIncludeDevice : deviceBillContainer.getResIncludeDeviceList()) {
      //费用
      long usageFee = deviceBillContainer.getUsageFee(resIncludeDevice.getTpInsId(),
                                                      resIncludeDevice.getPlanVersionId(),
                                                      resIncludeDevice.getBillId());


      setDeviceUsage(resIncludeDevice,String.valueOf(usageFee),String.valueOf(resIncludeDevice.getCurrValue()),
                     paramContainer,deviceBillContainer);
    }
    //灵活共享部分
    for(ResIncludeShareTurn resIncludeShareTurn : deviceBillContainer.getResIncludeAgileShareTrunList()){
      setDeviceUsage(resIncludeShareTurn,BillEnum.SHARE_VALUE,BillEnum.SHARE_VALUE,
                     paramContainer,deviceBillContainer);
    }
    //固定共享部分
    for(ResIncludeShareTurn resIncludeShareTurn : deviceBillContainer.getResIncludeFixShareTurnList()){
      setDeviceUsage(resIncludeShareTurn,BillEnum.SHARE_VALUE,BillEnum.SHARE_VALUE,
                     paramContainer,deviceBillContainer);
    }
    BillUtil.trimDeviceBillData(deviceBillContainer.getDeviceBillDataList());
    BillUtil.trimDeviceBillSms(deviceBillContainer.getDeviceBillSmsList());
    BillUtil.trimDeviceBillVoice(deviceBillContainer.getDeviceBillVoiceList());
  }

  private static <T extends DeviceResInterface> void setDeviceUsage(final T resInclude,
                                                     final String usageFee,
                                                     final String includeValue,
                                                     final ParamContainer paramContainer,
                                                     DeviceBillContainer deviceBillContainer){

    RatePlanBean ratePlanBean = paramContainer.getRatePlan(resInclude.getPlanId());
    int planType = ratePlanBean.getPlanType();
    //批价规则
    RateBill rateBill = paramContainer.getRateBill(resInclude.getPlanVersionId(), resInclude.getBillId());
    int includeMode = BillUtil.getIncludeMode(planType);
    //累积量
    long roundValue = 0;
    long roundAdjust = 0;
    long bulkAdjust = 0;
    UsedAddDevice usedAddDevice =
            deviceBillContainer.getUsedAddDevice(resInclude.getTpInsId(), resInclude.getBillId());
    if (usedAddDevice != null) {
      roundValue = usedAddDevice.getCurrValue();
      roundAdjust = usedAddDevice.getRoundAdjust();
      bulkAdjust = usedAddDevice.getBulkAdjust();
    }

    if (rateBill.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {

      DeviceBillData deviceBillData = new DeviceBillData();
      deviceBillData.setAcctId(resInclude.getAcctId());
      deviceBillData.setDeviceId(resInclude.getDeviceId());
      deviceBillData.setCycleId(resInclude.getCycleId());
      deviceBillData.setPlanId(resInclude.getPlanId());
      deviceBillData.setPlanType(planType);
      deviceBillData.setZoneId(resInclude.getZoneId());
      deviceBillData.setRemote(rateBill.isRoam());

      RatePlanVersionBean ratePlanVersionBean =
              paramContainer.getRatePlanVersion(resInclude.getPlanId(), resInclude.getPlanVersionId());
      if (ratePlanVersionBean != null) {
        deviceBillData.setIsExpireTermByUsage(ratePlanVersionBean.getIsExpireTermByUsage());
      } else {
        deviceBillData.setIsExpireTermByUsage(1);
      }
      deviceBillData.setGprsValue(roundValue);
      deviceBillData.setGprsFee(usageFee);
      deviceBillData.setIncludeMode(includeMode);
      deviceBillData.setIncludeValue(includeValue);
      deviceBillData.setRoundAdjust(roundAdjust);
      if(BillEnum.SHARE_VALUE.equals(includeValue)){
        deviceBillData.setBulkAdjust(BillEnum.SHARE_VALUE);
      }else{
        deviceBillData.setBulkAdjust(String.valueOf(bulkAdjust));
      }

      deviceBillContainer.addDeviceBillDataList(deviceBillData);
    } else if (rateBill.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {

      DeviceBillVoice deviceBillVoice = new DeviceBillVoice();

      deviceBillVoice.setAcctId(resInclude.getAcctId());
      deviceBillVoice.setDeviceId(resInclude.getDeviceId());
      deviceBillVoice.setCycleId(resInclude.getCycleId());
      deviceBillVoice.setPlanId(resInclude.getPlanId());
      deviceBillVoice.setPlanType(planType);
      deviceBillVoice.setZoneId(resInclude.getZoneId());
      deviceBillVoice.setGroupId(rateBill.getBillingGroupId());
      deviceBillVoice.setRemote(rateBill.isRoam());
      deviceBillVoice.setVoiceValue(roundValue);
      deviceBillVoice.setVoiceCharge(usageFee);
      deviceBillVoice.setIncludeMode(includeMode);
      deviceBillVoice.setIncludeValue(includeValue);
      deviceBillVoice.setCallType(rateBill.getChargeMode());

      deviceBillContainer.addDeviceBillVoiceList(deviceBillVoice);

    } else if (rateBill.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {

      DeviceBillSms deviceBillSms = new DeviceBillSms();

      deviceBillSms.setAcctId(resInclude.getAcctId());
      deviceBillSms.setDeviceId(resInclude.getDeviceId());
      deviceBillSms.setCycleId(resInclude.getCycleId());
      deviceBillSms.setPlanId(resInclude.getPlanId());
      deviceBillSms.setPlanType(planType);
      deviceBillSms.setZoneId(resInclude.getZoneId());
      deviceBillSms.setGroupId(rateBill.getBillingGroupId());
      deviceBillSms.setRemote(rateBill.isRoam());
      deviceBillSms.setSmsValue(roundValue);
      deviceBillSms.setSmsCharge(usageFee);
      deviceBillSms.setIncludeMode(includeMode);
      deviceBillSms.setIncludeValue(includeValue);
      deviceBillSms.setCallType(rateBill.getChargeMode());

      deviceBillContainer.addDeviceBillSmsList(deviceBillSms);
    }
  }


  /**
   * 激活账单生成
   */
  private static void setDeviceBillActive(DeviceBillContainer deviceBillContainer) {

    List<DeviceBillActivation> deviceBillActivationList = deviceBillContainer.getDeviceBillActivationList();
    if (deviceBillActivationList == null || deviceBillActivationList.isEmpty()){
      return;
    }
    List<DeviceBillActive> deviceBillActiveList = deviceBillContainer.getDeviceBillActiveList();

    deviceBillActivationList.forEach(deviceBillActivation -> {
      DeviceBillActive deviceBillActive = new DeviceBillActive();
      deviceBillActive.setAcctId(deviceBillActivation.getAcctId());
      deviceBillActive.setDeviceId(deviceBillActivation.getDeviceId());
      deviceBillActive.setCycleId(deviceBillActivation.getCycleId());
      deviceBillActive.setActiveTime(deviceBillActivation.getActivationTime());
      deviceBillActive.setActiveCharge(deviceBillActivation.getFee());
      deviceBillActive.setActiveType(deviceBillActivation.getActivationType());
      deviceBillActiveList.add(deviceBillActive);
    });
  }

  /**
   * 预付账单生成 预付单个,预付固定,预付灵活，事件，堆叠
   */
  private static void setDeviceBillPrepay(final ParamContainer paramContainer,
                                          final DeviceInfoContainer deviceInfoContainer,
                                          DeviceBillContainer deviceBillContainer) {

    List<DeviceBillOrder> deviceBillOrderList = deviceBillContainer.getDeviceBillOrderList();

    int cycleId = paramContainer.getCycleBean().getCycleId();
    long acctId = deviceInfoContainer.getDeviceBean().getAcctId();

    for(DeviceRatePlanBean deviceRatePlanBean:deviceInfoContainer.getDeviceRatePlanBeanList()){

       boolean isPrePay = false;
       if(deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PILE){
         isPrePay =true;
       }
       if(!isPrePay){
         continue;
       }
      DeviceBillPrepay deviceBillPrepay = new DeviceBillPrepay();
      deviceBillPrepay.setAcctId(acctId);
      deviceBillPrepay.setDeviceId(deviceRatePlanBean.getDeviceId());
      deviceBillPrepay.setCycleId(cycleId);
      deviceBillPrepay.setPlanId(deviceRatePlanBean.getPlanId());
      deviceBillPrepay.setPlanType(deviceRatePlanBean.getPlanType());
      deviceBillPrepay.setPlanVersionId(deviceRatePlanBean.getPlanVersionId());

      long orderCharge = 0L;
      if(deviceRatePlanBean.getPlanType() != ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
        if(deviceBillOrderList!=null && !deviceBillOrderList.isEmpty()){
          orderCharge = deviceBillOrderList.stream().filter(t -> t.getTpInsId() == deviceRatePlanBean.getOrderId())
                                                    .mapToLong(DeviceBillOrder::getFee).sum();
        }
        double usageProrationIndex = orderCharge>0 ? 1.0:0;
        deviceBillPrepay.setUsageProrationIndex(usageProrationIndex);
        deviceBillPrepay.setOrderCharge(orderCharge);
      }else{
        deviceBillPrepay.setUsageProrationIndex(1);
        deviceBillPrepay.setOrderCharge(0);
      }

      initDeviceBillPrepay(deviceRatePlanBean.getOrderId(), deviceRatePlanBean.getPlanType(), deviceBillContainer, paramContainer, deviceBillPrepay);
      deviceBillContainer.addDeviceBillPrepayList(deviceBillPrepay);
    }
  }

  private static void initDeviceBillPrepay(final long tpInsId,
                                           final int planType,
                                           final DeviceBillContainer deviceBillContainer,
                                           final ParamContainer paramContainer,
                                           DeviceBillPrepay deviceBillPrepay){

    //资源量信息
    Map<String,Long> valueMap = new HashMap<>();
    if(planType == ParamEnum.PlanType.PLANTYPE_PILE){
      //堆叠资费处理
      List<ResIncludePile> resIncludePileList = deviceBillContainer.getResIncludePileList(tpInsId);
      BillUtil.getPrepayInclude(resIncludePileList,paramContainer,valueMap);
    }else{
      //独占部分
      List<ResIncludeDevice> resIncludeDevices = deviceBillContainer.getResIncludeDeviceList(tpInsId);
      BillUtil.getPrepayInclude(resIncludeDevices,paramContainer,valueMap);
    }
    deviceBillPrepay.setTermStartDate(valueMap.getOrDefault("termStartDate",0L));
    deviceBillPrepay.setTermEndDate(valueMap.getOrDefault("termEndDate",0L));
    deviceBillPrepay.setIncludeDataValue(String.valueOf(valueMap.getOrDefault("includeDataValue",0L)) );
    deviceBillPrepay.setIncludeSmsValue(String.valueOf(valueMap.getOrDefault("includeSmsValue",0L)));
    deviceBillPrepay.setIncludeSmsMoValue(String.valueOf(valueMap.getOrDefault("includeSmsMoValue",0L)));
    deviceBillPrepay.setIncludeSmsMtValue(String.valueOf(valueMap.getOrDefault("includeSmsMtValue",0L)));
    deviceBillPrepay.setIncludeVoiceValue(String.valueOf(valueMap.getOrDefault("includeVoiceValue",0L)));
    deviceBillPrepay.setIncludeVoiceMoValue(String.valueOf(valueMap.getOrDefault("includeVoiceMoValue",0L)));
    deviceBillPrepay.setIncludeVoiceMtValue(String.valueOf(valueMap.getOrDefault("includeVoiceMtValue",0L)));
    //共享部分
    if (planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE ||
        planType == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE) {
      valueMap.clear();
      List<ResIncludeShareTurn> resIncludeAgileShareTruns =
          deviceBillContainer.getResIncludeAgileShareTrunList(tpInsId);
      BillUtil.getPrepayInclude(resIncludeAgileShareTruns,paramContainer,valueMap);
      if(valueMap.containsKey("includeDataValue")){
        deviceBillPrepay.setIncludeDataValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeSmsValue")){
        deviceBillPrepay.setIncludeSmsValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeSmsMoValue")){
        deviceBillPrepay.setIncludeSmsMoValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeSmsMtValue")){
        deviceBillPrepay.setIncludeSmsMtValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeVoiceValue")){
        deviceBillPrepay.setIncludeVoiceValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeVoiceMoValue")){
        deviceBillPrepay.setIncludeVoiceMoValue(BillEnum.SHARE_VALUE);
      }
      if(valueMap.containsKey("includeVoiceMtValue")){
        deviceBillPrepay.setIncludeVoiceMtValue(BillEnum.SHARE_VALUE);
      }
    }
    //累积量信息
    List<UsedAddDevice> usedAddDevices = deviceBillContainer.getUsedAddDeviceList(tpInsId);
    valueMap.clear();
    BillUtil.getAddValue(usedAddDevices,valueMap);

    deviceBillPrepay.setTermDataUsage(valueMap.getOrDefault("termDataUsage",0L));
    deviceBillPrepay.setCurrPeroidData(valueMap.getOrDefault("currPeroidData",0L));
    deviceBillPrepay.setTermSmsUsage(valueMap.getOrDefault("termSmsUsage",0L));
    deviceBillPrepay.setCurrPeroidSms(valueMap.getOrDefault("currPeroidSms",0L));
    deviceBillPrepay.setCurrPeroidSmsMo(valueMap.getOrDefault("currPeroidSmsMo",0L));
    deviceBillPrepay.setCurrPeroidSmsMt(valueMap.getOrDefault("currPeroidSmsMt",0L));
    deviceBillPrepay.setTermVoiceUsage(valueMap.getOrDefault("termVoiceUsage",0L));
    deviceBillPrepay.setCurrPeroidVoice(valueMap.getOrDefault("currPeroidVoice",0L));
    deviceBillPrepay.setCurrPeroidVoiceMo(valueMap.getOrDefault("currPeroidVoiceMo",0L));
    deviceBillPrepay.setCurrPeroidVoiceMt(valueMap.getOrDefault("currPeroidVoiceMt",0L));

  }

  /**
   * 设备账单生成
   */
  private static void setDeviceBillMonth(final ParamContainer paramContainer,
                                         final DeviceInfoContainer deviceInfoContainer,
                                         DeviceBillContainer deviceBillContainer) {

    DeviceBill deviceBill = new DeviceBill();

    CycleBean cycleBean = paramContainer.getCycleBean();
    int cycleId = cycleBean.getCycleId();
    long acctId = deviceInfoContainer.getDeviceBean().getAcctId();
    long deviceId = deviceInfoContainer.getDeviceBean().getDeviceId();
    long subAcctId = deviceInfoContainer.getDeviceBean().getSubAcctId();

    int planId = 0;
    int standradPlanId = 0;

    deviceInfoContainer.getDeviceRatePlanBeanList().sort(Comparator.comparing(DeviceRatePlanBean::getStartTime));

    for (DeviceRatePlanBean deviceRatePlanBean : deviceInfoContainer.getDeviceRatePlanBeanList()) {
      if (deviceRatePlanBean.getStartTime().getTime() < cycleBean.getCycEndTime().getTime() &&
          deviceRatePlanBean.getEndTime().getTime() > cycleBean.getCycStartTime().getTime()) {

        if(deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT  ||
           deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_ADD    ||
           deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PILE){
          continue;
        }

        if (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE   ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE) {
          planId = deviceRatePlanBean.getPlanId();
        }

        if (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE     ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE   ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE    ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE  ||
            deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
          standradPlanId = deviceRatePlanBean.getPlanId();
        }
      }
    }
    int status = deviceInfoContainer.getRateType();

    int prepayTermNums = 0;
    double usageProrationIndex = 0;
    long orderCharge = 0;
    long shareCharge = 0;
    int activeEvents = 0;
    long eventCharge = 0;
    for (DeviceBillOrder deviceBillOrder : deviceBillContainer.getDeviceBillOrderList()) {
      if (deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT) {
        activeEvents += 1;
        eventCharge += deviceBillOrder.getFee();
      }
      if (deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
        orderCharge += deviceBillOrder.getFee();
        usageProrationIndex = deviceBillOrder.getOrderNum();
      }

      if (deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE ||
          deviceBillOrder.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
        prepayTermNums += deviceBillOrder.getOrderNum();
      }
    }
    long dataValue = 0;
    long smsValue = 0;
    long smsMoValue = 0;
    long smsMtValue = 0;
    long platformSmsValue = InfoDao.getDevicePlatSmsValue(paramContainer.getCycleBean(),acctId,deviceId);
    long voiceValue = 0;
    long voiceMoValue = 0;
    long voiceMtValue = 0;

    List<UsedAddDevice> usedAddDevices = deviceBillContainer.getUsedAddDeviceList();
    if (usedAddDevices != null && !usedAddDevices.isEmpty()) {
      for (UsedAddDevice usedAddDevice : usedAddDevices) {
        if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_DATA) {
          dataValue += usedAddDevice.getCurrValue();
        } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_SMS) {
          smsValue += usedAddDevice.getCurrValue();
          smsMoValue += usedAddDevice.getMoValue();
          smsMtValue += usedAddDevice.getMtValue();
        } else if (usedAddDevice.getBizType() == ParamEnum.BizType.BIZ_TYPE_VOICE) {
          voiceValue += usedAddDevice.getCurrValue();
          voiceMoValue += usedAddDevice.getMoValue();
          voiceMtValue += usedAddDevice.getMtValue();
        }
      }
    }

    deviceBill.setCycleId(cycleId);
    deviceBill.setAcctId(acctId);
    deviceBill.setDeviceId(deviceId);
    deviceBill.setSubAcctId(subAcctId);
    deviceBill.setPlanId(planId);
    deviceBill.setStandradPlanId(standradPlanId);
    deviceBill.setStatus(status);
    deviceBill.setPrepayTermNums(prepayTermNums);
    deviceBill.setUsageProrationIndex(usageProrationIndex);
    deviceBill.setOrderCharge(orderCharge);
    deviceBill.setShareCharge(shareCharge);
    deviceBill.setActiveEvents(activeEvents);
    deviceBill.setEventCharge(eventCharge);
    deviceBill.setDataValue(dataValue);
    deviceBill.setSmsValue(smsValue);
    deviceBill.setSmsMoValue(smsMoValue);
    deviceBill.setSmsMtValue(smsMtValue);
    deviceBill.setVoiceValue(voiceValue);
    deviceBill.setVoiceMoValue(voiceMoValue);
    deviceBill.setVoiceMtValue(voiceMtValue);
    deviceBill.setPlatformSmsValue(platformSmsValue);

    deviceBillContainer.setDeviceBill(deviceBill);
  }

  private static void setSeqId(final long seqId,DeviceBillContainer deviceBillContainer) {

    //多维度账单
    if(deviceBillContainer.getDeviceUsage()!=null){
      deviceBillContainer.getDeviceUsage().setSeqId(seqId);
    }
    if(!deviceBillContainer.getDeviceBillActiveList().isEmpty()){
      deviceBillContainer.getDeviceBillActiveList().forEach(t -> t.setSeqId(seqId));
    }
    if(!deviceBillContainer.getDeviceBillDataList().isEmpty()){
      deviceBillContainer.getDeviceBillDataList().forEach(t -> t.setSeqId(seqId));
    }
    if(!deviceBillContainer.getDeviceBillSmsList().isEmpty()){
      deviceBillContainer.getDeviceBillSmsList().forEach(t -> t.setSeqId(seqId));
    }
    if(!deviceBillContainer.getDeviceBillVoiceList().isEmpty()){
      deviceBillContainer.getDeviceBillVoiceList().forEach(t -> t.setSeqId(seqId));
    }
    if(!deviceBillContainer.getDeviceBillPrepayList().isEmpty()){
      deviceBillContainer.getDeviceBillPrepayList().forEach(t -> t.setSeqId(seqId));
    }
    if(deviceBillContainer.getDeviceBill()!=null){
      deviceBillContainer.getDeviceBill().setSeqId(seqId);
    }

  }

}
