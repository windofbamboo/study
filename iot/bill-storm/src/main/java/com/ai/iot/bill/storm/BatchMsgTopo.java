package com.ai.iot.bill.storm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.param.BaseParamDao;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
/**   
 * Topo基类
 * @Description:  [Topo基类]
 * @Author:       [xulr]   
 * @CreateDate:   [2017.9.14 16:54]   
 * @Version:      [v1.0]
 */
public abstract class BatchMsgTopo {
	private static Logger logger = LoggerFactory.getLogger(BatchMsgTopo.class);
	
	/**
	*  运行topo
	*  @param topoName topo名称，不能为空
	*  @param args 运行参数，若参数中带"local"，则表示本地集群模式，否则为分布式集群模式
	*  @since  1.0
	*/
	public void run(String topoName,String[] args) throws Exception {
		Map<String,Object> conf = new HashMap<>();
		
		//设置参数
		if(topoName!=null && !topoName.isEmpty()){
        	getConf(topoName,conf);
        	
        	TopologyBuilder builder = new TopologyBuilder();
    		if(setBuilder(builder,conf)){  
    			String mode = getMode(args);
    			
	    		//提交拓扑架
	            if("local".equalsIgnoreCase(mode)){
		        	//本地模式
	            	conf.put(Config.STORM_CLUSTER_MODE, "local");
		        	conf.put("topology.enable.metrics", false);
		        	LocalCluster cluster = new LocalCluster();
		        	cluster.submitTopology(topoName, conf, builder.createTopology());
		        	logger.info("submitTopology local finished:{}",topoName);
		        	Thread.sleep(getLocalRunMs());
		            cluster.shutdown();
		        }else{
		        	conf.put(Config.STORM_CLUSTER_MODE, "distributed");
		        	StormSubmitter.submitTopology(topoName, conf, builder.createTopology());
		        	logger.info("submitTopology distributed finished:{}",topoName);
		        }		        
    		}
        }else{
	        logger.info("Topology name is null.");
        }
	}
	
	/**
	*  获取运行模式
	*  @param args 若参数中带"local"，则表示本地集群模式，否则为分布式集群模式
	*  @since  1.0
	*/
	public static String getMode(String[] args){
		String mode = "distributed";
		
		if(args!=null){
			for(String arg:args){
				if("local".equalsIgnoreCase(arg)){
					return "local";
				}
			}
		}
		
		return mode;
	}
     
	/**
	*  本地集群模式时，程序运行时长(ms)
	*  @return 本地模式运行时长(ms)
	*  @since  1.0
	*/
    protected long getLocalRunMs(){    	
    	return 600000;
    }
    
    /**
	*  根据topo名从数据库td_b_param获取该topo参数配置
	*  @param topoName topo名
	*  @param conf 参数配置
	*  @since  1.0
	*/
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void getConf(String topoName,Map conf){
    	if(topoName !=null && !topoName.isEmpty()){    		
    		BaseParamDao.getConf(conf, topoName);
    		conf.put(Config.TOPOLOGY_NAME, topoName);
    	}
    }
    
    /**
     * 设置ack线程数，如为0则不使用ack
     * @param conf 配置文件
     * @param numbers ack线程数
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setAckNumbers(Map conf,int numbers){
    	conf.put(Config.TOPOLOGY_ACKER_EXECUTORS, numbers);
    }
	
    /**
	*  构建topo
	*  @param builder topo构建对象
	*  @param conf 参数配置
	*  @since  1.0
	*/
	@SuppressWarnings("rawtypes")
	protected abstract boolean setBuilder(TopologyBuilder builder,Map conf);

}
