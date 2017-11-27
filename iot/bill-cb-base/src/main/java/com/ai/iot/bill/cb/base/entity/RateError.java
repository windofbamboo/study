package com.ai.iot.bill.cb.base.entity;

/**
 * 批价错误代码定义类
 * @author xue
 *
 */
public class RateError {

	/**
	 * 默认值
	 */
	public static final int E_DEFAULT = 0;

	/**
	 * 基数
	 */
	public static final int E_RATE_BASE = 900;

	/**
	 * 离线重单
	 */
	public static final int E_RR_CDR = E_RATE_BASE + 1;

	/**
	 * 账期错误
	 */
	public static final int E_BILL_CYCLE = E_RATE_BASE + 2;

	/**
	 * 无资费计划记录
	 */
	public static final int E_NO_DEVICE_RATE_PLAN = E_RATE_BASE + 3;

	/**
	 * 无资费计划参数
	 */
	public static final int E_NO_RATE_PLAN = E_RATE_BASE + 4;

	/**
	 * 无累计量参数
	 */
	public static final int E_NO_ADDUP = E_RATE_BASE + 5;

	/**
	 * 无区域参数
	 */
	public static final int E_NO_ZONE = E_RATE_BASE + 6;

	/**
	 * 无标准资费
	 */
	public static final int E_NO_STD_RATE_PLAN = E_RATE_BASE + 7;

	/**
	 * 无累计量ID
	 */
	public static final int E_NO_BILL_ID = E_RATE_BASE + 8;
	
	/**
	 * 流量无使用量
	 */
	public static final int E_NO_DATA = E_RATE_BASE + 9;
	
	/**
	 * 无时长
	 */
	public static final int E_NO_DURATION = E_RATE_BASE + 10;
	
	/**
	 * 无可测试流量用量
	 */
	public static final int E_NO_TEST_DATA = E_RATE_BASE + 11;
	
	/**
	 * 无可测试时长用量
	 */
	public static final int E_NO_TEST_DURATION = E_RATE_BASE + 12;
	
	/**
	 * 读取设备累计量错误
	 */
	public static final int E_GET_DEVICE_BILL_SUM = E_RATE_BASE + 13;
	
	/**
	 * 读取池累计量错误
	 */
	public static final int E_GET_POOL_BILL_SUM = E_RATE_BASE + 14;
	
	/**
	 * 更新设备累计量错误
	 */
	public static final int E_UPDATE_DEVICE_BILL_SUM = E_RATE_BASE + 15;
	
	/**
	 * 更新池累计量错误
	 */
	public static final int E_UPDATE_POOL_BILL_SUM = E_RATE_BASE + 16;
	
	/**
	 * 累计量表呼叫类型配置与话单呼叫类型不匹配
	 */
	public static final int E_ADDUP_CALL_TYPE = E_RATE_BASE + 17;
	
	/**
	 * 用量超出阈值
	 */
	public static final int E_EXCEED_LIMIT = E_RATE_BASE + 18;
	
	/**
	 * 资费计划用量不能覆盖话单
	 */
	public static final int E_RATE_PLAN_NO_FULLY_COVER_CDR = E_RATE_BASE + 19;
	
	/**
	 * 在线话单无授权量规则
	 */
	public static final int E_NO_GRANT_RULE = E_RATE_BASE + 20;
	
	/**
	 * 无基本资费计划参数
	 */
	public static final int E_NO_BASE_RATE_PLAN_PARAM = E_RATE_BASE + 21;
	
	/**
	 * 匹配计费组失败
	 */
	public static final int E_BILLING_GROUP = E_RATE_BASE + 22;
	
	/**
	 * 可激活状态的话单
	 */
	public static final int E_DEVICE_STATUS_ACTIVATION = E_RATE_BASE + 23;
	
	/**
	 * 没有匹配到资费计划
	 */
	public static final int E_NO_MATCH_RATE_PLAN = E_RATE_BASE + 24;
	
	/**
	 * 匹配不到JS执行规则
	 */
	public static final int E_NO_JS_RULE = E_RATE_BASE + 25;
	
	/**
	 * 匹配累计量失败
	 */
	public static final int E_ADDUP = E_RATE_BASE + 26;
	
	/**
	 * 在线重单
	 */
	public static final int E_ONLINE_RR_CDR = E_RATE_BASE + 27;
	
	/**
	 * 在线跳包
	 */
	public static final int E_ONLINE_SKIP_CDR = E_RATE_BASE + 28;
	
	/**
	 * 匹配运营商错误
	 */
	public static final int E_NO_PROVIDER = E_RATE_BASE + 29;
}
