package com.ai.iot.bill.common.config;

import com.ai.iot.bill.common.util.CheckNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigFactory {
    private final static Logger logger = LoggerFactory.getLogger(ConfigFactory.class);
    private static ConfigFactory instance;
    public static synchronized ConfigFactory getInstance() {
        logger.info("#############ConfigFactory::getInstance()#################");
        if (instance == null) {
            instance = new ConfigFactory();
            logger.info("#############ConfigFactory::getInstance()->init();#################");
            init();
        }
        return instance;
    }

    private static Map<String, BaseConfig> configMap;
    private static Map<String, BaseConfig.ConfigTypeSet> configTypeSetMap;
    private static void init() {
        configMap = new HashMap<>();
        configTypeSetMap = new HashMap<>();
        BaseConfig.ConfigTypeSet configTypeSet = new BaseConfig.ConfigTypeSet();
        configTypeSetMap.put(ModuleNameEnum.COMMON_CDR.toString(), configTypeSet);
        configTypeSetMap.put(ModuleNameEnum.PARAM_LOAD.toString(), configTypeSet);
        configTypeSetMap.put(ModuleNameEnum.INFO_LOAD.toString(), configTypeSet);
        configTypeSetMap.put(ModuleNameEnum.INFO_SYCHRONIZE.toString(), configTypeSet);

        BaseConfig.ConfigTypeSet filterConfigTypeSet = new BaseConfig.ConfigTypeSet();
        filterConfigTypeSet.setWithManagerConfig(false);
        configTypeSetMap.put(ModuleNameEnum.FILTER.toString(), filterConfigTypeSet);

        configTypeSetMap.put(ModuleNameEnum.REDO.toString(), configTypeSet);
        configTypeSetMap.put(ModuleNameEnum.RECYCLE.toString(), configTypeSet);
    }

    //各个模块名的枚举类型定义
    public static enum ModuleNameEnum {
        COMMON_CDR("COMMON-CDR"),
        PARAM_LOAD("PARAM-LOAD"),
        INFO_LOAD("INFO-LOAD"),
        INFO_SYCHRONIZE("INFO-SYCHRONIZE"),
        FILTER("FILTER"),
        REDO("REDO"),
        RECYCLE("RECYCLE");

        private String name;
        ModuleNameEnum(String s){
            name=s;
        }

        private static final Map<String, ModuleNameEnum> stringToEnum = new HashMap<String, ModuleNameEnum>();
        static {
            for(ModuleNameEnum moduleNameEnum : values()) {
                stringToEnum.put(moduleNameEnum.toString(), moduleNameEnum);
            }
        }

        public static ModuleNameEnum fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    //参数的来源类型，包括4种：代码中的常量定义，数据库参数中定义，参数管理器中定义，文件中读取。
    public static enum ParamSourceEnum {
        CONST("CONST"),
        MYSQL_PARAM("MYSQL-PARAM"),
        PARAM_NAMAGER_PARAM("PARAM-NAMAGER-PARAM"),
        FILE_PARAM("FILE-PARAM");

        private String name;
        ParamSourceEnum(String s){
            name=s;
        }

        private static final Map<String, ParamSourceEnum> stringToEnum = new HashMap<String, ParamSourceEnum>();
        static {
            for(ParamSourceEnum paramSourceEnum : values()) {
                stringToEnum.put(paramSourceEnum.toString(), paramSourceEnum);
            }
        }

        public static ParamSourceEnum fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static BaseConfig getModuleConfig(ModuleNameEnum moduleNameEnum) {
        BaseConfig baseConfig = configMap.get(moduleNameEnum.toString());
        if (!CheckNull.isNull(baseConfig)) {
            return baseConfig;
        }

        configMap.put(moduleNameEnum.toString(), new BaseConfig(configTypeSetMap.get(moduleNameEnum.toString())));
        return configMap.get(moduleNameEnum.toString());
    }
}
