package com.ai.iot.bill.common.mdb;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.iot.bill.common.util.ProtostuffUtil;
import com.ai.iot.bill.common.util.Const;

/*
 * MdbTables获取和更新MDB内存对象数据
 * 使用方式：
 * 1、实例化MdbTables，调用selectTables（新建类）
 * 2、调用getData或getList获取数据
 * 3、调用setData更新数据
 * 4、调用updateTables提交数据，或updateTablesWithVer带版本提交数据
 */
public class MdbTables {
	private static final Logger logger = LoggerFactory.getLogger(MdbTables.class);
	
	//定义field的类型
	public static final int FIELD_TYPE_LIST = 0;
	public static final int FIELD_TYPE_OBJECT = 1;
	public static final int FIELD_TYPE_STRING = 2;
	public static final int FIELD_TYPE_LONG = 3;
	public static final int FIELD_TYPE_INT = 4;
	
	//版本的field
	public static final String TABLE_VER = "VER";
	
	//查询出来的数据
	protected Map<String,Object> selectTables = new HashMap<>();
	//要更新的数据
	protected Map<String,Object> updateTables = new HashMap<>();
	protected CustJedisCluster jc=null;
	protected MdbTable mmTable=null; //本次查询的表
	
	public MdbTables(CustJedisCluster ajc){
		jc=ajc;
	}
	
	/**重置本地对象的缓存*/
	public void reset(){
		selectTables.clear();
		updateTables.clear();
	}
	
	//获取mdbkey对应数据,会一次性全部取出,一个mdbkey只需调用一次
	public boolean selectTables(){		
		return selectTables(mmTable);
	}
	public boolean selectTables(MdbTable table){		
		List<String> fieldsList = table.getFields();
		List<Integer> fieldsType = table.getFieldsType();
		
		if(fieldsList.size()!=fieldsType.size()){
			logger.error("fieldsList.size={},fieldsType.size={},the two are not equal.",fieldsList.size(),fieldsType.size());
			return false;
		}
		
		reset();
		mmTable = table;
		
		byte[][] fields=new byte[fieldsList.size()][];
		for(int i=0;i<fieldsList.size();i++){
			fields[i] = fieldsList.get(i).getBytes(Const.UTF8);
		}		
		
		List<byte[]> rets = jc.hmget(mmTable.getKey().getBytes(Const.UTF8), fields);

		if (rets != null) {
			for (int i=0;i<fieldsList.size();i++) {
				putData(fieldsList.get(i),fieldsType.get(i),rets.get(i));
			}
		}
		
		return true;
	}
	
	/**返回某个Mo,类型需自行强制转换*/
	public Object getData(String field){
		if(updateTables.containsKey(field)){
			return updateTables.get(field);
		}else if(selectTables.containsKey(field)){
			return selectTables.get(field);
		}
		
		return null;
	}
	
	/**返回某个Mo,类型需自行强制转换*/
    public boolean hasVer(){
        if(updateTables.containsKey(TABLE_VER)){
            return true;
        }else if(selectTables.containsKey(TABLE_VER)){
            return true;
        }else {
            return false;
        }

    }
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(String field){
		if(updateTables.containsKey(field)){
			return (T)(updateTables.get(field));
		}else if(selectTables.containsKey(field)){
			return (T)(selectTables.get(field));
		}
		
		return null;
	}
	
	/**返回某个List<Mo>,类型需自行强制转换*/
	@SuppressWarnings("rawtypes")
	public List getList(String field){
		Object obj = getData(field);
		if(obj instanceof List){
			return (List)obj;
		}
		return Collections.emptyList();
	}
	
	/**修改某个hashkey即field对应Mo*/
	public Object setData(String field,Object value){
		return updateTables.put(field, value);
	}
	
