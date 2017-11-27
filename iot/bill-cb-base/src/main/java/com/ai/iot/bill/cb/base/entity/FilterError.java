package com.ai.iot.bill.cb.base.entity;


/**
 * 话单的错误码常量定义
 * @Author:       [zhangrui]
 * @CreateDate:   [2017-09-12 09:36]
 * @Version:      [v1.0]
 */
public class FilterError {
    /**
     * 默认值
     */
    public static final int E_DEFAULT = 0;

    /**
     * 基数
     */
    public static final int E_FILTER_BASE = 800;

    /**
     * 根据IMSI求取不到设备资料
     */
    public static final int E_NO_DEVINFO_CDR = E_FILTER_BASE + 1;

    /**
     * 话单中没有开始时间或日期
     */
    public static final int E_NO_BEGIN_DATE_OR_TIME = E_FILTER_BASE + 2;

    /**
     * 没有确认收获，而系统设置需要确认收获
     */
    public static final int E_GOOD_NOT_CONFIRM = E_FILTER_BASE + 3;

    /**
     * sim卡被禁用
     */
    public static final int E_SIM_IS_BARRED = E_FILTER_BASE + 4;

    /**
     * SIM卡的状态不被允许上网
     */
    public static final int E_INVALID_DEVICE_STATUS = E_FILTER_BASE + 5;

}
