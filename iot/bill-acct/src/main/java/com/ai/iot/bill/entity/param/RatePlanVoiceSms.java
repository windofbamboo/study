package com.ai.iot.bill.entity.param;

/**
 * Created by geyunfeng on 2017/8/31.
 */
public interface RatePlanVoiceSms {

  int getPlanVersionId();

  int getZoneId();

  boolean isRoamTag();

  int getBaseUnit();

  int getBillId();

  int getBillIdMo();

  int getBillIdMt();

  long getInclude();

  long getIncludeMo();

  long getIncludeMt();

  long getUnitRatio();

  long getUnitRatioMo();

  long getUnitRatioMt();

  int getZoneBillingGroupId();

  int getPrecision();
}
