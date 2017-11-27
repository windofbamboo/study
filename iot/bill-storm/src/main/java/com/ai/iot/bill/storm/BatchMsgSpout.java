package com.ai.iot.bill.storm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.Const;
import com.alibaba.jstorm.utils.JStormUtils;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**   
 * spout批量基类
 * @Description:  [spout批量基类]
 * @Author:       [xulr]   
 * @CreateDate:   [2017.9.14 16:54]   
 * @Version:      [v1.0]
 */
public abstract class BatchMsgSpout implements IRichSpout{
	private static final long serialVersionUID = 5076777332772095393L;

	private static Logger logger = LoggerFactory.getLogger(BatchMsgSpout.class);
	
	//最大重试次数
	public static final int MAX_RETRY = 3;
	
	protected SpoutOutputCollector _collector;
	@SuppressWarnings("rawtypes")
	protected Map _conf;
	protected TopologyContext _ctx;
	
	//消息id序列
	protected AtomicLong idSequence = new AtomicLong(0);
	
	//是否启用ack机制
	protected boolean useAck = false;
	//启用ack时，缓存发送数据
	protected Map<Long,Object> cacheMsgs=null;
	//启用ack时，缓存发送次数
	protected Map<Long,Integer> cacheRetry=null;
	//启用ack时，缓存发送成功数据
	protected List<Object> cacheSuccess=null;
	//启用ack时，缓存发送失败数据
	protected List<Object> cacheFail=null;
	//初始化code
	private int initErrorCode = Const.OK;
	//
	private String initErrorMsg = "";
	//无数据空闲处理时是否提示信息
	protected boolean isIdleTips = true;

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		_collector = collector;
		_conf = conf;
		_ctx = context;
		
		boolean ack = (JStormUtils.parseInt(_conf.get(Config.TOPOLOGY_ACKER_EXECUTORS), 0) > 0);
		
		enabledAck(ack);
		
		init(_conf);
		
