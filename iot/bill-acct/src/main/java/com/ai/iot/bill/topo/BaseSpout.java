package com.ai.iot.bill.topo;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.UUIDUtil;
import com.ai.iot.bill.define.CommonEnum;
import com.alibaba.jstorm.utils.JStormUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public abstract class BaseSpout extends BaseRichSpout {

  private static final long serialVersionUID = 5549017361377682057L;
  protected Logger logger = null;
  protected KafkaMq kafkaMq = null;
  protected Gson gson = null;
  protected SpoutOutputCollector collector;

  protected Map<String,byte[]> dataMap = new ConcurrentHashMap<>();   // 记录发送的数据
  protected Map<String,Integer> timeMap = new ConcurrentHashMap<>();  // 记录发送的次数
  protected AtomicBoolean dealState = new AtomicBoolean(false);

  @Override
  public void open(Map conf, TopologyContext context,
                   SpoutOutputCollector collector) {
    this.collector = collector;
    this.gson = new Gson();
    setLogger();
    try{
      Map<String, String> props = new HashMap<>();
      props.put("group.id", "iot");
      props.put("enable.auto.commit", "false");
      props.put("max.poll.records", "10");
      props.put("auto.offset.reset", "earliest");
      kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_ONLY,props);
      setTopic();
    }catch (Exception e){
      logger.info("KafkaMq init fail : {}", e.getMessage());
    }
  }

  protected void setTopic(){
  }
  protected void setLogger(){
  }

  @Override
  public void nextTuple() {

    if(!dataMap.isEmpty()){
      JStormUtils.sleepMs(CommonEnum.SLEEP_TIME);
    }else{
      if(dealState.compareAndSet(true,false)){
        kafkaMq.commit();
      }
      //从kafka中获取数据
      List<byte[]> dataList = getMqData();
      if (dataList == null || dataList.isEmpty()) {
        JStormUtils.sleepMs(CommonEnum.SLEEP_TIME);
      } else {
        dealData(dataList);
        dealState.compareAndSet(false,true);
      }
    }
  }

  protected void dealData(final List<byte[]> dataList){
    for (byte[] data : dataList) {
      String msgId = UUIDUtil.getUUID();
      dataMap.put(msgId, data);
      timeMap.put(msgId,1);
      this.collector.emit(new Values(data), msgId);
    }
  }

  protected List<byte[]> getMqData(){
    List<byte[]> dataList = null;
    try {
      dataList = kafkaMq.recvMsgBytes(CommonEnum.TIMEOUT);
    }catch (Exception e){
      logger.info("get KafkaMq data fail : " + e.getMessage());
    }
    return dataList;
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("log"));
  }

  @Override
  public void ack(final Object msgId) {
    String id = (String)msgId;
    byte[] data = dataMap.get(id);
    sucessLog(data);
    dataMap.remove(id);
    timeMap.remove(id);
  }

  /**
   * 消息处理失败后需要自己处理
   */
  @Override
  public void fail(final Object msgId) {
    String id = (String)msgId;
    boolean send = false;
    if(timeMap.containsKey(id)){
      int times = timeMap.get(id);
      send = (times <= CommonEnum.RETRY_TIME);
      timeMap.put(id,times+1);
    }

    if(send){
      if(dataMap.containsKey(id)){
        byte[] data =dataMap.get(id);
        this.collector.emit(new Values(data),id);
        logger.warn("bolt is failure, emit again.");
      }
    }else {
      byte[] data = dataMap.get(id);
      faillog(data);
      dataMap.remove(id);
      timeMap.remove(id);
    }

  }

  public void sucessLog(byte[] data){

  }

  public void faillog(byte[] data){

  }


}
