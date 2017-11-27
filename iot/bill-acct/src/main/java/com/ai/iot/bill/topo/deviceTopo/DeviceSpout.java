package com.ai.iot.bill.topo.deviceTopo;

import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.topo.BaseSpout;
import org.slf4j.LoggerFactory;

/**账户级账单处理
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class DeviceSpout extends BaseSpout {

  private static final long serialVersionUID = -2596696823899560368L;

  @Override
  protected void setTopic(){
    kafkaMq.setTopic(CommonEnum.TOPIC_DEVICEINFO);
  }

  @Override
  protected void setLogger(){
    logger = LoggerFactory.getLogger(DeviceSpout.class);
  }


}