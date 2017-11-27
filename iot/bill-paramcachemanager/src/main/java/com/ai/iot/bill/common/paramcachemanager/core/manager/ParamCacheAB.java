package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.util.CheckNull;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [AB缓存切换管理类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class ParamCacheAB {
	
	private final static Logger logger = LoggerFactory.getLogger(ParamCacheAB.class);
	
    /** AB两块数据区*/
    private ArrayList<ParamCache> paramCacheArray=new ArrayList<ParamCache>();
    
    /** 当前业务线程指向的数据区下标*/
    private volatile int currentCachePos=CacheABEnum.CACHE_A.get();

	/** po组信息：当前版本号,上次版本号*/
    MdbParamDataWrapper.ParamGroup poGroupInfo;
    
    /** po组信息的备份*/
    MdbParamDataWrapper.ParamGroup poGroupInfoNewVersion;
    
    private static enum CacheABEnum{
    	CACHE_A(0),CACHE_B(1);
    	private CacheABEnum(int i) {
    		pos=i;
    	}
    	public int get() {
			return pos;
		}
		private int pos;
    }
    
    public ParamCacheAB(String poGroupName) {
    	//仅需要AB两份
    	paramCacheArray.add(new ParamCache(poGroupName,CacheABEnum.CACHE_A.get()));
    	paramCacheArray.add(new ParamCache(poGroupName,CacheABEnum.CACHE_B.get()));
	}
    
    ///返回当前AB内存的指示位置
    public int getCurrentCachePos() {
		return currentCachePos;
	}
    
    ///返回AB两块内存对应的访问者信息
    public List<MdbParamDataWrapper.VisitorInfo> getVisitorInfoAB(String clientId){
    	List<MdbParamDataWrapper.VisitorInfo> visitorInfoAB=new ArrayList<MdbParamDataWrapper.VisitorInfo>();
    	visitorInfoAB.add(paramCacheArray.get(0).getVisitorInfos().get(clientId));
    	visitorInfoAB.add(paramCacheArray.get(1).getVisitorInfos().get(clientId));
    	return visitorInfoAB;
    }
    
    ///返回po组名
	public String getPoGroupName() {
		return paramCacheArray.get(0).getPoContainerGroup().getPoGroupName();
	}

	/** 当前业务线程使用的缓存*/
    public ParamCache getCurrentCache() {
    	return paramCacheArray.get(currentCachePos);
    }
    
    /** 另外一块缓存*/
    public ParamCache getAnotherCache() {
    	return paramCacheArray.get(currentCachePos==CacheABEnum.CACHE_A.get()?CacheABEnum.CACHE_B.get():CacheABEnum.CACHE_A.get());
    }
    
    /**获取需要新的未生效版本号*/
    public long getPoGroupInfoNewVersion() {
    	return poGroupInfoNewVersion.getCurrentVersion();
    }
    
    /**返回po组的基本信息，和切换无关的信息*/
    public MdbParamDataWrapper.ParamGroup getPoGroupInfo(){
    	return poGroupInfo;
    }
    
    ///获取po组基本信息
    public void setPoGroupInfoNewVersion() throws POException {
		logger.info("setPoGroupInfo({})...",getPoGroupName());
		poGroupInfoNewVersion=MdbParamDataWrapper.getPoGroupInfo(getPoGroupName());
		if(CheckNull.isNull(poGroupInfoNewVersion)) {
			throw new POException(POException.POExceptionCodeENUM.MDB_PO_GROUP_INFO_NONE,getPoGroupName());
		}
		if(CheckNull.isNull(poGroupInfo)) {
			poGroupInfo=poGroupInfoNewVersion;//初始化为指向同一个版本
		}else {
  			//参数加载模块已经设置上次的值，为保证内存正确释放，这里重置一下
  			poGroupInfoNewVersion.setLastVersion(poGroupInfo.getCurrentVersion());
		}
  		return ;
  	}
    
    /** 上报业务线程访问者信息(当前必须是在切换之后)，每个po组对应的访问者信息均上报
     * PARAM_INS_GROUP_VISITOR（37000）
     * PARAM_INS_GROUP_VISITOR_INFO（37100）
     * */
    public void reportClientCacheInfo() {
    		getCurrentCache().reportMdbInsClientInfo(getPoGroupInfo().getCurrentVersion(),getPoGroupInfo().getLastVersion());
    }
    
    /** 
     * 这里是删除访问者信息
     * */
    public void cleanMdbInsClientInfo(long version) {
    		getCurrentCache().delMdbInsClientInfo(version);
    }
    
    public void cleanMdbInsClientInfo() {
		getCurrentCache().delMdbInsClientInfo(getPoGroupInfo().getCurrentVersion());
    }
    
    /** 上报进程实例对应的缓存信息(当前必须是在切换之后)
     * PARAM_INS_GROUP（35100）
     * */
    public void reportPoGroupCacheInfo() {
    		MdbParamDataWrapper.delInsPoGroupInfo(getPoGroupName());
    		MdbParamDataWrapper.setInsPoGroupInfo(getPoGroupName(), getPoGroupInfo());
    }
    
    public void cleanMdbInsPoGroupCacheInfo() {
		MdbParamDataWrapper.delInsPoGroupInfo(getPoGroupName());
    }
    
	///切换版本
    public boolean commitAndSwitch() {
    	poGroupInfo=poGroupInfoNewVersion;
    	currentCachePos=((currentCachePos==1)?0:1);
    	return true;
    }
    
    ///获取po组基本信息
    public boolean isUpdate() {
    	try {
    		MdbParamDataWrapper.ParamGroup mdbPoGroupInfoVersion=MdbParamDataWrapper.getPoGroupInfo(getPoGroupName());
      		if(CheckNull.isNull(mdbPoGroupInfoVersion)) {
      			logger.error("{} : No Param Group Info(301) In MDB!!!Please Check It!!!",getPoGroupName());
      			return false;
      		}
      		if(mdbPoGroupInfoVersion.getCurrentVersion()==0) {
      			logger.error("{} : Param Group Info(301) Version In MDB  Is Zero(0),Please Check It!!!",getPoGroupName());
      			return false;
      		}
    		if(mdbPoGroupInfoVersion.getCurrentVersion()!=this.getPoGroupInfo().getCurrentVersion()) {
      			return true;
      		}
	    }catch(Exception e) {
	    	logger.error(e.getLocalizedMessage(),e);
			return false;
	    }
  		return false;
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
		//po组信息
		logger.info("[{}]==>{}",paramCacheArray.get(currentCachePos).getPoContainerGroup().getPoGroupName(),
									poGroupInfo.toString());
		//po本地cache信息
		if(isDumpDataAndIndex) {
			paramCacheArray.get(currentCachePos).dumpDataAndIndex();
		}else {
			paramCacheArray.get(currentCachePos).dump();
		}
	}
	
	///打印简要信息
    public void dumpLastVersion() {
		innerDumpLastVersion(false);
	}
	
    ///打印数据信息
	public void dumpDataAndIndexLastVersion() {
		innerDumpLastVersion(true);
	}
	
	private void innerDumpLastVersion(boolean isDumpDataAndIndex) {
		//po组信息
		logger.info("[{}]==>{}",paramCacheArray.get(currentCachePos).getPoContainerGroup().getPoGroupName(),
									poGroupInfo.toString());
		//po本地cache信息
		if(isDumpDataAndIndex) {
			getAnotherCache().dumpDataAndIndex();
		}else {
			getAnotherCache().dump();
		}
	}
}
