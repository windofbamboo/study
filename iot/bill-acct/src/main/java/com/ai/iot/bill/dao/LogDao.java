package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.db.XmlBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.define.BillEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.entity.log.AcctLog;
import com.ai.iot.bill.entity.log.DeviceLog;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**日志操作
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class LogDao {

  private static final Logger logger = LoggerFactory.getLogger(LogDao.class);

  private LogDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 按账户获取所有的设备处理信息，用于判断账户的处理进度
   */
  public static int getDeviceLogNum(final long acctId,final long dealId) {

    String connCode = BaseDefine.CONNCODE_MYSQL_PARAM;
    try {
      String sqlStr = " select count(*) from TL_B_DEVICE " +
              " where DEAL_ID=? and ACCT_ID=? and "+
              " (DEAL_STAGE!="+BillEnum.DealStage.DEVICE_DEAL_END+" or (deal_tag!="+ErrEnum.DealTag.SUCCESS+" and deal_tag!="+ErrEnum.DealTag.ERR+" ))";
      return JdbcBaseDao.getCount(connCode, sqlStr, dealId, acctId);
    } catch (Exception e) {
      logger.error("getPlatSmsValue err.{} ", e);
      return 0;
    }

  }

  /**
   * 按账户插入设备处理信息，用于账户阶段，记录下所有的设备信息
   */
  public static void insertDeviceLog(List<DeviceLog> deviceLogList) {

    String connCode = BaseDefine.CONNCODE_MYSQL_PARAM;
    try {
      XmlBaseDao.execBatch(connCode, "logMapper.insertDeviceLog", deviceLogList);
    } catch (Exception e) {
      logger.error("insertDeviceLog err.{} ", e);
    }
  }

  public static void updateDeviceLog(final long dealId, final long acctId, final long deviceId,ErrEnum.DealResult dealResult) {

    int dealStage;
    String dealTag,sqlStr;
    StringBuilder dealInfo = new StringBuilder();

    if(ErrEnum.DEAL_START.equals(dealResult)) {
      dealStage = BillEnum.DealStage.DEVICE_DEAL_START;
      dealTag = ErrEnum.DealTag.START;
      dealInfo.append(dealResult.getResultInfo());

      sqlStr =" update TL_B_DEVICE " +
              "    set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?, " +
              "        START_TIME=sysdate(),UPDATE_TIME=sysdate() " +
              "  where ACCT_ID=? and DEVICE_ID=? and DEAL_ID=? ";
    }else{
      if(ErrEnum.DEAL_SUCESS.equals(dealResult)){
        dealTag   = ErrEnum.DealTag.SUCCESS;
        dealInfo.append(dealResult.getResultInfo());
      }else{
        dealTag = ErrEnum.DealTag.ERR;
        dealInfo.append("err_no : ");
        dealInfo.append(dealResult.getResultNo());
        dealInfo.append(",err_info : ");
        dealInfo.append(dealResult.getResultInfo());
      }
      dealStage = BillEnum.DealStage.DEVICE_DEAL_END;

      sqlStr =" update TL_B_DEVICE " +
              "    set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?, " +
              "        END_TIME=sysdate(),UPDATE_TIME=sysdate() " +
              "  where ACCT_ID=? and DEVICE_ID=? and DEAL_ID=? ";
    }
    execSqlBase(sqlStr,dealStage,
            dealTag,
            dealInfo.toString(),
            acctId,
            deviceId,
            dealId);
  }


  public static void updateDeviceDealTimes(final long dealId,
                                           final long acctId,
                                           final long deviceId){

    String sqlStr =" UPDATE TL_B_DEVICE SET DEAL_TIMES = DEAL_TIMES +1 ,START_TIME = sysdate(),DEAL_STAGE = ?,DEAL_TAG = ? " +
                   " WHERE DEAL_ID=? AND ACCT_ID = ? AND DEVICE_ID = ? ";

    execSqlBase(sqlStr,
                          BillEnum.DealStage.DEVICE_INFO_START,
                          ErrEnum.DealTag.INTI,
                          dealId,
                          acctId,
                          deviceId);
  }

  /**
   * 初始化账户处理日志的时候使用
   */
  public static void insertAcctLog(final long dealId, final long acctId) {

    AcctLog acctLog = new AcctLog(dealId,acctId, BillEnum.DealStage.ACCT_DEAL_START, ErrEnum.DealTag.INTI);
    String connCode = BaseDefine.CONNCODE_MYSQL_PARAM;
    try {
      XmlBaseDao.execOne(connCode, "logMapper.insertAcctLog", acctLog);
    } catch (Exception e) {
      logger.error("insertDeviceLog err.{} ", e);
    }
  }

  public static void updateAcctLog(final long dealId,final long acctId, ErrEnum.DealResult dealResult) {

    int dealStage;
    String dealTag,sqlStr;
    StringBuilder dealInfo = new StringBuilder();

    if(ErrEnum.DEAL_START.equals(dealResult)) {
      dealStage = BillEnum.DealStage.ACCT_DEAL_START;
      dealTag = ErrEnum.DealTag.START;
      dealInfo.append(dealResult.getResultInfo());

      sqlStr =" update TL_B_ACCT " +
              "    set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?," +
              "        ACCT_START_TIME=sysdate(),UPDATE_TIME=sysdate()" +
              "  where ACCT_ID=? and DEAL_ID=?";

    }else{
      if(ErrEnum.DEAL_SUCESS.equals(dealResult)){
        dealTag   = ErrEnum.DealTag.SUCCESS;
        dealInfo.append(dealResult.getResultInfo());
      }else{
        dealTag = ErrEnum.DealTag.ERR;
        dealInfo.append("err_no: ");
        dealInfo.append(dealResult.getResultNo());
        dealInfo.append(" ,err_info: ");
        dealInfo.append(dealResult.getResultInfo());
      }
      dealStage = BillEnum.DealStage.ACCT_DEAL_END;

      sqlStr =" update TL_B_ACCT " +
              "    set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?," +
              "        ACCT_END_TIME=sysdate(),UPDATE_TIME=sysdate()" +
              "  where ACCT_ID=? and DEAL_ID=?";
    }
    execSqlBase(sqlStr,
            dealStage,
            dealTag,
            dealInfo.toString(),
            acctId,
            dealId);
  }


  public static void updateBillLog(final long dealId,final long acctId, ErrEnum.DealResult dealResult) {

    int dealStage;
    String dealTag,sqlStr;
    StringBuilder dealInfo = new StringBuilder();

    if(ErrEnum.DEAL_START.equals(dealResult)) {
      dealStage = BillEnum.DealStage.BILL_DEAL_START;
      dealTag = ErrEnum.DealTag.START;
      dealInfo.append(dealResult.getResultInfo());

      sqlStr =" update TL_B_ACCT " +
              "    set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?," +
              "        BILL_START_TIME=sysdate(),UPDATE_TIME=sysdate()" +
              "  where ACCT_ID=? and DEAL_ID=?";
    }else{
      if(ErrEnum.DEAL_SUCESS.equals(dealResult)){
        dealTag   = ErrEnum.DealTag.SUCCESS;
        dealInfo.append(dealResult.getResultInfo());
      }else{
        dealTag = ErrEnum.DealTag.ERR;
        dealInfo.append("err_no: ");
        dealInfo.append(dealResult.getResultNo());
        dealInfo.append(",err_info: ");
        dealInfo.append(dealResult.getResultInfo());
      }
      dealStage = BillEnum.DealStage.BILL_DEAL_END;

      sqlStr =" update TL_B_ACCT " +
              "   set DEAL_STAGE=?,DEAL_TAG=?,REMARK=?, " +
              "       BILL_END_TIME=sysdate(),UPDATE_TIME=sysdate() " +
              " where ACCT_ID=? and DEAL_ID=?";
    }
    execSqlBase(sqlStr,
            dealStage,
            dealTag,
            dealInfo.toString(),
            acctId,
            dealId);
  }

  public static void updateDeallog(final long dealId,int result) {

    StringBuilder sqlStr=new StringBuilder();
    switch (result){
      case ErrEnum.AcctDealResult.GET:
        sqlStr.append(" UPDATE TL_B_DEALLOG SET DEAL_NUM = DEAL_NUM + 1,UPDATE_TIME=sysdate()  WHERE DEAL_ID = ? ");
        break;
      case ErrEnum.AcctDealResult.IGNORE:
        sqlStr.append(" UPDATE TL_B_DEALLOG SET DEAL_NUM = DEAL_NUM + 1,IGNORE_NUM = IGNORE_NUM + 1, MQ_END_TIME = sysdate(), UPDATE_TIME = sysdate()  WHERE DEAL_ID = ? ");
        break;
      case ErrEnum.AcctDealResult.FAIL:
        sqlStr.append(" UPDATE TL_B_DEALLOG SET FAIL_NUM = FAIL_NUM + 1, MQ_END_TIME = sysdate(), UPDATE_TIME = sysdate()  WHERE DEAL_ID = ? ");
        break;
      case ErrEnum.AcctDealResult.SUCESS:
        sqlStr.append(" UPDATE TL_B_DEALLOG SET SUCESS_NUM = SUCESS_NUM + 1, MQ_END_TIME = sysdate(), UPDATE_TIME = sysdate()  WHERE DEAL_ID = ? ");
        break;
      default:
        break;
    }

    if(!"".equals(sqlStr.toString())){
        execSqlBase(sqlStr.toString(), dealId);
    }
  }

  private static void execSqlBase(String sqlStr,Object... params) {

    Connection conn = BaseDao.getConnection(BaseDefine.CONNCODE_MYSQL_PARAM);
    if (conn == null) {
      return;
    }
    QueryRunner qr = new QueryRunner();
    try {
      qr.update(conn, sqlStr, params);
      conn.commit();
    } catch (Exception e) {
      logger.error("execSql err.{} ", e);
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }

  }



}
