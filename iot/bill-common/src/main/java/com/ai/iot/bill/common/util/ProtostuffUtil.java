package com.ai.iot.bill.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**必须实现一个空构造函数,且内部逻辑是可重入的.*/
public class ProtostuffUtil {
	private static ProtostuffUtilImp imp=new ProtostuffUtilImp();
	public static ProtostuffUtilImp globalInstance() {
		return imp;
	}
	
	/**用于多线程环境,一个线程对应一个对象*/
	public static ProtostuffUtilImp newInstance() {
		return new ProtostuffUtilImp(true);
	}
	public static <T> byte[] serializer(T obj) {
		return globalInstance().serializer(obj);
	}
	public static <T> T deserializer(byte[] data, Class<T> clazz) {
		return globalInstance().deserializer(data,clazz);
	}
	public static <T> T deserializer(byte[] data, T object) {
		return globalInstance().deserializer(data,object);
	}
	@SuppressWarnings("rawtypes")
	public static byte[] serializerList(List objs){
		return globalInstance().serializerList(objs);
	}
	
	@SuppressWarnings("rawtypes")
	public static List deserializerList(byte[] data){
		return globalInstance().deserializerList(data);
	}
	
	public static class ProtostuffUtilImp {
		private Map<Class<?>, Schema<?>> cachedSchema;
		private static Objenesis objenesis = new ObjenesisStd(true);

		public ProtostuffUtilImp(){
			cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
		}
		
		public ProtostuffUtilImp(boolean isUseHashMap){
			if(isUseHashMap) {
				cachedSchema = new HashMap<Class<?>, Schema<?>>();
			}else {
				cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
			}
		}
	    private  <T> Schema<T> getSchema(Class<T> clazz) {
	        @SuppressWarnings("unchecked")
	        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
	        if (schema == null) {
	            schema = RuntimeSchema.getSchema(clazz);
	            if (schema != null) {
	                cachedSchema.put(clazz, schema);
	            }
	        }
	        return schema;
	    }

	    /**
	     * 序列化
	     *
	     * @param obj
	     * @return
	     */
	    public <T> byte[] serializer(T obj) {
	        @SuppressWarnings("unchecked")
	        Class<T> clazz = (Class<T>) obj.getClass();
	        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
	        try {
	            Schema<T> schema = getSchema(clazz);
	            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
	        } catch (Exception e) {
	            throw new IllegalStateException(e.getMessage(), e);
	        } finally {
	            buffer.clear();
	        }
	    }

	    /**
	     * 反序列化
	     *
	     * @param data
	     * @param clazz
	     * @return
	     */
	    public <T> T deserializer(byte[] data, Class<T> clazz) {
	        try {
	            T obj = objenesis.newInstance(clazz);
	            Schema<T> schema = getSchema(clazz);
	            ProtostuffIOUtil.mergeFrom(data, obj, schema);
	            return obj;
	        } catch (Exception e) {
	            throw new IllegalStateException(e.getMessage(), e);
	        }
	    }
	    public <T> T deserializer(byte[] data, T object) {
	        try {
	            @SuppressWarnings("unchecked")
				Schema<T> schema = (Schema<T>) getSchema(object.getClass());
	            ProtostuffIOUtil.mergeFrom(data, object, schema);
	            return object;
	        } catch (Exception e) {
	            throw new IllegalStateException(e.getMessage(), e);
	        }
	    }
	    
	    /**
	     * 序列化list
	     *
	     * @param objs
	     * @return
	     */
	    @SuppressWarnings("rawtypes")
		public byte[] serializerList(List objs) {
	    	ProtostuffList list=new ProtostuffList();
	    	list.setList(objs);
	    	return serializer(list);
	    }
	    
	    /**
	     * 反序列化list
	     *
	     * @param data
	     * @param clazz
	     * @return
	     */
	    @SuppressWarnings("rawtypes")
		public List deserializerList(byte[] data) {
	    	ProtostuffList list = deserializer(data, ProtostuffList.class);
	    	if(list!=null){
	    		return list.getList();
	    	}
	    	return null;
	    }
	}
}
