package com.ai.iot.bill.common;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.mq.KafkaMgr;
import com.ai.iot.bill.common.mq.KafkaMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.Const;

public class KafkaMqTest {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaMqTest.class);
	private static KafkaMq mq=null;
	private static final String topic="top_test_result";
	private static final String testStr="top_test_result";
	@BeforeClass
	public static void open() throws Exception {
	}

	@Test
	public void testSendRecv() {
		//准备发送和接收使用同一个队列对象
		mq=KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_AUTORULE, Const.READ_AND_WRITE, "onefixtime");
		List<Integer> partitionNumList=new ArrayList<Integer>();
	    partitionNumList.add(0);
	    mq.setPatitions(topic, partitionNumList);
	    
	    //发送
		logger.info("sendMsg={}",testStr);
		mq.sendMsg(topic, testStr);
		
		//接收
		List<String> strList=null;
		while(strList==null) {
			strList=mq.recvMsgs(100);
			if(strList!=null) {
				break;
			}
			logger.info("go on recvMsgs=");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}		
		logger.info("recvMsgs={}",strList);
		assertEquals(strList.get(0), testStr);
	}
	
	/*@Test
	public void testSendRecvByMode() {
		//发送
		mq=KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_AUTORULE, Const.WRITE_ONLY, "onefixtime");	    
		logger.info("<<<testSendRecvByMode>>>sendMsg={}",testStr);
		mq.sendMsg(topic, testStr);
		
		//接收,使用另外一个队列对象
		mq=KafkaMgr.getKafka(BaseDefine.CONNTYPE_KF_AUTORULE, Const.READ_ONLY, "onefixtime");
		List<Integer> partitionNumList=new ArrayList<Integer>();
	    partitionNumList.add(0);
	    mq.setPatitions(topic, partitionNumList);
		List<String> strList=null;
		while(strList==null) {
			strList=mq.recvMsgs(100);
			if(strList!=null) {
				break;
			}
			logger.info("<<<testSendRecvByMode>>>go on recvMsgs=");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}		
		logger.info("<<<testSendRecvByMode>>>recvMsgs={}",strList);
		assertEquals(strList.get(0), testStr);
	}*/
}
