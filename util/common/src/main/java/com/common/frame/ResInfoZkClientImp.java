package com.common.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.common.frame.zkpath.ZkOutput;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.frame.zkpath.ZkActiveValue;
import com.common.frame.zkpath.ZkFileTypeValue;
import com.common.util.BillException;
import com.common.util.Const;
import com.common.util.DateUtil;
import com.common.util.ZkWrapper;
import com.common.util.BillException.BillExceptionENUM;
import com.common.util.ZkWrapper.Type;
import com.common.util.ZkWrapper.ZkWatcherCallBack;
import com.common.util.ZkWrapper.ZkWatcherTypeCallBack;
import com.alibaba.fastjson.JSON;

public class ResInfoZkClientImp extends ResZkPathOperator implements ResInfoZkClient {
	private final Logger logger = LoggerFactory.getLogger(ResInfoZkClientImp.class);

	private ZkActiveValue zkActiveValueOwn = null;
	/** 模块的输入信息，map<file_type,map<dir_id,ZkOutput>> */
	private Map<String, Map<String, ZkOutput>> assignInputMap = null;
	private int suffixModuleInstanceNumber = -1;
	private boolean isSuffixModuleInstanceNumberWatcherAdded=false;
	private boolean isRegisterOk = false;
	/** 需保证线程安全 */
	private Map<String, Boolean> assignInputListWatcherOnOff = new ConcurrentHashMap<>();
	/** 需保证线程安全 */
	private volatile boolean suffixModuleInstanceNumberWatcherOnOff = false;
	//用于保存上次已经获取的输入任务相关的文件类型，通过监听定期刷新
	private BlockingQueue<String> fileTypeListForTask=new LinkedBlockingQueue<>();
	//因为未考虑zk操作的一致性，理论上fileTypeListForTask的文件类型不一定全部监听设置ok
	private BlockingQueue<String> pathListForListen=new LinkedBlockingQueue<>();

	public ResInfoZkClientImp(String zkConnectList, String zkRoot, Const.ModuleNameEnum moduleName, String channelId) {
		super(zkConnectList, zkRoot);
		this.moduleName = moduleName;
		this.channelId = channelId;
		zkActiveValueOwn = new ZkActiveValue(channelId);
	}
	///////////////////////////////////////////////////////////////////
	/// 业务模块
	///////////////////////////////////////////////////////////////////

