package com.ai.iot.bill.common.mdb;

import java.util.HashMap;
import java.util.Map;

import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;

/**
 * mdb相关对象的管理器
 * mdb的连接都在此实现,反序列化在MdbTables实现
 * 关系:
 * 1)每个mo都独立的hashkey,也叫field,多个相同mdbkey的mo组成MdbTable,
 * 2)MdbTables实现了对一个MdbTable的查询和update等操作
 * 3)MMxxx封装多个MdbTables的业务操作,是MdbTables的进一步封装,一个mdb对应一个MMxxx,从MMBase继承
 * 此管理器主要实现:
 * 1)不同mdbkey对应的MdbTables的封装,一个观望着的实现
 * 2)对同一个mdb的业务接口的公共封装和归集,方便将来管理和升级
 * 模块调用方式:
 * 1)使用MMFactory获取一个MM对象mmxxx,默认使用master
 * 2)一个话单的处理:
 *   a)mmxxx.reset();
 *   b)各种业务查询和修改接口,如果要修改数据,可以调用mmxxx.update(),也可以调用派生类封装对应的api修改接口
 *   c)mmxxx.commit();
 * 3)如果仅仅是查询,可以省略commit();
 *   a)mmxxx.reset();
 *   b)查询: mmxxx.yyyyy();
 * 增加一个mm:
 * 1)先定义keyid和field的常量(RedisConst)
 * 1)先增加mo
 * 2)再增加mt
 * 3)最后增加mm,同时实现业务
 * 4)修改MMFactory
 * */
public abstract class MMBase {
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	protected Map<String,MMBase>  mmIntances=new HashMap<String,MMBase>();
	
	///连接MDB的集群管理对象
	protected CustJedisCluster mdbClient4Cluster;
	
	/**不同mdbkey的MdbTables在此定义,一个mdbkey一个MdbTables对象,map<mdbkey,MdbTables>*/
	protected Map<String,MdbTables> wholeMdb=new HashMap<String,MdbTables>();
	///////////////////////////////////////////////////////////////////////////////
	///产生一个MM对象
	///////////////////////////////////////////////////////////////////////////////
	public MMBase(int mdbConnectType,boolean isMdbMaster) throws MdbCommonException{
		if(isMdbMaster)
			mdbClient4Cluster=RedisMgr.getJedisCluster(mdbConnectType,RedisMgr.MDB_MASTER);
		else
			mdbClient4Cluster=RedisMgr.getJedisCluster(mdbConnectType,RedisMgr.MDB_SLAVE);
		init(mdbClient4Cluster);
	}
	
	public MMBase(CustJedisCluster jc) throws MdbCommonException{
		init(jc);
	}

	///////////////////////////////////////////////////////////////////////////////
	///开放的接口
	///////////////////////////////////////////////////////////////////////////////
	abstract protected void init() ;
	
	public void reset() {
		wholeMdb.forEach((k,v) -> {v.reset();	});
	}
	public void update(String mdbTableName,String field,Object value) throws MdbCommonException {
		MdbTables found=wholeMdb.get(mdbTableName);
		if(found==null)
			throw new MdbCommonException(MdbCommonException.MdbCommonExceptionENUM.MDB_TABLE_NAME_NOT_EXISTS,"mdbTableName="+mdbTableName);
		found.setData(field, value);
	}
	/**会把所有的key都提交一遍,但不负责事务一致性,返回每个key对应的提交结果*/
	public Map<String,Integer> commit() {
		Map<String,Integer> result=new HashMap<String,Integer>();
		for(Map.Entry<String,MdbTables> mt:wholeMdb.entrySet()) {
		    if(mt.getValue().hasVer()) {
		        if(mt.getValue().updateTablesWithVer()==Const.FAIL) {
	                result.put(mt.getKey(), Const.FAIL);
	            }else {
	                result.put(mt.getKey(), Const.OK);
	            }
		    }else {
		        if(mt.getValue().updateTables()==Const.FAIL) {
	                result.put(mt.getKey(), Const.FAIL);
	            }else {
	                result.put(mt.getKey(), Const.OK);
	            }
		    }
			
		}
		return result;
	}
	///////////////////////////////////////////////////////////////////////////////
	///初始化接口
	///////////////////////////////////////////////////////////////////////////////
	protected void addMdbTable(MdbTables mts,MdbTable mt) {//一般是MdbTables:MdbTable=1:1,所以下面的查询是多余的
		wholeMdb.put(mt.getMdbTableKeyId(), mts);
		wholeMdb.get(mt.getMdbTableKeyId()).setMdbTable(mt);
	}
	
	private void init(CustJedisCluster jc)throws MdbCommonException{
		mdbClient4Cluster=jc;
		if(CheckNull.isNull(mdbClient4Cluster)) {
			throw new MdbCommonException(MdbCommonException.MdbCommonExceptionENUM.JEDIS_CLUSTER_IS_NULL);
		}
		mmIntances.put(this.getClass().getName(),this);
		init();
	}

}
