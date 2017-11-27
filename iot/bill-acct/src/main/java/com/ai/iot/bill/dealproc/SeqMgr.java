package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.common.db.DataSourceMgr;
import com.ai.iot.bill.common.db.JdbcBaseDao;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.DateUtil;
import com.alibaba.jstorm.utils.JStormUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SeqMgr {

  private static final Logger logger = LoggerFactory.getLogger(SeqMgr.class);
  private static final int INIT = 0;
  private static final int MAX  = 99999999;
  private static AtomicInteger seqId = new AtomicInteger(INIT);
  private static AtomicBoolean updateTag = new AtomicBoolean(true);

  public static long getSeqId(final String seqName) {

    DataSource ds = DataSourceMgr.getDataSource(BaseDefine.CONNCODE_MYSQL_PARAM);
    if(seqId.compareAndSet(MAX,INIT)){
      updateTag.compareAndSet(true,false);
      String sqlStr = " UPDATE sequence SET current_value = ?  WHERE NAME = '" + seqName + "' ";
      JdbcBaseDao.execsql(ds, sqlStr,seqId.get());
      updateTag.compareAndSet(false,true);
    }

    while (!updateTag.get()){
      JStormUtils.sleepMs(40);
    }

    try {
      String sqlStr = " select nextval(\"" + seqName + "\") ";
      seqId.set(JdbcBaseDao.getInt(ds, sqlStr));

      String currDay = DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD);
      return Long.valueOf(currDay) * 100000000 + seqId.get();

    } catch (Exception e) {
      logger.error("getPlatSmsValue err.{}", e);
      return 0;
    }

  }


}
