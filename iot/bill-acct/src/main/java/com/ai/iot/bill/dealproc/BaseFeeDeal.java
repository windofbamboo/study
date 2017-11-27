package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.dealproc.container.RateBill;
import com.ai.iot.bill.dealproc.util.InfoUtil;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.computebill.AcctBillUsage;
import com.ai.iot.bill.entity.computebill.BillTrackAcct;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.usage.BulkAdjustInterface;

import java.util.Date;
import java.util.List;

/**费用计算
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
class BaseFeeDeal {

  /**
   * 订购天数折算，适用于预付,堆叠的使用量，按月进行拆分，计算拆分比例
   */
  static long[] calculateRatiobyDays(final CycleBean cycleBean,
                                     final Date orderTime,
                                     final Date endTime){

    long[] ratio = {0, 1};

    Date currStartDate = orderTime.getTime() < cycleBean.getCycStartTime().getTime() ? cycleBean.getCycStartTime():orderTime;
    Date currEndDate = cycleBean.getCycEndTime().getTime() < endTime.getTime() ? cycleBean.getCycEndTime():endTime;

    int days = InfoUtil.getDays(currStartDate,currEndDate);
    int totalDays = InfoUtil.getDays(orderTime,endTime);

    ratio[0] = days;
    ratio[1] = totalDays;
    return ratio;
  }

  /**
   * 费用计算的折算比例，适用于月付的资费
   */
  static long[] calculateRatiobyMod(final CycleBean cycleBean,
                                    final Date orderTime,
                                    final String modTag) {
    long[] ratio = {0, 1};

    switch (modTag) {
      case InfoEnum.DisountMode.HALF_MONTH:
        if (orderTime.getTime() < cycleBean.getCycHalfTime().getTime()) {
          ratio[0] = 100;
          ratio[1] = 100;
        } else {
          ratio[0] = 50;
          ratio[1] = 100;
        }
        break;
      case InfoEnum.DisountMode.DAYS:
        int days = InfoUtil.getDays(orderTime,cycleBean.getCycEndTime());
        int totalDays = InfoUtil.getDays(cycleBean.getCycStartTime(),cycleBean.getCycEndTime());
        ratio[0] = days;
        ratio[1] = totalDays;
        break;
      default:
        ratio[0] = 100;
        ratio[1] = 100;
        break;
    }
    return ratio;
  }

  /**
   * 计算超额费用,超额量
   */
  static long[] calculateUsageFee(final int bizType,
                                  final int unit,
                                  final int unitTimes,
                                  final boolean isBulk,
                                  final long ratio,
                                  final int precision,
                                  final long upperValue) {

    long[] result = {0, 0};//费用，批量超额
    long unitBase = 1L;
    if (bizType == ParamEnum.BizType.BIZ_TYPE_DATA){
      switch (unit) {
        case ParamEnum.UnitType.DATA_UNIT_K:
          unitBase = 1024L;
          break;
        case ParamEnum.UnitType.DATA_UNIT_M:
          unitBase = 1024 * 1024L;
          break;
        case ParamEnum.UnitType.DATA_UNIT_G:
          unitBase = 1024 * 1024 * 2014L;
          break;
        case ParamEnum.UnitType.DATA_UNIT_T:
          unitBase = 1024 * 1024 * 2014 * 1024L;
          break;
        default:
          break;
      }
    } else {
      if (bizType == ParamEnum.BizType.BIZ_TYPE_VOICE) {
        unitBase = 60L;
      }
    }

    unitBase = unitBase * unitTimes; //实际定价单位
    double p= Math.pow(10d,(double) (precision-2));//精度运算

    double a = ((double) upperValue) / unitBase; // 定价单位的倍数
    long b = (long) Math.ceil(a); // 获取定价用量的 整数倍，以计算费用

    long fee = (long) Math.floor(b * ratio / p);//费用
    result[0] = fee;
    result[1] = b * unitBase - upperValue;//批量超额

    if(!isBulk){
      result[1] = 0L;
    }

    return (result);
  }

  /**
   * 计算需要批价的值
   * @param currValue 当月使用量
   * @param includeValue 额度
   * @return 需要批价的量
   */
  static long getUpperValue(final long includeValue,
                            final long currValue) {
    return currValue > includeValue ? currValue-includeValue:0L;
  }

  /**
   * 账户级使用量批价
   */
  static <T extends BulkAdjustInterface> void dealAcctUsageFee(final ParamContainer paramContainer,
                                                               final RateBill rateBill,
                                                               T t,
                                                               final long upperValue,
                                                               List<AcctBillUsage> acctBillUsageList,
                                                               List<BillTrackAcct> billTrackAcctList) {
    if (upperValue > 0) {
      CycleBean cycleBean = paramContainer.getCycleBean();

      long[] result = calculateUsageFee(rateBill.getBizType(), rateBill.getUnit(), rateBill.getTimes(), rateBill.isBulk(),
              rateBill.getRatio(),rateBill.getPrecision(), upperValue);
      long fee = result[0];
      long bulkAdjust = result[1];
      int itemId = paramContainer.getSumbillUse(rateBill.getChargeMode(), rateBill.getPaymentType(), rateBill.getBizType(), rateBill.isRoam());
      int planId = paramContainer.getPlanId(rateBill.getPlanVersionId());

      AcctBillUsage acctBillUsage = new AcctBillUsage();
      acctBillUsage.setAcctId(t.getAcctId());
      acctBillUsage.setCycleId(cycleBean.getCycleId());
      acctBillUsage.setPlanId(planId);
      acctBillUsage.setPlanVersionId(rateBill.getPlanVersionId());
      acctBillUsage.setZoneId(rateBill.getZoneId());
      acctBillUsage.setGroupId(rateBill.getBillingGroupId());
      acctBillUsage.setBillId(rateBill.getBillId());
      acctBillUsage.setItemId(itemId);
      acctBillUsage.setFee(fee);
      acctBillUsage.setBizType(rateBill.getBizType());
      acctBillUsage.setRoam(rateBill.isRoam());

      acctBillUsageList.add(acctBillUsage);

      BillTrackAcct billTrackAcct = new BillTrackAcct();

      billTrackAcct.setAcctId(t.getAcctId());
      billTrackAcct.setCycleId(cycleBean.getCycleId());
      billTrackAcct.setStage(BillEnum.AcctTrackStage.SHARE_USAGE);
      billTrackAcct.setSourceId(rateBill.getPlanVersionId());
      billTrackAcct.setItemId(itemId);
      billTrackAcct.setFee(fee);

      billTrackAcctList.add(billTrackAcct);

      t.setBulkAdjust(bulkAdjust);
    }
  }

  /**
   * 根据单位转化流量数据到bit
   */
  static long getBitValue(final int unitType,
                          final long value) {

    long bitValue = 1L;
    switch (unitType) {
      case ParamEnum.UnitType.DATA_UNIT_K:
        bitValue = value * 1024L;
        break;
      case ParamEnum.UnitType.DATA_UNIT_M:
        bitValue = value * 1024 * 1024L;
        break;
      case ParamEnum.UnitType.DATA_UNIT_G:
        bitValue = value * 1024 * 1024 * 1024L;
        break;
      case ParamEnum.UnitType.DATA_UNIT_T:
        bitValue = value * 1024 * 1024 * 1024 * 1024L;
        break;
      default:
        break;
    }
    return bitValue;
  }


}
