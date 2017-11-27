package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.entity.info.AcctAdjustBeforeBean;
import com.ai.iot.bill.entity.info.AcctBillingGeneralBean;
import com.ai.iot.bill.entity.info.AcctCommitmentsBean;
import com.ai.iot.bill.entity.info.AcctDiscountBean;
import com.ai.iot.bill.entity.info.AcctDiscountGrade;
import com.ai.iot.bill.entity.info.AcctInfoBean;
import com.ai.iot.bill.entity.info.AcctMonthFeeBean;
import com.ai.iot.bill.entity.info.AcctOrderBean;
import com.ai.iot.bill.entity.info.AcctPromiseBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountMemberBean;
import com.ai.iot.bill.entity.info.AcctSmsDiscount;
import com.ai.iot.bill.entity.info.AcctValumeDiscountBean;
import com.ai.iot.bill.entity.info.AppendFeepolicyBean;
import com.ai.iot.bill.entity.info.DeviceBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.DeviceStateBean;
import com.ai.iot.bill.entity.info.SharePoolBean;
import com.ai.iot.bill.entity.usage.UsedAddThirdParty;

import java.io.Serializable;
import java.util.*;

/**
 * 存放账户资料的容器
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctInfoContainer implements Serializable {

  private static final long serialVersionUID = 1205882520407600810L;

  private long dealId; // 处理批次Id
  private long seqId;  // 账单ID
  private AcctInfoBean acctInfoBean = new AcctInfoBean();

  private AcctCommitmentsBean acctCommitmentsBean = new AcctCommitmentsBean(); //计费设置-设备级承诺
  private AcctBillingGeneralBean acctBillingGeneralBean = new AcctBillingGeneralBean(); //账户计费-常规
  private List<AcctOrderBean> acctOrderBeanList = new ArrayList<>(); //账户的订单
  private List<AppendFeepolicyBean> appendFeepolicyBeanList = new ArrayList<>(); //资费追加表

  private AcctPromiseBean acctPromiseBean = new AcctPromiseBean(); //计费设置-每月最低消费或最低用户数
  private AcctValumeDiscountBean acctValumeDiscountBean = new AcctValumeDiscountBean(); //批量折扣
  private List<AcctDiscountBean> acctDiscountBeanList = new ArrayList<>(); //折扣明细
  private List<AcctMonthFeeBean> acctMonthFeeBeanList = new ArrayList<>(); //一次性费用
  private List<AcctAdjustBeforeBean> acctAdjustBeforeBeanList = new ArrayList<>(); //计费调整及其它
  private List<AcctRateDiscountBean> acctRateDiscountBeanList = new ArrayList<>(); //资费组折扣
  private List<AcctRateDiscountMemberBean> acctRateDiscountMemberBeanList = new ArrayList<>(); //资费组构成
  private List<AcctSmsDiscount> acctSmsDiscountList = new ArrayList<>(); //账户短信折扣
  private List<AcctDiscountGrade> acctDiscountGradeList = new ArrayList<>(); //短信折扣档次

  private int platSmsValue = 0; //平台短信的数量
  private List<UsedAddThirdParty> usedAddThirdPartyList = new ArrayList<>(); //第三方付费，总的使用量信息

  private List<SharePoolBean> sharePoolBeanList = new ArrayList<>(); //账户下的预付共享池
  private List<DeviceBean> deviceBeanList = new ArrayList<>(); //账户下的设备
  private List<DeviceRatePlanBean> deviceRatePlanBeanList = new ArrayList<>(); //设备的资费计划信息
  private List<DeviceStateBean> deviceStateBeanList = new ArrayList<>(); //设备的状态信息

  private Map<AcctShare, Set<Long>> monthFixShareMap = new HashMap<>(); //月付固定共享池下的用户列表,
  private Map<AcctShare, Set<Long>> prepayFixShareMap = new HashMap<>(); //预付固定共享池下的用户列表,
  private Map<AcctShare, Set<Long>> monthAgileShareMap = new HashMap<>(); //月付灵活共享池下的用户列表,
  private Map<AcctShare, Set<Long>> prepayAgileShareMap = new HashMap<>(); //预付灵活共享池下的用户列表,

  private Map<Long, Integer> deviceIdMap = new HashMap<>();

  private boolean isRate = false; //账户是否计费

  public AcctInfoContainer() {
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

  public AcctInfoBean getAcctInfoBean() {
    return acctInfoBean;
  }

  public void setAcctInfoBean(AcctInfoBean acctInfoBean) {
    this.acctInfoBean = acctInfoBean;
  }

  public AcctCommitmentsBean getAcctCommitmentsBean() {
    return acctCommitmentsBean;
  }

  public void setAcctCommitmentsBean(AcctCommitmentsBean acctCommitmentsBean) {
    this.acctCommitmentsBean = acctCommitmentsBean;
  }

  public AcctBillingGeneralBean getAcctBillingGeneralBean() {
    return acctBillingGeneralBean;
  }

  public void setAcctBillingGeneralBean(AcctBillingGeneralBean acctBillingGeneralBean) {
    this.acctBillingGeneralBean = acctBillingGeneralBean;
  }

  public AcctPromiseBean getAcctPromiseBean() {
    return acctPromiseBean;
  }

  public void setAcctPromiseBean(AcctPromiseBean acctPromiseBean) {
    this.acctPromiseBean = acctPromiseBean;
  }

  public List<AcctMonthFeeBean> getAcctMonthFeeBeanList() {
    return acctMonthFeeBeanList;
  }

  public void setAcctMonthFeeBeanList(List<AcctMonthFeeBean> acctMonthFeeBeanList) {
    this.acctMonthFeeBeanList = acctMonthFeeBeanList;
  }

  public AcctValumeDiscountBean getAcctValumeDiscountBean() {
    return acctValumeDiscountBean;
  }

  public void setAcctValumeDiscountBean(AcctValumeDiscountBean acctValumeDiscountBean) {
    this.acctValumeDiscountBean = acctValumeDiscountBean;
  }

  public List<AcctDiscountBean> getAcctDiscountBeanList() {
    return acctDiscountBeanList;
  }

  public void setAcctDiscountBeanList(List<AcctDiscountBean> acctDiscountBeanList) {
    this.acctDiscountBeanList = acctDiscountBeanList;
  }

  public List<AcctAdjustBeforeBean> getAcctAdjustBeforeBeanList() {
    return acctAdjustBeforeBeanList;
  }

  public void setAcctAdjustBeforeBeanList(List<AcctAdjustBeforeBean> acctAdjustBeforeBeanList) {
    this.acctAdjustBeforeBeanList = acctAdjustBeforeBeanList;
  }

  public List<DeviceBean> getDeviceBeanList() {
    return deviceBeanList;
  }

  public void setDeviceBeanList(List<DeviceBean> deviceBeanList) {
    this.deviceBeanList = deviceBeanList;
  }

  public List<SharePoolBean> getSharePoolBeanList() {
    return sharePoolBeanList;
  }

  public SharePoolBean getSharePool(long poolId) {
    for (SharePoolBean sharePoolBean : sharePoolBeanList) {
      if (sharePoolBean.getPoolId() == poolId){
        return sharePoolBean;
      }
    }
    return null;
  }

  public void setSharePoolBeanList(List<SharePoolBean> sharePoolBeanList) {
    this.sharePoolBeanList = sharePoolBeanList;
  }

  public List<AppendFeepolicyBean> getAppendFeepolicyBeanList() {
    return appendFeepolicyBeanList;
  }

  public void setAppendFeepolicyBeanList(List<AppendFeepolicyBean> appendFeepolicyBeanList) {
    this.appendFeepolicyBeanList = appendFeepolicyBeanList;
  }

  public Map<AcctShare, Set<Long>> getMonthFixShareMap() {
    return monthFixShareMap;
  }

  public Map<AcctShare, Set<Long>> getPrepayFixShareMap() {
    return prepayFixShareMap;
  }

  public Map<AcctShare, Set<Long>> getMonthAgileShareMap() {
    return monthAgileShareMap;
  }

  public Map<AcctShare, Set<Long>> getPrepayAgileShareMap() {
    return prepayAgileShareMap;
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

  public Map<Long, Integer> getDeviceIdMap() {
    return deviceIdMap;
  }

  public boolean isRateDevice(Long deviceId) {
    return deviceIdMap.containsKey(deviceId);
  }

  public int geteDeviceType(Long deviceId) {
    if (deviceIdMap.containsKey(deviceId)){
      return deviceIdMap.get(deviceId);
    }
    return -1;
  }

  public void addDeviceIdMap(Long deviceId, int rateType) {
    this.deviceIdMap.put(deviceId, rateType);
  }


  public List<AcctRateDiscountBean> getAcctRateDiscountBeanList() {
    return acctRateDiscountBeanList;
  }

  public void setAcctRateDiscountBeanList(List<AcctRateDiscountBean> acctRateDiscountBeanList) {
    this.acctRateDiscountBeanList = acctRateDiscountBeanList;
  }

  public List<AcctRateDiscountMemberBean> getAcctRateDiscountMemberBeanList() {
    return acctRateDiscountMemberBeanList;
  }

  public void setAcctRateDiscountMemberBeanList(List<AcctRateDiscountMemberBean> acctRateDiscountMemberBeanList) {
    this.acctRateDiscountMemberBeanList = acctRateDiscountMemberBeanList;
  }

  public List<AcctSmsDiscount> getAcctSmsDiscountList() {
    return acctSmsDiscountList;
  }

  public void setAcctSmsDiscountList(List<AcctSmsDiscount> acctSmsDiscountList) {
    this.acctSmsDiscountList = acctSmsDiscountList;
  }

  public List<AcctDiscountGrade> getAcctDiscountGradeList() {
    return acctDiscountGradeList;
  }

  public void setAcctDiscountGradeList(List<AcctDiscountGrade> acctDiscountGradeList) {
    this.acctDiscountGradeList = acctDiscountGradeList;
  }

  public List<AcctOrderBean> getAcctOrderBeanList() {
    return acctOrderBeanList;
  }

  public void setAcctOrderBeanList(List<AcctOrderBean> acctOrderBeanList) {
    this.acctOrderBeanList = acctOrderBeanList;
  }

  public boolean isRate() {
    return isRate;
  }

  public void setRate(boolean rate) {
    isRate = rate;
  }

  public int getPlatSmsValue() {
    return platSmsValue;
  }

  public void setPlatSmsValue(int platSmsValue) {
    this.platSmsValue = platSmsValue;
  }

  public List<UsedAddThirdParty> getUsedAddThirdPartyList() {
    return usedAddThirdPartyList;
  }

  public void setUsedAddThirdPartyList(List<UsedAddThirdParty> usedAddThirdPartyList) {
    this.usedAddThirdPartyList = usedAddThirdPartyList;
  }
}
