package com.ai.iot.bill.topo.deviceTopo;

import backtype.storm.tuple.Tuple;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.dealproc.DeviceDeal;
import com.ai.iot.bill.dealproc.container.DeviceInfoContainer;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.ControlBeat;
import com.ai.iot.bill.topo.BaseBolt;
import org.slf4j.LoggerFactory;

/**账户级账单处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceBolt extends BaseBolt {

  private static final long serialVersionUID = -841418115307666786L;

  @Override
  public void setLogger(){
    logger = LoggerFactory.getLogger(DeviceBolt.class);
  }

  @Override
  public void execute(Tuple input) {

    DeviceInfoContainer deviceInfoContainer =parseData(input,DeviceInfoContainer.class);
    if(deviceInfoContainer != null){
      long dealId = deviceInfoContainer.getDealId();
      long acctId = deviceInfoContainer.getDeviceBean().getAcctId();
      long deviceId = deviceInfoContainer.getDeviceBean().getDeviceId();
      long seqId = deviceInfoContainer.getSeqId();
      //处理
      try {
        DeviceDeal.deal(deviceInfoContainer);
        //向控制节点发送信息
        ControlBeat controlBeat = new ControlBeat(dealId, acctId, seqId, ControlBeat.BOLT_TYPE_DEVICE_BILL);
        if(reSendOne(CommonEnum.TOPIC_CONTROL,gson.toJson(controlBeat).getBytes())== Const.ERROR){
            this.collector.fail(input);
            logger.info("DeviceBolt send controlMq fail, [dealId : "+dealId+",acctId : "+acctId+",deviceId : "+deviceId+" ]");
        }else{
            this.collector.ack(input);
        }
      }catch (Exception e){
        this.collector.fail(input);
        logger.info("DeviceBolt execute fail : {}", e.getMessage());
      }
    }else{
      this.collector.fail(input);
    }

  }



}
