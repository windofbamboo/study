package com.ai.iot.bill.topo.billTopo;

import backtype.storm.tuple.Tuple;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dealproc.BillDeal;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.topo.BaseBolt;
import org.slf4j.LoggerFactory;

/**账户级账单处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BillBolt extends BaseBolt {

  private static final long serialVersionUID = 7464880713637500679L;

  @Override
  public void setLogger(){
    logger = LoggerFactory.getLogger(BillBolt.class);
  }

  @Override
  public void execute(Tuple input) {

    //获取数据
    DealAcct dealAcct = parseData(input,DealAcct.class);
    if (dealAcct != null){
      try {
        //数据处理
        if (BillDeal.deal(dealAcct)) {
          LogDao.updateDeallog(dealAcct.getDealId(), ErrEnum.AcctDealResult.SUCESS);
          collector.ack(input);
        } else {
          LogDao.updateDeallog(dealAcct.getDealId(), ErrEnum.AcctDealResult.FAIL);
          collector.ack(input);
          logger.info("BillBolt BillDeal.deal fail, [dealId : " + dealAcct.getDealId() + ",acctId : " + dealAcct.getAcctId() + " ]");
        }
      }catch (Exception e){
        this.collector.fail(input);
        logger.info("BillBolt execute fail : {}", e.getMessage());
      }
    }else{
      this.collector.fail(input);
    }

  }



}
