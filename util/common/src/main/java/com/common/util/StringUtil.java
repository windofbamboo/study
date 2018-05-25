package com.common.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * @author xue
 */
public class StringUtil {

	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	
	private StringUtil() {}
	/**
	 * 字符串列表转化为字符串
	 */
	public static String toString(List<String> strList) {
	    StringBuilder sb = new StringBuilder();
		if (strList == null || strList.isEmpty())
			return sb.toString();
		for (int i = 0; i < strList.size(); i++) {
			String str = strList.get(i);
			sb.append("'").append(str).append("'").append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 转化为整数
	 */
	public static int toInt(String srcStr, int defaultValue) {
		if (isEmpty(srcStr))
			return defaultValue;
		
		int result = defaultValue;
		try {
			result = Integer.parseInt(srcStr);
		} catch (NumberFormatException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return result;
	}

	public static int toInt(String srcStr) {
		return toInt(srcStr, 0);
	}

	/**
	 * 是否是数字
	 */
	public static boolean isNumber(String str) {
		if (isEmpty(str))
			return false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	/**
	 * 转化为double
	 */
	public static double toDouble(String srcStr, double defaultValue) {
		if (isEmpty(srcStr))
			return defaultValue;
		double result = defaultValue;
		try {
			result = Double.parseDouble(srcStr);
		} catch (NumberFormatException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return result;
	}

	public static double toDouble(String srcStr) {
		return toDouble(srcStr, 0.0D);
	}

	/**
	 * 转化为long
	 */
	public static long toLong(String srcStr, long defaultValue) {
		if (isEmpty(srcStr))
			return defaultValue;
		long result = defaultValue;
		try {
			result = Long.parseLong(srcStr);
		} catch (NumberFormatException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return result;
	}

	public static long toLong(String srcStr) {
		return toLong(srcStr, 0L);
	}

	/**
	 * 转化为boolean
	 */
	public static boolean toBoolean(String srcStr, boolean defaultValue) {
		if (isEmpty(srcStr))
			return defaultValue;
		boolean result = defaultValue;
		String src=srcStr.trim().toLowerCase();
		if ("true".equals(src) || "1".equals(src)) {
			result = true;
		} else {
			if ("false".equals(src) || "0".equals(src)||src.isEmpty()) {
				result = false;
			}else {
			    result = true;
			}
		}
		return result;
	}

	public static boolean toBoolean(String srcStr) {
		return toBoolean(srcStr, false);
	}

	/**
	 * 格式化数字
	 */
	public static String formatNumber(double db, String fmt) {
		String result = "";
		if (null == fmt || "".equals(fmt.trim()))
			return Double.toString(db);
		try {
			DecimalFormat decimalformat = new DecimalFormat(fmt);
			result = decimalformat.format(db);
		} catch (IllegalArgumentException iaex) {
			result = Double.toString(db);
		}
		return result;
	}

	public static String formatNumber(double db) {
		return formatNumber(db, "0.00");
	}

	public static String formatNumber(String numStr, String fmt) {
		double db = toDouble(numStr, 0.0D);
		return formatNumber(db, fmt);
	}

	public static String formatNumber(String numStr) {
		return formatNumber(numStr, "0.00");
	}

	/**
	 * 是否是空值
	 */
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str.trim());
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}


	public static String toVisualString(String srcStr) {
		if (srcStr == null || srcStr.equals(""))
			return "";
		else
			return srcStr;
	}

	/**
	 * 对象转成字符串 null或者"null"都转成 ""
	 */
	public static String toString(Object obj) {
		String str = "";
		if (obj == null) {
			return "";
		} else {
			str = obj.toString();
			if (("").equals(str) || "null".equals(str)) {
				return "";
			} else {
				return str;
			}
		}
	}

	/**
	 * 空字符串转换成空对象
	 */
	public static Object emptyStringToNull(String str) {
		if ("".equals(str)) {
			return null;
		}
		return str;
	}

	/**
	 * 判断对象是否为空
	 */
	public static boolean isNullEmpty(Object o) {
		return  (o == null || "".equals(o.toString().trim()) || "null".equalsIgnoreCase(o.toString().trim()));
	}
	
	/**
	 * 检查字符串是否是空白
	 */
	public static boolean isBlank(String str) {
		int length;

		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}

		for (int i = 0; i < length; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}
	
	///配置文件的key和变量名进行比较,忽略大小写和下划线'_'
	public static boolean equalIgnoreCaseAndIgnoreUnderline(String key,String innerVar) {
		return key.replace(Const.PROPERTIES_KEY_SPLIT, "").equalsIgnoreCase(innerVar);
	}
	
	//将多个合成一个
	public static String linkString(Object... params){
		StringBuilder sb = new StringBuilder();
		for(Object obj:params){
			sb.append(obj);
		}
		return sb.toString();
	}

	public static String join(List<String> list, String separator) {
		if (CheckNull.isNull(list)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String unit : list) {
			if (i>0) {
				sb.append(separator);
			}
			sb.append(unit);
			i++;
		}

		return sb.toString();
	}
	
	/**
	 * 拼接字符串
	 */
	public static String connectString(String... values){
	    StringBuilder sb = new StringBuilder();
		for(String value : values){
			sb.append(value);
		}
		return sb.toString();
	}
	
	public static String string2Unicode(String s) {  
	    StringBuilder out = new StringBuilder("");  
     byte[] bytes = s.getBytes(Const.UNICODE);
     for (int i = 2; i < bytes.length - 1; i += 2) {  
        out.append("u");  
        String str = Integer.toHexString(bytes[i + 1] & 0xff);  
        for (int j = str.length(); j < 2; j++) {  
          out.append("0");  
        }  
        String str1 = Integer.toHexString(bytes[i] & 0xff);  
  
        out.append(str);  
        out.append(str1);  
        out.append(" ");  
     }  
     return out.toString().toUpperCase();  
	  }   
	
	public static String unicode2String(String unicodeStr){  
	    StringBuilder sb = new StringBuilder();  
	    String str[] = unicodeStr.toUpperCase().split("U");  
	    for(int i=0;i<str.length;i++){  
	      if(str[i].equals("")) continue;  
	      char c = (char)Integer.parseInt(str[i].trim(),16);  
	      sb.append(c);  
	    }  
	    return sb.toString();  
	  }  
}
