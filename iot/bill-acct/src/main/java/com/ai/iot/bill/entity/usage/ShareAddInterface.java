package com.ai.iot.bill.entity.usage;

public interface ShareAddInterface {

  long getAcctId();

  int getPlanVersionId();

  int getBillId();

  long getCurrValue();

  long getRoundAdjust();

  long getBulkAdjust();
}
