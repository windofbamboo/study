package com.ai.iot.bill.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 非空判断工具类
 * @author xue
 */
public class CheckNull {

	/**
	 * 
	 * 检查list是否为空 包括list的size为0
	 * 
	 * @param List
	 * @return true:空 false：非空
	 */
	public static boolean isNull(List<?> lst) {
		return null == lst || lst.isEmpty();
	}

	/**
	 * 
	 * 检查set是否为空 包括set的size为0
	 * 
	 * @param List
	 * @return true:空 false：非空
	 */
	public static boolean isNull(Set<?> set) {
		return null == set || set.isEmpty();
	}

	/**
	 * 检查数组是否为空
	 * 
	 * @param objs
	 * @return true:空 false：非空
	 */
	public static boolean isNull(Object[] objs) {
		if (null == objs) {
			return true;
		} else {
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] != null && !"".equals(objs[i].toString().trim())) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * 
	 * 检查map是否为空 包括map的size为0
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNull(Map<?, ?> map) {
		return null == map || map.isEmpty();
	}

	/**
	 * 
	 * 检查object是否为空
	 * 
	 * @param map
	 * @return true 空
	 */
	public static boolean isNull(Object obj) {
		return null == obj;
	}
	
	public static boolean isNull(String str) {
		return null == str || str.length() == 0;
	}
	
	private CheckNull() {
	    throw new IllegalStateException("Utility class");
	}
}
