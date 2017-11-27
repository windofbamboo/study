package com.ai.iot.bill.common.paramcachemanager;

import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoadException;
import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoader;
import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoaderConfigure;
import com.alibaba.fastjson.parser.ParserConfig;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by zhangrui on 2017/7/10.
 */
public class ParamTest {

	@Test
	public void main() {
		// 加载参数
		ParamLoaderConfigure paramLoaderConfigure = new ParamLoaderConfigure();
		ParamLoader paramLoader;
		try {
			paramLoader = new ParamLoader(paramLoaderConfigure);
		} catch (Exception e1) {
			e1.printStackTrace();
			assertTrue(false);
			return;
		}
		// JSON序列化时必须的初始化操作
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

		try {
			paramLoader.run();
		} catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | InstantiationException
				| InterruptedException | ParamLoadException e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}

		System.out.println("ParamTest end....");
		assertTrue(true);
	}
}
