package com.ai.iot.bill.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shade.storm.org.apache.commons.lang.StringUtils;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.KillOptions;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Iface;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.NimbusClientWrapper;
import backtype.storm.utils.StormObject;
import backtype.storm.utils.Utils;

import com.alibaba.jstorm.client.ConfigExtension;
import com.alibaba.jstorm.client.WorkerAssignment;
import com.alibaba.jstorm.cluster.Cluster;
import com.alibaba.jstorm.cluster.StormConfig;
import com.alibaba.jstorm.cluster.StormZkClusterState;
import com.alibaba.jstorm.schedule.Assignment;
import com.alibaba.jstorm.task.error.TaskError;
import com.alibaba.jstorm.utils.JStormUtils;
import com.alibaba.jstorm.utils.LoadConf;

/**
 * JStorm集群或本机模式的操作封装工具类 使用方法: JStormUtil.getConfig(configureFile);
 * JStormUtil.runTopology("topname"); JStormUtil.killTopology("topname");
 * JStormUtil.cleanCluster(); 判断是否本地模式: JStormUtil.isLocalMode(),一般不需要使用 工具类方法:
 * JStormUtil.loadConfTool(configureFile);
 * 
 * 增加了新配置对象的操作支持,不仅仅限制于全局一个
 */
public final class JStormUtil {
	private static JStormUtilImp jstormUtilImp = new JStormUtilImp();

	/** 返回全局的对象 */
	private static JStormUtilImp globalInstance() {
		return jstormUtilImp;
	}

	/** 返回新的配置对象 */
	public static JStormUtilImp newInstance() {
		return new JStormUtilImp();
	}

	/** 可以加载yaml和properties文件 */
	public static Map<?, ?> loadConfTool(String configureFileName) {
		return JStormUtilImp.loadConfTool(configureFileName);
	}

	/**
	 * 获取配置信息
	 * 
	 * @param configureFileName
	 *            配置文件名,请勿带路径信息
	 */
	public static Config getConfig(String configureFileName) {
		return globalInstance().getConfig(configureFileName);
	}

	/**
	 * 本地还是集群来自配置文件: storm.cluster.mode : local or distributed
	 * 本地模式时日志在资源目录下的jstorm.log4j.local.properties文件决定 配置改成来自文件,不再写死.在yaml配置里边新增:
	 * [local]: jstorm.log.dir\topology.name\logfile.name\storm.cluster.mode
	 * [distributed]: storm.cluster.mode
	 */
	public static void runTopology(String topologyName, TopologyBuilder topologyBuilder) throws Exception {
		globalInstance().runTopology(topologyName, topologyBuilder);
	}

	/**
	 * 仅仅是删除指定的topology
	 */
	public static void killTopology(String topologyName) throws Exception {
		globalInstance().killTopology(topologyName);
	}

	/**
	 * 本地模式,直接shutdown 集群模式,只是清理topology
	 */
	public static void cleanCluster() throws Exception {
		globalInstance().cleanCluster();
	}

	/** 是否是本地模式 */
	public static boolean isLocalMode() {
		return globalInstance().isLocalMode();
	}

	/** 用户自定义资源调度 */
	public static Config defineWorderConfig(Map<String, String> workerMap) {
		return globalInstance().defineWorderConfig(workerMap);
	}

	/** 返回任务分配信息,即task和主机+端口的关系,task分配到哪个主机哪个端口 */
	public static Assignment getAssignmentInfo(String topologyName) throws Exception {
		return globalInstance().getAssignmentInfo(topologyName);
	}

	/** 返回supervisor的主机列表 */
	public static List<SupervisorSummary> getSupervisorHosts() throws Exception {
		return globalInstance().getSupervisorHosts();
	}

	/** 根据top名字打印具体的任务处理的错误信息 */
	public static void dumpAllTaskErrorByTopologyName(String topologyName) throws Exception {
		globalInstance().dumpAllTaskErrorByTopologyName(topologyName);
	}

