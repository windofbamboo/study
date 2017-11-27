package com.ai.iot.bill.common.paramcachemanager.core.pobase;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.manager.PoGroupCache;
import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegister;
import com.ai.iot.bill.common.paramcachemanager.pog.PoGroupRegisterFactory;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [Po组数据存储类]
 * 多个Po构成一个Po组，同步和版本切换以Po组作为最小单元
 * 该类包括Po组下面所有Po的数据记录集和对应的索引数据集
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * 
 * */
public class PoContainerGroup {
	private final static Logger logger = LoggerFactory.getLogger(PoContainerGroup.class);
	public final static int NONE_INDEX=-1;
	/**存放所有的数据,map<po,data>*/
    private Map<String,PoContainer> poContainerMap = new HashMap<String,PoContainer>();
    
    ///** 存放索引和对应的索引数据,key=PONAME+UPDATEFLAG+INDEXNAME+KEY*/
    private PoGroupCache poGroupCache;
    //private Map<String, List<PoBase> > poContainerIndexeMap=new  HashMap<String, List<PoBase> >();
    
    /** po组名称*/
    private String poGroupName;
    
    /**此块内存对应的下标*/
    private int index;
    
    public PoContainerGroup(String poGroupName) {
    	this(poGroupName,0);
	}
    
    public PoContainerGroup(String poGroupName,int index) {
    	this.index=index;
    	this.poGroupName=poGroupName;
    	if(index>=0) {
    		this.poGroupCache=new PoGroupCache(poGroupName,index);
    	}else {
    		this.poGroupCache=null;
    	}
	}

	/** 已经返回了Map，该函数去除*/
//    public void addPoContainer(PoContainer poContainer) {
//    	if(poContainerMap.containsKey(poContainer.getPoBase().getPoName()))
//    		return;
//    	poContainerMap.put(poContainer.getPoBase().getPoName(),poContainer);
//    }
    
    /** 返回该po组下面的所有po数据*/
    public Map<String,PoContainer> getPoContainerMap() {
    	return poContainerMap;
    }
    
    /**返回该po组指定po名下的po数据*/
    public PoContainer getPoContainerByName(String poName) {
		return poContainerMap.get(poName);
	}
    
    /**返回po组名*/
	public String getPoGroupName() {
		return poGroupName;
	}
	
	/**重置即删除数据和索引数据*/
	public void resetDataAndIndexes() {
		resetPoContainerIndexes();
		Iterator<Entry<String,PoContainer>> iter=poContainerMap.entrySet().iterator();
		Map.Entry<String,PoContainer> entry;
		while(iter.hasNext()) {
	    	entry=iter.next();
	    	entry.getValue().resetPoContainer();
		}
	}
	
	/***********************************************************************
	 * 从Mdb里边获取下面的数据:
	 * PO组信息
     * PO组PO记录数
     * PO组PO记录集（多key）
     * PO组PO索引名集
	 * ***********************************************************************/
	public boolean setAllPoDataFromMDB(long version) {
			///先核查一下Po组下面是否AB都在业务线程中使用的情况。
    		///产生同一个Po组下所有的Po对象，仅对新增的做处理！！！
			logger.info("setAllPoDataFromMDB({})...",poGroupName);
    		PoGroupRegister pgr=PoGroupRegisterFactory.getPoObjects(poGroupName);
    		Iterator<Entry<String,PoBase>> iter=pgr.getAllPoObjects().entrySet().iterator();
    		Map.Entry<String,PoBase> entry;
    		Iterator<Entry<String,PoContainer>> iterPoContainerGroup;
    		Map.Entry<String,PoContainer> entryPoContainerGroup;
    		
    	    while(iter.hasNext()) {
    	    	entry=iter.next();
				if(poContainerMap.containsKey(entry.getKey()))
		    		continue;
		    	poContainerMap.put(entry.getKey(),new PoContainer(entry.getValue()));
				logger.info("Add new PoContainer : {}",entry.getValue().getPoName());
    	    }

    	  ///重置数据
    	    try {
    	    	///重置每个Po对象的数据
	    	    iterPoContainerGroup=poContainerMap.entrySet().iterator();
	    		while(iterPoContainerGroup.hasNext()) {
	    			entryPoContainerGroup=iterPoContainerGroup.next();
	    			entryPoContainerGroup.getValue().setPoDataFromMDB(version);
	    		}
	    	
	    		///重置索引
	    		createIndexDatas(version);
    	    }catch(Exception e) {
    	    	logger.error(e.getLocalizedMessage(),e);
				return false;
    	    }
    		return true;
    }
        
