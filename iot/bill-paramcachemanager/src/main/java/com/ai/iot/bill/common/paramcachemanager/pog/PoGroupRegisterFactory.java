package com.ai.iot.bill.common.paramcachemanager.pog;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.mdb.MdbParamDataWrapper;
import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamCacheManagerImp;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.pog.bill_group.PoGroupRegisterBillGroup;
import com.ai.iot.bill.common.paramcachemanager.pog.grant.PoGroupRegisterGrant;
import com.ai.iot.bill.common.paramcachemanager.pog.operator.PoGroupRegisterOperator;
import com.ai.iot.bill.common.paramcachemanager.pog.rate_plan.PoGroupRegisterRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.route.PoGroupRegisterRoute;
import com.ai.iot.bill.common.paramcachemanager.pog.sample.PoGroupRegisterSample;
import com.ai.iot.bill.common.paramcachemanager.pog.std_rate_plan.PoGroupRegisterStdRatePlan;
import com.ai.iot.bill.common.paramcachemanager.pog.sys.PoGroupRegisterSys;
import com.ai.iot.bill.common.paramcachemanager.pog.zone.PoGroupRegisterZone;
import com.ai.iot.bill.common.util.CheckNull;

/**
 * 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [PO组注册类，将所有的PO组注册到一起] 
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 */
public class PoGroupRegisterFactory {
	private final static Logger logger = LoggerFactory.getLogger(ParamCacheManagerImp.class);
	
	/**存放po对象*/
	private static Map<String,PoGroupRegister> allPoGroupObjects=new HashMap<String,PoGroupRegister>();
	
	/** 存放索引方法:map<po组,map<po,set<index>>>*/
    private static Map<String,Map<String,Map<String,Method>>> allPoGroupIndexMethods=new HashMap<String,Map<String,Map<String,Method>>>();
    
	///定义POG的名字
	public static enum PoGroupNameEnum{
		POG_SAMPLE("POG_SAMPLE"),
		POG_ADDUP("POG_ADDUP"),
		POG_BILL_GROUP("POG_BILL_GROUP"),
		POG_GRANT("POG_GRANT"),
		POG_OPERATOR("POG_OPERATOR"),
		POG_RATE_PLAN("POG_RATE_PLAN"),
		POG_ROUTE("POG_ROUTE"),
		POG_STD_RATE_PLAN("POG_STD_RATE_PLAN"),
		POG_SYS("POG_SYS"),
		POG_ZONE("POG_ZONE")
		;
		private PoGroupNameEnum(String s){
			name=s;
		}
		public String getName() {
			return name;
		}
		private String name;
	}
	///下面的是po组方法
	public static Map<String,PoGroupRegister> getAllPoGroupObjects(){
		return allPoGroupObjects;
	}
	
	public static PoGroupRegister getPoObjects(String poGroupName){
		return allPoGroupObjects.get(poGroupName);
	}
	
	/** 返回索引名: map<po组,map<po,set<index>>>*/
    public static Map<String,Map<String,Map<String,Method>>> getAllPoGroupIndexMethods() {
    	return allPoGroupIndexMethods;
    }
    
    /**对外初始化接口*/
    public static void create() {
    	createAllPoGroupObjects(null);
    	createAllPoGroupIndexMethods();
    }
    
	public static void create(Set<String> usedPoGroupNames) {
		createAllPoGroupObjects(usedPoGroupNames);
    	createAllPoGroupIndexMethods();
	}
    
