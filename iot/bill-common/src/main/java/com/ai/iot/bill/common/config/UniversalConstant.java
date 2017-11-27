package com.ai.iot.bill.common.config;

import com.ai.iot.bill.common.param.BaseDefine;

public class UniversalConstant {
    public static final boolean universalDebugMode = false;
    public static final String TOPIC_DATA = "TOP_CDRS_RATE_DATA";
    public static final String TOPIC_SM = "TOP_CDRS_RATE_SM";
    public static final String TOPIC_VOICE = "TOP_CDRS_RATE_VOICE";
    //参数加载模块常量参数
    public static final class ParamLoadConst {
        //public static final int PARAM_LOAD_JC_ROUTE_ID = paramLoadDebugMode ? BaseDefine.CONNTYPE_REDIS_LOCAL : BaseDefine.CONNTYPE_REDIS_PARAM;
        public static final int PARAM_LOAD_JC_ROUTE_ID = BaseDefine.CONNTYPE_REDIS_PARAM;
        public static final String PARAMLOAD_CFG_FILE = "param-loader.yaml";
        public final static int SCAN_SLEEP_SECONDS = 60;
    }

    //资料加载、同步模块常量参数
    public static final class InfoLoadConst {
        public static final int INFO_LOAD_JC_ROUTE_ID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        public static final String INFOLOAD_CFG_FILE = "info-loader.yaml";
        public static final String INFOSYCHRONIZE_CFG_FILE = "info-sychronize.yaml";
        public static final String INFOLOAD_DISP_FIELD_NAME = "MO_ID";
        public static final int SYCHRONIZE_MSG_WAIT_TIME = 600*1000;
        public static final String TOP_INFO_ACCT_INFO = "TOP_INFO_ACCT_INFO";
        public static final String TOP_INFO_DEV_INFO = "TOP_INFO_DEV_INFO";
        public static final String TOP_INFO_RATE_PLAN = "TOP_INFO_RATE_PLAN";
        public static final String TOP_INFO_AUTORULE = "TOP_INFO_AUTORULE";
        public static final String INFO_01 = "INFO-01";
        public static final String INFO_02 = "INFO-02";
        public static final String INFO_03 = "INFO-03";
        public static final String INFO_04 = "INFO-04";
        public static final String INFO_05 = "INFO-05";
    }

    //分拣模块常量参数
    public static final class FilterConst {
        public enum  RatePlanType {
            MonthPaidSingle,
            PrePaidSingle,
            MonthPaidShare,
            PrePaidFixShare,
            MonthPaidSmartShare,
            PrePaidSmartShare,
            Event,
            Append,
            Stack
        };

        //public static final int FILTER_JC_ROUTE_ID = filterDebugMode ? BaseDefine.CONNTYPE_REDIS_LOCAL : BaseDefine.CONNTYPE_REDIS_PARAM;
        public static final int FILTER_JC_ROUTE_ID = BaseDefine.CONNTYPE_REDIS_PARAM;
        public static final String CB_CFG_FILE = "cb.yaml";
        public static final String FILTER_DISP_FIELD_NAME = "DISP_ID";
        public static final String FILTER_CDRINFO_FIELD_NAME = "CDRINFO";

        //是否需要确认收获
        public static final String RECV_CONFIRM = "false";
        //测试时用的CDR中的SGSN的IP
        public static final String TEST_CDR_STR_IP = "192.168.1.2";

        public final static String FILTER_MODULE_NAME = "FILTER";

        public static final String FILTER_MODULE_TEST_DATA = "FILTER+TEST+DATA";

        public final static int FILTER_CDR_MAX_SENDTIMES = 3;

        public final static String CRM_NOTIFY_IMEI_CHANGED = "TOP_FILTER_IMEI_CHANGED";

        public final static String FILTER_ACCT_DISP_PAMAM_NAME = "FILTER_ACCT_DISP"; //按账户分发开关

        public final static String DCC_CB_MSG_TYPE_CCA = "272";

        public final static String DCC_CB_MSG_TYPE_RAR = "258";

        public final static String DCC_AUTH_APPLICATION_ID_DCCA = "4";

        public final static String DCC_RESULT_CODE_SUCCESS = "2000";

        public final static String PARAM_NAME_DISP_BY_ACCT_SWITCH = "disp.by.acctid.enabled";

        public final static int OFFLINE_FILTER_CDR_FETCH_TIMEOUT = 1800000;
        public final static int ONLINE_FILTER_CDR_FETCH_TIMEOUT = 1800000;

        public final static String TOP_CDRS_TRANS = "TOP_CDRS_TRANS";
    }
    
    // 回退重批常量
    public static final class RedoCfgConst {
    	public static final String REDO_CFG_FILE = "redo.yaml";
    }
    
    // 回收常量
    public static final class RecycleCfgConst {
    	public static final String RECYCLE_CFG_FILE = "recycle.yaml";
    }

    public static final String FETAL_ERROR_CONFIG_INIT_0 = "ConfigFactory.getModuleConfig() failed.";
    public static final String FETAL_ERROR_CONFIG_INIT_1 = "Config.init() failed.";

}
