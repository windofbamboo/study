package com.ai.iot.bill.topo;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.define.CommonEnum;
import com.google.gson.Gson;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public abstract class BaseBolt implements IRichBolt {

  private static final long serialVersionUID = 6407843764363569148L;
  protected Logger logger = null;
  protected KafkaMq kafkaMq = null;
  protected Gson gson = null;
  protected OutputCollector collector;

  @Override
  public void prepare(Map stormConf, TopologyContext context,
                      OutputCollector collector) {
    this.collector = collector;
    this.gson = new Gson();
    setLogger();
    try {
      kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_AND_WRITE);
    }catch (Exception e){
      logger.info("KafkaMq init fail :{} " , e);
    }
  }

  protected void setLogger(){
  }

  @Override
  public void execute(Tuple input) {

  }
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

  }

  @Override
  public void cleanup() {
    this.kafkaMq.disconnect();
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    //do nothing
    return null;
  }

  protected <T> T parseData(final Tuple input,
                            final Class<T> type){
    try{
      byte[] t = input.getBinary(0);
      return gson.fromJson(new String(t),type);
    }catch (Exception e){
      logger.info("fromJson err :{} ",e.getMessage());
    }
    return null;
  }

  protected <T> List<T> reSendBatch(final String topic,
                                    final List<T> sendList) {
    List<T> failList = sendList;
    for(int count = 0;count<CommonEnum.RETRY_TIME;count++){
        failList = reSendList(topic, failList);
      if(failList.isEmpty())
        break;
    }
    return failList;
  }

  private <T> List<T> reSendList(final String topic,
                                 final List<T> sendList){
    kafkaMq.resetBatch();
    for(T rec : sendList){
      byte[] data = gson.toJson(rec).getBytes();
      kafkaMq.addMsgBytes(topic, data);
    }
    List<Integer> rets=kafkaMq.sendBatch();
    List<T> failList = new ArrayList<>();
    if(rets!=null && !rets.isEmpty()){
      kafkaMq.resetBatch();
      for(int k=0; k<rets.size(); k++){
        int result = rets.get(k);
        if(result==-1){
          failList.add(sendList.get(k));
        }
      }
      sendList.clear();
    }
    return failList;
  }

  protected int reSendOne(final String topic,
                          final byte[] data){
    int result = Const.ERROR;
    for(int count = 0;count<CommonEnum.RETRY_TIME;count++){
      result = kafkaMq.sendMsgBytes(topic, data);
      if(result >=0 )
        break;
    }
    return result;
  }


}