	public static class JStormUtilImp {
		private final static Logger logger = LoggerFactory.getLogger(JStormUtilImp.class);
		/// 本地集群的日志配置文件
		private final String LOCAL_LOGGER_PROPERTIES_FILE = "jstorm.log4j.local.properties";
		/// 本地集群,全局一个就够了
		private LocalCluster localCluster = null;
		/// nimbus客户端,用于集群管理
		private volatile NimbusClientWrapperWithRemote clientNimbus = null;
		/// 配置对象
		private Config config = null;
		/// 配置对象对应的文件,如果文件不同,会清空配置项,并重新加载配置
		private String configureFileName = "";
		/// 集群状态
		private StormZkClusterState state = null;

		/**不同于NimbusClientWrapper,增加远程发布的功能*/
		public static class NimbusClientWrapperWithRemote implements StormObject {
			private static final Logger LOG = LoggerFactory.getLogger(NimbusClientWrapper.class);
		    private final AtomicBoolean isValid = new AtomicBoolean(true);

		    Iface client;
		    NimbusClient remoteClient;
		    @SuppressWarnings("rawtypes")
			Map conf;
		    boolean isLocal = false;

		    @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
		    public void init(Map conf) throws Exception {
		        this.conf = conf;
		        isLocal = StormConfig.try_local_mode(conf);

		        if (isLocal) {
		            client = LocalCluster.getInstance().getLocalClusterMap().getNimbus();
		        } else {
		            if(conf.get(Config.NIMBUS_HOST) != null) {
		                String nimbusHost=(String)conf.get(Config.NIMBUS_HOST);
	                    int port=JStormUtils.parseInt(conf.get(Config.NIMBUS_THRIFT_PORT),6627);
	                    LOG.info("nimbusHost={},nimbusPort={}",nimbusHost,port);
	                    Map clientConf = Utils.readStormConfig();
                        clientConf.putAll(this.conf);
	                    remoteClient = new NimbusClient(clientConf,nimbusHost,port);
		            }else {
		                LOG.info("nimbusHost unconfigured,MUST BE to submit topology at nimbus server!!!nimbusHost={},nimbusPort={}",Config.NIMBUS_HOST,Config.NIMBUS_THRIFT_PORT);
		                LOG.info("Guess this server is a nimbus server , And Attemp to submit topology...");
		                Map clientConf = Utils.readStormConfig();
	                    clientConf.putAll(this.conf);
	                    remoteClient = NimbusClient.getConfiguredClient(clientConf);	                    
		            }
		        	
		            
		            client = remoteClient.getClient();
		        }
		        isValid.set(true);
		    }

		    public void invalidate() {
		        isValid.set(false);
		    }

		    public boolean isValid() {
		        return this.isValid.get();
		    }

		    @Override
		    public void cleanup() {
		        invalidate();
		        if (remoteClient != null) {
		            remoteClient.close();
		        }
		    }

		    public Iface getClient() {
		        return client;
		    }

		    public void reconnect() {
		        cleanup();
		        try {
		            init(this.conf);
		        } catch (Exception ex) {
		            LOG.error("reconnect error, maybe nimbus is not alive.");
		        }
		    }
		}
		
		/** 可以加载yaml和properties文件 */
		public static Map<?, ?> loadConfTool(String configureFileName) {
			if (configureFileName.endsWith("yaml")) {
				return LoadConf.LoadYaml(configureFileName);
			} else {
				return LoadConf.LoadProperty(configureFileName);
			}
		}

