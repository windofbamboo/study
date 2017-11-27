package com.ai.iot.bill.common.param;

/**
 * Created by geyunfeng on 2017/7/10.
 */
public final class BaseDefine {

    public static final int CONNTYPE_MYSQL_CRM   =1;//资料库
    public static final int CONNTYPE_MYSQL_BILL  =2;//清单库
    public static final int CONNTYPE_MYSQL_PARAM =3;//参数库
    public static final int CONNTYPE_MYSQL_ACCT  =4;//内存账单库


    public static final int CONNTYPE_KF_F2M  = 11;//kafka集群 f2m队列域
    public static final int CONNTYPE_KF_M2F  = 12;//kafka集群 m2f队列域
    public static final int CONNTYPE_KF_INFO = 13;//kafka集群 info队列域
    public static final int CONNTYPE_KF_INDB = 14;//kafka集群 入库队列域
    public static final int CONNTYPE_KF_TRANS = 15;//kafka集群 事务队列域
    public static final int CONNTYPE_KF_AUTORULE = 16;//kafka集群 自动化触发话单队列域
    public static final int CONNTYPE_KF_ACCT = 17;//kafka集群 账户处理队列域
    public static final int CONNTYPE_MQ_SESSION = 18;//redis集群 dcc消息队列域
    public static final int CONNTYPE_KF_LOG = 19;//kafka集群 日志队列域
    public static final int CONNTYPE_KF_REDO = 20;//kafka集群重批队列域
    //1x段使用完,使用新的号码段:5x
    public static final int CONNTYPE_MQ_AUTORULE_ONEFIXTIME   =51;//自动化规则一次性定时队列
    

    public static final int CONNTYPE_ZK_M2F  =21;//zookeeper集群 m2f队列域
    public static final int CONNTYPE_ZK_INFO  =22;//zookeeper集群 info队列域
    public static final int CONNTYPE_ZK_FILTER  =22;//zookeeper集群 info队列域

    public static final int CONNTYPE_REDIS_DEVINFO   =31;//redis集群 DEVINFO
    public static final int CONNTYPE_REDIS_LOCAL   =138;//redis集群 本地测试用
    public static final int CONNTYPE_REDIS_RATING    =32;//redis集群 RATING
    public static final int CONNTYPE_REDIS_AUTORULE  =33;//redis集群 AUTORULE
    public static final int CONNTYPE_REDIS_DUPCHECK  =34;//redis集群 DUPCHECK
    public static final int CONNTYPE_REDIS_RATINGCDR =35;//redis集群 RATINGCDR
    public static final int CONNTYPE_REDIS_SESSION   =36;//redis集群 SESSION
    public static final int CONNTYPE_REDIS_PARAM     =37;//redis集群 param

    public static final String CONNCODE_MYSQL_CRM   ="crm";//资料库
    public static final String CONNCODE_MYSQL_CRM1   ="crm1";//资料库
    public static final String CONNCODE_MYSQL_CRM2   ="crm2";//资料库
    public static final String CONNCODE_MYSQL_CRM3   ="crm3";//资料库
    public static final String CONNCODE_MYSQL_CRM4   ="crm4";//资料库
    public static final String CONNCODE_MYSQL_BILL1  ="bill1";//清单库
    public static final String CONNCODE_MYSQL_PARAM ="param";//参数库
    public static final String CONNCODE_MYSQL_ACCT  ="acct";//内存账单库
    public static final String CONNCODE_MYSQL_ERR  ="err";//错单库
    public static final String CONNCODE_MYSQL_RR ="rr";//重单库
    
    public static final String CONNCODE_MYCAT_BILL  ="bill";//mycat清单库
    public static final String CONNCODE_MYCAT_CRM   ="crm";//mycat资料库
    public static final String CONNCODE_MYCAT_ERROR  ="err";//mycat错单库

    public static final String CONNCODE_KF_F2M  = "kafka.f2m";//kafka集群 f2m队列域
    public static final String CONNCODE_KF_M2F  = "kafka.m2f";//kafka集群 m2f队列域
    public static final String CONNCODE_KF_INFO = "kafka.info";//kafka集群 info队列域

    public static final String CONNCODE_ZK_M2F  ="zookeeper.m2f";//zookeeper集群 m2f队列域

    public static final String CONNCODE_REDIS_DEVINFO   ="redis.devinfo";//redis集群 DEVINFO
    public static final String CONNCODE_REDIS_RATING    ="redis.rating";//redis集群 RATING
    public static final String CONNCODE_REDIS_AUTORULE  ="redis.autoRule";//redis集群 AUTORULE
    public static final String CONNCODE_REDIS_DUPCHECK  ="redis.dupCheck";//redis集群 DUPCHECK
    public static final String CONNCODE_REDIS_RATINGCDR ="redis.ratingCdr";//redis集群 RATINGCDR
    public static final String CONNCODE_REDIS_SESSION   ="redis.session";//redis集群 SESSION
    public static final String CONNCODE_REDIS_PARAM   ="redis.param";//redis集群 PARAM


    public static final int COMPUTE_MODE_MOD=1;//取模
    public static final int COMPUTE_MODE_NONE=2;//不取模


    public static final char PROP_TYPE_CONSUMER ='R';//消费者属性
    public static final char PROP_TYPE_PRODUCER ='W';//生产者属性
    public static final char PROP_TYPE_COMMON   ='A';//通用属性


    public static final char HOST_TYPE_MASTER='M';//主节点
    public static final char HOST_TYPE_SLAVE='S';//从节点

    public static final String PROVINCE_CODE_COMMON = "0000";//通配

}
