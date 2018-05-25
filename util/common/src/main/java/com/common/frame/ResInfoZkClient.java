package com.common.frame;

import java.util.Map;

import com.common.frame.zkpath.ZkFileTypeValue;
import com.common.frame.zkpath.ZkOutput;
import com.common.util.Const;

public interface ResInfoZkClient {
	public void register() throws Exception;
	public void unRegister() ;
	public void registerOutInfo(Map<String, ZkFileTypeValue> zkFileTypeValues) throws Exception ;
	public Map<String, Map<String, ZkOutput>> refreshAllTaskListBySuffixModule() throws Exception ;
	public int getSuffixModuleInstanceNumberByPrefixModule(Const.ModuleNameEnum suffixModuleName) throws Exception ;
}
