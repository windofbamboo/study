package com.ai.iot.bill.common.config;

import java.text.MessageFormat;

public class ConfigException extends Exception {
    private static final long serialVersionUID = 2017091313590000001L;
    @SuppressWarnings("unused")
    private ExceptionENUM errorCode = ExceptionENUM.SUCCESS;

    public  enum ExceptionENUM{
        SUCCESS,
        CONFIG_FILE_NOT_EXIST,
        CONFIG_DBNAME_IS_NULL,
        CONFIG_MANAGER_IS_NULL,
        CONFIG_FILE_IS_NOT_YAML;
    }
    protected static final String[] exceptionMessage = {
            "SUCCESS",
            "config file is needed, but does not exist.",
            "config file is not yaml format."
    };

    public ConfigException(ExceptionENUM eInt) {
        super(exceptionMessage[eInt.ordinal()]);
        this.errorCode=eInt;
    }
    public ConfigException(ExceptionENUM eInt,Object... args) {
        super(MessageFormat.format(exceptionMessage[eInt.ordinal()],args));
        this.errorCode=eInt;
    }
}
