package com.ai.iot.bill.cb.base.entity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.cb.base.manager.KafkaManager;
import com.ai.iot.bill.cb.base.manager.MdbManager;
import com.ai.iot.bill.cb.base.manager.ParamManager;
import com.ai.iot.bill.cb.base.manager.SessionManager;
import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.mdb.CustJedisCluster;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.pos.PoCycle;
import com.ai.iot.bill.common.util.CdrConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.mdb.common.device.DeviceRatePlanMo;
import com.ai.iot.mdb.common.rate.BillManager;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * 批价全局信息类
 * 
 * @author xue
 *
 */
public class RateGlobalInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(RateGlobalInfo.class);

	/**
	 * 话单
	 */
	private Cdr cdr;

	/**
	 * 累计量管理类
	 */
	private BillManager billManager;

	/**
	 * 参数管理类
	 */
	private ParamManager paramManager;

	/**
	 * 内存管理类
	 */
	private MdbManager mdbManager;
	
	/**
	 * session管理类
	 */
	private SessionManager sessionManager;

	/**
	 * kafka输出类
	 */
	private KafkaManager kafkaManager;

	/**
	 * Jedis客户端
	 */
	private CustJedisCluster custJedisCluster;

	/**
	 * 资费订购记录
	 */
	private List<DeviceRatePlanMo> deviceRatePlanList;

	/**
	 * CRM状态通知
	 */
	private List<CrmNotifyInfo> crmNotifyList;
	
	/**
	 * bolt输出
	 */
	private OutputCollector collector;
	
	/**
	 * bolt tuple
	 */
	private Tuple tuple;
	
	/**
	 * 共享池ID
	 */
	private int poolId;
	
	public RateGlobalInfo() {
		crmNotifyList = new ArrayList<CrmNotifyInfo>();
	}

	public Cdr getCdr() {
		return cdr;
	}

	public void setCdr(Cdr cdr) {
		this.cdr = cdr;
		if (cdr.getInt(CdrAttri.ATTRI_CYCLE_ID) == 0) {
			setCycleId(cdr);
		}
	}

	public BillManager getBillManager() {
		return billManager;
	}

	public void setBillManager(BillManager billManager) {
		this.billManager = billManager;
	}

	public ParamManager getParamManager() {
		return paramManager;
	}

	public void setParamManager(ParamManager paramManager) {
		this.paramManager = paramManager;
	}

	public MdbManager getMdbManager() {
		return mdbManager;
	}

	public void setMdbManager(MdbManager mdbManager) {
		this.mdbManager = mdbManager;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public KafkaManager getKafkaManager() {
		return kafkaManager;
	}

	public void setKafkaManager(KafkaManager kafkaManager) {
		this.kafkaManager = kafkaManager;
	}

	public CustJedisCluster getCustJedisCluster() {
		return custJedisCluster;
	}

	public void setCustJedisCluster(CustJedisCluster custJedisCluster) {
		this.custJedisCluster = custJedisCluster;
	}

	public List<DeviceRatePlanMo> getDeviceRatePlanList() {
		return deviceRatePlanList;
	}

	public void setDeviceRatePlanList(List<DeviceRatePlanMo> deviceRatePlanList) {
		this.deviceRatePlanList = deviceRatePlanList;
	}

	public List<CrmNotifyInfo> getCrmNotifyList() {
		return crmNotifyList;
	}

	public void setCrmNotifyList(List<CrmNotifyInfo> crmNotifyList) {
		this.crmNotifyList = crmNotifyList;
	}

	public OutputCollector getCollector() {
		return collector;
	}

	public void setCollector(OutputCollector collector) {
		this.collector = collector;
	}

	public Tuple getTuple() {
		return tuple;
	}

	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}
	
	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	/**
	 * 系统时间
	 * 
	 * @return int
	 */
	public String getDealTime() {
		return DateUtil.getCurrentDateTime(DateUtil.SEG_YYYYMMDD_HHMMSS_SSS);
	}
	
	public String getDealTime(String format) {
		return DateUtil.getCurrentDateTime(format);
	}

	public int getCycleId() {
		return cdr.getInt(CdrAttri.ATTRI_CYCLE_ID);
	}
	
	private void setCycleId(Cdr cdr) {
		int billCycle = 0;
		// 双账期：首先根据话单开始日期时间求取账期，如果求取不到，就用系统账期
		PoCycle poCycle = paramManager.getBillCycle(cdr.get(CdrAttri.ATTRI_BEGIN_DATE) + cdr.get(CdrAttri.ATTRI_BEGIN_TIME));
		if (!CheckNull.isNull(poCycle)) {
			if (poCycle.getMonthAcctStatus().equals(String.valueOf(RateConst.CYCLE_STATUS_INIT))) {
				billCycle = poCycle.getCycleId();
			}
		}
		
		// 根据系统时间求取账期
		if (billCycle == 0) {
			poCycle = paramManager.getBillCycle(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS));
			if (!CheckNull.isNull(poCycle)) {
				billCycle = poCycle.getCycleId();
			}
		}
		
		// 求取不到账期，打为错单
		if (billCycle == 0) {
			cdr.setInt(CdrAttri.ATTRI_ERROR_CODE, RateError.E_BILL_CYCLE);
		}
		
		cdr.setInt(CdrAttri.ATTRI_CYCLE_ID, billCycle);
	}
	
	/**
	 * 重置
	 */
	public void reset() {
		crmNotifyList.clear();
		poolId = 0;
	}
	
	public void emitAndAck(RateInfo rateInfo) {
		logger.info("======emitAndAck======");
		collector.emit(new Values(rateInfo));
		// ack给spout，只有离线业务才需要ack
		if (!cdr.get(CdrAttri.ATTRI_SOURCE_TYPE).equals(CdrConst.SROUCE_TYPE_ONLINE_DATA)) {
			logger.info("======ack======");
			logger.info("======ack messageId======:" + tuple.getMessageId().toString());
			collector.ack(tuple);
		}
	}
	
	public void fail() {
		if (!cdr.get(CdrAttri.ATTRI_SOURCE_TYPE).equals(CdrConst.SROUCE_TYPE_ONLINE_DATA)) {
			logger.info("======fail======");
			logger.info("======MessageId======：" + tuple.getMessageId());
			collector.fail(tuple);
		}
	}
}
