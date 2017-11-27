package com.ai.iot.bill.topo.acctTopo;

import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.topo.BaseSpout;
import org.slf4j.LoggerFactory;

import java.util.List;

/**账户列表获取
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctSpout extends BaseSpout {

  private static final long serialVersionUID = -8454199195229076277L;

  @Override
  protected void setTopic(){
    kafkaMq.setTopic(CommonEnum.TOPIC_ACCTID);
  }

  @Override
  protected void setLogger(){
    logger = LoggerFactory.getLogger(AcctSpout.class);
  }


}
