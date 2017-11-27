package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoContainerGroup;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [单块缓存类，包括索引数据、普通数据、访问者信息,需要支持多线程操作安全]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class ParamCache {
	
	private final static Logger logger = LoggerFactory.getLogger(ParamCache.class);
	
    /** po数据*/
	private PoContainerGroup poContainerGroup;

	/** 访问者（即业务线程客户端）*/
    private Set<String> visitorIds=new HashSet<String>();
    
    /** 访问者（即业务线程客户端）*/
    private Map<String,MdbParamDataWrapper.VisitorInfo> visitorInfos=new HashMap<String,MdbParamDataWrapper.VisitorInfo>();

	///默认的访问者信息
    MdbParamDataWrapper.VisitorInfo visitorInfoDefault = new MdbParamDataWrapper.VisitorInfo();
    
    /**此块内存对应的下标*/
    private int index;
    
	public ParamCache(String poGroupName,int index) {
		this.index=index;
		poContainerGroup=new PoContainerGroup(poGroupName,index);
	}
   
	public PoContainerGroup getPoContainerGroup() {
		return poContainerGroup;
	}

    public Map<String, MdbParamDataWrapper.VisitorInfo> getVisitorInfos() {
		return visitorInfos;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//下面的是客户端即访问者线程接口
	////////////////////////////////////////////////////////////////////////////////////////
	/**注册新客户端即访问者以及缓存信息*/
	public void clientRegister(String clientId,long version){
		synchronized (visitorInfos) {
			if(visitorIds.contains(clientId)) {
				//return visitorInfos.get(clientId);
			}else {
				MdbParamDataWrapper.VisitorInfo visitorInfo=new MdbParamDataWrapper.VisitorInfo();
				visitorInfos.put(clientId, visitorInfo);
				visitorIds.add(clientId);
				//return visitorInfo;
			}
		}		
	}
	
	/**注销新客户端即访问者以及缓存信息*/
	public void clientUnRegister(String clientId,long version){
		synchronized (visitorInfos) {
			visitorIds.remove(clientId);
			visitorInfos.remove(clientId);
		}
	}
	
	///注册信息到Mdb
	public void clientRegisterMdbInsClientInfo(String clientId,long version) {
    	visitorInfoDefault.heartBeat();
    	clientUnRegisterClientMdbInsClientInfo(clientId,version);
    	MdbParamDataWrapper.addInsPoGroupVisitorIds(poContainerGroup.getPoGroupName(), version, clientId);
    	MdbParamDataWrapper.delInsPoGroupVisitorInfo(poContainerGroup.getPoGroupName(), version,clientId);
		MdbParamDataWrapper.setInsPoGroupVisitorInfo(poContainerGroup.getPoGroupName(), version,clientId,
						visitorInfos.getOrDefault(clientId,visitorInfoDefault));
    }
    
    ///从Mdb中注销信息
	public void clientUnRegisterClientMdbInsClientInfo(String clientId,long version) {
    	MdbParamDataWrapper.removeInsPoGroupVisitorIds(poContainerGroup.getPoGroupName(), version, clientId);
    	MdbParamDataWrapper.delInsPoGroupVisitorInfo(poContainerGroup.getPoGroupName(), version,clientId);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////
    //下面的是管理线程接口
    ////////////////////////////////////////////////////////////////////////////////////////
    /** 返回业务线程使用状态*/
    public boolean isReadingStatusByNoJudgeExpire() {
    	if(ParamCacheManagerConfigure.getGlobalInstance().isForcedImmediatlySwitch()) {
    		return true;
    	}
    	synchronized (visitorInfos) {
	    	Iterator<Entry<String,MdbParamDataWrapper.VisitorInfo>> iter=visitorInfos.entrySet().iterator();
			Map.Entry<String,MdbParamDataWrapper.VisitorInfo> entry;
		    while(iter.hasNext()) {
		    	entry=iter.next();
		    	if(entry.getValue().getReadStatus()==MdbParamDataWrapper.VisitorInfo.ReadStatusEnum.READING)
			    	return true;
		    }
	    	return false;
    	}
    }
    
    /** 返回业务线程使用状态，不包含超时的业务线程*/
    public boolean isReadingStatusByJudgeExpire() {
    	if(ParamCacheManagerConfigure.getGlobalInstance().isForcedImmediatlySwitch()) {
    		return true;
    	}
    	synchronized (visitorInfos) {
	    	Iterator<Entry<String,MdbParamDataWrapper.VisitorInfo>> iter=visitorInfos.entrySet().iterator();
			Map.Entry<String,MdbParamDataWrapper.VisitorInfo> entry;
		    while(iter.hasNext()) {
		    	entry=iter.next();
		    	SimpleDateFormat sdf=(new SimpleDateFormat(DateUtil.YYYYMMDD_HHMMSS));
		    	try {
		    		if(entry.getValue().getReadStatus()==MdbParamDataWrapper.VisitorInfo.ReadStatusEnum.READING &&
		    				ParamCacheManagerConfigure.getGlobalInstance().getVisitorHbExpireSeconds()>0&&
								DateUtil.nowAbsSeconds()-sdf.parse(String.valueOf(entry.getValue().getHbTime())).getTime() >= ParamCacheManagerConfigure.getGlobalInstance().getVisitorHbExpireSeconds()) {
		    			logger.info("[{}]==>readStatus={},version={},hbtime={}",entry.getKey(),entry.getValue().getReadStatus(),entry.getValue().getVersion(),entry.getValue().getHbTime());
		    			return true;
		    		}
				} catch (ParseException e) {
					logger.error(e.getLocalizedMessage(),e);
					return true;
				}
		    }
    	}
    	return false;
    }

    public void reportMdbInsClientInfo(long version,long lastVersion) {
    	synchronized (visitorInfos) {
    		visitorInfoDefault.heartBeat();    		
    		MdbParamDataWrapper.delInsPoGroupVisitorIds(poContainerGroup.getPoGroupName(), lastVersion);
    		MdbParamDataWrapper.setInsPoGroupVisitorIds(poContainerGroup.getPoGroupName(), version, visitorIds);
    		for(String visitorId : visitorIds) {
    			visitorInfos.getOrDefault(visitorId,visitorInfoDefault).refreshToMdb(poContainerGroup.getPoGroupName(), visitorId, version,lastVersion);
			}
    	}
    }
    
    ///删除访问者信息
    public void delMdbInsClientInfo(long version) {
    	synchronized (visitorInfos) {
    		MdbParamDataWrapper.delInsPoGroupVisitorIds(poContainerGroup.getPoGroupName(), version);
	    	for(String visitorId : visitorIds) {
				MdbParamDataWrapper.delInsPoGroupVisitorInfo(poContainerGroup.getPoGroupName(), version,visitorId);
			}
    	}
    }
    //线程不安全
//	public Set<String> getVisitorIds() {
//		return visitorIds;
//	}
//	
//	public Map<String,MdbParamDataWrapper.VISITOR_INFO> getVisitorInfos(){
//		return visitorInfos;
//	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//下面的是公共接口
	////////////////////////////////////////////////////////////////////////////////////////
	///打印简要信息
    public void dump() {
		innerDump(false);
	}
	
  ///打印数据信息
	public void dumpDataAndIndex() {
		innerDump(true);
	}
	
	///打印数据、索引、访问者信息
	private void innerDump(boolean isDumpDataAndIndex) {
		synchronized (visitorInfos) {
			Iterator<Entry<String,MdbParamDataWrapper.VisitorInfo>> iter=visitorInfos.entrySet().iterator();
			Map.Entry<String,MdbParamDataWrapper.VisitorInfo> entry;
			while(iter.hasNext()) {
				entry=iter.next();
				logger.info("[{}{}{}]==>{}",poContainerGroup.getPoGroupName(),Const.KEY_JOIN,entry.getKey(),entry.getValue().toString());
			}
			if(isDumpDataAndIndex) {
				poContainerGroup.dumpDataAndIndex();
			}else {
				poContainerGroup.dump();
			}
		}
	}

	public int getIndex() {
		return index;
	}
}
