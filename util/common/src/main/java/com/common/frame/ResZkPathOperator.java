package com.common.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.common.frame.zkpath.ZkOutTypeEnum;
import com.common.frame.zkpath.ZkOutput;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.frame.zkpath.ZkOutputState;
import com.common.util.Const;
import com.common.util.ZkWrapper;
import com.alibaba.fastjson.JSON;

/**
 * 
 * 输入输出和任务状态 1){zkRoot}/register/{module_name}/{instance-id}
 * 2){zkRoot}/register/{module_name}/{instance-id}/isactive
 * 3){zkRoot}/register/{module_name}/{instance-id}/{file-type}/assign-input-list
 * 4){zkRoot}/register/{module_name}/{instance-id}/{file-type}/output/{dir-id}
 * 5){zkRoot}/register/{module_name}/{instance-id}/{file-type}/output/{dir-id}/{n}
 * 6){zkRoot}/register/{module_name}/{instance-id}/{file-type}/errout/{dir-id}
 * 7){zkRoot}/task_state/{module_name}/{instance-id}/{file-type}/output_state/{dir-id}
 * 8){zkRoot}/task_state/{module_name}/{instance-id}/{file-type}/output_state/{dir-id}/{n}
 * 9){zkRoot}/task_state/{module_name}/{instance-id}/{file-type}/errout/{dir-id}
 *
 * 资源均衡管理模块状态 {zkRoot}/res_balance_manager_state/isactive
 */
public class ResZkPathOperator {
	private final Logger logger = LoggerFactory.getLogger(ResZkPathOperator.class);
	protected static final String ZK_ISACTIVE = "isactive";
	protected static final String ZK_ASSIGN_INPUT_LIST = "assign-input-list";

	protected ZkWrapper zkWrapper = null;
	protected boolean isJsonValuePretty = true;
	protected String zkRoot;
	protected Const.ModuleNameEnum moduleName = Const.ModuleNameEnum.MODULE_NAME_UNKNOWN;
	protected String channelId = "";

	public ResZkPathOperator(String zkConnectList, String zkRoot) {
		zkWrapper = new ZkWrapper(zkConnectList, zkRoot);
		this.zkRoot = zkRoot;
	}

	protected static enum RootTypeEnum {
		ROOT_TYPE_REGISTER("register"), ROOT_TYPE_TASKSTATE("task_state"), ROOT_TYPE_RESOURCEMANAGER(
				"res_balance_manager_state");
		private String name;

