package com.ai.iot.bill.common.paramcachemanager.core.mdb;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.nustaq.serialization.FSTConfiguration;

import com.ai.iot.bill.common.paramcachemanager.core.pobase.POException;
import com.ai.iot.bill.common.paramcachemanager.core.pobase.PoBase;
import com.ai.iot.bill.common.redisLdr.RedisConst;
import com.ai.iot.bill.common.util.CheckNull;
import com.ai.iot.bill.common.util.Const;
import com.ai.iot.bill.common.util.DateUtil;
import com.ai.iot.bill.common.util.SysUtil;
import redis.clients.jedis.ScanResult;

/** 
 * FST序列化工具本身不需要实现Serializable，实现Serializable是为了将来切换序列化工具方便
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [MDB对象操作封装类]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Version: [v1.0]
 * */
public final class MdbParamDataWrapper implements Serializable {
	
	private static final long serialVersionUID = 2627845860576764820L;
	///序列化工具,因为只有管理线程需要序列化和反序列化，所以暂时只用一个
	///后期如果改成多线程加载时，需要改成每个线程一个实例
	private static FSTConfiguration serializeTool = FSTConfiguration.createDefaultConfiguration();
	private static FSTConfiguration serializeJsonTool = FSTConfiguration.createJsonConfiguration(false, false);
	private static final String INDEX_STRING="INDEX";
	private static final String VISITOR_STRING="VISITOR";
	
	///序列化类型:0=FST;1=Json
	private static int serializeType=0;
	public MdbParamDataWrapper() {
		serializeTool.asByteArray("tt");
        serializeJsonTool.asByteArray("tt");//第一次序列化,防止后续序列化格式不同,影响redis的结果
	}
	///po组基本信息
	public static final class ParamGroup implements Serializable{
		private static final long serialVersionUID = 1930230484229945789L;
		private long currentVersion;//14位时间
		private long lastVersion;//14位时间

		public void setCurrentVersion(long currentVersion) {
			this.currentVersion = currentVersion;
		}
		public void setLastVersion(long lastVersion) {
			this.lastVersion = lastVersion;
		}
		public long getCurrentVersion() {
			return currentVersion;
		}
		public long getLastVersion() {
			return lastVersion;
		}
		public String toString() {
	    	return serializeJsonTool.asJsonString(this);
	    }
	}
	
	///访问者信息,该类管理线程和业务线程都会访问,需注意加锁的时间
	public static final class VisitorInfo implements Serializable ,Cloneable{
		private static final long serialVersionUID = -4952233528699734475L;
		private ReadStatusEnum readStatus;
		private long hbTime;//14位时间
		private long version;//14位时间
		
		public VisitorInfo() {
			readStatus=ReadStatusEnum.NO_READING;
			hbTime=Long.parseLong(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS));
			this.version=0;
		}
		
		VisitorInfo(VisitorInfo source) {
            this.hbTime=source.hbTime;
            this.version=source.version;
            this.readStatus=source.readStatus;
        }
        
		public void setReadStatus(ReadStatusEnum readStatus) {
			synchronized (this) {//仅对当前线程对应的访问者信息进行加锁,如果readStatus添加volatile,此处可不加锁
				this.readStatus = readStatus;
			}
		}
		
		public enum ReadStatusEnum{
			READING('1'),NO_READING('0');
			private ReadStatusEnum(char c) {
				status=c;
			}
			public char getStatus() {
					return status;
			}
			private char status;
		}
		public ReadStatusEnum getReadStatus() {
			synchronized (this) {//仅对当前线程对应的访问者信息进行加锁,如果readStatus添加volatile,此处可不加锁
				return readStatus;
			}
		}
		public long getHbTime() {
			return hbTime;
		}
		public long getVersion() {
			return version;
		}
		public void heartBeat() {
			hbTime=Long.parseLong(DateUtil.getCurrentDateTime(DateUtil.YYYYMMDD_HHMMSS));
		}
		public String toString() {
	    	return serializeJsonTool.asJsonString(this);
	    }
		
