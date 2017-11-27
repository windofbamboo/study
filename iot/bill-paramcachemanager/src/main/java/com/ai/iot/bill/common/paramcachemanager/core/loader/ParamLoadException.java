package com.ai.iot.bill.common.paramcachemanager.core.loader;

import java.text.MessageFormat;

/**
 * 参数加载的异常定义类
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/11 09:36]
 * @Version:      [v1.0]
 */
public class ParamLoadException extends Exception {
    private static final long serialVersionUID = 2017071114340000001L;
    @SuppressWarnings("unused")
    private ExceptionENUM errorCode = ExceptionENUM.SUCCESS;
    public  enum ExceptionENUM{
        SUCCESS,
        COMMIT_UPDATEFLAG_TOMDB_FAILED,
        CLEAN_OLD_DATA_FAILED,
        MDB_CLUSTER_IS_NULL,
        MDB_POG_INFO_IS_NULL;
    }
    protected static final String[] exceptionMessage = {
            "SUCCESS",
            "commit update flag to mdb failed.",
            "clean old data failed.",
            "mdb cluster is null.",
            "mdb pog info is null."
    };

    public ParamLoadException(ExceptionENUM eInt) {
        super(exceptionMessage[eInt.ordinal()]);
        this.errorCode=eInt;
    }
    public ParamLoadException(ExceptionENUM eInt,Object... args) {
        super(MessageFormat.format(exceptionMessage[eInt.ordinal()],args));
        this.errorCode=eInt;
    }

}
