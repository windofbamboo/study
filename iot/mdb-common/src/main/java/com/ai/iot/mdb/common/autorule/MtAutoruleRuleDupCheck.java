package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleRuleDupcheck;

/**
 * 同一个mdbkey的为一个mdbtable
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MtAutoruleRuleDupCheck extends MdbTable {
  private String guid;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutoruleRuleDupCheck() {
    addMoBaseField(MoAutoruleRuleDupcheck.class);
    addMoIntegerField(MdbTables.TABLE_VER);
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_DUPCHECK_RULE + Const.KEY_JOIN + guid;
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_DUPCHECK_RULE;
  }

}
