package com.ai.iot.bill;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import com.ai.iot.bill.common.config.ConfigFactory;
import com.ai.iot.jstormunify.JstormUnify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import static com.ai.iot.bill.common.config.UniversalConstant.ParamLoadConst.PARAMLOAD_CFG_FILE;

/**
 * 参数加载的总拓架，提交设置及入口。
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class TopoParamLoad {
    private static Logger logger = LoggerFactory.getLogger(TopoParamLoad.class);
    public static final boolean paramLoadDebugMode = false;
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
        JstormUnify.initConfig(ConfigFactory.ModuleNameEnum.PARAM_LOAD, TopoParamLoad.class, args[0].isEmpty()?PARAMLOAD_CFG_FILE:args[0]);
        Map conf = JstormUnify.getJstormConfMap();
        Config.setNumAckers(conf, 1);
        conf.put(Config.TOPOLOGY_WORKERS, 1);
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(ParamLoaderSpout.class.getSimpleName(), new ParamLoaderSpout(), 1);
        if (paramLoadDebugMode) {
            JstormUnify.runLocalCluster(conf, builder);
            return;
        }

        logger.info("#################7777777777777############### topologyName:{}, conf size is:{}", JstormUnify.getTopologyName(), conf.size());
        StormSubmitter.submitTopology(JstormUnify.getTopologyName(), conf, builder.createTopology());
    }
}
