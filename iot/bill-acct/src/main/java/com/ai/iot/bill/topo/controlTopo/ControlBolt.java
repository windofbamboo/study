package com.ai.iot.bill.topo.controlTopo;

import backtype.storm.tuple.Tuple;
import clojure.lang.MapEntry;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.entity.log.DeviceLog;
import com.ai.iot.bill.topo.BaseBolt;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**控制节点
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ControlBolt extends BaseBolt {

  private static final long serialVersionUID = 7464880713637500679L;

  private static final long TimeOut = 3600; //数据过期的秒数
  private Map<DealAcct,Long> dealList = new ConcurrentHashMap<>();
  private AtomicLong  lastTime = new AtomicLong();

  @Override
  public void setLogger(){
    logger = LoggerFactory.getLogger(ControlBolt.class);
  }

  @Override
  public void execute(Tuple input) {

    //获取数据
    DealAcct dealAcct = parseData(input,DealAcct.class);
    if(dealAcct != null){
      try{
        //数据处理
        if(!dealList.containsKey(dealAcct)){
          boolean finished = deal(dealAcct);
          if (finished) {
            if(reSendOne(CommonEnum.TOPIC_BILL,gson.toJson(dealAcct).getBytes())== Const.ERROR){
              this.collector.fail(input);
              logger.info("ControlBolt send billMq fail, [dealId : " + dealAcct.getDealId() + ",acctId : " + dealAcct.getAcctId() + " ]");
              return;
            }
            long currTime = DateUtil.nowAbsSeconds();//当前相对时间，到秒
            dealList.put(dealAcct,currTime);
            clearDealData(currTime);
          }
        }
        this.collector.ack(input);
      }catch (Exception e){
        this.collector.fail(input);
        logger.info("ControlBolt execute fail : {}", e.getMessage());
      }
    }else{
      this.collector.fail(input);
    }
  }


  private boolean deal(DealAcct dealAcct) {

    int dealNum = LogDao.getDeviceLogNum(dealAcct.getAcctId(),dealAcct.getDealId());
    return dealNum==0;
  }

  private void clearDealData(long currTime){

    if(lastTime.get() == 0){
      lastTime.set(currTime);
      return;
    }
    //一小时以内，不做处理
    if(currTime - lastTime.get() < TimeOut ){
      return;
    }
    //清楚过期的记录
    for (Map.Entry<DealAcct,Long> entry:dealList.entrySet()){
         if(currTime - entry.getValue() > TimeOut){
           dealList.remove(entry.getKey());
         }
    }
    lastTime.set(currTime);
  }


}
