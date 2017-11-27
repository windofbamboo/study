package com.ai.iot.bill.common.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * properties文件辅助类
 * 
 * @author xue
 *
 */
public class PropertiesUtil {
	private final static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private final static String propertiesFileSuffix=".properties";
	
	/////////////////////////////////////////////////////////////////////////
	///和IOT_HOME变量相关
	/////////////////////////////////////////////////////////////////////////
	/**
	 * 根据Properties文件名，读取内容,等同getProperties,路径变量需配置到CLASSPATH里边
	 * 
	 * @param name  Properties文件名（不含路径和后缀）
	 * @return Properties Properties文件内容
	 */
	public static Properties getPropertiesWithHomeEnv(String name) {
//		String homeEnv=System.getenv(Const.SYSTEM_HOME);
//		if(!CheckNull.isNull(homeEnv)) {
//			File envPropertiesFile=new File(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+name);
//			if(!envPropertiesFile.exists()) {//尝试一下不带后缀的情况
//				envPropertiesFile=new File(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+name+propertiesFileSuffix);
//				if(!envPropertiesFile.exists()) {
//					return PropertiesUtil.getProperties(name);
//				}
//			}
//			logger.info("Loader Home Env's properties: {}",envPropertiesFile.getPath());
//			Properties properties = new Properties();
//			try {
//				properties.load(new FileInputStream(envPropertiesFile));
//				return properties;
//			} catch (Exception ex) {
//				logger.error(ex.getLocalizedMessage(),ex);
//			}
//		}
		return PropertiesUtil.getProperties(name);
	}
	
	/**
	 * 根据Properties文件名，读取内容,等同getPropertiesConfig,路径变量需配置到CLASSPATH里边
	 * 
	 * @param name
	 *            Properties文件名（不含路径）
	 * @return HashMap<String, String> Properties文件内容
	 */
	public static Map<String, String> getPropertiesConfigWithHomeEnv(String name) throws Exception {
//		String homeEnv=System.getenv(Const.SYSTEM_HOME);
//		if(!CheckNull.isNull(homeEnv)) {
//			File envPropertiesFile=new File(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+name);
//			if(!envPropertiesFile.exists()) {//尝试一下不带后缀的情况
//				envPropertiesFile=new File(homeEnv+File.separator+Const.SYSTEM_CONF+File.separator+name+propertiesFileSuffix);
//				if(!envPropertiesFile.exists()) {
//					return PropertiesUtil.getPropertiesConfig(name);
//				}
//			}
//			logger.info("Loader Home Env's properties: {}",envPropertiesFile.getPath());
//			try {
//				return PropertiesUtil.loadValues(new FileInputStream(envPropertiesFile));
//			} catch (Exception ex) {
//				logger.error(ex.getLocalizedMessage(),ex);
//			}
//		}
		return PropertiesUtil.getPropertiesConfig(name);
	}
	/////////////////////////////////////////////////////////////////////////
	//单个文件
	/////////////////////////////////////////////////////////////////////////
	/**
	 * 根据Properties文件名，读取内容
	 * 
	 * @param name
	 *            Properties文件名（不含路径）
	 * @return HashMap<String, String> Properties文件内容
	 */
	public static Properties getProperties(String name) {
		if (!CheckNull.isNull(name)) {
			// 文件名(包括路径)
			String filename = name;

			Properties properties = new Properties();
			InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
			if(CheckNull.isNull(is)) {//尝试一下不带后缀的情况
				if(name.endsWith(propertiesFileSuffix)) {
					return null;
				}
				
				logger.info("attemp to add {} ...",propertiesFileSuffix);
				filename = name + propertiesFileSuffix;
				return getProperties(filename);
			}
			logger.info("url={}",PropertiesUtil.class.getClassLoader().getResource(filename));
			try {
				properties.load(is);
				return properties;
			} catch (Exception ex) {//尝试一下不带后缀的情况
				if(name.endsWith(propertiesFileSuffix)) {
					return null;
				}
				logger.info("attemp to add {} ...",propertiesFileSuffix);
				filename = name + propertiesFileSuffix;
				 return getProperties(filename);
			}
		}
		return null;
	}

	/**
	 * 根据Properties文件名，读取内容
	 * 
	 * @param name
	 *            Properties文件名（不含路径）
	 * @return HashMap<String, String> Properties文件内容
	 */
	public static Map<String, String> getPropertiesConfig(String name) throws Exception {
		if (!CheckNull.isNull(name)) {
			// 文件名(包括路径)
			String filename = name;
			InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
			if(CheckNull.isNull(is)) {//尝试一下不带后缀的情况
				if(name.endsWith(propertiesFileSuffix)) {
					return null;
				}
				logger.info("attemp to add {} ...",propertiesFileSuffix);
				filename = name + propertiesFileSuffix;
				 return getPropertiesConfig(filename);
			}
			logger.info("url={}",PropertiesUtil.class.getClassLoader().getResource(filename));
			try {
				return loadValues(is);
			} catch (Exception ex) {//尝试一下不带后缀的情况
				if(name.endsWith(propertiesFileSuffix)) {
					return null;
				}
				logger.info("attemp to add {} ...",propertiesFileSuffix);
				filename = name + propertiesFileSuffix;
				return getPropertiesConfig(filename);
			}
		}
		return null;
	}

	public static Map<String, String> loadValues(InputStream inputStream) throws Exception {
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage(),ex);
			throw ex;
		} finally {
			inputStream.close();
		}
		Map<String, String> result = new HashMap<String, String>();
		Object[] keys = properties.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			String value = properties.getProperty(key).trim();
			result.put(key, value);
		}
		return result;
	}
	
}
