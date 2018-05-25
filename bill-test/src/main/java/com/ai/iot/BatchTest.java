package com.ai.iot;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchTest {

  private static final Logger logger = LoggerFactory.getLogger(BatchTest.class);
  /**
   * 创建账户资料、设备资料、累积量等数据
   */
  public static void main(String args[]) {

//    int planList[] ={10001100,10002120,10003340,10004340,10005340,10006340};
    int planList[] ={10001100};

    int comAcctNum = 2000;
    int bigAcctNum = 20;
    int supperAcctNum = 2;

    int comDeviceNum = 5000;
    int bigDeviceNum = 100000;
    int supperDeviceNum = 1000000;

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(300);
    //资费轮询
    int totalAcctNum = comAcctNum+bigAcctNum+supperAcctNum;
    for(int pos=0;pos<planList.length;pos++){
      int planId=planList[pos];

      int planType=(planId%10000)/1000;
      long startAcctId=3000000000000000L+ (planType) * 100000000000000L;
      long endAcctId = 3000000000000000L+ (planType+1) * 100000000000000L;

//      deleteAcct(startAcctId,endAcctId);
      //单个账户生成
      for(int a=1;a<=totalAcctNum;a++){
        int deviceNum = bigDeviceNum;
        if(a<=comAcctNum){
          deviceNum = comDeviceNum;
        }else if(a>comAcctNum+bigAcctNum){
          deviceNum = supperDeviceNum;
        }
        AcctTask acctTask = new AcctTask(planId,a,deviceNum);
        fixedThreadPool.execute(acctTask);
//        System.out.println(acctTask);
      }
    }

    fixedThreadPool.shutdown();
    while (!fixedThreadPool.isTerminated()){
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }


  private static void deleteAcct(long startAcctId,long endAcctId){

    DataSource ds = DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_CRM);
    try {
      String deleteSql1 = " delete from TF_F_ACCT where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql1,startAcctId,endAcctId);
      String deleteSql2 = " delete from TF_F_ACCT_BILLING_GENERAL where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql2,startAcctId,endAcctId);
      String deleteSql3 = " delete from TF_F_DEVICE where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql3,startAcctId,endAcctId);
      String deleteSql4 = " delete from TF_F_DEVICE_STATE where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql4,startAcctId,endAcctId);
      String deleteSql5 = " delete from TF_F_DEVICE_RATE_PLAN where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql5,startAcctId,endAcctId);
      String deleteSql6 = " delete from tf_f_sharepool where ACCT_ID BETWEEN ? and ? ";
      JdbcBaseDao.execsql(ds, deleteSql6,startAcctId,endAcctId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
