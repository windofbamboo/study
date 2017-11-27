package com.ai.iot.bill.topo.acctTopo;

import backtype.storm.tuple.Tuple;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.dao.LogDao;
import com.ai.iot.bill.dealproc.AcctInfoDeal;
import com.ai.iot.bill.dealproc.container.AcctInfoContainer;
import com.ai.iot.bill.dealproc.container.DeviceInfoContainer;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.define.ErrEnum;
import com.ai.iot.bill.define.InfoEnum;
import com.ai.iot.bill.entity.ControlBeat;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.entity.info.AcctInfoBean;
import com.ai.iot.bill.topo.BaseBolt;
import org.slf4j.LoggerFactory;

import java.util.List;

/**账户资料获取
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class AcctBolt extends BaseBolt {

  private static final long serialVersionUID = 7464880713637500679L;

  @Override
  public void setLogger(){
    logger = LoggerFactory.getLogger(AcctBolt.class);
  }

  @Override
  public void execute(Tuple input) {

      //获取数据
      DealAcct dealAcct = parseData(input,DealAcct.class);
      if(dealAcct != null){
        //数据处理
        AcctInfoContainer acctInfoContainer = AcctInfoDeal.acctDealFirst(dealAcct.getDealId(), dealAcct.getAcctId());
        if(acctInfoContainer == null){
          //记录错误
          LogDao.updateDeallog(dealAcct.getDealId(), ErrEnum.AcctDealResult.IGNORE);
          this.collector.ack(input);
          logger.info("acctInfoContainer is null , [dealId : " + dealAcct.getDealId() + ",acctId : " + dealAcct.getAcctId() + " ]");
        }else{
          AcctInfoBean acctInfoBean=acctInfoContainer.getAcctInfoBean();
          if(InfoEnum.IsPayBack.YES.equals(acctInfoBean.getIsPayBack())){
            //第三方付费的账户，直接发送到 账单处理节点
            ControlBeat controlBeat = new ControlBeat(dealAcct.getDealId(), dealAcct.getAcctId(), acctInfoContainer.getSeqId(),ControlBeat.BOLT_TYPE_ACCT_BILL);
            sendContr(input,controlBeat);
          }else{
            commAcctDeal(input,acctInfoContainer);
          }
        }
      }else{
        logger.warn("dealAcct is null ");
        this.collector.fail(input);
      }
  }


  private void commAcctDeal(Tuple input,AcctInfoContainer acctInfoContainer){

    long dealId = acctInfoContainer.getDealId();
    long acctId = acctInfoContainer.getAcctInfoBean().getAcctId();

    //普通账户，发送到设备处理节点
    List<DeviceInfoContainer> deviceInfoContainerList = AcctInfoDeal.acctDealSecond(acctInfoContainer);
    if (deviceInfoContainerList != null) {
      //向设备处理节点发送信息
      if(!deviceInfoContainerList.isEmpty()){
        if (reSendBatch(CommonEnum.TOPIC_DEVICEINFO,deviceInfoContainerList).isEmpty()) {
          ControlBeat controlBeat = new ControlBeat(dealId,acctId, acctInfoContainer.getSeqId(),ControlBeat.BOLT_TYPE_ACCT_NONE);
          sendContr(input,controlBeat);
        } else {
          this.collector.fail(input);
          logger.warn("AcctBolt send deviceMq fail, [dealId : " + dealId + ",acctId : " + acctId + " ]");
        }
      }else{
        ControlBeat controlBeat = new ControlBeat(dealId,acctId, acctInfoContainer.getSeqId(),ControlBeat.BOLT_TYPE_ACCT_BILL);
        sendContr(input,controlBeat);
      }
    }else{
      LogDao.updateDeallog(dealId, ErrEnum.AcctDealResult.IGNORE);
      this.collector.ack(input);
      logger.warn("deviceInfoContainerList is empty, [dealId : " + dealId + ",acctId : " + acctId + " ]");
    }
  }

  /**
   * 向控制节点发送信息
   */
  private void sendContr(Tuple input,ControlBeat controlBeat){

    if (reSendOne(CommonEnum.TOPIC_CONTROL,gson.toJson(controlBeat).getBytes()) != Const.ERROR) {
      LogDao.updateDeallog(controlBeat.getDealId(), ErrEnum.AcctDealResult.GET);
      this.collector.ack(input);
    } else {
      this.collector.fail(input);
      logger.warn("AcctBolt send controlMq fail, [dealId : " + controlBeat.getDealId() + ",acctId : " + controlBeat.getAcctId() + " ]");
    }

  }


}
