package acct;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.dao.MdbAddDao;
import com.ai.iot.mdb.common.rate.*;
import mdb.MdbKey;
import mdb.MdbValue;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcctTask implements Runnable {

  private int orderNo;
  private int planId;

  public AcctTask(int orderNo, int planId) {
    this.orderNo = orderNo;
    this.planId = planId;
  }

  @Override
  public void run() {
    System.out.println("正在执行 planId:"+planId +",orderNo:"+orderNo);
    try {
      createAcctData(orderNo,planId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(" planId:"+planId +",orderNo:"+orderNo+"执行完毕");
  }

  private static void createAcctData(int orderNo,int planId){

    int planVersionId=planId+1;
    int planType=(planId%10000)/1000;
    long acctId=3000000000000000L+ (planType) * 100000000000000L + orderNo*10000000L;
    long operAcctId=acctId+10;
    int poolId = planType==4? (30000000+planType * 1000000 + orderNo*10) : 0;

    System.out.println("acctId:"+acctId+" insert acct begin:"+ System.currentTimeMillis() );
//    deleteAcct(acctId); // 删除资料
    insertAcct(acctId,operAcctId,poolId); //账户资料
    System.out.println("acctId:"+acctId+" insert acct end:" + System.currentTimeMillis());
//    Map<MdbKey,MdbValue> mdbMap = new HashMap<>();
    //单个设备
    List<DeviceInfo> deviceInfoList = new ArrayList<>();
    for(int d=1;d<5001;d++){
      long deviceId=acctId+d*10L;
      long orderId=deviceId+1;

      DeviceInfo deviceInfo = new DeviceInfo(acctId,operAcctId,orderId,deviceId,poolId,planType,planId,planVersionId);
      deviceInfoList.add(deviceInfo);
//      createValue(acctId,orderId,deviceId,poolId,planId,mdbMap);
    }
    System.out.println("acctId:"+acctId + " insert device begin:" + System.currentTimeMillis());
    insertDevice(deviceInfoList); //设备资料
    System.out.println("acctId:"+acctId + " insert device end:" + System.currentTimeMillis());
//    try {
//      updateValue(mdbMap);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

  }

  private static void insertAcct(long acctId,long operAcctId,int poolId){

    int partitionId=(int)Math.floorMod(acctId,10000);

    DataSource ds =DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_CRM);
    try {
      String insertSql1 = " insert into TF_F_ACCT " +
              " (PARTITION_ID,ACCT_ID,ACCT_NAME,OPER_ACCTID,STATUS,REMOVE_TAG,REMOVE_TIME,SERVICE_PROVIDER_CODE,PROVINCE_CODE,CITY_CODE,DEPART_CODE) " +
              " values(?,?,'acctBatchTest',?,'0','0',null,'CUC','010','011','1') ";
      JdbcBaseDao.execsql(ds, insertSql1,partitionId,acctId,operAcctId);

      String insertSql2 = " insert into TF_F_ACCT_BILLING_GENERAL" +
              " (ACCT_ID,PARTITION_ID,BILLABLE,ACTIVATION_PRORATION,RENEWAL_PRORATION," +
              " DEFAULT_ACTIVATION_PLAN,DEFAULT_ACTIVATION_FEE," +
              " DEFAULT_PREPAID_RENEWAL_MODE,DEFAULT_PREPAID_RENEWAL_RATE_PLAN)" +
              " values(?,?,1,1,1,2,200,0,0) ";
      JdbcBaseDao.execsql(ds, insertSql2,acctId,partitionId);

      if(poolId>0){
        String insertSql3 = " insert into tf_f_sharepool " +
                " (POOL_ID,ACCT_ID,PLAN_ID,PLAN_VERSION_ID,RENEWAL_MODE,START_DATE,END_DATE) " +
                " values(?,?,10004340,10004341,2,STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20170926235959','%Y%m%d%H%i%s'))";
        JdbcBaseDao.execsql(ds, insertSql3,poolId,acctId);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  private static void insertDevice(List<DeviceInfo> deviceInfoList){

    String connCode = BaseDefine.CONNCODE_MYSQL_CRM;
    try {
      String insertSql1 = " insert into TF_F_DEVICE " +
              "(PARTITION_ID,DEVICE_ID,ICCID,ACCT_ID,OPER_ACCTID,MSISDN,IMEI,RECEIVING_CONFIRM_DATE,ACTIVATION_DATE,SHIPPED_DATE," +
              "RATE_PLAN_ID,PLAN_VERSION_ID,OVERAGE_LIMIT_REACHED,STATUS,IS_LOCKED,DELEGATER_USER_NAME)" +
              "values(#{partitionId},#{deviceId},'jflksjflsjl',#{acctId},#{operAcctId},'1862326988723','8866634376483'," +
              "STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20170206132442','%Y%m%d%H%i%s')," +
              "10005010,10005011,0,'2',0,'acctBatchTest')";
      JdbcBaseDao.execBatch(connCode, insertSql1,deviceInfoList);

      String insertSql2 = " insert into TF_F_DEVICE_STATE" +
              "(DEVICE_ID,PARTITION_ID,ACCT_ID,STATE_CODE,START_DATE,END_DATE)" +
              "values(#{deviceId},#{partitionId},#{acctId},'2',STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20501231235959','%Y%m%d%H%i%s'))";
      JdbcBaseDao.execBatch(connCode, insertSql2,deviceInfoList);

      String insertSql3 = " insert into TF_F_DEVICE_RATE_PLAN" +
              "(PARTITION_ID,RATE_ORDER_ID,DEVICE_ID,ACCT_ID,PLAN_TYPE,PLAN_ID,PLAN_VERSION_ID,POOL_ID," +
              "RENEWAL_MODE,ACTIVE_FLAG,STATE,ORDER_DATE,START_DATE,END_DATE)" +
              "values(#{partitionId},#{orderId},#{deviceId},#{acctId},#{planType},#{planId},#{planVersionId},#{poolId}," +
              "'2','1','1',STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20170523132442','%Y%m%d%H%i%s'),STR_TO_DATE('20170926235959','%Y%m%d%H%i%s'))";
      JdbcBaseDao.execBatch(connCode,insertSql3,deviceInfoList);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  private static void updateValue(Map<MdbKey,MdbValue> mdbMap) throws Exception{

    if(mdbMap==null || mdbMap.isEmpty() ){
      return;
    }

    for(MdbKey key : mdbMap.keySet()){
      MdbValue value=mdbMap.get(key);
      if(!value.getMdbBillPoolSum2GprsList().isEmpty()){
        MdbAddDao.setPoolAdd(key.getCycleId(),key.getAcctId(),key.getPoolId(),value.getMdbBillPoolSum2GprsList());
      }
      if(!value.getMdbBillUserSum1GprsList().isEmpty()||
              !value.getMdbBillUserSum1GsmList().isEmpty()||
              !value.getMdbBillUserSum1SmsList().isEmpty()||
              !value.getMdbBillUserSum2GprsList().isEmpty()||
              !value.getMdbBillUserSum2GsmList().isEmpty()||
              !value.getMdbBillUserSum2SmsList().isEmpty()){
        MdbAddDao.setDeviceAdd(key.getCycleId(),key.getAcctId(),key.getDeviceId(),
                value.getMdbBillUserSum1GprsList(),
                value.getMdbBillUserSum2GprsList(),
                value.getMdbBillUserSum1GsmList(),
                value.getMdbBillUserSum2GsmList(),
                value.getMdbBillUserSum1SmsList(),
                value.getMdbBillUserSum2SmsList());
      }
    }
  }



  private static void createValue(long acctId,long orderId,long deviceId,int poolId, int planId,
                           Map<MdbKey,MdbValue> mdbMap){

    int cycleId = 201707;
    int day = 20170726;
    MdbKey mdbKey=new MdbKey(acctId, deviceId,201707, poolId);
    MdbValue mdbValue;

    if(mdbMap.containsKey(mdbKey)){
      mdbValue =mdbMap.get(mdbKey);
    }else{
      mdbValue =new MdbValue();
      mdbMap.put(mdbKey,mdbValue);
    }
    List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList = mdbValue.getMdbBillUserSum1GprsList();
    List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList = mdbValue.getMdbBillUserSum2GprsList();
    List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList = mdbValue.getMdbBillUserSum1GsmList();
    List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList = mdbValue.getMdbBillUserSum2GsmList();
    List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList = mdbValue.getMdbBillUserSum1SmsList();
    List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList = mdbValue.getMdbBillUserSum2SmsList();

    switch (planId){
      case 10001100:
        MdbBillUserSum1Gprs sum1Gprs11 = new MdbBillUserSum1Gprs(orderId, 50001001, day, 5368708720L, 5368709120L);
        MdbBillUserSum1Gprs sum1Gprs12 = new MdbBillUserSum1Gprs(orderId, 50001002, day, 4294967196L, 4294967296L);
        MdbBillUserSum1Gprs sum1Gprs13 = new MdbBillUserSum1Gprs(orderId, 50001003, day, 6442450544L, 6442450944L);

        MdbBillUserSum1Gsm sum1Gsm11 = new MdbBillUserSum1Gsm(orderId, 50002001, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm12 = new MdbBillUserSum1Gsm(orderId, 50002002, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm13 = new MdbBillUserSum1Gsm(orderId, 50002003, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm14 = new MdbBillUserSum1Gsm(orderId, 50002012, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Sms sum1Sms11 = new MdbBillUserSum1Sms(orderId, 50003001, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms12 = new MdbBillUserSum1Sms(orderId, 50003002, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms13 = new MdbBillUserSum1Sms(orderId, 50003003, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms14 = new MdbBillUserSum1Sms(orderId, 50003004, day, 530, 530, 0, 530);

        mdbBillUserSum1GprsList.add(sum1Gprs11);
        mdbBillUserSum1GprsList.add(sum1Gprs12);
        mdbBillUserSum1GprsList.add(sum1Gprs13);

        mdbBillUserSum1GsmList.add(sum1Gsm11);
        mdbBillUserSum1GsmList.add(sum1Gsm12);
        mdbBillUserSum1GsmList.add(sum1Gsm13);
        mdbBillUserSum1GsmList.add(sum1Gsm14);

        mdbBillUserSum1SmsList.add(sum1Sms11);
        mdbBillUserSum1SmsList.add(sum1Sms12);
        mdbBillUserSum1SmsList.add(sum1Sms13);
        mdbBillUserSum1SmsList.add(sum1Sms14);
        break;
      case 10002120:
        MdbBillUserSum2Gprs sum2Gprs21 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001001, day, 2147483648L, 5368708320L, 5368709120L);
        MdbBillUserSum2Gprs sum2Gprs22 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001002, day, 2147483648L, 4294967096L, 4294967296L);
        MdbBillUserSum2Gprs sum2Gprs23 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001003, day, 2147483648L, 6442450844L, 6442450944L);

        mdbBillUserSum2GprsList.add(sum2Gprs21);
        mdbBillUserSum2GprsList.add(sum2Gprs22);
        mdbBillUserSum2GprsList.add(sum2Gprs23);

        MdbBillUserSum2Gsm sum2Gsm201 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002001, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm202 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002002, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm203 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002003, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm204 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002012, day, 9600, 18545, 18600, 0, 18600);

        MdbBillUserSum2Gsm sum2Gsm211 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002101, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm212 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002102, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm213 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002103, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm214 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002112, day, 9600, 18545, 18600, 0, 18600);

        MdbBillUserSum2Gsm sum2Gsm221 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002201, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm222 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002202, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm223 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002203, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm224 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002212, day, 9600, 18545, 18600, 0, 18600);

        mdbBillUserSum2GsmList.add(sum2Gsm201);
        mdbBillUserSum2GsmList.add(sum2Gsm202);
        mdbBillUserSum2GsmList.add(sum2Gsm203);
        mdbBillUserSum2GsmList.add(sum2Gsm204);
        mdbBillUserSum2GsmList.add(sum2Gsm211);
        mdbBillUserSum2GsmList.add(sum2Gsm212);
        mdbBillUserSum2GsmList.add(sum2Gsm213);
        mdbBillUserSum2GsmList.add(sum2Gsm214);
        mdbBillUserSum2GsmList.add(sum2Gsm221);
        mdbBillUserSum2GsmList.add(sum2Gsm222);
        mdbBillUserSum2GsmList.add(sum2Gsm223);
        mdbBillUserSum2GsmList.add(sum2Gsm224);

        MdbBillUserSum2Sms sum2Sms201 = new MdbBillUserSum2Sms(cycleId, orderId, 50003001, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms202 = new MdbBillUserSum2Sms(cycleId, orderId, 50003002, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms203 = new MdbBillUserSum2Sms(cycleId, orderId, 50003003, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms204 = new MdbBillUserSum2Sms(cycleId, orderId, 50003012, day, 170, 530, 530, 0, 530);

        MdbBillUserSum2Sms sum2Sms211 = new MdbBillUserSum2Sms(cycleId, orderId, 50003101, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms212 = new MdbBillUserSum2Sms(cycleId, orderId, 50003102, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms213 = new MdbBillUserSum2Sms(cycleId, orderId, 50003103, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms214 = new MdbBillUserSum2Sms(cycleId, orderId, 50003112, day, 170, 530, 530, 0, 530);

        MdbBillUserSum2Sms sum2Sms221 = new MdbBillUserSum2Sms(cycleId, orderId, 50003201, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms222 = new MdbBillUserSum2Sms(cycleId, orderId, 50003202, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms223 = new MdbBillUserSum2Sms(cycleId, orderId, 50003203, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms224 = new MdbBillUserSum2Sms(cycleId, orderId, 50003212, day, 170, 530, 530, 0, 530);

        mdbBillUserSum2SmsList.add(sum2Sms201);
        mdbBillUserSum2SmsList.add(sum2Sms202);
        mdbBillUserSum2SmsList.add(sum2Sms203);
        mdbBillUserSum2SmsList.add(sum2Sms204);
        mdbBillUserSum2SmsList.add(sum2Sms211);
        mdbBillUserSum2SmsList.add(sum2Sms212);
        mdbBillUserSum2SmsList.add(sum2Sms213);
        mdbBillUserSum2SmsList.add(sum2Sms214);
        mdbBillUserSum2SmsList.add(sum2Sms221);
        mdbBillUserSum2SmsList.add(sum2Sms222);
        mdbBillUserSum2SmsList.add(sum2Sms223);
        mdbBillUserSum2SmsList.add(sum2Sms224);
        break;
      case 10003340:
        MdbBillUserSum1Gprs sum1Gprs31 = new MdbBillUserSum1Gprs(orderId, 50001001, day, 5368708720L, 5368709120L);
        MdbBillUserSum1Gprs sum1Gprs32 = new MdbBillUserSum1Gprs(orderId, 50001002, day, 4294967196L, 4294967296L);
        MdbBillUserSum1Gprs sum1Gprs33 = new MdbBillUserSum1Gprs(orderId, 50001003, day, 6442450544L, 6442450944L);

        MdbBillUserSum1Gsm sum1Gsm301 = new MdbBillUserSum1Gsm(orderId, 50002001, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm302 = new MdbBillUserSum1Gsm(orderId, 50002002, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm303 = new MdbBillUserSum1Gsm(orderId, 50002003, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm304 = new MdbBillUserSum1Gsm(orderId, 50002012, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Gsm sum1Gsm311 = new MdbBillUserSum1Gsm(orderId, 50002101, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm312 = new MdbBillUserSum1Gsm(orderId, 50002102, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm313 = new MdbBillUserSum1Gsm(orderId, 50002103, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm314 = new MdbBillUserSum1Gsm(orderId, 50002112, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Gsm sum1Gsm321 = new MdbBillUserSum1Gsm(orderId, 50002201, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm322 = new MdbBillUserSum1Gsm(orderId, 50002202, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm323 = new MdbBillUserSum1Gsm(orderId, 50002203, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm324 = new MdbBillUserSum1Gsm(orderId, 50002212, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Sms sum1Sms301 = new MdbBillUserSum1Sms(orderId, 50003001, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms302 = new MdbBillUserSum1Sms(orderId, 50003002, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms303 = new MdbBillUserSum1Sms(orderId, 50003003, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms304 = new MdbBillUserSum1Sms(orderId, 50003004, day, 530, 530, 0, 530);

        MdbBillUserSum1Sms sum1Sms311 = new MdbBillUserSum1Sms(orderId, 50003101, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms312 = new MdbBillUserSum1Sms(orderId, 50003102, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms313 = new MdbBillUserSum1Sms(orderId, 50003103, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms314 = new MdbBillUserSum1Sms(orderId, 50003104, day, 530, 530, 0, 530);

        MdbBillUserSum1Sms sum1Sms321 = new MdbBillUserSum1Sms(orderId, 50003201, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms322 = new MdbBillUserSum1Sms(orderId, 50003202, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms323 = new MdbBillUserSum1Sms(orderId, 50003203, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms324 = new MdbBillUserSum1Sms(orderId, 50003204, day, 530, 530, 0, 530);

        mdbBillUserSum1GprsList.add(sum1Gprs31);
        mdbBillUserSum1GprsList.add(sum1Gprs32);
        mdbBillUserSum1GprsList.add(sum1Gprs33);

        mdbBillUserSum1GsmList.add(sum1Gsm301);
        mdbBillUserSum1GsmList.add(sum1Gsm302);
        mdbBillUserSum1GsmList.add(sum1Gsm303);
        mdbBillUserSum1GsmList.add(sum1Gsm304);
        mdbBillUserSum1GsmList.add(sum1Gsm311);
        mdbBillUserSum1GsmList.add(sum1Gsm312);
        mdbBillUserSum1GsmList.add(sum1Gsm313);
        mdbBillUserSum1GsmList.add(sum1Gsm314);
        mdbBillUserSum1GsmList.add(sum1Gsm321);
        mdbBillUserSum1GsmList.add(sum1Gsm322);
        mdbBillUserSum1GsmList.add(sum1Gsm323);
        mdbBillUserSum1GsmList.add(sum1Gsm324);

        mdbBillUserSum1SmsList.add(sum1Sms301);
        mdbBillUserSum1SmsList.add(sum1Sms302);
        mdbBillUserSum1SmsList.add(sum1Sms303);
        mdbBillUserSum1SmsList.add(sum1Sms304);
        mdbBillUserSum1SmsList.add(sum1Sms311);
        mdbBillUserSum1SmsList.add(sum1Sms312);
        mdbBillUserSum1SmsList.add(sum1Sms313);
        mdbBillUserSum1SmsList.add(sum1Sms314);
        mdbBillUserSum1SmsList.add(sum1Sms321);
        mdbBillUserSum1SmsList.add(sum1Sms322);
        mdbBillUserSum1SmsList.add(sum1Sms323);
        mdbBillUserSum1SmsList.add(sum1Sms324);
        break;
      case 10004340:
        MdbBillUserSum2Gprs sum2Gprs41 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001001, day, 2147483648L, 5368708320L, 5368709120L);
        MdbBillUserSum2Gprs sum2Gprs42 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001002, day, 2147483648L, 4294967096L, 4294967296L);
        MdbBillUserSum2Gprs sum2Gprs43 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001003, day, 2147483648L, 6442450844L, 6442450944L);

        MdbBillUserSum2Gsm sum2Gsm41 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002001, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm42 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002002, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm43 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002003, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm44 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002012, day, 9600, 18545, 18600, 0, 18600);

        MdbBillUserSum2Sms sum2Sms41 = new MdbBillUserSum2Sms(cycleId, orderId, 50003001, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms42 = new MdbBillUserSum2Sms(cycleId, orderId, 50003002, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms43 = new MdbBillUserSum2Sms(cycleId, orderId, 50003003, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms44 = new MdbBillUserSum2Sms(cycleId, orderId, 50003012, day, 170, 530, 530, 0, 530);

        mdbBillUserSum2GprsList.add(sum2Gprs41);
        mdbBillUserSum2GprsList.add(sum2Gprs42);
        mdbBillUserSum2GprsList.add(sum2Gprs43);
        mdbBillUserSum2GsmList.add(sum2Gsm41);
        mdbBillUserSum2GsmList.add(sum2Gsm42);
        mdbBillUserSum2GsmList.add(sum2Gsm43);
        mdbBillUserSum2GsmList.add(sum2Gsm44);
        mdbBillUserSum2SmsList.add(sum2Sms41);
        mdbBillUserSum2SmsList.add(sum2Sms42);
        mdbBillUserSum2SmsList.add(sum2Sms43);
        mdbBillUserSum2SmsList.add(sum2Sms44);
        break;
      case 10005340:
        MdbBillUserSum1Gprs sum1Gprs51 = new MdbBillUserSum1Gprs(orderId, 50001001, day, 5368708720L, 5368709120L);
        MdbBillUserSum1Gprs sum1Gprs52 = new MdbBillUserSum1Gprs(orderId, 50001002, day, 4294967196L, 4294967296L);
        MdbBillUserSum1Gprs sum1Gprs53 = new MdbBillUserSum1Gprs(orderId, 50001003, day, 6442450544L, 6442450944L);

        MdbBillUserSum1Gsm sum1Gsm501 = new MdbBillUserSum1Gsm(orderId, 50002001, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm502 = new MdbBillUserSum1Gsm(orderId, 50002002, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm503 = new MdbBillUserSum1Gsm(orderId, 50002003, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm504 = new MdbBillUserSum1Gsm(orderId, 50002012, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Gsm sum1Gsm511 = new MdbBillUserSum1Gsm(orderId, 50002101, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm512 = new MdbBillUserSum1Gsm(orderId, 50002102, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm513 = new MdbBillUserSum1Gsm(orderId, 50002103, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm514 = new MdbBillUserSum1Gsm(orderId, 50002112, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Gsm sum1Gsm521 = new MdbBillUserSum1Gsm(orderId, 50002201, day, 18545, 18600, 18600, 0);
        MdbBillUserSum1Gsm sum1Gsm522 = new MdbBillUserSum1Gsm(orderId, 50002202, day, 30573, 30600, 30600, 0);
        MdbBillUserSum1Gsm sum1Gsm523 = new MdbBillUserSum1Gsm(orderId, 50002203, day, 50978, 51000, 51000, 0);
        MdbBillUserSum1Gsm sum1Gsm524 = new MdbBillUserSum1Gsm(orderId, 50002212, day, 18545, 18600, 0, 18600);

        MdbBillUserSum1Sms sum1Sms501 = new MdbBillUserSum1Sms(orderId, 50003001, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms502 = new MdbBillUserSum1Sms(orderId, 50003002, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms503 = new MdbBillUserSum1Sms(orderId, 50003003, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms504 = new MdbBillUserSum1Sms(orderId, 50003004, day, 530, 530, 0, 530);

        MdbBillUserSum1Sms sum1Sms511 = new MdbBillUserSum1Sms(orderId, 50003101, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms512 = new MdbBillUserSum1Sms(orderId, 50003102, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms513 = new MdbBillUserSum1Sms(orderId, 50003103, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms514 = new MdbBillUserSum1Sms(orderId, 50003104, day, 530, 530, 0, 530);

        MdbBillUserSum1Sms sum1Sms521 = new MdbBillUserSum1Sms(orderId, 50003201, day, 250, 250, 250, 0);
        MdbBillUserSum1Sms sum1Sms522 = new MdbBillUserSum1Sms(orderId, 50003202, day, 410, 410, 410, 0);
        MdbBillUserSum1Sms sum1Sms523 = new MdbBillUserSum1Sms(orderId, 50003203, day, 670, 670, 670, 0);
        MdbBillUserSum1Sms sum1Sms524 = new MdbBillUserSum1Sms(orderId, 50003204, day, 530, 530, 0, 530);

        mdbBillUserSum1GprsList.add(sum1Gprs51);
        mdbBillUserSum1GprsList.add(sum1Gprs52);
        mdbBillUserSum1GprsList.add(sum1Gprs53);

        mdbBillUserSum1GsmList.add(sum1Gsm501);
        mdbBillUserSum1GsmList.add(sum1Gsm502);
        mdbBillUserSum1GsmList.add(sum1Gsm503);
        mdbBillUserSum1GsmList.add(sum1Gsm504);
        mdbBillUserSum1GsmList.add(sum1Gsm511);
        mdbBillUserSum1GsmList.add(sum1Gsm512);
        mdbBillUserSum1GsmList.add(sum1Gsm513);
        mdbBillUserSum1GsmList.add(sum1Gsm514);
        mdbBillUserSum1GsmList.add(sum1Gsm521);
        mdbBillUserSum1GsmList.add(sum1Gsm522);
        mdbBillUserSum1GsmList.add(sum1Gsm523);
        mdbBillUserSum1GsmList.add(sum1Gsm524);

        mdbBillUserSum1SmsList.add(sum1Sms501);
        mdbBillUserSum1SmsList.add(sum1Sms502);
        mdbBillUserSum1SmsList.add(sum1Sms503);
        mdbBillUserSum1SmsList.add(sum1Sms504);
        mdbBillUserSum1SmsList.add(sum1Sms511);
        mdbBillUserSum1SmsList.add(sum1Sms512);
        mdbBillUserSum1SmsList.add(sum1Sms513);
        mdbBillUserSum1SmsList.add(sum1Sms514);
        mdbBillUserSum1SmsList.add(sum1Sms521);
        mdbBillUserSum1SmsList.add(sum1Sms522);
        mdbBillUserSum1SmsList.add(sum1Sms523);
        mdbBillUserSum1SmsList.add(sum1Sms524);
        break;
      case 10006340:
        MdbBillUserSum2Gprs sum2Gprs61 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001001, day, 2147483648L, 5368708320L, 5368709120L);
        MdbBillUserSum2Gprs sum2Gprs62 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001002, day, 2147483648L, 4294967096L, 4294967296L);
        MdbBillUserSum2Gprs sum2Gprs63 = new MdbBillUserSum2Gprs(cycleId, orderId, 50001003, day, 2147483648L, 6442450844L, 6442450944L);

        mdbBillUserSum2GprsList.add(sum2Gprs61);
        mdbBillUserSum2GprsList.add(sum2Gprs62);
        mdbBillUserSum2GprsList.add(sum2Gprs63);

        MdbBillUserSum2Gsm sum2Gsm601 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002001, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm602 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002002, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm603 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002003, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm604 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002012, day, 9600, 18545, 18600, 0, 18600);

        MdbBillUserSum2Gsm sum2Gsm611 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002101, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm612 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002102, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm613 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002103, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm614 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002112, day, 9600, 18545, 18600, 0, 18600);

        MdbBillUserSum2Gsm sum2Gsm621 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002201, day, 9000, 18545, 18600, 18600, 0);
        MdbBillUserSum2Gsm sum2Gsm622 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002202, day, 18600, 30573, 30600, 30600, 0);
        MdbBillUserSum2Gsm sum2Gsm623 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002203, day, 30600, 50978, 51000, 51000, 0);
        MdbBillUserSum2Gsm sum2Gsm624 = new MdbBillUserSum2Gsm(cycleId, orderId, 50002212, day, 9600, 18545, 18600, 0, 18600);

        mdbBillUserSum2GsmList.add(sum2Gsm601);
        mdbBillUserSum2GsmList.add(sum2Gsm602);
        mdbBillUserSum2GsmList.add(sum2Gsm603);
        mdbBillUserSum2GsmList.add(sum2Gsm604);
        mdbBillUserSum2GsmList.add(sum2Gsm611);
        mdbBillUserSum2GsmList.add(sum2Gsm612);
        mdbBillUserSum2GsmList.add(sum2Gsm613);
        mdbBillUserSum2GsmList.add(sum2Gsm614);
        mdbBillUserSum2GsmList.add(sum2Gsm621);
        mdbBillUserSum2GsmList.add(sum2Gsm622);
        mdbBillUserSum2GsmList.add(sum2Gsm623);
        mdbBillUserSum2GsmList.add(sum2Gsm624);

        MdbBillUserSum2Sms sum2Sms601 = new MdbBillUserSum2Sms(cycleId, orderId, 50003001, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms602 = new MdbBillUserSum2Sms(cycleId, orderId, 50003002, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms603 = new MdbBillUserSum2Sms(cycleId, orderId, 50003003, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms604 = new MdbBillUserSum2Sms(cycleId, orderId, 50003012, day, 170, 530, 530, 0, 530);

        MdbBillUserSum2Sms sum2Sms611 = new MdbBillUserSum2Sms(cycleId, orderId, 50003101, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms612 = new MdbBillUserSum2Sms(cycleId, orderId, 50003102, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms613 = new MdbBillUserSum2Sms(cycleId, orderId, 50003103, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms614 = new MdbBillUserSum2Sms(cycleId, orderId, 50003112, day, 170, 530, 530, 0, 530);

        MdbBillUserSum2Sms sum2Sms621 = new MdbBillUserSum2Sms(cycleId, orderId, 50003201, day, 123, 250, 250, 250, 0);
        MdbBillUserSum2Sms sum2Sms622 = new MdbBillUserSum2Sms(cycleId, orderId, 50003202, day, 220, 410, 410, 410, 0);
        MdbBillUserSum2Sms sum2Sms623 = new MdbBillUserSum2Sms(cycleId, orderId, 50003203, day, 300, 670, 670, 670, 0);
        MdbBillUserSum2Sms sum2Sms624 = new MdbBillUserSum2Sms(cycleId, orderId, 50003212, day, 170, 530, 530, 0, 530);

        mdbBillUserSum2SmsList.add(sum2Sms601);
        mdbBillUserSum2SmsList.add(sum2Sms602);
        mdbBillUserSum2SmsList.add(sum2Sms603);
        mdbBillUserSum2SmsList.add(sum2Sms604);
        mdbBillUserSum2SmsList.add(sum2Sms611);
        mdbBillUserSum2SmsList.add(sum2Sms612);
        mdbBillUserSum2SmsList.add(sum2Sms613);
        mdbBillUserSum2SmsList.add(sum2Sms614);
        mdbBillUserSum2SmsList.add(sum2Sms621);
        mdbBillUserSum2SmsList.add(sum2Sms622);
        mdbBillUserSum2SmsList.add(sum2Sms623);
        mdbBillUserSum2SmsList.add(sum2Sms624);
        break;
      default:
        break;
    }


  }




}
