package com.ai.iot.bill.common;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.ai.iot.bill.common.mq.RedisSortMq;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.util.DateUtil;

import org.junit.Before;
import org.junit.Test;

public class RedisSortMqTest {
	Map<String, Long> results;
	long size = 0;
	boolean isOk = true;
	RedisSortMq sortMq;
	long now;
	int batch = 3;

	@Before
	public void setUp() throws Exception {
		sortMq = new RedisSortMq(BaseDefine.CONNTYPE_MQ_AUTORULE_ONEFIXTIME);
		now = DateUtil.nowAbsSeconds();
		// 准备数据
		sortMq.sendMessage("1", "45d", now - 300);
		sortMq.sendMessage("1", "mkdk", now - 238);
		sortMq.sendMessage("1", "9kdkj", now - 38);
		sortMq.sendMessage("1", "put", now - 13);
		sortMq.sendMessage("1", "bde", now);
		sortMq.sendMessage("0", "mkdk", now - 238);
		sortMq.sendMessage("0", "put", now - 13);
	}

	// 全量数据获取
	@Test
	public void testRecvAllMsgStrings() {
		isOk = true;
		System.out.println("test recvAllMsgStrings()...");
		results = sortMq.recvAllMsgStrings("1", now - 500, now);
		if(results==null||results.size()!=5) {
			assertTrue(false);
			return;
		}
		for (Map.Entry<String, Long> entry : results.entrySet()) {
			if (!(entry.getKey().equals("mkdk") || entry.getKey().equals("9kdkj") || entry.getKey().equals("put")
					|| entry.getKey().equals("bde") || entry.getKey().equals("45d"))) {
				isOk = false;
				break;
			}
		}
		assertTrue(isOk);
	}

	// 数据获取
	@Test
	public void testRecvAllMsgStringsByLimit() {
		isOk = true;
		System.out.println("test recvAllMsgStrings() by limit ...");
		results = sortMq.recvAllMsgStrings("1", now - 238, now);
		if(results==null||results.size()!=4) {
			assertTrue(false);
			return;
		}
		for (Map.Entry<String, Long> entry : results.entrySet()) {
			if (!(entry.getKey().equals("mkdk") || entry.getKey().equals("9kdkj") || entry.getKey().equals("put")
					|| entry.getKey().equals("bde"))) {
				isOk = false;
				break;
			}
		}
		assertTrue(isOk);
	}

	// 数据获取
	@Test
	public void testSize() {
		isOk = true;
		System.out.println("test sizeByScore()...");
		size = sortMq.sizeByScore("1", now - 238, now);
		if (size != 4) {
			isOk = false;
		}
		assertTrue(isOk);
	}

	// 分批次数据获取
	@Test
	public void testRecvMsgStringsFrom0() {
		isOk = true;
		int count = 0;
		System.out.println("test recvMsgStrings() from 0...");
		results = sortMq.recvMsgStrings("1", now - 238, now, 0, batch);
		if(results==null) {
			assertTrue(false);
			return;
		}
		for (Map.Entry<String, Long> entry : results.entrySet()) {
			if (entry.getKey().equals("mkdk") || entry.getKey().equals("9kdkj") || entry.getKey().equals("put")
					|| entry.getKey().equals("bde")) {
				count++;
			}
		}
		if (count != batch) {
			isOk = false;
		}
		assertTrue(isOk);
	}

	// 分批次数据获取
	@Test
	public void testRecvMsgStringsFrom3() {
		isOk = true;
		System.out.println("test recvMsgStrings() from 3...");
		results = sortMq.recvMsgStrings("1", now - 238, now, 3, batch);
		if(results==null) {
			assertTrue(false);
			return;
		}
		int count = 0;
		for (Map.Entry<String, Long> entry : results.entrySet()) {
			if (entry.getKey().equals("mkdk") || entry.getKey().equals("9kdkj") || entry.getKey().equals("put")
					|| entry.getKey().equals("bde")) {
				count++;
			}
		}
		if (count != 1) {
			isOk = false;
		}
		assertTrue(isOk);
	}
}
