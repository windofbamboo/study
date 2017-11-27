package com.ai.iot.mdb.common.rate;

import java.io.Serializable;

import shade.storm.org.apache.commons.lang.builder.EqualsBuilder;
import shade.storm.org.apache.commons.lang.builder.HashCodeBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringBuilder;
import shade.storm.org.apache.commons.lang.builder.ToStringStyle;

/**
 * 测试状态的累计量实体类
 * @author xue
 *
 */
public class MdbBillTest implements Serializable, Cloneable {

	private static final long serialVersionUID = -8980690943371051592L;

	/**
	 * 累计量
	 */
	private long value;
	
	public MdbBillTest() {
    }

	public MdbBillTest(MdbBillTest source) {
        this.value=source.value;
    }
    
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
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

}
