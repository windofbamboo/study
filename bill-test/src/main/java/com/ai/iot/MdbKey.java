package com.ai.iot;

/**
 * Created by geyunfeng on 2017/10/9.
 */
public class MdbKey {
  private long acctId;
  private long deviceId;
  private int cycleId;
  private long poolId;

  public MdbKey() {
  }

  public MdbKey(long acctId, long deviceId, int cycleId, long poolId) {
    this.acctId = acctId;
    this.deviceId = deviceId;
    this.cycleId = cycleId;
    this.poolId = poolId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MdbKey mdbKey = (MdbKey) o;

    if (acctId != mdbKey.acctId) return false;
    if (deviceId != mdbKey.deviceId) return false;
    if (cycleId != mdbKey.cycleId) return false;
    return poolId == mdbKey.poolId;

  }

  @Override
  public int hashCode() {
    int result = (int) (acctId ^ (acctId >>> 32));
    result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
    result = 31 * result + cycleId;
    result = 31 * result + (int) (poolId ^ (poolId >>> 32));
    return result;
  }

  public long getAcctId() {
    return acctId;
  }

  public void setAcctId(long acctId) {
    this.acctId = acctId;
  }

  public long getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(long deviceId) {
    this.deviceId = deviceId;
  }

  public int getCycleId() {
    return cycleId;
  }

  public void setCycleId(int cycleId) {
    this.cycleId = cycleId;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long poolId) {
    this.poolId = poolId;
  }
}
