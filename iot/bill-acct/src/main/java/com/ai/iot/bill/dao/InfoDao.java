package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.route.RouteMgr;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.dealproc.container.AcctInfoContainer;
import com.ai.iot.bill.dealproc.util.InfoUtil;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.entity.info.AcctAdjustBeforeBean;
import com.ai.iot.bill.entity.info.AcctBillingGeneralBean;
import com.ai.iot.bill.entity.info.AcctCommitmentsBean;
import com.ai.iot.bill.entity.info.AcctDiscountBean;
import com.ai.iot.bill.entity.info.AcctDiscountGrade;
import com.ai.iot.bill.entity.info.AcctInfoBean;
import com.ai.iot.bill.entity.info.AcctMonthFeeBean;
import com.ai.iot.bill.entity.info.AcctOrderBean;
import com.ai.iot.bill.entity.info.AcctPromiseBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountBean;
import com.ai.iot.bill.entity.info.AcctRateDiscountMemberBean;
import com.ai.iot.bill.entity.info.AcctSmsDiscount;
import com.ai.iot.bill.entity.info.AcctValumeDiscountBean;
import com.ai.iot.bill.entity.info.AppendFeepolicyBean;
import com.ai.iot.bill.entity.info.DeviceBean;
import com.ai.iot.bill.entity.info.DeviceRatePlanBean;
import com.ai.iot.bill.entity.info.DeviceStateBean;
import com.ai.iot.bill.entity.info.SharePoolBean;
import com.ai.iot.bill.entity.param.CycleBean;
import com.ai.iot.bill.entity.usage.UsedAddThirdParty;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**资料获取类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class InfoDao {

  private static final Logger logger = LoggerFactory.getLogger(InfoDao.class);

  private InfoDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 账期内有效的账户资料获取
   */
  public static AcctInfoContainer getAcctInfo(final CycleBean cycleBean,final long acctId) {

    int partitionId = (int)(acctId % 10000);
    String month = String.valueOf(cycleBean.getCycleId()).substring(4, 6);
    Map<String, String> propValues = new HashMap<>();
    propValues.put("month", month);

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);
    Connection conn = BaseDao.getConnection(connCode);
    if (conn == null) {
      return null;
    }

    QueryRunner qr = new QueryRunner();

    AcctInfoBean acctInfoBean;
    AcctCommitmentsBean acctCommitmentsBean;
    AcctBillingGeneralBean acctBillingGeneralBean;
    List<AcctPromiseBean> acctPromiseBeanList;
    List<AcctMonthFeeBean> acctMonthFeeBeanList;
    List<AcctDiscountBean> acctDiscountBeanList;
    List<AcctAdjustBeforeBean> acctAdjustBeforeBeanList;
    AcctValumeDiscountBean acctValumeDiscountBean;
    List<SharePoolBean> sharePoolBeanList;
    List<AcctRateDiscountBean> acctRateDiscountBeanList;
    List<AcctRateDiscountMemberBean> acctRateDiscountMemberBeanList;
    List<AcctOrderBean> acctOrderBeanList;
    List<AcctSmsDiscount> acctSmsDiscountList;
    List<AcctDiscountGrade> acctDiscountGradeList;
    List<DeviceBean> deviceBeanList;
    List<DeviceStateBean> deviceStateBeanList;
    List<DeviceRatePlanBean> deviceRatePlanBeanList;
    List<UsedAddThirdParty> usedAddThirdPartyList;
    List<AppendFeepolicyBean> appendFeepolicyBeanList;
    try {
      acctInfoBean = BaseDao.selectOne(qr, conn, "infoMapper.getAcctInfo", partitionId,acctId);
      appendFeepolicyBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAppendFeepolicyBean", acctId);
      acctCommitmentsBean = BaseDao.selectOne(qr, conn, "infoMapper.getAcctCommitments",partitionId,acctId);
      acctBillingGeneralBean = BaseDao.selectOne(qr, conn, "infoMapper.getAcctBillingGeneral",partitionId,acctId);
      acctPromiseBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctPromise", partitionId,acctId);
      acctMonthFeeBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctMonthFee", partitionId,acctId);
      acctDiscountBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctDiscount", partitionId,acctId);
      acctAdjustBeforeBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctAdjustBefore", partitionId,acctId,cycleBean.getCycleId(),cycleBean.getCycleId());
      acctValumeDiscountBean = BaseDao.selectOne(qr, conn, "infoMapper.getAcctValumeDiscount", partitionId,acctId);
      sharePoolBeanList = BaseDao.selectList(qr, conn, "infoMapper.getSharePool", acctId);
      acctRateDiscountBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctRateDiscount", partitionId,acctId);
      acctRateDiscountMemberBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctRateDiscountMember",partitionId,acctId);
      acctOrderBeanList = BaseDao.selectList(qr, conn, "infoMapper.getAcctOrder", acctId);
      acctSmsDiscountList = BaseDao.selectList(qr, conn, "infoMapper.getAcctSmsDiscount", partitionId,acctId);
      acctDiscountGradeList = BaseDao.selectList(qr, conn, "infoMapper.getAcctDiscountGrade", partitionId,acctId);
      usedAddThirdPartyList = BaseDao.selectList(qr, conn, propValues, "infoMapper.getUsedAddThirdParty", acctId);

      //设备级的信息
      deviceBeanList = BaseDao.selectList(qr, conn, "infoMapper.getDeviceByAcctId", acctId);
      deviceStateBeanList = BaseDao.selectList(qr, conn, "infoMapper.getDeviceStateByAcctId", acctId);
      deviceRatePlanBeanList = BaseDao.selectList(qr, conn, "infoMapper.getDeviceRatePlanByAcctId", acctId);
    } catch (Exception e) {
      logger.error("getAcctInfo sql err.{} ", e);
      return null;
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }

    AcctInfoContainer acctInfoContainer = new AcctInfoContainer();

    if(acctInfoBean != null){
      if(InfoEnum.AcctStatus.STOP.equals(acctInfoBean.getRemoveTag()) &&
         acctInfoBean.getRemoveTime().getTime()<cycleBean.getCycStartTime().getTime()){
        acctInfoBean = null;
      }
    }
    if(acctInfoBean==null){
      return null;
    }
    Date tEndTIme = InfoEnum.AcctStatus.STOP.equals(acctInfoBean.getRemoveTag())?acctInfoBean.getRemoveTime():cycleBean.getCycEndTime();

    InfoUtil.judgeAvtication(cycleBean,appendFeepolicyBeanList);
    InfoUtil.judgeAvtication(cycleBean,acctDiscountBeanList);
    InfoUtil.judgeAvtication(cycleBean,acctMonthFeeBeanList);

    InfoUtil.judgeAvtication(cycleBean,sharePoolBeanList);
    InfoUtil.judgeAvtication(cycleBean,acctRateDiscountBeanList);
    InfoUtil.judgeAvtication(cycleBean,acctRateDiscountMemberBeanList);

    InfoUtil.judgeAvtication(cycleBean,acctSmsDiscountList);
    InfoUtil.judgeAvtication(cycleBean,acctDiscountGradeList);
    InfoUtil.judgeAvtication(cycleBean,deviceStateBeanList);
    InfoUtil.judgeAvtication(cycleBean,deviceRatePlanBeanList);

    acctInfoContainer.setAcctInfoBean(acctInfoBean);
    acctInfoContainer.setAppendFeepolicyBeanList(appendFeepolicyBeanList);
    acctInfoContainer.setAcctCommitmentsBean(acctCommitmentsBean);
    if(acctBillingGeneralBean!=null){
      acctInfoContainer.setAcctBillingGeneralBean(acctBillingGeneralBean);
      acctInfoContainer.setRate(acctBillingGeneralBean.isBillAble());
    }

    for(AcctPromiseBean acctPromiseBean:acctPromiseBeanList){
      if(acctPromiseBean.getStartTime().getTime()<tEndTIme.getTime() &&
         acctPromiseBean.getEndTime().getTime()>= tEndTIme.getTime() ){
        acctInfoContainer.setAcctPromiseBean(acctPromiseBean);
        break;
      }
    }
    acctInfoContainer.setAcctValumeDiscountBean(acctValumeDiscountBean);
    acctInfoContainer.setAcctDiscountBeanList(acctDiscountBeanList);

    acctInfoContainer.setAcctMonthFeeBeanList(acctMonthFeeBeanList);
    acctInfoContainer.setAcctAdjustBeforeBeanList(acctAdjustBeforeBeanList);
    acctInfoContainer.setSharePoolBeanList(sharePoolBeanList);
    acctInfoContainer.setAcctRateDiscountBeanList(acctRateDiscountBeanList);
    acctInfoContainer.setAcctRateDiscountMemberBeanList(acctRateDiscountMemberBeanList);
    acctInfoContainer.setAcctOrderBeanList(acctOrderBeanList);
    acctInfoContainer.setAcctSmsDiscountList(acctSmsDiscountList);
    acctInfoContainer.setAcctDiscountGradeList(acctDiscountGradeList);
    acctInfoContainer.setUsedAddThirdPartyList(usedAddThirdPartyList);

    acctInfoContainer.setDeviceBeanList(deviceBeanList);
    acctInfoContainer.setDeviceStateBeanList(deviceStateBeanList);

    if(deviceRatePlanBeanList!=null && !deviceRatePlanBeanList.isEmpty()){
      deviceRatePlanBeanList.removeIf(t -> InfoEnum.PlanState.INACTIVE.equals(t.getState()));
    }
    acctInfoContainer.setDeviceRatePlanBeanList(deviceRatePlanBeanList);

    return acctInfoContainer;
  }

  /**
   * 获取订单下当月的sim卡数量
   * @param cycleBean 账期
   * @param acctId 账户
   * @param orderId 订购实例
   * @return 订购数量
   */
  public static int getOrderNum(final CycleBean cycleBean,final long acctId,final long orderId) {

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);
    try {
      String sqlStr = " select ifnull(sum(b.EXECUTED_CHANGES),0) " +
                      " from TF_F_TRANSFER_RECORD a," +
                      "      TF_B_BATCH_UPDATE_DETAIL b" +
                      " where a.ACCT_ID =? and a.ORDER_ID =? and " +
                      " STR_TO_DATE(?,'%Y%m%d%H%i%s') <= a.ACCEPT_DATE and " +
                      " STR_TO_DATE(?,'%Y%m%d%H%i%s') >= a.ACCEPT_DATE and " +
                      " a.SUBSCRIBE_ID=b.SUBSCRIBE_ID ";
      return JdbcBaseDao.getCount(connCode, sqlStr,acctId, orderId, cycleBean.getsTime(), cycleBean.geteTime());
    } catch (Exception e) {
      logger.error("getOrderNum err.{} ", e);
      return 0;
    }
  }

  /**
   * 获取账户下的第三方短信数量
   * @param cycleBean 账期
   * @param acctId 账户
   * @return 短信数量
   */
  public static int getAcctPlatSmsValue(final CycleBean cycleBean,final long acctId) {

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);
    try {
      String sqlStr = " select count(*) from TF_F_SMS " +
              " where ACCT_ID=? and type='MO' and " +
              " SEND_DATE<=STR_TO_DATE(?,'%Y%m%d%H%i%s') and " +
              " SEND_DATE>=STR_TO_DATE(?,'%Y%m%d%H%i%s')";
      return JdbcBaseDao.getCount(connCode, sqlStr, acctId, cycleBean.geteTime(), cycleBean.getsTime());
    } catch (Exception e) {
      logger.error("getPlatSmsValue err.{} ", e);
      return 0;
    }
  }

  /**
   * 获取设备的第三方短信数量
   */
  public static int getDevicePlatSmsValue(final CycleBean cycleBean,final long acctId,final long deviceId) {

    String connCode = RouteMgr.getDbConnect(BaseDefine.CONNTYPE_MYSQL_CRM, acctId);
    try {
      String sqlStr = " select count(*) from TF_F_SMS " +
                      " where DEVICE_ID=? and " +
                      " SEND_DATE<=STR_TO_DATE(?,'%Y%m%d%H%i%s') and " +
                      " SEND_DATE>=STR_TO_DATE(?,'%Y%m%d%H%i%s')";
      return JdbcBaseDao.getCount(connCode, sqlStr, deviceId, cycleBean.geteTime(), cycleBean.getsTime());
    } catch (Exception e) {
      logger.error("getPlatSmsValue err.{} ", e);
      return 0;
    }
  }



}
