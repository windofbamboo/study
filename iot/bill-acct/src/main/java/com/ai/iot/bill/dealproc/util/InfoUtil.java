package com.ai.iot.bill.dealproc.util;

import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.dealproc.container.AcctInfoContainer;
import com.ai.iot.bill.dealproc.container.AcctShare;
import com.ai.iot.bill.dealproc.container.DeviceInfoContainer;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.entity.info.AcctCommitmentsBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountMemberBean;
import com.ai.iot.bill.entity.info.DeviceBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.DeviceStateBean;
import com.ai.iot.bill.entity.info.TimeInterface;
import com.ai.iot.bill.entity.param.CycleBean;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**资料工具类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class InfoUtil {

  public static <T extends TimeInterface> void judgeAvtication(final CycleBean cycleBean, List<T> tList) {
    if (tList == null || tList.isEmpty()) {
      return;
    }
    tList.removeIf(t -> !(t.getStartTime().getTime() < t.getEndTime().getTime() &&
                          t.getStartTime().getTime() <= cycleBean.getCycEndTime().getTime() &&
                          t.getEndTime().getTime() >= cycleBean.getCycStartTime().getTime()));
  }

  public static <T extends TimeInterface> void judgeAvtication(final CycleBean cycleBean, T t) {
    if(t == null ) return;
    if(!(t.getStartTime().getTime() < t.getEndTime().getTime() &&
         t.getStartTime().getTime() <= cycleBean.getCycEndTime().getTime() &&
         t.getEndTime().getTime() >= cycleBean.getCycStartTime().getTime())){
      t = null;
    }
  }

  /**
   * 设备状态(收费原因)
   */
  public static int getDeviceRateType(final DeviceBean deviceBean,
                                      final List<DeviceStateBean> deviceStateBeanList,
                                      final AcctCommitmentsBean acctCommitmentsBean,
                                      final CycleBean cycleBean) {

    if (hasNomalStatus(cycleBean.getCycStartTime(), cycleBean.getCycEndTime(), deviceStateBeanList)) {
      return InfoEnum.RateType.NOMAL;
    }
    if(acctCommitmentsBean!=null){
      //激活宽限期判断 //激活时间为空,认为是未激活状态
      if (acctCommitmentsBean.isBillActivationGracePeriod() && (deviceBean.getActivationTime() == null) ) {
        //发货时间+激活宽限天数>=账期结束时间
        if (addDays(deviceBean.getShippedTime(),acctCommitmentsBean.getActivationGracePeriod()).getTime() < cycleBean.getCycEndTime().getTime()) {
          return InfoEnum.RateType.BILL_ACTIVATION_GRACE_PERIOD;//激活宽限期
        }
      }
      //最短激活期判断
      if (acctCommitmentsBean.isBillMininumActivationTerm() && deviceBean.getActivationTime() !=null) {
        //激活时间+最短激活月份数>账期开始时间
        if (DateUtil.addMonths(deviceBean.getActivationTime(),acctCommitmentsBean.getMinActivationMonth()).getTime() > cycleBean.getCycStartTime().getTime()) {
          return InfoEnum.RateType.BILL_MININUM_ACTIVATION_TERM;//最短激活期
        }
      }
    }
    return -1;
  }

  /**
   * 判断是否有正常状态
   */
  private static boolean hasNomalStatus(final Date startTime,
                                       final Date endTime,
                                       final List<DeviceStateBean> deviceStateBeanList) {

    if (deviceStateBeanList == null || deviceStateBeanList.isEmpty()) {
      return false;
    }
    return deviceStateBeanList.stream().anyMatch(t -> t.getStartTime().getTime() < endTime.getTime() &&
                                                      t.getEndTime().getTime() > startTime.getTime() &&
                                                      InfoEnum.DeviceState.ACTIVE.equals(t.getStateCode()) );
  }

  /**
   * 转换acctInfoContainer为DeviceInfoContainer
   */
  public static List<DeviceInfoContainer> getDeviceInfo(AcctInfoContainer acctInfoContainer) {

    List<DeviceInfoContainer> deviceInfoContainerList = new ArrayList<>();

    Map<Long, List<DeviceRatePlanBean>> deviceRatePlanMap =
        acctInfoContainer.getDeviceRatePlanBeanList().stream().collect(groupingBy(DeviceRatePlanBean::getDeviceId));
    Map<Long, List<DeviceStateBean>> deviceStateMap =
        acctInfoContainer.getDeviceStateBeanList().stream().collect(groupingBy(DeviceStateBean::getDeviceId));
    for (DeviceBean deviceBean : acctInfoContainer.getDeviceBeanList()) {
      int rateType = acctInfoContainer.geteDeviceType(deviceBean.getDeviceId());
      if (rateType > 0) {

        DeviceInfoContainer deviceInfoContainer = new DeviceInfoContainer();

        List<DeviceRatePlanBean> deviceRatePlanBeanList = deviceRatePlanMap.get(deviceBean.getDeviceId());
        List<DeviceStateBean> deviceStateBeanList = deviceStateMap.get(deviceBean.getDeviceId());

        deviceInfoContainer.setDeviceBean(deviceBean);
        deviceInfoContainer.setDeviceStateBeanList(deviceStateBeanList);
        deviceInfoContainer.setDeviceRatePlanBeanList(deviceRatePlanBeanList);
        deviceInfoContainer.setAcctBillingGeneralBean(acctInfoContainer.getAcctBillingGeneralBean());
//        deviceInfoContainer.setAcctCommitmentsBean(acctInfoContainer.getAcctCommitmentsBean());
        deviceInfoContainer.setRateType(rateType);
        deviceInfoContainer.setSharePoolBeanList(acctInfoContainer.getSharePoolBeanList());
        deviceInfoContainer.setDealId(acctInfoContainer.getDealId());
        deviceInfoContainer.setSeqId(acctInfoContainer.getSeqId());
        deviceInfoContainerList.add(deviceInfoContainer);
      }
    }
    return deviceInfoContainerList;
  }

  /**
   * 获取资费计划组下面的资费计划ID
   */
  public static Set<Integer> getAcctRateDiscountMember(final int rateGroup,
                                                       final List<AcctRateDiscountMemberBean> acctRateDiscountMemberBeanList) {

    if (acctRateDiscountMemberBeanList == null || acctRateDiscountMemberBeanList.isEmpty()) {
      return Collections.emptySet();
    }
    return acctRateDiscountMemberBeanList.stream().filter(tRecord -> tRecord.getRateGroup() == rateGroup)
                                                  .map(AcctRateDiscountMemberBean::getPlanId)
                                                  .collect(Collectors.toSet());
  }

  /**
   * 计算天数
   */
  public static int getDays(final Date startDate, final Date endDate) {

    if(startDate.getTime()>endDate.getTime()){
      return 0;
    }

    String sTime = DateUtil.getCurrentDateTime(startDate,DateUtil.YYYYMMDD);
    String eTime = DateUtil.getCurrentDateTime(endDate,DateUtil.YYYYMMDD);

    Date sDate = DateUtil.string2Date(String.valueOf(sTime),DateUtil.YYYYMMDD);
    Date eDate = DateUtil.string2Date(String.valueOf(eTime),DateUtil.YYYYMMDD);

    long diffSeconds = DateUtil.diffSeconds(eDate,sDate);
    return (int) (diffSeconds / 86400)+1;
  }

  /**
   * 增加天数
   */
  public static java.sql.Date addDays(final java.sql.Date startTime, final int days){

    long time=startTime.getTime()+days*86400L*1000L;
    return new java.sql.Date(time);
  }

  /**
   * 获取共享的设备列表
   */
  public static Set<Long> getDeviceSet(final long acctId,
                                       final int planId,
                                       final AcctInfoContainer acctInfoContainer){

    Set<Long> deviceSet = new HashSet<>();
    for(Map.Entry<AcctShare, Set<Long>> entry:acctInfoContainer.getMonthFixShareMap().entrySet()){
      AcctShare acctShare = entry.getKey();
      if(acctShare.getAcctId() == acctId && acctShare.getPlanId() == planId){
        deviceSet.addAll(entry.getValue());
      }
    }
    for(Map.Entry<AcctShare, Set<Long>> entry:acctInfoContainer.getMonthAgileShareMap().entrySet()){
      AcctShare acctShare = entry.getKey();
      if(acctShare.getAcctId() == acctId && acctShare.getPlanId() == planId){
        deviceSet.addAll(entry.getValue());
      }
    }
    for(Map.Entry<AcctShare, Set<Long>> entry:acctInfoContainer.getPrepayFixShareMap().entrySet()){
      AcctShare acctShare = entry.getKey();
      if(acctShare.getAcctId() == acctId && acctShare.getPlanId() == planId){
        deviceSet.addAll(entry.getValue());
      }
    }
    for(Map.Entry<AcctShare, Set<Long>> entry:acctInfoContainer.getPrepayAgileShareMap().entrySet()){
      AcctShare acctShare = entry.getKey();
      if(acctShare.getAcctId() == acctId && acctShare.getPlanId() == planId){
        deviceSet.addAll(entry.getValue());
      }
    }
    return deviceSet;
  }


}
