package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoSubAcct;

/**
 * @author qianwx
 */
public class MtSubAcct extends MdbTable {
  private long subAcctId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtSubAcct() {
    addMoBaseField(MoSubAcct.class);
  }

  public long getSubAcctId() {
    return subAcctId;
  }

  public void setSubAcctId(long subAcctId) {
    this.subAcctId = subAcctId;
  }

  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_SUB_ACCT + Const.KEY_JOIN + String.valueOf(subAcctId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_SUB_ACCT;
  }
}
