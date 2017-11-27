package com.ai.iot.bill.common.paramcachemanager.pog.sample;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PmBase;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;

public class PmSample extends PmBase {
	///为减少内存的频繁申请引起gc,每个po在此产生一个永久的对象,用于临时数据传递.
	PoSample poSample=new PoSample();
	
	public PmSample(ParamClient paramClient) {
		super(paramClient);
	}
	
	/**返回指定索引1的key对应的所有数据
	 * @param key key数据,来自Po定义
	 * */
	public List<PoBase>  getPoSampleDatasByKey1(String tabGroupName) {
		return paramClient.getDatasByKey(poSample.getPoGroupName(), poSample.getPoName(),
															PoSample.getIndex1Name(),poSample.getIndex1Key()) ;
	}
	
	/**返回指定索引2的key对应的所有数据
	 * @param key key数据,来自Po定义
	 * */
	public List<PoBase>  getPoSampleDatasByKey2(long updateVersion) {
		poSample.setUpdateFlag(updateVersion);
		return paramClient.getDatasByKey(poSample.getPoGroupName(), poSample.getPoName(),
															PoSample.getIndex2Name(),poSample.getIndex2Key()) ;
	}
	
	/**返回指定索引3的key对应的所有数据
	 * @param key key数据,来自Po定义
	 * */
	public List<PoBase>  getPoSampleDatasByKey3(String tabGroupName,long updateVersion) {
		poSample.setTabGroupName(tabGroupName);
		poSample.setUpdateFlag(updateVersion);
		return paramClient.getDatasByKey(poSample.getPoGroupName(), poSample.getPoName(),
															PoSample.getIndex3Name(),poSample.getIndex3Key()) ;
	}
	
	/**
	 * 返回所有数据
	 * */
	public List<PoBase>  getPoSampleAllDatas() {
		return paramClient.getAllDatas(poSample.getPoGroupName(), poSample.getPoName());
	}
}
