package com.ai.iot.bill.common.util;

import java.util.UUID;

/**
 * UUID工具类
 * @author xue
 *
 */
public class UUIDUtil {
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
