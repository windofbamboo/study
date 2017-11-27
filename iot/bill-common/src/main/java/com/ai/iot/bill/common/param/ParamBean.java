/**
 * 
 */
package com.ai.iot.bill.common.param;

/**
 * @author XLR
 *
 */
public class ParamBean {
	private String paramType;
	private String paramName;
	private String paramValue;
	private String paramValue2;
	private String paramValue3;
	
	public String getParamValue3() {
		return paramValue3;
	}

	public void setParamValue3(String paramValue3) {
		this.paramValue3 = paramValue3;
	}

	public ParamBean(){
	}
	
	public ParamBean(String aType){
		paramType = aType;
	}
	
	public ParamBean(String aType,String aName){
		paramType = aType;
		paramName = aName;
	}
	
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getParamValue2() {
		return paramValue2;
	}
	public void setParamValue2(String paramValue2) {
		this.paramValue2 = paramValue2;
	}
}
