package com.ai.iot.bill.storm;

import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.mq.ZkMgr;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.ZkLock;
import com.alibaba.jstorm.utils.JStormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 接收kafka各分区数据，使用分布式锁，每个spout处理一个分区数据
 *  
 * @Description: [接收kafka各分区数据，每个spout处理一个分区数据]
 * @Author: [xulr]
 * @CreateDate: [2017.9.14 16:54]
 * @Version: [v1.0]
 */
public abstract class KafkaMsgSpout extends BatchMsgSpout {
	private static final long serialVersionUID = 1232913498L;
	
	private static Logger logger = LoggerFactory.getLogger(KafkaMsgSpout.class);
	
	//错误代码
	protected static final int ERROR_CODE_KAFKA = 3;
	protected static final int ERROR_CODE_KAFKA_PARALLEL = 4;
	protected static final int ERROR_CODE_KAFKA_LOCK = 5;
	
	//spout并发度配置
	public static final String SPOUT_PARALLEL = "spout.parallel";
	//kafka超时设置配置
	public static final String KAFKA_TIME_OUT = "kafka.timeout";
	//默认超时设置(ms)
	public static final int TIME_OUT = 1800000;
	
	//kafka队列
	protected KafkaMq kafka = null;
	//kafka队列超时设置(ms)
	protected int kafkaTimeOut;
	//分区锁
	protected ZkLock zkLock = null;

	@SuppressWarnings("rawtypes")
	@Override
	public void init(Map conf) {
		// 连接kafka
		if(getInitOK() || getInitErrorCode()==ERROR_CODE_KAFKA){
			kafka = KafkaMgr.getKafka(kafkaId(), Const.READ_ONLY);
			if(kafka == null){
				setInitErrorMsg(ERROR_CODE_KAFKA,"get kafka("+kafkaId()+") error.");
	        }else{
	        	resetInitError();
	        }
		}
		
		if(getInitOK()){
			String topicName = topicName();
			
			// 获取并发数(kafka分区数)	
			int parallel = spoutParallel(conf);
			if(parallel>0){		
				// 获取分区锁
				if(getInitOK() || getInitErrorCode()==ERROR_CODE_KAFKA_LOCK){
					zkLock = ZkMgr.getPartitionLock(zookeeperId(), topicName, parallel);
					if(zkLock!=null){
						int partition = zkLock.getPartition();
					
						resetInitError();
						kafka.setPatition(topicName, partition);
						logger.info("lock partition {}{}",topicName,partition);						
					}else{
						setInitErrorMsg(ERROR_CODE_KAFKA_LOCK,"zkLock.getPartitionLock "+topicName+" return null");
					}
				}
			}else{
				setInitErrorMsg("get spout parallel fail,kafka topic:"+topicName);
			}
		}
		
		if(getInitOK()){
			kafkaTimeOut = JStormUtils.parseInt(conf.get(KAFKA_TIME_OUT),TIME_OUT);
			
			customInit(conf);
		}
	}
	
	/**
	*  指定spout的并发度，默认conf.get(SPOUT_PARALLEL)
	*  @return spout的并发度
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected int spoutParallel(Map conf){
		return JStormUtils.parseInt(conf.get(SPOUT_PARALLEL),1);
	}
	
	/**
	*  自定义初始化
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected void customInit(Map conf){
		
	}
	/**
	*  返回kafka连接id
	*  @since  1.0
	*/
	protected abstract int kafkaId();
	/**
	*  返回topic名称
	*  @since  1.0
	*/
	protected abstract String topicName();
	/**
	*  返回zk连接id
	*  @since  1.0
	*/
	protected abstract int zookeeperId();
	/**
	*  从kafka取数据，默认recvMsgs(TIME_OUT)
	*  @param kafka kafka队列
	*  @return kafka数据List
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected List kafkaMsgs(KafkaMq kafka){
		return kafka.recvMsgs(kafkaTimeOut);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> getBatch() {
		// 循环直到取到数据
		List objs = kafkaMsgs(kafka);

		if (objs != null && !objs.isEmpty()) {
			logger.debug("===> get msgs:{}", objs);
			return objs;
		}else{
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void finishBatch(List successMsgs, List failMsgs) {
		// 处理完成
		kafka.commit();
	}
}