		/**
		 * 获取配置信息
		 * 
		 * @param configureFileName
		 *            配置文件名,请勿带路径信息
		 */
		@SuppressWarnings("unchecked")
		public Config getConfig(String configureFileName) {
			synchronized (JStormUtil.class) {
				if (CheckNull.isNull(config)) {
					this.configureFileName = configureFileName;
					config = new Config();
				} else {// 配置文件不同,重新加载配置
					if (!this.configureFileName.equals(configureFileName)) {
						this.configureFileName = configureFileName;
						config = new Config();
					}
				}
			}
			if (configureFileName == null || configureFileName.isEmpty()) {
				return config;
			}

			if (StringUtils.isBlank(configureFileName)) {
				return config;
			}

			Map<?, ?> conf = JStormUtilImp.loadConfTool(configureFileName);
			config.putAll((Map<? extends String, ? extends Object>) conf);
			if (config.get(Config.TOPOLOGY_WORKERS) == null) {
				config.setNumWorkers(1);
			}
			logger.info("JStormConfig-your={}",JSONUtil.prettyFormatJson(JSONUtil.toJson(config)));
			@SuppressWarnings("rawtypes")
            Map clientConf = Utils.readStormConfig();
            clientConf.putAll(config);
            logger.info("JStormConfig-your+default(before submit topology)={}",JSONUtil.prettyFormatJson(JSONUtil.toJson(clientConf)));
			return config;
		}

		/** 返回配置对象 */
		public Config getConfig() {
			return config;
		}

		private void runTopologyLocally(String topologyName, TopologyBuilder topologyBuilder) throws Exception {
			// 本地环境加载log4j.properties配置文件
			loadLocalLogProperties();
			synchronized (JStormUtil.class) {
				if (CheckNull.isNull(localCluster)) {
					localCluster = new LocalCluster();
				}
				// if(CheckNull.isNull(state)) {
				// state = new StormZkClusterState(config);
				// }
			}

			localCluster.submitTopology(topologyName, config, topologyBuilder.createTopology());
			// JStormUtils.sleepMs(999999);
			// localCluster.killTopology(topologyName);
		}

		@SuppressWarnings("unchecked")
        private void runTopologyRemotely(String topologyName, TopologyBuilder topologyBuilder) throws Exception {
			StormSubmitter.submitTopology(topologyName, config, topologyBuilder.createTopology());
			getClientNimbus();
			synchronized (JStormUtil.class) {
				if (CheckNull.isNull(state)) {
				    @SuppressWarnings("rawtypes")
                    Map clientConf = Utils.readStormConfig();
                    clientConf.putAll(config);
					state = new StormZkClusterState(clientConf);
				}
			}
		}

		/**
		 * 本地还是集群来自配置文件: storm.cluster.mode : local or distributed
		 * 本地模式时日志在资源目录下的jstorm.log4j.local.properties文件决定 配置改成来自文件,不再写死.在yaml配置里边新增:
		 * [local]: jstorm.log.dir\topology.name\logfile.name\storm.cluster.mode
		 * [distributed]: storm.cluster.mode
		 */
		public void runTopology(String topologyName, TopologyBuilder topologyBuilder) throws Exception {
			if (isLocalMode()) {
				runTopologyLocally(topologyName, topologyBuilder);
			} else {
				runTopologyRemotely(topologyName, topologyBuilder);
			}
		}

		/**
		 * 仅仅是删除指定的topology
		 */
		public void killTopology(String topologyName) throws Exception {
			if (isLocalMode()) {
				if (CheckNull.isNull(localCluster)) {
					throw new Exception("local cluster not running!");
				}
				localCluster.killTopology(topologyName);
			} else {
				KillOptions killOption = new KillOptions();
				killOption.set_wait_secs(1);
				getClientNimbus().getClient().killTopologyWithOpts(topologyName, killOption);
			}
		}

