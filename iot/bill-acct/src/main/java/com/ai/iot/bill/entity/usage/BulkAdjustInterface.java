package com.ai.iot.bill.entity.usage;


public interface BulkAdjustInterface {

  long getAcctId();

  int getBillId();

  long getBulkAdjust();

  void setBulkAdjust(long bulkAdjust);

}
