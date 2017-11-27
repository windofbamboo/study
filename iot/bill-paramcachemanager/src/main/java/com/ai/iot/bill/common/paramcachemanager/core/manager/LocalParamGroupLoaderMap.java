package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoContainer;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException.POExceptionCodeENUM;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.DateUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [所有PO组加载管理类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class LocalParamGroupLoaderMap {
    /** PO组加载器集*/
    private HashMap<String,LocalParamGroupLoader> localParamGroupLoaderMap=new HashMap<String,LocalParamGroupLoader>();;
    
    /**PO组数据集*/
    private Map<String,ParamCacheAB> localParamGroupCacheABMap=new HashMap<String,ParamCacheAB>();
    
    /** 返回PO组数据集,给客户端使用*/
    public Map<String, ParamCacheAB> getLocalParamGroupCacheABMap() {
		return localParamGroupCacheABMap;
	}

	private final static Logger logger = LoggerFactory.getLogger(LocalParamGroupLoaderMap.class);
    
    ///是否第一次加载
    private boolean isFirstLoading=true;
    
    ///本次需要更新的Po组
    private Set<String> needUpdatePoGroupNames=new HashSet<String>();
    
    ///本次数据拉取成功，但尚未提交和切换的数据
    private Set<String> updateSuccessPoGroupNames=new HashSet<String>();
    
  ///本次已经提交和切换的数据
    private Set<String> committedPoGroupNames=new HashSet<String>();
    
    /** 加载所有PO组,单个组失败不影响其他组刷新
     * @throws Exception */
    private boolean loaderPoGroupDatas(Set<String> allPoGroupNames,Set<String> updateSuccessPoGroupNames) throws Exception {
    	LocalParamGroupLoader lpgl;
    	Date beginDate;
    	boolean isAllOk=true;
    	long dataSize=0;
    	long totalPoNum=0,totalPoDataSize=0,totalPoIndexNum=0;
    	long totalPoNumByGroup=0,totalPoDataSizeByGroup=0,totalPoIndexNumByGroup=0;
    	
    	Date beginDateAllPoGroups=new Date();
    	logger.info("-------------------------------------------------------------------");
    	logger.info("{}==>Start Loading All PoGroups ...",DateUtil.getCurrentDateTime(beginDateAllPoGroups,DateUtil.SEG_YYYYMMDD_HHMMSS));
    	logger.info("-------------------------------------------------------------------");
    	for(String poGroupName:allPoGroupNames) {
    		if(CheckNull.isNull(lpgl=localParamGroupLoaderMap.get(poGroupName))) {
    			continue;
    		}
    		totalPoNumByGroup=totalPoDataSizeByGroup=totalPoIndexNumByGroup=0;
    		//下面的是单PO组操作
    		beginDate=new Date();
    		//logger.info("{}==>{}...",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),entry.getKey());
    		try {
    			///如果另外一块内存还有业务线程在访问，就先不更新
    			if(lpgl.getParamCacheAB().getAnotherCache().isReadingStatusByJudgeExpire()) {
    				throw new POException(POExceptionCodeENUM.LOADING_FROM_MDB_FALSE,"waiting clients release another cache,pog="+poGroupName);
//    				dataSize=0;
//	    			for(PoContainer poContainer:lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().values()) {
//	    				dataSize+=poContainer.getPoDataList().size();
//	    			}
//	    			totalPoDataSizeByGroup+=dataSize;
//	    			totalPoNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
//	    			totalPoIndexNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
//	    			totalPoDataSize+=dataSize;
//	    			totalPoNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
//	    			totalPoIndexNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
//	    			logger.error("{}==>[FAILED]{} waiting clients release another cache : Total(secs)={},Po={},PoDataSize={},PoIndex={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),poGroupName,DateUtil.diffSeconds(new Date(),beginDate),
//	    					totalPoNumByGroup,totalPoDataSizeByGroup,totalPoIndexNumByGroup);
//    				isAllOk=false;
//	    			continue;
	    		}				
	    		//从MDB获取数据
	    		if(lpgl.getDataFromMDB()==false) {
	    			throw new POException(POExceptionCodeENUM.LOADING_FROM_MDB_FALSE,"error to get data from MDB,pog="+poGroupName);
//	    			dataSize=0;
//	    			for(PoContainer poContainer:lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().values()) {
//	    				dataSize+=poContainer.getPoDataList().size();
//	    			}
//	    			totalPoDataSizeByGroup+=dataSize;
//	    			totalPoNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
//	    			totalPoIndexNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
//	    			totalPoDataSize+=dataSize;
//	    			totalPoNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
//	    			totalPoIndexNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
//	    			logger.error("{}==>[FAILED]{} error to get data from MDB: Total(secs)={},Po={},PoDataSize={},PoIndex={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),poGroupName,DateUtil.diffSeconds(new Date(),beginDate),
//	    					totalPoNumByGroup,totalPoDataSizeByGroup,totalPoIndexNumByGroup);
//	    			isAllOk=false;
//	    			continue;
	    		}
	    		//对数据进行加工优化，比如创建索引
	    		lpgl.optimizeCache();	    		
    		}catch(Exception e) {
    			isAllOk=false;
    			dataSize=0;
    			for(PoContainer poContainer:lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().values()) {
    				dataSize+=poContainer.getPoDataList().size();
    			}
    			totalPoDataSizeByGroup+=dataSize;
    			totalPoNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
    			totalPoIndexNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
    			totalPoDataSize+=dataSize;
    			totalPoNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
    			totalPoIndexNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
    			logger.error("{}==>[FAILED]{} : ERROR={} Total(secs)={},Po={},PoDataSize={},PoIndex={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),poGroupName,e.getLocalizedMessage(),DateUtil.diffSeconds(new Date(),beginDate),
    					totalPoNumByGroup,totalPoDataSizeByGroup,totalPoIndexNumByGroup);
	
    			if(isFirstLoading) {//第一次加载需抛异常退出，否则业务线程可以使用旧版本继续处理，等待下次加载成功
    				throw e;
    			}else {//默认错误跳过
    				logger.error(e.getLocalizedMessage(),e);
    				continue;//用continue可以测试其他PO组是否能正常加载，方便纠错
    			}
    		}
    		updateSuccessPoGroupNames.add(poGroupName);
    		dataSize=0;
			for(PoContainer poContainer:lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().values()) {
				dataSize+=poContainer.getPoDataList().size();
			}
			totalPoDataSizeByGroup+=dataSize;
			totalPoNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
			totalPoIndexNumByGroup+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
			totalPoDataSize+=dataSize;
			totalPoNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerMap().size();
			totalPoIndexNum+=lpgl.getParamCacheAB().getAnotherCache().getPoContainerGroup().getPoContainerIndexeMap().size();
			logger.info("{}==>[SUCCESS]{} : Total(secs)={},Po={},PoDataSize={},PoIndex={}",DateUtil.getCurrentDateTime(beginDate,DateUtil.SEG_YYYYMMDD_HHMMSS),poGroupName,DateUtil.diffSeconds(new Date(),beginDate),
					totalPoNumByGroup,totalPoDataSizeByGroup,totalPoIndexNumByGroup);

    	}
    	logger.info("-------------------------------------------------------------------");
    	logger.info("{}==>End Loading All PoGroups: Total(secs)={},PoGroup={},Po={},PoDataSize={},PoIndex={}",DateUtil.getCurrentDateTime(DateUtil.SEG_YYYYMMDD_HHMMSS),DateUtil.diffSeconds(new Date(),beginDateAllPoGroups),
    			allPoGroupNames.size(),totalPoNum,totalPoDataSize,totalPoIndexNum);
    	if(!isAllOk) {
    		logger.error("{}==>[loaderPoGroupDatas()=FALSE]",DateUtil.getCurrentDateTime(DateUtil.SEG_YYYYMMDD_HHMMSS));
    		return false;
    	}
    	isFirstLoading=false;
    	logger.info("{}==>[loaderPoGroupDatas()=TRUE]",DateUtil.getCurrentDateTime(DateUtil.SEG_YYYYMMDD_HHMMSS));
    	return true;
    }
    
    /** */
    public boolean isUpdate() {
    	//logger.info("isUpdate()...");
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	
    	needUpdatePoGroupNames.clear();
    	updateSuccessPoGroupNames.clear();
    	committedPoGroupNames.clear();
    	while(iter.hasNext()){
    		entry=iter.next();
    		if(entry.getValue().isUpdate()) {
    			needUpdatePoGroupNames.add(entry.getKey());
    		} 
    	}
    	if(!(needUpdatePoGroupNames.isEmpty())) {
    		logger.info("Need Update()...");
    	}
    	return !(needUpdatePoGroupNames.isEmpty());
    }
    
    /**
     * @throws Exception */
    public void update() throws Exception {
    	logger.info("update()...");
    	if(loaderPoGroupDatas(needUpdatePoGroupNames,updateSuccessPoGroupNames)==false) {
			throw new POException(POExceptionCodeENUM.LOADING_FROM_MDB_FALSE);
		}
		commitAndSwitch();
    	return ;
    }
    
    /**
     * @throws Exception */
    public void updateAll() throws Exception {
    	logger.info("updateAll()...");
    	if(loaderPoGroupDatas(PoGroupRegisterFactory.getAllPoGroupObjects().keySet(),
    			updateSuccessPoGroupNames)==false) {
			throw new POException(POExceptionCodeENUM.LOADING_FROM_MDB_FALSE);
		}
		commitAndSwitch();
    	return ;
    }
    
    /** 提交版本信息，并进行切换*/
    private void commitAndSwitch() {
    	LocalParamGroupLoader lpgl;
    	for(String poGroupName:updateSuccessPoGroupNames) {
    		if(CheckNull.isNull(lpgl=localParamGroupLoaderMap.get(poGroupName))) {
    			continue;
    		}
    		lpgl.commitAndSwitch();
    		committedPoGroupNames.add(poGroupName);
    	}
    	return ;
    }
    
    /** 上报业务线程访问者信息(当前必须是在切换之后)
     * */
    public void reportAllClientCacheInfo() {
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		entry.getValue().getParamCacheAB().reportClientCacheInfo();
    	}
    }
    
    ///仅上报指定po组的访问者信息
    public void reportClientCacheInfo() {
    	logger.info("reportClientCacheInfo()...");
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		if(!committedPoGroupNames.contains(entry.getKey())) {
    			continue;
    		}
    		//PARAM_INS_GROUP_VISITOR（37000）,PARAM_INS_GROUP_VISITOR_INFO（37100）
    		entry.getValue().getParamCacheAB().reportClientCacheInfo();
    	}
    }
    
    /** 
     * 删除指定版本的实例化汇报的数据，如果是实体变更升级，在升级时需手工清空mdb的数据防止自动清理不干净
     * 也可以在将来不停服务的情况下，手工清空MDB，然后等待参数加载全量加载一遍即可。
     * 汇报信息在切换时会自动删除以前的版本数据，所以无需额外处理
     * 该函数仅进程或业务线程退出时调用
        //PARAM_INS_GROUP_VISITOR（37000）,PARAM_INS_GROUP_VISITOR_INFO（37100）
    	//PARAM_INS_GROUP（35100）
    	//PARAM_INS_GROUP_PO_DATA_SIZE（35200）
    	//PARAM_INS_GROUP_PO_INDEX（35400）
     * */
    public void cleanOldDataInMdb() {
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		entry.getValue().cleanOldDataInMDB();
    	}
    	return ;
    }
    
    /** 上报po组和po对应的缓存信息*/
    public boolean reportProcCacheInfo() {
    	logger.info("reportProcCacheInfo() ...");
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		//PARAM_INS_GROUP（35100）
    		entry.getValue().getParamCacheAB().reportPoGroupCacheInfo();
    		//PARAM_INS_GROUP_PO_DATA_SIZE（35200）
    		entry.getValue().getParamCacheAB().getCurrentCache().getPoContainerGroup().reportAllPoCacheInfo(
    						entry.getValue().getParamCacheAB().getPoGroupInfo().getCurrentVersion(),
    						entry.getValue().getParamCacheAB().getPoGroupInfo().getLastVersion());
    		//PARAM_INS_GROUP_PO_INDEX（35400）
    		PoGroupRegisterFactory.reportAllPoIndexInfo(entry.getKey(),
    				entry.getValue().getParamCacheAB().getPoGroupInfo().getCurrentVersion(),
    				entry.getValue().getParamCacheAB().getPoGroupInfo().getLastVersion());
    	}
    	return true;
    }
    public boolean reportProcCacheInfo(Set<String> commitPoGroupNames) {
    	Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		if(!commitPoGroupNames.contains(entry.getKey())) {
    			continue;
    		}
    		//PARAM_INS_GROUP（35100）
    		entry.getValue().getParamCacheAB().reportPoGroupCacheInfo();
    		//PARAM_INS_GROUP_PO_DATA_SIZE（35200）
    		entry.getValue().getParamCacheAB().getCurrentCache().getPoContainerGroup().reportAllPoCacheInfo(
    						entry.getValue().getParamCacheAB().getPoGroupInfo().getCurrentVersion(),
    						entry.getValue().getParamCacheAB().getPoGroupInfo().getLastVersion());
    		//PARAM_INS_GROUP_PO_INDEX（35400）
    		PoGroupRegisterFactory.reportAllPoIndexInfo(entry.getKey(),
    				entry.getValue().getParamCacheAB().getPoGroupInfo().getCurrentVersion(),
    				entry.getValue().getParamCacheAB().getPoGroupInfo().getLastVersion());
    	}
    	return true;
    }

    /**加载类定义的所有的对象,因为是静态数据,所以只允许调用一次*/
	public void loaderPoGroupObjects() {
		logger.info("loaderPoGroupObjects()...");
		LocalParamGroupLoader localParamGroupLoader;
		for(String object:PoGroupRegisterFactory.getAllPoGroupObjects().keySet()){
			if(localParamGroupLoaderMap.containsKey(object)) {
				continue;
			}
			logger.info("POG_NAME={}",object);
			localParamGroupLoader=new LocalParamGroupLoader(object);
			localParamGroupLoaderMap.put(object,localParamGroupLoader);
			localParamGroupCacheABMap.put(object, localParamGroupLoader.getParamCacheAB());
		}
	}
	
	public void unLoaderPoGroupDatas() {
		Iterator<Entry<String,LocalParamGroupLoader>> iter=localParamGroupLoaderMap.entrySet().iterator();
    	Map.Entry<String,LocalParamGroupLoader> entry;
    	while(iter.hasNext()){
    		entry=iter.next();
    		entry.getValue().unLoaderPoGroupDatas();
    	}
	}
	///打印简要信息
    public void dump() {
		innerDump(false);
	}
	
    ///打印数据信息
	public void dumpDataAndIndex() {
		innerDump(true);
	}
	
	private void innerDump(boolean isDumpDataAndIndex) {
		logger.info("==================================================");
		Iterator<Entry<String,ParamCacheAB>> iter=localParamGroupCacheABMap.entrySet().iterator();
    	Map.Entry<String,ParamCacheAB> entry;
    	
    	if(isDumpDataAndIndex) {
    		while(iter.hasNext()){
	    		entry=iter.next();
	    		entry.getValue().dumpDataAndIndex();
	    	}
    	}else {
	    	while(iter.hasNext()){
	    		entry=iter.next();
	    		entry.getValue().dump();
	    	}
    	}
    	logger.info("==================================================");
	}
	
	///打印简要信息
    public void dumpLastVersion() {
		innerDumpLastVersion(false);
	}
	
    ///打印数据信息
	public void dumpDataAndIndexLastVersion() {
		innerDump(true);
	}
	
	private void innerDumpLastVersion(boolean isDumpDataAndIndex) {
		logger.info("==================================================");
		Iterator<Entry<String,ParamCacheAB>> iter=localParamGroupCacheABMap.entrySet().iterator();
    	Map.Entry<String,ParamCacheAB> entry;
    	
    	if(isDumpDataAndIndex) {
    		while(iter.hasNext()){
	    		entry=iter.next();
	    		entry.getValue().dumpDataAndIndexLastVersion();
	    	}
    	}else {
	    	while(iter.hasNext()){
	    		entry=iter.next();
	    		entry.getValue().dumpLastVersion();
	    	}
    	}
    	logger.info("==================================================");
	}
}
