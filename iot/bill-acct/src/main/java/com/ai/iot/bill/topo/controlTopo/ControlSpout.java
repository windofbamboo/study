package com.ai.iot.bill.topo.controlTopo;

import backtype.storm.tuple.Values;
import com.ai.iot.bill.common.util.UUIDUtil;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.ControlBeat;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.topo.BaseSpout;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**控制节点
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class ControlSpout extends BaseSpout {

  private static final long serialVersionUID = -7225985998407713413L;

  @Override
  protected void setTopic(){
    kafkaMq.setTopic(CommonEnum.TOPIC_CONTROL);
  }

  @Override
  protected void setLogger(){
    logger = LoggerFactory.getLogger(ControlSpout.class);
  }

  @Override
  public void dealData(List<byte[]> dataList){
    List<ControlBeat> controlBeatList = new ArrayList<>();
    for (byte[] data : dataList) {
      ControlBeat controlBeat = gson.fromJson(new String(data), ControlBeat.class);
      controlBeatList.add(controlBeat);
    }
    List<DealAcct> dealAcctList =getDealAcctList(controlBeatList);
    //发送数据到 bolt 端
    for(DealAcct dealAcct:dealAcctList){
      byte[] data = gson.toJson(dealAcct).getBytes();
      String msgId = UUIDUtil.getUUID();
      dataMap.put(msgId,data);
      this.collector.emit(new Values(data),msgId);
    }
  }

  private List<DealAcct> getDealAcctList(List<ControlBeat> controlBeatList){

    if(controlBeatList == null || controlBeatList.isEmpty()){
      return Collections.emptyList();
    }
    controlBeatList.sort((o1, o2) -> {
      if (o1.getDealId() < o2.getDealId()) return -1;
      if (o1.getDealId() > o2.getDealId()) return 1;

      if (o1.getAcctId() < o2.getAcctId()) return -1;
      if (o1.getAcctId() > o2.getAcctId()) return 1;
      return 0;
    });

    List<DealAcct> dealAcctList = new ArrayList<>();
    DealAcct dealAcct = null;
    for(ControlBeat controlBeat:controlBeatList){
      if (controlBeat.getBoltType() == 'A') {
        continue;
      }
      if(dealAcct == null){
        dealAcct = new DealAcct(controlBeat.getDealId(),controlBeat.getAcctId(),controlBeat.getSeqId());
        dealAcctList.add(dealAcct);
      }else{
        if(dealAcct.getDealId() != controlBeat.getDealId() ||
           dealAcct.getAcctId() != controlBeat.getAcctId()){

          dealAcct = new DealAcct(controlBeat.getDealId(),controlBeat.getAcctId(),controlBeat.getSeqId());
          dealAcctList.add(dealAcct);
        }
      }
    }
    return dealAcctList;
  }


}