		logger.debug("useAck:{}",useAck);
	}
	
	protected void setInitErrorMsg(String errMsg){
		initErrorCode = Const.ERROR;
		initErrorMsg = errMsg;
	}
	
	protected void setInitErrorMsg(int code,String errMsg){
		initErrorCode = code;
		initErrorMsg = errMsg;
	}
	
	protected void resetInitError(){
		initErrorCode = Const.OK;
		initErrorMsg = "";
	}
	
	protected boolean getInitOK(){
		return initErrorCode==Const.OK;
	}
	
	protected int getInitErrorCode(){
		return initErrorCode;
	}
	
	protected String getInitErrorMsg(){
		return initErrorMsg;
	}
	
	/**
	*  初始化
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected abstract void init(Map conf);
	
	/**
	*  获取批量数据
	*  @return  批量数据
	*  @since  1.0
	*/
	public abstract List<Object> getBatch();	
	
	/**
	*  数据处理完毕调用
	*  @param successMsgs 处理成功的消息列表
	*  @param failMsgs 处理失败的消息列表
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	public abstract void finishBatch(List successMsgs,List failMsgs);
	
	/**
	*  是否启用ack机制
	*  @since  1.0
	*/
	protected void enabledAck(boolean enable){
		if(useAck!=enable){
			if(enable){
				cacheMsgs = new HashMap<>();
				cacheRetry = new HashMap<>();
				cacheSuccess = new ArrayList<>();
				cacheFail = new ArrayList<>();
			}else{
				cacheMsgs = null;
				cacheRetry = null;
				cacheSuccess = null;
				cacheFail = null;
			}
		}
		
		useAck = enable;
	}
	
	//清理变量
	private void nextReset(){
		if(useAck){
			cacheMsgs.clear();
			cacheRetry.clear();
			cacheSuccess.clear();
			cacheFail.clear();
		}
	}

	/**
	*  nextTuple前调用的函数
	*  @return  是否继续处理，true:继续处理nextTuple，false：后续不处理（不发送），直接跳到nextAfter
	*  @since  1.0
	*/
	protected boolean nextBefore(SpoutOutputCollector collector,List<Object> msgs){
		return true;
	}
	
	/**
	*  nextTuple后调用的函数
	*  @return  是否继续处理，true:继续处理nextTuple，false：直接跳到nextAfter
	*  @since  1.0
	*/
	protected void nextAfter(SpoutOutputCollector collector){
	}

	/**
	*  获取处理成功的数据
	*  @return  处理成功的数据列表
	*  @since  1.0
	*/
	protected List<Object> getCacheSuccess() {
		return cacheSuccess;
	}

	/**
	*  获取处理失败的数据
	*  @return  处理失败的数据列表
	*  @since  1.0
	*/
	protected List<Object> getCacheFail() {
		return cacheFail;
	}
	
	protected void processException(RuntimeException e){
		logger.error("{} RuntimeException:",this.getClass().getName(),e);
	}

	@Override
	public void nextTuple() {
		try{
			if(!getInitOK()){
				//重新调用初始化
				init(_conf);
			}
			
			if(getInitOK()){
				nextReset();
		
				List<Object> msgs = getBatch();		
		
				if (msgs != null && !msgs.isEmpty()) {
					if (!nextBefore(_collector, msgs)) {
						// 数据在nextBefore自己处理了，后续不需要处理了
						return;
					}
					
					for (Object msg : msgs) {
						emitMsg(msg, -1);
					}
					if (useAck) {
						synchronized (cacheMsgs) {
							// 有数据的情况下等待所有消息处理结束
							while (!cacheMsgs.isEmpty()) {
								try {
									cacheMsgs.wait();
								} catch (InterruptedException e) {
									logger.debug("InterruptedException:", e);
									Thread.currentThread().interrupt();
								}
							}
						}
						// 完成
						finishBatch(cacheSuccess, cacheFail);
					}					
					nextAfter(_collector);
				}else{
					if(isIdleTips)
						logger.info("nextTuple getBatch() return no data.");
				}
			}else{
				logger.error(getInitErrorMsg());
				JStormUtils.sleepMs(600000);
			}
		}catch(RuntimeException e){
			processException(e);
		}
	}
		
	protected boolean emitMsg(Object msg,long msgId){
		if(useAck){
			if(msgId<=0){
				//第一次发送
				msgId = this.idSequence.incrementAndGet();
				cacheMsgs.put(msgId,msg);
				customEmit(_collector,msg, msgId);
			}else{
				//重发
				int retry;
				if(cacheRetry.containsKey(msgId)){
					retry = cacheRetry.get(msgId);
				}else{
					retry = 0;
				}
				
				if(retry < MAX_RETRY){
					//重发次数+1	
					cacheRetry.put(msgId, retry+1);
					customEmit(_collector,msg, msgId);
				}else{
					//超过重发次数限制
					return false;
				}
			}			
		}else{
			customEmit(_collector,msg);
		}
		
		return true;
	}
	
	/**
	*  发送需要ack的消息
	*  @since  1.0
	*/
	protected void customEmit(SpoutOutputCollector collector,Object msg,long msgId){
		collector.emit(generateValues(msg), msgId);
	}
	
	/**
	*  发送不需要ack的消息
	*  @since  1.0
	*/
	protected void customEmit(SpoutOutputCollector collector,Object msg){
		collector.emit(generateValues(msg));
	}
	
	/**
	*  组织发送的数据，需要和declareOutputFields定义的格式一致
	*  @return  要发送的数据列表
	*  @since  1.0
	*/
	protected List<Object> generateValues(Object msg){
		return (new Values(msg));
	}
	
	protected void notifyMsg(){
		synchronized (cacheMsgs) {
	        if (cacheMsgs.isEmpty()) {
	        	cacheMsgs.notifyAll();
	        }
	    }
	}

	@Override
	public void ack(Object msgId) {
		cacheSuccess.add(cacheMsgs.get(msgId));
		cacheMsgs.remove(msgId);
				
		notifyMsg();
	}

	@Override
	public void fail(Object msgId) {
		Object msg = cacheMsgs.get(msgId);
		if(!emitMsg(msg,(long)msgId)){
			cacheFail.add(msg);
			cacheMsgs.remove(msgId);

			notifyMsg();
		}
	}

	protected boolean getIdleTips() {
		return isIdleTips;
	}

	protected void setIdleTips(boolean idleTips) {
		this.isIdleTips = idleTips;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		declarer.declare(new Fields("msg"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public void close() {	
	}

	@Override
	public void activate() {	
	}

	@Override
	public void deactivate() {	
	}
}
