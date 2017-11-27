package com.ai.iot.bill.topo.billTopo;

import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.topo.BaseSpout;
import org.slf4j.LoggerFactory;

/**账户级账单处理，账户列表获取
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BillSpout extends BaseSpout {

  private static final long serialVersionUID = 1579445337099570867L;

  @Override
  protected void setTopic(){
    kafkaMq.setTopic(CommonEnum.TOPIC_BILL);
  }

  @Override
  protected void setLogger(){
    logger = LoggerFactory.getLogger(BillSpout.class);
  }

  @Override
  public void faillog(byte[] data){
    DealAcct dealAcct =  gson.fromJson(new String(data),DealAcct.class);
    LogDao.updateDeallog(dealAcct.getDealId(), ErrEnum.AcctDealResult.FAIL);
  }

}
