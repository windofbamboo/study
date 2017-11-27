package com.ai.iot.bill.entity.computebill;

/**
 * Created by geyunfeng on 2017/8/14.
 */
public interface BillUsageInterface {

  int getPlanVersionId();

  int getBizType();

  long getFee();

  boolean isRoam();
}
