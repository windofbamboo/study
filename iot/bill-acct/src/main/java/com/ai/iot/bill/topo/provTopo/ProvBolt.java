package com.ai.iot.bill.topo.provTopo;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.dao.AcctValidDao;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.DealAcct;
import com.ai.iot.bill.entity.info.OutAcctTaskBean;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 按省份处理账户，将有效账户信息放入出账账户队列
 * 
 * Created by zhaojiajun on 2017/8/13.
 */
public class ProvBolt implements IRichBolt {

	private static final long serialVersionUID = 7190298670408876265L;
	private static final Logger logger = LoggerFactory.getLogger(ProvBolt.class);
	private KafkaMq kafkaMq;
	private OutputCollector collector;
	private static final int RETRY_TIME = 3;
	private Gson gson;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		this.kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.WRITE_ONLY);
		this.gson = new Gson();
	}

	public void execute(Tuple input) {
		//数据处理
		OutAcctTaskBean acctTaskBean = (OutAcctTaskBean) input.getValueByField("OutAcctTaskBean");
		if (acctTaskBean == null) {
			logger.warn("Bolt gets data is null, error");
			collector.ack(input);
			return;
		}
		String provCode = acctTaskBean.getProvinceCode();
		long dealId = acctTaskBean.getDealId();
		List<String> acctIdList = acctTaskBean.getProvAcctList();
		
		//acctIdList为空是按省份出账，否则是按账户出账
		if(ListUtil.isEmpty(acctIdList)){
			//如果是省份出账则获取省份下所有账户后入kafka
			acctIdList = AcctValidDao.getAcctListByProvCode(provCode);
		}
		
		if(ListUtil.isNotEmpty(acctIdList)){
			if(this.reSendMsg(dealId, acctIdList, 1).isEmpty()){
				collector.ack(input);
				logger.info("Out account successed, province : {}, account size : {}", provCode, acctIdList.size());
			}else{
				//如果入kafka尝试三次之后还是失败则通知spout该批次重发
				collector.fail(input);
				logger.warn("Out account failed after trying {} times, province : {}", RETRY_TIME, provCode);
			}
		} else {
			//省份下未获取到账户列表
			logger.info("No account in the province, province : {}", provCode);
			collector.ack(input);
		}
	}
	
	private List<String> reSendMsg(long dealId, List<String> acctList, int count) {
		//批量提交后查看返回结果
		kafkaMq.resetBatch();
		for(String acctId : acctList){
			DealAcct dealAcct = new DealAcct(dealId, Long.valueOf(acctId),0);
			kafkaMq.addMsgBytes(CommonEnum.TOPIC_ACCTID, gson.toJson(dealAcct).getBytes());
		}
		List<Integer> rets=kafkaMq.sendBatch();
		List<String> failList = new ArrayList<>();
		if(rets!=null && !rets.isEmpty()){
			kafkaMq.resetBatch();
			for(int k=0; k<rets.size(); k++){
				int result = rets.get(k);
				if(result==-1){
					failList.add(acctList.get(k));
				}
			}
			acctList.clear();
		}
		//如果结果中失败列表不为空则进行重复发送N次
		if (!failList.isEmpty() && count < RETRY_TIME) {
			count++;
			reSendMsg(dealId, failList, count);
		}
		return failList;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//do nothing
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
}
