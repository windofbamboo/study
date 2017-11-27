package com.ai.iot.bill.common.util;

/**
 * 话单属性值常量类
 * @author xue
 *
 */
public class CdrConst {

	// 语音
	public static final int BIZ_VOICE = 1;
	
	// 短信
	public static final int BIZ_SMS = 2;
	
	// 流量
	public static final int BIZ_DATA = 3;
	
	//在线状态变更
	public static final int BIZ_STATUS = 4;
	
	// 初始包
	public static final int PARTIAL_INIT = 1;
	
	// 更新包
	public static final int PARTIAL_UPDATE = 2;
	
	// 结束包
	public static final int PARTIAL_TERMINAL = 3;
	
	// 可测试
	public static final int DEVICE_STATUS_TEST = 0;
	
	// 可激活
	public static final int DEVICE_STATUS_ACTIVATION = 1;
	
	// 已激活
	public static final int DEVICE_STATUS_ACTIVATED = 2;
	
	// 已停用
	public static final int DEVICE_STATUS_DEACTIVATED = 3;
	
	// 已失效
	public static final int DEVICE_STATUS_EXPIRED = 4;
	
	// 已清除
	public static final int DEVICE_STATUS_CLEARED = 5;
	
	// 已更换
	public static final int DEVICE_STATUS_REPLACED = 6;
	
	// 库存
	public static final int DEVICE_STATUS_STOCK = 7;
	
	// 离线
	public static final int ONLINE_STATUS_N = 0;
	
	// 在线
	public static final int ONLINE_STATUS_Y = 1;
	
	// 默认在线状态
	public static final int ONLINE_STATUS_DEFAULT = 9;
	
	// 语音数据源
	public static final String SOURCE_TYPE_GSM = "11";
	
	// 短信数据源
	public static final String SOURCE_TYPE_SMS = "21";
	
	// 流量离线数据源
	public static final String SROUCE_TYPE_OFFLINE_DATA = "31";
	
	// 流量在线数据源
	public static final String SROUCE_TYPE_ONLINE_DATA = "32";
	
	/**
	 *  不区分MO/MT
	 */
	public static final int CALL_TYPE_ALL = 0;
	
	/**
	 *  MO
	 */
	public static final int CALL_TYPE_MO = 1;
	
	/**
	 *  MT
	 */
	public static final int CALL_TYPE_MT = 2;
	
	private CdrConst() {
	    throw new IllegalStateException("Utility class");
	}
}