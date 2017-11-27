package com.ai.iot.bill.common.util;

/**
 * kafka常量定义类
 * @author xue
 *
 */
public class KafkaConst {

	/**话单类*/
	public static final String TOP_CDRS_INDB_DATA = "TOP_CDRS_INDB_DATA";
	public static final String TOP_CDRS_INDB_VOICE = "TOP_CDRS_INDB_VOICE";
	public static final String TOP_CDRS_INDB_SM = "TOP_CDRS_INDB_SM";
	public static final String TOP_CDRS_AMOUNT = "TOP_CDRS_AMOUNT";
	public static final String TOP_CDRS_REDO = 	"TOP_CDRS_REDO";

	public static final String TOP_CDRS_RR_DATA = 	"TOP_CDRS_RR_DATA";
	public static final String TOP_CDRS_RR_VOICE = 	"TOP_CDRS_RR_VOICE";
	public static final String TOP_CDRS_RR_SM = 	"TOP_CDRS_RR_SM";
	
	public static final String TOP_CDRS_ERR_DATA = "TOP_CDRS_ERR_DATA";
	public static final String TOP_CDRS_ERR_VOICE = "TOP_CDRS_ERR_VOICE";
	public static final String TOP_CDRS_ERR_SM = "TOP_CDRS_ERR_SM";
	
	public static final String TOP_CRM_NOTIFY = "TOP_CRM_NOTIFY";
	
	public static final String TOP_CDRS_TRANS = "TOP_CDRS_TRANS";
	
	/**自动化规则类*/
	public static final String TOPIC_RULE_EVENT_DCC = "top_rule_event_dcc";
	public static final String TOPIC_RULE_EVENT_CDRS = "top_rule_event_cdrs";
	public static final String TOPIC_RULE_EVENT_CRM = "top_rule_event_crm";
	public static final String TOPIC_RULE_EVENT_CYCLE = "top_rule_event_cycle";
	public static final String TOPIC_RULE_CRM_JOB = 	"top_rule_crm_job";
	public static final String TOPIC_RULE_CRM_REWRITE_JOB = "top_rule_crm_rewrite_job";
	public static final String TOPIC_RULE_JOB_FOLLOW_OPER_FIXTIME = "top_rule_job_follow_oper_fixtime";
	public static final String TOPIC_RULE_JOB_FOLLOW_OPER_NOW = "top_rule_job_follow_oper_now";
	public static final String TOPIC_RULE_JOB_OPER = "top_rule_job_oper";
	public static final String TOPIC_RULE_EVENT_ERR_DCC = 	"top_rule_event_err_dcc";
	public static final String TOPIC_RULE_EVENT_ERR_CDRS = 	"top_rule_event_err_cdrs";
	public static final String TOPIC_RULE_EVENT_ERR_CRM = 	"top_rule_event_err_crm";
	public static final String TOPIC_RULE_EVENT_ERR_CYCLE = 	"top_rule_event_err_cycle";
	
	/**
	 * 规划的kafka topic
	 */
//	public static final String TOP_CDRS_INDB_DATA = "TOP_CDRS_INDB_DATA";
//	public static final String TOP_CDRS_INDB_VOICE = "TOP_CDRS_INDB_VOICE";
//	public static final String TOP_CDRS_INDB_SM = "TOP_CDRS_INDB_SM";
//	public static final String TOP_CDRS_ERR_DATA = "TOP_CDRS_ERR_DATA";
//	public static final String TOP_CDRS_ERR_VOICE = "TOP_CDRS_ERR_VOICE";
//	public static final String TOP_CDRS_ERR_SM = "TOP_CDRS_ERR_SM";
//	public static final String TOP_CDRS_RR_DATA = "TOP_CDRS_RR_DATA";
//	public static final String TOP_CDRS_RR_VOICE = "TOP_CDRS_RR_VOICE";
//	public static final String TOP_CDRS_RR_SM = "TOP_CDRS_RR_SM";
//	public static final String TOP_CDRS_AMOUNT = "TOP_CDRS_AMOUNT";
}