    /***********************************************************************
	 * 汇报下面的缓存信息到MDB
	 * PARAM_INS_GROUP_PO_DATA_SIZE（35200）
     * ***********************************************************************/
	public boolean reportAllPoCacheInfo(long version,long lastVersion) {
			logger.info("reportAllPoCacheInfo({})...",poGroupName);
    		PoGroupRegister pgr=PoGroupRegisterFactory.getPoObjects(poGroupName);
    		Iterator<Entry<String,PoBase>> iter=pgr.getAllPoObjects().entrySet().iterator();
    		Map.Entry<String,PoBase> entry;
    		PoContainer poContainer;
    		
    	    while(iter.hasNext()) {
    	    	entry=iter.next();
    	    	poContainer=poContainerMap.get(entry.getKey());
    	    	//删除数据
    	    	MdbParamDataWrapper.delInsPoDataSize(poGroupName, entry.getKey(), lastVersion);
    	    	if(CheckNull.isNull(poContainer)) {
    	    		MdbParamDataWrapper.setInsPoDataSize(poGroupName, entry.getKey(), version,0L);//如果没数据也插入
    	    	}else {
    	    		MdbParamDataWrapper.setInsPoDataSize(poGroupName, entry.getKey(), version,poContainer.getPoDataList().size());
    	    	} 
    	    }
    		return true;
    }
	
	/**删除Mdb里边实例化的缓存汇报信息*/
	public void cleanMdbInsAllPoCacheInfo(long version) {
		logger.info("cleanMdbInsAllPoCacheInfo({})...",poGroupName);
		PoGroupRegister pgr=PoGroupRegisterFactory.getPoObjects(poGroupName);
		Iterator<Entry<String,PoBase>> iter=pgr.getAllPoObjects().entrySet().iterator();
		Map.Entry<String,PoBase> entry;
		
	    while(iter.hasNext()) {
	    	entry=iter.next();
	    	//删除数据
	    	MdbParamDataWrapper.delInsPoDataSize(poGroupName, entry.getKey(), version);
	    }
	}
	
	//////////////////////////////////////////////////////////////////////
	//下面的是索引操作
	//////////////////////////////////////////////////////////////////////
	
	/** 返回索引名和对应的索引数据,key=PONAME+UPDATEFLAG+INDEXNAME+KEY*/
    public PoGroupCache getPoContainerIndexeMap() {
		return poGroupCache;
	}

    ///重置数据和索引
    private void resetPoContainerIndexes() {
    	poGroupCache.clear();
    }
    
