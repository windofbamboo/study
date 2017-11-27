package com.ai.iot.bill.define;

/**参数枚举
 * Created by geyunfeng on 2017/6/6.
 */
public final class ParamEnum {

  //账期的状态
  public final class CycleStatus {
    public static final int INIT            = 0; //正常状态
    public static final int ADD_SOLIDIFY    = 1; //月末计费累积量固化
    public static final int BILL_SOLIDIFY   = 2; //月末账单生成完毕
    public static final int ADJUST_SOLIDIFY = 3; //账单发布结束
    public static final int CYC_END         = 9; //cb账单生成结束
  }

  //资费类型
  public final class PlanType {
    public static final int PLANTYPE_MONTH_SINGLE       = 1; //月付单个
    public static final int PLANTYPE_PREPAY_SINGLE      = 2; //预付单个
    public static final int PLANTYPE_MONTH_FIXSHARE     = 3; //月付固定共享
    public static final int PLANTYPE_PREPAY_FIXSHARE    = 4; //预付固定共享
    public static final int PLANTYPE_MONTH_AGILESHARE   = 5; //月付灵活共享
    public static final int PLANTYPE_PREPAY_AGILESHARE  = 6; //预付灵活共享
    public static final int PLANTYPE_EVENT  = 7; //事件
    public static final int PLANTYPE_ADD    = 8; //追加
    public static final int PLANTYPE_PILE   = 9; //堆叠
  }

  //付费类型
  public final class Payment {
    public static final int PAYMENT_TYPE_MONTH      = 1; //月付
    public static final int PAYMENT_TYPE_PREPAY     = 2; //预付
    public static final int PAYMENT_TYPE_TEMPORARY  = 3; //临时性套餐
  }

  public final class PlanVersionState {
    public static final int NOT_ISSUE = 0; //未发布的
    public static final int ISSUE     = 1; //已经发布的
  }

  public final class IsExpireTermByUsage {
    public static final int YES = 0; //标记生效
    public static final int NO  = 1; //不生效
  }

  public final class SubscriberChargeFerquency {
    public static final int ONLY_FIRST  = 1; //只在订户的订购首月收费
    public static final int EVERY_MONTH = 2; //每个月收费
  }

  public final class ChargeTag{
    public static final int YES  = 0; //按计费组生效
    public static final int NO   = 1; //不按计费组生效
  }

  //mo，mt计费方式
  public final class ChargeMode {
    public static final int CHARGE_MODE_MO      = 0; //mo计费
    public static final int CHARGE_MODE_MT      = 1; //mt计费
    public static final int CHARGE_MODE_DETACH  = 2; //分别计费
    public static final int CHARGE_MODE_MERGE   = 3; //合并计费
  }

  public final class SharedMo {
    public static final String SHARE    = "0"; //共享
    public static final String UN_SHARE = "1"; //不共享
  }

  //业务类型
  public final class BizType {
    public static final int BIZ_TYPE_VOICE = 1; //语音
    public static final int BIZ_TYPE_SMS  = 2; //短信
    public static final int BIZ_TYPE_DATA = 3; //流量
  }

  //流量定价单位
  public final class UnitType {
    public static final int DATA_UNIT_K = 0; //流量单位:K
    public static final int DATA_UNIT_M = 1; //流量单位:M
    public static final int DATA_UNIT_G = 2; //流量单位:G
    public static final int DATA_UNIT_T = 3; //流量单位:T
  }

  //费用类型(账目求取时使用)
  public final class ChargeType {
    public static final int CHARGE_TYPE_ACCT_PLAN   = 1; //收费业务类型:账户费
    public static final int CHARGE_TYPE_DEVICE_PLAN = 2; //收费业务类型:订户费
    public static final int CHARGE_TYPE_USAGE       = 3; //收费业务类型:超额使用费
    public static final int CHARGE_TYPE_ACTIVATION  = 4; //收费业务类型:激活费
    public static final int CHARGE_TYPE_LOWERLIMIT  = 5; //收费业务类型:账户保底费
    public static final int CHARGE_TYPE_MINISUBS    = 6; //收费业务类型:最低订购用户数-违约费
    public static final int CHARGE_TYPE_ORDER       = 7; //收费业务类型:订单费
    public static final int CHARGE_TYPE_PLATSMS     = 8; //收费业务类型:平台短信
    public static final int CHARGE_TYPE_ADJUST_BEFORE= 9; //收费业务类型:账前调账
    public static final int CHARGE_TYPE_ADJUST_AFTER= 10; //收费业务类型:账后调账
    public static final int CHARGE_TYPE_ACCT_SET    = 11; //收费业务类型:账户设置费用
    public static final int CHARGE_TYPE_VPN_SET     = 12; //收费业务类型:VPN设置费用
    public static final int CHARGE_TYPE_DEFINE_VPN  = 13; //收费业务类型:自定义VPN
    public static final int CHARGE_TYPE_DEFINE_BRAND= 14; //收费业务类型:自定义品牌
    public static final int CHARGE_TYPE_PERFORMANCE_ANALYSIS    = 15; //收费业务类型:性能分析
    public static final int CHARGE_TYPE_VPN_MONTH    = 16; //收费业务类型:VPN月费
    public static final int CHARGE_TYPE_PREMIUM_SUPPORT = 17; //收费业务类型:高级支持
  }

  //第三方计费的升档方式
  public final class LevelModel {
    public static final int SUBSECTION = 1; //逐级升档(分段批价)
    public static final int MAX_LEVEL  = 2; //按适用的最高档
  }


}
