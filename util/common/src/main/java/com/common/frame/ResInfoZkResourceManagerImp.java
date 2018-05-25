package com.common.frame;

import java.util.List;

import com.common.frame.zkpath.ZkActiveValue;
import com.common.util.Const;
import com.common.util.ZkWrapper;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class ResInfoZkResourceManagerImp extends ResZkPathOperator implements ResInfoZkResourceManager {
	private final Logger logger = LoggerFactory.getLogger(ResInfoZkResourceManagerImp.class);

	private ZkActiveValue zkActiveValueOwn = null;
	private boolean isRegisterOk = false;

	/** 需保证线程安全 */
	private volatile boolean suffixModuleInstanceChangedWatcherOnOff = false;
	private volatile boolean prevModuleOutInfoChangedWatcherOnOff = false;

	private boolean isSuffixModuleInstanceChangedWatcherAdded = false;
	private boolean isPrevModuleOutInfoChangedWatcherAdded = false;
	
	public ResInfoZkResourceManagerImp(String zkConnectList, String zkRoot) {
		super(zkConnectList, zkRoot);
		this.moduleName = Const.ModuleNameEnum.MODULE_NAME_RESOURCEMANAGER;
		this.channelId = "";
		zkActiveValueOwn = new ZkActiveValue(channelId);
	}

	///////////////////////////////////////////////////////////////////
	/// 资源均衡管理模块
	///////////////////////////////////////////////////////////////////
	/**
	 * 检测自己是否是主 检测是否是自己进程创建的临时节点 0=自己不是主，1=自己是主，-1=不存在,-2=异常
	 */
	public int judgeMasterState() {
		if (zkWrapper == null) {
			return -2;
		}
		String instanceRootPath = getModuleInstanceRootPath(zkRoot, Const.ModuleNameEnum.MODULE_NAME_RESOURCEMANAGER,
				"", RootTypeEnum.ROOT_TYPE_RESOURCEMANAGER);
		String instanceActivePath = instanceRootPath + ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE;
		ZkActiveValue zkActiveValue = null;
		try {
			zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
		} catch (NoNodeException e) {
			logger.error("NoNodeException={}", instanceActivePath, e);
			zkActiveValue = null;
			return -1;
		} catch (Exception e) {
			logger.error("Exception={}", instanceActivePath, e);
			return -2;
		}
		if (zkActiveValue != null && zkActiveValue.equals(zkActiveValueOwn)) {
			logger.info("My Own Proccess is MASTER:{}", instanceActivePath);
			return 1;
		} else if (zkActiveValue != null && !zkActiveValue.equals(zkActiveValueOwn)) {
			logger.info("My Own Proccess is not MASTER:master={},own={}", instanceActivePath, zkActiveValueOwn);
			return 0;
		} else {
			logger.info("Not Exist And Unknown MASTER:{}", instanceActivePath, zkActiveValueOwn);
			return -1;
		}
	}

	/**
	 * 将自己设置为主
	 * 
	 * @throws Exception
	 */
	public boolean register() throws Exception {
		if (isRegisterOk)
			return true;
		String instanceRootPath = getModuleInstanceRootPath(zkRoot, Const.ModuleNameEnum.MODULE_NAME_RESOURCEMANAGER,
				"", RootTypeEnum.ROOT_TYPE_RESOURCEMANAGER);
		String instanceActivePath = instanceRootPath + ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE;
		zkWrapper.insertTempNode(instanceActivePath, JSON.toJSONString(zkActiveValueOwn, isJsonValuePretty));
		isRegisterOk = judgeMasterState() == 1;
		return isRegisterOk;
	}

	/**
	 * 用于资源均衡管理模块的注销，注销仅仅是为了及时删除临时节点，请使用RunTime组件等级hook进行处理。
	 */
	public void unRegister() {
		if (zkWrapper == null || !isRegisterOk) {// 异常或注册失败
			return;
		}

		String instanceRootPath = getModuleInstanceRootPath(zkRoot, Const.ModuleNameEnum.MODULE_NAME_RESOURCEMANAGER,
				"", RootTypeEnum.ROOT_TYPE_RESOURCEMANAGER);
		String instanceActivePath = instanceRootPath + ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE;
		ZkActiveValue zkActiveValue = null;
		try {
			try {
				zkActiveValue = JSON.parseObject(zkWrapper.getNodeValue(instanceActivePath), ZkActiveValue.class);
			} catch (NoNodeException e) {
				logger.error("NoNodeException={}", instanceActivePath, e);
				zkActiveValue = null;
			}
			if (zkActiveValue != null && zkActiveValue.equals(zkActiveValueOwn)) {
				logger.info("zkWrapper.deleteTempNode({})...", instanceActivePath);
				zkWrapper.deleteTempNode(instanceActivePath);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		isRegisterOk = false;
	}
	/**
	 * 前置模块的输出信息是否发生变化prevModuleOutInfoChangedWatcherOnOff
	 */
	@Override
	public boolean isPrevModuleOutInfoChanged(List<String> prevModuleList) {
		if (zkWrapper == null) {
			return false;
		}
//		String instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId,
//				RootTypeEnum.ROOT_TYPE_REGISTER);
//		if(!isPrevModuleOutInfoChangedWatcherAdded) {
//			// 注册前置模块的实例列表的监听
//			fileTypeListForTask.addAll(getAllFileTypes(zkRoot, moduleName, channelId));
//			// 注册输入信息文件类型的变化
//			zkWrapper.setListenterChildrenNode(new ZkWatcherTypeCallBack() {
//				@Override
//				public void updateZkInfo(Type type, String node, String data) {
//					switch (type) {
//					case ADDED:
//					case REMOVED:
//						//文件类型发生了变化,考虑到异常，所以每次都是全量比对
//						try {
//							//同步最新的文件类型
//							List<String> deleteFileTypeList=new ArrayList<>();
//							List<String> addFileTypeList=new ArrayList<>();
//							List<String> fileTypeList=getAllFileTypes(zkRoot, moduleName, channelId);
//							for(String fileType:fileTypeList) {
//								if(!fileTypeListForTask.contains(fileType)) {
//									fileTypeListForTask.add(fileType);
//									addFileTypeList.add(fileType);
//								}
//							}
//							
//							fileTypeListForTask.forEach(fileType -> {
//								if(!fileTypeList.contains(fileType)) {
//									deleteFileTypeList.add(fileType);
//								}
//							});
//							fileTypeListForTask.removeAll(deleteFileTypeList);
//							// 刷新注册输入信息同步的监听器
//							for (String fileType : fileTypeListForTask) {
//								if(pathListForListen.contains(fileType)) {
//									continue;//已经增加过监听了
//								}
//								pathListForListen.add(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
//								zkWrapper.setListenterNode(new ZkWatcherCallBack() {
//									@Override
//									public void updateZkInfo(String node, String data) {
//										if (assignInputListWatcherOnOff.get(fileType) == false)
//											assignInputListWatcherOnOff.put(fileType, true);
//									}
//								}, instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
//							}
//							//下面删除的可以考虑删除
//							for (String fileType : pathListForListen) {
//								if(!fileTypeListForTask.contains(fileType)) {
//									zkWrapper.deleteListenterNode(fileType);
//								}
//							}
//						} catch (Exception e) {
//							logger.error("updateZkInfo({}) ... Skipped:",node,e); 
//						}
//						break;
//					default:
//						break;
//					}
//				}
//			}, instanceRootPath);
//			// 注册输入信息同步的监听器
//			fileTypeListForTask.addAll(getAllFileTypes(zkRoot, moduleName, channelId));
//			// 注册输入信息文件类型的变化
//			zkWrapper.setListenterChildrenNode(new ZkWatcherTypeCallBack() {
//				@Override
//				public void updateZkInfo(Type type, String node, String data) {
//					switch (type) {
//					case ADDED:
//					case REMOVED:
//						//文件类型发生了变化,考虑到异常，所以每次都是全量比对
//						try {
//							//同步最新的文件类型
//							List<String> deleteFileTypeList=new ArrayList<>();
//							List<String> addFileTypeList=new ArrayList<>();
//							List<String> fileTypeList=getAllFileTypes(zkRoot, moduleName, channelId);
//							for(String fileType:fileTypeList) {
//								if(!fileTypeListForTask.contains(fileType)) {
//									fileTypeListForTask.add(fileType);
//									addFileTypeList.add(fileType);
//								}
//							}
//							
//							fileTypeListForTask.forEach(fileType -> {
//								if(!fileTypeList.contains(fileType)) {
//									deleteFileTypeList.add(fileType);
//								}
//							});
//							fileTypeListForTask.removeAll(deleteFileTypeList);
//							// 刷新注册输入信息同步的监听器
//							for (String fileType : fileTypeListForTask) {
//								if(pathListForListen.contains(fileType)) {
//									continue;//已经增加过监听了
//								}
//								pathListForListen.add(instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
//								zkWrapper.setListenterNode(new ZkWatcherCallBack() {
//									@Override
//									public void updateZkInfo(String node, String data) {
//										if (assignInputListWatcherOnOff.get(fileType) == false)
//											assignInputListWatcherOnOff.put(fileType, true);
//									}
//								}, instanceRootPath + ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + ZK_ASSIGN_INPUT_LIST);
//							}
//							//下面删除的可以考虑删除
//							for (String fileType : pathListForListen) {
//								if(!fileTypeListForTask.contains(fileType)) {
//									zkWrapper.deleteListenterNode(fileType);
//								}
//							}
//						} catch (Exception e) {
//							logger.error("updateZkInfo({}) ... Skipped:",node,e); 
//						}
//						break;
//					default:
//						break;
//					}
//				}
//			}, instanceRootPath);
//			isPrevModuleOutInfoChangedWatcherAdded=true;
//		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public boolean isSuffixModuleInstanceChangedWatcherOnOff() {
		if (zkWrapper == null) {
			return false;
		}
		if(!isSuffixModuleInstanceChangedWatcherAdded) {
			isSuffixModuleInstanceChangedWatcherAdded=true;
		}
		return false;
	}
	/** TODO */
	public boolean isPrefixOutputTaskUseFixRoute() {
		return false;
	}

	/** TODO */
	public void getAllPrefixOutputTaskList() {

	}

	/** TODO */
	public void getAllPrefixOutputTaskList4FixRoute() {

	}

	/** TODO */
	public void getAllPrefixErrorOutputTaskList() {

	}

	/** TODO */
	public void setPrefixOutputTaskState() {

	}

	/** TODO */
	public void setPrefixOutputTaskState4FixRoute() {

	}

	/** TODO */
	public void setPrefixErrorOutputTaskState() {

	}

	/** TODO */
	public void getPrefixOutputTaskState4FixRoute() {

	}

	/** TODO */
	public void getPrefixErrorOutputTaskState() {

	}

	/** TODO */
	public void assignSuffixInputTaskList() {

	}

	/** TODO */
	public void reassignInvalidSuffixInstanceInputTaskList() {

	}

	/** TODO */
	public void deleteInvalidSuffixInstanceInfomation() {

	}

}