    /**创建该PO组对应的索引数据，根据定义的索引创建对应的索引数据
     * @throws Exception 
     */
    private void createIndexDatas(long version) throws Exception   {
    	Map<String,Map<String,Method>> poGroupIndexMethods=PoGroupRegisterFactory.getAllPoGroupIndexMethods().get(poGroupName);
    	if(CheckNull.isNull(poGroupIndexMethods)) {
    		return;
    	}
    	Iterator<Entry<String,Map<String,Method>>> iterPo=poGroupIndexMethods.entrySet().iterator();
    	Map.Entry<String,Map<String,Method>> entryPo;
    	Iterator<Entry<String, Method>> iterIndex;
    	Map.Entry<String,Method> entryIndex;
    	StringBuilder  key=new StringBuilder ();
        StringBuilder keyPrefix=new StringBuilder ();
        int keyPrefixLength;
        PoBaseLinkedList foundValue;
        PoContainer poContainer;//包含po继承类和po数据
        Date beginDatePo,beginDatePoIndex;
        int indexDataSize=0,totalIndexDataSize=0,totalDataSize=0;
        
		while(iterPo.hasNext()) {
			entryPo=iterPo.next();
			beginDatePo=new Date();
			//logger.info("{}->createIndexData({}.{}) : methodsize={}",DateUtil.getCurrentDateTime(beginDatePo,DateUtil.SEG_YYYYMMDD_HHMMSS),
			//		poContainerGroup.getPoGroupName(),entryPo.getKey(),entryPo.getValue().size());
			poContainer=poContainerMap.get(entryPo.getKey());
			//logger.info("create({}.{})...",poContainerGroup.getPoGroupName(),entryPo.getKey());
			///初始化key前缀
	        keyPrefix.setLength(0);
	        keyPrefix.append(poContainer.getPoBase().getPoPrefix(version));
	        keyPrefixLength=keyPrefix.length();
	    	key=keyPrefix;
			/// 索引遍历
    		iterIndex = entryPo.getValue().entrySet().iterator();
			while(iterIndex.hasNext()) {
    			entryIndex = iterIndex.next();
    			beginDatePoIndex=new Date();
    			//logger.info("{}->createIndexData({}.{}.{}) : datasize={}",DateUtil.getCurrentDateTime(beginDatePoIndex,DateUtil.SEG_YYYYMMDD_HHMMSS),
    			//		poContainerGroup.getPoGroupName(),entryPo.getKey(),entryIndex.getKey(),poContainer.getPoDataList().size());
    			///创建索引数据
    			indexDataSize=0;
    			for(PoBase data:poContainer.getPoDataList()) {
        			///创建索引数据的key
        			key.delete(keyPrefixLength, key.length());
        			key.append(entryIndex.getKey());
        			key.append(Const.KEY_JOIN);
        			try {
						key.append(entryIndex.getValue().invoke(data));
					} catch (Exception e) {
						logger.error("{}->createIndexData({}.{}.{}.{}) .INDEXDATA: {}={}",DateUtil.getCurrentDateTime(beginDatePoIndex,DateUtil.SEG_YYYYMMDD_HHMMSS),
		    					poGroupName,entryPo.getKey(),entryIndex.getKey(),indexDataSize,key,data);
						logger.error(e.getLocalizedMessage()+"={}",e);
						throw e;
					}
        			///插入数据，如果已经有记录了，就进行追加
	    			foundValue=poGroupCache.get(key.toString());
	    			if(CheckNull.isNull(foundValue)) {
	    				foundValue=new PoBaseLinkedList();
	    				poGroupCache.put(key.toString(), foundValue);
	    				indexDataSize++;
		    			totalIndexDataSize++;
	    			}
	    			foundValue.add(data);	   
	    			if(indexDataSize<=3) {//仅打印3条记录,减少屏幕信息量
	    				logger.info("{}->createIndexData({}.{}.{}.{}) .INDEXDATA: {}={}",DateUtil.getCurrentDateTime(beginDatePoIndex,DateUtil.SEG_YYYYMMDD_HHMMSS),
	    					poGroupName,entryPo.getKey(),entryIndex.getKey(),indexDataSize,key,data);
	    			}else {
	    				if(indexDataSize==4) {//仅显示一次
		    				logger.info("{}->createIndexData({}.{}.{}.{}) .INDEXDATA: ...<more>",DateUtil.getCurrentDateTime(beginDatePoIndex,DateUtil.SEG_YYYYMMDD_HHMMSS),
			    					poGroupName,entryPo.getKey(),entryIndex.getKey(),indexDataSize);
	    				}
	    			}
    			}
    			totalDataSize+=poContainer.getPoDataList().size();
    			logger.info("{}->createIndexData({}.{}.{}) : datasize={},indexdatasize={},total(secs)={}",DateUtil.getCurrentDateTime(beginDatePoIndex,DateUtil.SEG_YYYYMMDD_HHMMSS),
    					poGroupName,entryPo.getKey(),entryIndex.getKey(),poContainer.getPoDataList().size(),indexDataSize,DateUtil.diffSeconds(new Date(),beginDatePoIndex));
    		}
			logger.info("{}->createIndexData({}.{}.TOTAL) : methodsize={},datasize={},indexdatasize={},total(secs)={}",DateUtil.getCurrentDateTime(beginDatePo,DateUtil.SEG_YYYYMMDD_HHMMSS),
					poGroupName,entryPo.getKey(),entryPo.getValue().size(),totalDataSize,totalIndexDataSize,DateUtil.diffSeconds(new Date(),beginDatePo));
		}
    }
    
