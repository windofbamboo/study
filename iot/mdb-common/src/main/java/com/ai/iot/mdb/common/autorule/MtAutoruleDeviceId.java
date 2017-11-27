package com.ai.iot.mdb.common.autorule;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.mdb.MdbTables;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerAddUp;
import com.ai.iot.mdb.common.autorule.mos.MoAutoruleTriggerStatusDev;

/**
 * 同一个mdbkey的为一个mdbtable
 * 账户自动化规则
 *
 * @author qianwx
 */
public class MtAutoruleDeviceId extends MdbTable {
  private long deviceId;//key

  ////////////////////////////////////
  ///构造
  ////////////////////////////////////
  public MtAutoruleDeviceId() {
    addMoBaseField(MoAutoruleTriggerAddUp.class);
    addMoBaseField(MoAutoruleTriggerStatusDev.class);
    addMoIntegerField(MdbTables.TABLE_VER);
  }

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }


  @Override
  public String getKey() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ADDUP_STATUS_DEV + Const.KEY_JOIN + String.valueOf(deviceId);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.AutoRuleMdbKey.MDB_KEY_ADDUP_STATUS_DEV;
  }

}
