package com.ai.iot.bill.common.mq;

import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.mdb.RedisMgr;
import com.ai.iot.bill.common.util.BillException;
import com.ai.iot.bill.common.util.BillException.BillExceptionENUM;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.common.util.JSONUtil;
import com.alibaba.fastjson.parser.ParserConfig;

import redis.clients.jedis.Tuple;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * redis as sorted message queue,the score is long_yyyymmddhhmiss
 * 参考kafka消息,key的组成为主题topic+'+'+partitionId 方法参数的最大最小为闭区间.
 * recvMsg*不会删除数据,需另外调用removeMsg*接口进行删除 序列化\反序列化请使用该类提供的方法,保持双方序列化的一致性
 * redis有序集合只能存放string类型,不能存放对象,所以在zadd时需要转换成String,zrange时可以直接转换成对象
 * 否则可能会引起数据删除失败
 * 
 * @author shanzhiqiang
 **/
public class RedisSortMq {

	private static final Logger logger = LoggerFactory.getLogger(RedisSortMq.class);
	// /序列化工具
	private FSTConfiguration serializeTool = FSTConfiguration.createDefaultConfiguration();
	// 同一个对象序列化出来的字符串的内容有变化,虽不影响对象使用,但却影响有序队列的删除操作.所以暂不使用
	// private FSTConfiguration serializeJsonTool =
	// FSTConfiguration.createJsonConfiguration(true, false);
	// /序列化类型:false=FST;true=Json,如果使用FST,需要先进行测试,之前发现同一个对象使用FST序列化后的字符串会不同.
	private boolean isJsonSerializeType = true;

	/**
	 * 集群对象
	 */
	private CustJedisCluster mdbClient4Cluster = null;

	/**
	 * 错误返回值
	 */
	public static final int RESULT_INT_ERROR = -1;

	/**
	 * @Desciption 初始化
	 */
	public RedisSortMq(CustJedisCluster mdbClient4Cluster) {
		this.mdbClient4Cluster = mdbClient4Cluster;
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	}

	/**
	 * Creates a new instance of RedisMq. Description
	 *
	 * @param connType
	 *            BaseDefine里面定义的ConnectType
	 * @throws BillException
	 */
	public RedisSortMq(int connType) throws BillException {
		this.mdbClient4Cluster = RedisMgr.getJedisCluster(connType);
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		if (mdbClient4Cluster == null) {
			throw new BillException(BillExceptionENUM.JDIS_CLUSTER_IS_NULL, "connType=" + String.valueOf(connType));
		}
	}

	/**
	 * @Desciption 断开连接
	 */
	public void disconnect() {

		try {
			if (mdbClient4Cluster != null) {
				mdbClient4Cluster.close();
				mdbClient4Cluster = null;
			}
		} catch (Exception e) {
			logger.error("Faild to disconnect.{}", e);
		}
	}

	/**
	 * 序列化函数
	 */
	private String toByteArray(Object object) {
		if (isJsonSerializeType) {
			// return new String(serializeJsonTool.asJsonString(object));
			return JSONUtil.serializeToJson(object);
		} else {
			return serializeTool.asJsonString(object);
		}
	}

	/**
	 * 反序列化函数
	 */
	private Object toObject(byte[] data) {
		if (isJsonSerializeType) {
			// return serializeJsonTool.asObject(data);
			return JSONUtil.reverseSerializeToObject(new String(data));
		} else {
			return serializeTool.asObject(data);
		}
	}

