package com.common.frame;

import java.util.List;

public interface ResInfoZkResourceManager {
	
	public int judgeMasterState() ;
	public boolean register() throws Exception ;
	public void unRegister();
	public boolean isPrefixOutputTaskUseFixRoute();
	public void getAllPrefixOutputTaskList();
	public void getAllPrefixOutputTaskList4FixRoute();
	public void getAllPrefixErrorOutputTaskList();
	public void setPrefixOutputTaskState();
	public void setPrefixOutputTaskState4FixRoute();
	public void setPrefixErrorOutputTaskState();
	public void getPrefixOutputTaskState4FixRoute();
	public void getPrefixErrorOutputTaskState();
	public void assignSuffixInputTaskList();
	public void reassignInvalidSuffixInstanceInputTaskList() ;
	public void deleteInvalidSuffixInstanceInfomation() ;
	public boolean isPrevModuleOutInfoChanged(List<String> prevModuleList);
	public boolean isSuffixModuleInstanceChangedWatcherOnOff();

}