	/*
	 * 带版本更新表数据,一个话单处理完毕后仅调用一次
	 */
	public int updateTablesWithVer(){
		if(updateTables.size()<=0){
			return Const.OK; 
		}
		
		String oldVer=null;
		String newVer=null;
		
		Object ver = getData(TABLE_VER);
			
		if(ver == null){
			oldVer = "0";
			newVer = "1";
		}else{	
			oldVer = String.valueOf(ver);
			newVer = String.valueOf(Integer.parseInt(oldVer) + 1);
		}
		
		byte[][] params = new byte[MdbUtil.LUA_UPDATE_WITH_VER_PARAMS_LENGTH][];
		int i=0;
		// key
		params[i++] = mmTable.getKey().getBytes(Const.UTF8);

		// oldVer
		params[i++] = oldVer.getBytes(Const.UTF8);

		// newVer
		params[i++] = newVer.getBytes(Const.UTF8);
		
		//需要删除updateTables里的版本，防止重复更新
		updateTables.remove(TABLE_VER);
		
		//参数
		for(Entry<String,Object> table:updateTables.entrySet()){
			byte[] data = serializer(table.getValue());
			params[i++]=table.getKey().getBytes(Const.UTF8);
			params[i++]=data;
		}
		
		while(i<MdbUtil.LUA_UPDATE_WITH_VER_PARAMS_LENGTH){
			params[i++] = "tmp".getBytes(Const.UTF8);
			params[i++] = "".getBytes(Const.UTF8);
		}
		
		return MdbUtil.updateWithVersion(jc, params);
	}
	
	/*
	 * 更新表数据,一个话单处理完毕后仅调用一次
	 */
	public int updateTables(){
		if(updateTables.size()<=0){
			return Const.OK; 
		}
		
		Map<byte[],byte[]> tablesData = new HashMap<>();
		for(Entry<String,Object> table:updateTables.entrySet()){			
			tablesData.put(table.getKey().getBytes(Const.UTF8), serializer(table.getValue()));
		}
		String ret=jc.hmset(mmTable.getKey().getBytes(), tablesData);
		
		if("OK".equals(ret)){
			return Const.OK;
		}else{
			return Const.ERROR;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected byte[] serializer(Object obj){
		byte[] data = null;
		if(obj instanceof List){
			data = ProtostuffUtil.serializerList((List)obj);
		}else if(obj instanceof String){
			data = ((String)obj).getBytes(Const.UTF8);
		}else if(obj instanceof Long){
			data = (String.valueOf((Long)obj)).getBytes(Const.UTF8);
		}else if(obj instanceof Integer){
			data = (String.valueOf((Integer)obj)).getBytes(Const.UTF8);
		}else{
			data = ProtostuffUtil.serializer(obj);
		}
		return data;
	}
	
	/**用于多个MdbTable使用同一个MdbTables对象的场景,进行切换更新,不建议经常使用,除非对实现细节熟悉
	 * */
	public int updateTables(MdbTable table){
		mmTable = table;
		return updateTables();
	}
	
	/**返回MdbTable对象*/
	public MdbTable getMdbTable() {
		return mmTable;
	}

	/**设置MdbTable对象,一般设置一次*/
	public void setMdbTable(MdbTable table) {
		this.mmTable = table;
	}

	private void putData(String field,int type,byte[] v){
		if (v != null) {
			switch (type){
			case FIELD_TYPE_LIST:
				selectTables.put(field,ProtostuffUtil.deserializerList(v));
				break;
			case FIELD_TYPE_OBJECT:
				Class<?> clazz=mmTable.getFieldClazz(field);
				if(clazz!=null){
					selectTables.put(field,ProtostuffUtil.deserializer(v,clazz));
				}else{
					selectTables.put(field,ProtostuffUtil.deserializerList(v));
				}
				break;
			case FIELD_TYPE_STRING:
				selectTables.put(field,new String(v,Const.UTF8));
				break;
			case FIELD_TYPE_LONG:
				selectTables.put(field,Long.parseLong(new String(v,Const.UTF8)));
				break;
			case FIELD_TYPE_INT:
				selectTables.put(field,Integer.parseInt(new String(v,Const.UTF8)));
				break;
			default:
				logger.info("{} type is wrong",field);
				break;
			}					
		}
	}
}
