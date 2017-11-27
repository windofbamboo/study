package com.ai.iot.bill.topo.fileTopo;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.common.util.UUIDUtil;
import com.ai.iot.bill.dao.AcctBill2BssDao;
import com.ai.iot.bill.define.ParamEnum;
import com.alibaba.jstorm.utils.JStormUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AcctBillFileSpout extends BaseRichSpout {

	private static final long serialVersionUID = 5574378889297692831L;
	private static final Logger logger = LoggerFactory.getLogger(AcctBillFileSpout.class);
	private SpoutOutputCollector collector;
	private static Map<String, String> cacheMap= new ConcurrentHashMap<>();
	//本账期账单文件生成结束标志
	private static final String END_FLAG = "-1";
	private int cycleId;
	//账单无数据时的等待时长，10s
	private static final int WAIT_TIME = 10 * 1000;
	//发送错误次数
	private Map<String, Integer> emitErrorMap; 
	private static final int MAX_ERROR_NUM = 3;

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.emitErrorMap = new HashMap<>();
	}

	public void nextTuple() {
		//账单文件生成启动状态
		List<Integer> cycleIdList = AcctBill2BssDao.getCycleIdByStatus(ParamEnum.CycleStatus.ADJUST_SOLIDIFY);
		if(ListUtil.isEmpty(cycleIdList)){
			JStormUtils.sleepMs(WAIT_TIME);
		} else {
			for (int cycleId : cycleIdList) {
				this.cycleId = cycleId;
				List<String> provCodeList = AcctBill2BssDao.getProvCodeByCycleId(cycleId);
				if (ListUtil.isNotEmpty(provCodeList)) {
					for (String provCode : provCodeList) {
						String messageId = UUIDUtil.getUUID();
						cacheMap.put(messageId, provCode);
						//按账户和省份发射生成账单文件的请求
						this.collector.emit(new Values(cycleId, provCode), messageId);
					}
					synchronized (cacheMap) {
						try {
							//等待该批次处理完毕
							cacheMap.wait();
							String messageId = UUIDUtil.getUUID();
							cacheMap.put(messageId, END_FLAG);
							//发送本批次完毕的结束通知，用于批量文件从临时目录移动到正式目录
							this.collector.emit(new Values(cycleId, END_FLAG), messageId);
							cacheMap.wait();
						} catch (Exception e) {
							logger.warn("Thread waiting exception , {}", e);
						}
					}
				}
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("cycleId", "provCode"));
	}

	@Override
	public void ack(Object msgId) {
		String messageId = (String) msgId;
		String provCode = cacheMap.get(messageId);
		this.completeEmit(messageId, provCode);
	}

	/**
	 * 消息失败后记录操作日志
	 */
	@Override
	public void fail(Object msgId) {
		String messageId = (String) msgId;
		String provCode = cacheMap.get(messageId);
		int errorNum = 0;
		if (emitErrorMap.get(messageId) != null) {
			errorNum = emitErrorMap.get(messageId);
		}
		if(errorNum >= MAX_ERROR_NUM){
			this.completeEmit(messageId, provCode);
			logger.warn("Make Acct file is failure. cycleId : {}, provCode : {}", cycleId, provCode);
		} else {
			this.collector.emit(new Values(cycleId, provCode), msgId);
			emitErrorMap.put(messageId, ++errorNum);
		}
	}
	
	private void completeEmit(String messageId, String provCode){
		cacheMap.remove(messageId);
		emitErrorMap.remove(messageId);
		if(cacheMap.isEmpty()){
			if(END_FLAG.equals(provCode)){
				//本账期生成账单完成后更新账期出账进度表
				boolean bl = AcctBill2BssDao.updateMonthAcctStatus(ParamEnum.CycleStatus.CYC_END, cycleId);
				if(!bl){
					logger.warn("Update acct bill status is failure, cycleId : {}", cycleId);
				}
				synchronized(cacheMap){
					cacheMap.notifyAll();
				}
				logger.info("Acct file is successful, cycleId : {}", cycleId);
			} else {
				synchronized(cacheMap){
					cacheMap.notifyAll();
				}
			}
		}
	}
}