		///线程安全函数
		public void refreshToMdb(String poGroupName,String clientId,long version,long lastVersion) {
			VisitorInfo visitorInfoMdb;
			synchronized (this) {//仅对当前线程对应的访问者信息进行加锁
				visitorInfoMdb=new VisitorInfo(this);//可以先拷贝出来,因为后面Mdb操作比较耗时.
			}
			MdbParamDataWrapper.delInsPoGroupVisitorInfo(poGroupName, lastVersion,clientId);
    		MdbParamDataWrapper.setInsPoGroupVisitorInfo(poGroupName, version,clientId,visitorInfoMdb);
		}
	}
	/**
	 * 根据数据类型进行操作的统一方法定义
	 * */
	public static final class MdbParamDataImp{
	    private MdbParamDataImp() throws POException {
	        throw new POException(POException.POExceptionCodeENUM.CANNT_CREATE_INSTANCE_MANNUL);
	      }
	    //设置数据接口
		public static Set<String> getSetData(String key){
			Set<String> setData=new HashSet<>();
			ScanResult<byte[]> objects;
            objects = MdbClient4Param.getGlobalInstance().getCluster().sscan(key.getBytes(Const.UTF8), String.valueOf(0).getBytes(Const.UTF8));
			for(byte[] object:objects.getResult()) {
				setData.add(new String(object));
			}
			return setData;
		}
		///该key对应的set集合，覆盖方式。
		public static void setSetData(String key,Set<String> setData){
			for(String s : setData) {
				MdbClient4Param.getGlobalInstance().getCluster().sadd(key.getBytes(Const.UTF8), s.getBytes(Const.UTF8));
			}
		}
		
		///该key对应的set集合，增量方式。
		public static void addSetData(String key,String data){
			MdbClient4Param.getGlobalInstance().getCluster().sadd(key.getBytes(Const.UTF8), data.getBytes(Const.UTF8));
		}
		
		///删除key对应的某个对象数据,非整个集合删除
		public static void removeSetData(String key,String data){
			MdbClient4Param.getGlobalInstance().getCluster().srem(key.getBytes(Const.UTF8),data.getBytes(Const.UTF8));
		}
		
		///删除key
		public static void delSetData(String key){
			MdbClient4Param.getGlobalInstance().getCluster().del(key.getBytes(Const.UTF8));
		}
		///object------------------------------------------------
		@SuppressWarnings("unchecked")
		public static <T> T getObjectData(String key){
			byte[] serializableData;
            serializableData = MdbClient4Param.getGlobalInstance().getCluster().get(key.getBytes(Const.UTF8));
			if(CheckNull.isNull(serializableData))
				return null;
			if(MdbParamDataWrapper.serializeType==0) {
				return (T)serializeTool.asObject(serializableData);			
			}else {
				return (T)serializeJsonTool.asObject(serializableData);		
			}
		}
		
		@SuppressWarnings("unchecked")
		public static <T> T getObjectJsonData(String key){
			byte[] serializableData;
            serializableData = MdbClient4Param.getGlobalInstance().getCluster().get(key.getBytes(Const.UTF8));
			if(CheckNull.isNull(serializableData))
				return null;
			return (T)serializeJsonTool.asObject(serializableData);
		}
		public static <T> void setObjectData(String key,T object, Class<T> beanCalss){
			//需核对基类直接序列化是否正确
			if(MdbParamDataWrapper.serializeType==0) {
				MdbClient4Param.getGlobalInstance().getCluster().set(key.getBytes(Const.UTF8), serializeTool.asByteArray(object));
			}else {
				MdbClient4Param.getGlobalInstance().getCluster().set(key.getBytes(Const.UTF8), serializeJsonTool.asByteArray(object));
			}
		}
		public static <T> void setObjectJsonData(String key,T object){
			MdbClient4Param.getGlobalInstance().getCluster().set(key.getBytes(Const.UTF8), serializeJsonTool.asByteArray(object));
		}
		
		public static void delObjectData(String key){
			MdbClient4Param.getGlobalInstance().getCluster().del(key.getBytes(Const.UTF8));
		}
		//long------------------------------------------------
		public static long getLongData(String key){
			byte[] data;
            data = MdbClient4Param.getGlobalInstance().getCluster().get(key.getBytes(Const.UTF8));
			if(CheckNull.isNull(data)) {
				return 0L;
			}
			return Long.parseLong(new String(data));
		}
		
		public static void setLongData(String key,long v){
			MdbClient4Param.getGlobalInstance().getCluster().set(key.getBytes(Const.UTF8),String.valueOf(v).getBytes(Const.UTF8));
		}
		
		public static void delLongData(String key){
			MdbClient4Param.getGlobalInstance().getCluster().del(key.getBytes(Const.UTF8));
		}
	}
    ////////////////////////////////////////////////////////////////////////////
    //基本信息
    ////////////////////////////////////////////////////////////////////////////
	///返回管理实例的id，用于和mdb查询
	public static String getManagerInstanceId() {
	    return SysUtil.getProcessInstanceId();
	}
    ////////////////////////////////////////////////////////////////////////////
    //参数缓存信息
    ////////////////////////////////////////////////////////////////////////////
    ///PARAM_POG（30000）:300+'POG'
	public static Set<String> getPoGroupNames(){
		return MdbParamDataImp.getSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_NAMES);
	}
	
	public static void setPoGroupNames(Set<String> poGroupNames){
		MdbParamDataImp.setSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_NAMES,poGroupNames);
	}
	
	public static void delPoGroupNames(){
		MdbParamDataImp.delSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_NAMES);
	}
	
	///PARAM_POG（30000）:301+PO组名
	public static MdbParamDataWrapper.ParamGroup getPoGroupInfo(String poGroupName){
		return MdbParamDataImp.getObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_INFO+Const.KEY_JOIN+poGroupName);
	}
	
	public static void setPoGroupInfo(String poGroupName,MdbParamDataWrapper.ParamGroup pg){
		MdbParamDataImp.setObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_INFO+Const.KEY_JOIN+poGroupName,pg);
	}
	
	public static void delPoGroupInfo(String poGroupName){
		MdbParamDataImp.delObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_INFO+Const.KEY_JOIN+poGroupName);	
	}
	
	///PARAM_GROUP_PO_DATA_SIZE（30200）:302+PO组名+PO名+更新时间戳+'SIZE'，转换成string，相当于序列化
	public static long getPoDataSize(String poGroupName,String poName,long version){
		return MdbParamDataImp.getLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																			Const.KEY_JOIN+poName+
																			Const.KEY_JOIN+String.valueOf(version)+
																			Const.KEY_JOIN+"SIZE");
	}
	
	public static void setPoDataSize(String poGroupName,String poName,long version,long size){
		MdbParamDataImp.setLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+"SIZE",
																size);	
	}
	
	public static void delPoDataSize(String poGroupName,String poName,long version){
		MdbParamDataImp.delLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+"SIZE");	
	}
	
	///PARAM_GROUP_PO_DATA（30300）:303+PO组名+PO名+更新时间戳+<N>
	public static PoBase getPoData(String poGroupName,String poName,long version,long n){
		return MdbParamDataImp.getObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_DATA+Const.KEY_JOIN+poGroupName+
																			Const.KEY_JOIN+poName+
																			Const.KEY_JOIN+String.valueOf(version)+
																			Const.KEY_JOIN+String.valueOf(n));
	}
	
	public static void setPoData(String poGroupName,String poName,long version,long n,PoBase poBase){
		MdbParamDataImp.setObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_DATA+Const.KEY_JOIN+poGroupName+
																	Const.KEY_JOIN+poName+
																	Const.KEY_JOIN+String.valueOf(version)+
																	Const.KEY_JOIN+String.valueOf(n),
																	poBase,
																	PoBase.class);
	}
	
	public static void delPoData(String poGroupName,String poName,long version,long n){
		MdbParamDataImp.delObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_DATA+Const.KEY_JOIN+poGroupName+
																	Const.KEY_JOIN+poName+
																	Const.KEY_JOIN+String.valueOf(version)+
																	Const.KEY_JOIN+String.valueOf(n));
	}
	
	///PARAM_GROUP_PO_INDEX（30400）:304+PO组名+PO名+更新时间戳+'INDEX_STRING'
	public static Set<String> getPoIndexNames(String poGroupName,String poName,long version){
		return MdbParamDataImp.getSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
																		Const.KEY_JOIN+poName+
																		Const.KEY_JOIN+String.valueOf(version)+
																		Const.KEY_JOIN+INDEX_STRING);
	}
	
	public static void setPoIndexNames(String poGroupName,String poName,long version,Set<String> PoIndexNames){
		MdbParamDataImp.setSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+poName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+INDEX_STRING,
															PoIndexNames);
	}
	
	public static void delPoIndexNames(String poGroupName,String poName,long version){
		MdbParamDataImp.delSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+INDEX_STRING);
	}
	////////////////////////////////////////////////////////////////////////////
	//应用实例缓存信息
	////////////////////////////////////////////////////////////////////////////
	///PARAM_INS_GROUP（35100）:351+PO组名+应用缓存实例标识
	public static MdbParamDataWrapper.ParamGroup getInsPoGroupInfo(String poGroupName){
		return MdbParamDataImp.getObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_INFO+Const.KEY_JOIN+poGroupName+
																			Const.KEY_JOIN+getManagerInstanceId());
	}
	
	public static void setInsPoGroupInfo(String poGroupName,MdbParamDataWrapper.ParamGroup pg){
		MdbParamDataImp.setObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_INFO+Const.KEY_JOIN+poGroupName+
																	Const.KEY_JOIN+getManagerInstanceId(),
																	pg);
	}
	
	public static void delInsPoGroupInfo(String poGroupName){
		MdbParamDataImp.delObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_INFO+Const.KEY_JOIN+poGroupName+
																	Const.KEY_JOIN+getManagerInstanceId());
	}
	///PARAM_INS_GROUP_PO_DATA_SIZE（35200）:352+PO组名+PO名+更新时间戳+应用缓存实例标识+'SIZE'
	public static long getInsPoDataSize(String poGroupName,String poName,long version){
		return MdbParamDataImp.getLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																			Const.KEY_JOIN+poName+
																			Const.KEY_JOIN+String.valueOf(version)+
																			Const.KEY_JOIN+getManagerInstanceId()+
																			Const.KEY_JOIN+"SIZE");
	}
	
	public static void setInsPoDataSize(String poGroupName,String poName,long version,long size){
		MdbParamDataImp.setLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+getManagerInstanceId()+
																Const.KEY_JOIN+"SIZE",
																size);	
	}
	
	public static void delInsPoDataSize(String poGroupName,String poName,long version){
		MdbParamDataImp.delLongData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_SIZE+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+getManagerInstanceId()+
																Const.KEY_JOIN+"SIZE");	
	}
	
	///PARAM_INS_GROUP_PO_INDEX（35400）:354+PO组名+PO名+更新时间戳+应用缓存实例标识+'INDEX_STRING'
	public static Set<String> getInsPoIndexNames(String poGroupName,String poName,long version){
		return MdbParamDataImp.getSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
																		Const.KEY_JOIN+poName+
																		Const.KEY_JOIN+String.valueOf(version)+
																		Const.KEY_JOIN+getManagerInstanceId()+
																		Const.KEY_JOIN+INDEX_STRING);
	}
		
	public static void setInsPoIndexNames(String poGroupName,String poName,long version,Set<String> PoIndexNames){
			MdbParamDataImp.setSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+poName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+getManagerInstanceId()+
																Const.KEY_JOIN+INDEX_STRING,
																PoIndexNames);
	}
	
	public static void delInsPoIndexNames(String poGroupName,String poName,long version){
		MdbParamDataImp.delSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_PO_INDEX+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+poName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+getManagerInstanceId()+
															Const.KEY_JOIN+INDEX_STRING);
	}
	
	///PARAM_INS_GROUP_VISITOR（37000）:370+PO组名+更新时间戳+应用缓存实例标识+'VISITOR'
	public static Set<String> getInsPoGroupVisitorIds(String poGroupName,long version){
		return MdbParamDataImp.getSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR+Const.KEY_JOIN+poGroupName+
																		Const.KEY_JOIN+String.valueOf(version)+
																		Const.KEY_JOIN+getManagerInstanceId()+
																		Const.KEY_JOIN+VISITOR_STRING);
	}
		
	public static void setInsPoGroupVisitorIds(String poGroupName,long version,Set<String> poGroupVisitorIds){
			MdbParamDataImp.setSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+getManagerInstanceId()+
																Const.KEY_JOIN+VISITOR_STRING,
																poGroupVisitorIds);
	}
	
	///增加一个访问者Id到Mdb
	public static void addInsPoGroupVisitorIds(String poGroupName,long version,String poGroupVisitorId){
		MdbParamDataImp.addSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+getManagerInstanceId()+
															Const.KEY_JOIN+VISITOR_STRING,
															poGroupVisitorId);
	}
	
	///从Mdb里边移除一个访问者Id
	public static void removeInsPoGroupVisitorIds(String poGroupName,long version,String poGroupVisitorId){
		MdbParamDataImp.removeSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+getManagerInstanceId()+
															Const.KEY_JOIN+VISITOR_STRING,poGroupVisitorId);
	}
	
	public static void delInsPoGroupVisitorIds(String poGroupName,long version){
		MdbParamDataImp.delSetData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+getManagerInstanceId()+
															Const.KEY_JOIN+VISITOR_STRING);
	}
	
	///PARAM_INS_GROUP_VISITOR_INFO（37100）:371+PO组名+更新时间戳+应用缓存实例标识+<VISITOR>
	public static MdbParamDataWrapper.VisitorInfo getInsPoGroupVisitorInfo(String poGroupName,long version,String Id){
		return MdbParamDataImp.getObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR_INFO+Const.KEY_JOIN+poGroupName+
																		Const.KEY_JOIN+String.valueOf(version)+
																		Const.KEY_JOIN+getManagerInstanceId()+
																		Const.KEY_JOIN+Id);
	}
		
	public static void setInsPoGroupVisitorInfo(String poGroupName,long version,String Id,MdbParamDataWrapper.VisitorInfo poGroupVisitorInfo){
			MdbParamDataImp.setObjectJsonData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR_INFO+Const.KEY_JOIN+poGroupName+
																Const.KEY_JOIN+String.valueOf(version)+
																Const.KEY_JOIN+getManagerInstanceId()+
																Const.KEY_JOIN+Id,
																poGroupVisitorInfo);
	}
	
	public static void delInsPoGroupVisitorInfo(String poGroupName,long version,String Id){
		MdbParamDataImp.delObjectData(RedisConst.ParamMdbKey.MDB_KEY_PARAM_APP_POG_VISITOR_INFO+Const.KEY_JOIN+poGroupName+
															Const.KEY_JOIN+String.valueOf(version)+
															Const.KEY_JOIN+getManagerInstanceId()+
															Const.KEY_JOIN+Id);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static void setSerializeType(int serializeType) {
		MdbParamDataWrapper.serializeType = serializeType;
	}
	public static int getSerializeType() {
		return MdbParamDataWrapper.serializeType;
	}
}
