package com.ai.iot.jstormunify;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import com.ai.iot.bill.common.config.BaseConfig;
import com.ai.iot.bill.common.config.ConfigException;
import com.ai.iot.bill.common.config.ConfigFactory;
import com.ai.iot.bill.common.config.UniversalConstant;
import com.ai.iot.bill.common.param.BaseDefine;
import com.ai.iot.bill.common.paramcachemanager.core.adapter.ParamManagerAdapter;
import com.ai.iot.bill.common.util.CheckNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.ai.iot.bill.common.config.UniversalConstant.FETAL_ERROR_CONFIG_INIT_0;
import static com.ai.iot.bill.common.config.UniversalConstant.FETAL_ERROR_CONFIG_INIT_1;

public class JstormUnify {
    private final static Logger logger = LoggerFactory.getLogger(JstormUnify.class);
    //可以通过jstorm组件互相传的参数Map，包含了fileProperties、dbProperties、managerProperties三者。
    private static Map jstormConfMap = new HashMap();
    private static String topologyName = null;
    private static BaseConfig baseConfig = null;
    public static final BaseConfig initConfig(ConfigFactory.ModuleNameEnum moduleNameEnum, Class clazz, String cfgFile) {
        Logger logger = LoggerFactory.getLogger(clazz);
        ConfigFactory.getInstance();
        logger.info("##############JstormUnify::initConfig()#################");
        baseConfig = ConfigFactory.getModuleConfig(moduleNameEnum);
        if (CheckNull.isNull(baseConfig)) {
            logger.error(moduleNameEnum.toString() + FETAL_ERROR_CONFIG_INIT_0);
            return baseConfig;
        }

        try {
            ParamManagerAdapter paramManagerAdapter = new ParamManagerAdapter();
            baseConfig.init(cfgFile, BaseDefine.CONNCODE_MYSQL_PARAM, paramManagerAdapter);
        } catch (ConfigException e) {
            logger.error(moduleNameEnum.toString() + FETAL_ERROR_CONFIG_INIT_1);
            return baseConfig;
        }

        topologyName = clazz.getSimpleName();
        jstormConfMap = getConfMap(baseConfig);
        jstormConfMap.put(Config.TOPOLOGY_NAME, topologyName);
        jstormConfMap.put(Config.STORM_CLUSTER_MODE, "distributed");
        return baseConfig;
    }

    public static BaseConfig getBaseConfig() {
        return baseConfig;
    }

    private static Map getConfMap(BaseConfig baseConfig) {
        Map<String, Object> fileProperties = baseConfig.getFileProperties();
        Map<String, String> dbProperties = baseConfig.getDbProperties();
        Map<String, String> managerProperties = baseConfig.getManagerProperties();

        if (!CheckNull.isNull(fileProperties) && !UniversalConstant.universalDebugMode) {
            for (Map.Entry<String, Object> entry : fileProperties.entrySet()) {
                if (!jstormConfMap.containsKey(entry.getKey())) {
                    jstormConfMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (!CheckNull.isNull(dbProperties)) {
            for (Map.Entry<String, String> entry : dbProperties.entrySet()) {
                if (!jstormConfMap.containsKey(entry.getKey())) {
                    jstormConfMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (!CheckNull.isNull(managerProperties)) {
            for (Map.Entry<String, String> entry : managerProperties.entrySet()) {
                if (!jstormConfMap.containsKey(entry.getKey())) {
                    jstormConfMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return jstormConfMap;
    }

    public static Map getJstormConfMap() {
        return jstormConfMap;
    }

    public static String getTopologyName() {
        return topologyName;
    }

    public static void runLocalCluster(Map conf, TopologyBuilder builder) {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(JstormUnify.getTopologyName(), conf, builder.createTopology());
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("runLocalCluster={}", e);
        }
        cluster.killTopology(JstormUnify.getTopologyName());
        cluster.shutdown();
    }
}
