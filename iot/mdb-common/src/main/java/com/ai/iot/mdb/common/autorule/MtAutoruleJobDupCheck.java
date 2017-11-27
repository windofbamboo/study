package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleJobDupcheck;

/**
 * 同一个mdbkey的为一个mdbtable
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MtAutoruleJobDupCheck extends MdbTable {
  private long jobId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutoruleJobDupCheck() {
    addMoBaseField(MoAutoruleJobDupcheck.class);
    addMoIntegerField(MdbTables.TABLE_VER);
  }

  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }

  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_DUPCHECK_JOB + Const.KEY_JOIN + String.valueOf(jobId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_DUPCHECK_JOB;
  }

}
