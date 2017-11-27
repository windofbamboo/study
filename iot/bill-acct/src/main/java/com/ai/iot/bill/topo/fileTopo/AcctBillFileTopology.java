package com.ai.iot.bill.topo.fileTopo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.param.ParamBean;
import com.ai.iot.bill.common.util.JStormUtil;
import com.ai.iot.bill.common.util.ListUtil;
import com.ai.iot.bill.common.util.StringUtil;
import com.ai.iot.bill.dao.AcctBill2BssDao;

import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;


public class AcctBillFileTopology {
	
	private static Logger logger = LoggerFactory.getLogger(AcctBillFileTopology.class);
	private static final String STORM_CONFIG = "acctBillFile.yaml";
	private static final String TOPOLOGY_NAME = "TopoOutAcctFile";
	private static final String SPOUT_NAME = "FileSpout";
	private static final String BOLT_NAME = "FileBolt";
	//账单文件存储目录和临时目录
	private static final String PATH_PARAM_TYPE = "OUT_ACCT_FILE";
	
	public static void main(String[] args) throws Exception {
		JStormUtil.getConfig(STORM_CONFIG);
		//启动topology
		JStormUtil.runTopology(TOPOLOGY_NAME, AcctBillFileTopology.setBuilder());
		logger.info("AcctBillFileTopology submit successed, topology.spout.parallel only is one");
	}
	
	public static TopologyBuilder setBuilder() {
		Map<String, String> workerMap = new HashMap<>();
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout(SPOUT_NAME, new AcctBillFileSpout());
		BoltDeclarer bolt = topologyBuilder.setBolt(BOLT_NAME, new AcctBillFileBolt()).localOrShuffleGrouping(SPOUT_NAME);
		
		ParamBean paramBean = getFilePathConfig();
		if(paramBean == null){
			logger.error("The billing file path is not configured ");
			return null;
		}
		if(StringUtil.isEmpty(paramBean.getParamValue())){
			logger.error("The billing file path is configured to be empty ");
			return null;
		}
		if(StringUtil.isEmpty(paramBean.getParamValue2())){
			logger.error("The billing file tmp path is configured to be empty ");
			return null;
		}
		
		bolt.addConfiguration("filePath", paramBean.getParamValue());
		bolt.addConfiguration("tmpPath", paramBean.getParamValue2());
		
		workerMap.put("1_worker.hostname", paramBean.getParamName());
		workerMap.put("1_worker.component", BOLT_NAME+":1");
		workerMap.put("user.worker.num", "1");
		JStormUtil.defineWorderConfig(workerMap);
		
		return topologyBuilder;
	}
	
	public static ParamBean getFilePathConfig(){
		ParamBean paramBean = null;
		//获取账单文件存放目录和临时目录
		List<ParamBean> filePahtList = AcctBill2BssDao.getParamBeanList(PATH_PARAM_TYPE);
		if(ListUtil.isNotEmpty(filePahtList)){
			paramBean = filePahtList.get(0);
		}
		return paramBean;
	}
}
