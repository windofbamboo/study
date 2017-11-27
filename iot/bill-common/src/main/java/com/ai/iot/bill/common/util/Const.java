/**
 * 
 */
package com.ai.iot.bill.common.util;

import java.nio.charset.Charset;

/**
 * @author XLR
 *
 */
public class Const {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final Charset UNICODE = Charset.forName("UNICODE");
    
	///conf目录名称
	public static final String SYSTEM_HOME="IO_HOME";
	public static final String SYSTEM_CONF="conf";
	public static final String PROPERTIES_KEY_SPLIT="_";//配置文件的key用下划线'_'分割,需去掉下划线后转换为内部变量名
	public static final String PROPERTIES_VALUE_SPLIT=",";
	
	///常用常量
	public static final int OK=0;
	public static final int FAIL=1;
	public static final int ERROR=-1;
	public static final String STR_OK="OK";
	public static final String STR_ERROR="ERROR";
	public static final String STR_ok="ok";
	public static final String STR_error="error";
	
	//kvs连接符
	public static final String KEY_JOIN="+";
	public static final String VALUE_JOIN=",";
	public static final String VLINE="|";
	
	//数据库
	public static final String DB_CENDB="cendb";
	
	//共享内存
	public static final String SHM_DB="param";
	public static final String SHM_TABLE="param";
	public static final String SHM_PARAM_DB="db";
	public static final String SHM_PARAM_MQ="mq";
	public static final String SHM_PARAM_ROUTE="route";
	public static final String SHM_PARAM_CYCLE="cycle";
	
	//进程名
	public static final String PROC_PARSE="parse";
	public static final String PROC_FILTER="filter";
	public static final String PROC_RATE="rate";
	public static final String PROC_INDB="indb";
	public static final String PROC_RR="rr";
	
	//话单中sourceType的所在位置，起点为1
	public static final int SOURCETYPE_POS_PARSE_0 = 2;
	public static final int SOURCETYPE_POS_INDB_1 = 2;
	public static final int PARSE_TYPE = 0;
	
	//read and write mode
	public static final int READ_ONLY=0;
	public static final int WRITE_ONLY=1;
	public static final int READ_AND_WRITE=2;
	//max value
	public static final int MAX_MQ_BATCH_SIZE=1000;
	
	//批价kafka主题
	public static final String TOPIC_RATE_DEVICE="topic_rate_device";
	public static final String TOPIC_RATE_DEVICE_DISCNT="topic_rate_device_discnt";
	public static final String TOPIC_RATE_OUT="topic_rate_out";

	//kafka和redis类型
	public static final String KAFKA_CLUSTER="kafka";
	public static final String MDB_CLUSTER="mdb";
	
	//模块定义
	public static final String MODULE_FILTER = "1";
	public static final String MODULE_RATE = "2";
	
	//超时扫描记录数
	public static final String SESSION_TIME_OUT = "SESSION_TIME_OUT";
	public static final String SESSION_TIME_OUT_MAX_DURATION = "MAX_DURATION";
	public static final String SESSION_TIME_OUT_MAX_COUNT = "TIME_OUT_COUNT";
	
	public static final String KF_REDO = "KF_REDO";
}