		private RootTypeEnum(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	protected static enum ValueTypeEnum {
		VALUE_TYPE_OUTPUT("output"), VALUE_TYPE_ERROUT("errout"), VALUE_TYPE_OUTPUT_TASKSTATE(
				"output_state"), VALUE_TYPE_ERROUT_TASKSTATE("errout_state");
		private String name;

		private ValueTypeEnum(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public void setJsonValuePretty(boolean isJsonValuePretty) {
		this.isJsonValuePretty = isJsonValuePretty;
	}

	public void setZkWrapper(ZkWrapper zkWrapper) {
		this.zkWrapper = zkWrapper;
	}

	protected String getModuleInstanceRootPath(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			RootTypeEnum rootType) {
		switch (rootType) {
		case ROOT_TYPE_TASKSTATE:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_TASKSTATE.getName() + ZkWrapper.ZK_PATH_SEP
					+ moduleName.getModuleName() + ZkWrapper.ZK_PATH_SEP + String.valueOf(channelId);
		case ROOT_TYPE_RESOURCEMANAGER:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_RESOURCEMANAGER.getName();
		case ROOT_TYPE_REGISTER:
		default:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_REGISTER.getName() + ZkWrapper.ZK_PATH_SEP
					+ moduleName.getModuleName() + ZkWrapper.ZK_PATH_SEP + String.valueOf(channelId);
		}
	}

	protected String getModuleRootPath(String zkRoot, Const.ModuleNameEnum moduleName, RootTypeEnum rootType) {
		switch (rootType) {
		case ROOT_TYPE_TASKSTATE:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_TASKSTATE.getName() + ZkWrapper.ZK_PATH_SEP
					+ moduleName.getModuleName();
		case ROOT_TYPE_RESOURCEMANAGER:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_RESOURCEMANAGER.getName();
		case ROOT_TYPE_REGISTER:
		default:
			return zkRoot + ZkWrapper.ZK_PATH_SEP + RootTypeEnum.ROOT_TYPE_REGISTER.getName() + ZkWrapper.ZK_PATH_SEP
					+ moduleName.getModuleName();
		}
	}

	/**
	 * 返回指定模块实例下面所有的文件类型
	 * 
	 * @throws Exception
	 */
	public List<String> getAllFileTypes(String zkRoot, Const.ModuleNameEnum moduleName, String channelId)
			throws Exception {
		return innerGetAllFileTypes(RootTypeEnum.ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId);
	}

	/**
	 * 返回指定模块的实例个数
	 * 
	 * @throws Exception
	 */
	public int getModuleInstanceNumber(String zkRoot, Const.ModuleNameEnum moduleName) throws Exception {
		return getModuleInstanceList(zkRoot,moduleName).size();
	}
	/**
	 * 返回指定模块的实例节点列表
	 * 
	 * @throws Exception
	 */
	public List<String> getModuleInstanceList(String zkRoot, Const.ModuleNameEnum moduleName) throws Exception {
		String moduleRootPath = getModuleRootPath(zkRoot, moduleName, RootTypeEnum.ROOT_TYPE_REGISTER);
		try {
			List<String> instanceList = zkWrapper.getChildNodeList(moduleRootPath);
			return instanceList;
		} catch (NoNodeException noNode) {
			logger.error("node is not exist:{}", moduleRootPath);
			return Collections.emptyList();
		}
	}

	public List<String> getAllFileTypesByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId)
			throws Exception {
		return innerGetAllFileTypes(RootTypeEnum.ROOT_TYPE_TASKSTATE, zkRoot, moduleName, channelId);
	}

	private List<String> innerGetAllFileTypes(RootTypeEnum rootTypeEnum, String zkRoot, Const.ModuleNameEnum moduleName,
			String channelId) throws Exception {
		String instanceRootPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum);
		try {
			List<String> fileTypeList = zkWrapper.getChildNodeList(instanceRootPath);
			return fileTypeList.stream()
					.filter(s -> (!s.equalsIgnoreCase(ZK_ISACTIVE) && !s.endsWith(ZkWrapper.ZK_PATH_SEP + ZK_ISACTIVE)))
					.collect(Collectors.toList());
		} catch (NoNodeException noNode) {
			logger.error("node is not exist:{}", instanceRootPath);
			return Collections.emptyList();
		}
	}

	/**
	 * 返回指定模块实例指定文件类型下面的输出信息
	 * 
	 * @throws Exception
	 */
	public List<ZkOutput> getOutputByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
                                              String fileType) throws Exception {
		return innerGetValueByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_OUTPUT.getName(),
				zkRoot, moduleName, channelId, fileType, ZkOutput.class);
	}

