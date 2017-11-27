package com.ai.iot.bill.common.mgr;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.mq.ZkMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by geyunfeng on 2017/7/10.
 */
public class ZkMgrTest {

    public static void main(String[] args) throws Exception{

        ZkMgr.getPartitionLock(BaseDefine.CONNTYPE_ZK_M2F,"abc",2);
    }


    @Test
    public void getJedisCluster() {
        CustJedisCluster custJedisCluster= RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_RATING);

        try {
            custJedisCluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
