package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [单个PO组加载管理类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class LocalParamGroupLoader {
    /** 参数缓存对象*/
	private ParamCacheAB paramCacheAB;
    
	private final static Logger logger = LoggerFactory.getLogger(LocalParamGroupLoader.class);
	
    public ParamCacheAB getParamCacheAB() {
		return paramCacheAB;
	}

	///需要删除的版本信息，万一删除不掉等下次删除
    private Set<Long> versionList4Delete=new HashSet<Long>();
    
    public LocalParamGroupLoader(String poGroupName) {
    	paramCacheAB=new ParamCacheAB(poGroupName);
	}

	/** 重新拉取数据，并重建索引和数据
	 * @throws POException 
	  * */
    public boolean getDataFromMDB() throws POException {
    	paramCacheAB.setPoGroupInfoNewVersion();
		paramCacheAB.getAnotherCache().getPoContainerGroup().resetDataAndIndexes();
    	if(!paramCacheAB.getAnotherCache().getPoContainerGroup().setAllPoDataFromMDB(paramCacheAB.getPoGroupInfoNewVersion())) {
    		return false;
    	}
    	return true;
    }
    
    /** 对数据进行加工优化，比如创建索引*/
    public boolean optimizeCache() {
    	///暂无
    	return true;
    }
    
    /** 提交版本信息，并进行AB内存切换，之后业务线程即访问者将访问到新版本的数据，同时记录需要删除的版本号*/
    public boolean commitAndSwitch() {
    	long needDeleteVersion=paramCacheAB.getPoGroupInfo().getLastVersion();
    	paramCacheAB.commitAndSwitch();
    	if(needDeleteVersion>0 ) {
    		versionList4Delete.add(needDeleteVersion);
    	}
    	return true;
    }
    
    /** 
     * 删除下面的数据
        //PARAM_INS_GROUP_VISITOR（37000）,PARAM_INS_GROUP_VISITOR_INFO（37100）
    	//PARAM_INS_GROUP（35100）
    	//PARAM_INS_GROUP_PO_DATA_SIZE（35200）
    	//PARAM_INS_GROUP_PO_INDEX（35400）
     * */
    public void cleanOldDataInMDB() {
///暂时不用，因为切换时已经删除了
//    	paramCacheAB.cleanMdbInsPoGroupCacheInfo();
//    	for(long version:versionList4Delete) {
//    		paramCacheAB.cleanMdbInsClientInfo(version);
//    		paramCacheAB.getCurrentCache().getPoContainerGroup().cleanMdbInsAllPoCacheInfo(version);
//    		PoGroupRegisterFactory.cleanMdbInsAllPoIndexInfo(paramCacheAB.getPoGroupName(), version);
//    	}    	
//    	versionList4Delete.clear();
    }
    ///进程退出时的数据清理，理论上只有最近两个版本的数据
    public void unLoaderPoGroupDatas() {
    	logger.info("unLoaderPoGroupDatas({})...",paramCacheAB.getPoGroupName());
    	cleanOldDataInMDB() ;
        if(paramCacheAB.getPoGroupInfo()!=null) {
            versionList4Delete.add(paramCacheAB.getPoGroupInfo().getLastVersion());
            versionList4Delete.add(paramCacheAB.getPoGroupInfo().getCurrentVersion());
        }
    	paramCacheAB.cleanMdbInsPoGroupCacheInfo();
    	for(long version:versionList4Delete) {
    		logger.info("unLoaderPoGroupDatas({})<version={}>...",paramCacheAB.getPoGroupName(),version);
    		paramCacheAB.cleanMdbInsClientInfo(version);
    		paramCacheAB.getCurrentCache().getPoContainerGroup().cleanMdbInsAllPoCacheInfo(version);
    		PoGroupRegisterFactory.cleanMdbInsAllPoIndexInfo(paramCacheAB.getPoGroupName(), version);
    	}
    	versionList4Delete.clear();
    }

	public boolean isUpdate() {
		return paramCacheAB.isUpdate();
	}
}
