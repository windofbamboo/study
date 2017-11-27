package com.ai.iot.bill.common.mq;

import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.param.ConnHostBean;
import com.ai.iot.bill.common.param.ConnPropertyBean;
import com.ai.iot.bill.common.util.Const;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * mq类 Created by geyunfeng on 2017/7/10.
 */
public class KafkaMq {

	private static final Logger logger = LoggerFactory.getLogger(KafkaMq.class);
	// /序列化工具
	private FSTConfiguration serializeTool = FSTConfiguration.createDefaultConfiguration();
	private FSTConfiguration serializeJsonTool = FSTConfiguration.createJsonConfiguration(false, false);
	// /序列化类型:false=FST;true=Json
	private boolean isJsonSerializeType = true;

	private boolean autoCommit = false;
	private int mode = Const.READ_AND_WRITE;
	private KafkaConsumer<String, byte[]> consumer = null;
	private KafkaProducer<String, byte[]> producer = null;
	private int batchSize = 1;
	private List<ProducerRecord<String, byte[]>> records = null;
	private String passWd = null;

	public KafkaMq(String passWd) {
		this.passWd = passWd;
		serializeTool.asByteArray("tt");
		serializeJsonTool.asByteArray("tt");// 第一次序列化,防止后续序列化格式不同,影响redis的结果
	}

	public void disconnect() {
		if (consumer != null) {
			consumer.close(5L, TimeUnit.SECONDS);
		}
		if (producer != null) {
			producer.close();
		}
	}

	// 批量发送
	public List<Integer> sendBatch() {
		if (producer != null && !records.isEmpty()) {
			List<Integer> rets = new ArrayList<>();
			List<Future<RecordMetadata>> rms=new ArrayList<>();

			for (ProducerRecord<String, byte[]> record : records) {
				Future<RecordMetadata> rm = producer.send(record);
				rms.add(rm);
			}
			producer.flush();
			
			for(Future<RecordMetadata> rm:rms){
				try {
					rets.add(rm.get().partition());
				} catch (InterruptedException e) {
					logger.debug("kafka send InterruptedException:{}", e);
					Thread.currentThread().interrupt();//重新设置中断标示
				} catch (ExecutionException e) {
					logger.debug("kafka send ExecutionException:{}", e);
					rets.add(-1);
				}
			}
			
			records.clear();
			return rets;
		}
		return Collections.emptyList();
	}

	public void resetBatch() {
		if (records == null)
			records = new ArrayList<>();
		else
			records.clear();
	}

	public List<ProducerRecord<String, byte[]>> getBatch() {
		return records;
	}

	public int addMsg(String topic, String msg) {
		records.add(new ProducerRecord<String, byte[]>(topic, msg.getBytes(Const.UTF8)));
		return records.size();
	}

	public int addMsgObject(String topic, Object msg) {
		if (isJsonSerializeType) {
			records.add(new ProducerRecord<String, byte[]>(topic, serializeJsonTool.asByteArray(msg)));
		} else {
			records.add(new ProducerRecord<String, byte[]>(topic, serializeTool.asByteArray(msg)));
		}
		return records.size();
	}

	public int addMsgBytes(String topic, byte[] msg) {
		records.add(new ProducerRecord<String, byte[]>(topic, msg));
		return records.size();
	}

	public int addMsg(String topic, String key, String msg) {
		records.add(new ProducerRecord<String, byte[]>(topic, key, msg.getBytes(Const.UTF8)));
		return records.size();
	}

	public int addMsgBytes(String topic, String key, byte[] msg) {
		records.add(new ProducerRecord<String, byte[]>(topic, key, msg));
		return records.size();
	}

	public int addMsgObject(String topic, String key, Object msg) {
		if (isJsonSerializeType) {
			records.add(new ProducerRecord<String, byte[]>(topic, key, serializeJsonTool.asByteArray(msg)));
		} else {
			records.add(new ProducerRecord<String, byte[]>(topic, key, serializeTool.asByteArray(msg)));
		}
		return records.size();
	}

	private int send(ProducerRecord<String, byte[]> record) {
		if (producer != null) {
			Future<RecordMetadata> rm = producer.send(record);
			producer.flush();
			try {
				return rm.get().partition();
			} catch (InterruptedException e) {
				logger.debug("kafka send InterruptedException:{}", e);
				Thread.currentThread().interrupt();//重新设置中断标示
			} catch (ExecutionException e) {
				logger.debug("kafka send ExecutionException:{}", e);
			}
		}

		return Const.ERROR;
	}

