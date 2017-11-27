package acct;

public class DeviceInfo {

  private long acctId;
  private long operAcctId;
  private long orderId;
  private long deviceId;
  private int poolId;
  private int planType;
  private int planId;
  private int planVersionId;
  private int partitionId;

  DeviceInfo(long acctId, long operAcctId, long orderId, long deviceId, int poolId, int planType, int planId, int planVersionId) {
    this.acctId = acctId;
    this.operAcctId = operAcctId;
    this.orderId = orderId;
    this.deviceId = deviceId;
    this.poolId = poolId;
    this.planType = planType;
    this.planId = planId;
    this.planVersionId = planVersionId;
    this.partitionId = (int)(deviceId%10000);
  }

  public long getAcctId() {
    return acctId;
  }

  public long getOperAcctId() {
    return operAcctId;
  }

  public long getOrderId() {
    return orderId;
  }

  public long getDeviceId() {
    return deviceId;
  }

  public int getPoolId() {
    return poolId;
  }

  public int getPlanType() {
    return planType;
  }

  public int getPlanId() {
    return planId;
  }

  public int getPlanVersionId() {
    return planVersionId;
  }

  public int getPartitionId() {
    return partitionId;
  }
}
