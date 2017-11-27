package com.ai.iot.bill.dealproc;

import com.ai.iot.bill.dao.ParamDao;
import com.ai.iot.bill.dealproc.container.ParamContainer;
import com.ai.iot.bill.define.CommonEnum;
import com.alibaba.jstorm.utils.JStormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**参数类
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ParamMgr {

  private static final Logger logger = LoggerFactory.getLogger(ParamMgr.class);
  private static ParamContainer paramContainer = null;

  private static final int INIT   = 0;
  private static final int UPDATE = 1;
  private static final int FAIL   = 2;
  private static final int SUCESS = 3;
  private static AtomicInteger initTag = new AtomicInteger(INIT);
  private static long lastDealId = 0L;//批次号

  public static ParamContainer getParamContainer(final long dealId) {

    init(dealId);
    if(initTag.get() == SUCESS){
      return paramContainer;
    }else{
      return null;
    }

  }

  /**
   * 参数初始化
   */
  private static void init(final long dealId) {

    //初始化的判断
    if (initTag.compareAndSet(INIT,UPDATE)) {
      done(dealId);
    }
    //批次号的判断(用于重新加载)
    if(lastDealId < dealId){
      if(initTag.compareAndSet(SUCESS,UPDATE)){
        done(dealId);
      }
    }

    //等待更新线程完成
    for(;;){
      if(initTag.get() == UPDATE){
        JStormUtils.sleepMs(CommonEnum.SLEEP_TIME);
      }else{
        break;
      }
    }
  }

  private static void done(final long dealId) {

    try {
      paramContainer = ParamDao.initialize();
    } catch (Exception e) {
      logger.error("ParamDao initialize err: {}", e);
      initTag.set(FAIL);
    }
    lastDealId = dealId;
    initTag.set(SUCESS);
  }

}
