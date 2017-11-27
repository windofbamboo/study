package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.route.RouteMgr;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.dealproc.container.AcctBillContainer;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.entity.computebill.DeviceBillActivation;
import com.ai.iot.bill.entity.computebill.DeviceBillOrder;
import com.ai.iot.bill.entity.computebill.DeviceBillUsage;
import com.ai.iot.bill.entity.multibill.DeviceBill;
import com.ai.iot.bill.entity.multibill.DeviceBillActive;
import com.ai.iot.bill.entity.multibill.DeviceBillData;
import com.ai.iot.bill.entity.multibill.DeviceBillPrepay;
import com.ai.iot.bill.entity.multibill.DeviceBillSms;
import com.ai.iot.bill.entity.multibill.DeviceBillVoice;
import com.ai.iot.bill.entity.multibill.DeviceUsage;
import com.ai.iot.bill.entity.res.ResIncludeDevice;
import com.ai.iot.bill.entity.res.ResIncludePile;
import com.ai.iot.bill.entity.res.ResIncludeShareTurn;
import com.ai.iot.bill.entity.res.ResUsedDevice;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddShareDetail;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**账单账单操作
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctBillDao {

  private static final Logger logger = LoggerFactory.getLogger(AcctBillDao.class);

  private AcctBillDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 删除账户级账单
   * @param month 月份
   * @param acctId 账户
   */
  public static ErrEnum.DealResult deleteAcctPdb(final String month,final long acctId) {

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
      //设备级
//      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResDevice2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResAgileShareTurn2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResFixShareTurn2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResPile2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResUsedDevice2", acctId);
//
//      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteDeviceAdd2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteUsedAddShareDetail2", acctId);
//
//      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceUsage2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceOrder2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteDeviceActive2", acctId);
//
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceUsage2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillData2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillSms2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillVoice2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillActive2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBillPrepay2", acctId);
//      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteDeviceBill2", acctId);
      //账户级
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResShare", acctId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResPool", acctId);

      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteUsedAddShare", acctId);
      BaseDao.execsql(qr, conn, propValues, "addMapper.deleteUsedAddPoolTotal", acctId);

      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResUsedPool", acctId);
      BaseDao.execsql(qr, conn, propValues, "resMapper.deleteResUsedShare", acctId);

      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteAcctOrder", acctId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteAcctUsage", acctId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteAcctTrack", acctId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteAcctBill", acctId);
      BaseDao.execsql(qr, conn, propValues, "computeBillMapper.deleteBssBill", acctId);

      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctPlanZoneBill", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctPlanBill", acctId);

      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctBillAdd", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctBillDiscount", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctRateGroup", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctBillOther", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAdjustBill", acctId);
      BaseDao.execsql(qr, conn, propValues, "multiBillMapper.deleteAcctBillSum", acctId);

      conn.commit();
    } catch (Exception e) {
      logger.error("mapper Sql execute err! ", e);
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
   * 获取设备级的信息
   * @param month 月份
   * @param acctId 账户
   * @param acctBillContainer 账户账单容器
   */
  public static ErrEnum.DealResult getDevicePdb(String month, long acctId, AcctBillContainer acctBillContainer) {

    List<ResIncludeShareTurn> resIncludeAgileShareTrunList;
    List<ResIncludeShareTurn> resIncludeFixShareTurnList;
    List<UsedAddDevice> usedAddDeviceList;
    List<UsedAddShareDetail> usedAddShareDetailList;
    List<DeviceBillOrder> deviceBillOrderList;
    List<DeviceBillUsage> deviceBillUsageList;
    List<DeviceBillActivation> deviceBillActivationList;
    List<DeviceUsage> deviceUsageList;
    List<DeviceBillActive> deviceBillActiveList;
    List<DeviceBillData> deviceBillDataList;
    List<DeviceBillSms> deviceBillSmsList;
    List<DeviceBillVoice> deviceBillVoiceList;
    List<DeviceBillPrepay> deviceBillPrepayList;
    List<DeviceBill> deviceBillList;

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);
    Map<String, String> propValues = new HashMap<>();
    propValues.put("month", month);

    Connection conn = BaseDao.getConnection(connCode);
    if (conn == null) {
      return ErrEnum.dbErr.GET_CONNECT_ERR;
    }
    QueryRunner qr = new QueryRunner();

    try {
//      resIncludeDeviceList =BaseDao.selectList(qr, conn, propValues, "resMapper.getResIncludeDevice", acctId);
//      resIncludePileList =BaseDao.selectList(qr, conn, propValues, "resMapper.getResIncludePile", acctId);
      resIncludeAgileShareTrunList =BaseDao.selectList(qr, conn, propValues, "resMapper.getResIncludeAgileShareTrun", acctId);
      resIncludeFixShareTurnList =BaseDao.selectList(qr, conn, propValues, "resMapper.getResIncludeFixShareTurn", acctId);

//      resUsedDeviceList = BaseDao.selectList(qr, conn, propValues, "resMapper.getResUsedDevice", acctId);
      usedAddDeviceList = BaseDao.selectList(qr, conn, propValues, "addMapper.getUsedAddDevice", acctId);
      usedAddShareDetailList = BaseDao.selectList(qr, conn, propValues, "addMapper.getUsedAddShareDetail", acctId);

      deviceBillOrderList = BaseDao.selectList(qr, conn, propValues, "computeBillMapper.getDeviceBillOrder", acctId);
      deviceBillUsageList = BaseDao.selectList(qr, conn, propValues, "computeBillMapper.getDeviceBillUsage", acctId);
      deviceBillActivationList = BaseDao.selectList(qr, conn, propValues, "computeBillMapper.getDeviceBillActive", acctId);

      deviceUsageList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceUsage", acctId);
      deviceBillActiveList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBillActive", acctId);
      deviceBillDataList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBillData", acctId);
      deviceBillSmsList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBillSms", acctId);
      deviceBillVoiceList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBillVoice", acctId);
      deviceBillPrepayList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBillPrepay", acctId);
      deviceBillList = BaseDao.selectList(qr, conn, propValues, "multiBillMapper.getDeviceBill", acctId);

    } catch (Exception e) {
      logger.error("getDevicePdb err! ",e);
      return ErrEnum.dbErr.SELECT_ERR;
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }

    acctBillContainer.setResIncludeAgileShareTrunList(resIncludeAgileShareTrunList);
    acctBillContainer.setResIncludeFixShareTurnList(resIncludeFixShareTurnList);

    acctBillContainer.setUsedAddDeviceList(usedAddDeviceList);
    acctBillContainer.setUsedAddShareDetailList(usedAddShareDetailList);

    acctBillContainer.setDeviceBillOrderList(deviceBillOrderList);
    acctBillContainer.setDeviceBillActivationList(deviceBillActivationList);
    acctBillContainer.setDeviceBillUsageList(deviceBillUsageList);

    acctBillContainer.setDeviceUsageList(deviceUsageList);
//    acctBillContainer.setDeviceBillActiveList(deviceBillActiveList);
    acctBillContainer.setDeviceBillDataList(deviceBillDataList);
//    acctBillContainer.setDeviceBillSmsList(deviceBillSmsList);
//    acctBillContainer.setDeviceBillVoiceList(deviceBillVoiceList);
//    acctBillContainer.setDeviceBillPrepayList(deviceBillPrepayList);
    acctBillContainer.setDeviceBillList(deviceBillList);

    return ErrEnum.DEAL_SUCESS;
  }

  /**
   * 提交账单
   * @param month 月份
   * @param acctId 账户
   * @param acctBillContainer 账户账单容器
   */
  public static ErrEnum.DealResult insertDevicePdb(final String month, final long acctId, final AcctBillContainer acctBillContainer) {

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
      //账户级的结果
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResShare", acctBillContainer.getResIncludeFixShareList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResShare", acctBillContainer.getResIncludeAgileShareList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResPool", acctBillContainer.getResIncludePoolList());

      BaseDao.execBatch(qr, conn, propValues, "addMapper.insertUsedAddShare", acctBillContainer.getUsedAddShareList());
      BaseDao.execBatch(qr, conn, propValues, "addMapper.insertUsedAddPoolTotal", acctBillContainer.getUsedAddPoolTotalList());

      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResUsedPool", acctBillContainer.getResUsedPoolTotalList());
      BaseDao.execBatch(qr, conn, propValues, "resMapper.insertResUsedShare", acctBillContainer.getResUsedShareTotalList());

      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertAcctOrder", acctBillContainer.getAcctBillOrderList());
      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertAcctUsage", acctBillContainer.getAcctBillUsageList());
      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertAcctTrack", acctBillContainer.getBillTrackAcctList());

      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertAcctBill", acctBillContainer.getBillAcctList());
      BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.insertBssBill", acctBillContainer.getAcctBill2BssList());

      if(!acctBillContainer.getFixShareDeviceBillList().isEmpty()){
        BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.updateDeviceBill",acctBillContainer.getFixShareDeviceBillList());
      }
      if(!acctBillContainer.getFixShareDeviceBillOrderList().isEmpty()){
        BaseDao.execBatch(qr, conn, propValues, "computeBillMapper.updateDeviceOrder", acctBillContainer.getFixShareDeviceBillOrderList());
      }

      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctBillAdd", acctBillContainer.getAcctBillAddList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctBillDiscount", acctBillContainer.getAcctBillDiscountList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctBillOther", acctBillContainer.getAcctBillOtherList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAdjustBill", acctBillContainer.getAdjustBillList());

      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctRateGroup", acctBillContainer.getRateGroupDiscountList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctPlanZoneBill", acctBillContainer.getPlanZoneBillList());
      BaseDao.execBatch(qr, conn, propValues, "multiBillMapper.insertAcctPlanBill", acctBillContainer.getPlanBillList());

      BaseDao.execOne(qr, conn, propValues, "multiBillMapper.insertAcctBillSum", acctBillContainer.getAcctBillSum());

      conn.commit();
    } catch (Exception e) {
      logger.error("insertDevicePdb err ", e);
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
