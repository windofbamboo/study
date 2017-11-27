package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.info.AcctBillingGeneralBean;
import com.ai.iot.bill.entity.info.AcctCommitmentsBean;
import com.ai.iot.bill.entity.info.DeviceBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.DeviceStateBean;
import com.ai.iot.bill.entity.info.SharePoolBean;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 存放设备资料
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceInfoContainer implements Serializable {

  private static final long serialVersionUID = -8065656932796794161L;
  private long dealId; // 处理批次Id
  private long seqId;  // 账单ID
  private DeviceBean deviceBean = new DeviceBean(); //设备信息
  private List<DeviceRatePlanBean> deviceRatePlanBeanList = new ArrayList<>(); //设备的资费计划信息
  private List<DeviceStateBean> deviceStateBeanList = new ArrayList<>(); //设备的状态信息

//  private AcctCommitmentsBean acctCommitmentsBean = new AcctCommitmentsBean(); //计费设置-设备级承诺
  private AcctBillingGeneralBean acctBillingGeneralBean = new AcctBillingGeneralBean(); //账户计费-常规
  private List<SharePoolBean> sharePoolBeanList = new ArrayList<>(); //账户下的预付共享池

  private int rateType; //设备收费原因:激活、激活宽限期、最短激活期

  public DeviceInfoContainer() {
  }

  @Override
  public String toString() {
    return "DeviceInfoContainer{" +
            "deviceBean=" + deviceBean +
            ", deviceRatePlanBeanList=" + deviceRatePlanBeanList +
            ", deviceStateBeanList=" + deviceStateBeanList +
//            ", acctCommitmentsBean=" + acctCommitmentsBean +
            ", acctBillingGeneralBean=" + acctBillingGeneralBean +
            ", sharePoolBeanList=" + sharePoolBeanList +
            ", rateType=" + rateType +
            '}';
  }

  public DeviceRatePlanBean getDeviceRatePlan(long orderId) {

    if (deviceRatePlanBeanList == null || deviceRatePlanBeanList.isEmpty()) {
      return null;
    }
    for (DeviceRatePlanBean deviceRatePlanBean : deviceRatePlanBeanList) {
      if (deviceRatePlanBean.getOrderId() == orderId){
        return deviceRatePlanBean;
      }
    }
    return null;
  }

  public DeviceRatePlanBean getActivationPlan(Date activationTime) {

    for (DeviceRatePlanBean deviceRatePlanBean : deviceRatePlanBeanList) {
      if (deviceRatePlanBean.getStartTime().getTime() <= activationTime.getTime() &&
          deviceRatePlanBean.getEndTime().getTime() >= activationTime.getTime() &&
          (deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE     ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE    ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE   ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE  ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
          deviceRatePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE)) {
        return deviceRatePlanBean;
      }
    }
    return null;
  }


  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public long getDealId() {
    return dealId;
  }

  public void setDealId(long dealId) {
    this.dealId = dealId;
  }

  public long getSeqId() {
    return seqId;
  }

  public void setSeqId(long seqId) {
    this.seqId = seqId;
  }

  public DeviceBean getDeviceBean() {
    return deviceBean;
  }

  public void setDeviceBean(DeviceBean deviceBean) {
    this.deviceBean = deviceBean;
  }

  public List<DeviceRatePlanBean> getDeviceRatePlanBeanList() {
    return deviceRatePlanBeanList;
  }

  public void setDeviceRatePlanBeanList(List<DeviceRatePlanBean> deviceRatePlanBeanList) {
    this.deviceRatePlanBeanList = deviceRatePlanBeanList;
  }

  public List<DeviceStateBean> getDeviceStateBeanList() {
    return deviceStateBeanList;
  }

  public void setDeviceStateBeanList(List<DeviceStateBean> deviceStateBeanList) {
    this.deviceStateBeanList = deviceStateBeanList;
  }

//  public AcctCommitmentsBean getAcctCommitmentsBean() {
//    return acctCommitmentsBean;
//  }
//
//  public void setAcctCommitmentsBean(AcctCommitmentsBean acctCommitmentsBean) {
//    this.acctCommitmentsBean = acctCommitmentsBean;
//  }

  public AcctBillingGeneralBean getAcctBillingGeneralBean() {
    return acctBillingGeneralBean;
  }

  public void setAcctBillingGeneralBean(AcctBillingGeneralBean acctBillingGeneralBean) {
    this.acctBillingGeneralBean = acctBillingGeneralBean;
  }

  public List<SharePoolBean> getSharePoolBeanList() {
    return sharePoolBeanList;
  }

  public void setSharePoolBeanList(List<SharePoolBean> sharePoolBeanList) {
    this.sharePoolBeanList = sharePoolBeanList;
  }

  public int getRateType() {
    return rateType;
  }

  public void setRateType(int rateType) {
    this.rateType = rateType;
  }
}
