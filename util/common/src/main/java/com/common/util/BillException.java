package com.common.util;

import java.util.Arrays;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [定义异常类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 * */
public class BillException extends Exception {
	private static final long serialVersionUID = 7160375318735556024L;
	private final BillExceptionENUM errorCode;
	public  enum BillExceptionENUM{
		SUCCESS,
		JDIS_CLUSTER_IS_NULL,
		CONFIG_IS_ERROR,
		ZK_PATH_ALREADY_EXISTS,
		ZK_WRAPPER_IS_NULL,
		IS_NOT_EXPECTED_OBJECT,
		CHANNEL_INSTANCE_ALREADY_EXIST,
		DUP_VALUE,
		MAX;
	}

    protected static final String[] exceptionMessage = {
    	"成功",
    	"JedisCluster为null",
    	"配置错误",
    	"ZK路径已经存在",
    	"ZK对象不存在",
    	"对象不是需要的类型",
    	"模块的通道实例已经存在",
    	"值重复"
    };
	public BillExceptionENUM getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return super.getLocalizedMessage();
	}
	
	public BillException(){
		errorCode = BillExceptionENUM.SUCCESS;
	}

	public BillException(BillExceptionENUM eInt) {
		super(exceptionMessage[eInt.ordinal()]);
		this.errorCode=eInt;
	}
	public BillException(BillExceptionENUM eInt,String... eMsgs) {
		super("["+Arrays.toString(eMsgs)+"]"+exceptionMessage[eInt.ordinal()]);
		this.errorCode=eInt;
	}
}
