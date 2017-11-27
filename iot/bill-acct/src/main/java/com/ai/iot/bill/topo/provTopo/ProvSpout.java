package com.ai.iot.bill.topo.provTopo;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.JSONUtil;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.common.util.StringUtil;
import com.ai.iot.bill.common.util.UUIDUtil;
import com.ai.iot.bill.dao.AcctValidDao;
import com.ai.iot.bill.dealproc.SeqMgr;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.entity.info.OutAcctTaskBean;
import com.ai.iot.bill.entity.log.DealLog;
import com.alibaba.jstorm.utils.JStormUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从crm中获取一批出账任务，过滤重复账户后进行发射
 * 
 * Created by zhaojiajun on 2017/8/13.
 */
public class ProvSpout extends BaseRichSpout {

	private static final long serialVersionUID = -3923309508036464415L;
	private static final Logger logger = LoggerFactory.getLogger(ProvSpout.class);
	private KafkaMq kafkaMq;
	private SpoutOutputCollector collector;
	//缓存发送数据，一旦失败后可重发
	private Map<String, OutAcctTaskBean> cacheMap;
	private Map<String, String> acctIdMap;
	private Map<String, List<String>> provAcctMap;
	private Set<String> provCodeSet;
	//同步锁，用于不同线程间使用kafka consumer对象
	private static final Object obj = new Object();
	//判断一批请求是否结束，是指从kafka中获取连续数据结束
	private boolean isEnd = false;
	//kafka队列中无数据时的等待时长，10s
	private static final int WAIT_TIME = 10 * 1000;
	//发送错误次数
	private Map<String, Integer> emitErrorMap; 
	private final static int MAX_ERROR_NUM = 3;
	private final static int BATCH_SIZE = 100;
	//对上一批次的监控
	private boolean monitor = false; 
	private long dealId = 0;

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.cacheMap = new ConcurrentHashMap<>();
		this.acctIdMap = new HashMap<>();
		this.provAcctMap = new HashMap<>();
		this.provCodeSet = new HashSet<>();
		this.emitErrorMap = new HashMap<>(); 
		Map<String, String> props = new HashMap<>();
		props.put("group.id", "iot");
		props.put("enable.auto.commit", "false");
		props.put("max.poll.records", "1000");
		props.put("auto.offset.reset", "earliest");
		kafkaMq = KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_ACCT, Const.READ_ONLY, props);
		if (kafkaMq != null) {
			kafkaMq.setTopic(CommonEnum.TOPIC_ACCT_TASK);
		}
	}

	public void nextTuple() {
		if(monitor){
			//获取批次表，判断是否可以进入下一批次
			DealLog dealLog = AcctValidDao.getDealLogById(dealId);
			if(dealLog!=null 
					&& (dealLog.getTotalNum() != dealLog.getSucessNum() + dealLog.getFailNum() + dealLog.getIgnoreNum()
					|| dealLog.getTotalNum() != dealLog.getDealNum())){
				logger.info("Current batch processing, dealId:{}", dealId);
				JStormUtils.sleepMs(5000);
				return;
			}
			//如果前一批次已经处理完毕，则关闭监控
			this.monitor = false;
		} 
		
		List<String> acctTaskList ;
		synchronized(obj){
			//从kafka中获取出账任务列表
			acctTaskList = kafkaMq.recvMsgs(WAIT_TIME);
		}
		if (ListUtil.isNotEmpty(acctTaskList)) {
			for(String acctTask : acctTaskList){
				//将从crm获取的出账任务json串转换为对象类型， 格式：{'acctId':'123456', 'provinceCode':'101'}
				//全量出账  格式：{'acctId':'-1', 'provinceCode':'-1'}
				OutAcctTaskBean acctTaskBean = JSONUtil.toBean(acctTask, OutAcctTaskBean.class);
				if (acctTaskBean != null) {
					if("-1".equals(acctTaskBean.getAcctId()) && "-1".equals(acctTaskBean.getProvinceCode())){
						//全量出账
						provCodeSet.addAll(AcctValidDao.getAllProvList());
					} else if (StringUtil.isEmpty(acctTaskBean.getAcctId())) {
						provCodeSet.add(acctTaskBean.getProvinceCode());
					} else {
						acctIdMap.put(acctTaskBean.getAcctId(), null);
					}
					isEnd = true;
				}
			}
		} else {
			if(isEnd){
				if(acctIdMap!=null && !acctIdMap.isEmpty()){
					//通过出账账户列表获取本次出账的省份列表
					List<String> list = new ArrayList<>(acctIdMap.keySet());
					int maxSize = list.size();
					int batchNum = maxSize/BATCH_SIZE + 1;
					for(int m=0; m<batchNum; m++){
						int batchEnd = (m+1)*BATCH_SIZE < maxSize ? (m+1)*BATCH_SIZE : maxSize;
						List<String> subList = list.subList(m*BATCH_SIZE, batchEnd);
						
						List<OutAcctTaskBean> provCodeList = AcctValidDao.getProvCodeByAcctId(subList);
						if(ListUtil.isNotEmpty(provCodeList)){
							for(OutAcctTaskBean bean : provCodeList){
								String provCode = bean.getProvinceCode();
								//如果本次出账省份中包含某个账户，则该账户过滤掉，避免重复出账
								if(!provCodeSet.contains(provCode)){
									List<String> provAcctList = provAcctMap.get(provCode);
									if(provAcctList == null){
										provAcctList = new ArrayList<>();
									}
									if(StringUtil.isNotEmpty(bean.getAcctId())){
										provAcctList.add(bean.getAcctId());
										provAcctMap.put(provCode, provAcctList);
									}
								}
							}
						}
					}
					acctIdMap.clear();
				}
				
				long dealId = SeqMgr.getSeqId("DEAL_ID");
				
				int totalNum = 0;
				if(provCodeSet!=null && !provCodeSet.isEmpty()){
					//获取省份下账户数目
					int num = AcctValidDao.getAcctTotalByProvCode(provCodeSet);
					totalNum =+ num;
					for(String provCode : provCodeSet){
						String msgId = UUIDUtil.getUUID();
						OutAcctTaskBean bean = new OutAcctTaskBean(dealId, provCode);
						bean.setTotalNum(totalNum);
						this.cacheMap.put(msgId, bean);
						//本次出账中按省份出账的任务进行发射
						this.collector.emit(new Values(bean), msgId);
						logger.info("emit success, {}", bean.toString());
					}
					provCodeSet.clear();
				}
				
				if(provAcctMap!=null && !provAcctMap.isEmpty()){
					for(Map.Entry<String, List<String>> acctMap : provAcctMap.entrySet()){
						totalNum =+ acctMap.getValue().size();
					}
					
					for(Map.Entry<String, List<String>> acctMap : provAcctMap.entrySet()){
						String msgId = UUIDUtil.getUUID();
						OutAcctTaskBean bean = new OutAcctTaskBean(dealId, acctMap.getKey(), acctMap.getValue());
						bean.setTotalNum(totalNum);
						this.cacheMap.put(msgId, bean);
						//本次出账中按账户出账的任务进行发射
						this.collector.emit(new Values(bean), msgId);
						logger.info("emit success, {}", bean.toString());
					}
					provAcctMap.clear();
				}
				this.dealId = dealId;
				isEnd = false;
				try {
					//本次出账任务发射完成后等待后续bolt处理
					synchronized(cacheMap){
						this.cacheMap.wait();
					}
				} catch (Exception e) {
					logger.warn("Thread waiting exception , {}", e);
				}
			} else {
				JStormUtils.sleepMs(WAIT_TIME);
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("OutAcctTaskBean"));
	}

	/**
	 * 消息处理成功后提交本次记录，并启动下一批次
	 * 
	 * @param msgId 消息ID
	 */
	@Override
	public void ack(Object msgId) {
		String messageId = (String) msgId;
		OutAcctTaskBean bean = cacheMap.get(messageId);
		this.completeEmit(messageId, bean.getDealId(), bean.getTotalNum());
		logger.info("Out account task is successful, {}", bean.toString());
	}

	/**
	 * 消息处理失败后进行重发，并记录操作日志
	 * 
	 * @param msgId 消息ID
	 */
	@Override
	public void fail(Object msgId) {
		String messageId = (String) msgId;
		OutAcctTaskBean bean = cacheMap.get(messageId);
		int errorNum = 0;
		if (emitErrorMap.get(messageId) != null) {
			errorNum = emitErrorMap.get(messageId);
		}
		if(errorNum >= MAX_ERROR_NUM){
			this.completeEmit(messageId, bean.getDealId(), bean.getTotalNum());
			logger.warn("Out account task is failure, {}", bean.toString());
		} else {
			this.collector.emit(new Values(bean), messageId);
			emitErrorMap.put(messageId, ++errorNum);
		}
	}
	
	private void completeEmit(String messageId, long dealId, int totalNum){
		cacheMap.remove(messageId);
		emitErrorMap.remove(messageId);
		if(cacheMap.isEmpty()){
			synchronized(obj){
				kafkaMq.commit();
			}
			synchronized(cacheMap){
				//初始化出账进度表信息
				if(!AcctValidDao.insertDealLog(dealId, totalNum)){
					logger.error("Failed to generate account log");
				} else {
					//启动对上一批次的监控
					monitor = true;
				}
				this.cacheMap.notifyAll();
			}
		}
	}
}
