package com.ai.iot.bill.define;

/**storm 枚举
 * Created by geyunfeng on 2017/7/21.
 */
public final class CommonEnum {

  public static final String TOPIC_ACCT_TASK = "TOP_ACCT_TASK";
  public static final String TOPIC_ACCTID = "TOP_ACCT_ID";
  public static final String TOPIC_DEVICEINFO = "TOP_ACCT_INFO";
  public static final String TOPIC_CONTROL = "TOP_ACCT_CONTR";
  public static final String TOPIC_BILL = "TOP_ACCT_BILL";

  public static final int TIMEOUT = 10000; // 超时时间
  public static final int SLEEP_TIME = 1000; // 休眠时间
  public static final int RETRY_TIME = 3; // kafka发送重试次数

  public static final String TOPOLOGY_NAME      = "TopoOutAccount";
  public static final String ACCT_SPOUT_NAME    = "TopoOutAccount-AcctSpout";
  public static final String ACCT_BOLT_NAME     = "TopoOutAccount-AcctBolt";
  public static final String DEVICE_SPOUT_NAME  = "TopoOutAccount-DeviceSpout";
  public static final String DEVICE_BOLT_NAME   = "TopoOutAccount-DeviceBolt";
  public static final String CONTROL_SPOUT_NAME = "TopoOutAccount-ControlSpout";
  public static final String CONTROL_BOLT_NAME  = "TopoOutAccount-ControlBolt";
  public static final String BILL_SPOUT_NAME    = "TopoOutAccount-BillSpout";
  public static final String BILL_BOLT_NAME     = "TopoOutAccount-BillBolt";

}
