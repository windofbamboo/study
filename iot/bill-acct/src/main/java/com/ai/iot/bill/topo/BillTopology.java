package com.ai.iot.bill.topo;

import backtype.storm.Config;
import backtype.storm.topology.TopologyBuilder;
import com.ai.iot.bill.common.util.JStormUtil;
import com.ai.iot.bill.define.CommonEnum;
import com.ai.iot.bill.topo.acctTopo.AcctBolt;
import com.ai.iot.bill.topo.acctTopo.AcctSpout;
import com.ai.iot.bill.topo.billTopo.BillBolt;
import com.ai.iot.bill.topo.billTopo.BillSpout;
import com.ai.iot.bill.topo.controlTopo.ControlBolt;
import com.ai.iot.bill.topo.controlTopo.ControlSpout;
import com.ai.iot.bill.topo.deviceTopo.DeviceBolt;
import com.ai.iot.bill.topo.deviceTopo.DeviceSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

/**
 * @Author:       [geyf]
 * @CreateDate:   [2017/9/1 12:00:00]
 * @Version:      [v1.0]
 */
public class BillTopology {

  private static Logger logger = LoggerFactory.getLogger(BillTopology.class);
  private static final String STORM_CONFIG = "acct.yaml";
  private static Config acctConfig;

  public static void main(String[] args) throws Exception {
    acctConfig =JStormUtil.getConfig(STORM_CONFIG);
    //启动topology
    JStormUtil.runTopology(CommonEnum.TOPOLOGY_NAME, BillTopology.setBuilder());
    logger.info("AcctTopology submit successed, topology.spout.parallel only is one");
  }

  public static TopologyBuilder setBuilder() {

    LinkedHashMap spoutParallelism = (LinkedHashMap)acctConfig.get("topology.spout.parallelism");

    int acctSpoutParallel    = (Integer) spoutParallelism.get("AcctSpout");
    int billSpoutParallel    = (Integer) spoutParallelism.get("BillSpout");
    int deviceSpoutParallel    = (Integer) spoutParallelism.get("DeviceSpout");
    int controlSpoutParallel    = (Integer) spoutParallelism.get("ControlSpout");

    LinkedHashMap boltParallelism = (LinkedHashMap)acctConfig.get("topology.bolt.parallelism");
    int acctBoltParallel    = (Integer) boltParallelism.get("AcctBolt");
    int billBoltParallel    = (Integer) boltParallelism.get("BillBolt");
    int deviceBoltParallel  = (Integer) boltParallelism.get("DeviceBolt");
    int controlBoltParallel = (Integer) boltParallelism.get("ControlBolt");

    TopologyBuilder topologyBuilder = new TopologyBuilder();

    topologyBuilder.setSpout(CommonEnum.ACCT_SPOUT_NAME, new AcctSpout(),acctSpoutParallel);
    topologyBuilder.setBolt(CommonEnum.ACCT_BOLT_NAME, new AcctBolt(),acctBoltParallel).localOrShuffleGrouping(CommonEnum.ACCT_SPOUT_NAME);

    topologyBuilder.setSpout(CommonEnum.DEVICE_SPOUT_NAME, new DeviceSpout(),deviceSpoutParallel);
    topologyBuilder.setBolt(CommonEnum.DEVICE_BOLT_NAME, new DeviceBolt(),deviceBoltParallel).localOrShuffleGrouping(CommonEnum.DEVICE_SPOUT_NAME);

    topologyBuilder.setSpout(CommonEnum.CONTROL_SPOUT_NAME, new ControlSpout(),controlSpoutParallel);
    topologyBuilder.setBolt(CommonEnum.CONTROL_BOLT_NAME, new ControlBolt(),controlBoltParallel).localOrShuffleGrouping(CommonEnum.CONTROL_SPOUT_NAME);

    topologyBuilder.setSpout(CommonEnum.BILL_SPOUT_NAME, new BillSpout(),billSpoutParallel);
    topologyBuilder.setBolt(CommonEnum.BILL_BOLT_NAME, new BillBolt(),billBoltParallel).localOrShuffleGrouping(CommonEnum.BILL_SPOUT_NAME);

    return topologyBuilder;
  }



}
