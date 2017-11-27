package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.util.CheckNull;
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
import shade.storm.org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AccountMo extends MoBase implements MoLoadable {
  private final static Logger logger = LoggerFactory.getLogger(AccountMo.class);

  private Long acctId;
  private String automaticFlag;
  private String removeTag;
  private Long removeTime;
  private String operatorCode;
  private String provinceCode;
  private String  acctName;
  private Long createDate;

  public Long getAcctId() {
    return acctId;
  }

  public void setAcctId(Long acctId) {
    this.acctId = acctId;
  }

  public String getAutomaticFlag() {
    return automaticFlag;
  }

  public void setAutomaticFlag(String automaticFlag) {
    this.automaticFlag = automaticFlag;
  }

  public String getRemoveTag() {
    return removeTag;
  }

  public void setRemoveTag(String removeTag) {
    this.removeTag = removeTag;
  }

  public Long getRemoveTime() {
    return removeTime;
  }

  public void setRemoveTime(Long removeTime) {
    this.removeTime = removeTime;
  }

  public String getOperatorCode() {
    return operatorCode;
  }

  public void setOperatorCode(String operatorCode) {
    this.operatorCode = operatorCode;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getAcctName() {
    return acctName;
}

public void setAcctName(String acctName) {
    this.acctName = acctName;
}

public Long getCreateDate() {
    return createDate;
}

public void setCreateDate(Long createDate) {
    this.createDate = createDate;
}

@Override
  public byte[] getKey() {
    StringBuffer key = new StringBuffer();
    key.append(MoBaseRegisterFactory.mdbOperaCode_ACCOUNT_ID);
    key.append(MoBaseRegisterFactory.separator);
    key.append(acctId);
    return key.toString().getBytes();
  }

  @Override
  public void setKeyId(String keyId) {
    acctId = Long.parseLong(keyId);
  }

  @Override
  public String getField() {
    return MoBaseRegisterFactory.hashField_ACCOUNT;
  }

  @Override
  public int getFieldType() {
    return MdbTables.FIELD_TYPE_OBJECT;
  }

  @Override
  public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
    TotalInfo totalInfo = null;
    try {
      totalInfo = BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getAccountMoTotalInfo");
    } catch (Exception e) {
      logger.error("getPlatSmsValue err ", e);
    }
    return totalInfo;
  }

  public static List<SegInfo> defaultSliceIds(TotalInfo totalInfo, int bit, long idRange) {
    int minFirst4 = (int) (totalInfo.getMinId() / idRange);
    int maxFirst4 = (int) (totalInfo.getMaxId() / idRange);

    List<SegInfo> segInfos = new ArrayList<>();
    SegInfo segInfo = null;
    long segPartMin = minFirst4 / bit;
    long segPartMax = maxFirst4 / bit;
    if (segPartMax == segPartMin) {
      segInfo = new SegInfo();
      segInfo.setMinId((segPartMin * bit * idRange) + 0L);
      segInfo.setMaxId((segPartMin * bit * idRange) + idRange - 1);
      segInfos.add(segInfo);
      return segInfos;
    }

    for (int i = 0; i <= segPartMax - segPartMin; i++) {
      segInfo = new SegInfo();
      segInfo.setMinId((((segPartMin + i) * bit) * idRange) + 0L);
      segInfo.setMaxId((((segPartMin + i) * bit) * idRange) + idRange - 1);
      segInfos.add(segInfo);
    }
    return segInfos;
  }

  @Override
  public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
    if (CheckNull.isNull(totalInfo)) {
      return null;
    }

    return AccountMo.defaultSliceIds(totalInfo, 10, 1000000000000L);
  }

  @Override
  public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
    return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getAccountIdSeg", segInfo.getMinId(), segInfo.getMaxId());
  }

  @Override
  public boolean isKeyId() {
    return true;
  }

  @Override
  public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
    return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_ACCOUNT;
  }

  @Override
  public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectOne(qr, conn, "InfoloadDevInfoMapper.getAccountByAcctId", id);
  }

  @Override
  public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectList(qr, conn, "InfoloadDevInfoMapper.getAccountByAcctId", id);
  }

  /**
   * 判断账户是否开启自动化规则
   *
   * @return
   */
  public boolean useRule() {
    return ((!automaticFlag.isEmpty())&&StringUtils.contains(automaticFlag, "1")) && StringUtils.equals(removeTag, "0");
  }
}
