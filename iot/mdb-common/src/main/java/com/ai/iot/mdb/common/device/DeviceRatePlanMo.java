package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.mdb.common.daos.BaseDao;
import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.entity.SegInfo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import com.ai.iot.mdb.common.mdb.loader.MoLoadable;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

public class DeviceRatePlanMo extends MoBase implements MoLoadable {
  private static Logger logger = LoggerFactory.getLogger(DeviceRatePlanMo.class);
  private long deviceId;
  private long ratePlanInsId;
  private int planVersionId;
  private int planId;
  private int planType;
  private long startDate;
  private long endDate;
  private int poolId;
  private int renewalMode;//RENEWAL_MODE
  private boolean termByAmount;

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public long getRatePlanInsId() {
    return ratePlanInsId;
  }

  public void setRatePlanInsId(long ratePlanInsId) {
    this.ratePlanInsId = ratePlanInsId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public void setPlanVersionId(int planVersionId) {
    this.planVersionId = planVersionId;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int planId) {
    this.planId = planId;
  }

  public int getPlanType() {
    return planType;
  }

  public void setPlanType(int planType) {
    this.planType = planType;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public void setEndDate(long endDate) {
    this.endDate = endDate;
  }

  public int getPoolId() {
    return poolId;
  }

  public void setPoolId(int poolId) {
    this.poolId = poolId;
  }

  public int getRenewalMode() {
    return renewalMode;
}

public void setRenewalMode(int renewalMode) {
    this.renewalMode = renewalMode;
}

public boolean isTermByAmount() {
    return termByAmount;
  }

  public void setTermByAmount(boolean termByAmount) {
    this.termByAmount = termByAmount;
  }

  public DeviceRatePlanMo() {
  }

  public DeviceRatePlanMo(long ratePlanInsId) {
    this.ratePlanInsId = ratePlanInsId;
  }

  @Override
  public byte[] getKey() {
    return generateKey(MoBaseRegisterFactory.mdbOperaCode_DEVICE_ID, deviceId);
  }

  @Override
  public void setKeyId(String keyId) {
    logger.info("#############DeviceRatePlanMo keyId is:{}#################", keyId);
    deviceId = Long.parseLong(keyId);
  }

  @Override
  public String getField() {
    return MoBaseRegisterFactory.hashField_RATE_PLAN;
  }

  @Override
  public int getFieldType() {
    return MdbTables.FIELD_TYPE_LIST;
  }

  @Override
  public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
    return null;
  }

  @Override
  public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
    return null;
  }

  @Override
  public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
    return null;
  }

  @Override
  public boolean isKeyId() {
    return false;
  }

  @Override
  public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
    return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_DEVICE;
  }

  @Override
  public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getDeviceRatePlanByDeviceId", id);
  }

  @Override
  public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getDeviceRatePlanByDeviceId", id);
  }

  /**
   * 判断资费计划是否在有效期内
   *
   * @return
   */
  public boolean useful(long eventTime) {
    //long now = System.currentTimeMillis();
    return eventTime >= startDate && eventTime <= endDate;
  }
}