		/**
		 * 本地模式,直接shutdown 集群模式,只是清理topology
		 */
		public void cleanCluster() throws Exception {
			if (isLocalMode()) {
				if (CheckNull.isNull(localCluster)) {
					return;
				}
				localCluster.shutdown();
				synchronized (JStormUtil.class) {
					localCluster = null;
				}
				logger.info("Successfully local cluster shutdown!");
				return;
			}
			if (CheckNull.isNull(clientNimbus)) {
				return;
			}
			try {
				getClientNimbus();
				ClusterSummary clusterSummary = clientNimbus.getClient().getClusterInfo();
				List<TopologySummary> topologySummaries = clusterSummary.get_topologies();

				KillOptions killOption = new KillOptions();
				killOption.set_wait_secs(1);
				for (TopologySummary topologySummary : topologySummaries) {
					clientNimbus.getClient().killTopologyWithOpts(topologySummary.get_name(), killOption);
					logger.info("Successfully kill topology ==>" + topologySummary.get_name());
				}
				logger.info("Successfully kill all topology Done!");
			} catch (Exception e) {
				if (clientNimbus != null) {
					clientNimbus.cleanup();
					synchronized (JStormUtil.class) {
						clientNimbus = null;
					}
				}
				logger.error("Failed to kill all topology : {}", e);
			}
		}

		/** 是否是本地模式 */
		public boolean isLocalMode() {
			if (CheckNull.isNull(config)) {
				return false;
			}
			String mode = (String) (config.get(Config.STORM_CLUSTER_MODE));
			if (mode != null) {
				if (mode.equalsIgnoreCase("local")) {
					return true;
				}
			}
			return false;
		}

		private NimbusClientWrapperWithRemote getClientNimbus() throws Exception {
			if (!CheckNull.isNull(clientNimbus)) {
				return clientNimbus;
			}
			synchronized (JStormUtil.class) {
				if (CheckNull.isNull(clientNimbus)) {
					clientNimbus = new NimbusClientWrapperWithRemote();
					clientNimbus.init(config);
				}
			}
			return clientNimbus;
		}

		/** 用户自定义资源调度  */
		public Config defineWorderConfig(Map<String, String> workerList){
			List<WorkerAssignment> userDefines = new ArrayList<WorkerAssignment>();
			Map<String, String> workerMap = new HashMap<String, String>();
			Map<String, String> spoutMap = new HashMap<String, String>();
			Map<String, String> boltMap = new HashMap<String, String>();
			List<String> workerArray = new ArrayList<String>();
			for(Map.Entry<String, String> param : workerList.entrySet()){
				if(param.getKey().endsWith("_spout.parallelism")){
					spoutMap.put(param.getValue().split(":")[0], param.getValue().split(":")[1]);
				}else if(param.getKey().endsWith("_bolt.parallelism")){
					boltMap.put(param.getValue().split(":")[0], param.getValue().split(":")[1]);
				}else{
					if(param.getKey().endsWith("_worker.hostname") && !param.getValue().isEmpty()){
						workerArray.add(param.getKey().split("_")[0]);
					}
					workerMap.put(param.getKey(), param.getValue());
				}
			}
			if(!workerMap.isEmpty()){
				if(workerMap.get("user.worker.num")!=null){
					config.setNumWorkers(JStormUtils.parseInt(workerMap.get("user.worker.num"), 0));//定义worker数
				}
				
				if(workerArray.size()>0){
					for(int i=0; i<workerArray.size(); i++){
						String k = workerArray.get(i);
						WorkerAssignment worker = new WorkerAssignment();
						String hostName = workerMap.get(k+"_worker.hostname");
						int port = JStormUtils.parseInt(this.defaultValue(workerMap.get(k+"_worker.port")));
						int cpu = JStormUtils.parseInt(this.defaultValue(workerMap.get(k+"_worker.cpu")));
						long mem = JStormUtils.parseInt(this.defaultValue(workerMap.get(k+"_worker.mem")));
						String jvm = workerMap.get(k+"_worker.jvm");
						String component = workerMap.get(k+"_worker.component");
						if(StringUtils.isNotEmpty(hostName)){
							worker.setHostName(hostName.trim());
						}
						if(port>0){
							worker.setPort(port);
						}
						if(cpu>0){
							worker.setCpu(cpu);
						}
						if(mem>0){
							worker.setMem(mem);
						}
						if(StringUtils.isNotEmpty(jvm)){
							worker.setJvm(jvm.trim());
						}
						if(StringUtils.isNotEmpty(component)){
							String[] components = component.trim().split(";");
							if(components.length>0){
								for(int j=0;j<components.length; j++){
									String[] componentInfo = components[j].trim().split(":");
									worker.addComponent(componentInfo[0].trim(), Integer.parseInt(componentInfo[1].trim()));
								}
							}
						}
						userDefines.add(worker);
					}
					ConfigExtension.setUserDefineAssignment(config, userDefines);
					ConfigExtension.setUseOldAssignment(config, false);
					ConfigExtension.setTaskOnDifferentNode(config, false);
				}
				config.put("topology.spout.parallelism", spoutMap);
				config.put("topology.bolt.parallelism", boltMap);
			}
			return config;
		}
		
