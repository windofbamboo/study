package com.ai.iot.bill.common.redisLdr;

public class RedisConst {

	public static class DevInfoMdbKey{
		public static final String MDB_KEY_DEVICE_ID_START_DATE="100";
		public static final String MDB_KEY_DEVICE_MSISDN="101";
		public static final String MDB_KEY_DEVICE_IMSI="102";
		public static final String MDB_KEY_ACCOUNT_BASE_INFO="103";
		public static final String MDB_KEY_DEVICE_DISCNT_ID="110";
	}
	
	public static class RatingMdbKey{
		public static final String MDB_KEY_DEVICE_DATA_SUM = "400";
		public static final String MDB_KEY_DEVICE_DATA_SHARED_POOL_SUM = "401";
		public static final String MDB_KEY_DEVICE_VOICE_SUM = "410";
		public static final String MDB_KEY_DEVICE_SMS_SUM = "420";
		public static final String MDB_KEY_DEVICE_DATA_TEST_SUM = "430";
		public static final String MDB_KEY_DEVICE_VOICE_TEST_SUM = "431";
		public static final String MDB_KEY_DEVICE_SMS_TEST_SUM = "432";
	}
	
	public static class AutoRuleMdbKey{
		public static final String MDB_KEY_ACCT_RULE_INFO = "700";
		public static final String MDB_KEY_ACCT_RULE_OPER_CONT = "701";
		public static final String MDB_KEY_SUB_ACCT = "702";
		public static final String MDB_KEY_ADDUP_STATUS_DEV = "710";
		public static final String MDB_KEY_ADDUP_STATUS_ACCT = "711";
		public static final String MDB_KEY_DUPCHECK_RULE = "720";
		public static final String MDB_KEY_DUPCHECK_JOB = "721";
	}
	
	public static class DupCheckMdbKey{
		public static final String MDB_KEY_BLOOM_FID = "600";
		public static final String MDB_KEY_BLOOM_GUID= "601";
	}
	
	public static class RatingCdrMdbKey{
		public static final String MDB_KEY_RATINGCDR= "500";
	}
	
	public static class SessionMdbKey{
		public static final String MDB_KEY_SESSION = "200";
		public static final String MDB_KEY_SESSION_EXPIRE = "201";
	}
	
	//参数缓存KEY
	public static class ParamMdbKey{
		public static final String MDB_KEY_PARAM_POG_NAMES="300+POG";
		public static final String MDB_KEY_PARAM_POG_INFO="301";
		public static final String MDB_KEY_PARAM_POG_PO_SIZE="302";
		public static final String MDB_KEY_PARAM_POG_PO_DATA="303";
		public static final String MDB_KEY_PARAM_POG_PO_INDEX="304";
		public static final String MDB_KEY_PARAM_POG_PO_INDEX_DATA="305";
		public static final String MDB_KEY_PARAM_APP_POG_INFO="351";
		public static final String MDB_KEY_PARAM_APP_POG_PO_SIZE="352";
		public static final String MDB_KEY_PARAM_APP_POG_PO_DATA="353";
		public static final String MDB_KEY_PARAM_APP_POG_PO_INDEX="354";
		public static final String MDB_KEY_PARAM_APP_POG_VISITOR="370";
		public static final String MDB_KEY_PARAM_APP_POG_VISITOR_INFO="371";
	}
	
	//redis 
	public static final String EVALSHA_RATE_UPDATE = "573b29c2433b166edc52a25981a47fe17cd2fe9c";
	
}
