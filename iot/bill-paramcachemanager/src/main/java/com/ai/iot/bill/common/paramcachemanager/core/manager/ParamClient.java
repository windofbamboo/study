package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [参数缓存业务线程客户端类]
 * 1）线程每个话单事务访问前需设置访问者的读状态
 * 2）线程需和管理线程定时刷新心跳时间
 * 3）客户端启动和退出需进行注册和注销
 * 4）将来增加客户端汇报的过滤机制(TODO)
 * 具体用法,请参考对应的AppTest
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 * */
public class ParamClient {
	private final static Logger logger = LoggerFactory.getLogger(ParamClient.class);
    /** 客户端标识*/
    private String clientId;

	/** PO组数据集*/
    private Map<String, ParamCacheAB> localParamGroupCacheABMap;

	/** 本线程对应的访问者信息:map<pogroupname,list<visitorinfo>> list.size=2*/
    private Map<String,List<MdbParamDataWrapper.VisitorInfo>> visitorInfoABMap=new HashMap<String,List<MdbParamDataWrapper.VisitorInfo>>();
    
    ///key信息,临时生成
    StringBuffer keyPo=new StringBuffer();
    
    ///上次心跳时间
    private long lastHbTime=DateUtil.nowAbsSeconds();
    
    @SuppressWarnings("unused")
	private ParamClient() {}
    
    public ParamClient(String clientId,Map<String, ParamCacheAB> localParamGroupCacheABMap) {
    	this.clientId=clientId;
    	this.localParamGroupCacheABMap=localParamGroupCacheABMap;
    }
    
