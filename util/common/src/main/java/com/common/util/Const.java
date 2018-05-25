package com.common.util;

import java.nio.charset.Charset;

public class Const {

    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final Charset UNICODE = Charset.forName("UNICODE");

    ///conf目录名称
    public static final String SYSTEM_HOME="IO_HOME";
    public static final String SYSTEM_CONF="conf";
    public static final String PROPERTIES_KEY_SPLIT="_";//配置文件的key用下划线'_'分割,需去掉下划线后转换为内部变量名
    public static final String PROPERTIES_VALUE_SPLIT=",";

    //kvs连接符
    public static final String KEY_JOIN="+";
    public static final String VALUE_JOIN=",";
    public static final String VLINE="|";

    public static enum ModuleNameEnum{
    	MODULE_NAME_EDIRINFOSYNC("edirinfosync"),
    	MODULE_NAME_SSFTPGET("ssftpget"),
    	MODULE_NAME_SSFTPPUT("ssftpput"),
    	MODULE_NAME_ROAMDEAL("roamdeal"),
    	MODULE_NAME_RR("rr"),
    	MODULE_NAME_FSEND("fsend"),
    	MODULE_NAME_FRECEIVE("freceive"),
    	MODULE_NAME_GENHIGH("genhigh"),
    	MODULE_NAME_RESOURCEMANAGER("resourcemanager"),
    	MODULE_NAME_UNKNOWN("unknown");
    	
    	private ModuleNameEnum(String moduleName) {
    		this.moduleName=moduleName;
    	}
    	private String moduleName;
		public String getModuleName() {
			return moduleName;
		}
    }
    public static enum SSFtpGetOrPutEnum{
    	SSFTP_GET_FOR_FTP,
    	SSFTP_PUT_FOR_FTP,
    	SSFTP_GET_FOR_SFTP,
    	SSFTP_PUT_FOR_SFTP    	
    }
}
