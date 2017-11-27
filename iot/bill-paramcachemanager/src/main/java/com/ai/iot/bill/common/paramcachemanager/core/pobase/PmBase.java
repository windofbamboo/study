package com.ai.iot.bill.common.paramcachemanager.core.pobase;

import java.util.List;

import com.ai.iot.bill.common.paramcachemanager.core.manager.ParamClient;

/** 
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [Pm查询器基类]
 * 考虑到每个Po会对应一些通用的查询方法,提供给各个模块统一调用,对业务层进一步减少对底层的直接操作
 * 所以再增加查询层(Pm),对各个Po的查询进行封装处理
 * 建议在Pm里边对每个po类产生一个po对象,用于和临时数据传递数据,尽可能避免引擎gc
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public class PmBase {
	protected ParamClient paramClient;
	
	public PmBase(ParamClient paramClient) {
		this.paramClient=paramClient;;
	}
	
	public ParamClient getParamClient() {
		return paramClient;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	//下面的统一的方法
	//////////////////////////////////////////////////////////////////////////////////
	
	/**返回指定key的所有数据
	 * @param poGroupName po组名,来自Po定义
	 * @param poName  po名,来自Po定义
	 * @param indexName 索引名,来自Po定义
	 * @param key key数据,来自Po定义
	 * */
	public List<PoBase>  getDatasByKey(String poGroupName,String poName,String indexName,String key) {
		return paramClient.getDatasByKey(poGroupName,poName,indexName,key) ;
	}
	
	/**返回所有数据
	 * @param poGroupName po组名,来自Po定义
	 * @param poName  po名,来自Po定义
	 * */
	public List<PoBase>  getAllDatas(String poGroupName,String poName) {
		return paramClient.getAllDatas(poGroupName, poName);
	}
}
