package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerStatusAct;

/**
 * 同一个mdbkey的为一个mdbtable
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MtAutoruleAcctId extends MdbTable {
  private long acctId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutoruleAcctId() {
    addMoBaseField(MoAutoruleTriggerStatusAct.class);
    addMoIntegerField(MdbTables.TABLE_VER);
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }


  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ADDUP_STATUS_ACCT + Const.KEY_JOIN + String.valueOf(acctId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ADDUP_STATUS_ACCT;
  }

}
