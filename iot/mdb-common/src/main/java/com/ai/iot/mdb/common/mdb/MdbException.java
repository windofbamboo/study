package com.ai.iot.mdb.common.mdb;

public class MdbException extends Exception {
    private static final long serialVersionUID = 2017080914400000001L;
    //private int errorCode = 0;
    public static final int GET_REDIS_CONNECT_FAILED = 1;
    public static final String GET_REDIS_CONNECT_FAILED_DESC = "初始化建立REDIS连接时失败";

    public MdbException(String message, int errorCode) {
        super(message);
        //this.errorCode = errorCode;
    }
}
