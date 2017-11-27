package com.ai.iot.bill.storm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.Const;
import com.alibaba.jstorm.utils.JStormUtils;

import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public abstract class BatchMsgBolt implements IRichBolt{
	private static final long serialVersionUID = 5616885272277183057L;
	private static Logger logger = LoggerFactory.getLogger(BatchMsgBolt.class);
	
	protected OutputCollector _collector;
	@SuppressWarnings("rawtypes")
	protected Map _conf;
	protected TopologyContext _ctx;
	
	//是否启用ack机制
	protected boolean useAck = false;
	//初始化code
	private int initErrorCode = Const.OK;
	//初始化错误消息
	private String initErrorMsg="";

	@Override
	public void prepare(@SuppressWarnings("rawtypes")Map conf, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		_conf = conf;
		_ctx = context;
		
		useAck = (JStormUtils.parseInt(_conf.get(Config.TOPOLOGY_ACKER_EXECUTORS), 0) > 0);
		
		init(_conf);
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
	
	@SuppressWarnings("rawtypes")
	protected Map getConf(){
		return _conf;
	}
	
	protected OutputCollector getCollector(){
		return _collector;
	}
	
	/**
	*  初始化
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected abstract void init(Map conf);
	
	/**
	*  处理消息数据
	*  @return ack模式下，ack还是fail，true：ack，false：fail
	*  @since  1.0
	*/
	protected abstract boolean processMsg(OutputCollector collector,Tuple input);
	
	protected boolean processException(Tuple input,RuntimeException e){
		logger.error("{} RuntimeException:",this.getClass().getName(),e);
		return false;
	}
	
	@Override
	public void execute(Tuple input) {
		boolean isOK = false;
		try{
			if(!getInitOK()){
				//重新调用初始化
				init(_conf);
			}
			if(getInitOK()){
				isOK = processMsg(_collector,input);
			}else{
				//初始化失败
				logger.error(getInitErrorMsg());
				try {
		            Thread.sleep(600000);
		        } catch (InterruptedException ignored) {
		        	logger.debug("InterruptedException:",ignored);
		        	Thread.currentThread().interrupt();
		        }
			}
		}catch(RuntimeException e){
			isOK = processException(input,e);
		}
		if(useAck){
			if(isOK){
				_collector.ack(input);
			}else{
				_collector.fail(input);
			}
		}
	}
	
	/**
	*  是否启用ack机制
	*  @since  1.0
	*/
	protected void enabledAck(boolean enable){
		useAck = enable;
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("msg"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}
