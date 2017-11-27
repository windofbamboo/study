package com.ai.iot.bill.common.paramcachemanager.core.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBaseLinkedList;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;

import net.sf.ehcache.Element;

public class PoGroupCache {
	private final static Logger logger = LoggerFactory.getLogger(PoGroupCache.class);
	
	/**hashmap方式 :  存放索引和对应的索引数据,key=PONAME+UPDATEFLAG+INDEXNAME+KEY*/
    private Map<String, PoBaseLinkedList> poContainerIndexeMap=new  HashMap<String, PoBaseLinkedList >();
    
    /**ehcache方式 : Cache<String, PoIndexDataWrapper >*/
    private net.sf.ehcache.Cache  poContainerIndexeEhCache;
    
    /**ehcache3方式 : Cache<String, PoIndexDataWrapper>*/
    private org.ehcache.Cache<String, PoBaseLinkedList>  poContainerIndexeEhCache3;
    
    /**ehcache3方式使用,因为自身没带计数器*/
    private int ehcache3size=0; 
    
    /** po组名称*/
    private String poGroupName;
    
    /**cacheType来自配置对象,仅初始化一次*/
    private ParamCacheManagerConfigure configure;
    
    /**此块内存对应的下标*/
    private int index;
    
    public PoGroupCache(String poGroupName, int index) {
    	this.index=index;
    	this.poGroupName=poGroupName;
    	configure=ParamCacheManagerConfigure.getGlobalInstance();
    	if(configure.isEhCacheType()) {
    		poContainerIndexeEhCache=PoGroupCacheFactory.getGlobalInstance().getNewInstance(poGroupName,index);
    	}
    	if(configure.isEhCache3Type()) {
    		poContainerIndexeEhCache3=PoGroupCacheFactory4EhCache3.getGlobalInstance().getNewInstance(poGroupName, index);
    	}
    	
	}
   
    public void clear() {
    	if(configure.isEhCacheType()) {
    		poContainerIndexeEhCache.removeAll();
    		return;
    	}else if(configure.isEhCache3Type()) {
    		poContainerIndexeEhCache3.clear();
    		ehcache3size=0;
    		return;
    	}
    	poContainerIndexeMap.clear();
    }
    
    public int size() {
    	if(configure.isEhCacheType()) {
    		return poContainerIndexeEhCache.getSize();
    	}else if(configure.isEhCache3Type()) {
    		return ehcache3size;
    	}
    	return poContainerIndexeMap.size();
    }
    
	public PoBaseLinkedList get(String key) {
    	if(configure.isEhCacheType()) {
    		Element element=poContainerIndexeEhCache.get(key);
    		if(CheckNull.isNull(element)) {
    			return null;
    		}
    		return (PoBaseLinkedList) element.getObjectValue();
    	}else if(configure.isEhCache3Type()) {
    		PoBaseLinkedList pc=poContainerIndexeEhCache3.get(key);
    		if(CheckNull.isNull(pc)) {
    			return null;
    		}
    		return pc;
    	}
    	return poContainerIndexeMap.get(key);
    }
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getTrueList(String key) {
    	if(configure.isEhCacheType()) {
    		Element element=poContainerIndexeEhCache.get(key);
    		if(CheckNull.isNull(element)) {
    			return null;
    		}
    		return (List<T>) element.getObjectValue();
    	}else if(configure.isEhCache3Type()) {
    		PoBaseLinkedList pc=poContainerIndexeEhCache3.get(key);
    		if(CheckNull.isNull(pc)) {
    			return null;
    		}
    		return (List<T>) pc;
    	}
    	return (List<T>) poContainerIndexeMap.get(key);
    }
    
    public void put(String key,PoBaseLinkedList datas) {
    	if(configure.isEhCacheType()) {
    		datas.setElement(key);
    		poContainerIndexeEhCache.put(datas.getElement());
    		return;
    	}else if(configure.isEhCache3Type()) {
    		poContainerIndexeEhCache3.put(key, datas);
    		ehcache3size++;
    		return;
    	}
    	poContainerIndexeMap.put(key,datas);
    }
    
    public void dumpIndex() {
    	if(configure.isEhCacheType()) {
			@SuppressWarnings("unchecked")
			Iterator<String> iter=poContainerIndexeEhCache.getKeys().iterator();
    		String key;
    		int n;
    		Element element;
    		PoBaseLinkedList value;
    		
    		while(iter.hasNext()) {
    			key=iter.next();
    			element=poContainerIndexeEhCache.get(key);
    			if(CheckNull.isNull(element))
    				continue;
    			value=(PoBaseLinkedList) element.getObjectValue();
    			n=0;
    			for(PoBase poBase : value) {
    				n++;
    				logger.info("[{}{}{}][{}]==>{}",poGroupName,Const.KEY_JOIN,key,n,poBase.toString());
    			}
    		}
    		return;
    	}else if(configure.isEhCache3Type()) {
			poContainerIndexeEhCache3.forEach(new Consumer<org.ehcache.Cache.Entry<String,PoBaseLinkedList>>() {
			    @Override
			    public void accept(org.ehcache.Cache.Entry<String,PoBaseLinkedList> kv) {
			    	int n=0;
	    			for(PoBase poBase : kv.getValue()) {
	    				n++;
	    				logger.info("[{}{}{}][{}]==>{}",poGroupName,Const.KEY_JOIN,kv.getKey(),n,poBase.toString());
	    			}
			    }
			});
    		return;
    	}
    	Iterator<Entry<String,PoBaseLinkedList>> iter=poContainerIndexeMap.entrySet().iterator();
		Map.Entry<String, PoBaseLinkedList> entry;
		int n;
		
		while(iter.hasNext()) {
			entry=iter.next();
			n=0;
			for(PoBase poBase : entry.getValue()) {
				n++;
				logger.info("[{}{}{}][{}]==>{}",poGroupName,Const.KEY_JOIN,entry.getKey(),n,poBase.toString());
			}
		}
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