	///打印简要信息
    public void dump() {
		innerDump(false);
	}
	
    ///打印简要信息和数据信息
	public void dumpDataAndIndex() {
		innerDump(true);
	}
	
	private void innerDump(boolean isDumpDataAndIndex) {
		
		Iterator<Entry<String,PoContainer>> iterPo=poContainerMap.entrySet().iterator();
    	Map.Entry<String,PoContainer> entryPo;
    	
    	while(iterPo.hasNext()) {
			entryPo=iterPo.next();
			logger.info("[{}{}{}]==>DataNum={}",poGroupName,Const.KEY_JOIN,entryPo.getKey(),entryPo.getValue().getPoDataList().size());
    	}
    	
    	///因为索引数据已经打印，所以这里暂时不打印了
//    	if(!isDumpDataAndIndex)
//    		return;
//    	
//    	iterPo=poContainerMap.entrySet().iterator();
//    	long i=0;
//    	long total=0;
//    	
//    	while(iterPo.hasNext()) {
//    		entryPo=iterPo.next();
//    		i++;
//    		total=entryPo.getValue().getPoDataList().size();
//    		for(PoBase poBase:entryPo.getValue().getPoDataList()) {
//    		logger.info("[{}{}{}]==>[{}.{}]{}",poGroupName,Const.KEY_JOIN,entryPo.getKey(),
//    								total,i,poBase.toString());
//    		}
//		}
    	innerDumpIndex(isDumpDataAndIndex);
	}
	
	private void innerDumpIndex(boolean isDumpDataAndIndex) {
		Map<String,Map<String,Method>> poGroupIndexMethods=PoGroupRegisterFactory.getAllPoGroupIndexMethods().get(poGroupName);
    	if(CheckNull.isNull(poGroupIndexMethods)) {
    		return;
    	}
		Iterator<Entry<String,Map<String,Method>>> iterPo=poGroupIndexMethods.entrySet().iterator();
    	Map.Entry<String,Map<String,Method>> entryPo;
    	Iterator<Entry<String, Method>> iterIndex;
    	Map.Entry<String,Method> entryIndex;
    	List<PoBase> poIndexDataList;
    	
    	while(iterPo.hasNext()) {
			entryPo=iterPo.next();
			if(CheckNull.isNull(entryPo.getValue()))
				continue;
			iterIndex = entryPo.getValue().entrySet().iterator();
			while(iterIndex.hasNext()) {
    			entryIndex = iterIndex.next();
    			poIndexDataList=poGroupCache.get(entryPo.getKey());
    			if(CheckNull.isNull(entryIndex.getValue())||CheckNull.isNull(poIndexDataList))
    				continue;
    			logger.info("[{}{}{}]==>IndexName={},DataNum={}",poGroupName,Const.KEY_JOIN,entryPo.getKey(),
    								entryIndex.getKey(),poIndexDataList.size());
			}
    	}
    	if(!isDumpDataAndIndex)
    		return;
    	poGroupCache.dumpIndex();
//    	Iterator<Entry<String, List<PoBase> >> iterIndexData=poContainerIndexeMap.entrySet().iterator();
//    	Map.Entry<String,List<PoBase>> entryIndexData;
//    	while(iterIndexData.hasNext()) {
//    		entryIndexData=iterIndexData.next();
//    		logger.info("[{}{}{}]==>ResultNum={}",poGroupName,Const.KEY_JOIN,entryIndexData.getKey(),entryIndexData.getValue().size());
//		}
	}

	///返回此块内存对应的下标
	public int getIndex() {
		return index;
	}
}
