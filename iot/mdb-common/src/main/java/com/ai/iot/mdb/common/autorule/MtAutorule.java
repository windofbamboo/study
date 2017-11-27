package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutorule;

/**
 * 同一个mdbkey的为一个mdbtable
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MtAutorule extends MdbTable {
  private long acctId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutorule() {
    addMoBaseField(MoAutorule.class);
  }

  ////////////////////////////////////
  ///mdbkey
  ////////////////////////////////////
  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_INFO + Const.KEY_JOIN + String.valueOf(acctId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ACCT_RULE_INFO;
  }
}
