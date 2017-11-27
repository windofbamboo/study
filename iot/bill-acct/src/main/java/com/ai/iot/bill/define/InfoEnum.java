package com.ai.iot.bill.define;

/**资料字段枚举
 * Created by geyunfeng on 2017/6/6.
 */
public final class InfoEnum {

  //账户状态
  public final class AcctStatus {
    public static final String NOMAL = "1"; //正常
    public static final String STOP  = "2"; //停用
  }
  //账户是否为后项付费的账户
  public final class IsPayBack {
    public static final String NO   = "0"; //不是
    public static final String YES  = "1"; //是
  }

  //设备状态
  public final class DeviceState {
    public static final String TEST     = "0"; //可测试
    public static final String INIT     = "1"; //可激活
    public static final String ACTIVE   = "2"; //已激活
    public static final String STOP     = "3"; //已停用
    public static final String INACTIVE = "4"; //已失效
    public static final String REMOVE   = "5"; //已清除
    public static final String CHANGE   = "6"; //已更换
    public static final String STORE    = "7"; //库存
    public static final String START    = "8"; //开始
  }

  //资费订购预约标记
  public final class ActiveFlag {
    public static final String ACTIVATION = "1"; //激活
    public static final String OTHER      = "2"; //其它
    public static final String RENEWAL    = "3"; //续约
  }

  //激活首月折算方式
  public final class DisountMode {
    public static final String DAYS       = "1"; //按天折算
    public static final String HALF_MONTH = "2"; //按半月折算
    public static final String NONE       = "3"; //不折算
  }

  //默认激活计划
  public final class DefaultActivationPlan {
    public static final int NEVER      = 0;  //无
    public static final int ONLY_FIRST = 1;  //仅第一次
    public static final int EVERY_TIME = 2;  //每次
  }

  //资费订购状态
  public final class PlanState {
    public static final String ACTIVE   = "1";//有效
    public static final String INACTIVE = "0";//无效
  }

  //激活方式
  public final class ActivationType {
    public static final int FIRST = 0; //首次激活
    public static final int AGAIN = 1; //再次激活
  }

  //设备收费原因
  public final class RateType {
    public static final int NOMAL = 1; //激活状态
    public static final int BILL_ACTIVATION_GRACE_PERIOD = 2; //激活宽限期
    public static final int BILL_MININUM_ACTIVATION_TERM = 3; //最短激活期
  }

  //调账业务类型
  public final class AdjustOperateType{
    public static final int ADJUST_BEFORE = 1; // 账前调账
    public static final int ADJUST_AFTER  = 2; // 账后调账
  }

  //调账类型
  public final class AdjustType{
    public static final int ADD       = 1; // 补费
    public static final int SUBTRACT  = 2; // 退费
  }
}
