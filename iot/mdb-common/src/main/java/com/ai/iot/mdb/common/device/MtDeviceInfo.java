package com.ai.iot.mdb.common.device;

import com.ai.iot.bill.common.mdb.MdbTable;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.Const;

/**
 * @author qianwx
 */
public class MtDeviceInfo extends MdbTable {

  private long deviceId;

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public MtDeviceInfo() {
    addMoBaseField(DeviceMo.class);
    addMoBaseField(DeviceRatePlanMo.class);
    addMoBaseField(DeviceOthersMo.class);
    addMoBaseField(RenewalRateQueueMo.class);
  }

  @Override
  public String getMdbTableKeyId() {
    return RedisConst.DevInfoMdbKey.MDB_KEY_DEVICE_ID_START_DATE;
  }

  @Override
  public String getKey() {
    return RedisConst.DevInfoMdbKey.MDB_KEY_DEVICE_ID_START_DATE + Const.KEY_JOIN + String.valueOf(deviceId);
  }
}