	public int sendMsg(String topic, String msg) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, msg.getBytes(Const.UTF8));
		return send(record);
	}

	public int sendMsgObject(String topic, Object msg) {
		ProducerRecord<String, byte[]> record;
		if (isJsonSerializeType) {
			record = new ProducerRecord<String, byte[]>(topic, serializeJsonTool.asByteArray(msg));
		} else {
			record = new ProducerRecord<String, byte[]>(topic, serializeTool.asByteArray(msg));
		}
		return send(record);
	}

	public int sendMsgBytes(String topic, byte[] msg) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, msg);
		return send(record);
	}

	public int sendMsg(String topic, String key, String msg) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, key, msg.getBytes(Const.UTF8));
		return send(record);
	}

	public int sendMsgBytes(String topic, String key, byte[] msg) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, key, msg);
		return send(record);
	}

	public int sendMsgObject(String topic, String key, Object msg) {
		ProducerRecord<String, byte[]> record;
		if (isJsonSerializeType) {
			record = new ProducerRecord<String, byte[]>(topic, key, serializeJsonTool.asByteArray(msg));
		} else {
			record = new ProducerRecord<String, byte[]>(topic, key, serializeTool.asByteArray(msg));
		}
		return send(record);
	}

	public ConsumerRecords<String, byte[]> recvRecords(int timeout) {
		ConsumerRecords<String, byte[]> records = consumer.poll(timeout);
		if (autoCommit) {
			consumer.commitSync();
		}
		return records;		
	}

	public List<String> recvMsgs(int timeout) {
		ConsumerRecords<String, byte[]> records = recvRecords(timeout);

		if (records.count() <= 0)
			return Collections.emptyList();
		else {
			List<String> retArr = new ArrayList<>();
			
			Iterator<ConsumerRecord<String, byte[]>> iter = records.iterator();
			while (iter.hasNext()) {
				ConsumerRecord<String, byte[]> rec = iter.next();
				retArr.add(new String(rec.value(), Const.UTF8));
			}
			return retArr;
		}
	}

	public List<Object> recvMsgObjects(int timeout) {
		ConsumerRecords<String, byte[]> records = recvRecords(timeout);

		if (records.count() <= 0)
			return Collections.emptyList();
		else {
			List<Object> retArr = new ArrayList<>();

			Iterator<ConsumerRecord<String, byte[]>> iter = records.iterator();
			if (isJsonSerializeType) {
				while (iter.hasNext()) {
					ConsumerRecord<String, byte[]> rec = iter.next();
					retArr.add(serializeJsonTool.asObject(rec.value()));
				}
			} else {
				while (iter.hasNext()) {
					ConsumerRecord<String, byte[]> rec = iter.next();
					retArr.add(serializeTool.asObject(rec.value()));
				}
			}
			return retArr;
		}
	}

	public List<byte[]> recvMsgBytes(int timeout) {
		ConsumerRecords<String, byte[]> records = recvRecords(timeout);

		if (records.count() <= 0)
			return Collections.emptyList();
		else {
			List<byte[]> retArr = new ArrayList<>();

			Iterator<ConsumerRecord<String, byte[]>> iter = records.iterator();
			while (iter.hasNext()) {
				ConsumerRecord<String, byte[]> rec = iter.next();
				retArr.add(rec.value());
			}
			return retArr;
		}
	}

	public boolean isconnected() {
		boolean bRet = true;
		if ((mode == Const.READ_ONLY || mode == Const.READ_AND_WRITE) && consumer == null) {
			// consumer.
			bRet = false;
		}
		if (bRet && (mode == Const.WRITE_ONLY || mode == Const.READ_AND_WRITE) && producer == null) {
			bRet = false;
		}

		return bRet;
	}

	public boolean getAutoCommit() {
		return autoCommit;
	}

	public void commit() {
		if (consumer != null)
			consumer.commitSync();
	}

	public boolean setTopic(String topic) {
		if (consumer == null)
			return false;

		List<PartitionInfo> infos = consumer.partitionsFor(topic);
		if (infos == null || infos.size() <= 0) {
			return false;
		}

		List<TopicPartition> list = new ArrayList<TopicPartition>();
		for (PartitionInfo info : infos) {
			TopicPartition part = new TopicPartition(topic, info.partition());
			list.add(part);
		}
		consumer.assign(list);

		return true;
	}

	public boolean setPatition(String topic, int partition) {
		if (consumer == null)
			return false;

		TopicPartition part = new TopicPartition(topic, partition);
		consumer.assign(Arrays.asList(part));

		return true;
	}

	public boolean setPatitions(String topic, List<Integer> partitions) {
		if (consumer == null)
			return false;

		List<TopicPartition> list = new ArrayList<>();
		for (int i : partitions) {
			TopicPartition part = new TopicPartition(topic, i);
			list.add(part);
		}
		consumer.assign(list);

		return true;
	}

	public int setBatchSize(int iBatchSize) {
		if (iBatchSize >= 1 && iBatchSize <= Const.MAX_MQ_BATCH_SIZE)
			batchSize = iBatchSize;
		return batchSize;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public boolean connectCluster(List<ConnHostBean> connHostBeanList, List<ConnPropertyBean> connPropertyBeanList,
			Map<String, String> propertys, int aMode) {
		if (aMode >= Const.READ_ONLY && aMode <= Const.READ_AND_WRITE)
			mode = aMode;
		else {
			logger.error("connect kafka fail:connect mode set error.");
			return false;
		}

		Set<HostAndPort> handp = new HashSet<>();
		Iterator<ConnHostBean> it = connHostBeanList.iterator();
		while (it.hasNext()) {
			ConnHostBean connHostBean = (ConnHostBean) it.next();
			handp.add(new HostAndPort(connHostBean.getHostIp(), connHostBean.getHostPort()));
		}

		boolean bRet = true;
		if ((mode == Const.READ_ONLY || mode == Const.READ_AND_WRITE) && consumer == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", getKafkaClusterUrl(handp));

			Iterator<ConnPropertyBean> itr = connPropertyBeanList.iterator();
			while (itr.hasNext()) {
				ConnPropertyBean connPropertyBean = (ConnPropertyBean) itr.next();
				if (connPropertyBean.getPropType().charAt(0) == BaseDefine.PROP_TYPE_CONSUMER
						|| connPropertyBean.getPropType().charAt(0) == BaseDefine.PROP_TYPE_COMMON) {
					props.setProperty(connPropertyBean.getPropName(), connPropertyBean.getPropValue());
				}
			}

			// 除上面配置外，添加其他用户自定义配置
			Set<String> configName = ConsumerConfig.configNames();
			if (propertys != null && !propertys.isEmpty()) {
				for (Map.Entry<String, String> entry : propertys.entrySet()) {
					if (configName.contains(entry.getKey())) {
						props.setProperty(entry.getKey(), entry.getValue());
					}
				}
			}

			// 设置密码
			if (passWd != null) {
				props.setProperty("ssl.truststore.password", this.passWd);
			}

			if(props.containsKey("enable.auto.commit")){
				if (props.getProperty("enable.auto.commit").equalsIgnoreCase("true")) {
					autoCommit = true;
				}
			}else{
				props.put("enable.auto.commit", "false");
			}
			
			consumer = new KafkaConsumer<String, byte[]>(props);

			bRet = (consumer != null);
		}
		if (bRet && (mode == Const.WRITE_ONLY || mode == Const.READ_AND_WRITE) && producer == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", getKafkaClusterUrl(handp));// 该地址是集群的子集，用来探测集群。

			Iterator<ConnPropertyBean> itr = connPropertyBeanList.iterator();
			while (itr.hasNext()) {
				ConnPropertyBean connPropertyBean = (ConnPropertyBean) itr.next();
				if (connPropertyBean.getPropType().charAt(0) == BaseDefine.PROP_TYPE_PRODUCER
						|| connPropertyBean.getPropType().charAt(0) == BaseDefine.PROP_TYPE_COMMON) {
					props.setProperty(connPropertyBean.getPropName(), connPropertyBean.getPropValue());
				}
			}

			// 除上面配置外，添加其他用户自定义配置
			Set<String> configName = ProducerConfig.configNames();
			if (propertys != null && !propertys.isEmpty()) {
				for (Map.Entry<String, String> entry : propertys.entrySet()) {
					if (configName.contains(entry.getKey())) {
						props.setProperty(entry.getKey(), entry.getValue());
					}
				}
			}

			// 设置密码
			if (passWd != null) {
				props.setProperty("ssl.truststore.password", this.passWd);
				props.setProperty("ssl.keystore.password", this.passWd);
				props.setProperty("ssl.key.password", this.passWd);
			}

			producer = new KafkaProducer<String, byte[]>(props);

			bRet = (producer != null);
		}

		return bRet;
	}

	private String getKafkaClusterUrl(Set<HostAndPort> handp) {
		StringBuilder sb = new StringBuilder();
		boolean isNext = false;

		Iterator<HostAndPort> iter = handp.iterator();
		while (iter.hasNext()) {
			if (isNext)
				sb.append(",");
			HostAndPort hap = (HostAndPort) iter.next();
			sb.append(hap.getHost()).append(":").append(hap.getPort());
			isNext = true;
		}
		return sb.toString();
	}

	public int getPatitionSize(String topic) {
		int ret = 0;
		if (consumer != null) {
			List<PartitionInfo> infos = consumer.partitionsFor(topic);
			ret = infos.size();
		}

		return ret;
	}
	
	//仅仅获取kafka分区数
	public static int getPartitionSize(int kafkaId,String topicName){
		// 连接kafka
		KafkaMq kafka = KafkaMgr.getKafka(kafkaId, Const.READ_ONLY);
		
		if(kafka!=null){
			// 获取并发数(kafka分区数)		
			int parallel = kafka.getPatitionSize(topicName);
			
			kafka.disconnect();
			
			kafka = null;
			
			return parallel;
		}
		
		return Const.ERROR;
	}

	//获取topic列表
	public Set<String> getTopicList() {
		return consumer.listTopics().keySet();
	}
	
	//注册需要序列化的类
	@SuppressWarnings("rawtypes")
	public void registerClass( Class ... c){
		if(isJsonSerializeType){
			serializeJsonTool.registerClass(c);
		}else{
			serializeTool.registerClass(c);
		}
	}
}
