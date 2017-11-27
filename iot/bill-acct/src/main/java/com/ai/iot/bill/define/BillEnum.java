package com.ai.iot.bill.define;

/**bill枚举
 * Created by geyunfeng on 2017/6/13.
 */
public final class BillEnum {

  /**账户处理阶段
   */
  public final class AcctTrackStage {
    public static final int DEVICE_SUM_ACTIVIE = 1; //设备级费用汇总,激活费
    public static final int DEVICE_SUM_ORDER   = 2; //设备级费用汇总,订户费
    public static final int DEVICE_SUM_USAGE   = 3; //设备级费用汇总,使用费
    public static final int PLAN_ACCT_FEE      = 11; //账户费收取
    public static final int APPEND_PLAN        = 12; //追加资费费用收取
    public static final int GROUP_DISCOUNT     = 13; //资费组折扣
    public static final int SHARE_USAGE        = 14; //共享用量处理
    public static final int ACCT_ORDER         = 15; //订单费用
    public static final int ACCT_OTHER         = 16; //一次性费用+月费
    public static final int ADJUST_BEFORE      = 17; //账前调账
    public static final int ACCT_DISCOUNT      = 18; //账户折扣
    public static final int PLAT_SMS           = 19; //平台短信
    public static final int ACCT_PROMISE       = 20; //账户承诺
    public static final int THIRD_PARTY        = 21; //第三方计费
  }

  public final class DealStage {
    public static final int ACCT_DEAL_START   = 10; //账户资料处理开始
    public static final int ACCT_DEAL_END     = 11; //账户资料处理结束
    public static final int DEVICE_INFO_START = 20; //设备资料初始化开始
    public static final int DEVICE_INFO_END   = 20; //设备资料初始化结束
    public static final int DEVICE_DEAL_START = 30; //设备处理开始
    public static final int DEVICE_DEAL_END   = 31; //设备处理结束
    public static final int BILL_DEAL_START   = 40; //账单处理开始
    public static final int BILL_DEAL_END     = 41; //账单处理结束
  }

  //计划内模式
  public final class IncludeMode {
    public static final int MONTH       = 1; //月付 - 每个 SIM 卡 。每个设备收到当月分配的用量
    public static final int MONTH_SHARE = 2; //月付共享 。每个设备将为其分配的用量添加到一个公共池中，以供计划中的所有其他设备使用
    public static final int PERPAY      = 3; //按期限 。每个设备收到分配的流量，可以在资费计划期限内的任何时候使用
  }

  //表名的前缀
  public final class MdbAddTable{
    public static final String COMM = "SUM-MON-"; //不跨月的
    public static final String SPE  = "SUM-PRE";  //跨月的
  }
  //按业务分表的key
  public final class BizTable{
    public static final String DATA  = "430"; //数据业务
    public static final String VOICE = "410"; //语音业务
    public static final String SMS   = "420"; //短信业务
    public static final String POOL  = "400"; //预付共享池
  }

  //其它账单的收费类型
  public final class ChargeType{
    public static final int ADJUST   = 1; //调整
    public static final int ACCT_SET = 2; //账户设置
    public static final int SIMCARD  = 3; //SIM 卡费用
    public static final int SERVICE  = 4; //训练 和 专业服务
  }

  public static final String SHARE_VALUE = "SHARE";

}
