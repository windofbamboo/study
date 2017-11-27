package com.ai.iot.mdb.common.session;

import java.util.HashMap;
import java.util.Map;

import com.ai.iot.bill.common.param.BaseParamDao;

public class SessionUtil {

	@SuppressWarnings("rawtypes")
	public static Map getConf(String paramType) {
		Map conf = new HashMap();
		BaseParamDao.getConf(conf, paramType);
		return conf;
	}
}
