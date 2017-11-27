package com.ai.iot.bill.common.paramcachemanager.pog.sys.pos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.StringUtil;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

public class PoScriptCode extends PoBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8596999754507045654L;

	/**
	 * @fields 编码标识
	 */
	private int codeId;
	/**
	 * @fields 类型标识
	 */
	private int typeId;
	/**
	 * @fields JS执行代码
	 */
	private String exeCode;
	/**
	 * @fields 优先级
	 */
	private int priority;

	public int getCodeId() {
		return codeId;
	}

	public void setCodeId(int codeId) {
		this.codeId = codeId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getExeCode() {
		return exeCode;
	}

	public void setExeCode(String exeCode) {
		this.exeCode = exeCode;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/*
	 * (非 Javadoc) <p>Title: </p>
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	/*
	 * (非 Javadoc) <p>Title: </p>
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/*
	 * (非 Javadoc) <p>Title: </p>
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	@Override
	public boolean fillData(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> fields = (List<String>) obj;
		this.codeId = StringUtil.toInt(fields.get(0));
		this.typeId = StringUtil.toInt(fields.get(1));
		this.exeCode = fields.get(2);
		this.priority = StringUtil.toInt(fields.get(3));

		return true;
	}

	@Override
	public String getPoGroupName() {
		return PoGroupRegisterFactory.PoGroupNameEnum.POG_SYS.toString();
	}

	@Override
	public Map<String, Method> createIndexMethods() throws NoSuchMethodException, SecurityException {
        Map<String,Method> indexMethods=new HashMap<String,Method>();
        indexMethods.put(getIndex1Name(),getClass().getMethod(getIndex1Name()));
        return indexMethods;
	}

    public String getIndex1Key() {
        return String.valueOf(this.typeId);
    }
    
	public static String getIndex1Name() {
		return "getIndex1Key";
	}
}