	public Object toObject(Tuple tuple) {
		if (isJsonSerializeType) {
			// return serializeJsonTool.asObject(tuple.getElement().getBytes(Const.UTF8));
			return JSONUtil.reverseSerializeToObject(tuple.getElement());
		} else {
			return serializeTool.asObject(tuple.getElement().getBytes(Const.UTF8));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T toTrueObject(Tuple tuple) {
		if (isJsonSerializeType) {
			// return (T)
			// serializeJsonTool.asObject(tuple.getElement().getBytes(Const.UTF8));
			return JSONUtil.reverseSerializeToObject(tuple.getElement());
		} else {
			return (T) serializeTool.asObject(tuple.getElement().getBytes(Const.UTF8));
		}
	}

	/**
	 * 字符串对象保存
	 *
	 * @Desciption 发送消息：字符串
	 */
	public long sendMessage(String topicPartitionId, Map<String, Double> objectScores) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		Map<byte[], Double> sdata = new HashMap<byte[], Double>();
		for (Map.Entry<String, Double> entry : objectScores.entrySet()) {
			sdata.put(entry.getKey().getBytes(Const.UTF8), entry.getValue());
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), sdata).longValue();
	}

	public long sendMessage(String topicPartitionId, String object, double score) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), score, object.getBytes(Const.UTF8)).longValue();
	}

	/**
	 * 普通java序列化的对象保存
	 *
	 * @Desciption 发送消息：对象
	 */
	public long sendMessageObject(String topicPartitionId, Map<Object, Double> objectScores) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		Map<byte[], Double> sdata = new HashMap<byte[], Double>();
		for (Map.Entry<Object, Double> entry : objectScores.entrySet()) {
			sdata.put(toByteArray(entry.getKey()).getBytes(Const.UTF8), entry.getValue());
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), sdata).longValue();
	}

	public long sendMessageObject(String topicPartitionId, Object object) {
		return sendMessageObject(topicPartitionId, object,
				Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
	}

	public long sendMessageObject(String topicPartitionId, Object object, double score) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), score, toByteArray(object).getBytes(Const.UTF8)).longValue();
	}

	/**
	 * 外部序列化的对象保存,如果不带score,默认按系统时间绝对秒数填入score
	 *
	 * @Desciption 发送消息：已经序列化的对象
	 */
	public long sendMessageBytes(String topicPartitionId, Map<byte[], Double> objectScores) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), objectScores).longValue();
	}

	public long sendMessageBytes(String topicPartitionId, byte[] object) {
		return sendMessageBytes(topicPartitionId, object,
				Long.valueOf(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS)));
	}

	public long sendMessageBytes(String topicPartitionId, byte[] object, double score) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		return mdbClient4Cluster.zadd(topicPartitionId.getBytes(Const.UTF8), score, object).longValue();
	}

	/**
	 * 根据指定的score闭区间返回符合的记录数
	 */
	public long size(String topicPartitionId) {
		Date now = new Date();
		now.setTime(DateUtil.nowAbsSeconds() + 3600); // 扩到将来的的1小时时间
		return sizeByScore(topicPartitionId, -1,
				Long.valueOf(DateUtil.getCurrentDateTime(now, DateUtil.YYYYMMDD_HHMMSS)));
	}

	public long sizeByScore(String topicPartitionId, long minScore, long maxScore) {
		if (mdbClient4Cluster == null) {
			return RESULT_INT_ERROR;
		}
		return mdbClient4Cluster.zcount(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore).longValue();
	}

	/**
	 * 根据指定的score闭区间返回符合的记录
	 *
	 * @param topicPartitionId
	 *            队列标识
	 * @param minScore
	 *            最小分数
	 * @param maxScore
	 *            最大分数
	 * @param curOffset
	 *            当前批次开始的下标(从0开始)
	 * @param batchCount
	 *            批次对应返回的最大记录数
	 */
	public Set<Tuple> recvMsgs(String topicPartitionId, long minScore, long maxScore, int curOffset, int batchCount) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		return mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore, curOffset,
        		batchCount);
	}

	public Map<Object, Long> recvMsgObjects(String topicPartitionId, long minScore, long maxScore, int curOffset,
			int batchCount) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		Set<Tuple> results;
        results = mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore,
        		curOffset, batchCount);
		Map<Object, Long> resultDatas = new HashMap<Object, Long>();
		for (Tuple obj : results) {
			resultDatas.put(toObject(obj), Long.valueOf((long) obj.getScore()));
		}
		return resultDatas;
	}

	public Map<String, Long> recvMsgStrings(String topicPartitionId, long minScore, long maxScore, int curOffset,
			int batchCount) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		Set<Tuple> results;
        results = mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore,
        		curOffset, batchCount);
		Map<String, Long> resultDatas = new HashMap<String, Long>();
		for (Tuple obj : results) {
			resultDatas.put(new String(obj.getElement().getBytes(Const.UTF8)), Long.valueOf((long) obj.getScore()));
		}
		return resultDatas;
	}

	public Set<Tuple> recvAllMsgs(String topicPartitionId, long minScore, long maxScore) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		return mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore);
	}

	public Map<Object, Long> recvAllMsgObjects(String topicPartitionId, long minScore, long maxScore) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		Set<Tuple> results;
        results = mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore);
		Map<Object, Long> resultDatas = new HashMap<Object, Long>();
		for (Tuple obj : results) {
			resultDatas.put(toObject(obj.getElement().getBytes(Const.UTF8)), Long.valueOf((long) obj.getScore()));
		}
		return resultDatas;
	}

	public Map<String, Long> recvAllMsgStrings(String topicPartitionId, long minScore, long maxScore) {
		if (mdbClient4Cluster == null) {
			return null;
		}
		Set<Tuple> results;
        results = mdbClient4Cluster.zrangeByScoreWithScores(topicPartitionId.getBytes(Const.UTF8), minScore, maxScore);
		Map<String, Long> resultDatas = new HashMap<String, Long>();
		for (Tuple obj : results) {
			resultDatas.put(new String(obj.getElement().getBytes(Const.UTF8)), Long.valueOf((long) obj.getScore()));
		}
		return resultDatas;
	}

	/**
	 * @Desciption 删除消息
	 */
	public void removeMsg(String topicPartitionId, byte[]... removeObjects) {
		if (mdbClient4Cluster == null) {
			return;
		}
		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), removeObjects);
	}

	public void removeMsg(String topicPartitionId, byte[] removeObject) {
		if (mdbClient4Cluster == null) {
			return;
		}
		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), removeObject);
	}

	/**
	 * @Desciption 删除消息
	 */
	public void removeMsg(String topicPartitionId, Object... removeObjects) {
		if (mdbClient4Cluster == null) {
			return;
		}
		byte[][] datas = new byte[removeObjects.length][];
		int i = 0;
		for (Object obj : removeObjects) {
			datas[i] = toByteArray(obj).getBytes(Const.UTF8);
			i++;
		}

		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), datas);
	}

	public void removeMsg(String topicPartitionId, Object removeObject) {
		if (mdbClient4Cluster == null) {
			return;
		}
		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), toByteArray(removeObject).getBytes(Const.UTF8));
	}

	/**
	 * @Desciption 删除消息
	 */
	public void removeMsg(String topicPartitionId, String... removeObjects) {
		if (mdbClient4Cluster == null) {
			return;
		}
		byte[][] datas = new byte[removeObjects.length][];
		int i = 0;
		for (String obj : removeObjects) {
			datas[i] = obj.getBytes(Const.UTF8);
			i++;
		}
		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), datas);
	}

	public void removeMsg(String topicPartitionId, String removeObject) {
		if (mdbClient4Cluster == null) {
			return;
		}
		mdbClient4Cluster.zrem(topicPartitionId.getBytes(Const.UTF8), removeObject.getBytes(Const.UTF8));
	}

	/**
	 * @Desciption 判断是否连接
	 */
	public boolean isConnected() {
		if (mdbClient4Cluster != null) {
			String ok;
            ok = new String(mdbClient4Cluster.echo("11".getBytes(Const.UTF8)));
			if (ok.equals("11")) {
				return true;
			}
		}
		return false;
	}

}