    /** 客户端使用前需先注册,需同时记录业务线程实例缓存信息*/
    public boolean register() {
    	logger.info("==>client register({})...",clientId);
    	//记录业务线程实例缓存信息
    	Iterator<Entry<String, ParamCacheAB>> iter=localParamGroupCacheABMap.entrySet().iterator();
    	Map.Entry<String, ParamCacheAB> entry;
    	while(iter.hasNext()) {
    		entry=iter.next();
    		entry.getValue().getCurrentCache().clientRegister(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		entry.getValue().getAnotherCache().clientRegister(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		entry.getValue().getCurrentCache().clientRegisterMdbInsClientInfo(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		if(visitorInfoABMap.containsKey(entry.getKey())) {
    			continue;
    		}
    		visitorInfoABMap.put(entry.getKey(), entry.getValue().getVisitorInfoAB(clientId));
    	}
    	return true;
    }
    
    /** 处理事件前需调用,通知管理线程正在读*/
    public boolean beginTrans() {
    	heartBeat();
    	logger.info("==>client beginTrans({})...",clientId);
    	Iterator<Entry<String, List<MdbParamDataWrapper.VisitorInfo>>> iter=visitorInfoABMap.entrySet().iterator();
    	Map.Entry<String, List<MdbParamDataWrapper.VisitorInfo>> entry;
    	while(iter.hasNext()) {
    		entry=iter.next();
    		entry.getValue().get(localParamGroupCacheABMap.get(entry.getKey()).getCurrentCachePos()).
    				setReadStatus(MdbParamDataWrapper.VisitorInfo.ReadStatusEnum.READING);
    	}
    	return true;
    }
    
    /** 处理事件后也需调用,通知管理线程不再读*/
    public void endTrans() {
    	logger.info("==>client endTrans({})...",clientId);
    	Iterator<Entry<String, List<MdbParamDataWrapper.VisitorInfo>>> iter=visitorInfoABMap.entrySet().iterator();
    	Map.Entry<String, List<MdbParamDataWrapper.VisitorInfo>> entry;
    	while(iter.hasNext()) {
    		entry=iter.next();
    		entry.getValue().get(localParamGroupCacheABMap.get(entry.getKey()).getCurrentCachePos()).
    				setReadStatus(MdbParamDataWrapper.VisitorInfo.ReadStatusEnum.NO_READING);
    	}
    }
    
    /** 心跳方法,空闲时需要调用,通知管理线程'我是活的'*/
    public void heartBeat() {
    	long nowTime=DateUtil.nowAbsSeconds();
    	if(nowTime-lastHbTime < ParamCacheManagerConfigure.getGlobalInstance().getVisitorHbSeconds()) {
    		return;
    	}
    	lastHbTime=nowTime;
    	logger.info("==>client heartBeat({})...",clientId);
    	Iterator<Entry<String, List<MdbParamDataWrapper.VisitorInfo>>> iter=visitorInfoABMap.entrySet().iterator();
    	Map.Entry<String, List<MdbParamDataWrapper.VisitorInfo>> entry;
    	while(iter.hasNext()) {
    		entry=iter.next();
    		entry.getValue().get(localParamGroupCacheABMap.get(entry.getKey()).getCurrentCachePos()).heartBeat();
    	}
    }
    
    /** 线程退出以后不再使用时需调用,同时删除业务线程实例缓存信息*/
    public void unRegister() {
    	logger.info("==>client unRegister({})...bye,bye!",clientId);
    	Iterator<Entry<String, ParamCacheAB>> iter=localParamGroupCacheABMap.entrySet().iterator();
    	Map.Entry<String, ParamCacheAB> entry;
    	while(iter.hasNext()) {
    		entry=iter.next();
    		//AB内存都需要注册客户端信息
    		entry.getValue().getCurrentCache().clientUnRegister(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		entry.getValue().getAnotherCache().clientUnRegister(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		entry.getValue().getCurrentCache().clientUnRegisterClientMdbInsClientInfo(clientId, entry.getValue().getPoGroupInfo().getCurrentVersion());
    		visitorInfoABMap.remove(entry.getKey());
    	}
    }
    
    /** 
     * 查询数据,需指定Po组名称\Po名称\索引名称\key,其中key可以通过Po来获取.
     * 
     * */
    public List<PoBase>  getDatasByKey(String poGroupName,String poName,String indexName,String key) {
    	ParamCacheAB foundValue=localParamGroupCacheABMap.get(poGroupName);
    	if(CheckNull.isNull(foundValue)) {
    		return null;
    	}
    	///拼key: 305+POGROUPNAME+PONAME+UPDATEFLAG+INDEXNAME+KEY
    	keyPo.setLength(0);
    	keyPo.append(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX_DATA);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(poGroupName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(poName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(foundValue.getPoGroupInfo().getCurrentVersion());
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(indexName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(key);
    	return foundValue.getCurrentCache().getPoContainerGroup().getPoContainerIndexeMap().get(keyPo.toString());
    }
    public <T> List<T>  getTrueDatasByKey(String poGroupName,String poName,String indexName,String key) {
    	ParamCacheAB foundValue=localParamGroupCacheABMap.get(poGroupName);
    	if(CheckNull.isNull(foundValue)) {
    		return null;
    	}
    	///拼key: 305+POGROUPNAME+PONAME+UPDATEFLAG+INDEXNAME+KEY
    	keyPo.setLength(0);
    	keyPo.append(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX_DATA);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(poGroupName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(poName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(foundValue.getPoGroupInfo().getCurrentVersion());
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(indexName);
    	keyPo.append(Const.KEY_JOIN);
    	keyPo.append(key);
    	return foundValue.getCurrentCache().getPoContainerGroup().getPoContainerIndexeMap().getTrueList(keyPo.toString());
    }
    
    /** 
     * 查询po的所有数据,需指定Po组名称\Po名称.
     * 
     * */
    public List<PoBase>  getAllDatas(String poGroupName,String poName) {
    	ParamCacheAB foundValue=localParamGroupCacheABMap.get(poGroupName);
    	if(CheckNull.isNull(foundValue)) {
    		return null;
    	}
    	return foundValue.getCurrentCache().getPoContainerGroup().getPoContainerByName(poName).getPoDataList();
    }
    
    @SuppressWarnings("unchecked")
	public <T> List<T>  getAllTrueDatas(String poGroupName,String poName) {
    	ParamCacheAB foundValue=localParamGroupCacheABMap.get(poGroupName);
    	if(CheckNull.isNull(foundValue)) {
    		return null;
    	}
    	return (List<T>) foundValue.getCurrentCache().getPoContainerGroup().getPoContainerByName(poName).getPoDataList();
    }
    
    public String getClientId() {
		return clientId;
	}

    /**
     * 返回管理实例的id，用于和mdb查询
     * */
    public String getManagerInstanceId() {
        return MdbParamDataWrapper.getManagerInstanceId();
    }
    
    public Map<String, ParamCacheAB> getLocalParamGroupCacheABMap() {
		return localParamGroupCacheABMap;
	}
}
