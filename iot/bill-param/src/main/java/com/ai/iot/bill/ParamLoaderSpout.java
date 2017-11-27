package com.ai.iot.bill;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import com.ai.iot.bill.common.config.UniversalConstant;
import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoadException;
import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoader;
import com.ai.iot.bill.common.paramcachemanager.core.loader.ParamLoaderConfigure;
import com.ai.iot.bill.common.util.CheckNull;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.jstorm.utils.JStormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 参数加载的Spout 运行参数加载器
 * @Author:       [zhangrui]
 * @CreateDate:   [2017/7/12 09:36]
 * @Version:      [v1.0]
 */
public class ParamLoaderSpout implements IRichSpout {
    private static Logger logger = LoggerFactory.getLogger(ParamLoaderSpout.class);
    private static ParamLoader paramLoader;
    private static ParamLoaderConfigure paramLoaderConfigure = new ParamLoaderConfigure();;
    private static boolean finish = false;
    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        paramLoaderConfigure.setScanSleepSeconds(UniversalConstant.ParamLoadConst.SCAN_SLEEP_SECONDS);
        if (CheckNull.isNull(paramLoader)) {
            try {
                paramLoader = new ParamLoader(paramLoaderConfigure);
            } catch (Exception e) {
                logger.error("ParamLoaderSpout::paramLoader create failed. " + e.getMessage());
            }
        }

        if(CheckNull.isNull(paramLoader)) {
            logger.info("############paramLoader is null.###############");
        }

        //JSON序列化时必须的初始化操作
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {
        try {
            paramLoader.run();
            JStormUtils.sleepMs(paramLoaderConfigure.getScanSleepSeconds()*1000L);
            logger.info("##########ParamLoaderSpout::nextTuple() sleep {} seconds. ##########", paramLoaderConfigure.getScanSleepSeconds());
        } catch (InterruptedException e) {
            logger.error("ParamLoaderSpout::nextTuple() InterruptedException occured.");
            Thread.currentThread().interrupt();
        } catch (ParamLoadException e) {
            logger.error("ParamLoaderSpout::nextTuple() ParamLoadException occured.");
        } catch (InvocationTargetException e) {
            logger.error("ParamLoaderSpout::nextTuple() InvocationTargetException occured.");
        } catch (IllegalAccessException e) {
            logger.error("ParamLoaderSpout::nextTuple() IllegalAccessException occured.");
        } catch (ClassNotFoundException e) {
            logger.error("ParamLoaderSpout::nextTuple() ClassNotFoundException occured.");
        } catch (InstantiationException e) {
            logger.error("ParamLoaderSpout::nextTuple() InstantiationException occured.");
        }
        finish = true;
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