	public List<ZkOutput> getErrOutByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType) throws Exception {
		return innerGetValueByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_ERROUT.getName(),
				zkRoot, moduleName, channelId, fileType, ZkOutput.class);
	}

	public List<ZkOutputState> getOutputByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName,
			String channelId, String fileType) throws Exception {
		return innerGetValueByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE,
				ValueTypeEnum.VALUE_TYPE_OUTPUT_TASKSTATE.getName(), zkRoot, moduleName, channelId, fileType,
				ZkOutputState.class);
	}

	public List<ZkOutputState> getErrOutByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName,
			String channelId, String fileType) throws Exception {
		return innerGetValueByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE,
				ValueTypeEnum.VALUE_TYPE_ERROUT_TASKSTATE.getName(), zkRoot, moduleName, channelId, fileType,
				ZkOutputState.class);
	}

	private <T extends ZkOutput> List<T> innerGetValueByFileType(RootTypeEnum rootTypeEnum, String valueType,
			String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType, Class<T> clazz)
			throws Exception {
		String outputPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum)
				+ ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + valueType;
		List<T> zkOutputs = new ArrayList<>();
		List<String> outPutList = zkWrapper.getChildNodeList(outputPath);
		for (String dir : outPutList) {
			zkOutputs.add(JSON.parseObject(zkWrapper.getNodeValue(outputPath + ZkWrapper.ZK_PATH_SEP + dir), clazz));
		}
		return zkOutputs;
	}

	private String getSubWorkDir(String path, int i) {
		String pathNew = new String(path);
		if (!pathNew.endsWith(ZkWrapper.ZK_PATH_SEP)) {
			pathNew = pathNew + ZkWrapper.ZK_PATH_SEP;
		}
		return pathNew + String.valueOf(i);
	}

	private String getSubWorkPrefix(String workPrefix, int i) {
		return workPrefix + "%" + String.valueOf(i) + "%";
	}

	public void deleteByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType)
			throws Exception {
		innerDeleteByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId, fileType);
	}

	public void deleteByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType) throws Exception {
		innerDeleteByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, zkRoot, moduleName, channelId, fileType);
	}

	private void innerDeleteByFileType(RootTypeEnum rootTypeEnum, String zkRoot, Const.ModuleNameEnum moduleName,
			String channelId, String fileType) throws Exception {
		String fileTypePath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum)
				+ ZkWrapper.ZK_PATH_SEP + fileType;
		logger.info("zkWrapper.deleteNode({})", fileTypePath);
		zkWrapper.deleteNode(fileTypePath);
	}

	public void modifyOutputByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutput> zkOutputOlds, List<ZkOutput> zkOutputNews) throws Exception {
		innerModifyOutputByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_OUTPUT.getName(), zkRoot,
				moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
	}

	public void modifyErrOutByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutput> zkOutputOlds, List<ZkOutput> zkOutputNews) throws Exception {
		innerModifyOutputByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_ERROUT.getName(), zkRoot,
				moduleName, channelId, fileType, zkOutputOlds, zkOutputNews);
	}

	public void modifyOutputByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutputState> zkOutputOlds, List<ZkOutputState> zkOutputNews) throws Exception {
		innerModifyOutputByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE,
				ValueTypeEnum.VALUE_TYPE_OUTPUT_TASKSTATE.getName(), zkRoot, moduleName, channelId, fileType,
				zkOutputOlds, zkOutputNews);
	}

	public void modifyErrOutByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutputState> zkOutputOlds, List<ZkOutputState> zkOutputNews) throws Exception {
		innerModifyOutputByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE,
				ValueTypeEnum.VALUE_TYPE_ERROUT_TASKSTATE.getName(), zkRoot, moduleName, channelId, fileType,
				zkOutputOlds, zkOutputNews);
	}

	private <T extends ZkOutput> void innerModifyOutputByFileType(RootTypeEnum rootTypeEnum, String valueType,
			String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType, List<T> zkOutputOlds,
			List<T> zkOutputNews) throws Exception {
		String outputPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum)
				+ ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + valueType;
		boolean isFound;
		// 删除已经剔除的目录
		for (T zkOutputOld : zkOutputOlds) {
			if(zkOutputOld==null) {
				continue;
			}
			isFound = false;
			for (T zkOutputNew : zkOutputNews) {
				if (zkOutputOld.getDirId().equals(zkOutputNew.getDirId())) {
					isFound = true;
					if (!zkOutputOld.equals(zkOutputNew)) {
						// 更新已经存在目录信息
						modifyOutputByFileTypeWithOneOutput(outputPath + ZkWrapper.ZK_PATH_SEP + zkOutputOld.getDirId(),
								zkOutputOld, zkOutputNew);
					}
					break;
				}
			}
			if (!isFound) {// 删除已经剔除的目录
				logger.info("zkWrapper.deleteNode({})", outputPath + ZkWrapper.ZK_PATH_SEP + zkOutputOld.getDirId());
				zkWrapper.deleteNode(outputPath + ZkWrapper.ZK_PATH_SEP + zkOutputOld.getDirId());
			}
		}
		// 增加新增的目录
		List<T> zkOutputAdds = null;
		for (T zkOutputNew : zkOutputNews) {
			isFound = false;
			for (T zkOutputOld : zkOutputOlds) {
				if(zkOutputOld==null) {
					continue;
				}
				if (zkOutputOld.getDirId().equals(zkOutputNew.getDirId())) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {// 增加新增的目录
				if (zkOutputAdds == null)
					zkOutputAdds = new ArrayList<>();
				zkOutputAdds.add(zkOutputNew);
			}
		}
		if (zkOutputAdds != null)
			innerAddOutputByFileType(rootTypeEnum, valueType, zkRoot, moduleName, channelId, fileType, zkOutputAdds);
		return;
	}

	private <T extends ZkOutput> void modifyOutputByFileTypeWithOneOutput(String outputRootPath, T zkOutputOld,
			T zkOutputNew) throws Exception {
		String outputPath = outputRootPath;
		String pathNew = zkOutputNew.getPath();
		String subWorDirNew = zkOutputNew.getWorkPath();
		String subWorkPrefixNew = zkOutputNew.getWorkPrefix();
		// 输出类型不变的处理
		if (zkOutputOld.getOutType() == zkOutputNew.getOutType()) {
			// 普通目录直接更新
			if (zkOutputOld.getOutType() == ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR) {
				if (logger.isInfoEnabled()) {
					logger.info("zkWrapper.updateNodeValue({})={}", outputPath, zkOutputNew);
				}
				zkWrapper.updateNodeValue(outputPath, JSON.toJSONString(zkOutputNew, isJsonValuePretty));
			} else {// ZkOutTypeEnum.OUT_TYPE_SUB_DIR
					// 子目录需要区分拆分字目录数大小
				if (zkOutputOld.getOutSplitNum() > zkOutputNew.getOutSplitNum()) {
					for (int i = zkOutputNew.getOutSplitNum(); i < zkOutputOld.getOutSplitNum(); i++) {
						logger.info("zkWrapper.deleteNode({})", outputPath + ZkWrapper.ZK_PATH_SEP + i);
						zkWrapper.deleteNode(outputPath + ZkWrapper.ZK_PATH_SEP + i);
					}
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {
						zkOutputNew.setPath(pathNew + ZkWrapper.ZK_PATH_SEP + i);
						zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath(getSubWorkDir(subWorDirNew, i));
						zkOutputNew.setWorkPrefix(getSubWorkPrefix(subWorkPrefixNew, i));
						if (logger.isInfoEnabled()) {
							logger.info("zkWrapper.updateNodeValue({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i,
									zkOutputNew);
						}
						zkWrapper.updateNodeValue(outputPath + ZkWrapper.ZK_PATH_SEP + i,
								JSON.toJSONString(zkOutputNew, isJsonValuePretty));
					}
				} else if (zkOutputOld.getOutSplitNum() < zkOutputNew.getOutSplitNum()) {
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {
						zkOutputNew.setPath(pathNew + ZkWrapper.ZK_PATH_SEP + i);
						zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath(getSubWorkDir(subWorDirNew, i));
						zkOutputNew.setWorkPrefix(getSubWorkPrefix(subWorkPrefixNew, i));
						if (i >= zkOutputOld.getOutSplitNum()) {
							if (logger.isInfoEnabled()) {
								logger.info("zkWrapper.insertNode({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i,
										zkOutputNew);
							}
							zkWrapper.insertNode(outputPath + ZkWrapper.ZK_PATH_SEP + i,
									JSON.toJSONString(zkOutputNew, isJsonValuePretty));
						} else {
							if (logger.isInfoEnabled()) {
								logger.info("zkWrapper.updateNodeValue({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i,
										zkOutputNew);
							}
							zkWrapper.updateNodeValue(outputPath + ZkWrapper.ZK_PATH_SEP + i,
									JSON.toJSONString(zkOutputNew, isJsonValuePretty));
						}
					}
				} else {
					for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {
						zkOutputNew.setPath(pathNew + ZkWrapper.ZK_PATH_SEP + i);
						zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
						zkOutputNew.setWorkPath(getSubWorkDir(subWorDirNew, i));
						zkOutputNew.setWorkPrefix(getSubWorkPrefix(subWorkPrefixNew, i));
						if (logger.isInfoEnabled()) {
							logger.info("zkWrapper.updateNodeValue({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i,
									zkOutputNew);
						}
						zkWrapper.updateNodeValue(outputPath + ZkWrapper.ZK_PATH_SEP + i,
								JSON.toJSONString(zkOutputNew, isJsonValuePretty));
					}
				}
				zkOutputNew.setPath(pathNew);
				zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_SUB_DIR);
				zkOutputNew.setWorkPath(subWorDirNew);
				zkOutputNew.setWorkPrefix(subWorkPrefixNew);
			}
			return;
		}
		/// 不相同的处理
		/// 先更新子目录，再更新普通目录
		if (zkOutputNew.getOutType() == ZkOutTypeEnum.OUT_TYPE_SUB_DIR) {
			// 子目录需要插入,此时zkOutputOld没有子目录
			for (int i = 0; i < zkOutputNew.getOutSplitNum(); i++) {
				zkOutputNew.setPath(pathNew + ZkWrapper.ZK_PATH_SEP + i);
				zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
				zkOutputNew.setWorkPath(getSubWorkDir(subWorDirNew, i));
				zkOutputNew.setWorkPrefix(getSubWorkPrefix(subWorkPrefixNew, i));
				if (logger.isInfoEnabled()) {
					logger.info("zkWrapper.insertNode({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i, zkOutputNew);
				}
				zkWrapper.insertNode(outputPath + ZkWrapper.ZK_PATH_SEP + i,
						JSON.toJSONString(zkOutputNew, isJsonValuePretty));
			}
			zkOutputNew.setPath(pathNew);
			zkOutputNew.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
			zkOutputNew.setWorkPath(subWorDirNew);
			zkOutputNew.setWorkPrefix(subWorkPrefixNew);
		}
		if (zkOutputOld.getOutType() == ZkOutTypeEnum.OUT_TYPE_SUB_DIR) {
			// 子目录需要删除,此时zkOutputNew没有子目录
			for (int i = 0; i < zkOutputOld.getOutSplitNum(); i++) {
				logger.info("zkWrapper.deleteNode({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + i);
				zkWrapper.deleteNode(outputPath + ZkWrapper.ZK_PATH_SEP + i);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("zkWrapper.updateNodeValue({})={}", outputPath, zkOutputNew);
		}
		//更新普通目录以及子目录的父目录
		zkWrapper.updateNodeValue(outputPath, JSON.toJSONString(zkOutputNew, isJsonValuePretty));
	}

	public void addOutputByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType,
			List<ZkOutput> zkOutputs) throws Exception {
		innerAddOutputByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_OUTPUT.getName(), zkRoot,
				moduleName, channelId, fileType, zkOutputs);
	}

	public void addErrOutByFileType(String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType,
			List<ZkOutput> zkOutputs) throws Exception {
		innerAddOutputByFileType(RootTypeEnum.ROOT_TYPE_REGISTER, ValueTypeEnum.VALUE_TYPE_ERROUT.getName(), zkRoot,
				moduleName, channelId, fileType, zkOutputs);
	}

	public void addOutputByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutput> zkOutputs) throws Exception {
		innerAddOutputByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE, ValueTypeEnum.VALUE_TYPE_OUTPUT_TASKSTATE.getName(),
				zkRoot, moduleName, channelId, fileType, zkOutputs);
	}

	public void addErrOutByFileTypeByTaskState(String zkRoot, Const.ModuleNameEnum moduleName, String channelId,
			String fileType, List<ZkOutput> zkOutputs) throws Exception {
		innerAddOutputByFileType(RootTypeEnum.ROOT_TYPE_TASKSTATE, ValueTypeEnum.VALUE_TYPE_ERROUT_TASKSTATE.getName(),
				zkRoot, moduleName, channelId, fileType, zkOutputs);
	}

	private <T extends ZkOutput> void innerAddOutputByFileType(RootTypeEnum rootTypeEnum, String valueType,
			String zkRoot, Const.ModuleNameEnum moduleName, String channelId, String fileType, List<T> zkOutputs)
			throws Exception {
		String outputPath = getModuleInstanceRootPath(zkRoot, moduleName, channelId, rootTypeEnum)
				+ ZkWrapper.ZK_PATH_SEP + fileType + ZkWrapper.ZK_PATH_SEP + valueType;
		/// 先更新子目录，再更新普通目录
		// 带子目录的父目录也需要更新
		zkWrapper.insertNode(outputPath);
		for (T zkOutput : zkOutputs) {
			if (zkOutput.getOutType() == ZkOutTypeEnum.OUT_TYPE_SUB_DIR) {
				// 子目录需要插入,此时zkOutputOld没有子目录
				String pathNew = zkOutput.getPath();
				String subWorDirNew = zkOutput.getWorkPath();
				String subWorkPrefixNew = zkOutput.getWorkPrefix();
				for (int i = 0; i < zkOutput.getOutSplitNum(); i++) {
					zkOutput.setPath(pathNew + ZkWrapper.ZK_PATH_SEP + i);
					zkOutput.setOutType(ZkOutTypeEnum.OUT_TYPE_NORMAL_DIR);
					zkOutput.setWorkPath(getSubWorkDir(subWorDirNew, i));
					zkOutput.setWorkPrefix(getSubWorkPrefix(subWorkPrefixNew, i));
					if (logger.isInfoEnabled()) {
						logger.info("zkWrapper.insertNode({})={}",
								outputPath + ZkWrapper.ZK_PATH_SEP + zkOutput.getDirId() + ZkWrapper.ZK_PATH_SEP + i,
								zkOutput);
					}
					zkWrapper.insertNode(
							outputPath + ZkWrapper.ZK_PATH_SEP + zkOutput.getDirId() + ZkWrapper.ZK_PATH_SEP + i,
							JSON.toJSONString(zkOutput, isJsonValuePretty));
				}
				zkOutput.setPath(pathNew);
				zkOutput.setOutType(ZkOutTypeEnum.OUT_TYPE_SUB_DIR);
				zkOutput.setWorkPath(subWorDirNew);
				zkOutput.setWorkPrefix(subWorkPrefixNew);
			}
			// 带子目录的父目录也需要更新
			if (logger.isInfoEnabled()) {
				logger.info("zkWrapper.insertNode({})={}", outputPath + ZkWrapper.ZK_PATH_SEP + zkOutput.getDirId(),
						zkOutput);
			}
			zkWrapper.insertNode(outputPath + ZkWrapper.ZK_PATH_SEP + zkOutput.getDirId(),
					JSON.toJSONString(zkOutput, isJsonValuePretty));
		}
	}

}
