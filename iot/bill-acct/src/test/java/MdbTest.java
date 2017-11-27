import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.dao.MdbAddDao;
import com.ai.iot.bill.dealproc.ParamMgr;
import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddPoolTotal;
import com.ai.iot.mdb.common.rate.MdbBillPoolSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Sms;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Sms;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.util.JedisClusterCRC16;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by geyunfeng on 2017/8/15.
 */
public class MdbTest {

  private void clearDeviceAdd(long deviceId) {

    CustJedisCluster custJedisCluster = RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_RATING);
    String key = BillEnum.BizTable.DATA + "+" + deviceId;
    custJedisCluster.del(key.getBytes());

    key = BillEnum.BizTable.SMS + "+" + deviceId;
    custJedisCluster.del(key.getBytes());

    key = BillEnum.BizTable.VOICE + "+" + deviceId;
    custJedisCluster.del(key.getBytes());
  }

  private void clearPoolAdd(long acctId,long poolId) {

    CustJedisCluster custJedisCluster = RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_RATING);
    String key = BillEnum.BizTable.POOL + "+" + acctId + "+" + poolId ;
    custJedisCluster.del(key.getBytes());

  }

  private void iniDeviceData(int cycleId,int day,List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList,
                          List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList,
                          List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList,
                          List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList,
                          List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList,
                          List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList){

    MdbBillUserSum1Gprs Sum1Gprs1 = new MdbBillUserSum1Gprs();
    Sum1Gprs1.setRatePlanInsId(1L);
    Sum1Gprs1.setDay(day);
    Sum1Gprs1.setBillId(1001);
    Sum1Gprs1.setValue(629145324L);
    Sum1Gprs1.setRoundValue(629145600L);
    MdbBillUserSum1Gprs Sum1Gprs2 = new MdbBillUserSum1Gprs();
    Sum1Gprs2.setRatePlanInsId(4L);
    Sum1Gprs2.setDay(day);
    Sum1Gprs2.setBillId(1001);
    Sum1Gprs2.setValue(629145324L);
    Sum1Gprs2.setRoundValue(629145600L);
    mdbBillUserSum1GprsList.add(Sum1Gprs1);
    mdbBillUserSum1GprsList.add(Sum1Gprs2);

    MdbBillUserSum1Gsm  Sum1Gsm1 = new MdbBillUserSum1Gsm();
    Sum1Gsm1.setRatePlanInsId(1L);
    Sum1Gsm1.setDay(day);
    Sum1Gsm1.setBillId(2011);
    Sum1Gsm1.setMoValue(18360);
    Sum1Gsm1.setMtValue(18420);
    Sum1Gsm1.setRoundValue(36780);
    Sum1Gsm1.setValue(36743);
    MdbBillUserSum1Gsm  Sum1Gsm2 = new MdbBillUserSum1Gsm();
    Sum1Gsm2.setRatePlanInsId(1L);
    Sum1Gsm2.setDay(day);
    Sum1Gsm2.setBillId(2012);
    Sum1Gsm2.setMoValue(18360);
    Sum1Gsm2.setMtValue(18420);
    Sum1Gsm2.setRoundValue(36780);
    Sum1Gsm2.setValue(36743);
    mdbBillUserSum1GsmList.add(Sum1Gsm1);
    mdbBillUserSum1GsmList.add(Sum1Gsm2);

    MdbBillUserSum1Sms Sum1Sms1 = new MdbBillUserSum1Sms();
    Sum1Sms1.setRatePlanInsId(1L);
    Sum1Sms1.setDay(day);
    Sum1Sms1.setBillId(3011);
    Sum1Sms1.setMoValue(345);
    Sum1Sms1.setMtValue(543);
    Sum1Sms1.setRoundValue(888);
    Sum1Sms1.setValue(888);
    MdbBillUserSum1Sms Sum1Sms2 = new MdbBillUserSum1Sms();
    Sum1Sms2.setRatePlanInsId(1L);
    Sum1Sms2.setDay(day);
    Sum1Sms2.setBillId(3012);
    Sum1Sms2.setMoValue(345);
    Sum1Sms2.setMtValue(543);
    Sum1Sms2.setRoundValue(888);
    Sum1Sms2.setValue(888);
    mdbBillUserSum1SmsList.add(Sum1Sms1);
    mdbBillUserSum1SmsList.add(Sum1Sms2);
  }

  private void iniPoolData(int cycleId,int day,List<MdbBillPoolSum2Gprs> mdbBillPoolSum2GprsList){

    MdbBillPoolSum2Gprs Sum2Gprs1 = new MdbBillPoolSum2Gprs();
    Sum2Gprs1.setCycleId(cycleId);
    Sum2Gprs1.setDay(day);
    Sum2Gprs1.setRatePlanVersionId(10004001);
    Sum2Gprs1.setBillId(1001);
    Sum2Gprs1.setValue(1887436432L);
    Sum2Gprs1.setRoundValue(1887436800L);
    Sum2Gprs1.setBaseValue(188743680L);
    mdbBillPoolSum2GprsList.add(Sum2Gprs1);
  }

