package com.ai.iot.bill.common;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.ai.iot.bill.common.mdb.BloomFilter;
import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.param.BaseDefine;

public class BloomFilterTest {

    private CustJedisCluster cjc = null;
    
    @Before
    public void setUp() throws Exception {
        int routeID = BaseDefine.CONNTYPE_REDIS_DEVINFO;
        cjc = RedisMgr.getJedisCluster(routeID);
    }

    @Test
    public void bloomFilterNormalTest() {
        
        String cdrStartDate = "20170717";
        String guid = "1234";
        String fid = "test_file";
        int repeatTimes = 0;
        
        BloomFilter bf = new BloomFilter();
        int res = bf.bloomFilter(cjc, cdrStartDate, guid, fid, repeatTimes);
        assertEquals(0, res);
        
        res = bf.bloomFilter(cjc, cdrStartDate, guid, fid, repeatTimes);
        assertEquals(1, res);
    }
    
    @Test
    public void bloomFilterNormalTest2() {
        
        String cdrStartDate = "20170717";
        String guid = "1234";
        String fid = "test_file";
        int repeatTimes = 1;
        
        BloomFilter bf = new BloomFilter();
        int res = bf.bloomFilter(cjc, cdrStartDate, guid, fid, repeatTimes);
        assertEquals(0, res);
        
    }

}
