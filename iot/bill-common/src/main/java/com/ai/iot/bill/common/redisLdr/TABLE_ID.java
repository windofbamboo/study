package com.ai.iot.bill.common.redisLdr;

/**
 * 定义表相关的常量,比如表id,hashkey,表名等信息
 * Created by geyunfeng on 2017/6/30.
 */
public interface TABLE_ID {

    interface DEVINFO{
        int TF_F_DEVICE=10000;
        String TF_F_DEVICE_HASHKEY = "DEVICE";
        int TF_F_DEVICE_RATE_PALN=10001;
        String TF_F_DEVICE_RATE_PLAN_HASHKEY = "DEVICE_RATE_PLAN";
        int TF_F_DEVICE_OTHERS=10002;
        String TF_F_DEVICE_OTHER_HASHKEY = "DEVICE_OTHERS";
        int TF_F_RENEWAL_RATE_QUEUE=10003;
        String TF_F_RENEWAL_RATE_QUEUE_HASHKEY = "RENEWAL_RATE_QUEUE";
        int TF_F_DEVICE_MSISDN_ID=10100;
        String TF_F_DEVICE_MSISDN_ID_HASHKEY = "DEVICE_MSISDN";
        int TF_F_DEVICE_IMSI_ID=10200;
        String TF_F_DEVICE_IMSI_ID_HASHKEY = "DEVICE_IMSI";
        int TF_F_ACCT=10300;
        String TF_F_ACCT_HASHKEY = "ACCOUNT";
        int TF_F_ACCT_APPLICATION=10301;
        String TF_F_ACCT_APPLICATION_HASHKEY = "ACCOUNT_APPLICATION";
        
    }

    interface RATING{
        int BILL_USER_SUM1_GPRS=40000;
        int BILL_USER_SUM2_GPRS=40001;
        int BILL_CDR_FACTOR_GPRS=40002;
        int BILL_USER_ZONE_GPRS=40003;
        int BILL_STAT_GPRS=40004;
        int BILL_POOL_SUM2_GPRS=40100;
        int BILL_USER_SUM1_GSM_SMS=41000;
        int BILL_USER_SUM2_GSM_SMS=41001;
        int BILL_TEST=43000;
    }

    interface RATING_FIELDNAME{
        String BILL_USER_SUM1_GPRS="SUM-MON-";
        String BILL_USER_SUM2_GPRS="SUM-PRE";
        String BILL_CDR_FACTOR_GPRS="SUM-FACTOR-";
        String BILL_USER_ZONE_GPRS="SUM-ZONE-";
        String BILL_STAT_GPRS="SUM-STAT";
        String BILL_STAT_GSM="SUM-STAT";
        String BILL_STAT_SMS="SUM-STAT";
        String BILL_POOL_SUM2_GPRS="SUM-PRE";
        String BILL_USER_SUM1_GSM="SUM-MON-";
        String BILL_USER_SUM2_GSM="SUM-PRE";
        String BILL_USER_SUM1_SMS="SUM-MON-";
        String BILL_USER_SUM2_SMS="SUM-PRE";
        String BILL_TEST="AMOUNT";
    }

    interface AUTORULE{
    	int TF_A_AUTO_RULE=70001;
    	String TF_A_AUTO_RULE_NAME="TF_A_AUTO_RULE";
    	String TF_A_AUTO_RULE_HASHKEY="AUTORULE";
    	String TF_A_AUTO_RULE_WRAPPER_HASHKEY="AUTORULE_WRAPPER";
    	int TF_A_OPER_CONT=70100;
    	String TF_A_OPER_CONT_NAME="TF_A_OPER_CONT";
    	String TF_A_OPER_CONT_HASHKEY="AUTORULE_OPER";
    	int TF_F_SUB_ACCT=70200;
        String TF_F_SUB_ACCT_NAME="TF_F_SUB_ACCT";
        String TF_F_SUB_ACCT_HASHKEY="SUB_ACCT";
        int RULE_TRIGGER_ADDUP=71000;
        String RULE_TRIGGER_ADDUP_NAME="RULE_TRIGGER_ADDUP";
    	String RULE_TRIGGER_ADDUP_HASHKEY="TRIGGER_ADDUP";
        int RULE_TRIGGER_STATUS_DEV=71001;
        String RULE_TRIGGER_STATUS_DEV_NAME="RULE_TRIGGER_STATUS_DEV";
    	String RULE_TRIGGER_STATUS_DEV_HASHKEY="TRIGGER_STATUS_DEV";
        int RULE_TRIGGER_STATUS_ACT=71100;
        String RULE_TRIGGER_STATUS_ACT_NAME="RULE_TRIGGER_STATUS_ACT";
    	String RULE_TRIGGER_STATUS_ACT_HASHKEY="TRIGGER_STATUS_ACT";
        int RULE_RULE_DUPCHECK=72000;
        String RULE_RULE_DUPCHECK_NAME="RULE_RULE_DUPCHECK";
    	String RULE_RULE_DUPCHECK_HASHKEY="RULE_DUPCHECK";
        int RULE_JOB_DUPCHECK=72100;
        String RULE_JOB_DUPCHECK_NAME="RULE_JOB_DUPCHECK";
    	String RULE_JOB_DUPCHECK_HASHKEY="JOB_DUPCHECK";
    }

    interface RATINGCDR{
        int CDR_TRANS_INFO = 50000;
    }

    interface SESSION{
        int SESSION_BASE_INFO = 20000;
        String SESSINO_BASE_INFO_HASHKEY = "SESSION_INFO";
    }

    interface param{
        int PARAM_POG=30000;
        int PARAM_GROUP=30100;
        int PARAM_GROUP_PO_DATA_SIZE=30200;
        int PARAM_GROUP_PO_DATA=30300;
        int PARAM_GROUP_PO_INDEX=30400;
        int PARAM_INS_GROUP=35100;
        int PARAM_INS_GROUP_PO_DATA_SIZE=35200;
        int PARAM_INS_GROUP_PO_DATA=35300;
        int PARAM_INS_GROUP_PO_INDEX=35400;
        int PARAM_INS_GROUP_VISITOR=37000;
        int PARAM_INS_GROUP_VISITOR_INFO=37100;
    }

    interface TRANS{
    	final int TRANS_VER = 90001;
    	final int TRANS_GUID = 90002;
    }


}
