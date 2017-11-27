package com.ai.iot.bill.common.mdb;

import org.junit.Test;

import com.ai.iot.bill.common.param.BaseDefine;

public class RedisMgrTest {

    @Test
    public void redisSlaveTest(){
        
        int routeID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        CustJedisCluster cjc = RedisMgr.getJedisCluster(routeID);
        cjc.setSlaveOnly();
        
        cjc.set("zftest".getBytes(), "test".getBytes());
        @SuppressWarnings("unused")
		String val = new String((cjc.get("zftest".getBytes())));
        
    }
}
