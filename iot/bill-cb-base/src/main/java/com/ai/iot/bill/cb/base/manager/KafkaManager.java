package com.ai.iot.bill.cb.base.manager;

import java.util.List;

import com.ai.iot.bill.cb.base.entity.CdrInfo;
import com.ai.iot.bill.cb.base.entity.CrmNotifyInfo;
import com.ai.iot.bill.cb.base.entity.ImeiChangedNotify;
import com.ai.iot.bill.common.cdr.Cdr;
import com.ai.iot.bill.common.cdr.CdrAttri;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.util.CdrConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.JSONUtil;

/**
 * Kafka管理类
 * @author xue
 *
 */
public class KafkaManager {
	private KafkaMq kafkaMq;
	
	public KafkaManager(KafkaMq kafkaMq) {
		this.kafkaMq = kafkaMq;
	}

	/**
	 * 发送消息
	 * @param topic
	 * @param msg
	 * @return
	 */
	public int sendMsg(String topic, String msg) {
		return kafkaMq.sendMsg(topic, msg);
	}
	
	/**
	 * 发送实时用量
	 * 
	 * @param cdr
	 *            话单
	 * @param partitions
	 *            分区为null 则取所有分区
	 * @return 返回-1代表出错
	 */
	public int sendCdrAmount(String topic, Cdr cdr, List<Integer> partitions) {
		if (CheckNull.isNull(cdr) || CheckNull.isNull(kafkaMq)) {
			return -1;
		}
		
		if (!CheckNull.isNull(partitions)) {
			kafkaMq.setPatitions(topic, partitions);
		}
		
		StringBuffer sb = new StringBuffer();
		int bizType = cdr.getInt(CdrAttri.ATTRI_BIZ_TYPE);
		int totalRows = 0;
		if (bizType == CdrConst.BIZ_DATA) {
			totalRows = cdr.getRows(CdrAttri.ATTRI_RG_ID);
		} else {
			totalRows = cdr.getRows(CdrAttri.ATTRI_BEGIN_TIME);
		}
		
		for (int row = 0; row < totalRows; row ++) {
			sb.setLength(0);
			if (cdr.isErrorCdr(row)) {
				continue;
			}
			// ACCT_ID + DEVICE_ID = 主键
			sb.append(cdr.get(CdrAttri.ATTRI_ACCT_ID));
			sb.append(cdr.get(CdrAttri.ATTRI_DEVICE_ID));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_BIZ_TYPE));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_DEVICE_ID));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_ICCID));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_ACCT_ID));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_SERVICE_PROVIDER_CODE));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_CYCLE_ID));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_GSM_AMOUNT, row));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_SMS_AMOUNT, row));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_BASE_AMOUNT, row));
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_EVENT_AMOUNT, row));
			sb.append(Const.VALUE_JOIN);
			sb.append(CdrConst.ONLINE_STATUS_DEFAULT);
			sb.append(Const.VALUE_JOIN);
			sb.append(cdr.get(CdrAttri.ATTRI_IS_LIMITED));
			kafkaMq.sendMsg(topic, sb.toString());
		}
		return 0;
	}

	/**
	 * 通知状态变更
	 * 
	 * @param
	 */
	public int sendCrmNotify(String topic, CrmNotifyInfo crmNotifyInfo) {
		if (CheckNull.isNull(kafkaMq)) {
			return 0;
		}

		return kafkaMq.sendMsg(topic, JSONUtil.toJson(crmNotifyInfo));
	}

	/**
	 * 通知SIM卡状态变更
	 *
	 * @param cdrInfo
	 *            话单信息
	 */
	public int sendImeiChangedNotify2Crm(String topic, CdrInfo cdrInfo) {
		if (CheckNull.isNull(kafkaMq)) {
			return 0;
		}

		ImeiChangedNotify imeiChangedNotify = new ImeiChangedNotify();
		Cdr cdr = cdrInfo.getCdr();
		imeiChangedNotify.setAcctId(cdr.getLong(CdrAttri.ATTRI_ACCT_ID));
		imeiChangedNotify.setDeviceId(cdr.getLong(CdrAttri.ATTRI_DEVICE_ID));
		imeiChangedNotify.setNewImei(cdr.get(CdrAttri.ATTRI_IMEI));
		imeiChangedNotify.setOldImei(cdrInfo.getImei());

		return kafkaMq.sendMsg(topic, imeiChangedNotify.toString());
	}
}
