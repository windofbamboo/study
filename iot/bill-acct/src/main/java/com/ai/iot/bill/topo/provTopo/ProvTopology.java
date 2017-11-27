package com.ai.iot.bill.topo.provTopo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.JStormUtil;

import backtype.storm.topology.TopologyBuilder;


public class ProvTopology {
	
	private static Logger logger = LoggerFactory.getLogger(ProvTopology.class);
	private static final String STORM_CONFIG = "prov.yaml";
	private static final String TOPOLOGY_NAME = "TopoOutAcctLoad";
	private static final String PROV_SPOUT_NAME = "LoadSpout";
	private static final String PROV_BOLT_NAME = "LoadBolt";
	
	public static void main(String[] args) throws Exception {
		JStormUtil.getConfig(STORM_CONFIG);
		//启动topology
		JStormUtil.runTopology(TOPOLOGY_NAME, ProvTopology.setBuilder());
		logger.info("ProvTopology submit successed, topology.spout.parallel only is one");
	}
	
	public static TopologyBuilder setBuilder() {
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout(PROV_SPOUT_NAME, new ProvSpout());
		topologyBuilder.setBolt(PROV_BOLT_NAME, new ProvBolt()).localOrShuffleGrouping(PROV_SPOUT_NAME);
		return topologyBuilder;
	}
}
