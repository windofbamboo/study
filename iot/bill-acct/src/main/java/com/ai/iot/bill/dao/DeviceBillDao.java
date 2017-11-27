package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.route.RouteMgr;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.dealproc.container.DeviceBillContainer;
import com.ai.iot.bill.define.ErrEnum;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备级账单操作
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceBillDao {

  private static final Logger logger = LoggerFactory.getLogger(DeviceBillDao.class);

  private DeviceBillDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 设备级的账单删除
   */
  public static ErrEnum.DealResult deleteDevicePdb(final String month, final long acctId, final long deviceId) {

    ErrEnum.DealResult dealResult = ErrEnum.DEAL_SUCESS;

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);

    Map<String, String> propValues = new HashMap<>();
    propValues.put("month", month);

    Connection conn = BaseDao.getConnection(connCode);
    if (conn == null) {
      return ErrEnum.dbErr.GET_CONNECT_ERR;
    }
    QueryRunner qr = new QueryRunner();

    try {
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResDevice", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResAgileShareTurn", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResFixShareTurn", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResPile", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResUsedDevice", acctId, deviceId);

      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteDeviceAdd", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteUsedAddShareDetail", acctId, deviceId);

      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceUsage", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceOrder", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceActive", acctId, deviceId);

      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceUsage", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillData", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillSms", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillVoice", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillActive", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillPrepay", acctId, deviceId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBill", acctId, deviceId);

      conn.commit();
    } catch (Exception e) {
      logger.error("exec delete sql err.{} ", e);
      dealResult = ErrEnum.dbErr.DELETE_ERR;
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }
    return dealResult;
  }

  /**
   * crm物理库的数据提交
   * @param month 月份
   * @param deviceBillContainer 设备级账单容器
   */
  public static ErrEnum.DealResult insertDevicePdb(final String month, DeviceBillContainer deviceBillContainer) {

    ErrEnum.DealResult dealResult = ErrEnum.DEAL_SUCESS;

    Map<String, String> propValues = new HashMap<>();
    propValues.put("month", month);

    long acctId = deviceBillContainer.getAcctId();
    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);

    Connection conn = BaseDao.getConnection(connCode);
    if (conn == null) {
      return ErrEnum.dbErr.GET_CONNECT_ERR;
    }
    QueryRunner qr = new QueryRunner();

    try {
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResDevice", deviceBillContainer.getResIncludeDeviceList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResAgileShareTurn", deviceBillContainer.getResIncludeAgileShareTrunList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResFixShareTurn", deviceBillContainer.getResIncludeFixShareTurnList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResPile", deviceBillContainer.getResIncludePileList());

      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResUsedDevice", deviceBillContainer.getResUsedDeviceList());

      BaseDao.execBatch(qr, conn, propValues, "addMapper.insertDeviceAdd", deviceBillContainer.getUsedAddDeviceList());
      BaseDao.execBatch(qr, conn, propValues, "addMapper.insertUsedAddShareDetail", deviceBillContainer.getUsedAddShareDetailList());

      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertDeviceUsage", deviceBillContainer.getDeviceBillUsageList());
      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertDeviceOrder", deviceBillContainer.getDeviceBillOrderList());
      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertDeviceActive", deviceBillContainer.getDeviceBillActivationList());

      BaseDao.execOne(qr, conn, propValues, "multiBillMapper.insertDeviceUsage", deviceBillContainer.getDeviceUsage());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertDeviceBillData", deviceBillContainer.getDeviceBillDataList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertDeviceBillSms", deviceBillContainer.getDeviceBillSmsList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertDeviceBillVoice", deviceBillContainer.getDeviceBillVoiceList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertDeviceBillActive", deviceBillContainer.getDeviceBillActiveList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertDeviceBillPrepay", deviceBillContainer.getDeviceBillPrepayList());
      BaseDao.execOne(qr, conn, propValues, "multiBillMapper.insertDeviceBill", deviceBillContainer.getDeviceBill());

      conn.commit();
    } catch (Exception e) {
      logger.error("insertDevicePdb err.{} ", e);
      dealResult = ErrEnum.dbErr.UPDATE_ERR;
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }
    return dealResult;
  }


}
