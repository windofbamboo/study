package com.ai.iot.bill.cb.base.entity;

/**
 * 批价常量定义类
 * @author xue
 *
 */
public class RateConst {

	/**
	 *  普通区域
	 */
	public static final int ZONE_TYPE_NORMAL = 0;
	
	/**
	 *  其它区域
	 */
	public static final int ZONE_TYPE_OTHER = 1;
	
	/**
	 *  标准资费区域
	 */
	public static final int ZONE_TYPE_STD = 2;
	
	/**
	 *  月付单个
	 */
	public static final int PLAN_TYPE_MONTHLY_INDIVIDUAL = 1;
	
	/**
	 *  预付单个
	 */
	public static final int PLAN_TYPE_PREPAID_INDIVIDUAL = 2;
	
	/**
	 *  月付固定共享
	 */
	public static final int PLAN_TYPE_MONTHLY_FIXED_POOL = 3;
	
	/**
	 *  预付固定共享
	 */
	public static final int PLAN_TYPE_PREPAID_FIXED_POOL = 4;
	
	/**
	 *  月付灵活共享
	 */
	public static final int PLAN_TYPE_MONTHLY_FLEXIBLE_POOL = 5;
	
	/**
	 *  预付灵活共享
	 */
	public static final int PLAN_TYPE_PREPAID_FLEXIBLE_POOL = 6;
	
	/**
	 *  事件类型
	 */
	public static final int PLAN_TYPE_EVENT = 7;
	
	/**
	 *  过滤器不处理
	 */
	public static final int FILTER_NONE = 0;
	
	/**
	 *  计划内过滤器
	 */
	public static final int FILTER_INCLUDE = 1;
	
	/**
	 *  计划外过滤器
	 */
	public static final int FILTER_EXCLUDE = 2;
	
	/**
	 *  计费组自定义
	 */
	public static final int BILL_GROUP_VALUE_TYPE_CUSTOMER = 1;
	
	/**
	 *  计费组扩展
	 */
	public static final int BILL_GROUP_VALUE_TYPE_EXTEND = 2;
	
	/**
	 *  计费组国家
	 */
	public static final int BILL_GROUP_VALUE_TYPE_COUNTRY = 0;
	
	/**
	 *  计费组区域
	 */
	public static final int BILL_GROUP_VALUE_TYPE_ZONE = 4;
	
	/**
	 * 前缀匹配
	 */
	public static final int BILL_GROUP_VALUE_TYPE_PREFIX = 0;
	
	/**
	 * 全值匹配
	 */
	public static final int BILL_GROUP_VALUE_TYPE_ALL = 1;
	
	/**
	 *  舍入频率按天
	 */
	public static final long ROUNDING_FREQ_DAY = 1;
	
	/**
	 *  舍入频率按话单
	 */
	public static final long ROUNDING_FREQ_CDR = 2;
	
	/**
	 *  舍入频率会话
	 */
	public static final long ROUNDING_FREQ_SESSION = 3;

	/**
	 *  是否限量
	 */
	public static final String RATE_PLAN_LIMIT_TAG_YES = "1";
	
	public static final String RATE_PLAN_LIMIT_TAG_NO = "0";
	
	/**
	 * 流量可测试状态默认阈值，单位(byte)
	 */
	public static final long DATA_TEST_THRESHOLD_VALUE = 1024;
	
	/**
	 *  流量系统阈值KEY
	 */
	public static final String CC_DATA_THRESHOLD = "CC_DATA_THRESHOLD";

	/**
	 *  流量系统阈值KEY
	 */
	public static final String CC_TIME_THRESHOLD = "CC_TIME_THRESHOLD";
	
	/**
	 * 超时会话type
	 */
	public static final String SESSION_TIME_OUT = "SESSION_TIME_OUT";
	
	/**
	 * 超时会话name
	 */
	public static final String TIME_OUT_COUNT = "TIME_OUT_COUNT";
	
	/**
	 *  时长授权默认值
	 */
	public static final long CC_TIME = 360L;
	
	/**
	 *  可测试用量用尽发送状态变更通知
	 */
	public static final int SIM_EVENT_TEST = 1;
	
	/**
	 *  用量用尽发送状态变更通知
	 */
	public static final int SIM_RATE_PLAN_OUT = 2;
	
	/**
	 * 累计量和阈值相等
	 */
	public static final int AMOUNT_EQUAL = 1;
	
	/**
	 * 累计量超出阈值
	 */
	public static final int AMOUNT_EXCEED = 2;
	
	/**
	 * 总量限额不限量的最大值
	 */
	public static final long MAX_USAGE_LIMIT = Long.MAX_VALUE;
	
	/**
	 * 区域限额不限量的最大值
	 */
	public static final long MAX_ZONE_USAGE_LIMIT = Long.MAX_VALUE;
	
	/**
	 * 账期正常状态
	 */
    public static final int CYCLE_STATUS_INIT = 0;
    
    /**
     * 月末计费累积量固化
     */
    public static final int CYCLE_STATUS_ADD_SOLIDIFY = 1;
    
    /**
     * 月末账单生成完毕
     */
    public static final int CYCLE_STATUS_BILL_SOLIDIFY = 2;
    
    /**
     * 账单发布结束
     */
    public static final int CYCLE_STATUS_ADJUST_SOLIDIFY = 3;
    
    /**
     * cb账单生成结束
     */
    public static final int CYCLE_STATUS_CYC_END = 9;
}
