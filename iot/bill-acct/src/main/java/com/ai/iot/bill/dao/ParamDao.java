package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.dao.util.BaseDao;
import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.dealproc.container.RateBill;
import com.ai.iot.bill.dealproc.container.RateOrderFee;
import com.ai.iot.bill.dealproc.util.InfoUtil;
import com.ai.iot.bill.dealproc.util.ParamUtil;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.param.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**参数的数据库访问类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ParamDao {

  private static final Logger logger = LoggerFactory.getLogger(ParamDao.class);

  private ParamDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 参数容器初始化
   */
  public static ParamContainer initialize() {

    Connection conn = BaseDao.getConnection(BaseDefine.CONNCODE_MYSQL_PARAM);
    if (conn == null) {
      return null;
    }
    QueryRunner qr = new QueryRunner();

    List<RatePlanBean> ratePlanBeanList ;
    List<RatePlanVersionBean> ratePlanVersionBeanList ;
    List<RatePlanLevelBean> ratePlanLevelBeanList;
    List<RatePlanDataBean> ratePlanDataBeanList;
    List<RatePlanSmsBean> ratePlanSmsBeanList;
    List<RatePlanVoiceBean> ratePlanVoiceBeanList;
    List<ZoneBillingGroupBean> zoneBillingGroupBeanList;
    List<FeeBaseBean> feeBaseBeanList;
    List<SumbillUseBean> sumbillUseBeanList;
    List<SumbillFixBean> sumbillFixBeanList;
    List<AddupIdBean> addupIdBeanList;
    List<ItemBean> itemBeanList;
    List<CycleBean> cycleBeanList;
    List<DiscontGradeDetailBean> discontGradeDetailBeanList;
    List<Opn> opnList;
    List<ThirdPartyPlan> thirdPartyPlanList;
    List<ThirdPartyVersion> thirdPartyVersionList;
    List<ThirdPartyZone> thirdPartyZoneList;
    List<ThirdPartyLevel> thirdPartyLevelList;
    List<ItemMonthFee> itemMonthFeeList;

    try {
      ratePlanBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlan");
      ratePlanVersionBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlanVersion");
      ratePlanLevelBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlanLevel");
      ratePlanDataBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlanData");
      ratePlanSmsBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlanSms");
      ratePlanVoiceBeanList = BaseDao.selectList(qr, conn, "paramMapper.getRatePlanVoice");
      zoneBillingGroupBeanList = BaseDao.selectList(qr, conn, "paramMapper.getZoneBillingGroup");
      feeBaseBeanList = BaseDao.selectList(qr, conn, "paramMapper.getFeeBase");
      sumbillUseBeanList = BaseDao.selectList(qr, conn, "paramMapper.getSumbillUse");
      sumbillFixBeanList = BaseDao.selectList(qr, conn, "paramMapper.getSumbillFix");
      addupIdBeanList = BaseDao.selectList(qr, conn, "paramMapper.getAddupId");
      itemBeanList = BaseDao.selectList(qr, conn, "paramMapper.getItem");
      itemMonthFeeList = BaseDao.selectList(qr, conn, "paramMapper.getItemMonthFee");
      cycleBeanList = BaseDao.selectList(qr, conn, "paramMapper.getCycle");
      discontGradeDetailBeanList = BaseDao.selectList(qr, conn, "paramMapper.getDiscontGradeDetailBean");
      opnList = BaseDao.selectList(qr, conn, "paramMapper.getOpn");
      thirdPartyPlanList = BaseDao.selectList(qr, conn, "paramMapper.getThirdPartyPlan");
      thirdPartyVersionList = BaseDao.selectList(qr, conn, "paramMapper.getThirdPartyVersion");
      thirdPartyZoneList = BaseDao.selectList(qr, conn, "paramMapper.getThirdPartyZone");
      thirdPartyLevelList = BaseDao.selectList(qr, conn, "paramMapper.getThirdPartyLevel");
    } catch (Exception e) {
      logger.error("param sql err.{}", e);
      return null;
    }finally {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        logger.error("reback Connection to pool err.{} ", e);
      }
    }

    //准备阶段
    Collections.sort(cycleBeanList);
    Collections.sort(ratePlanBeanList);
    Collections.sort(ratePlanVersionBeanList);
    Collections.sort(ratePlanLevelBeanList);
    Collections.sort(ratePlanDataBeanList);
    Collections.sort(ratePlanVoiceBeanList);
    Collections.sort(ratePlanSmsBeanList);
    Collections.sort(zoneBillingGroupBeanList);
    Collections.sort(addupIdBeanList);
    Collections.sort(sumbillFixBeanList);
    Collections.sort(sumbillUseBeanList);
    Collections.sort(discontGradeDetailBeanList);
    Collections.sort(thirdPartyPlanList);
    Collections.sort(thirdPartyVersionList);
    Collections.sort(thirdPartyZoneList);
    Collections.sort(thirdPartyLevelList);

    CycleBean currCycleBean = null;
    for (CycleBean cycleBean : cycleBeanList) {
      if (cycleBean.getMonthAcctStatus() == ParamEnum.CycleStatus.INIT||
          cycleBean.getMonthAcctStatus() == ParamEnum.CycleStatus.ADD_SOLIDIFY) {
        currCycleBean = cycleBean;
        long sTime=Long.valueOf(DateUtil.getCurrentDateTime(cycleBean.getCycStartTime(), DateUtil.YYYYMMDD_HHMMSS));
        long hTime  =Long.valueOf(DateUtil.getCurrentDateTime(cycleBean.getCycHalfTime(), DateUtil.YYYYMMDD_HHMMSS));
        long eTime  =Long.valueOf(DateUtil.getCurrentDateTime(cycleBean.getCycEndTime(), DateUtil.YYYYMMDD_HHMMSS));
        currCycleBean.setsTime(sTime);
        currCycleBean.sethTime(hTime);
        currCycleBean.seteTime(eTime);
        break;
      }
    }
    if (currCycleBean == null) {
      logger.error("not found current cycleId !");
      return null;
    }
    InfoUtil.judgeAvtication(currCycleBean, discontGradeDetailBeanList);
    ParamUtil.judgeCycle(currCycleBean, thirdPartyPlanList);
    ParamUtil.judgeCycle(currCycleBean, thirdPartyVersionList);

    Map<Integer, List<DiscontGradeDetailBean>> discontGradeDetailBeanMap = discontGradeDetailBeanList.stream().collect(groupingBy(DiscontGradeDetailBean::getGradeId));
    Map<Integer, List<ThirdPartyZone>> thirdPartyZoneMap = thirdPartyZoneList.stream().collect(groupingBy(ThirdPartyZone::getPlanVersionId));
    Map<Integer, List<ThirdPartyLevel>> thirdPartyLevelMap = thirdPartyLevelList.stream().collect(groupingBy(ThirdPartyLevel::getPlanVersionId));
    //设置
    ParamContainer paramContainer = new ParamContainer();

    paramContainer.setCycleBean(currCycleBean);
    paramContainer.setSumbillFixBeanList(sumbillFixBeanList);
    paramContainer.setSumbillUseBeanList(sumbillUseBeanList);
    paramContainer.setRatePlanVersionBeanList(ratePlanVersionBeanList);
    paramContainer.setAddupIdBeanList(addupIdBeanList);
    paramContainer.setDiscontGradeDetailBeanMap(discontGradeDetailBeanMap);
    paramContainer.setThirdPartyLevelMap(thirdPartyLevelMap);
    paramContainer.setThirdPartyZoneMap(thirdPartyZoneMap);
    //订户费账目
    if(!sumbillFixBeanList.isEmpty()){
      Set<Integer> orderItemSet = sumbillFixBeanList.parallelStream().filter(sumbillFixBean -> sumbillFixBean.getChargeType() == ParamEnum.ChargeType.CHARGE_TYPE_DEVICE_PLAN ||
          sumbillFixBean.getChargeType() == ParamEnum.ChargeType.CHARGE_TYPE_ACCT_PLAN).map(SumbillFixBean::getItemId).collect(Collectors.toSet());
      paramContainer.setOrderItemSet(orderItemSet);
    }
    itemMonthFeeList.forEach(paramContainer::addMonthfeeItemMap);
    itemBeanList.forEach(paramContainer::addItemBeanMap);
    feeBaseBeanList.forEach(paramContainer::addFeeBaseMap);
    ratePlanBeanList.forEach(paramContainer::addratePlanOMap);
    opnList.forEach(paramContainer::addOpnMap);
    thirdPartyVersionList.forEach(paramContainer::addThirdPartyVersionMap);
    thirdPartyPlanList.forEach(paramContainer::addThirdPartyPlanMap);

    setRateParam(ratePlanVersionBeanList,ratePlanBeanList,ratePlanDataBeanList,ratePlanSmsBeanList,ratePlanVoiceBeanList,
            ratePlanLevelBeanList,zoneBillingGroupBeanList,paramContainer);

    return paramContainer;
  }

  /**
   * 主要完成 RateBill 和 RateOrderFee 的初始化
   */
  private static void setRateParam(final List<RatePlanVersionBean> ratePlanVersionBeanList,
                                   final List<RatePlanBean> ratePlanBeanList,
                                   final List<RatePlanDataBean> ratePlanDataBeanList,
                                   final List<RatePlanSmsBean> ratePlanSmsBeanList,
                                   final List<RatePlanVoiceBean> ratePlanVoiceBeanList,
                                   final List<RatePlanLevelBean> ratePlanLevelBeanList,
                                   final List<ZoneBillingGroupBean> zoneBillingGroupBeanList,
                                   ParamContainer paramContainer){

    if(ratePlanVersionBeanList==null||ratePlanVersionBeanList.isEmpty()){
      return;
    }
    Map<Integer, List<RatePlanDataBean>> ratePlanDataMap = ratePlanDataBeanList.stream().collect(groupingBy(RatePlanDataBean::getPlanVersionId));
    Map<Integer, List<RatePlanSmsBean>> ratePlanSmsMap = ratePlanSmsBeanList.stream().collect(groupingBy(RatePlanSmsBean::getPlanVersionId)) ;
    Map<Integer, List<RatePlanVoiceBean>> ratePlanVoiceMap = ratePlanVoiceBeanList.stream().collect(groupingBy(RatePlanVoiceBean::getPlanVersionId)) ;
    Map<Integer, List<RatePlanLevelBean>> ratePlanLevelMap = ratePlanLevelBeanList.stream().collect(groupingBy(RatePlanLevelBean::getLevelId)) ;
    Map<Integer, List<ZoneBillingGroupBean>> zoneBillingGroupMap =zoneBillingGroupBeanList.stream().collect(groupingBy(ZoneBillingGroupBean::getZoneBillingGroupId)) ;

    for (RatePlanVersionBean ratePlanVersionBean : ratePlanVersionBeanList) {
      if (ratePlanVersionBean.getState() == ParamEnum.PlanVersionState.NOT_ISSUE){
        continue;
      }
      if (ratePlanVersionBean.getStartTime().getTime() > paramContainer.getCycleBean().getCycEndTime().getTime() ||
          ratePlanVersionBean.getRateExpireTime().getTime() < paramContainer.getCycleBean().getCycStartTime().getTime()) {
        continue;
      }
      RatePlanBean ratePlanBean = new RatePlanBean(ratePlanVersionBean.getPlanId());
      int a = ratePlanBeanList.indexOf(ratePlanBean);
      if (a == -1)
        continue;
      ratePlanBean = ratePlanBeanList.get(a);

      int payment = ParamUtil.getPaymentType(ratePlanBean.getPlanType());
      if(payment == ParamEnum.Payment.PAYMENT_TYPE_MONTH){
        paramContainer.addRatePlanVersionMap(ratePlanVersionBean.getPlanId(),
                                             ratePlanVersionBean.getPlanVersionId());
      }
      paramContainer.addRatePlanMap(ratePlanVersionBean.getPlanVersionId(),
                                    ratePlanVersionBean.getPlanId());
      //资费计划对应的费用
      RateOrderFee rateOrderFee = new RateOrderFee( ratePlanVersionBean.getPlanVersionId(),
                                                    ratePlanBean.getPlanType(),
                                                    payment,
                                                    ratePlanVersionBean.getAccountCharge(),
                                                    ratePlanVersionBean.getSubscriberChargeFerquency(),
                                                    ratePlanVersionBean.getSubscriberCharge(),
                                                    ratePlanVersionBean.getLevelId());
      if (ratePlanLevelMap.containsKey(rateOrderFee.getLevelId())) {
        rateOrderFee.setRatePlanLevelBeanList(ratePlanLevelMap.get(rateOrderFee.getLevelId()));
      }
      paramContainer.addRateOrderFeeMap(rateOrderFee);

      try {
        List<RateBill> rateBillList = getRateBill(ratePlanDataMap.get(ratePlanVersionBean.getPlanVersionId()),
                                                  ratePlanSmsMap.get(ratePlanVersionBean.getPlanVersionId()),
                                                  ratePlanVoiceMap.get(ratePlanVersionBean.getPlanVersionId()),
                                                  zoneBillingGroupMap,
                                                  ratePlanVersionBean,
                                                  ratePlanBean,
                                                  payment);
        if(rateBillList != null){
          paramContainer.addRateBillMap(ratePlanVersionBean.getPlanVersionId(), rateBillList);
        }
      } catch (Exception e) {
        logger.error("planVersionId:"+ratePlanVersionBean.getPlanVersionId()+" deal err..{}",e);
      }
    }
  }

  /**
   * 生成用量相关的参数 RateBill
   */
  private static List<RateBill> getRateBill(final List<RatePlanDataBean> ratePlanDataBeanList,
                                            final List<RatePlanSmsBean> ratePlanSmsBeanList,
                                            final List<RatePlanVoiceBean> ratePlanVoiceBeanList,
                                            final Map<Integer, List<ZoneBillingGroupBean>> zoneBillingGroupMap,
                                            final RatePlanVersionBean ratePlanVersionBean,
                                            final RatePlanBean ratePlanBean,
                                            final int payment) throws Exception {

    if ((ratePlanDataBeanList == null || ratePlanDataBeanList.isEmpty()) &&
        (ratePlanSmsBeanList == null || ratePlanSmsBeanList.isEmpty()) &&
        (ratePlanVoiceBeanList == null || ratePlanVoiceBeanList.isEmpty())) {
      return Collections.emptyList();
    }
    List<RateBill> rateBillList = new ArrayList<>();

    boolean isPrice = true; // 对于超出的部分，是否需要进行批价
    boolean isShare = false;
    //事件资费，数据业务，批价规则不生效；语音业务和短信业务，批价规则生效
    //追加,堆叠只共享用量，不做批价处理
    //对于预付个人,预付固定共享,如果按照用量结束周期,则不做超出量的批价
    if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_EVENT ||
        ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_ADD   ||
        ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PILE  ||
        (ratePlanVersionBean.getIsExpireTermByUsage() == ParamEnum.IsExpireTermByUsage.YES &&
         (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
          ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE))) {
      isPrice = false;
    }
    if (ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_FIXSHARE   ||
        ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE  ||
        ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_MONTH_AGILESHARE ||
        ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_AGILESHARE) {
      isShare = true;
    }
    //数据业务
    if (ratePlanDataBeanList != null && !ratePlanDataBeanList.isEmpty()) {
      for (RatePlanDataBean ratePlanDataBean : ratePlanDataBeanList) {
        long baseValue = ParamUtil.getBaseValue(ParamEnum.BizType.BIZ_TYPE_DATA,ratePlanDataBean.getIncludeData());
        RateBill rateBill = new RateBill(ratePlanVersionBean.getPlanVersionId(), ratePlanDataBean.getBillId(), payment,
            ParamEnum.BizType.BIZ_TYPE_DATA, ratePlanDataBean.isRoamTag(),ratePlanDataBean.isBulkTag(),ratePlanDataBean.getZoneId(), 0,
            baseValue, ratePlanDataBean.getUnitRatio(),
            ratePlanDataBean.getBaseTimes(), ratePlanDataBean.getBaseUnit(), ratePlanDataBean.getPrecision(),
            isPrice, isShare, ParamEnum.ChargeMode.CHARGE_MODE_MERGE);
        rateBillList.add(rateBill);
      }
    }

    //根据用量终止期限(预付个人、预付固定),只对流量分支起作用，对语音，短信，不起作用
    if(ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_SINGLE ||
       ratePlanBean.getPlanType() == ParamEnum.PlanType.PLANTYPE_PREPAY_FIXSHARE){
      isPrice = true;
    }

    //语音业务
    if (ratePlanVoiceBeanList != null && !ratePlanVoiceBeanList.isEmpty()) {
      for (RatePlanVoiceBean ratePlanVoiceBean : ratePlanVoiceBeanList){
        setRateBill(ParamEnum.BizType.BIZ_TYPE_VOICE, isPrice, isShare, payment,
            ratePlanVersionBean,
            ratePlanVoiceBean,
            zoneBillingGroupMap,
            rateBillList);
      }
    }
    //短信业务
    if (ratePlanSmsBeanList != null && !ratePlanSmsBeanList.isEmpty()) {
      for (RatePlanSmsBean ratePlanSmsBean : ratePlanSmsBeanList){
        setRateBill(ParamEnum.BizType.BIZ_TYPE_SMS, isPrice, isShare, payment,
            ratePlanVersionBean,
            ratePlanSmsBean,
            zoneBillingGroupMap,
            rateBillList);
      }
    }
    return rateBillList;
  }

  /**
   * 语音、短信 用量相关的参数设置
   */
  private static void setRateBill (final int bizType,final boolean isPrice,final boolean isShare,final int paymentType,
                                  final RatePlanVersionBean ratePlanVersionBean,
                                  final RatePlanVoiceSms tRecord,
                                  final Map<Integer, List<ZoneBillingGroupBean>> zoneBillingGroupMap,
                                  List<RateBill> rateBillList) throws Exception{

    boolean subShare; // 合并计费，是否共享
    boolean moShare; // mo计费，是否共享
    boolean mtShare; // mt计费，是否共享

    if(bizType == ParamEnum.BizType.BIZ_TYPE_VOICE){
      moShare = ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoVoice()) && isShare;
      mtShare = ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMtVoice()) && isShare;
    }else{
      moShare = ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMoSms()) && isShare;
      mtShare = ParamEnum.SharedMo.SHARE.equals(ratePlanVersionBean.getSharedMtSms()) && isShare;
    }
    subShare = moShare && mtShare;

    int chargeTag = (bizType == ParamEnum.BizType.BIZ_TYPE_VOICE)? ratePlanVersionBean.getVoiceChargeTag():ratePlanVersionBean.getSmsChargeTag();
    int chargeMode = (bizType == ParamEnum.BizType.BIZ_TYPE_VOICE)? ratePlanVersionBean.getVoiceChargeMode():ratePlanVersionBean.getSmsChargeMode();

    long baseValue = ParamUtil.getBaseValue(bizType,tRecord.getInclude());

    RateBill rateBill = new RateBill(tRecord.getPlanVersionId(), tRecord.getBillId(), paymentType,
        bizType, tRecord.isRoamTag(),false, tRecord.getZoneId(), 0,
        baseValue, tRecord.getUnitRatio(),
        tRecord.getBaseUnit(), 1,tRecord.getPrecision(), isPrice, subShare, ParamEnum.ChargeMode.CHARGE_MODE_MERGE);
    //合并计费
    if (chargeMode == ParamEnum.ChargeMode.CHARGE_MODE_MERGE ) {

      rateBill.setShare(subShare);
      rateBill.setBillId(tRecord.getBillId());
      rateBill.setValue(baseValue);
      rateBill.setRatio(tRecord.getUnitRatio());
      rateBill.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MERGE);
      rateBillList.add(rateBill);
    }
    //MO计费
    else if (chargeMode == ParamEnum.ChargeMode.CHARGE_MODE_MO ) {
      //按计费组计费
      if(chargeTag == ParamEnum.ChargeTag.YES){
        rateBill.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MO);
        rateBill.setShare(moShare);
        List<ZoneBillingGroupBean> zoneBillingGroupBeanList = zoneBillingGroupMap.get(tRecord.getZoneBillingGroupId());
        dealZoneBillingGroup(zoneBillingGroupBeanList, rateBill, rateBillList);
      }else{
        baseValue = ParamUtil.getBaseValue(bizType,tRecord.getIncludeMo());

        rateBill.setShare(moShare);
        rateBill.setBillId(tRecord.getBillIdMo());
        rateBill.setValue(baseValue);
        rateBill.setRatio(tRecord.getUnitRatioMo());
        rateBill.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MO);
        rateBillList.add(rateBill);
      }
    }
    //MT计费
    else if (chargeMode == ParamEnum.ChargeMode.CHARGE_MODE_MT) {

      baseValue = ParamUtil.getBaseValue(bizType,tRecord.getIncludeMt());

      rateBill.setShare(mtShare);
      rateBill.setBillId(tRecord.getBillIdMt());
      rateBill.setValue(baseValue);
      rateBill.setRatio(tRecord.getUnitRatioMt());
      rateBill.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MT);
      rateBillList.add(rateBill);
    }
    //MO,MT分别计费
    else if (chargeMode == ParamEnum.ChargeMode.CHARGE_MODE_DETACH) {

      baseValue = ParamUtil.getBaseValue(bizType,tRecord.getIncludeMt());

      rateBill.setShare(mtShare);
      rateBill.setBillId(tRecord.getBillIdMt());
      rateBill.setValue(baseValue);
      rateBill.setRatio(tRecord.getUnitRatioMt());
      rateBill.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MT);
      rateBillList.add(rateBill);

      RateBill rateBillt = (RateBill) rateBill.clone();
      if(chargeTag == ParamEnum.ChargeTag.YES){
        rateBillt.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MO);
        rateBillt.setShare(moShare);
        List<ZoneBillingGroupBean> zoneBillingGroupBeanList = zoneBillingGroupMap.get(tRecord.getZoneBillingGroupId());
        dealZoneBillingGroup(zoneBillingGroupBeanList, rateBillt, rateBillList);
      }else{
        baseValue = ParamUtil.getBaseValue(bizType,tRecord.getIncludeMo());

        rateBillt.setShare(moShare);
        rateBillt.setBillId(tRecord.getBillIdMo());
        rateBillt.setValue(baseValue);
        rateBillt.setRatio(tRecord.getUnitRatioMo());
        rateBillt.setChargeMode(ParamEnum.ChargeMode.CHARGE_MODE_MO);
        rateBillList.add(rateBillt);
      }

    }

  }

  /**
   * 计费组 相关批价规则处理
   */
  private static void dealZoneBillingGroup(final List<ZoneBillingGroupBean> zoneBillingGroupBeanList,
                                           final RateBill rateBillBase,
                                           List<RateBill> rateBillList)
          throws CloneNotSupportedException {

    if (zoneBillingGroupBeanList != null && rateBillBase != null) {
      for (ZoneBillingGroupBean zoneBillingGroupBean : zoneBillingGroupBeanList) {

        RateBill rateBillt = (RateBill) rateBillBase.clone();

        long baseValue = ParamUtil.getBaseValue(rateBillBase.getBizType(),zoneBillingGroupBean.getIncludeValue());

        rateBillt.setPrecision(zoneBillingGroupBean.getPrecision());
        rateBillt.setValue(baseValue);
        rateBillt.setBillId(zoneBillingGroupBean.getBillId());
        rateBillt.setBillingGroupId(zoneBillingGroupBean.getBillingGroupId());
        rateBillt.setRatio(zoneBillingGroupBean.getUnitRatio());
        rateBillList.add(rateBillt);
      }
    }

  }


}
