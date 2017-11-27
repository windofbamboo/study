package com.ai.iot.bill.cb.base.entity;

import java.text.MessageFormat;

/**
 * 分拣的异常定义类
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class FilterException extends Exception {
    private static final long serialVersionUID = 2017080709580000001L;
    private ExceptionENUM errorCode = ExceptionENUM.SUCCESS;
    public  enum ExceptionENUM{
        SUCCESS,
        GET_DEVICE_INFO_FAILED,
        MDB_JC_IS_NULL,
        KAFKA_MQ_INIT_FAILED
    }
    protected static final String[] exceptionMessage = {
            "SUCCESS",
            "failed to get device info.",
            "mdb jc is null.",
            "kafka mq init failed."
    };

    public FilterException(ExceptionENUM eInt) {
        super(exceptionMessage[eInt.ordinal()]);
        this.errorCode=eInt;
    }
    public FilterException(ExceptionENUM eInt,Object... args) {
        super(MessageFormat.format(exceptionMessage[eInt.ordinal()],args));
        this.errorCode=eInt;
    }
}
