package com.ai.iot.bill.common.mdb;

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
public class MdbCommonException extends Exception {
	private static final long serialVersionUID = 7160375318735556024L;
	private MdbCommonExceptionENUM errorCode;
	public  enum MdbCommonExceptionENUM{
		SUCCESS(0),
		JEDIS_CLUSTER_IS_NULL(1),
		MM_OBJECT_IS_NULL(2),
		MDB_TABLE_NAME_NOT_EXISTS(3),
		MAX(4);
		private  MdbCommonExceptionENUM(int p) {
			this.pos=p;
		}
		public int getPos() {
			return pos;
		}
		private int pos;
	}
    protected static final String[] exceptionMessage = {
    	"成功",
    	"jedis集群连接对象为空",
    	"MM对象不存在",
    	"mdbkey未在代码定义和实现"
    };
	public MdbCommonExceptionENUM getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return super.getLocalizedMessage();
	}
	
	public MdbCommonException() {
		errorCode = MdbCommonExceptionENUM.SUCCESS;
	}

	public MdbCommonException(MdbCommonExceptionENUM eInt) {
		super(exceptionMessage[eInt.getPos()]);
		this.errorCode=eInt;		
	}
	public MdbCommonException(MdbCommonExceptionENUM eInt,String... eMsgs) {
		super("["+Arrays.toString(eMsgs)+"]"+exceptionMessage[eInt.getPos()]);
		this.errorCode=eInt;		
	}
}
