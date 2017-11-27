package com.ai.iot.bill.entity.res;

import java.sql.Date;

/**资源的基本情况
 * Created by geyunfeng on 2017/8/14.
 */
public interface ResInterface {

  long getAcctId();

  int getCycleId();

  int getPlanId();

  int getPlanVersionId();

  int getZoneId();

  int getBillId();

  Date getStartTime();

  Date getEndTime();

  long getValue();
}
