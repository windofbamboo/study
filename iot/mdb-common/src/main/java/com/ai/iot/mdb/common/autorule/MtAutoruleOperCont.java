package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleOperCont;

/**
 * @author qianwx
 */
public class MtAutoruleOperCont extends MdbTable {
  private long ruleId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutoruleOperCont() {
    addMoBaseField(MoAutoruleOperCont.class);
  }

  public long getRuleId() {
    return ruleId;
  }

  public void setRuleId(long ruleId) {
    this.ruleId = ruleId;
  }

  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_OPER_CONT + Const.KEY_JOIN + String.valueOf(ruleId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_OPER_CONT;
  }
}