	///注册所有的Po组对象,增加Po组时需要在此增加。
    private static void createAllPoGroupObjects(Set<String> usedPoGroupNames) {
    	///////////////////////////////////////////////////////////////////////
    	///新增PO组
    	///////////////////////////////////////////////////////////////////////
    	addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterSample());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterBillGroup());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterGrant());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterOperator());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterRatePlan());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterRoute());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterStdRatePlan());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterSys());
		addOnePoGroupObjects(usedPoGroupNames,new PoGroupRegisterZone());
		///////////////////////////////////////////////////////////////////////
		///设置300+POG信息
		Set<String> poGroupNames=new HashSet<String>();
		for(PoGroupNameEnum name:PoGroupNameEnum.values()) {
		    poGroupNames.add(name.getName());
		}
		MdbParamDataWrapper.setPoGroupNames(poGroupNames);
	}
	
	private static void addOnePoGroupObjects(Set<String> usedPoGroupNames,PoGroupRegister poGroupRegister) {
		if(!CheckNull.isNull(usedPoGroupNames) &&  usedPoGroupNames.size() > 0 && !usedPoGroupNames.contains(poGroupRegister.getPoGroupName())) {
			return;//如果个性化指定了哪些需要使用的Po组,那么不在集合里边的Po组就不加载
		}
		if(allPoGroupObjects.containsKey(poGroupRegister.getPoGroupName())) {
			return;
		}
		poGroupRegister.setAllPoObjects();
		allPoGroupObjects.put(poGroupRegister.getPoGroupName(), poGroupRegister);
	}
	
	/**创建所有的索引定义，方便下次快速访问*/
	private static void createAllPoGroupIndexMethods() {
    	Iterator<Entry<String, PoGroupRegister>> iterPoGroup=allPoGroupObjects.entrySet().iterator();
    	Map.Entry<String,PoGroupRegister> entryPoGroup;
    	Iterator<Entry<String, PoBase>> iterPo;
    	Map.Entry<String,PoBase> entryPo;
        Map<String,Map<String,Method>> poGroupIndexNameMap;
        Map<String,Method> poIndexNameMap;
        Map<String,Method> newPoIndexNameMap;
        
    	while(iterPoGroup.hasNext()) {
    		entryPoGroup=iterPoGroup.next();
    		poGroupIndexNameMap=allPoGroupIndexMethods.get(entryPoGroup.getKey());
    		if(CheckNull.isNull(poGroupIndexNameMap)) {
    			if(entryPoGroup.getValue().getAllPoObjects().isEmpty()) {
    				continue;
    			}
    			poGroupIndexNameMap=new HashMap<String,Map<String,Method>>();
    			allPoGroupIndexMethods.put(entryPoGroup.getKey(), poGroupIndexNameMap);
    		}
    		iterPo=entryPoGroup.getValue().getAllPoObjects().entrySet().iterator();
    		while(iterPo.hasNext()) {
    			entryPo=iterPo.next();
    			try {
    				newPoIndexNameMap=entryPo.getValue().createIndexMethods();
    				if(CheckNull.isNull(newPoIndexNameMap)) {
    					logger.error("No Indexes:{}.{}",entryPoGroup.getKey(),entryPo.getKey());
    					continue;
    				}					
					if(newPoIndexNameMap.isEmpty()) {
						continue;
					}else {
						logger.info("Check Indexes:{}.{}",entryPoGroup.getKey(),entryPo.getKey());
						newPoIndexNameMap.forEach(new BiConsumer<String,Method>(){
							@Override
							public void accept(String key, Method value) {
								if(CheckNull.isNull(key)) {
									logger.error("One Index Name Is Null");
								}
								if(CheckNull.isNull(value)) {
									logger.error("One Index Method Is Null");
								}
							}
						});
					}
				} catch (NoSuchMethodException | SecurityException e) {
					logger.error(e.getLocalizedMessage(),e);
					continue;
				}
    			logger.info("createAllIndex({}.{})...",entryPo.getValue().getPoGroupName(),entryPo.getValue().getPoName());
    			poIndexNameMap=poGroupIndexNameMap.get(entryPo.getKey());
        		if(CheckNull.isNull(poIndexNameMap)) {
        			poIndexNameMap=new HashMap<String,Method>();
        			poGroupIndexNameMap.put(entryPo.getKey(), poIndexNameMap);
        		}    			
        		poIndexNameMap.putAll(newPoIndexNameMap);
    		}
    	}
    }
	 /***********************************************************************
	 * PARAM_INS_GROUP_PO_INDEX（35400）
	 * ***********************************************************************/
	public static boolean reportAllPoIndexInfo(String poGroupName,long version,long lastVersion) {
		Map<String,Map<String,Method>> poGroupIndexMethods=allPoGroupIndexMethods.get(poGroupName);
		if(CheckNull.isNull(poGroupIndexMethods)) {
			return true;
		}
   		Iterator<Entry<String,Map<String,Method>>> iter=poGroupIndexMethods.entrySet().iterator();
   		Map.Entry<String,Map<String,Method>> entry;
   		
   	    while(iter.hasNext()) {
   	    	entry=iter.next();
   	    	logger.info("reportAllPoIndexInfo({}+{}+{})...",poGroupName,entry.getKey(),version);
   	    	//删除数据
   	    	MdbParamDataWrapper.delInsPoIndexNames(poGroupName, entry.getKey(), lastVersion);
   	    	MdbParamDataWrapper.setInsPoIndexNames(poGroupName, entry.getKey(), version, entry.getValue().keySet());
   	    }
   		return true;
   }
	
	public static boolean cleanMdbInsAllPoIndexInfo(String poGroupName,long version) {
		Map<String,Map<String,Method>> poGroupIndexMethods=allPoGroupIndexMethods.get(poGroupName);
		if(CheckNull.isNull(poGroupIndexMethods)) {
			return true;
		}
   		Iterator<Entry<String,Map<String,Method>>> iter=poGroupIndexMethods.entrySet().iterator();
   		Map.Entry<String,Map<String,Method>> entry;
   		
   	    while(iter.hasNext()) {
   	    	entry=iter.next();
   	    	logger.info("reportAllPoIndexInfo({}+{}+{})...",poGroupName,entry.getKey(),version);
   	    	//删除数据
   	    	MdbParamDataWrapper.delInsPoIndexNames(poGroupName, entry.getKey(), version);
   	    }
   		return true;
   }
}
