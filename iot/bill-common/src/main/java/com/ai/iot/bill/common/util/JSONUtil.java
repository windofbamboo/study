package com.ai.iot.bill.common.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json操作工具类
 * @author xue
 * @ClassName: JSONUtil
 */
public abstract class JSONUtil {

	/**
	 * 默认json格式化方式
	 */
	protected static final SerializerFeature[] DEFAULT_FORMAT = { SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.WriteEnumUsingToString, SerializerFeature.WriteNonStringKeyAsString,
			SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField, SerializerFeature.SortField,
			SerializerFeature.PrettyFormat };
	/**
	 * 默认序列化用的json格式化方式
	 */
	protected static final SerializerFeature[] SERIALIZE_DEFAULT_FORMAT = { SerializerFeature.WriteClassName,
			SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.SkipTransientField};

	/**
	 * @param json
	 *            json字符串
	 * @param key
	 *            字符串的key
	 * @return 指定key的值
	 * @Title: getStringFromJSONObject
	 * @Description: 从json获取指定key的字符串
	 */
	public static Object getStringFromJSONObject(final String json, final String key) {
		return JSON.parseObject(json).getString(key);
	}

	/**
	 * @param jsonString
	 *            json字符串
	 * @return 转换成的json对象
	 * @Title: getJSONFromString
	 * @Description: 将字符串转换成JSON字符串
	 */
	public static JSONObject getJSONFromString(final String jsonString) {
		if (StringUtil.isBlank(jsonString)) {
			return new JSONObject();
		}
		return JSON.parseObject(jsonString);
	}
	
	/**
	 * @param jsonStr
	 *            json串对象
	 * @param beanClass
	 *            指定的bean
	 * @param <T>
	 *            任意bean的类型
	 * @return 转换后的java bean对象
	 * @Title: toBean
	 * @Description: 将json字符串，转换成指定java bean
	 */
	public static <T> T toBean(final String jsonStr, final Class<T> beanClass) {
		JSONObject jo = JSON.parseObject(jsonStr);
		jo.put(JSON.DEFAULT_TYPE_KEY, beanClass.getName());
		return JSON.parseObject(jo.toJSONString(), beanClass);
	}

	/**
	 * @param obj
	 *            需要转换的java bean
	 * @return 对应的json字符串
	 * @Title: toJson
	 */
	public static <T> String toJson(final T obj) {
		return JSON.toJSONString(obj, DEFAULT_FORMAT);
	}

	/**
	 * 需要进程级初始化一次下面的语句,支持autotype:
	 * ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	 * @param obj  需要转换的java对象
	 * @return 对应的json字符串
	 * @Title: toJson
	 */
	public static <T> String serializeToJson(final T javaObj) {
		return JSON.toJSONString(javaObj, SERIALIZE_DEFAULT_FORMAT);
	}
	
	/**
	 * 需要进程级初始化一次下面的语句,支持autotype:
	 * ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	 * @param jsonString  json字符串
	 * @return 转换成的java派生类对象
	 * @Title: toObject
	 * @Description: 将字符串转换成java对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T reverseSerializeToObject(final String serializeJsonString) {
		if (StringUtil.isBlank(serializeJsonString)) {
			return null;
		}
		return (T)JSON.parse(serializeJsonString);
	}

	/**
	 * 通过Map生成一个json字符串
	 * @param <T>
	 *
	 * @param map
	 * @return
	 */
	public static <T> String toJson(final Map<String, T> map) {
		return JSON.toJSONString(map, DEFAULT_FORMAT);
	}
	
	/**
	 * 通过List生成一个json字符串
	 * @param <T>
	 *
	 * @param map
	 * @return
	 */
	public static <T> String toJson(List<T> list) {
		return JSON.toJSONString(list, DEFAULT_FORMAT);
	}

	/**
	 * 美化传入的json,使得该json字符串容易查看
	 *
	 * @param jsonString
	 * @return
	 */
	public static String prettyFormatJson(final String jsonString) {
	    boolean isArray=false;
	    char c;
	    for(int i=0;i<jsonString.length();i++) {
	        c=jsonString.charAt(i);
	        if(c=='\n'||c=='\r'||c==' ') {
	            continue;
	        }
	        if(c=='[') {
	            isArray=true;
	            break;
	        }else {
	            break;
	        }
	    }
	    if(isArray) {
	        return JSON.toJSONString(JSON.parseArray(jsonString), true);
	    }
		return JSON.toJSONString(getJSONFromString(jsonString), true);
	}

	/**
	 * 将传入的json字符串转换成Map
	 * @param <T>
	 *
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> toMap(final String jsonString) {
		return (Map<String, T>) getJSONFromString(jsonString);
	}

	/**
	 * 将传入的json字符串转换成List
	 * @param <T>
	 * @param <T>
	 *
	 * @param jsonString
	 * @return
	 */
	public static <T> List<T> toList(final String jsonString, final Class<T> beanClass) {
		return JSON.parseArray(jsonString, beanClass);
	}
}
