package com.ai.iot.mdb.common.autorule.mos;

import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.mdb.MoBase;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.redisLdr.TABLE_ID;
import com.ai.iot.mdb.common.daos.BaseDao;
import com.ai.iot.mdb.common.daos.KeyId;
import com.ai.iot.mdb.common.mdb.MoBaseRegisterFactory;
import com.ai.iot.mdb.common.mdb.entity.SegInfo;
import com.ai.iot.mdb.common.mdb.entity.TotalInfo;
import com.ai.iot.mdb.common.mdb.loader.MoLoadable;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.dbutils.QueryRunner;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MoAutoruleOperCont extends MoBase implements MoLoadable, Serializable {
  private static final long serialVersionUID = 2029007884854263785L;
  //private static Logger logger = LoggerFactory.getLogger(MoAutoruleOperCont.class);
  @JSONField(name = "ruleId")
  private long ruleId;// key
  @JSONField(name = "ruleVerId")
  private int ruleVerId;
  @JSONField(name = "operContId")
  private long operContId;
  @JSONField(name = "operId")
  private int operId;
  @JSONField(name = "operType")
  private int operType;
  @JSONField(name = "operCont1")
  private String operCont1;
  @JSONField(name = "operCont2")
  private String operCont2;
  @JSONField(name = "operCont3")
  private String operCont3;
  @JSONField(name = "followOperMode")
  private String followOperMode;
  @JSONField(name = "followFixtimeType")
  private int followFixtimeType;
  @JSONField(name = "followFixtimeArg")
  private String followFixtimeArg;
  @JSONField(name = "updateTime")
  private long updateTime;

  /**
   * 关键字定义
   */
  public boolean isSame(MoAutoruleOperCont right) {
    return this.ruleId == right.getRuleId() && this.ruleVerId == right.getRuleVerId() &&
            this.operContId == right.getOperContId() && this.operId == right.getOperId() &&
            this.operType == right.getOperType();
  }

  public long getRuleId() {
    return ruleId;
  }

  public void setRuleId(long ruleId) {
    this.ruleId = ruleId;
  }

  public int getRuleVerId() {
    return ruleVerId;
  }

  public void setRuleVerId(int ruleVerId) {
    this.ruleVerId = ruleVerId;
  }

  public long getOperContId() {
    return operContId;
  }

  public void setOperContId(long operContId) {
    this.operContId = operContId;
  }

  public int getOperId() {
    return operId;
  }

  public void setOperId(int operId) {
    this.operId = operId;
  }

  public int getOperType() {
    return operType;
  }

  public void setOperType(int operType) {
    this.operType = operType;
  }

  public String getOperCont1() {
    return operCont1;
  }

  public void setOperCont1(String operCont1) {
    this.operCont1 = operCont1;
  }

  public String getOperCont2() {
    return operCont2;
  }

  public void setOperCont2(String operCont2) {
    this.operCont2 = operCont2;
  }

  public String getOperCont3() {
    return operCont3;
  }

  public void setOperCont3(String operCont3) {
    this.operCont3 = operCont3;
  }

  public String getFollowOperMode() {
    return followOperMode;
  }

  public void setFollowOperMode(String followOperMode) {
    this.followOperMode = followOperMode;
  }

  public int getFollowFixtimeType() {
    return followFixtimeType;
  }

  public void setFollowFixtimeType(int followFixtimeType) {
    this.followFixtimeType = followFixtimeType;
  }

  public String getFollowFixtimeArg() {
    return followFixtimeArg;
  }

  public void setFollowFixtimeArg(String followFixtimeArg) {
    this.followFixtimeArg = followFixtimeArg;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public byte[] getKey() {
    return generateKey(RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_OPER_CONT, ruleId);
  }

  @Override
  public void setKeyId(String keyId) {
    ruleId = Long.parseLong(keyId);
  }

  @Override
  public String getField() {
    return TABLE_ID.AUTORULE.TF_A_OPER_CONT_HASHKEY;
  }

  @Override
  public int getFieldType() {
    return MdbTables.FIELD_TYPE_LIST;
  }

  //获取本MO的总体信息：物理表中的ID总数，最小ID，最大ID。
  @Override
  public TotalInfo getTotalInfo(Connection conn, QueryRunner qr) {
    return null;
  }

  //把所有的ID划分成多个片，自己定义ID分片的方法，对于DeviceMo,默认采用AccountMo的分片方法：
  //例如一下5条ID，将会被分成5片：3000000000000000~300099999999999、3100000000000000~310099999999999、
  //3200000000000000~320099999999999、3300000000000000~330099999999999、3400000000000000~340099999999999
  //示例ID：
  //3000000001000111
  //3000000002000111
  //3000000002000112
  //3000000003000112
  //3400000003000111
  @Override
  public List<SegInfo> sliceIdsIntoSegs(TotalInfo totalInfo) {
    return null;
  }

  //分片去捞所有ID。
  //资料加载的时候根据ID，一片一片的取，为了达到边取边分发加载的效果。
  @Override
  public List<KeyId> fetchBatchIds(Connection conn, QueryRunner qr, SegInfo segInfo) {
    return null;
  }

  //派发的ID是不是本MO的key中的ID。
  //如加载DeviceInfo时，是按deviceId 派发的，那么就return true;
  //而一条设备记录相关的DeviceImsi之类的Mo, 因为派发时的DeviceId 并不是DeviceImsiMo的Key中的id，此时就需要返回false.
  @Override
  public boolean isKeyId() {
    return false;
  }

  //返回本MO对应的派发类型
  @Override
  public MoBaseRegisterFactory.MdbKeyDispTypeEnum getKeyDispType() {
    return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_AUTORULE_OPER;
  }

  //本MO从物理库加载的具体实现。单条
  @Override
  public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
      return BaseDao.selectOne(qr, conn, "InfoloadAutoruleMapper.getMdbRuleOperByRuleId", id);
  }

  //本MO从物理库加载的具体实现。多条
  @Override
  public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectList(qr, conn, "InfoloadAutoruleMapper.getMdbRuleOperByRuleId", id);
  }
}
