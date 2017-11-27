package com.ai.iot.bill.common.mdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 获取和更新MDB数据
 */
public abstract class MdbTable {	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	protected Map<String,Class<?>> classFields=new HashMap<>();
	protected List<String> fieldList=new ArrayList<>();
	protected List<Integer> fieldTypeList=new ArrayList<>();
	
	protected void addMoBaseField(Class<?> clazz) {
		MoBase mo;
		try {
			mo = (MoBase) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("class is unknown:{}",e);
			return;
		}
		classFields.put(mo.getField(), clazz);
		fieldList.add(mo.getField());
		fieldTypeList.add(mo.getFieldType());		
	}
	protected void addMoStringField(String field) {
		classFields.put(field, String.class);
		fieldList.add(field);
		fieldTypeList.add(MdbTables.FIELD_TYPE_STRING);		
	}
	protected void addMoIntegerField(String field) {
		classFields.put(String.valueOf(field), Integer.class);
		fieldList.add(String.valueOf(field));
		fieldTypeList.add(MdbTables.FIELD_TYPE_INT);		
	}
	protected void addMoLongField(String field) {
		classFields.put(String.valueOf(field), Long.class);
		fieldList.add(String.valueOf(field));
		fieldTypeList.add(MdbTables.FIELD_TYPE_LONG);		
	}
	/**基本类型对应的List,包括String*/
	protected void addMoPrimaryListField(String field) {
		classFields.put(field, String.class);
		fieldList.add(field);
		fieldTypeList.add(MdbTables.FIELD_TYPE_LIST);		
	}
	/*
	 * 继承类需在此函数中返回MdbTable的唯一名称
	 */
	public abstract String getMdbTableKeyId() ;
	
	/*
	 * 继承类需在此函数中返回需要hash的key
	 */
	public abstract String getKey();
	
	/*
	 * 继承类需在此函数中返回需要hash的fields
	 */
	public List<String> getFields(){
		return fieldList;
	}
	
	/*
	 * 返回各fields的类型，必须和要select的field顺序一致
	 */
	public List<Integer> getFieldsType(){
		return fieldTypeList;
	}
	
	/*
	 * 返回fields对象的Object类型，用于反序列化生成此类
	 */
	public Class<?> getFieldClazz(String fieldName) {
		return classFields.get(fieldName);
	}
	
}
