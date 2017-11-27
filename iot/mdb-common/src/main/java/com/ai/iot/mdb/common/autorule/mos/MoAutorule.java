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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import shade.storm.com.google.common.collect.Lists;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MoAutorule extends MoBase implements MoLoadable, Serializable {
  private static final long serialVersionUID = 2029007884854263785L;
  //private static Logger logger = LoggerFactory.getLogger(MoAutorule.class);
  @JSONField(name = "acctId")
  private long acctId;// 目标账户id
  @JSONField(name = "ruleId")
  private long ruleId;// 规则id
  @JSONField(name = "ruleVerId")
  private int ruleVerId;// 版本id
  @JSONField(name = "ruleType")
  private int ruleType;// 规则类型 0 普通账户 1 运营商 2 省份
  @JSONField(name = "ruleStatus")
  private int ruleStatus;// 规则状态 0 激活 1 停用 2删除
  @JSONField(name = "isUse")
  private int isUse;// 规则是否可用 0 false 1 true
  @JSONField(name = "operatorCode")
  private String operatorCode;// 目标运营商
  @JSONField(name = "provCode")
  private String provCode; // 目标省份
  @JSONField(name = "triggerId")
  private int triggerId = -1;// 触发器id
  @JSONField(name = "triggerArg1")
  private String triggerArg1;// 触发器参数1
  @JSONField(name = "triggerArg2")
  private String triggerArg2;// 触发器参数2
  @JSONField(name = "triggerArg3")
  private String triggerArg3;// 触发器参数3
  @JSONField(name = "filter1Arg")
  private String filter1Arg;// 筛选器参数1
  @JSONField(name = "filter2Arg")
  private String filter2Arg;// 筛选器参数2
  @JSONField(name = "filter3Arg")
  private String filter3Arg;// 筛选器参数3
  @JSONField(name = "filter101Arg")
  private String filter101Arg;// 筛选器101参数
  @JSONField(name = "filter102Arg")
  private String filter102Arg;// 筛选器102参数
  @JSONField(name = "filter103Arg")
  private String filter103Arg;// 筛选器103参数
  @JSONField(name = "filter104Arg")
  private String filter104Arg;// 筛选器104参数
  @JSONField(name = "filter105Arg")
  private String filter105Arg;// 筛选器105参数
  @JSONField(name = "filter106Arg")
  private String filter106Arg;// 筛选器106参数
  @JSONField(name = "filter107Arg")
  private String filter107Arg;// 筛选器107参数
  @JSONField(name = "filter108Arg")
  private String filter108Arg;// 筛选器108参数
  @JSONField(name = "filter109Arg")
  private String filter109Arg;// 筛选器109参数
  @JSONField(name = "filter110Arg")
  private String filter110Arg;// 筛选器110参数
  @JSONField(name = "filter201Arg")
  private String filter201Arg;// 筛选器201参数
  @JSONField(name = "filter202Arg")
  private String filter202Arg;// 筛选器202参数
  @JSONField(name = "filter203Arg")
  private String filter203Arg;// 筛选器203参数
  @JSONField(name = "filter204Arg")
  private String filter204Arg;// 筛选器204参数
  @JSONField(name = "filter205Arg")
  private String filter205Arg;// 筛选器205参数
  @JSONField(name = "filter301Arg")
  private String filter301Arg;// 筛选器301参数
  @JSONField(name = "filter302Arg")
  private String filter302Arg;// 筛选器302参数
  @JSONField(name = "filter303Arg")
  private String filter303Arg;// 筛选器303参数
  @JSONField(name = "filter304Arg")
  private String filter304Arg;// 筛选器304参数
  @JSONField(name = "filter305Arg")
  private String filter305Arg;// 筛选器305参数
  @JSONField(name = "filter401Arg")
  private String filter401Arg;// 筛选器401参数
  @JSONField(name = "filter402Arg")
  private String filter402Arg;// 筛选器402参数
  @JSONField(name = "filter403Arg")
  private String filter403Arg;// 筛选器403参数
  @JSONField(name = "filter404Arg")
  private String filter404Arg;// 筛选器404参数
  @JSONField(name = "filter405Arg")
  private String filter405Arg;// 筛选器405参数
  @JSONField(name = "updateTime")
  private long updateTime;// 更新时间


  /**
   * 关键字定义
   */
  public boolean isSame(MoAutorule right) {
    return this.acctId == right.getAcctId() && this.ruleId == right.getRuleId() && this.ruleVerId == right.getRuleVerId();
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
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

  public int getRuleType() {
    return ruleType;
  }

  public void setRuleType(int ruleType) {
    this.ruleType = ruleType;
  }

  public int getRuleStatus() {
    return ruleStatus;
  }

  public void setRuleStatus(int ruleStatus) {
    this.ruleStatus = ruleStatus;
  }

  public int getIsUse() {
    return isUse;
  }

  public void setIsUse(int isUse) {
    this.isUse = isUse;
  }

  public String getOperatorCode() {
    return operatorCode;
  }

  public void setOperatorCode(String operatorCode) {
    this.operatorCode = operatorCode;
  }

  public String getProvCode() {
    return provCode;
  }

  public void setProvCode(String provCode) {
    this.provCode = provCode;
  }

  public int getTriggerId() {
    return triggerId;
  }

  public void setTriggerId(int triggerId) {
    this.triggerId = triggerId;
  }

  public String getTriggerArg1() {
    return triggerArg1;
  }

  public void setTriggerArg1(String triggerArg1) {
    this.triggerArg1 = triggerArg1;
  }

  public String getTriggerArg2() {
    return triggerArg2;
  }

  public void setTriggerArg2(String triggerArg2) {
    this.triggerArg2 = triggerArg2;
  }

  public String getTriggerArg3() {
    return triggerArg3;
  }

  public void setTriggerArg3(String triggerArg3) {
    this.triggerArg3 = triggerArg3;
  }

  public String getFilter1Arg() {
    return filter1Arg;
  }

  public void setFilter1Arg(String filter1Arg) {
    this.filter1Arg = filter1Arg;
  }

  public String getFilter2Arg() {
    return filter2Arg;
  }

  public void setFilter2Arg(String filter2Arg) {
    this.filter2Arg = filter2Arg;
  }

  public String getFilter3Arg() {
    return filter3Arg;
  }

  public void setFilter3Arg(String filter3Arg) {
    this.filter3Arg = filter3Arg;
  }

  public String getFilter101Arg() {
    return filter101Arg;
  }

  public void setFilter101Arg(String filter101Arg) {
    this.filter101Arg = filter101Arg;
  }

  public String getFilter102Arg() {
    return filter102Arg;
  }

  public void setFilter102Arg(String filter102Arg) {
    this.filter102Arg = filter102Arg;
  }

  public String getFilter103Arg() {
    return filter103Arg;
  }

  public void setFilter103Arg(String filter103Arg) {
    this.filter103Arg = filter103Arg;
  }

  public String getFilter104Arg() {
    return filter104Arg;
  }

  public void setFilter104Arg(String filter104Arg) {
    this.filter104Arg = filter104Arg;
  }

  public String getFilter105Arg() {
    return filter105Arg;
  }

  public void setFilter105Arg(String filter105Arg) {
    this.filter105Arg = filter105Arg;
  }

  public String getFilter106Arg() {
    return filter106Arg;
  }

  public void setFilter106Arg(String filter106Arg) {
    this.filter106Arg = filter106Arg;
  }

  public String getFilter107Arg() {
    return filter107Arg;
  }

  public void setFilter107Arg(String filter107Arg) {
    this.filter107Arg = filter107Arg;
  }

  public String getFilter108Arg() {
    return filter108Arg;
  }

  public void setFilter108Arg(String filter108Arg) {
    this.filter108Arg = filter108Arg;
  }

  public String getFilter109Arg() {
    return filter109Arg;
  }

  public void setFilter109Arg(String filter109Arg) {
    this.filter109Arg = filter109Arg;
  }

  public String getFilter110Arg() {
    return filter110Arg;
  }

  public void setFilter110Arg(String filter110Arg) {
    this.filter110Arg = filter110Arg;
  }

  public String getFilter201Arg() {
    return filter201Arg;
  }

  public void setFilter201Arg(String filter201Arg) {
    this.filter201Arg = filter201Arg;
  }

  public String getFilter202Arg() {
    return filter202Arg;
  }

  public void setFilter202Arg(String filter202Arg) {
    this.filter202Arg = filter202Arg;
  }

  public String getFilter203Arg() {
    return filter203Arg;
  }

  public void setFilter203Arg(String filter203Arg) {
    this.filter203Arg = filter203Arg;
  }

  public String getFilter204Arg() {
    return filter204Arg;
  }

  public void setFilter204Arg(String filter204Arg) {
    this.filter204Arg = filter204Arg;
  }

  public String getFilter205Arg() {
    return filter205Arg;
  }

  public void setFilter205Arg(String filter205Arg) {
    this.filter205Arg = filter205Arg;
  }

  public String getFilter301Arg() {
    return filter301Arg;
  }

  public void setFilter301Arg(String filter301Arg) {
    this.filter301Arg = filter301Arg;
  }

  public String getFilter302Arg() {
    return filter302Arg;
  }

  public void setFilter302Arg(String filter302Arg) {
    this.filter302Arg = filter302Arg;
  }

  public String getFilter303Arg() {
    return filter303Arg;
  }

  public void setFilter303Arg(String filter303Arg) {
    this.filter303Arg = filter303Arg;
  }

  public String getFilter304Arg() {
    return filter304Arg;
  }

  public void setFilter304Arg(String filter304Arg) {
    this.filter304Arg = filter304Arg;
  }

  public String getFilter305Arg() {
    return filter305Arg;
  }

  public void setFilter305Arg(String filter305Arg) {
    this.filter305Arg = filter305Arg;
  }

  public String getFilter401Arg() {
    return filter401Arg;
  }

  public void setFilter401Arg(String filter401Arg) {
    this.filter401Arg = filter401Arg;
  }

  public String getFilter402Arg() {
    return filter402Arg;
  }

  public void setFilter402Arg(String filter402Arg) {
    this.filter402Arg = filter402Arg;
  }

  public String getFilter403Arg() {
    return filter403Arg;
  }

  public void setFilter403Arg(String filter403Arg) {
    this.filter403Arg = filter403Arg;
  }

  public String getFilter404Arg() {
    return filter404Arg;
  }

  public void setFilter404Arg(String filter404Arg) {
    this.filter404Arg = filter404Arg;
  }

  public String getFilter405Arg() {
    return filter405Arg;
  }

  public void setFilter405Arg(String filter405Arg) {
    this.filter405Arg = filter405Arg;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public byte[] getKey() {
    return generateKey(RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_INFO, acctId);
  }

  @Override
  public void setKeyId(String keyId) {
    acctId = Long.parseLong(keyId);
  }

  @Override
  public String getField() {
    return TABLE_ID.AUTORULE.TF_A_AUTO_RULE_HASHKEY;
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
    return MoBaseRegisterFactory.MdbKeyDispTypeEnum.DISP_TYPE_AUTORULE;
  }

  //本MO从物理库加载的具体实现。单条
  @Override
  public MoBase loadDataFromPdb(Connection conn, String id, QueryRunner qr) {
    return BaseDao.selectOne(qr, conn, "InfoloadAutoruleMapper.getMdbAutoruleByAcctId", id);
  }

  //本MO从物理库加载的具体实现。多条
  @Override
  public List<MoBase> loadDataListFromPdb(Connection conn, String id, QueryRunner qr) {
    List<MoBase> ruleBases = BaseDao.selectList(qr, conn, "InfoloadAutoruleMapper.getMdbAutoruleByAcctId", id);
    List<MoBase> baseList = Lists.newArrayList();
    if (CollectionUtils.isEmpty(ruleBases)) {
      return ruleBases;
    }
    //加载moautoruleopercont，以实现根据acctId一次性加载rule及ruleOpercont
    baseList.addAll(ruleBases);
    ruleBases.forEach(rule -> {
      MoAutorule autorule = (MoAutorule) rule;
      baseList.addAll(BaseDao.selectList(qr, conn,
              "InfoloadAutoruleMapper.getMdbRuleOperByRuleId", String.valueOf(autorule.getRuleId())));
    });
    return baseList;
  }
}
