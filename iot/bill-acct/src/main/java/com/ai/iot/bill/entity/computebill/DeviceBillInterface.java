package com.ai.iot.bill.entity.computebill;

/**
 * Created by geyunfeng on 2017/7/28.
 */
public interface DeviceBillInterface {

  long getAcctId();

  int getCycleId();

  int getPlanId();

  int getItemId();

  long getFee();
}