	/**
	 * 注册了上述(1)\(2)节点,用于业务模块的注册，主要注册业务模块信息，以及zk连接的监听设置
	 * 
	 * @throws Exception
	 */
	public void register() throws Exception {
		if (isRegisterOk)
			return;
		String instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId,
				RootTypeEnum.ROOT_TYPE_REGISTER);
		String instanceActivePath = instanceRootPath + ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE;
		// 处理临时节点，必须保证是当前进程所属的临时节点
		doTempNodeForConcurrence(instanceActivePath);
		// 增加连接监听
		ZkActiveValue zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
		zkWrapper.setListenterConnect(new ConnectionStateListener() {
			private ZkActiveValue zkActiveValueOrg = null;

			// 如果是网络闪断的场景，必须进行重连并重建临时节点
			@Override
			public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
				if (zkActiveValueOrg == null) {
					zkActiveValueOrg = zkActiveValue;
				}
				logger.error("==============================================================================");
				logger.error("instanceActivePath={}", instanceActivePath);
				ZkActiveValue zkActiveValue;
				try {
					if (connectionState == ConnectionState.CONNECTED
							|| connectionState == ConnectionState.RECONNECTED) {
						logger.error("ConnectionState={}", ConnectionState.RECONNECTED);
						while (true) {
							try {
								zkWrapper.insertTempNode(instanceActivePath,
										JSON.toJSONString(new ZkActiveValue(channelId), isJsonValuePretty));
							} catch (NodeExistsException e) {
								logger.error("NodeExistsException={}", instanceActivePath, e);
							}
							try {
								zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath),
										ZkActiveValue.class);
								break;
							} catch (NoNodeException e) {
								logger.error("NoNodeException={}", instanceActivePath, e);
								zkActiveValue = null;
							}
						}
						if (!zkActiveValue.equals(zkActiveValueOrg)) {
							logger.error("CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
									+ instanceActivePath + ",new=" + zkActiveValue + ",old=" + zkActiveValueOrg);
							System.exit(999);
						} else {
							logger.error("CHANNEL INSTANCE BE RE-TAKED BY OWN PROCCESS!instanceActivePath="
									+ instanceActivePath + ",new=" + zkActiveValue + ",old=" + zkActiveValueOrg);
						}
					} else {
						logger.error("other ignored connectionState={}", connectionState);
					}
				} catch (Exception e) {
					logger.error("有异常发生：", e);
				}
			}
		});
		// 注册根目录
		zkWrapper.insertNode(instanceRootPath);
		isRegisterOk = true;
	}

	/**
	 * 用于业务模块的注销，注销仅仅是为了及时删除临时节点，请使用RunTime组件等级hook进行处理。
	 * 
	 * @throws Exception
	 */
	public void unRegister() {
		if (zkWrapper == null || !isRegisterOk) {// 异常或注册失败
			return;
		}

		String instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId,
				RootTypeEnum.ROOT_TYPE_REGISTER);
		String instanceActivePath = instanceRootPath + ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE;
		ZkActiveValue zkActiveValue;
		try {
			try {
				zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
			} catch (NoNodeException e) {
				logger.error("NoNodeException={}", instanceActivePath, e);
				zkActiveValue = null;
			}
			if (zkActiveValue != null && zkActiveValue.equals(new ZkActiveValue(channelId))) {
				logger.info("zkWrapper.deleteTempNode({})...", instanceActivePath);
				zkWrapper.deleteTempNode(instanceActivePath);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		isRegisterOk = false;
	}

	/**
	 * 业务模块注册输出信息，可以重复调用，用于全量刷新输出信息
	 * 
	 * @param zkRoot
	 * @param moduleName
	 * @param channelId
	 * @param zkFileTypeValues
	 *            必须是全量信息，即所有文件类型的所有普通输出和错误输出
	 * @throws Exception
	 */
	public void registerOutInfo(Map<String, ZkFileTypeValue> zkFileTypeValues) throws Exception {
		List<ZkOutput> zkOutputNews;
		List<ZkOutput> zkErrOutNews;
		String fileType;
		List<String> fileTypeOlds = getAllFileTypes(zkRoot, moduleName, channelId);
		for (ZkFileTypeValue zkFileTypeValue : zkFileTypeValues.values()) {
			fileType = zkFileTypeValue.getFileType();
			zkOutputNews = zkFileTypeValue.getZkOutputs();
			zkErrOutNews = zkFileTypeValue.getZkErrOuts();
			List<ZkOutput> zkOutputOlds = getOutputByFileType(zkRoot, moduleName, channelId, fileType);
			List<ZkOutput> zkErrOutOlds = getErrOutByFileType(zkRoot, moduleName, channelId, fileType);
			// 先处理output
			if (!zkOutputNews.isEmpty()) {
				if (!zkOutputOlds.isEmpty()) {
					modifyOutputByFileType(zkRoot, moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
				} else {
					addOutputByFileType(zkRoot, moduleName, channelId, fileType, zkOutputNews);
				}
			}
			// 处理errput
			if (!zkErrOutNews.isEmpty()) {
				if (!zkErrOutOlds.isEmpty()) {
					modifyOutputByFileType(zkRoot, moduleName, channelId, fileType, zkErrOutOlds, zkErrOutNews);
				} else {
					addOutputByFileType(zkRoot, moduleName, channelId, fileType, zkErrOutNews);
				}
			}
			if (zkOutputNews.isEmpty() && zkErrOutNews.isEmpty()) {
				deleteByFileType(zkRoot, moduleName, channelId, fileType);
			}
		}
		// 删除不需要的文件类型
		for (String fileTypeOld : fileTypeOlds) {
			if (!zkFileTypeValues.containsKey(fileTypeOld)) {
				deleteByFileType(zkRoot, moduleName, channelId, fileTypeOld);
			}
		}
	}

	/**
	 * 临时节点的注册处理
	 * 
	 * @param instanceActivePath
	 * @param channelId
	 * @throws Exception
	 */
	private void doTempNodeForConcurrence(String instanceActivePath) throws Exception {

		if (zkWrapper.isTempNodeExist(instanceActivePath)) {// 如果临时节点已经存在，需检查是否是自己创建的
			// 如果是其他进程，需等待进程退出后再判断
			ZkActiveValue zkActiveValue = null;
			long nowAbsSeconds = DateUtil.nowAbsSeconds();
			for (;;) {
				try {
					zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
				} catch (NoNodeException e) {// 别的进程已经退出
					break;
				}
				if (zkActiveValue != null && !zkActiveValue.equals(zkActiveValueOwn)) {
					if (DateUtil.nowAbsSeconds() - nowAbsSeconds >= 180) {// 说明其他的进程一直存活
						logger.error("CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
								+ instanceActivePath + ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn
								+ ",It maybe active,Now Current proccess will exit now!");
						System.exit(999);
					} else {
						logger.error("CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
								+ instanceActivePath + ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn
								+ ",Watching: Is it active...");
						Thread.sleep(3000);
					}
				} else {// 一般情况下，走不到这里
					break;
				}
			}
			logger.error("CHANNEL INSTANCE IS RE-TAKING ... ,instanceActivePath=" + instanceActivePath + ",zk="
					+ zkActiveValue + ",own=" + zkActiveValueOwn);
			// 需要更新一下
			try {
				zkWrapper.insertTempNode(instanceActivePath, JSON.toJSONString(zkActiveValueOwn, isJsonValuePretty));
			}catch (NodeExistsException e) {
				logger.error("PROCCESS ALREADY EXISTS!instanceActivePath="
						+ instanceActivePath + ",own=" + zkActiveValueOwn,e);
			}
			
			zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
			if (!zkActiveValue.equals(zkActiveValueOwn)) {
				logger.error("CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath="
						+ instanceActivePath + ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn);
				System.exit(999);
			} else {
				logger.error("CHANNEL INSTANCE BE RE-TAKED BY OWN PROCCESS!instanceActivePath=" + instanceActivePath
						+ ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn);
			}
			return;
		}
		// 不存在就更新临时节点
		try {
			zkWrapper.insertTempNode(instanceActivePath, JSON.toJSONString(zkActiveValueOwn, isJsonValuePretty));
		}catch (NodeExistsException e) {
			logger.error("PROCCESS ALREADY EXISTS!instanceActivePath=" + instanceActivePath +",own=" + zkActiveValueOwn,e);
		}
		// 再次核对是否自己进程注册
		ZkActiveValue zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
		if (!zkActiveValue.equals(zkActiveValueOwn)) {
			logger.error("CHANNEL INSTANCE BE ALREADY TAKED BY OTHER PROCCESS!instanceActivePath=" + instanceActivePath
					+ ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn);
			System.exit(999);
		} else {
			logger.error("CHANNEL INSTANCE BE RE-TAKED BY OWN PROCCESS!instanceActivePath=" + instanceActivePath
					+ ",zk=" + zkActiveValue + ",own=" + zkActiveValueOwn);
		}

	}

	/**
	 * 业务模块刷取输入信息,后置模块调用，输入信息已经由资源均衡模块设置
	 * @return 
	 * 
	 * @throws Exception
	 */
	public Map<String, Map<String, ZkOutput>> refreshAllTaskListBySuffixModule() throws Exception {
		if (zkWrapper == null) {
			return assignInputMap;
		}

		String instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId,
				RootTypeEnum.ROOT_TYPE_REGISTER);
		String inputListStr;
		List<ZkOutput> inputList;
		Map<String, ZkOutput> inputMapByFileType;

		if (assignInputMap == null) {// 仅运行一次
			fileTypeListForTask.addAll(getAllFileTypes(zkRoot, moduleName, channelId));
			// 注册输入信息文件类型的变化
			zkWrapper.setListenterChildrenNode(new ZkWatcherTypeCallBack() {
				@Override
				public void updateZkInfo(Type type, String node, String data) {
					switch (type) {
					case ADDED:
					case REMOVED:
						//文件类型发生了变化,考虑到异常，所以每次都是全量比对
						try {
							//同步最新的文件类型
							List<String> deleteFileTypeList=new ArrayList<>();
							List<String> addFileTypeList=new ArrayList<>();
							List<String> fileTypeList=getAllFileTypes(zkRoot, moduleName, channelId);
							for(String fileType:fileTypeList) {
								if(!fileTypeListForTask.contains(fileType)) {
									fileTypeListForTask.add(fileType);
									addFileTypeList.add(fileType);
								}
							}
							
							fileTypeListForTask.forEach(fileType -> {
								if(!fileTypeList.contains(fileType)) {
									deleteFileTypeList.add(fileType);
								}
							});
							fileTypeListForTask.removeAll(deleteFileTypeList);
							// 刷新注册输入信息同步的监听器
							for (String fileType : fileTypeListForTask) {
								if(pathListForListen.contains(fileType)) {
									continue;//已经增加过监听了
								}
								pathListForListen.add(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
								zkWrapper.setListenterNode(new ZkWatcherCallBack() {
									@Override
									public void updateZkInfo(String node, String data) {
										if (assignInputListWatcherOnOff.get(fileType) == false)
											assignInputListWatcherOnOff.put(fileType, true);
									}
								}, instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
							}
							//下面删除的可以考虑删除
							for (String fileType : pathListForListen) {
								if(!fileTypeListForTask.contains(fileType)) {
									zkWrapper.deleteListenterNode(fileType);
								}
							}
						} catch (Exception e) {
							logger.error("updateZkInfo({}) ... Skipped:",node,e); 
						}
						break;
					default:
						break;
					}
				}
			}, instanceRootPath);
			// 注册输入信息同步的监听器
			for (String fileType : fileTypeListForTask) {
				pathListForListen.add(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
				zkWrapper.setListenterNode(new ZkWatcherCallBack() {
					@Override
					public void updateZkInfo(String node, String data) {
						if (assignInputListWatcherOnOff.get(fileType)==null||
								assignInputListWatcherOnOff.get(fileType) == false)
							assignInputListWatcherOnOff.put(fileType, true);
					}
				}, instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
			}
			//
			assignInputMap = new HashMap<>();
			for (String fileType : fileTypeListForTask) {
				try {
					inputListStr = zkWrapper.getNodeValue(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType
							+ ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
				} catch (NoNodeException e) {
					inputListStr = null;
					continue;
				}
				inputList = JSON.parseArray(inputListStr, ZkOutput.class);
				for (ZkOutput zkOutput : inputList) {
					inputMapByFileType = assignInputMap.get(fileType);
					if (inputMapByFileType == null) {
						assignInputMap.put(fileType, new HashMap<>());
						inputMapByFileType = assignInputMap.get(fileType);
					}
					if (inputMapByFileType.containsKey(zkOutput.getDirId())) {
						throw new BillException(BillExceptionENUM.DUP_VALUE,
								",fileType=" + fileType + ",dirId=" + zkOutput.getDirId());
					}
					inputMapByFileType.put(zkOutput.getDirId(), zkOutput);
				
				}
			}
		} else {// 处理监听事件
			//获取监听事件
			Map<String, Boolean> assignInputListWatcherOnOffClone = new HashMap<>();
			assignInputListWatcherOnOffClone.putAll(assignInputListWatcherOnOff);
			assignInputListWatcherOnOff.clear();// 重置监听事件
			String fileType;
			//处理监听事件
			for (Map.Entry<String, Boolean> entry : assignInputListWatcherOnOffClone.entrySet()) {
				fileType = entry.getKey();
				inputMapByFileType = assignInputMap.get(fileType);
				if (inputMapByFileType != null) {
					inputMapByFileType.clear();
				}
				try {
					inputListStr = zkWrapper.getNodeValue(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType
							+ ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
				} catch (NoNodeException e) {
					inputListStr = null;
					continue;
				}
				inputList = JSON.parseArray(inputListStr, ZkOutput.class);
				for (ZkOutput zkOutput : inputList) {
					inputMapByFileType = assignInputMap.get(fileType);
					if (inputMapByFileType == null) {
						assignInputMap.put(fileType, new HashMap<>());
						inputMapByFileType = assignInputMap.get(fileType);
					}
					if (inputMapByFileType.containsKey(zkOutput.getDirId())) {
						throw new BillException(BillExceptionENUM.DUP_VALUE,
								",fileType=" + fileType + ",dirId=" + zkOutput.getDirId());
					}
					inputMapByFileType.put(zkOutput.getDirId(), zkOutput);
				}
			
			}
		}
		return assignInputMap;
	}

	/**
	 * 前置模块获取后置模块的实例个数，前置模块调用
	 * 
	 * @param suffixModuleName
	 *            后置模块名
	 * @throws Exception
	 */
	public int getSuffixModuleInstanceNumberByPrefixModule(Const.ModuleNameEnum suffixModuleName) throws Exception {
		if (zkWrapper == null) {
			return suffixModuleInstanceNumber;
		}
		if (!isSuffixModuleInstanceNumberWatcherAdded) {// 仅运行一次
			// 注册输入信息同步的监听器
			String moduleRootPath = getModuleRootPath(zkRoot, suffixModuleName, RootTypeEnum.ROOT_TYPE_REGISTER);
			zkWrapper.setListenterChildrenNode(new ZkWatcherTypeCallBack() {
				public void updateZkInfo(Type type, String node, String data) {
					suffixModuleInstanceNumberWatcherOnOff = true;
				}
			}, moduleRootPath);
			isSuffixModuleInstanceNumberWatcherAdded=true;
			suffixModuleInstanceNumber = getModuleInstanceNumber(zkRoot, suffixModuleName);
			return suffixModuleInstanceNumber;
		} else {
			if (suffixModuleInstanceNumberWatcherOnOff) {
				suffixModuleInstanceNumberWatcherOnOff = false;// 重置监听
				suffixModuleInstanceNumber = getModuleInstanceNumber(zkRoot, suffixModuleName);
				return suffixModuleInstanceNumber;
			}
		}
		return suffixModuleInstanceNumber;
	}

}
