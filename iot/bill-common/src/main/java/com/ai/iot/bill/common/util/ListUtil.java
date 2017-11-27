package com.ai.iot.bill.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhaojiajun on 2017/8/8
 */
public class ListUtil {

	public static <T> boolean isEmpty(List<T> list){
		if(list == null){
			return true;
		}
		return list.isEmpty();
	}
	
	public static <T> boolean isNotEmpty(List<T> list){
		return !isEmpty(list);
	}
	
	public static <T> Set<T> toSet(List<T> list){
		if(isNotEmpty(list)){
			return new HashSet<T>(list);
		}
		return null;
	}
	
	public static <T> List<T> toList(Set<T> set){
		List<T> list = new ArrayList<>();
		if(set!=null && !set.isEmpty()){
			for(T t : set){
				list.add(t);
			}
		}
		return list;
	}
	
	public static <T> List<T> clone(List<T> list){
		List<T> target = new ArrayList<>();
		if(isNotEmpty(list)){
			for(T t : list){
				target.add(t);
			}
		}
		return target;
	}
	
	public static String toString(List<String> list){
		if(list == null || list.isEmpty()){
			return "";
		} else {
			String str = list.toString();
			return str.substring(1, str.length()-1);
		}
	}
	
	public static <T> List<T> makeSameValue(int number, T value){
		if (number == 0) {
			return null;
		} else {
			List<T> list = new ArrayList<>();
			for (int i = 0; i < number; i++) {
				list.add(value);
			}
			return list;
		}
	}
	
	public static List<Integer> str2Int(List<String> list){
		if(list == null || list.isEmpty()){
			return null;
		} else {
			List<Integer> newList = new ArrayList<>();
			for(String value : list){
				newList.add(Integer.parseInt(value));
			}
			return newList;
		}
	}
}
