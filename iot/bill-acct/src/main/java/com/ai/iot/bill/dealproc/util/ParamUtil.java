package com.ai.iot.bill.dealproc.util;

import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.param.CycleInterface;

import java.util.List;

/**参数工具类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ParamUtil {

  /**
   * 根据账期，过滤记录
   */
  public static <T extends CycleInterface> void judgeCycle(final CycleBean cycleBean, List<T> tList) {
    if (tList == null || tList.isEmpty()) {
      return;
    }
    tList.removeIf(t -> !(t.getStartCycleId() < t.getEndCycleId() &&
                        t.getStartCycleId() <= cycleBean.getCycleId() &&
                        t.getEndCycleId() >= cycleBean.getCycleId()));
  }

  /**
   * 根据业务类型，转换 参数中的include量
   */
  public static long getBaseValue(final int bizType,final long value){
    if(bizType==ParamEnum.BizType.BIZ_TYPE_DATA){
      return value*1024*1024;
    }else if(bizType==ParamEnum.BizType.BIZ_TYPE_VOICE){
      return value*60;
    }else{
      return value;
    }
  }


  public static int getPaymentType(final int planType){

    int payment ;
    switch (planType){
      case ParamEnum.PlanType.PLANTYPE_MONTH_SINGLE:
      case ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE:
      case ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE:
        payment = ParamEnum.Payment.PAYMENT_TYPE_MONTH;
        break;
      case ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE:
      case ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE:
        payment = ParamEnum.Payment.PAYMENT_TYPE_PREPAY;
        break;
      default:
        payment = ParamEnum.Payment.PAYMENT_TYPE_TEMPORARY;
        break;
    }
    return payment;
  }


}
