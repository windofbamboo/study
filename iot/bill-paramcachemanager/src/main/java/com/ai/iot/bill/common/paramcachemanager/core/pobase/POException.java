package com.ai.iot.bill.common.paramcachemanager.core.pobase;

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
public class POException extends Exception {
	private static final long serialVersionUID = 7160375318735556024L;
	private final POExceptionCodeENUM errorCode;
	public static class PORunTimeError extends RuntimeException{
        private static final long serialVersionUID = 964523633730252027L;
        PORunTimeError(String message){
            super(message);
        }
	}
	public  enum POExceptionCodeENUM{
		SUCCESS,
		PO_NOT_EXIST,
		POGROUP_NOT_EXIST,
		PO_CONTAINER_NOT_ASSIGNED,
		PARAM_CLIENT_EXIST,
		PARAM_CACHE_MANAGER_NULL,
		MDB_CLIENT_INIT_FALSE,
		LOADING_FROM_MDB_FALSE,
		PO_OBJECT_ALREADY_EXIST,
		PARAM_CACHE_MANAGER_START_FALSE,
		MDB_PO_GROUP_INFO_NONE,
		EHCACHE_PARAM_XML_FILE_NOT_EXIST,
		PARAM_CLIENT_NO_EXIT,
		EHCACHE_MANAGER_CREATE_FALSE,
		CANNT_CREATE_INSTANCE_MANNUL,
		MAX;
	}
    protected static final String[] exceptionMessage = {
    	"成功",
    	"PO不存在",
    	"PO组不存在",
    	"PO容器未赋值",
    	"缓存客户端已经存在",
    	"缓存管理组件未初始化",
    	"MDB客户端初始化失败",
    	"从MDB加载数据失败",
    	"PO对象已经存在",
    	"缓存管理组件初始化失败",
    	"PO组信息不存在,请核查MDB数据!!!",
    	"ehcache对应缓存的配置文件不存在,请核查!",
    	"有ParamClient没有退出",
    	"ehcache的管理器创建失败,请核查配置文件!!!",
    	"不能显式创建实例"
    };
	public POExceptionCodeENUM getErrorCode() {
	    if(errorCode==null)
	        return POExceptionCodeENUM.SUCCESS;
		return errorCode;
	}
	public String getErrorMessage() {
		return super.getLocalizedMessage();
	}

	public POException(POExceptionCodeENUM eInt) {
		super(exceptionMessage[eInt.ordinal()]);
		this.errorCode=eInt;		
		checkSelf();
	}
	public POException(POExceptionCodeENUM eInt,String... eMsgs) {
		super("["+Arrays.toString(eMsgs)+"]"+exceptionMessage[eInt.ordinal()]);
		this.errorCode=eInt;		
		checkSelf();
	}
	private void checkSelf() {
		if(POExceptionCodeENUM.MAX.ordinal() != POException.exceptionMessage.length) {
			throw new PORunTimeError("错误信息定义有问题,请核查代码,错误代码和错误信息必须匹配!");
		}
	}
}