		private String defaultValue(String val){
			if(null==val || "".equals(val.trim()) || "null".equalsIgnoreCase(val.trim())){
				return "0";
			}else{
				return val.trim();
			}
		}

		private void loadLocalLogProperties() {
			// Properties properties = new Properties();
			// properties.put("log4j.rootLogger", "INFO,console");
			// properties.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
			// properties.put("log4j.appender.console.Encoding", Const.UTF8);
			// properties.put("log4j.appender.console.Threshold", "DEBUG");
			// properties.put("log4j.appender.console.ImmediateFlush", "true");
			// properties.put("log4j.appender.console.Target", "System.out");
			// properties.put("log4j.appender.console.layout",
			// "org.apache.log4j.PatternLayout");
			// properties.put("log4j.appender.console.layout.ConversionPattern", "%d - %c
			// -%-4r [%t] %-5p %x - %m%n");
			PropertyConfigurator.configure(PropertiesUtil.getProperties(LOCAL_LOGGER_PROPERTIES_FILE));
		}

		/** 返回任务分配信息,即task和主机+端口的关系,task分配到哪个主机哪个端口 */
		public Assignment getAssignmentInfo(String topologyName) throws Exception {
			if (state == null)
				return null;
			String topologyId = getClientNimbus().getClient().getTopologyId(topologyName);
			return state.assignment_info(topologyId, null);
		}

		/** 返回supervisor的主机列表 */
		public List<SupervisorSummary> getSupervisorHosts() throws Exception {
			// List<String> hosts = new ArrayList<>();
			ClusterSummary clusterSummary = getClientNimbus().getClient().getClusterInfo();
			List<SupervisorSummary> supervisorSummaries = clusterSummary.get_supervisors();
			Collections.sort(supervisorSummaries, new Comparator<SupervisorSummary>() {
				// 根据剩余未分配任务数倒序排列
				@Override
				public int compare(SupervisorSummary o1, SupervisorSummary o2) {
					int o1Left = o1.get_numWorkers() - o1.get_numUsedWorkers();
					int o2Left = o2.get_numWorkers() - o2.get_numUsedWorkers();
					return o1Left - o2Left;
				}
			});

			// for (SupervisorSummary supervisorSummary : supervisorSummaries) {
			// hosts.add(supervisorSummary.get_host());
			// }
			return supervisorSummaries;
		}

		/** 根据top名字打印具体的任务处理的错误信息 */
		public void dumpAllTaskErrorByTopologyName(String topologyName) throws Exception {
			if (state == null)
				return;
			String topologyId = getClientNimbus().getClient().getTopologyId(topologyName);

			for (Map.Entry<Integer, List<TaskError>> entry : Cluster.get_all_task_errors(state, topologyId)
					.entrySet()) {
				Integer taskId = entry.getKey();
				List<TaskError> errorList = entry.getValue();
				for (TaskError error : errorList) {
					logger.error("taskId={},errorList={}", taskId, error.toString());
					// if (ErrorConstants.ERROR.equals(error.getLevel())) {
					// System.out.println(taskId + " occur error:" + error.getError());
					// } else if (ErrorConstants.FATAL.equals(error.getLevel())) {
					// System.out.println(taskId + " occur error:" + error.getError());
					// }
				}
			}
		}
	}

}
