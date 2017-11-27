package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;

public class MtAccount extends MdbTable {
  private long acctId;// key

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public MtAccount() {
    addMoBaseField(AccountMo.class);
    addMoBaseField(AccountAppMo.class);
  }

  @Override
  public String getKey() {
    return RedisConst.DevInfoMdbKey.MDB_KEY_ACCOUNT_BASE_INFO + Const.KEY_JOIN + String.valueOf(acctId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.DevInfoMdbKey.MDB_KEY_ACCOUNT_BASE_INFO;
  }

}
