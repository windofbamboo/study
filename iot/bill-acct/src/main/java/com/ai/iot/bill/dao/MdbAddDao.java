package com.ai.iot.bill.dao;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;
import com.ai.iot.bill.dealproc.util.BillUtil;
import com.ai.iot.bill.define.ParamEnum;
import com.ai.iot.bill.entity.usage.UsedAddDevice;
import com.ai.iot.bill.entity.usage.UsedAddPoolTotal;
import com.ai.iot.mdb.common.rate.MdbBillPoolSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum1Sms;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gprs;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Gsm;
import com.ai.iot.mdb.common.rate.MdbBillUserSum2Sms;
import com.ai.iot.mdb.common.rate.MdbTableRating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**累积量获取
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class MdbAddDao {

  private static final Logger logger = LoggerFactory.getLogger(MdbAddDao.class);

  private static MdbTables mdbRatingTables;
  private static Map<Integer, MdbTableRating> ratingMap = new HashMap<>();
  private static Lock tLock = new ReentrantLock();
  private static boolean initTag = false;

  private MdbAddDao() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 连接初始化
   */
  private static boolean init(){

    try {
      tLock.lock();
      if (!initTag) {
        try {
          CustJedisCluster custJedisCluster = RedisMgr.getJedisCluster(BaseDefine.CONNTYPE_REDIS_RATING,RedisMgr.MDB_SLAVE);

          if (custJedisCluster == null) {
            return false;
          }
          mdbRatingTables = new MdbTables(custJedisCluster);
        }catch (Exception e){
          logger.error("getJedisCluster err.{}",e);
          return false;
        }
        for (int i = 0; i <= 6; i++) {
          ratingMap.put(i, new MdbTableRating(i));
        }
        initTag = true;
      }
    } finally {
      tLock.unlock();
    }
    return true;
  }

  /**
   * 设备的累积量获取
   */
  public static List<UsedAddDevice> getDeviceAdd(final int cycleId,
                                                 final long acctId,
                                                 final long deviceId)
      throws Exception{

    if(!init()){
      return Collections.emptyList();
    }

    List<UsedAddDevice> usedAddDeviceList = new ArrayList<>();

    getDeviceGprs(cycleId,acctId,deviceId,usedAddDeviceList);
    getDeviceVoice(cycleId,acctId,deviceId,usedAddDeviceList);
    getDeviceSms(cycleId,acctId,deviceId,usedAddDeviceList);

    BillUtil.trimUsedAddDevice(usedAddDeviceList);

    return usedAddDeviceList;
  }

  /**
   * 预付固定共享累积量获取
   */
  public static List<UsedAddPoolTotal> getAcctAdd(final int cycleId,
                                                  final long acctId,
                                                  final long poolId)
      throws Exception{

    if(!init()){
      return Collections.emptyList();
    }

    List<UsedAddPoolTotal> usedAddPoolTotalList = new ArrayList<>();

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_POOL);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(0),"0",String.valueOf(acctId),String.valueOf(poolId));

    mdbRatingTables.selectTables(mdbTableRating);

    @SuppressWarnings("unchecked")
    List<MdbBillPoolSum2Gprs> mdbBillPoolSum2GprsList=(List<MdbBillPoolSum2Gprs>) mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_POOL_SUM2_GPRS);

    if(!mdbBillPoolSum2GprsList.isEmpty()) {
      mdbBillPoolSum2GprsList.stream().filter(t -> t.getCycleId() == cycleId).forEach(t -> {
        UsedAddPoolTotal usedAddPoolTotal = new UsedAddPoolTotal();

        usedAddPoolTotal.setAcctId(acctId);
        usedAddPoolTotal.setPoolId(poolId);
        usedAddPoolTotal.setCycleId(cycleId);
        usedAddPoolTotal.setPlanVersionId((int) t.getRatePlanVersionId());
        usedAddPoolTotal.setBillId(t.getBillId());
        usedAddPoolTotal.setCurrValue(t.getRoundValue());
        usedAddPoolTotal.setLastValue(t.getBaseValue());
        usedAddPoolTotal.setUpperValue(0);

        usedAddPoolTotalList.add(usedAddPoolTotal);
      });
    }
    BillUtil.trimUsedAddPoolTotal(usedAddPoolTotalList);
    return usedAddPoolTotalList;
  }

  /**
   * 设备的流量累积量获取
   */
  private static void getDeviceGprs(final int cycleId,
                                    final long acctId,
                                    final long deviceId,
                                    List<UsedAddDevice> usedAddDeviceList)
      throws Exception{

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_DATA);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));

    mdbRatingTables.selectTables(mdbTableRating);

    @SuppressWarnings("unchecked")
    List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList=(List<MdbBillUserSum1Gprs>) mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_GPRS+cycleId);
    @SuppressWarnings("unchecked")
    List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList=(List<MdbBillUserSum2Gprs>) mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_GPRS);

    if(!mdbBillUserSum2GprsList.isEmpty()){
      mdbBillUserSum2GprsList.stream().filter(t -> t.getCycleId() == cycleId).forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue()- t.getBaseValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(t.getBaseValue());
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(0);
        usedAddDevice.setMtValue(0);
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_DATA);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }

    if(!mdbBillUserSum1GprsList.isEmpty()) {
      mdbBillUserSum1GprsList.forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(0);
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(0);
        usedAddDevice.setMtValue(0);
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_DATA);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }
  }

  /**
   * 设备的语音累积量获取
   */
  private static void getDeviceVoice(final int cycleId,
                                    final long acctId,
                                    final long deviceId,
                                    List<UsedAddDevice> usedAddDeviceList)
      throws Exception{

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_VOICE);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));

    mdbRatingTables.selectTables(mdbTableRating);

    @SuppressWarnings("unchecked")
    List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList=(List<MdbBillUserSum1Gsm>) mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_GSM+cycleId);
    @SuppressWarnings("unchecked")
    List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList=(List<MdbBillUserSum2Gsm>) mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_GSM);

    if(!mdbBillUserSum2GsmList.isEmpty()) {
      mdbBillUserSum2GsmList.stream().filter(t -> t.getCycleId() == cycleId).forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue() - t.getBaseValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(t.getBaseValue());
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(t.getMoValue());
        usedAddDevice.setMtValue(t.getMtValue());
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_VOICE);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }

    if(!mdbBillUserSum1GsmList.isEmpty()) {
      mdbBillUserSum1GsmList.forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(0);
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(t.getMoValue());
        usedAddDevice.setMtValue(t.getMtValue());
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_VOICE);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }
  }

  /**
   * 设备的短信累积量获取
   */
  private static void getDeviceSms(final int cycleId,
                                     final long acctId,
                                     final long deviceId,
                                     List<UsedAddDevice> usedAddDeviceList)
      throws Exception{

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_SMS);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));

    mdbRatingTables.selectTables(mdbTableRating);

    @SuppressWarnings("unchecked")
    List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList=(List<MdbBillUserSum1Sms>)mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_SMS+cycleId);
    @SuppressWarnings("unchecked")
    List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList=(List<MdbBillUserSum2Sms>)mdbRatingTables.getList(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_SMS);

    if(!mdbBillUserSum2SmsList.isEmpty()) {
      mdbBillUserSum2SmsList.stream().filter(t -> t.getCycleId() == cycleId).forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue() - t.getBaseValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(t.getBaseValue());
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(t.getMoValue());
        usedAddDevice.setMtValue(t.getMtValue());
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_SMS);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }

    if(!mdbBillUserSum1SmsList.isEmpty()) {
      mdbBillUserSum1SmsList.forEach(t -> {
        UsedAddDevice usedAddDevice = new UsedAddDevice();
        usedAddDevice.setAcctId(acctId);
        usedAddDevice.setDeviceId(deviceId);
        usedAddDevice.setCycleId(cycleId);
        usedAddDevice.setTpInsId(t.getRatePlanInsId());
        usedAddDevice.setBillId(t.getBillId());
        usedAddDevice.setCurrValue(t.getRoundValue());
        usedAddDevice.setRoundAdjust(t.getRoundValue() - t.getValue());
        usedAddDevice.setLastValue(0);
        usedAddDevice.setUpperValue(0);
        usedAddDevice.setMoValue(t.getMoValue());
        usedAddDevice.setMtValue(t.getMtValue());
        usedAddDevice.setBizType(ParamEnum.BizType.BIZ_TYPE_SMS);
        usedAddDevice.setShareTag(false);

        usedAddDeviceList.add(usedAddDevice);
      });
    }
  }

  /**
   * 设备累积量的更新方法,仅在测试的时候，用于设置测试数据
   */
  public static void setDeviceAdd(final int cycleId,
                                   final long acctId,
                                   final long deviceId,
                                   List<MdbBillUserSum1Gprs> mdbBillUserSum1GprsList,
                                   List<MdbBillUserSum2Gprs> mdbBillUserSum2GprsList,
                                   List<MdbBillUserSum1Gsm> mdbBillUserSum1GsmList,
                                   List<MdbBillUserSum2Gsm> mdbBillUserSum2GsmList,
                                   List<MdbBillUserSum1Sms> mdbBillUserSum1SmsList,
                                   List<MdbBillUserSum2Sms> mdbBillUserSum2SmsList)
      throws Exception{

    if(!init()) return;

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_DATA);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));
    mdbRatingTables.setMdbTable(mdbTableRating);

    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_GPRS + cycleId, mdbBillUserSum1GprsList);
    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_GPRS,mdbBillUserSum2GprsList);
    mdbRatingTables.updateTables();

    mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_VOICE);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));
    mdbRatingTables.setMdbTable(mdbTableRating);

    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_GSM + cycleId, mdbBillUserSum1GsmList);
    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_GSM,mdbBillUserSum2GsmList);
    mdbRatingTables.updateTables();

    mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_DEVICE_SMS);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(deviceId),"0",String.valueOf(acctId),String.valueOf(0));
    mdbRatingTables.setMdbTable(mdbTableRating);

    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM1_SMS + cycleId, mdbBillUserSum1SmsList);
    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_USER_SUM2_SMS,mdbBillUserSum2SmsList);

    mdbRatingTables.updateTables();
  }

  /**
   * 预付共享池累积量的更新方法,仅在测试的时候，用于设置测试数据
   */
  public static void setPoolAdd(final int cycleId,
                                final long acctId,
                                final long poolId,
                                List<MdbBillPoolSum2Gprs> mdbBillPoolSum2GprsList)
      throws Exception{

    if(!init()) return;

    MdbTableRating mdbTableRating=ratingMap.get(MdbTableRating.TABLE_TYPE_POOL);
    mdbTableRating.reset(String.valueOf(cycleId),String.valueOf(0),"0",String.valueOf(acctId),String.valueOf(poolId));
    mdbRatingTables.setMdbTable(mdbTableRating);

    mdbRatingTables.setData(TABLE_ID.RATING_FIELDNAME.BILL_POOL_SUM2_GPRS,mdbBillPoolSum2GprsList);
    mdbRatingTables.updateTables();
  }







}
