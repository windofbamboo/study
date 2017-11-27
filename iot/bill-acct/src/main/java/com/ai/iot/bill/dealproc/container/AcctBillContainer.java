package com.ai.iot.bill.dealproc.container;

import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.computebill.AcctBill2Bss;
import com.ai.iot.bill.entity.computebill.AcctBillOrder;
import com.ai.iot.bill.entity.computebill.AcctBillUsage;
import com.ai.iot.bill.entity.computebill.BillAcct;
import com.ai.iot.bill.entity.computebill.BillTrackAcct;
import com.ai.iot.bill.entity.computebill.DeviceBillActivation;
import com.ai.iot.bill.entity.computebill.DeviceBillOrder;
import com.ai.iot.bill.entity.computebill.DeviceBillUsage;
import com.ai.iot.bill.entity.multibill.AcctBillAdd;
import com.ai.iot.bill.entity.multibill.AcctBillDiscount;
import com.ai.iot.bill.entity.multibill.AcctBillOther;
import com.ai.iot.bill.entity.multibill.AcctBillSum;
import com.ai.iot.bill.entity.multibill.AdjustBill;
import com.ai.iot.bill.entity.multibill.DeviceBill;
import com.ai.iot.bill.entity.multibill.DeviceBillActive;
import com.ai.iot.bill.entity.multibill.DeviceBillData;
import com.ai.iot.bill.entity.multibill.DeviceBillPrepay;
import com.ai.iot.bill.entity.multibill.DeviceBillSms;
import com.ai.iot.bill.entity.multibill.DeviceBillVoice;
import com.ai.iot.bill.entity.multibill.DeviceUsage;
import com.ai.iot.bill.entity.multibill.PlanBill;
import com.ai.iot.bill.entity.multibill.PlanZoneBill;
import com.ai.iot.bill.entity.multibill.RateGroupDiscount;
import com.ai.iot.bill.entity.res.ResIncludeDevice;
import com.ai.iot.bill.entity.res.ResIncludePile;
import com.ai.iot.bill.entity.res.ResIncludePool;
import com.ai.iot.bill.entity.res.ResIncludeShare;
import com.ai.iot.bill.entity.res.ResIncludeShareTurn;
import com.ai.iot.bill.entity.res.ResUsedDevice;
import com.ai.iot.bill.entity.res.ResUsedPoolTotal;
import com.ai.iot.bill.entity.res.ResUsedShareTotal;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddPoolTotal;
import com.ai.iot.bill.entity.usage.UsedAddShare;
import com.ai.iot.bill.entity.usage.UsedAddShareDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 账户级账单容器
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctBillContainer implements Serializable {

  private static final long serialVersionUID = -6576696071071249549L;
  //设备级信息

  private List<ResIncludeShareTurn> resIncludeAgileShareTrunList = new ArrayList<>(); //灵活共享资费计划，包含的用量转移信息(含自身、含堆叠)
  private List<ResIncludeShareTurn> resIncludeFixShareTurnList = new ArrayList<>(); //固定共享资费计划，包含的用量转移信息(含自身、堆叠)

  private List<UsedAddDevice> usedAddDeviceList = new ArrayList<>(); //设备的使用量信息(累积量信息)
  private List<UsedAddShareDetail> usedAddShareDetailList = new ArrayList<>(); //共享的资费，设备的使用量信息

  private List<DeviceBillUsage> deviceBillUsageList = new ArrayList<>(); //设备的用量超额费用
  private List<DeviceBillOrder> deviceBillOrderList = new ArrayList<>(); //设备的资费计划费用
  private List<DeviceBillActivation> deviceBillActivationList = new ArrayList<>(); //  设备的激活费用
  //设备级多维度账单
  private List<DeviceUsage> deviceUsageList = new ArrayList<>();
//  private List<DeviceBillActive> deviceBillActiveList = new ArrayList<>();
  private List<DeviceBillData> deviceBillDataList = new ArrayList<>();
//  private List<DeviceBillSms> deviceBillSmsList = new ArrayList<>();
//  private List<DeviceBillVoice> deviceBillVoiceList = new ArrayList<>();
//  private List<DeviceBillPrepay> deviceBillPrepayList = new ArrayList<>();
  private List<DeviceBill> deviceBillList = new ArrayList<>();

  //账户级信息
  //累积量信息
  private List<UsedAddShare> usedAddShareList = new ArrayList<>(); //预付灵活共享,月付灵活共享,月付固定共享的累积量，总的使用量信息
  private List<UsedAddPoolTotal> usedAddPoolTotalList = new ArrayList<>(); //预付固定共享池，总的使用量信息
  //用量部分
  private List<ResIncludeShare> resIncludeAgileShareList = new ArrayList<>(); //灵活共享中，总的共享量(以账期为单位)
  private List<ResIncludeShare> resIncludeFixShareList = new ArrayList<>(); //月付固定共享，总的共享量(以账期为单位)
  private List<ResIncludePool> resIncludePoolList = new ArrayList<>(); //预付固定共享池，总的共享量

  //核减信息
  private List<ResUsedShareTotal> resUsedShareTotalList = new ArrayList<>(); //预付灵活共享,月付灵活共享,月付固定共享,用量核减信息-总量信息
  private List<ResUsedPoolTotal> resUsedPoolTotalList = new ArrayList<>(); //预付固定共享,用量核减信息-总量信息

  //费用部分
  private List<DeviceBillOrder> fixShareDeviceBillOrderList = new ArrayList<>(); //固定共享资费的订户费 -- 重置费用使用
  private List<DeviceBill> fixShareDeviceBillList = new ArrayList<>(); //固定共享资费的 设备账单 -- 重置费用使用

  private List<AcctBillOrder> acctBillOrderList = new ArrayList<>(); //资费计划的账户费用
  private List<AcctBillUsage> acctBillUsageList = new ArrayList<>(); //用量的批价费用
  private List<BillTrackAcct> billTrackAcctList = new ArrayList<>(); //收费轨迹
  private List<BillAcct> billAcctList = new ArrayList<>(); //总费用
  private List<AcctBill2Bss> acctBill2BssList = new ArrayList<>(); //接口账单
  private int orderNo = 0;
  private int discount = 0; //批量折扣的折扣率
  private int platSmsLevel = 0; //平台下发短信批价等级

  //账户级多维度账单
  private List<PlanBill> planBillList = new ArrayList<>();
  private List<PlanZoneBill> planZoneBillList = new ArrayList<>();

  private List<AcctBillAdd> acctBillAddList = new ArrayList<>(); //追加资费的费用
  private List<RateGroupDiscount> rateGroupDiscountList = new ArrayList<>(); //资费组折扣的记录
  private List<AcctBillOther> acctBillOtherList = new ArrayList<>(); //其它费用的记录
  private List<AcctBillDiscount> acctBillDiscountList = new ArrayList<>(); //批量折扣的记录
  private List<AdjustBill> adjustBillList = new ArrayList<>(); // 调账账单
  private AcctBillSum acctBillSum = new AcctBillSum(); // 账户总账单

  public AcctBillContainer() {
  }

  public long getSumFee() {
    long fee = 0;
    for (BillTrackAcct billTrackAcct : billTrackAcctList) {
      fee += billTrackAcct.getFee();
    }
    return fee;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public List<DeviceBillUsage> getDeviceBillUsageList() {
    return deviceBillUsageList;
  }

  public void setDeviceBillUsageList(List<DeviceBillUsage> deviceBillUsageList) {
    if(deviceBillUsageList!=null && !deviceBillUsageList.isEmpty()){
      this.deviceBillUsageList = deviceBillUsageList;
    }
  }

  public List<DeviceBillOrder> getDeviceBillOrderList(int planVersionId, Set<Long> tSet) {

    List<DeviceBillOrder> newDeviceBillOrderList = new ArrayList<>();
    for (DeviceBillOrder t : deviceBillOrderList) {
      if (t.getPlanVersionId() == planVersionId && tSet.contains(t.getDeviceId())) {
        newDeviceBillOrderList.add(t);
      }
    }
    return newDeviceBillOrderList;
  }

  public List<DeviceBillOrder> getDeviceBillOrderList() {
    return deviceBillOrderList;
  }

  public void setDeviceBillOrderList(List<DeviceBillOrder> deviceBillOrderList) {
    if(deviceBillOrderList!=null && !deviceBillOrderList.isEmpty()){
      this.deviceBillOrderList = deviceBillOrderList;
    }
  }

  public List<DeviceBillActivation> getDeviceBillActivationList() {
    return deviceBillActivationList;
  }

  public void setDeviceBillActivationList(List<DeviceBillActivation> deviceBillActivationList) {
    if(deviceBillActivationList!=null && !deviceBillActivationList.isEmpty()){
      this.deviceBillActivationList = deviceBillActivationList;
    }
  }

  public List<UsedAddDevice> getUsedAddDeviceList() {
    return usedAddDeviceList;
  }

  public void setUsedAddDeviceList(List<UsedAddDevice> usedAddDeviceList) {
    if(usedAddDeviceList!=null && !usedAddDeviceList.isEmpty()){
      this.usedAddDeviceList = usedAddDeviceList;
    }
  }

  public List<UsedAddShareDetail> getUsedAddShareDetailList() {
    return usedAddShareDetailList;
  }

  public void setUsedAddShareDetailList(List<UsedAddShareDetail> usedAddShareDetailList) {
    if(usedAddShareDetailList!=null && !usedAddShareDetailList.isEmpty()){
      this.usedAddShareDetailList = usedAddShareDetailList;
    }
  }

  public List<ResIncludeShareTurn> getResIncludeAgileShareTrunList() {
    return resIncludeAgileShareTrunList;
  }

  public void setResIncludeAgileShareTrunList(List<ResIncludeShareTurn> resIncludeAgileShareTrunList) {
    if(resIncludeAgileShareTrunList!=null && !resIncludeAgileShareTrunList.isEmpty()){
      this.resIncludeAgileShareTrunList = resIncludeAgileShareTrunList;
    }
  }

  public List<ResIncludeShareTurn> getResIncludeFixShareTurnList() {
    return resIncludeFixShareTurnList;
  }

  public void setResIncludeFixShareTurnList(List<ResIncludeShareTurn> resIncludeShareTurnList) {
    if(resIncludeShareTurnList!=null && !resIncludeShareTurnList.isEmpty()){
      this.resIncludeFixShareTurnList = resIncludeShareTurnList;
    }
  }

  public List<UsedAddShare> getUsedAddShareList() {
    return usedAddShareList;
  }

  public List<UsedAddPoolTotal> getUsedAddPoolTotalList() {
    return usedAddPoolTotalList;
  }

  public List<ResIncludeShare> getResIncludeAgileShareList() {
    return resIncludeAgileShareList;
  }

  public void setResIncludeAgileShareList(List<ResIncludeShare> resIncludeAgileShareList) {
    if(resIncludeAgileShareList!=null && !resIncludeAgileShareList.isEmpty()){
      this.resIncludeAgileShareList = resIncludeAgileShareList;
    }
  }

  public void addResIncludeAgileShareValue(long planVersionId,RateBill rateBill) {

    for (ResIncludeShare resIncludeShare : this.resIncludeAgileShareList) {
      if (resIncludeShare.getPlanVersionId() == planVersionId &&
          resIncludeShare.getBillId() == rateBill.getBillId()) {
        resIncludeShare.setValue(resIncludeShare.getValue() + rateBill.getValue());
      }
    }
  }


  public List<ResIncludeShare> getResIncludeFixShareList() {
    return resIncludeFixShareList;
  }

  public void setResIncludeFixShareList(List<ResIncludeShare> resIncludeFixShareList) {
    if(resIncludeFixShareList!=null && !resIncludeFixShareList.isEmpty()){
      this.resIncludeFixShareList = resIncludeFixShareList;
    }
  }

  public void addResIncludeFixShare(ResIncludeShare resIncludeShare) {

    boolean match=false;
    for (ResIncludeShare resIncludeFixShare : this.resIncludeFixShareList) {
      if (resIncludeFixShare.getPlanVersionId() == resIncludeShare.getPlanVersionId() &&
          resIncludeFixShare.getBillId() == resIncludeShare.getBillId()) {
        resIncludeFixShare.setValue(resIncludeFixShare.getValue() + resIncludeShare.getValue());
        match=true;
        break;
      }
    }
    if(!match){
      this.resIncludeFixShareList.add(resIncludeShare);
    }
  }

  public void addResIncludeFixShareValue(long planVersionId,RateBill rateBill) {

    for (ResIncludeShare resIncludeFixShare : this.resIncludeFixShareList) {
      if (resIncludeFixShare.getPlanVersionId() == planVersionId &&
          resIncludeFixShare.getBillId() == rateBill.getBillId()) {
        resIncludeFixShare.setValue(resIncludeFixShare.getValue() + rateBill.getValue());
        break;
      }
    }
  }

  public ResIncludeShare getResIncludeShare(final long acctId,
                                            final int planVersionId,
                                            final int billId,
                                            final int planType) {

    ResIncludeShare resIncludeShare = new ResIncludeShare(acctId, planVersionId, billId);
    if (planType == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
        planType == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
      int a = this.resIncludeAgileShareList.indexOf(resIncludeShare);
      if (a != -1) {
        return this.resIncludeAgileShareList.get(a);
      }
    } else if (planType == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE) {
      int a = this.resIncludeFixShareList.indexOf(resIncludeShare);
      if (a != -1) {
        return this.resIncludeFixShareList.get(a);
      }
    }
    return null;
  }

  public List<ResIncludePool> getResIncludePoolList() {
    return resIncludePoolList;
  }

  public ResIncludePool getResIncldePool(final long acctId, final long poolId, final int billId) {

    ResIncludePool resIncludePool = new ResIncludePool(acctId, poolId, billId);
    int a = this.resIncludePoolList.indexOf(resIncludePool);
    if (a != -1) {
      return this.resIncludePoolList.get(a);
    }
    return null;
  }

  public void addResIncludePoolList(ResIncludePool resIncludePool) {
    if(resIncludePool!=null){
      this.resIncludePoolList.add(resIncludePool);
    }
  }

  public List<ResUsedShareTotal> getResUsedShareTotalList() {
    return resUsedShareTotalList;
  }

  public void addResUsedShareTotalList(ResUsedShareTotal resUsedShareTotal) {
    if(resUsedShareTotal!=null){
      this.resUsedShareTotalList.add(resUsedShareTotal);
    }
  }


  public List<ResUsedPoolTotal> getResUsedPoolTotalList() {
    return resUsedPoolTotalList;
  }

  public void addResUsedPoolTotalList(ResUsedPoolTotal resUsedPoolTotal) {
    if(resUsedPoolTotal!=null){
      this.resUsedPoolTotalList.add(resUsedPoolTotal);
    }
  }

  public List<DeviceBillOrder> getFixShareDeviceBillOrderList() {
    return fixShareDeviceBillOrderList;
  }

  public void addFixShareDeviceBillOrderList(DeviceBillOrder fixShareDeviceBillOrder) {
    if(fixShareDeviceBillOrder!=null){
      this.fixShareDeviceBillOrderList.add(fixShareDeviceBillOrder);
    }
  }

  public List<DeviceBill> getFixShareDeviceBillList() {
    return fixShareDeviceBillList;
  }

  public void addFixShareDeviceBillList(DeviceBill fixShareDeviceBill) {
    if(fixShareDeviceBill!=null){
      this.fixShareDeviceBillList.add(fixShareDeviceBill);
    }
  }

  public List<AcctBillOrder> getAcctBillOrderList() {
    return acctBillOrderList;
  }

  public void addAcctBillOrderList(AcctBillOrder acctBillOrder) {
    if(acctBillOrder!=null){
      this.acctBillOrderList.add(acctBillOrder);
    }
  }

  public List<AcctBillUsage> getAcctBillUsageList() {
    return acctBillUsageList;
  }

  public List<BillTrackAcct> getBillTrackAcctList() {
    return billTrackAcctList;
  }

  public void addBillTrackAcctList(BillTrackAcct billTrackAcct) {

    this.orderNo += 1;
    billTrackAcct.setOrderNo(this.orderNo);
    this.billTrackAcctList.add(billTrackAcct);
  }

  public List<BillAcct> getBillAcctList() {
    return billAcctList;
  }

  public void setBillAcctList(List<BillAcct> billAcctList) {
    if(billAcctList!=null && !billAcctList.isEmpty()){
      this.billAcctList = billAcctList;
    }
  }

  public int getDiscount() {
    return discount;
  }

  public void setDiscount(int discount) {
    this.discount = discount;
  }

  public List<AcctBillAdd> getAcctBillAddList() {
    return acctBillAddList;
  }

  public void addAcctBillAddList(AcctBillAdd acctBillAdd) {
    if(acctBillAdd!=null)
    this.acctBillAddList.add(acctBillAdd);
  }

  public List<AcctBillDiscount> getAcctBillDiscountList() {
    return acctBillDiscountList;
  }

  public void addAcctBillDiscountList(AcctBillDiscount acctBillDiscount) {
    if(acctBillDiscount!=null){
      this.acctBillDiscountList.add(acctBillDiscount);
    }
  }

  public List<AcctBillOther> getAcctBillOtherList() {
    return acctBillOtherList;
  }

  public List<DeviceUsage> getDeviceUsageList() {
    return deviceUsageList;
  }

  public void setDeviceUsageList(List<DeviceUsage> deviceUsageList) {
    this.deviceUsageList = deviceUsageList;
  }

//  public List<DeviceBillActive> getDeviceBillActiveList() {
//    return deviceBillActiveList;
//  }
//
//  public void setDeviceBillActiveList(List<DeviceBillActive> deviceBillActiveList) {
//    this.deviceBillActiveList = deviceBillActiveList;
//  }

  public List<DeviceBill> getDeviceBillList() {
    return deviceBillList;
  }

  public void setDeviceBillList(List<DeviceBill> deviceBillList) {
    this.deviceBillList = deviceBillList;
  }

  public List<DeviceBillData> getDeviceBillDataList() {
    return deviceBillDataList;
  }

  public void setDeviceBillDataList(List<DeviceBillData> deviceBillDataList) {
    this.deviceBillDataList = deviceBillDataList;
  }

//  public List<DeviceBillSms> getDeviceBillSmsList() {
//    return deviceBillSmsList;
//  }
//
//  public void setDeviceBillSmsList(List<DeviceBillSms> deviceBillSmsList) {
//    this.deviceBillSmsList = deviceBillSmsList;
//  }

//  public List<DeviceBillVoice> getDeviceBillVoiceList() {
//    return deviceBillVoiceList;
//  }
//
//  public void setDeviceBillVoiceList(List<DeviceBillVoice> deviceBillVoiceList) {
//    this.deviceBillVoiceList = deviceBillVoiceList;
//  }

//  public List<DeviceBillPrepay> getDeviceBillPrepayList() {
//    return deviceBillPrepayList;
//  }
//
//  public void setDeviceBillPrepayList(List<DeviceBillPrepay> deviceBillPrepayList) {
//    this.deviceBillPrepayList = deviceBillPrepayList;
//  }

  public List<AdjustBill> getAdjustBillList() {
    return adjustBillList;
  }

  public AcctBillSum getAcctBillSum() {
    return acctBillSum;
  }

  public void setAcctBillSum(AcctBillSum acctBillSum) {
    this.acctBillSum = acctBillSum;
  }

  public List<PlanBill> getPlanBillList() {
    return planBillList;
  }

  public void setPlanBillList(List<PlanBill> planBillList) {
    this.planBillList = planBillList;
  }

  public List<PlanZoneBill> getPlanZoneBillList() {
    return planZoneBillList;
  }

  public void addPlanZoneBillList(List<PlanZoneBill> planZoneBillList) {
    if(planZoneBillList!=null && !planZoneBillList.isEmpty()){
      this.planZoneBillList.addAll(planZoneBillList);
    }
  }

  public List<RateGroupDiscount> getRateGroupDiscountList() {
    return rateGroupDiscountList;
  }

  public void addRateGroupDiscountList(RateGroupDiscount rateGroupDiscount) {
    if(rateGroupDiscount!=null){
      this.rateGroupDiscountList.add(rateGroupDiscount);
    }
  }

  public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
  }

  public List<AcctBill2Bss> getAcctBill2BssList() {
    return acctBill2BssList;
  }

  public void setAcctBill2BssList(List<AcctBill2Bss> acctBill2BssList) {
    this.acctBill2BssList = acctBill2BssList;
  }

  public int getPlatSmsLevel() {
    return platSmsLevel;
  }

  public void setPlatSmsLevel(int platSmsLevel) {
    this.platSmsLevel = platSmsLevel;
  }


}