//  @Test
  public void setDeviceAddTest() throws Exception{

    int cycleId=201707;
    int day=20170731;
    long acctId=3000000001000111L;
    long deviceId=1000000001000111L;
    int poolId=21000111;

    List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList = new ArrayList<>();
    List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList = new ArrayList<>();
    List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList = new ArrayList<>();
    List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList = new ArrayList<>();
    List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList = new ArrayList<>();
    List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList = new ArrayList<>();

    clearDeviceAdd(deviceId);

    iniDeviceData(cycleId,day,mdbBillUserSum1GprsList,mdbBillUserSum2GprsList,
        mdbBillUserSum1GsmList,mdbBillUserSum2GsmList,
        mdbBillUserSum1SmsList,mdbBillUserSum2SmsList);

    MdbAddDao.setDeviceAdd(cycleId,acctId,deviceId,
        mdbBillUserSum1GprsList,mdbBillUserSum2GprsList,
        mdbBillUserSum1GsmList,mdbBillUserSum2GsmList,
        mdbBillUserSum1SmsList,mdbBillUserSum2SmsList);

    List<MdbBillPoolSum2Gprs> mdbBillPoolSum2GprsList = new ArrayList<>();
    iniPoolData(cycleId,day,mdbBillPoolSum2GprsList);

    clearPoolAdd(acctId,poolId);
    MdbAddDao.setPoolAdd(cycleId,acctId,poolId,mdbBillPoolSum2GprsList);
  }

  @Test
  public void getAddTest() throws Exception {

      long acctId=3100000010000000L;
      long deviceId=3100000010040780L;
      long poolId=0L;

      List<UsedAddDevice> usedAddDeviceList= MdbAddDao.getDeviceAdd(201707,acctId,deviceId);
      System.out.println(usedAddDeviceList);

      List<UsedAddPoolTotal> usedAddPoolTotalList = MdbAddDao.getAcctAdd(201707,acctId,poolId);
      System.out.println(usedAddPoolTotalList);
  }

//  @Test
  public void batchTest(){
    CustJedisCluster custJedisCluster = RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_RATING);
    Map<String, JedisPool> nodeMap = custJedisCluster.getClusterNodes();
    String anyHost = nodeMap.keySet().iterator().next();
    TreeMap<Long, String> slotHostMap= getSlotHostMap(anyHost);

    String key = "430+1000000001000111";

    int slot = JedisClusterCRC16.getSlot(key);

    Map.Entry<Long, String> entry = slotHostMap.lowerEntry(Long.valueOf(slot));

    Jedis jedis = nodeMap.get(entry.getValue()).getResource();
    Pipeline p = jedis.pipelined();

    p.hgetAll(key.getBytes());

    List<Object> list=p.syncAndReturnAll();
    if(list != null && !list.isEmpty()){
      for(Object t :list){
        Map<byte[], byte[]> dataMap=(Map<byte[], byte[]>)t;

        System.out.println(dataMap.size());
        for(Map.Entry<byte[],byte[]> m : dataMap.entrySet()) {
          try{
            System.out.println(new String(m.getKey()));

          }catch(Exception e){
            continue;
          }
        }
      }

    }

  }

  private static TreeMap<Long, String> getSlotHostMap(String anyHostAndPortStr) {
    TreeMap<Long, String> tree = new TreeMap<>();
    String parts[] = anyHostAndPortStr.split(":");
    HostAndPort anyHostAndPort = new HostAndPort(parts[0], Integer.parseInt(parts[1]));
    try{
      Jedis jedis = new Jedis(anyHostAndPort.getHost(), anyHostAndPort.getPort());
      List<Object> list = jedis.clusterSlots();
      for (Object object : list) {
        List<Object> list1 = (List<Object>) object;
        List<Object> master = (List<Object>) list1.get(2);
        String hostAndPort = new String((byte[]) master.get(0)) + ":" + master.get(1);
        tree.put((Long) list1.get(0), hostAndPort);
        tree.put((Long) list1.get(1), hostAndPort);
      }
      jedis.close();
    }catch(Exception e){

    }
    return tree;
  }


}
